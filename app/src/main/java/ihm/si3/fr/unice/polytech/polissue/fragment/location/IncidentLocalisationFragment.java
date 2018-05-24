package ihm.si3.fr.unice.polytech.polissue.fragment.location;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import ihm.si3.fr.unice.polytech.polissue.PermissionUtils;
import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.fragment.DeclareIssueFragment;
import ihm.si3.fr.unice.polytech.polissue.fragment.IssueDetailFragment;
import ihm.si3.fr.unice.polytech.polissue.model.Buildings;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;

import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING1;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING2;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING3;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING4;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING5;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING6;

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


    private MapView map;
    private ConstraintLayout issuePositionContainer;
    private Button validatePosition,validateDescription,cancelDescription;
    private EditText positionDescription;
    private GoogleMap mMap;
    private Spinner spinnerClassRooms;
    private TextView locationDescription;

    private FusedLocationProviderClient mLocationClient;
    private LatLng incidentPosition;
    private LatLng myPosition;
    private LatLng buildingPosition;
    private String positionDescriptionText="";

    private IssueModel issueModel;
    private ArrayList<String> classrooms;

    private Marker marker;
    private Marker buildingMarker;

    private Polygon currentPolygon;
    private List<Polygon>polygonList=new ArrayList<>();

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
        map = view.findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);
        validatePosition=view.findViewById(R.id.validatePosition) ;
        validateDescription= view.findViewById(R.id.validateDescription) ;
        positionDescription=view.findViewById(R.id.issuePositionText) ;
        cancelDescription=view.findViewById(R.id.canceldescription) ;
        locationDescription=view.findViewById(R.id.locationDescription) ;

        spinnerClassRooms=view.findViewById(R.id.spinnerClassRooms);
        classrooms=new ArrayList<>();
        classrooms.add("");
        spinnerClassRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionDescriptionText=(String)parent.getItemAtPosition(position);
                //positionDescription.setText((String)parent.getItemAtPosition(position));
                checkIfOtherThanAClassRoomSelected((String)parent.getItemAtPosition(position));
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
                bundle.putParcelable("imageUri",getArguments().getParcelable("imageUri"));
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

        validateDescription.setOnClickListener((View v) -> {
            if(!checkMandatoryFields()) {
                this.positionDescriptionText = positionDescription.getText().toString();
            }
            if(checkMandatoryFields()){
                issuePositionContainer.setVisibility(View.GONE);
                validatePosition.setVisibility(View.VISIBLE);

                if(marker!=null) {
                    marker.setTitle(positionDescriptionText);
                    marker.showInfoWindow();
                }

                if(currentPolygon!=null && marker==null){
                    MarkerOptions options=(MarkerOptions) currentPolygon.getTag();
                    options.title(positionDescriptionText);
                    buildingMarker=mMap.addMarker(options);
                    buildingMarker.showInfoWindow();
                }


                initialiseMapClickListener();
                mMap.setOnMarkerClickListener(this);
                mMap.setMapStyle(null);
                changePolygonColor(false);

            }
            else{
                Toast.makeText(this.getContext(), "Une description doit être ajoutée ", Toast.LENGTH_SHORT).show();
            }

        });

        cancelDescription.setOnClickListener(v -> {
            issuePositionContainer.setVisibility(View.GONE);
            validatePosition.setVisibility(View.VISIBLE);
            initialiseMapClickListener();
            mMap.setOnMarkerClickListener(this);
            mMap.setMapStyle(null);
            changePolygonColor(false);

        });

        return view;
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
        initialiseMapClickListener();
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
        positionDescriptionText="";
        locationDescription.setVisibility(View.VISIBLE);
        issuePositionContainer.setVisibility(View.GONE);
        validatePosition.setVisibility(View.VISIBLE);
        incidentPosition=null;
        marker.remove();

        return true;
    }

    public void initialiseMapClickListener(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //mMap.clear();
                if(marker!=null) {
                    marker.remove();
                    marker=null;
                }
                if(buildingMarker!=null) {
                    buildingMarker.remove();
                    buildingMarker=null;
                }
                positionDescription.setText("");
                positionDescriptionText="";
                locationDescription.setVisibility(View.VISIBLE);
                marker=mMap.addMarker(new MarkerOptions().position(point));
                incidentPosition=point;
                buildingPosition=null;
                showIssueDescriptionContainer(false);

            }
        });
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this.getActivity(), "Retour vers ma position", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private boolean checkMandatoryFields() {
        if (positionDescriptionText.length() == 0 || positionDescriptionText.equals("")){
            return false;
        }
        return true;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this.getActivity(), "L'incident est sur ma position", Toast.LENGTH_SHORT).show();
        showIssueDescriptionContainer(false);
        incidentPosition=null;
        buildingPosition=null;
    }

    private void showIssueDescriptionContainer(boolean building) {
        if(building){
            spinnerClassRooms.setVisibility(View.VISIBLE);
            positionDescription.setVisibility(View.GONE);
        }
        else{
            positionDescription.setVisibility(View.VISIBLE);
            spinnerClassRooms.setVisibility(View.GONE);

        }
        issuePositionContainer.setVisibility(View.VISIBLE);
        validatePosition.setVisibility(View.GONE);

        mMap.setOnMapClickListener(null);
        mMap.setOnMarkerClickListener(null);
        changePolygonColor(true);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.getActivity(), R.raw.style_json));

            if (!success) {
                System.err.println("Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            System.err.println("Can't find style. Error: "+ e);
        }

    }

    public void changePolygonColor(boolean transparency){
        if(transparency){
            for (Polygon polygon : polygonList) {
                polygon.setFillColor(0x55205DCF);
                polygon.setStrokeColor(0x55205DCF);
            }
        }
        else{
            for (Polygon polygon : polygonList) {
                polygon.setFillColor(0xFF205DCF);
                polygon.setStrokeColor(0xFF205DCF);
            }
        }

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
        if(this.incidentPosition!=null){
            return incidentPosition;
        }
        else if(this.buildingPosition!=null){
            return buildingPosition;
        }
        else{
            return myPosition;
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
        polygonList.add(polygon1);
        polygonList.add(polygon2);
        polygonList.add(polygon3);
        polygonList.add(polygon4);
        polygonList.add(polygon5);
        polygonList.add(polygon6);


        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {


            @Override
            public void onPolygonClick(Polygon polygon) {
                if(marker!=null){
                    marker.remove();
                    marker=null;
                }
                if(buildingMarker!=null){
                    buildingMarker.remove();
                    buildingMarker=null;
                }
                //positionDescription.setText("");
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
                classrooms.add("Autre");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, classrooms);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapter.notifyDataSetChanged();
                spinnerClassRooms.setAdapter(adapter);
                currentPolygon=polygon;
                currentPolygon.setTag(new MarkerOptions().position(createPositionWithPolygonPoints(polygon.getPoints())));
                buildingPosition=createPositionWithPolygonPoints(polygon.getPoints());
                incidentPosition=null;

            }
        });
    }

    public LatLng createPositionWithPolygonPoints(List<LatLng> list){
        double lat=0;
        double lon=0;
        for(LatLng pos:list){
            lat+=pos.latitude;
            lon+=pos.longitude;
        }
        return new LatLng(lat/list.size(),lon/list.size());
    }

    private void checkIfOtherThanAClassRoomSelected(String selected){
        if(selected.equals("Autre")){
            positionDescriptionText="";
            positionDescription.setVisibility(View.VISIBLE);
            locationDescription.setVisibility(View.GONE);

        }
        else{
            positionDescription.setVisibility(View.GONE);
            locationDescription.setVisibility(View.VISIBLE);

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Votre GPS semble être désactivé, voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        LatLng latLng = new LatLng(43.615243,7.072978);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                        mMap.animateCamera(cameraUpdate);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
