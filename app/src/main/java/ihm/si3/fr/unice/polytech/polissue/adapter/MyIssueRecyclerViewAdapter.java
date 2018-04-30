package ihm.si3.fr.unice.polytech.polissue.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import ihm.si3.fr.unice.polytech.polissue.R;

import ihm.si3.fr.unice.polytech.polissue.fragment.IssueDetailFragment;
import ihm.si3.fr.unice.polytech.polissue.fragment.IssueListFragment;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;

/**
 * {@link RecyclerView.Adapter} that can display a {@link IssueModel}
 * TODO: Replace the implementation with code for your data type.
 */
public class MyIssueRecyclerViewAdapter extends RecyclerView.Adapter<MyIssueRecyclerViewAdapter.ViewHolder> {

    private final List<IssueModel> mValues;
    private ChildEventListener issueEventListener;
    private DatabaseReference ref;



    public MyIssueRecyclerViewAdapter() {
        mValues=new ArrayList<>();
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
        holder.issueTitle.setText(mValues.get(position).title);
//        holder.issueState.setProgress(mValues.get(position).getState().getProgress());
//        holder.issueDeclarer.setText(mValues.get(position).getDeclarer().getName());
//        holder.issueDate.setText(mValues.get(position).getDate());

        holder.mView.setOnClickListener(v -> {
            FragmentTransaction ft = ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
            Fragment issueDetailFragment=IssueDetailFragment.newInstance();
            Bundle bundle=new Bundle();
            bundle.putParcelable("issue",mValues.get(position));
            issueDetailFragment.setArguments(bundle);
            ft.replace(R.id.content_frame, issueDetailFragment );
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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

    private void addEventListener(){
        issueEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                IssueModel issue = dataSnapshot.getValue(IssueModel.class);
                mValues.add(issue);
                notifyItemInserted(mValues.size()-1);
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
