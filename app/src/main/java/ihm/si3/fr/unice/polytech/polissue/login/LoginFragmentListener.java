package ihm.si3.fr.unice.polytech.polissue.login;

public interface LoginFragmentListener {
    public enum LoginMethod {
        EMAIL, FACEBOOK, GOOGLE, SIGN_UP
    }

    void done();

    void methodSelected(LoginMethod loginMethod);

    void toSignUp(String email, String password);
}
