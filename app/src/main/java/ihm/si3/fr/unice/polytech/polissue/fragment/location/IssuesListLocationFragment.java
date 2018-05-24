package ihm.si3.fr.unice.polytech.polissue.fragment.location;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import ihm.si3.fr.unice.polytech.polissue.PermissionUtils;
import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.adapter.MyIssueRecyclerViewAdapter;
import ihm.si3.fr.unice.polytech.polissue.factory.IssueModelFactory;
import ihm.si3.fr.unice.polytech.polissue.fragment.IssueDetailFragment;
import ihm.si3.fr.unice.polytech.polissue.fragment.IssueListFragment;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;

import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING1;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING2;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING3;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING4;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING5;
import static ihm.si3.fr.unice.polytech.polissue.model.Buildings.BUILDING6;

public class IssuesListLocationFragment extends Fragment
        implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
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
    private boolean mPermissionDenied = false;

    private MapView map;
    private GoogleMap mMap;
    private ChildEventListener issueEventListener;
    private FusedLocationProviderClient mLocationClient;
    private DatabaseReference ref;
    private LatLng myPosition;
    private List<Marker>markers=new ArrayList<>();
    public static IssuesListLocationFragment newInstance() {
        return new IssuesListLocationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.incident_list_gmaps, container, false);
        findViewById(view);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);

        Button button=(Button) view.findViewById(R.id.validatePosition) ;
        button.setOnClickListener(v -> {
            FragmentTransaction ft = ((FragmentActivity)this.getContext()).getSupportFragmentManager().beginTransaction();
            Fragment issueListFragemnt= IssueListFragment.newInstance(2);
            ft.replace(R.id.content_frame, issueListFragemnt );
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        });
       // mValues= MyIssueRecyclerViewAdapter.mValues;
        //TODO c est ici que je ne peux pas récuperer les éléments depuis la BD
        ref = FirebaseDatabase.getInstance().getReference("mishap");
        addEventListener();

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
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        initializeBuildings();
        enableMyLocation();
        setCurrentLocation();
        final LocationManager manager = (LocationManager) this.getActivity().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

    }

    private void addMarker(IssueModel issueModel){
        double latitude=0;
        double longitude=0;

        try{
             latitude=issueModel.getLocation().getLatitude();
             longitude=issueModel.getLocation().getLongitude();

        }
        catch (NullPointerException e){
            System.out.print(e);
        }
        if(longitude!=0 && latitude!=0){
            LatLng point=new LatLng(latitude,longitude);
            Marker marker=personaliseMarker(issueModel,point);
        }
    }

    public Marker personaliseMarker(IssueModel issueModel,LatLng point){
        Marker marker= mMap.addMarker(
                new MarkerOptions().position(point)
                        .icon(BitmapDescriptorFactory.defaultMarker(selectEmergencyColor(issueModel)))
                        .title(issueModel.getTitle())
                        .snippet(issueModel.getDate().toString()));
        marker.setTag(issueModel);
        markers.add(marker);

        return marker;
    }

    public float selectEmergencyColor(IssueModel issueModel){
        switch (issueModel.getEmergency()){
            case LOW:
                return BitmapDescriptorFactory.HUE_GREEN;
            case MEDIUM:
                return BitmapDescriptorFactory.HUE_YELLOW;
            case HIGH:
                return BitmapDescriptorFactory.HUE_RED;
            default:
                return BitmapDescriptorFactory.HUE_GREEN;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        IssueModel issueModel=(IssueModel) marker.getTag();
        FragmentTransaction ft = ((FragmentActivity)this.getContext()).getSupportFragmentManager().beginTransaction();
        Fragment issueDetailFragment= IssueDetailFragment.newInstance();
        Bundle bundle=new Bundle();
        bundle.putParcelable("issue",issueModel);
        issueDetailFragment.setArguments(bundle);
        ft.replace(R.id.content_frame, issueDetailFragment );
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }


    private void setCurrentLocation(){
        // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Task<Location> locationTask = mLocationClient.getLastLocation()
                .addOnSuccessListener( this.getActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                        mMap.animateCamera(cameraUpdate);
                        myPosition=latLng;
                        for(Marker marker:markers){
                            double distance=calculDistance(marker.getPosition().latitude,marker.getPosition().longitude,this.myPosition.latitude,this.myPosition.latitude);
                            if(distance>1.0008 || distance < 0.9992){
                                marker.remove();
                            }
                            System.out.println(calculDistance(marker.getPosition().latitude,marker.getPosition().longitude,this.myPosition.latitude,this.myPosition.latitude)+" DISTANCE MAMEN"+"  et nom "+marker.getTitle());

                        }
                    }
                });

        // }
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if(getContext()!=null){
            if (ActivityCompat.checkSelfPermission((Activity)getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }else if (mMap != null) {
                // Access to the location has been granted to the app.
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();

        return true;
    }

    @Override
    public void onResume() {
        map.onResume();
        super.onResume();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this.getActivity(), "Retour sur ma position", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
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
            mPermissionDenied = true;
        }
    }

    public void initializeBuildings(){
        Polygon polygon1 = mMap.addPolygon(BUILDING1.getPolygonOptions());
        Polygon polygon2 = mMap.addPolygon(BUILDING2.getPolygonOptions());
        Polygon polygon3 = mMap.addPolygon(BUILDING3.getPolygonOptions());
        Polygon polygon4 = mMap.addPolygon(BUILDING4.getPolygonOptions());
        Polygon polygon5 = mMap.addPolygon(BUILDING5.getPolygonOptions());
        Polygon polygon6 = mMap.addPolygon(BUILDING6.getPolygonOptions());
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



    public double calculDistance(double lat_a_degre,double lon_a_degre,double lat_b_degre,double lon_b_degre){

        double d = Math.sqrt(Math.pow(lat_b_degre-lat_a_degre,2)+Math.pow(lon_b_degre-lon_a_degre,2))/36.542265;
        return d;
    }

    private void addEventListener(){
        issueEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                IssueModel issue = new IssueModelFactory().forge(dataSnapshot);
                addMarker(issue);
                System.out.println(issue+" INCIDEEENTS");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TODO implement
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TODO implement
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO implement
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO implement
            }
        };
        ref.addChildEventListener(issueEventListener);
    }


}
