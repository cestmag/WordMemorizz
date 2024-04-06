package com.websarva.wings.android.wordmemorizz;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;


public class ApiResponse {
    @SerializedName("message")
    private String message;//="Login successful";//状況によってmessage変える!!!!!!!!!!!!!!!!

    @SerializedName("userId")
    private String userId;

    @SerializedName("word_global_id")
    private String word_global_id;

    @SerializedName("list_global_id")
    private String list_global_id;

    @SerializedName("token")
    private String token;

    @SerializedName("newIds")
    //private String newIds;
    private ArrayList<String> newIds;
    //private JSONArray newIds;

    @SerializedName("server2local1")
    private String server2local1;
    //private ArrayList<String> server2local1;
    //private JSONArray server2local1;

    @SerializedName("server2local2")
    private String server2local2;
    //private ArrayList<String> server2local2;
    //private JSONArray server2local2;

    public String getMessage() {
        return message;
    }

    public String getUserId()  { return userId; }

    public String get_word_global_id()  { return word_global_id; }

    public String get_list_global_id()  { return list_global_id; }

    public String get_token()  { return token; }

    //public JSONArray get_newIds() {return newIds; }
    //public String get_newIds() {return newIds; }
    public ArrayList<String> get_newIds() {return newIds; }
    //public JSONArray get_server2local1() {return server2local1; }
    //public ArrayList<String> get_server2local1() {return server2local1; }
    public String get_server2local1() {return server2local1; }

    public String get_server2local2(){return server2local2; }
    //public JSONArray get_server2local2() {return server2local2; }
    //public ArrayList<String> get_server2local2() {return server2local2; }
    public List<Server2local1> getServer2Local1Data() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Server2local1>>() {}.getType();
        return gson.fromJson(server2local1.toString(), listType);
    }

}

