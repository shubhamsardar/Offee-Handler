package com.shubham.tripin1.offeehandler;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shubham.tripin1.offeehandler.Adapters.MiniOrderAdapter;
import com.shubham.tripin1.offeehandler.Managers.SharedPrefManager;
import com.shubham.tripin1.offeehandler.Model.CoffeeOrder;
import com.shubham.tripin1.offeehandler.Model.MyOrder;
import com.shubham.tripin1.offeehandler.Utils.CircleTransform;
import com.shubham.tripin1.offeehandler.holders.OrderListHolder;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter firebaseRecyclerAdapterOrders;
    private DatabaseReference ref;
    private Context mContext;
    private RecyclerView mRecyclarOrders;
    private StorageReference storageReference;
    private SharedPrefManager mSharedPref;
    AVLoadingIndicatorView indicatorView;
    private boolean isOnScreen = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mSharedPref = new SharedPrefManager(mContext);
        isOnScreen = true;


        if (mSharedPref.getUserHpass().isEmpty()) {
            startActivity(new Intent(mContext, RagActivity.class));
        }

        initView();
        storageReference = FirebaseStorage.getInstance().getReference();


        ref = FirebaseDatabase.getInstance().getReference()
                .child(mSharedPref.getUserHpass()).child("orders");


        firebaseRecyclerAdapterOrders = new FirebaseRecyclerAdapter<MyOrder, OrderListHolder>(MyOrder.class, R.layout.item_mainorder, OrderListHolder.class, ref) {

            @Override
            protected void populateViewHolder(final OrderListHolder viewHolder, final MyOrder model, final int position) {
                MiniOrderAdapter miniOrderAdapter = new MiniOrderAdapter(model.getmOrderList(), mContext);
                viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                viewHolder.recyclerView.setAdapter(miniOrderAdapter);
                viewHolder.mTxtUserName.setText(model.getmUserName());
                viewHolder.mTxtTimeAgo.setText(gettimeDiff(model.getmTimeAgo()));
                miniOrderAdapter.notifyDataSetChanged();
                MyOrder myOrder = model;

                Glide.with(getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(storageReference.child("userImages/" + model.getmUserMobile() + ".jpg"))
                        .centerCrop().crossFade().bitmapTransform(new CircleTransform(mContext))
                        .placeholder(R.drawable.profile)
                        .into(viewHolder.imageUser);

                for (CoffeeOrder o : myOrder.getmOrderList()) {
                    o.setmOrderStatus("Seen");
                }

                firebaseRecyclerAdapterOrders.getRef(position).setValue(myOrder);


            }
        };

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                firebaseRecyclerAdapterOrders.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(mContext,"Order Completed!",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return super.isItemViewSwipeEnabled();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclarOrders); //set swipe to recylcerview

        firebaseRecyclerAdapterOrders.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                indicatorView.setVisibility(View.INVISIBLE);

                if(!isOnScreen){
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                    finish();
                }

                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    if(!r.isPlaying())
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                mRecyclarOrders.smoothScrollToPosition(mRecyclarOrders.getAdapter().getItemCount());

            }

        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclarOrders.setLayoutManager(linearLayoutManager);
        mRecyclarOrders.setAdapter(firebaseRecyclerAdapterOrders);
        firebaseRecyclerAdapterOrders.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnScreen = false;
    }

    private void initView() {
        mRecyclarOrders = (RecyclerView) findViewById(R.id.rv_main_orders);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicatorView2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.yoursettins, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_items: {
                startActivity(new Intent(this, ItemsActivity.class));
                break;
            }
            case R.id.action_settings: {
                startActivity(new Intent(this, RagActivity.class));
                break;
            }
            case R.id.action_gotit: {
                break;
            }
        }
        return true;
    }

    public String gettimeDiff(String time) {

        String diff = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate;
        try {
            startDate = df.parse(time);
            if (startDate != null) {
                Date endDate = new Date();
                long duration = endDate.getTime() - startDate.getTime();
                long diffInSeconds = Math.abs(TimeUnit.MILLISECONDS.toSeconds(duration));
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
                if (diffInSeconds == 0) {
                    return "Realtime!";
                }
                if (diffInSeconds < 60) {
                    diff = "" + diffInSeconds + " sec ago";
                } else if (diffInMinutes < 60) {
                    diff = "" + diffInMinutes + " min ago";
                } else if (diffInHours < 24) {
                    diff = "" + diffInHours + " hrs ago";
                } else {
                    long daysago = duration / (1000 * 60 * 60 * 24);
                    diff = "" + daysago + " days ago";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;

    }


}
