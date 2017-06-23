package com.shubham.tripin1.offeehandler.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.shubham.tripin1.offeehandler.Model.CoffeeOrder;
import com.shubham.tripin1.offeehandler.R;
import com.shubham.tripin1.offeehandler.holders.OrderHolder;

import java.util.List;

/**
 * Created by Tripin1 on 6/21/2017.
 */

public class MiniOrderAdapter extends RecyclerView.Adapter<OrderHolder> {

    private List<CoffeeOrder> orders;
    private Context mContext;
    private int lastPosition = -1;


    public MiniOrderAdapter(List<CoffeeOrder> orders, Context context){
        this.orders = orders;
        this.mContext = context;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coffee_order, parent, false);
        return new OrderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        CoffeeOrder order = orders.get(position);
        holder.getmNumberField().setText(order.getmCoffeeNumber());
        holder.getmNameField().setText(order.getmCoffeeName());
        holder.getmStatusField().setText(order.getmOrderStatus());
        // Here you apply the animation when the view is bound
        //setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
