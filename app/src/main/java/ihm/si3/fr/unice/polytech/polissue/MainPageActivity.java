package ihm.si3.fr.unice.polytech.polissue;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ihm.si3.fr.unice.polytech.polissue.login.LoginActivity;
import android.view.View;

import ihm.si3.fr.unice.polytech.polissue.fragment.DeclareIssueFragment;
import ihm.si3.fr.unice.polytech.polissue.fragment.IssueListFragment;

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
                displayView(R.id.nav_declare_issue);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayView(R.id.nav_issues_list);


        auth = FirebaseAuth.getInstance();

        auth.signOut();
        auth.addAuthStateListener(new NavigationAuthStateListener(navigationView));

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
        displayView(item.getItemId());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayView(int itemId){
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (itemId == R.id.nav_issues_list) {
            fragment = IssueListFragment.newInstance(2);
            title = getString(R.string.issue_list);
        } else if ( itemId == R.id.nav_log_in){
            Intent logInIntent = new Intent(this, LoginActivity.class);
            startActivity(logInIntent);
        } else if(itemId == R.id.nav_log_out) {
            auth.signOut();
        } else if(itemId == R.id.nav_declare_issue){
            fragment = DeclareIssueFragment.newInstance();
            title = getString(R.string.declare_issue);
        }

        if (fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            if (getSupportActionBar()!=null){
                getSupportActionBar().setTitle(title);
            }
        }


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
        }
    }
}
