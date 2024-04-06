package com.websarva.wings.android.wordmemorizz;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.UUID;

public class User {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("timestamp")
    private long latestTimestamp;

    @SerializedName("userId")
    private String userId = "fake-id";

    @SerializedName("sync_json")
    private JSONArray sync_info;

  //  private UUID workreq;

    //jsonarrayも持つことにする

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.latestTimestamp = 0;
    }

    public String getterName(){
        return this.username;
    }

    public String getterID(){return this.userId; }

    public void setterUserId(String id) {this.userId = id;}

    public void setterLatestTimestamp(long stamp) {this.latestTimestamp = stamp;}

    public void setterSync(JSONArray info) {this.sync_info = info;
                                             Log.d("prada","e");}

   // public void setterWorkreq(UUID infor){this.workreq = infor;}

    // Getters and setters (if needed)
}

