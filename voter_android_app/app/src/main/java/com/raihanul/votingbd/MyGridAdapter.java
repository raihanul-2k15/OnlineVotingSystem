package com.raihanul.votingbd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MyGridAdapter extends RecyclerView.Adapter<MyGridAdapter.MyGridViewHolder> {
    private List<String> mDataset;
    private List<String> mDataset2;
    private List<Bitmap> mDataset3;
    private List<String> mDataset4;
    public String mData, mData2;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        public ImageView mImageView;
        public Bitmap mBitmap;
        public TextView mTextView;
        public String mString, mString2, mString3, mString4;
        public MyGridViewHolder(LinearLayout ll) {
            super(ll);
            ll.setOnClickListener(this);
            mLinearLayout = ll;
            mImageView = (ImageView) ll.findViewById(R.id.candidateImg);
            mTextView = (TextView) ll.findViewById(R.id.candidateName);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), ConfirmActivity.class);
            i.putExtra("voter_id", mString3);
            i.putExtra("campaign", mString4);
            i.putExtra("candidate_id", mString2);
            i.putExtra("candidate_name", mTextView.getText().toString());
            i.putExtra("candidate_desc", mString);
            i.putExtra("candidate_img", mBitmap);
            v.getContext().startActivity(i);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyGridAdapter(List<String> myDataset, List<String> myDataset2, List<Bitmap> myDataset3, List<String> myDataset4, String myData, String myData2) {
        mDataset = myDataset;
        mDataset2 = myDataset2;
        mDataset3 = myDataset3;
        mDataset4 = myDataset4;
        mData = myData;
        mData2 = myData2;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyGridAdapter.MyGridViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_grid_adapter_view, parent, false);
        MyGridViewHolder vh = new MyGridViewHolder(ll);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyGridViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));
        holder.mString = mDataset2.get(position);
        Bitmap img = mDataset3.get(position);
        holder.mBitmap = img;
        if (img != null) {
            holder.mImageView.setImageBitmap(img);
        }
        holder.mString2 = mDataset4.get(position);
        holder.mString3 = mData;
        holder.mString4 = mData2;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}