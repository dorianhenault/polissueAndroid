package ihm.si3.fr.unice.polytech.polissue;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ihm.si3.fr.unice.polytech.polissue.login.LoginActivity;

public class MainPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        auth = FirebaseAuth.getInstance();

        auth.signOut();
        auth.addAuthStateListener(new NavigationAuthStateListener(navigationView));
        FacebookSdk.setApplicationId(getString(R.string.facebook_application_id));
        AppEventsLogger.activateApp(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_log_in) {
            Intent logInIntent = new Intent(this, LoginActivity.class);
            startActivity(logInIntent);
        } else if (id == R.id.nav_log_out) {
            auth.signOut();
        } else if (id == R.id.nav_sign_in) {
            Intent signInIntent = new Intent(this, LoginActivity.class);
            signInIntent.putExtra("signUp", true);
            startActivity(signInIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Updates the navigation view based on the auth state
     */
    private class NavigationAuthStateListener implements FirebaseAuth.AuthStateListener {
        private final NavigationView navView;

        NavigationAuthStateListener(NavigationView navigationView) {
            this.navView = navigationView;
        }

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Menu menu = navView.getMenu();

            MenuItem logIn = menu.findItem(R.id.nav_log_in);
            MenuItem logOut= menu.findItem(R.id.nav_log_out);
            MenuItem signIn= menu.findItem(R.id.nav_sign_in);
            MenuItem account= menu.findItem(R.id.nav_account);

            logIn.setVisible(user==null);
            signIn.setVisible(user==null);
            logOut.setVisible(user!=null);
            account.setVisible(user!=null);

            if (user != null) {
                View v = navView.getHeaderView(0);

                ImageView profilePic = v.findViewById(R.id.nav_header_profile_pic);
                TextView username = v.findViewById(R.id.nav_header_username);
                TextView email = v.findViewById(R.id.nav_header_email);

                username.setText(user.getDisplayName());
                email.setText(user.getEmail());

            }
        }
    }
}
