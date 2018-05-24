package ihm.si3.fr.unice.polytech.polissue.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.glide.GlideApp;

public class SignUpFragment extends Fragment {

    private static final String PASS_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String TAG = "SignUpFragment";
    private static final String USER_FIREBASE_REF = "users";
    LoginFragmentListener listener;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private TextView passwordTextView;
    private TextView passwordConfirmationTextView;
    private TextView emailTextView;
    private TextView surnameTextView;
    private TextView firstnameTextView;
    private ImageButton takeProfilePic;
    private ImageButton chooseProfilePic;
    private ImageView profilePic;
    private Uri profilePicUri;
    private static int TAKE_PICTURE_CODE = 1;
    private static int CHOOSE_PICTURE_CODE = 2;

    public SignUpFragment() {
    }

    public static SignUpFragment newInstance() {
        return newInstance("", "");
    }

    public static SignUpFragment newInstance(String email, String password) {

        Bundle args = new Bundle();
        args.putString(EMAIL_KEY, email);
        args.putString(PASS_KEY, password);

        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mainView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailTextView = mainView.findViewById(R.id.signup_email);
        passwordTextView = mainView.findViewById(R.id.signup_password);
        passwordConfirmationTextView = mainView.findViewById(R.id.signup_password_confirmation);
        firstnameTextView = mainView.findViewById(R.id.signup_firstname);
        surnameTextView = mainView.findViewById(R.id.signup_name);
        takeProfilePic = mainView.findViewById(R.id.take_profile_pic);
        chooseProfilePic = mainView.findViewById(R.id.choose_profile_pic);
        profilePic = mainView.findViewById(R.id.register_profile_pic);

        Bundle args = getArguments();
        String email = args.getString(EMAIL_KEY);
        String password = args.getString(PASS_KEY);

        if (email != null && !email.isEmpty()) {
            emailTextView.setText(email);
        }

        if (password != null && !password.isEmpty()) {
            passwordTextView.setText(password);
        }

        Button signUpButton = mainView.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener((View v) -> signUp());

        chooseProfilePic.setOnClickListener(v -> {
            final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, CHOOSE_PICTURE_CODE);
        });

        takeProfilePic.setOnClickListener(v -> {
            final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
                try {
                    File tempPicture = createPictureFile();
                    profilePicUri = FileProvider.getUriForFile(this.getContext(), "fr.unice.polytech.polissue.fileprovider", tempPicture);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, profilePicUri);
                    startActivityForResult(cameraIntent, TAKE_PICTURE_CODE);
                } catch (IOException e) {
                    Log.e(TAG, "onCreateView: error with temp file", e);
                    Toast.makeText(this.getContext(), getString(R.string.error_internal_file), Toast.LENGTH_LONG).show();
                }
            }
        });

        return mainView;
    }


    private File createPictureFile() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }


    private void signUp() {
        auth.signOut();
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        if (validateForm()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this::ackowledgeSignUp);
        } else {
            Log.d(TAG, "signUp: No Email or Password");
        }
    }

    /**
     * Check the data validity and display error messages
     *
     * @return true if the datas are valid, false otherwise
     */
    private boolean validateForm() {
        String email = emailTextView.getText().toString();
        boolean valid = true;
        String error = "";
        if (email.isEmpty()) error = getString(R.string.error_field_required);
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            error = getString(R.string.error_invalid_email);

        if (!error.isEmpty()) {
            emailTextView.setError(error);
            valid = false;
        }

        String password = passwordTextView.getText().toString();
        error = "";
        if (password.isEmpty()) error = getString(R.string.error_field_required);
        else if (password.length() < 8) error = getString(R.string.error_invalid_password);
        if (!error.isEmpty()) {
            passwordTextView.setError(error);
            valid = false;
        }
        if (valid) {
            String confirmation = passwordConfirmationTextView.getText().toString();
            error = "";
            if (confirmation.isEmpty()) error = getString(R.string.error_field_required);
            if (!confirmation.equals(password)) {
                error = getString(R.string.error_password_confirmation_must_match);
            }
            if (!error.isEmpty()) {
                passwordConfirmationTextView.setError(error);
                valid = false;
            }
        }
        String firstname = firstnameTextView.getText().toString();
        error = "";
        if (firstname.isEmpty()) error = getString(R.string.error_field_required);
        if (!error.isEmpty()) {
            firstnameTextView.setError(error);
            valid = false;
        }
        String surname = surnameTextView.getText().toString();
        error = "";
        if (surname.isEmpty()) error = getString(R.string.error_field_required);
        if (!error.isEmpty()) {
            surnameTextView.setError(error);
            valid = false;
        }


        return valid;
    }

    private void ackowledgeSignUp(Task<AuthResult> authResultTask) {
        if (authResultTask.isSuccessful()) {
            Log.d(TAG, "ackowledgeSignUp: Success");
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                user.sendEmailVerification();
                String name = firstnameTextView.getText().toString() +
                        " " +
                        surnameTextView.getText().toString();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference profilePictures = storage.getReference("images").child("profile").child(name);
                String pictureName = String.valueOf(System.currentTimeMillis() / 1000);
                StorageReference pictureRef = profilePictures.child(pictureName);
                pictureRef.putFile(profilePicUri);
                pictureRef.getDownloadUrl().addOnSuccessListener(t -> {
                    UserProfileChangeRequest changes = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(t)
                            .build();
                    user.updateProfile(changes).addOnFailureListener(e ->
                            Toast.makeText(getContext(), getString(R.string.error_profile_settings), Toast.LENGTH_LONG).show());
                });




            }

            //listener.done();
        } else {
            Log.d(TAG, "ackowledgeSignUp: failure", authResultTask.getException());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            listener = (LoginFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement LoginFragmentListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE_CODE) {
            if (profilePicUri != null) {
                GlideApp.with(this).load(profilePicUri).circleCrop().into(profilePic);
            }
        } else if (requestCode == CHOOSE_PICTURE_CODE && data != null) {
            Uri result = data.getData();
            if (result != null) {
                profilePicUri = result;
                GlideApp.with(this).load(profilePicUri).circleCrop().into(profilePic);
            }
        }

    }
}
