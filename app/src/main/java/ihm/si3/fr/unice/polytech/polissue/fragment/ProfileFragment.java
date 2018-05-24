package ihm.si3.fr.unice.polytech.polissue.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.glide.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView username;
    private TextView email;
    private ImageView profilePic;
    private FirebaseAuth auth;
    private Button changeEmail;
    private Button resetPassword;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_profile, container, false);

        username = mainView.findViewById(R.id.username);
        email = mainView.findViewById(R.id.email);

        profilePic = mainView.findViewById(R.id.profile_pic_view);

        auth = FirebaseAuth.getInstance();

        resetPassword = mainView.findViewById(R.id.profile_password_reset);
        resetPassword.setOnClickListener(v -> passwordPopUp());

        changeEmail = mainView.findViewById(R.id.profile_username_change);
        changeEmail.setOnClickListener(v -> emailPopUp());
        updateUser();

        // Inflate the layout for this fragment
        return mainView;

    }

    private void emailPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.change_username));


        final EditText usernameInput = new EditText(getContext());
        usernameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        usernameInput.setHint(R.string.new_username);
        builder.setView(usernameInput);

        builder.setPositiveButton(getString(R.string.change), (dialog, which) -> {
                    String newUsername = usernameInput.getText().toString();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest changes = new UserProfileChangeRequest.Builder().setDisplayName(newUsername).build();
                        user.updateProfile(changes).addOnSuccessListener(t -> {
                            Toast.makeText(getContext(), newUsername, Toast.LENGTH_LONG).show();
                            updateUser();
                        });
                    } else {
                        dialog.cancel();
                        Toast.makeText(getContext(), getString(R.string.error_not_logged_in), Toast.LENGTH_LONG).show();
                    }
                }
        );
        builder.setNegativeButton(R.string.dialog_negative_button, (dialog, which) -> dialog.cancel());
        builder.show();
    }


    /**
     * shows the password popup
     */
    private void passwordPopUp() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.reset_password);
        alertDialogBuilder.setMessage(R.string.reset_password_dialog_text)
                .setCancelable(true)
                .setPositiveButton(R.string.send, (dialog, which) -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        dialog.cancel();
                        Toast.makeText(getContext(), getString(R.string.error_not_logged_in), Toast.LENGTH_LONG).show();
                    } else {
                        auth.sendPasswordResetEmail(user.getEmail())
                                .addOnCompleteListener(t -> Toast.makeText(getContext(), getString(R.string.email_sent), Toast.LENGTH_LONG).show());
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    /**
     * update the userprofile views
     */
    private void updateUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            username.setText(user.getDisplayName());
            email.setText(user.getEmail());
            GlideApp.with(this)
                    .load(user.getPhotoUrl())
                    .transform(new CircleCrop())
                    .into(profilePic);
        }
    }

}
