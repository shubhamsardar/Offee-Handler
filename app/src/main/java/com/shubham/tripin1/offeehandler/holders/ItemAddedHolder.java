package com.shubham.tripin1.offeehandler.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shubham.tripin1.offeehandler.R;

/**
 * Created by Tripin1 on 6/28/2017.
 */

public class ItemAddedHolder extends RecyclerView.ViewHolder {

    private TextView mName,mPrice;
    private TextView mRemove;


    public ItemAddedHolder(View itemView) {
        super(itemView);

        mName = (TextView)itemView.findViewById(R.id.textViewName);
        mRemove = (TextView)itemView.findViewById(R.id.textViewRemove);
        mPrice = (TextView)itemView.findViewById(R.id.textViewPrise);
    }

    public TextView getmName() {
        return mName;
    }

    public void setmName(TextView mName) {
        this.mName = mName;
    }

    public TextView getmRemove() {
        return mRemove;
    }

    public TextView getmPrice() {
        return mPrice;
    }

    public void setmPrice(TextView mPrice) {
        this.mPrice = mPrice;
    }

    public void setmRemove(TextView mRemove) {
        this.mRemove = mRemove;
    }
}
