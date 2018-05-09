package ihm.si3.fr.unice.polytech.polissue.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import ihm.si3.fr.unice.polytech.polissue.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragmentListener {


    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;

    public LoginActivity() {
        //mandatory empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment startFragment = new LoginSelectorFragment();
        Bundle datas = getIntent().getExtras();

        if (datas != null && datas.containsKey("signUp") && datas.getBoolean("signUp")) {
            startFragment = SignUpFragment.newInstance();
        }

        fragmentTransaction.replace(R.id.login_placeholder, startFragment);
        fragmentTransaction.commit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    @Override
    public void done() {

        finish();
    }

    /**
     * React to the user choosing a login method
     *
     * @param loginMethod {@link LoginMethod} choosen login method
     */
    @Override
    public void methodSelected(LoginMethod loginMethod) {
        if (loginMethod != null) {
            if (loginMethod.equals(LoginMethod.GOOGLE)) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            } else {
                Fragment loginFragment = null;
                if (loginMethod.equals(LoginMethod.EMAIL)) loginFragment = new EmailLoginFragment();
                if (loginFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.login_placeholder, loginFragment);
                    transaction.addToBackStack("emailLogin");
                    transaction.commit();
                }
            }
        }
    }

    @Override
    public void toSignUp(String email, String password) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.login_placeholder, SignUpFragment.newInstance(email, password));
        transaction.addToBackStack("signUp");
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                String errorText = getString(R.string.authFail);
                if (e.getStatusCode() == 12500) {
                    errorText = getString(R.string.error_auth_google_play_service_update);
                }
                Toast.makeText(this, errorText,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "Firebase Auth With google");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, this::ackowledgeCredentialLogin);
    }

    private void ackowledgeCredentialLogin(Task<AuthResult> authResultTask) {
        if (authResultTask.isSuccessful()) {
            Log.d(TAG, "Successfull credential login");
            updateProviderData();
            done();
        } else {
            Log.d(TAG, "Failed credential Login", authResultTask.getException());
            Toast.makeText(this, authResultTask.getException().getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * updates the user's profile with the account provider data
     */
    private void updateProviderData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {

            for (UserInfo data : user.getProviderData()) {
                // Name, email address, and profile photo Url
                String name = data.getDisplayName();
                Uri photoUrl = data.getPhotoUrl();

                UserProfileChangeRequest changeRequest = new UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(name)
                        .setPhotoUri(photoUrl).build();
                user.updateProfile(changeRequest).addOnFailureListener((Exception e) -> {
                    Log.e(TAG, "updateProviderData: error updating profile", e);
                    Toast.makeText(this, getString(R.string.error_profile_settings), Toast.LENGTH_LONG).show();

                });
            }

        }
    }


}

