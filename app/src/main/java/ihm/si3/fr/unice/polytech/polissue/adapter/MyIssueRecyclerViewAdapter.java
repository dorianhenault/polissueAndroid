package ihm.si3.fr.unice.polytech.polissue.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.fragment.IssueFragment.OnListFragmentInteractionListener;
import ihm.si3.fr.unice.polytech.polissue.dummy.DummyContent.DummyItem;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyIssueRecyclerViewAdapter extends RecyclerView.Adapter<MyIssueRecyclerViewAdapter.ViewHolder> {

    private final List<IssueModel> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyIssueRecyclerViewAdapter(List<IssueModel> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
//        holder.issueTitle.setText(mValues.get(position).getTitle());
//        holder.issueState.setProgress(mValues.get(position).getState().getProgress());
//        holder.issueDeclarer.setText(mValues.get(position).getDeclarer().getName());
//        holder.issueDate.setText(mValues.get(position).getDate());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.issueModel);
                }
            }
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
}
