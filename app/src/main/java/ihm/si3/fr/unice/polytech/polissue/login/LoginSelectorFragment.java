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
 * {@link LoginFragmentListener} interface to handle the login method being selected
 */
public class LoginSelectorFragment extends Fragment {

    private LoginFragmentListener mListener;

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
            if (mListener != null)
                mListener.methodSelected(LoginFragmentListener.LoginMethod.EMAIL);
        });

        Button googleButton = mainView.findViewById(R.id.google_login);
        googleButton.setOnClickListener((View view) -> {
            if (mListener != null)
                mListener.methodSelected(LoginFragmentListener.LoginMethod.GOOGLE);
        });

        Button facebookButton = mainView.findViewById(R.id.facebook_login);
        facebookButton.setOnClickListener((View view) -> {
            if (mListener != null)
                mListener.methodSelected(LoginFragmentListener.LoginMethod.FACEBOOK);
        });

        Button signUpButton = mainView.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener((View view) -> {
            if (mListener != null) mListener.toSignUp("", "");
        });

        return mainView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            mListener = (LoginFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement LoginFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
