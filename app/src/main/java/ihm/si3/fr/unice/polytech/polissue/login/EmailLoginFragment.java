package ihm.si3.fr.unice.polytech.polissue.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ihm.si3.fr.unice.polytech.polissue.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragmentListener} interface
 * to handle interaction events.
 */
public class EmailLoginFragment extends Fragment {

    private static final String TAG = "Email Login Fragment";

    private LoginFragmentListener listener;

    private AutoCompleteTextView email;
    private TextView password;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public EmailLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_email_login, container, false);

        email = mainView.findViewById(R.id.email);
        password = mainView.findViewById(R.id.password);
        password.setOnEditorActionListener(
                (TextView v, int actionId, KeyEvent event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        login();
                        return true;
                    }
                    return false;
                }
        );

        Button login = mainView.findViewById(R.id.email_sign_in_button);
        login.setOnClickListener((View v) -> login());

        Button signup = mainView.findViewById(R.id.email_sign_up_button);
        signup.setOnClickListener((View v) -> {
            if (listener != null)
                listener.toSignUp(email.getText().toString(), password.getText().toString());
        });

        return mainView;
    }

    private void login() {
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();
        if (!emailText.isEmpty() && !passwordText.isEmpty()) {
            auth.signOut();
            auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this::loginAckowledge);
        }
    }

    private void loginAckowledge(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "createUserWithEmail:success");
            listener.done();
        } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "createUserWithEmail:failure", task.getException());
            Toast.makeText(getContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            listener = (LoginFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement LoginFragmentListener");
        }

    }


}
