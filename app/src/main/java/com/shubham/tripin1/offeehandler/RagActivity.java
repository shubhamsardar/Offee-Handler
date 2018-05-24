package com.shubham.tripin1.offeehandler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shubham.tripin1.offeehandler.Managers.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RagActivity extends AppCompatActivity {

    private static final String TAG = "RegActivity";

    private SharedPrefManager mSharedPrefManager;
    private Context mContext;
    private EditText mEditTextPass;
    private Button mSetButton;
    private TextView mTxtCurrentPass;
    private IntentIntegrator qrScan;
    private TextView mTxtScanQr;
    private JSONObject qrData;
    private ImageView imageView;
    public final static int QRcodeWidth = 500;
    private TextView mTextGeneratingQr;
    private Bitmap tosave;






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
            new GenerateQR().execute(generateJsonObject(mSharedPrefManager.getUserHpass()).toString());
            mTextGeneratingQr.setVisibility(View.VISIBLE);
        }
    }


    private void initViews() {
        mEditTextPass = (EditText)findViewById(R.id.editTextCrateHpass);
        mSetButton = (Button)findViewById(R.id.buttonSetHpass);
        mTxtCurrentPass = (TextView)findViewById(R.id.textViewCurrentPass);
        mTxtScanQr = (TextView)findViewById(R.id.textViewScanQr);
        imageView = (ImageView) findViewById(R.id.imageView);
        mTextGeneratingQr =(TextView) findViewById(R.id.textViewGenerating);


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
                    startActivity(new Intent(RagActivity.this,MainActivity.class));
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RagActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("QR");

                // Setting Dialog Message
                alertDialog.setMessage("Save QR image file?");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.mipmap.ic_launcher_hot);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        if(tosave!=null){
                            SaveImage(tosave);
                            // Write your code here to invoke YES event
                            Toast.makeText(getApplicationContext(), "Saving", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Not Available", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
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
                    if(obj.has("pass")){
                        mSharedPrefManager.setUserHpass(obj.getString("pass"));
                        Toast.makeText(getApplicationContext(),"Scan Success, Password Set!",Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        if(!mSharedPrefManager.getUserHpass().isEmpty()){
            startActivity(new Intent(RagActivity.this,MainActivity.class));
            finish();
        }

    }

    private JSONObject generateJsonObject(String pass) {
        qrData = new JSONObject();
        try {
            qrData.put("pass", pass);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return qrData;
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark) : ContextCompat.getColor(getApplicationContext(), R.color.colorAccentGray);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class GenerateQR extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap b = null;
            try {
                b = TextToImageEncode(qrData.toString());
            } catch (WriterException e) {
                e.printStackTrace();
            }

            return b;
        }

        @Override
        protected void onPostExecute(Bitmap b) {

            if (b != null) {
                tosave = b;
                imageView.setImageBitmap(b);
                mTextGeneratingQr.setVisibility(View.INVISIBLE);
            } else {
                mTextGeneratingQr.setText("some error! try again");

            }
            // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            mTextGeneratingQr.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String fname = "Image-"+ mSharedPrefManager.getUserHpass() +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="QR_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}
