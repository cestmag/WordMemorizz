package com.websarva.wings.android.wordmemorizz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyWorker2 extends Worker {

    private static final String TAG = "MyWorker";
    private ApiService apiService;
    private Retrofit goo;
    private DatabaseHelper _helper;
    private SharedPreferences sharedPreferences;
    private String userInfo = "";
    private String userId = "";
    private String userName = "";

    private ProgressBar progressBar;

    public MyWorker2(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.goo = RetrofitClient.getClient();
        this.apiService = goo.create(ApiService.class);
        this._helper = new DatabaseHelper(context);

        this.sharedPreferences = context.getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
      /*  String json = getInputData().getString("my_data_json");
        Gson gson = new Gson();
        User myData = gson.fromJson(json, User.class);
        userCopy = myData;*/
        //  this.userInfo = argument;//username and Id
        this.userInfo = getInputData().getString("argument_key");
        String[] parts = this.userInfo.split("=");
        this.userId = parts[0];
        this.userName = parts[1];
        Log.d("spain",parts[0]);
        Log.d("chicago",parts[1]);
    }

    @NonNull
    @Override
    public Result doWork() {
        //progressBar = findViewById(R.id.progressbar);
        // Set initial progress to 0
      //  setProgress(new Data.Builder().putInt("progress", 0).build());

        // Find the ProgressBar by its ID
        //ProgressBar progressBar = ((MainActivity2) getApplicationContext()).findViewById(R.id.progressBar100);
        // Find the ProgressBar by its ID
       // ProgressBar progressBar = new ProgressBar(getApplicationContext());

        //User postman = new User(this.userName,"pass");
        //postman.setterUserId(this.userId);
        //String username = getInputData().getString("username");
        //String password = getInputData().getString("password");
        // Make the ProgressBar visible
       // Handler mainHandler = new Handler(Looper.getMainLooper());
      //  progressBar.setVisibility(View.VISIBLE);
        //mainHandler.post(() -> progressBar.setProgress(10));
        getUserAlldata(this.userName, "pass");

        // Update progress on the main (UI) thread


      //  mainHandler.post(() -> progressBar.setProgress(100)); // Update with the desired progress

        // Update progress after completing a part of the task
      //  setProgress(new Data.Builder().putInt("progress", 100).build());

        // Hide the ProgressBar when the work is complete
        //progressBar.setVisibility(View.INVISIBLE);

        // Hide the ProgressBar when the work is complete
      //  mainHandler.post(() -> progressBar.setVisibility(View.INVISIBLE));
        return Result.success();
    }

    private void getUserAlldata(String username, String password) {
        // Your existing method logic goes here

        //Retrofit retrofit = RetrofitClient.getClient();

        //ApiService apiService = retrofit.create(ApiService.class);

        User postman = new User(username, password);

        if (((WordApp) getApplicationContext()).user != null) {
            String leID = ((WordApp) getApplicationContext()).user.getterID();
            postman.setterUserId(leID);
        }

        Call<ApiResponse> call = apiService.syncOK(postman);

        Log.d(TAG, "1");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "2");
                if (response.isSuccessful()) {
                    Log.d(TAG, "3");
                    ApiResponse apiResponse = response.body();
                   // String message = apiResponse.getMessage();

                    String server2local1_str = apiResponse.get_server2local1();

                    try {// list add
                        JSONArray server2local1 = new JSONArray(server2local1_str);
                        insertIntoLocal(server2local1, 1);
                        Log.d(TAG, "4");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "5");
                    }

                    String server2local2_str = apiResponse.get_server2local2();

                    try {// word add
                        JSONArray server2local2 = new JSONArray(server2local2_str);
                        insertIntoLocal(server2local2, 2);
                        Log.d(TAG, "6");


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "7");
                    }

                } else {
                    Log.d(TAG, "8");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "9");
                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean insertIntoLocal(JSONArray mrjson, int ver){
        try {

            ArrayList<String> successIds = new ArrayList<>();
            for (int i = 0; i < mrjson.length(); i++) {
                JSONObject jsonObject = mrjson.getJSONObject(i);

                //String list_local_id;
              //  String localId_str = jsonObject.getString("local_id");

                //    if(localId_str.isEmpty()){
                //        localId_str = "0";
                //    }
                //String localId_str = jsonObject.getString("local_id");//
                //     Log.d("localId_str",localId_str);
                //localId_str = "0";

                if(ver == 2 ) {//word &&            local _id が存在しないなら

                    String value1 = jsonObject.getString("meaning"); // Replace "key1" with your actual key
                    String value2 = jsonObject.getString("sentence");
                    String value3 = jsonObject.getString("name");
                    String value4 = jsonObject.getString("listTitle");
                    String IdOflist_str = jsonObject.getString("parent_id");//localなlist id

                    Log.d("IdOflist_str",IdOflist_str);

                    String correctRate_str = jsonObject.getString("correctRate");
                    String entire_str = jsonObject.getString("entire");

                    String results5 = jsonObject.getString("result");


                    long IdOflist;

                    try {
                        IdOflist = Long.valueOf(IdOflist_str);
                        // Use the 'IdOflist' variable here if the conversion is successful

                    } catch (NumberFormatException e) {
                        // Handle the case where the conversion fails
                        IdOflist = -1;
                        e.printStackTrace(); // This line is optional and can be removed or modified
                    }

                    int correctRate = Integer.valueOf(correctRate_str);
                    int entire = Integer.valueOf(entire_str);

                    String glo_id_of_word = jsonObject.getString("word_global_id");

                    long glo_id_of_word_long = Long.valueOf(glo_id_of_word);
                    if(glo_id_of_word.isEmpty()){
                        glo_id_of_word = "";
                    }

                    //    if (localId_str.equals("0")) {//add
                    Log.d("intoLocal","here");
                    //local dbのlistからIdOflistをもとにlistのlocal idをget!
                    //long list_local_id = _helper.getlistWithGlobalIds(IdOflist);

                    /*    long list_local_id;
                        if (!IdOflist.isEmpty()) {
                            list_local_id = _helper.getlistWithGlobalIds(IdOflist);
                        } else {
                            // Handle the case where IdOflist is empty, for example, set a default value or throw an error
                            Log.e("insertIntoLocal", "IdOflist is empty. Unable to parse as long.");
                            //return false; // Indicate failure
                            continue;
                        }*/
                    Log.d("intoLocal","here2");
                    //parent idで
                    // int value2 = jsonObject.getInt("key2"); // Replace "key2" with your actual key
                    //                //meaning  sentence                      name//5      6      7
                    Object[] eta = {value1, value2, IdOflist, value4, value3, correctRate,entire,results5,glo_id_of_word_long};

                    _helper.insertData2("words",eta);
                    //  successIds.add(glo_id_of_word);
                    Log.d("intoLocal","here3");
                    //sync_stats_2 ->1 defaultで1になるから大丈夫
                    //  }else{//update
                    //  Object[] eta = {"meaning="+value1, "sentence="+value2, "listTitle="+value4, "name="+value3,"sync_stats_2=1"};
                    //  _helper.updateData(Long.parseLong(localId_str),eta,"words");
                    //   successIds.add(glo_id_of_word);
                    //sync_stats_2 ->1
                    //   }
                }else if(ver == 1){//list
                    //local idが0ならadd otherwise update
                    String value1 = jsonObject.getString("listName");
                    //  String value2 = jsonObject.getString("global_id");
                    String value2 = jsonObject.getString("local_id");
                    //  if(localId_str.equals("0")) {//add
                    //  String value3 = jsonObject.getString("testdays");


                    long lisId;

                    try {
                        lisId = Long.valueOf(value2);
                        // Use the 'IdOflist' variable here if the conversion is successful

                    } catch (NumberFormatException e) {
                        // Handle the case where the conversion fails
                        lisId = -1;
                        e.printStackTrace(); // This line is optional and can be removed or modified
                    }
                    //  data.push({listName: sendData[0], global_id: sendData[1], local_id:sendData[2], testdays:sendData[3], torokuBi:sendData[4], wordsNum:sendData[5], userId:sendData[6]})
                    Object[] eta = {value1, lisId};
                    _helper.insertData2("wordlist", eta);//global idはどうするの??!!!!!!!!!!!!!
                    //sync stats-> defaultで1になるから大丈夫
                    //  successIds.add(value2);
                    Log.d("listUpdated","ok");
                    //  }else{//update

                    //    Object[] eta = {"listName="+value1,"sync_stats_2=1"};
                    //    _helper.updateData(Long.parseLong(localId_str),eta,"wordlist");
                    //   successIds.add(value2);
                    //    Log.d("listUpdated","ok2");
                    //sync stats
                }
                // _helper.insertData()
            }

            // Now you can work with the values you extracted from the JSON object
            // You can perform any operations you need inside this loop

            //sync stats 更新!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        } catch (JSONException e) {
            //Log.d("tutdut","noooooah");

            String errorMessage = e.getMessage();
            //System.out.println("JSONException: " + errorMessage);
            Log.d("insertLocal_error",errorMessage) ;
            // You can also print the entire stack trace if needed
            e.printStackTrace();
            // Handle JSON parsing exceptions here
            return  false;
        }
        return true;
    }


}

