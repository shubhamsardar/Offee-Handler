package com.shubham.tripin1.offeehandler;

import android.app.NotificationManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shubham.tripin1.offeehandler.Adapters.MiniOrderAdapter;
import com.shubham.tripin1.offeehandler.Managers.SharedPrefManager;
import com.shubham.tripin1.offeehandler.Model.CoffeeOrder;
import com.shubham.tripin1.offeehandler.Model.MyOrder;
import com.shubham.tripin1.offeehandler.Model.PostPaidPojo;
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
    private DatabaseReference ref, refCompletedOrders;
    private Context mContext;
    private RecyclerView mRecyclarOrders;
    private StorageReference storageReference;
    private SharedPrefManager mSharedPref;
    AVLoadingIndicatorView indicatorView;
    private boolean isOnScreen = false;
    DatabaseReference refHistory;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mSharedPref = new SharedPrefManager(mContext);


        if(mSharedPref.getUserHpass().isEmpty()){
            startActivity(new Intent(this,SplashActivity.class));
            finish();
        }else {
            FirebaseMessaging.getInstance().subscribeToTopic(mSharedPref.getUserHpass());
        }



        initView();
        storageReference = FirebaseStorage.getInstance().getReference();


        ref = FirebaseDatabase.getInstance().getReference()
                .child(mSharedPref.getUserHpass()).child("orders");
        refCompletedOrders = FirebaseDatabase.getInstance().getReference()
                .child(mSharedPref.getUserHpass()).child("corders");
        refHistory = FirebaseDatabase.getInstance().getReference().child("history");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0){
                    indicatorView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        firebaseRecyclerAdapterOrders = new FirebaseRecyclerAdapter<MyOrder, OrderListHolder>(MyOrder.class, R.layout.item_mainorder, OrderListHolder.class, ref) {

            @Override
            protected void populateViewHolder(final OrderListHolder viewHolder, final MyOrder model, final int position) {
                MiniOrderAdapter miniOrderAdapter = new MiniOrderAdapter(model.getmOrderList(), mContext);
                viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                viewHolder.recyclerView.setAdapter(miniOrderAdapter);
                viewHolder.mTxtUserName.setText(model.getmUserName());
                if(model.getmTimeAgo()!=null)
                viewHolder.mTxtTimeAgo.setText(gettimeDiff(model.getmTimeAgo())+", ₹"+model.getOrderCost());
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
                indicatorView.setVisibility(View.INVISIBLE);



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
                firebaseRecyclerAdapterOrders.getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final MyOrder myOrder = dataSnapshot.getValue(MyOrder.class);
                        refHistory.push().setValue(myOrder);
                        refCompletedOrders.child(myOrder.getmUserMobile()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                PostPaidPojo postPaidPojo = dataSnapshot.getValue(PostPaidPojo.class);
                                if(postPaidPojo != null){
                                    double d = postPaidPojo.getAmount();
                                    postPaidPojo.setAmount(d+myOrder.getOrderCost());
                                    refCompletedOrders.child(myOrder.getmUserMobile()).setValue(postPaidPojo);
                                }else {
                                    postPaidPojo = new PostPaidPojo(myOrder.getmUserName(),myOrder.getOrderCost(),myOrder.getmUserMobile());
                                    refCompletedOrders.child(myOrder.getmUserMobile()).setValue(postPaidPojo);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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
        isOnScreen = true;
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();



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
                finish();
                break;
            }
            case R.id.action_postpaid: {
                startActivity(new Intent(this, PostPaid.class));
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
