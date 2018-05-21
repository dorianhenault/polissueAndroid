package ihm.si3.fr.unice.polytech.polissue.fragment.location;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import ihm.si3.fr.unice.polytech.polissue.PermissionUtils;
import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.fragment.DeclareIssueFragment;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;

import static com.facebook.FacebookSdk.getApplicationContext;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.*;

public class IncidentLocalisationFragment extends Fragment
        implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
   // private boolean mPermissionDenied = false;

    private MapView map;
    private ConstraintLayout issuePositionContainer;
    private Button validatePosition,validateDescription,cancelDescription;
    private EditText positionDescription;
    private GoogleMap mMap;
    private Spinner spinnerClassRooms;

    private FusedLocationProviderClient mLocationClient;
    private LatLng incidentPosition;
    private LatLng myPosition;
    private String positionDescriptionText="";

    private IssueModel issueModel;
    private ArrayList<String> classrooms;

    private Marker marker;

    public static IncidentLocalisationFragment newInstance() {
        return new IncidentLocalisationFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments()!=null){
            issueModel=getArguments().getParcelable("issue");
        }

        final View view = inflater.inflate(R.layout.incident_location_gmaps, container, false);
        issuePositionContainer=view.findViewById(R.id.issuePositionContainer);
        findViewById(view);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);
        validatePosition=view.findViewById(R.id.validatePosition) ;
        validateDescription= view.findViewById(R.id.validateDescription) ;
        positionDescription=view.findViewById(R.id.issuePositionText) ;
        cancelDescription=view.findViewById(R.id.canceldescription) ;

        spinnerClassRooms=view.findViewById(R.id.spinnerClassRooms);
        classrooms=new ArrayList<>();
        classrooms.add("");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, classrooms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
        spinnerClassRooms.setAdapter(adapter);
        spinnerClassRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                positionDescription.setText((String)parent.getItemAtPosition(position));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        validatePosition.setOnClickListener(v -> {
            if(checkMandatoryFields()){
                ihm.si3.fr.unice.polytech.polissue.model.Location location=
                        new ihm.si3.fr.unice.polytech.polissue.model.Location(positionDescriptionText,getIncidentPosition().longitude,getIncidentPosition().latitude);
                FragmentTransaction ft = ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                Fragment declareIssueFragment= DeclareIssueFragment.newInstance();
                Bundle bundle=new Bundle();
                bundle.putParcelable("location",location);
                bundle.putParcelable("issue",issueModel);
                bundle.putBoolean("buttonClicked",true);
                declareIssueFragment.setArguments(bundle);
                ft.replace(R.id.content_frame, declareIssueFragment );
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
            }
            else{
                Toast.makeText(this.getContext(), "Une description doit être ajoutée ", Toast.LENGTH_SHORT).show();
            }

        });

        validateDescription.setOnClickListener(v -> {
            if(checkMandatoryFields()){
                this.positionDescriptionText=positionDescription.getText().toString();
                issuePositionContainer.setVisibility(View.GONE);
                validatePosition.setVisibility(View.VISIBLE);
            }
            else{
                Toast.makeText(this.getContext(), "Une description doit être ajoutée ", Toast.LENGTH_SHORT).show();
            }

        });

        cancelDescription.setOnClickListener(v -> {
            issuePositionContainer.setVisibility(View.GONE);
            validatePosition.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private void findViewById(View rootView) {

        map = (MapView) rootView.findViewById(R.id.map);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMarkerClickListener(this);

        initializeBuildings();
        enableMyLocation();
        setCurrentLocation();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //mMap.clear();
                if(marker!=null)
                    marker.remove();
                positionDescription.setText("");
                marker=mMap.addMarker(new MarkerOptions().position(point));
                incidentPosition=point;
                showIssueDescriptionContainer(false);
            }
        });
        final LocationManager manager = (LocationManager) this.getActivity().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

    }

    @Override
    public void onResume() {
        map.onResume();
        super.onResume();
    }

    private void setCurrentLocation(){
        // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Task<Location> locationTask = mLocationClient.getLastLocation()
                .addOnSuccessListener( this.getActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        System.out.println(location+"LOCATION");
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                        mMap.animateCamera(cameraUpdate);
                        myPosition=latLng;
                    }
                });

        // }
    }



    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Toast.makeText(this.getActivity(), "Marqueur de l'incident retiré", Toast.LENGTH_SHORT).show();
        positionDescription.setText("");
        issuePositionContainer.setVisibility(View.GONE);
        validatePosition.setVisibility(View.VISIBLE);
        incidentPosition=null;
        marker.remove();
        return true;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this.getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private boolean checkMandatoryFields() {
        if (positionDescription.getText().length() == 0 || positionDescription.getText().toString().equals("")){
            return false;
        }
        return true;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this.getActivity(), "L'incident est sur ma position", Toast.LENGTH_SHORT).show();
        showIssueDescriptionContainer(false);
    }

    private void showIssueDescriptionContainer(boolean building) {
        if(building){
            spinnerClassRooms.setVisibility(View.VISIBLE);
        }
        else{
            spinnerClassRooms.setVisibility(View.GONE);

        }
        issuePositionContainer.setVisibility(View.VISIBLE);
        validatePosition.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
           // mPermissionDenied = true;
        }
    }



    public LatLng getIncidentPosition(){
        if(this.incidentPosition==null){
            return myPosition;
        }
        else{
            return incidentPosition;
        }
    }

    public void initializeBuildings(){
        Polygon polygon1 = mMap.addPolygon(BUILDING1.getPolygonOptions());
        polygon1.setClickable(true);
        Polygon polygon2 = mMap.addPolygon(BUILDING2.getPolygonOptions());
        polygon2.setClickable(true);
        Polygon polygon3 = mMap.addPolygon(BUILDING3.getPolygonOptions());
        polygon3.setClickable(true);
        Polygon polygon4 = mMap.addPolygon(BUILDING4.getPolygonOptions());
        polygon4.setClickable(true);
        Polygon polygon5 = mMap.addPolygon(BUILDING5.getPolygonOptions());
        polygon5.setClickable(true);
        Polygon polygon6 = mMap.addPolygon(BUILDING6.getPolygonOptions());
        polygon6.setClickable(true);

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                if(marker!=null)
                    marker.remove();
                showIssueDescriptionContainer(true);
                if(polygon.toString().equals(polygon1.toString())){
                    classrooms.clear();
                    classrooms.addAll(BUILDING1.getClassRooms());
                }else  if(polygon.toString().equals(polygon2.toString())){
                    classrooms.clear();
                    classrooms.addAll(BUILDING2.getClassRooms());
                }else if(polygon.toString().equals(polygon3.toString())){
                    classrooms.clear();
                    classrooms.addAll(BUILDING3.getClassRooms());
                }else if(polygon.toString().equals(polygon4.toString())){
                   classrooms.clear();
                    classrooms.addAll(BUILDING4.getClassRooms());
                }else if(polygon.toString().equals(polygon5.toString())){
                    classrooms.clear();
                    classrooms.addAll(BUILDING5.getClassRooms());
                }else if(polygon.toString().equals(polygon6.toString())){
                    classrooms.clear();
                    classrooms.addAll(BUILDING6.getClassRooms());
                }
            }
        });

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
