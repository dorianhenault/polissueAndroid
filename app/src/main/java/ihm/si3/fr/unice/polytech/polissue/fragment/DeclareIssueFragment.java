package ihm.si3.fr.unice.polytech.polissue.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import ihm.si3.fr.unice.polytech.polissue.DataBaseAccess;
import ihm.si3.fr.unice.polytech.polissue.IncidentLocalisationActivity;
import ihm.si3.fr.unice.polytech.polissue.IssuePictureListener;
import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.model.Emergency;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.model.Location;
import ihm.si3.fr.unice.polytech.polissue.model.State;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclareIssueFragment extends Fragment{

    private static final int ADD_IMAGE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int REQUEST_GET_MAP_LOCATION = 0;
    private static final String TAG = "DeclareIssueFragment";
    private ImageButton validButton, addImage, takePicture, currentLocation, cancelButton;
    private ImageView image;
    private EditText title, description, declarer, location;
    private SeekBar emergencyLevel;
    private TextView titleError, declarerError, locationError;
    private Uri imageURI;

    private Location locationMap;

    private double longitude=0;
    private double latitude=0;

    public DeclareIssueFragment() {

    }


    public static Fragment newInstance(){
        return new DeclareIssueFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_declare_issue, container, false);
        validButton = view.findViewById(R.id.validButton);
        addImage = view.findViewById(R.id.addImageButton);
        takePicture = view.findViewById(R.id.takePicutreButton);
        currentLocation = view.findViewById(R.id.currentLocationButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        image = view.findViewById(R.id.issueImagePreview);
        title = view.findViewById(R.id.titleTextField);
        description = view.findViewById(R.id.descriptionTextField);
        declarer = view.findViewById(R.id.declarerTextField);
        location = view.findViewById(R.id.locationTextField);
        emergencyLevel = view.findViewById(R.id.emergencyLevel);
        titleError = view.findViewById(R.id.titleError);
        declarerError = view.findViewById(R.id.declarerError);
        locationError = view.findViewById(R.id.locationError);

        validButton.setOnClickListener((v) -> {
            if(checkMandatoryFields()){
                Emergency level = buildEmergencyLevel();
                this.locationMap=new Location(location.getText().toString(),longitude,latitude);
                // IssueModel issue = new IssueModel(title.getText().toString(),description.getText().toString(),new Date(), level,declarer.getText().toString());
                IssueModel issue = new IssueModel(title.getText().toString(),description.getText().toString(),new Date(), Emergency.MEDIUM,declarer.getText().toString(), State.NOT_RESOLVED);
                StorageReference imageRef = uploadPicture(imageURI, issue);
                issue.imagePathFromRef(imageRef);
                DataBaseAccess dataBaseAccess = new DataBaseAccess();
                dataBaseAccess.postIssue(issue);
                Log.d(TAG, "onCreateView: Posted issue");
                Fragment fragment = IssueListFragment.newInstance(2);
                FragmentTransaction ft = this.getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }else {
                Toast.makeText(this.getContext(), "Champ(s) manquant(s)", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener((v -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, IssueListFragment.newInstance(2));
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }));

        emergencyLevel.setProgress(0);
        emergencyLevel.setMax(100);

        emergencyLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 0 && progress <50){
                    seekBar.setProgress(0);
                }else if (progress>=50 && progress<100){
                    seekBar.setProgress(50);
                }else {
                    seekBar.setProgress(100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        addImage.setOnClickListener(v -> {
            final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, ADD_IMAGE);
        });
        takePicture.setOnClickListener(v -> {
            final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
                try {
                    File tempPicture = createPictureFile();
                    imageURI = FileProvider.getUriForFile(this.getContext(), "fr.unice.polytech.polissue.fileprovider", tempPicture);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                    startActivityForResult(cameraIntent, TAKE_PICTURE);
                } catch (IOException e) {
                    Log.e(TAG, "onCreateView: error with temp file", e);
                    Toast.makeText(this.getContext(), getString(R.string.error_internal_file), Toast.LENGTH_LONG).show();
                }
            }
        });
        image.setOnClickListener(v -> {
            if (imageURI != null) {
                final Intent galeryIntent = new Intent(Intent.ACTION_VIEW);
                galeryIntent.setDataAndType(imageURI, "image/*");
                startActivity(galeryIntent);
            }
        });
        currentLocation.setOnClickListener(v -> {
            Intent localisationActivity=new Intent(this.getActivity(), IncidentLocalisationActivity.class);
            startActivityForResult(localisationActivity,REQUEST_GET_MAP_LOCATION);
        });

        return view;
    }

    private Emergency buildEmergencyLevel() {
        if (emergencyLevel.getProgress() == 0){
            return Emergency.LOW;
        }else if (emergencyLevel.getProgress() == 50){
            return Emergency.MEDIUM;
        }
        else {
            return Emergency.HIGH;
        }
    }
    private StorageReference uploadPicture(Uri imageURI, IssueModel issue) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference issueImages = storage.getReference("images").child("issues").child(issue.getTitle());
        String pictureName = String.valueOf(System.currentTimeMillis() / 1000);
        StorageReference pictureRef = issueImages.child(pictureName);
        IssuePictureListener uploadListener = new IssuePictureListener(issue);
        pictureRef.putFile(imageURI).addOnCompleteListener(uploadListener);
        return pictureRef;
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
    private boolean checkMandatoryFields() {
        boolean ok = true;
        titleError.setVisibility(View.GONE);
        declarerError.setVisibility(View.GONE);
        locationError.setVisibility(View.GONE);

        if (title.getText().length() == 0 || title.getText().toString().equals("")){
            titleError.setVisibility(View.VISIBLE);
            ok = false;
        }
        if (declarer.getText().length() == 0 || declarer.getText().toString().equals("")){
            declarerError.setVisibility(View.VISIBLE);
            ok=false;
        }
        if (location.getText().length() == 0 || location.getText().toString().equals("")){
            locationError.setVisibility(View.VISIBLE);
            ok = false;
        }
        return ok;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_MAP_LOCATION && resultCode == Activity.RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
            // do something with B's return values
        }else if ((requestCode == ADD_IMAGE || requestCode == TAKE_PICTURE )
                && resultCode == RESULT_OK && data != null) {
            Bitmap picture = null;
            if (requestCode == ADD_IMAGE) {
                Uri result = data.getData();
                if (result != null) {
                    image.setImageURI(result);
                    imageURI = result;
                }
            }
            try {
                picture = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), imageURI);
            } catch (IOException e) {
                Log.e(TAG, "onActivityResult:Loading Picture ", e);
                Toast.makeText(this.getContext(), getString(R.string.error_loading_picture), Toast.LENGTH_LONG).show();
            }
            if (picture != null) {
                image.setImageBitmap(picture);

            }

        }
    }
}
