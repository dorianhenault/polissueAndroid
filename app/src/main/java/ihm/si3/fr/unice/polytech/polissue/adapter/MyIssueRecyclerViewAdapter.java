package ihm.si3.fr.unice.polytech.polissue.adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ihm.si3.fr.unice.polytech.polissue.R;

import ihm.si3.fr.unice.polytech.polissue.factory.IssueModelFactory;
import ihm.si3.fr.unice.polytech.polissue.fragment.IssueDetailFragment;
import ihm.si3.fr.unice.polytech.polissue.glide.GlideApp;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;
import ihm.si3.fr.unice.polytech.polissue.model.State;

/**
 * {@link RecyclerView.Adapter} that can display a {@link IssueModel}
 */
public class MyIssueRecyclerViewAdapter extends RecyclerView.Adapter<MyIssueRecyclerViewAdapter.ViewHolder> {

    private final List<IssueModel> mValues;
    private ChildEventListener issueEventListener;
    private DatabaseReference ref;
    private static final String TAG = "IssueViewAdapter";


    public MyIssueRecyclerViewAdapter() {
        mValues = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("mishap");
        addEventListener();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_issue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.issueModel = mValues.get(position);
        holder.issueTitle.setText(mValues.get(position).getTitle());
        setProgressBar(holder.issueState, holder.issueModel.getState());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference declarerRef =  ref.child("users").child(holder.issueModel.getUserID()).child("username");
        declarerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.issueDeclarer.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.issueDeclarer.setText(mValues.get(position).getUserID());
        holder.issueDate.setText(new SimpleDateFormat("dd-mm-yyyy HH:mm", Locale.FRANCE).format(mValues.get(position).getDate()));
        if (!mValues.get(position).getImagePath().equals("") || mValues.get(position).getImagePath() != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference(mValues.get(position).getImagePath());
            GlideApp.with(holder.issueImage.getContext())
                    .load(imageRef)
                    .into(holder.issueImage);
        }


        holder.mView.setOnClickListener(v -> {
            FragmentTransaction ft = ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction();
            Fragment issueDetailFragment = IssueDetailFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putParcelable("issue", mValues.get(position));
            issueDetailFragment.setArguments(bundle);
            ft.replace(R.id.content_frame, issueDetailFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    private void setProgressBar(ProgressBar progressBar, State state){
        if (state == State.NOT_RESOLVED){
            progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }else if (state == State.RESOLVED){
            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        }else {
            progressBar.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        }
        progressBar.setProgress(state.getProgress());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView issueImage;
        public final TextView issueTitle;
        public final TextView issueDeclarer;
        public final TextView issueDate;
        public final ProgressBar issueState;
        public IssueModel issueModel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            issueImage = view.findViewById(R.id.issueImage);
            issueTitle = view.findViewById(R.id.issueTitle);
            issueDeclarer = view.findViewById(R.id.issueDeclarer);
            issueDate = view.findViewById(R.id.issueDate);
            issueState = view.findViewById(R.id.issueState);

        }
    }

    private void addEventListener() {
        issueEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                IssueModel issue = new IssueModelFactory().forge(dataSnapshot);
                mValues.add(issue);
                notifyItemInserted(mValues.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TODO implement
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TODO implement
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO implement
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO implement
            }
        };
        ref.addChildEventListener(issueEventListener);
    }


}
