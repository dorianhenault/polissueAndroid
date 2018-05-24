package ihm.si3.fr.unice.polytech.polissue.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ihm.si3.fr.unice.polytech.polissue.DataBaseAccess;
import ihm.si3.fr.unice.polytech.polissue.IssuePictureListener;
import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.fragment.location.IncidentLocalisationFragment;
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
    private static final String TAG = "DeclareIssueFragment";
    private ImageButton validButton, addImage, takePicture, currentLocation, cancelButton;
    private ImageView image;
    private EditText title, description;
    private SeekBar emergencyLevel;
    private TextView titleError, locationError,cityLocation,cityLocationText;
    private Uri imageURI;

    private Location locationMap;

    private double longitude=0;
    private double latitude=0;
    private boolean locationButtonClicked=false;
    private IssueModel issueModelRetrieved;

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
        image.setVisibility(View.GONE);
        title = view.findViewById(R.id.titleTextField);
        description = view.findViewById(R.id.descriptionTextField);
        emergencyLevel = view.findViewById(R.id.emergencyLevel);
        titleError = view.findViewById(R.id.titleError);
        locationError = view.findViewById(R.id.locationError);
        cityLocation = view.findViewById(R.id.cityLocation);
        cityLocationText = view.findViewById(R.id.cityLocationText);

        if(getArguments()!=null){
            this.locationMap=getArguments().getParcelable("location");
            this.longitude=locationMap.getLongitude();
            this.latitude=locationMap.getLatitude();
            this.imageURI=getArguments().getParcelable("imageUri");
            this.issueModelRetrieved=getArguments().getParcelable("issue");
            this.locationButtonClicked=getArguments().getBoolean("buttonClicked");
        }

        validButton.setOnClickListener((v) -> {
            if(checkMandatoryFields()){
                Emergency level = buildEmergencyLevel();
                IssueModel issue = new IssueModel(title.getText().toString(),description.getText().toString(),new Date(), level,locationMap, FirebaseAuth.getInstance().getUid(),"", State.NOT_RESOLVED);
                if(imageURI!=null){
                    StorageReference imageRef = uploadPicture(imageURI, issue);
                    issue.imagePathFromRef(imageRef);
                }
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
            IssueModel issue = new IssueModel(title.getText().toString(),description.getText().toString(),new Date(), buildEmergencyLevel(),locationMap, FirebaseAuth.getInstance().getUid(),"", State.NOT_RESOLVED);
            Bundle bundle=new Bundle();
            bundle.putParcelable("issue",issue);
            bundle.putParcelable("imageUri",imageURI);
            FragmentTransaction ft = ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
            Fragment issueLocationFragment= IncidentLocalisationFragment.newInstance();
            issueLocationFragment.setArguments(bundle);
            ft.replace(R.id.content_frame, issueLocationFragment );
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();

        });

        updateCityLocation();
        if(this.locationMap!=null){
            TextView locationDescription=view.findViewById(R.id.locationDescription);
            locationDescription.setText(locationMap.getPlace());
            locationDescription.setVisibility(View.VISIBLE);
        }

        if(this.issueModelRetrieved!=null){
            restoreFormFields(this.issueModelRetrieved);

        }
        return view;
    }

    private void restoreFormFields(IssueModel issueModel){
        if(issueModel.getTitle()!=null){
            this.title.setText(issueModel.getTitle());
        }
        if(issueModel.getEmergency()!=null){
            System.out.println(buildEmergencyLevelReversed(issueModel.getEmergency())+" URGEEENCE");
            this.emergencyLevel.setProgress(buildEmergencyLevelReversed(issueModel.getEmergency()));
            emergencyLevel.refreshDrawableState();
        }
        if(imageURI!=null){
            Bitmap  picture=null;
            try {
                  picture = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), imageURI);
            } catch (IOException e) {
                Log.e(TAG, "onActivityResult:Loading Picture ", e);
                Toast.makeText(this.getContext(), getString(R.string.error_loading_picture), Toast.LENGTH_LONG).show();
            }
            if (picture != null) {
                image.setImageBitmap(picture);
                image.setVisibility(View.VISIBLE);
            }
            else{
                if (imageURI != null){
                    image.setImageURI(imageURI);
                    image.setVisibility(View.VISIBLE);
                }else {
                    image.setVisibility(View.GONE);
                }
            }
        }
        if(issueModel.getDescription()!=null){
            this.description.setText(issueModel.getDescription());
        }

    }

    private void updateCityLocation(){
        Geocoder gcd = new Geocoder(this.getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses.size() > 0) {
            cityLocation.setText(addresses.get(0).getLocality());
            cityLocation.setVisibility(View.VISIBLE);
            cityLocationText.setVisibility(View.VISIBLE);
            System.out.print(addresses.get(0).getLocality());
        }
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

    private int buildEmergencyLevelReversed(Emergency emergency) {
        if (emergency == Emergency.LOW){
            return 0 ;
        }else  if (emergency == Emergency.MEDIUM){
            return 50;
        }
        else {
            return 100;
        }
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
        locationError.setVisibility(View.GONE);

        if (title.getText().length() == 0 || title.getText().toString().equals("")){
            titleError.setVisibility(View.VISIBLE);
            ok = false;
        }
        if (!locationButtonClicked){
            locationError.setVisibility(View.VISIBLE);
            ok = false;
        }
        return ok;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_IMAGE || requestCode == TAKE_PICTURE )
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
                image.setVisibility(View.VISIBLE);
            }else {
                image.setVisibility(View.GONE);
            }

        }
    }
}
