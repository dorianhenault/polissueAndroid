package ihm.si3.fr.unice.polytech.polissue;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import ihm.si3.fr.unice.polytech.polissue.fragment.location.IssuesListLocationFragment;
import ihm.si3.fr.unice.polytech.polissue.login.LoginActivity;
import java.net.MalformedURLException;
import java.net.URL;

import ihm.si3.fr.unice.polytech.polissue.fragment.DeclareIssueFragment;
import ihm.si3.fr.unice.polytech.polissue.fragment.IssueListFragment;
import ihm.si3.fr.unice.polytech.polissue.service.HighEmergencyIssueService;
import ihm.si3.fr.unice.polytech.polissue.login.LoginActivity;
import ihm.si3.fr.unice.polytech.polissue.service.NotifyUserService;

public class MainPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
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
        fab.setVisibility(View.GONE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayView(R.id.nav_issues_list);


        Intent highEmergencyIssueServiceIntent = new Intent(getApplicationContext(), HighEmergencyIssueService.class);
        Intent notifyUserServiceIntent = new Intent(getApplicationContext(), NotifyUserService.class);
        getApplicationContext().startService(highEmergencyIssueServiceIntent);
        getApplicationContext().startService(notifyUserServiceIntent);

        auth = FirebaseAuth.getInstance();

       // if (auth.getCurrentUser() == null) auth.signInAnonymously();
        auth.addAuthStateListener(new NavigationAuthStateListener(navigationView));
        auth.addAuthStateListener(new DatabaseAuthStateListener());
        FacebookSdk.setApplicationId(getString(R.string.facebook_application_id));



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
        final Fragment[] fragment = {null};
        final String[] title = {getString(R.string.app_name)};

        if (itemId == R.id.nav_issues_list) {
            fragment[0] = IssueListFragment.newInstance(2);
            title[0] = getString(R.string.issue_list);
        } else if ( itemId == R.id.nav_log_in){
            Intent logInIntent = new Intent(this, LoginActivity.class);
            startActivity(logInIntent);
        } else if(itemId == R.id.nav_log_out) {
            auth.signOut();
        } else if (itemId == R.id.nav_sign_in) {
            title[0] = getString(R.string.declare_issue);
            Intent signInIntent = new Intent(this, LoginActivity.class);
            signInIntent.putExtra("signUp", true);
            startActivity(signInIntent);
        }else if(itemId == R.id.nav_issues_list_maps){
            fragment[0] = IssuesListLocationFragment.newInstance();
            title[0] = getString(R.string.issue_list_maps);
        } else if(itemId == R.id.nav_declare_issue){
            FirebaseUser user = auth.getCurrentUser();
            if (user == null){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.dialog_title);
                alertDialogBuilder.setMessage(R.string.dialog_message)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_positive_button, (dialog, which) -> {
                            Intent signInIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(signInIntent);
                            fragment[0] = DeclareIssueFragment.newInstance();
                            title[0] = getString(R.string.declare_issue);
                        })
                        .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                fragment[0] = null;
                            }
                        });
                alertDialogBuilder.create();
                alertDialogBuilder.show();
        }else {
                fragment[0] = DeclareIssueFragment.newInstance();
                title[0] = getString(R.string.declare_issue);
            }
        }

        if (fragment[0] != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment[0]);
            ft.commit();
            ft.addToBackStack(null);

            if (getSupportActionBar()!=null){
                getSupportActionBar().setTitle(title[0]);
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

            if (user != null) {
                View v = navView.getHeaderView(0);

                ImageView profilePic = v.findViewById(R.id.nav_header_profile_pic);
                TextView username = v.findViewById(R.id.nav_header_username);
                TextView email = v.findViewById(R.id.nav_header_email);

                username.setText(user.getDisplayName());
                email.setText(user.getEmail());
                try {
                    (new PictureFecthingTask(profilePic)).execute(new URL(String.valueOf(user.getPhotoUrl())));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
