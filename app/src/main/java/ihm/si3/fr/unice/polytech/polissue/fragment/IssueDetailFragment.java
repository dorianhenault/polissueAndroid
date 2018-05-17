package ihm.si3.fr.unice.polytech.polissue.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import ihm.si3.fr.unice.polytech.polissue.FirebasePictureFetcher;
import ihm.si3.fr.unice.polytech.polissue.R;
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

        title.setText(issue.title);
        declarer.setText(issue.userName);
        date.setText(issue.date.toString());
        place.setText(issue.location.place);
        description.setText(issue.description);
        emergency.setText(issue.emergency.toString());

        notification.setOnClickListener(v -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, UserListFragment.newInstance());
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        });

        loadImage();

        return  view;
    }

    /**
     * Load the image from memory or download the picture
     */
    private void loadImage() {
        Context context = getContext();
        if (context != null && issue.imagePath != null) {
            File imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            StorageReference imageRef = FirebaseStorage.getInstance().getReference(issue.imagePath);
            FirebasePictureFetcher fetcher = new FirebasePictureFetcher(image);

            try {
                fetcher.fetch(imageRef, imageDir);
            } catch (IOException e) {
                Log.e(TAG, "loadImage: failed", e);
                Toast.makeText(getContext(), getString(R.string.error_loading_picture), Toast.LENGTH_LONG).show();
            }
        }


    }
}
