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

import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.glide.GlideApp;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;


public class IssueDetailFragment extends Fragment{

    private IssueModel issue;
    private ImageView image;
    private ImageButton share,notification;
    private TextView title, declarer, date,place, description, emergency;
    private MapView mapView;
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

        title.setText(issue.getTitle());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(issue.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                declarer.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        date.setText(issue.getDate().toString());
        if (issue.getLocation().getPlace() != null) place.setText(issue.getLocation().getPlace());
        if (issue.getDescription() !=null) description.setText(issue.getDescription());
        emergency.setText(issue.getEmergency().toString());

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

        return  view;
    }

    /**
     * Load the image from memory or download the picture
     */
    private void loadImage() {
        Context context = getContext();
        if (context != null && issue.getImagePath() != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference(issue.getImagePath());
            GlideApp.with(this)
                    .load(imageRef)
                    .into(image);


        }


    }
}
