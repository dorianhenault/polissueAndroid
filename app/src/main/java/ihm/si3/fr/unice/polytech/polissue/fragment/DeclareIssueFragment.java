package ihm.si3.fr.unice.polytech.polissue.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ihm.si3.fr.unice.polytech.polissue.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeclareIssueFragment extends Fragment {


    public DeclareIssueFragment() {

    }


    public static Fragment newInstance(){
        return new DeclareIssueFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_declare_issue, container, false);
    }

}
