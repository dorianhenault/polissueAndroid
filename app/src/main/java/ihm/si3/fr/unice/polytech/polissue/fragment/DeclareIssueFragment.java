package ihm.si3.fr.unice.polytech.polissue.fragment;


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
import ihm.si3.fr.unice.polytech.polissue.fragment.location.IncidentLocalisationFragment;
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
    private EditText title, description, declarer;
    private SeekBar emergencyLevel;
    private TextView titleError, declarerError, locationError,cityLocation,cityLocationText;

    private Location locationMap;

    private double longitude=0;
    private double latitude=0;
    private boolean locationButtonClicked=false;

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
        emergencyLevel = view.findViewById(R.id.emergencyLevel);
        titleError = view.findViewById(R.id.titleError);
        declarerError = view.findViewById(R.id.declarerError);
        locationError = view.findViewById(R.id.locationError);
        cityLocation = view.findViewById(R.id.cityLocation);
        cityLocationText = view.findViewById(R.id.cityLocationText);

        if(getArguments()!=null){
            this.locationMap=getArguments().getParcelable("location");
            this.longitude=locationMap.longitude;
            this.latitude=locationMap.latitude;
            restoreFormFields(getArguments().getParcelable("issue"));
            this.locationButtonClicked=getArguments().getBoolean("buttonClicked");
        }

        validButton.setOnClickListener((v) -> {
            if(checkMandatoryFields()){
                Emergency level = buildEmergencyLevel();
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
            IssueModel issue = new IssueModel(title.getText().toString(),description.getText().toString(),new Date(), buildEmergencyLevel(),locationMap,declarer.getText().toString(),"");
            Bundle bundle=new Bundle();
            bundle.putParcelable("issue",issue);
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
            locationDescription.setText(locationMap.place);
            locationDescription.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void restoreFormFields(IssueModel issueModel){
        if(issueModel.title!=null){
            this.title.setText(issueModel.title);
        }
        if(issueModel.emergency!=null){
            this.emergencyLevel.setProgress(buildEmergencyLevelReversed(issueModel.emergency));
        }
        if(issueModel.userName!=null){
            this.declarer.setText(issueModel.userName);
        }
        //if(issueModel.imageURL!=null){
         //   this.im.setText(issueModel.title);
        //}
        if(issueModel.description!=null){
            this.description.setText(issueModel.description);
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
        if (!locationButtonClicked){
            locationError.setVisibility(View.VISIBLE);
            ok = false;
        }
        return ok;
    }

}
