package ihm.si3.fr.unice.polytech.polissue.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Date;
import java.util.Objects;

import ihm.si3.fr.unice.polytech.polissue.DataBaseAccess;
import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.model.Emergency;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;

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
    private EditText title, description, declarer, location;
    private SeekBar emergencyLevel;
    private TextView titleError, declarerError, locationError;


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
                IssueModel issue = new IssueModel(title.getText().toString(),description.getText().toString(),new Date(), Emergency.MEDIUM,declarer.getText().toString());
                DataBaseAccess dataBaseAccess = new DataBaseAccess();
                dataBaseAccess.postIssue(issue);
            }
        });

        cancelButton.setOnClickListener((v -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, IssueListFragment.newInstance(2));
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }));

        addImage.setOnClickListener(v -> {
            final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, ADD_IMAGE);
        });
        takePicture.setOnClickListener(v -> {
            final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
                startActivityForResult(cameraIntent, TAKE_PICTURE);
            }
        });
        currentLocation.setOnClickListener(v -> {
            //TODO implement the use of the location from the GPS sensor
        });

        return view;
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
        if (requestCode == ADD_IMAGE && resultCode == RESULT_OK) {
            Uri result = data.getData();
            if (result != null) {
                image.setImageURI(result);
            }
        } else if (requestCode == TAKE_PICTURE) {
            Bundle extras = data.getExtras();
            Bitmap preview = (Bitmap) extras.get("data");
            image.setImageBitmap(preview);
        }
    }
}
