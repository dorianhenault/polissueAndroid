package ihm.si3.fr.unice.polytech.polissue.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.notifications.IssueNotificationBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclareIssueFragment extends Fragment {

    private ImageButton validButton;

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
        validButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

}
