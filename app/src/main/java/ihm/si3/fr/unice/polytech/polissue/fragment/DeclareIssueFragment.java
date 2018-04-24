package ihm.si3.fr.unice.polytech.polissue.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.notifications.IssueNotificationBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclareIssueFragment extends Fragment {

    private ImageButton validButton;
    private ImageButton addImage;
    private ImageButton currentLocation;
    private ImageButton cancelButton;
    private ImageView image;
    private EditText title;
    private EditText description;
    private EditText declarer;
    private EditText location;
    private SeekBar emergencyLevel;


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
        validButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueModel issueModel;
            }
        });
        return view;
    }

}
