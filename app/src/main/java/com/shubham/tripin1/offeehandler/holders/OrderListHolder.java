package com.shubham.tripin1.offeehandler.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shubham.tripin1.offeehandler.R;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * Created by Tripin1 on 6/21/2017.
 */

public class OrderListHolder extends RecyclerView.ViewHolder {

    public RecyclerView recyclerView;
    public ImageView imageUser,imageRemove;
    public TextView mTxtUserName, mTxtTimeAgo, mTxtAction;
    public AVLoadingIndicatorView indicatorView;
    View mView;


    public OrderListHolder(View itemView) {
        super(itemView);
        mView = itemView;
        recyclerView = (RecyclerView)itemView.findViewById(R.id.rv_orders);
        imageUser = (ImageView) itemView.findViewById(R.id.imageViewUser);
        mTxtTimeAgo = (TextView)itemView.findViewById(R.id.textViewTimeAgo);
        mTxtUserName = (TextView)itemView.findViewById(R.id.textViewName);
        //mTxtAction = (TextView)itemView.findViewById(R.id.textViewAction);
        //indicatorView =(AVLoadingIndicatorView)itemView.findViewById(R.id.pacmanindicator);
        //imageRemove = (ImageView)itemView.findViewById(R.id.imageViewRemove);
    }

}