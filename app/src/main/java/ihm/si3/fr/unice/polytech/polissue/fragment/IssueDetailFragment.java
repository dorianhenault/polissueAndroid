package ihm.si3.fr.unice.polytech.polissue.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.glide.GlideApp;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;


public class IssueDetailFragment extends Fragment implements OnMapReadyCallback {

    private IssueModel issue;
    private ImageView image, stateImage;
    private ImageButton share,notification;
    private TextView title, declarer, date,place, description, emergency, stateText;
    private MapView mapView;
    private View emergencyLight;
    private static final String TAG = "IssueDetailsFragment";

    public IssueDetailFragment(){}


    public static IssueDetailFragment newInstance() {
        return new IssueDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
            issue=getArguments().getParcelable("issue");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_issue_detail, container, false);

        title=view.findViewById(R.id.incidentTitle);
        declarer=view.findViewById(R.id.incidentDeclarer);
        date=view.findViewById(R.id.incidentDate);
        place=view.findViewById(R.id.incidentPlace);
        description=view.findViewById(R.id.incidentDescription);
        mapView=view.findViewById(R.id.incidentMap);
        image=view.findViewById(R.id.incidentImage);
        share=view.findViewById(R.id.incidentShare);
        notification=view.findViewById(R.id.incidentNotify);
        emergency=view.findViewById(R.id.incidentEmergency);
        emergencyLight = view.findViewById(R.id.emergency_light);
        stateImage = view.findViewById(R.id.incidentStateImage);
        stateText = view.findViewById(R.id.incidentStateText);

        title.setText(issue.getTitle());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(issue.getUserID()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                declarer.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DateFormat dateFr=new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        date.setText(dateFr.format(issue.getDate()));
        if (issue.getLocation().getPlace() != null) place.setText(issue.getLocation().getPlace());
        if (issue.getDescription() !=null) description.setText(issue.getDescription());
        emergency.setText(issue.getEmergency().getMeaning());
        emergencyLight.setBackgroundResource(issue.getEmergency().getDrawableID());

        stateText.setText(issue.getState().getMeaning());
        GlideApp.with(this)
                .load(issue.getState().getDrawableId())
                .into(stateImage);

        notification.setOnClickListener(v -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putParcelable("issue", issue);
            Fragment fragment = UserListFragment.newInstance();
            fragment.setArguments(bundle);
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        });

        loadImage();


        share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TITLE, issue.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, issue.getDescription());
            startActivity(Intent.createChooser(shareIntent, "Partager un incident"));
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return  view;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

    }

    /**
     * Load the image from memory or download the picture
     */
    private void loadImage() {
        Context context = getContext();
        if (context != null && issue.getImagePath() != null && !issue.getImagePath().isEmpty()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference(issue.getImagePath());
            GlideApp.with(this)
                    .load(imageRef)
                    .into(image);


        }else {
            GlideApp.with(this)
                    .load(R.mipmap.ic_logo_polissue)
                    .into(image);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        double latitude = 0;
        double longitude = 0;
        try {
            latitude = issue.getLocation().getLatitude();
            longitude = issue.getLocation().getLongitude();
        } catch (NullPointerException e) {
            System.out.print(e);
        }

        if (longitude != 0 && latitude != 0) {
            LatLng point = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(point));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(17).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
