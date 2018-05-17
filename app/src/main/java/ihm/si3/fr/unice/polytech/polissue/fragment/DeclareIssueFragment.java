package ihm.si3.fr.unice.polytech.polissue.fragment;


import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ihm.si3.fr.unice.polytech.polissue.DataBaseAccess;
import ihm.si3.fr.unice.polytech.polissue.location.IncidentLocalisationActivity;
import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.model.Emergency;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.model.Location;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclareIssueFragment extends Fragment{

    private static final int REQUEST_GET_MAP_LOCATION = 0;
    private static final String TAG = "DeclareIssueFragment";
    private ImageButton validButton, addImage, currentLocation, cancelButton;
    private ImageView image;
    private EditText title, description, declarer, location;
    private SeekBar emergencyLevel;
    private TextView titleError, declarerError, locationError,cityLocation,cityLocationText;

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
        cityLocation = view.findViewById(R.id.cityLocation);
        cityLocationText = view.findViewById(R.id.cityLocationText);


        validButton.setOnClickListener((v) -> {
            if(checkMandatoryFields()){
                Emergency level = buildEmergencyLevel();
                this.locationMap=new Location(location.getText().toString(),longitude,latitude);
               // IssueModel issue = new IssueModel(title.getText().toString(),description.getText().toString(),new Date(), level,declarer.getText().toString());
                IssueModel issue = new IssueModel(title.getText().toString(),description.getText().toString(),new Date(), level,locationMap,declarer.getText().toString(),"http://www.picslyrics.net/images/141613-rick-astley-never-gonna-give-you-up.jpg");
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
            //TODO implement adding an image
        });

        currentLocation.setOnClickListener(v -> {
            FragmentTransaction ft = ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
            Fragment issueLocationFragment= IncidentLocalisationActivity.newInstance();
            ft.replace(R.id.content_frame, issueLocationFragment );
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();

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
        if (requestCode == REQUEST_GET_MAP_LOCATION && resultCode == Activity.RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
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
            }
        }
    }
}
