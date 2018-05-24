package com.shubham.tripin1.offeehandler;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shubham.tripin1.offeehandler.Managers.SharedPrefManager;
import com.shubham.tripin1.offeehandler.Model.CoffeeOrder;
import com.shubham.tripin1.offeehandler.Model.PostPaidPojo;
import com.shubham.tripin1.offeehandler.holders.ItemAddedHolder;

public class PostPaid extends AppCompatActivity {
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference ref;
    private FloatingActionButton floatingActionButtonAdd;
    private SharedPrefManager mSharedPref;
    private Context mContext;
    private RecyclerView mRecyclarItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        initView();
        getSupportActionBar().setTitle("Offee - PostPaid Manager");
        mContext = this;
        mSharedPref = new SharedPrefManager(mContext);
        floatingActionButtonAdd.setVisibility(View.INVISIBLE);

        ref = FirebaseDatabase.getInstance().getReference()
                .child(mSharedPref.getUserHpass()).child("corders");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PostPaidPojo, ItemAddedHolder>
                (PostPaidPojo.class,R.layout.item_input_item,ItemAddedHolder.class,ref) {

            @Override
            protected void populateViewHolder(ItemAddedHolder viewHolder, final PostPaidPojo model, final int position) {
                viewHolder.getmPrice().setText("₹"+model.getAmount());
                viewHolder.getmName().setText(model.getName());
                viewHolder.getmRemove().setText("CLEAR IT!");

                viewHolder.getmRemove().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        firebaseRecyclerAdapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                firebaseRecyclerAdapter.notifyDataSetChanged();
                                Toast.makeText(mContext,model.getName()+" is clear!",Toast.LENGTH_LONG);
                            }
                        });

                        return true;
                    }
                });

                viewHolder.getmRemove().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Long press required!",Toast.LENGTH_LONG).show();
                    }
                });
            }

        };

        mRecyclarItems.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclarItems.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();


    }



    private void initView() {
        floatingActionButtonAdd = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        mRecyclarItems = (RecyclerView)findViewById(R.id.rv_items);
    }
}