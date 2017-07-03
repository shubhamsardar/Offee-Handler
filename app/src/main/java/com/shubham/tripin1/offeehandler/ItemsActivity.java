package com.shubham.tripin1.offeehandler;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shubham.tripin1.offeehandler.Managers.SharedPrefManager;
import com.shubham.tripin1.offeehandler.Model.CoffeeOrder;
import com.shubham.tripin1.offeehandler.holders.ItemAddedHolder;

public class ItemsActivity extends AppCompatActivity {

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
        setListners();
        getSupportActionBar().setTitle("Offee - Stock Manager");
        mContext = this;
        mSharedPref = new SharedPrefManager(mContext);

        ref = FirebaseDatabase.getInstance().getReference()
                .child(mSharedPref.getUserHpass()).child("available_coffee");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CoffeeOrder, ItemAddedHolder>
                (CoffeeOrder.class,R.layout.item_input_item,ItemAddedHolder.class,ref) {

            @Override
            protected void populateViewHolder(ItemAddedHolder viewHolder, CoffeeOrder model, final int position) {

                viewHolder.getmName().setText(model.getmCoffeeName());
                viewHolder.getmRemove().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseRecyclerAdapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                firebaseRecyclerAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                Log.i("populate view holder,","Firebase adapter items"+position);

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

    private void setListners() {
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });
    }

    public void showAddItemDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_additem, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.editTextItemName);

        dialogBuilder.setTitle("Add new Item!");
        dialogBuilder.setMessage("Enter whats available, anything new!");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ref.push().setValue(new CoffeeOrder(edt.getText().toString().trim(),"0","UnOrdered"));
                firebaseRecyclerAdapter.notifyDataSetChanged();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}
