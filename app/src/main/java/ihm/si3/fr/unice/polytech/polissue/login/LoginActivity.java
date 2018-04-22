package ihm.si3.fr.unice.polytech.polissue.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ihm.si3.fr.unice.polytech.polissue.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements EmailLoginFragment.EmailLoginFragmentListener, LoginSelectorFragment.LoginSelectorListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_placeholder, new LoginSelectorFragment());
        fragmentTransaction.commit();

    }


    @Override
    public void loggedIn() {

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
            Fragment loginFragment = null;
            if (loginMethod.equals(LoginMethod.EMAIL)) loginFragment = new EmailLoginFragment();
            if (loginFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.login_placeholder, loginFragment);
                transaction.commit();
            }
        }
    }

    public enum LoginMethod {
        EMAIL, FACEBOOK, GOOGLE
    }


}

