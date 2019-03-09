package com.raihanul.votingbd;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListViewHolder> {
    private List<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTextView;
        public MyListViewHolder(TextView v) {
            super(v);
            v.setOnClickListener(this);
            mTextView = v;
        }

        @Override
        public void onClick(View v) {
            Activity selectActivity = (Activity)v.getContext();
            String voter_idStr = selectActivity.getIntent().getStringExtra("voter_id");
            String campaign = ((TextView)v).getText().toString();
            Intent i = new Intent(v.getContext(), VoteActivity.class);
            i.putExtra("voter_id", voter_idStr);
            i.putExtra("campaign", campaign);
            selectActivity.startActivity(i);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyListAdapter(List<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyListAdapter.MyListViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_list_adapter_view, parent, false);
        MyListViewHolder vh = new MyListViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyListViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}