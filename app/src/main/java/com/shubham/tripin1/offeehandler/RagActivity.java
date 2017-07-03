package com.shubham.tripin1.offeehandler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shubham.tripin1.offeehandler.Managers.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class RagActivity extends AppCompatActivity {

    private SharedPrefManager mSharedPrefManager;
    private Context mContext;
    private EditText mEditTextPass;
    private Button mSetButton;
    private TextView mTxtCurrentPass;
    private IntentIntegrator qrScan;
    private TextView mTxtScanQr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rag);
        getSupportActionBar().setTitle("Set Password");
        mContext = this;
        mSharedPrefManager = new SharedPrefManager(mContext);
        initViews();
        setListners();

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        if(!mSharedPrefManager.getUserHpass().isEmpty()){
            mTxtCurrentPass.setText("your current password is: '"+mSharedPrefManager.getUserHpass()+"'");
        }
    }


    private void initViews() {
        mEditTextPass = (EditText)findViewById(R.id.editTextCrateHpass);
        mSetButton = (Button)findViewById(R.id.buttonSetHpass);
        mTxtCurrentPass = (TextView)findViewById(R.id.textViewCurrentPass);
        mTxtScanQr = (TextView)findViewById(R.id.textViewScanQr);
    }

    private void setListners() {
        mSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditTextPass.getText().toString().isEmpty()){
                    Toast.makeText(mContext,"Bad Password!",Toast.LENGTH_LONG).show();
                }else {
                    mSharedPrefManager.setUserHpass(mEditTextPass.getText().toString());
                    Toast.makeText(mContext,"Password Set!",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        mTxtScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    if(obj.has("pass")&&obj.has("comp")){
                        mSharedPrefManager.setUserHpass(obj.getString("pass"));
                        mSharedPrefManager.setUserCompany(obj.getString("comp"));
                        Toast.makeText(getApplicationContext(),"Scan Success",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(mContext,MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"Something wrong!",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
