package ihm.si3.fr.unice.polytech.polissue.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ihm.si3.fr.unice.polytech.polissue.R;

/**
 * {@link Fragment} subclass to handle the login method selection
 * Activities that contain this fragment must implement the
 * {@link LoginSelectorListener} interface to handle the login method being selected
 */
public class LoginSelectorFragment extends Fragment {

    private LoginSelectorListener mListener;

    public LoginSelectorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_login_selector, container, false);

        Button emailButton = mainView.findViewById(R.id.email_login);
        emailButton.setOnClickListener((View view) -> {
            if (mListener != null) mListener.methodSelected(LoginActivity.LoginMethod.EMAIL);
        });

        Button googleButton = mainView.findViewById(R.id.google_login);
        googleButton.setOnClickListener((View view) -> {
            if (mListener != null) mListener.methodSelected(LoginActivity.LoginMethod.GOOGLE);
        });

        Button facebookButton = mainView.findViewById(R.id.facebook_login);
        facebookButton.setOnClickListener((View view) -> {
            if (mListener != null) mListener.methodSelected(LoginActivity.LoginMethod.FACEBOOK);
        });


        return mainView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginSelectorListener) {
            mListener = (LoginSelectorListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement LoginSelectorListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface LoginSelectorListener {
        void methodSelected(LoginActivity.LoginMethod method);
    }
}
