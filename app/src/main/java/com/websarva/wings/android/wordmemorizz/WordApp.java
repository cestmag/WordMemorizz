package com.websarva.wings.android.wordmemorizz;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WordApp extends Application {

    private static WordApp instance;

    private boolean isLoggedIn = false;

    private boolean isGenerated = false;

    private boolean behindWorkOn = false;//実際にbehindwork行われてるか

    private boolean synOn = false;//userがbehindwork設定してるか

    private static final String PREF_NAME = "app_prefs";

    public User user;

    private UUID urakataId;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static WordApp getInstance() {
        return instance;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isGenerated() {return isGenerated; }

    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    public void setLoggedOut(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    public boolean getBehindWork() {
        return this.behindWorkOn;
    }

    public void setBehindWorkOn(boolean inf) {this.behindWorkOn = inf; }

    public void setUrakataId(UUID inf){ this.urakataId = inf; }

    public void setSync(boolean inf){ this.synOn = inf; }

    public boolean getSync() { return this.synOn; }

    public void userGenerate(String username,String password){
        user = new User(username,password);
        this.isGenerated = true;
    }

    public void installUserData(Context context){
        if(this.user!=null){
            String deliverinfor = this.user.getterID() + "=" + this.user.getterName();

            Data inputData = new Data.Builder()
                    .putString("argument_key", deliverinfor)
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker2.class)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(context).enqueue(workRequest);
        }
    }

    public void urakataStart(Context context){

        if(this.user != null && !this.behindWorkOn){
            this.setBehindWorkOn(true);//preferenceに保存した方が良い
            String deliverinfor = this.user.getterID() + "=" + this.user.getterName();

            Data inputData = new Data.Builder()
                    .putString("argument_key", deliverinfor)
                    .build();

           OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    .setInputData(inputData)
                    .build();

         /*   PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                    MyWorker.class, // Your worker class
                    15, // Repeat interval in minutes
                    TimeUnit.MINUTES
            ).setInputData(inputData).build();*/


            WorkManager.getInstance(context).enqueue(workRequest);

           // this.setBehindWorkOn(true);//preferenceに保存した方が良い
         //   this.setSync(true);

            this.setUrakataId(workRequest.getId());
           // this.urakataId = workRequest.getId();
            Log.d("workReq",this.urakataId.toString());
            // Store the UUID in SharedPreferences (or any other storage mechanism)
            SharedPreferences preferences = context.getSharedPreferences(this.PREF_NAME, Context.MODE_PRIVATE);
            preferences.edit().putString("workRequestId", this.urakataId.toString()).apply();


            // Observe the work status
           /* WorkManager.getInstance(context)
                    .getWorkInfoByIdLiveData(workRequest.getId())
                    .observe((LifecycleOwner) context, workInfo -> {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            // Work has been completed
                            // You can perform any additional actions here
                            Log.d("WorkStatus", "Work completed");

                            urakataEnd(context);
                        }else{
                            //fail
                            urakataEnd(context);
                        }
                    });*/
        }


       // return workRequestId;
    }

    public void urakataEnd(Context context){
        WorkManager.getInstance(context).cancelWorkById(this.urakataId);//or sharedpreferenceからとる

        SharedPreferences preferences = context.getSharedPreferences(this.PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove("workRequestId").apply();

        this.setBehindWorkOn(false);
      //  this.setSync(false);
    }

    public boolean doesLabelExist(Context context) {//behindworkやってるか
        SharedPreferences preferences = context.getSharedPreferences(this.PREF_NAME, Context.MODE_PRIVATE);
        return preferences.contains("workRequestId");
    }


}
