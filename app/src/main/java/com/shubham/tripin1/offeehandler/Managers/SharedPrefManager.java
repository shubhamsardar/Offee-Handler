package com.shubham.tripin1.offeehandler.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Tripin1 on 6/15/2017.
 */

public class SharedPrefManager {

    public static final String PREF_USER_PASS = "user_pass";
    public static final String PREF_USER_COMP = "user_comp";
    public static final String PREF_USER_ONSCREEN = "user_on_screen";




    private SharedPreferences mSharedPref;
    private Context mContext;
    private static SharedPreferences.Editor editor;


    public SharedPrefManager(Context context){
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mSharedPref.edit();
    }

    public void setUserHpass(String reginfo) {
        editor.putString(PREF_USER_PASS, reginfo);
        editor.commit();
    }
    public  String getUserHpass() {
        String reginfo = mSharedPref.getString(PREF_USER_PASS, "");
        return reginfo;
    }

    public void setUserCompany(String userCompany) {
        editor.putString(PREF_USER_COMP, userCompany);
        editor.commit();
    }

    public  void setPrefUserOnscreen(boolean b){
        editor.putBoolean(PREF_USER_ONSCREEN,b);
        editor.commit();
    }
    public  Boolean getPrefUserOnscreen(){
       return mSharedPref.getBoolean(PREF_USER_ONSCREEN,false);
    }

}
