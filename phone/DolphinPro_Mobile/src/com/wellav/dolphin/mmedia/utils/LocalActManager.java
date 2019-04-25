package com.wellav.dolphin.mmedia.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.wellav.dolphin.application.DolphinApp;


public class LocalActManager {
    
    private String LOGTAG = "LocalActManager";
    private List<FragmentActivity> mList = new ArrayList<FragmentActivity>();
    
    public static LocalActManager getInstance() {
        return DolphinApp.getInstance().getLocalActManager();
    }
    public void addActivity(FragmentActivity activity) {
        mList.add(activity);
    }

    public void clearActivity(FragmentActivity act) {
        try {
            for (int i = 0; i < mList.size(); i++) {
                Activity activity = mList.get(i);
                if (activity != null && activity != act) {
                    activity.finish();
                }
            } 
        } catch (Exception e) {
   //         CommFunc.PrintLog(5, LOGTAG, "clearActivity:" + e.getMessage());
        }
    }

    public void removeActivity(Activity act) {
        try {
            for (int i = 0; i < mList.size(); i++) {
                Activity activity = mList.get(i);
                if (activity != null && activity == act) {
                    mList.remove(i);
                }
            }
        } catch (Exception e) {
          }
    }
    
    /**
     * finish������mList�е�Activity
     */
    public void finishActivity(){
    	for(FragmentActivity activity : mList) {  
            activity.finish();  
        }  
        mList.clear();
    }

}
