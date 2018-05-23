package ihm.si3.fr.unice.polytech.polissue.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ihm.si3.fr.unice.polytech.polissue.DataBaseAccess;
import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.adapter.MyUserRecyclerViewAdapter;
import ihm.si3.fr.unice.polytech.polissue.factory.UserFactory;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.model.User;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class UserListFragment extends Fragment {

    private static final String TAG = "UserListFragment";

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private List<User> users = new ArrayList<>();;
    private MyUserRecyclerViewAdapter adapter;
    private OnCheckUserListener checkUserListener;
    private Set<User> usersToNotify = new HashSet<>();
    private IssueModel issue;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserListFragment() {
    }

    public static Fragment newInstance()
    {
        return new UserListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            issue = getArguments().getParcelable("issue");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
            Context context = view.getContext();
            RecyclerView recyclerView =  view.findViewById(R.id.list);
            Button notify = view.findViewById(R.id.notifyButton);
            EditText searchField = view.findViewById(R.id.userSearchField);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            notify.setOnClickListener(v -> {
                DataBaseAccess dataBaseAccess = new DataBaseAccess();
                for (User user : usersToNotify) {
                    dataBaseAccess.postNotification(user, issue);
                }
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                Fragment fragment = IssueDetailFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelable("issue", issue);
                fragment.setArguments(bundle);
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
                Log.d(TAG, "users to notify size :" + usersToNotify.size());
            });
            searchField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equals("")){
                        adapter.filter(users);
                    }
                    filter(s.toString());
                    Log.d(TAG, s.toString());
                }
            });
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = new UserFactory().forge(snapshot);
                        users.add(user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addListenerForSingleValueEvent(eventListener);
            checkUserListener = new OnCheckUserListener() {

                @Override
                public void onUserChecked(User user) {
                    if (!usersToNotify.contains(user)){
                        usersToNotify.add(user);
                    }
                }

                @Override
                public void onUserUnchecked(User user) {
                    if (usersToNotify.contains(user)){
                        usersToNotify.remove(user);
                    }
                }
            };
            adapter = new MyUserRecyclerViewAdapter(users);
            adapter.setCheckUserListener(checkUserListener);
            recyclerView.setAdapter(adapter);
        return view;
    }

    private void filter(String s) {
        List<User> users = new ArrayList<>();

        for (User user : this.users) {
            if (user.getUsername().toLowerCase().contains(s.toLowerCase())||
                    user.getEmail().toLowerCase().contains(s.toLowerCase())){
                users.add(user);
            }
        }
        adapter.filter(users);


    }

    public interface OnCheckUserListener{
        void onUserChecked(User user);
        void onUserUnchecked(User user);
    }


}
