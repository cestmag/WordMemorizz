package com.websarva.wings.android.wordmemorizz;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

//使わんかも
public class MyWorker extends Worker {
    private Retrofit goo;
    private ApiService apiService;
    private User userCopy;

    private String userInfo = "";
    private String userId = "";
    private String userName = "";

    private boolean workendLocal = false;

    private boolean workendGlo = false;

    private int endcount = 0;
    private final int upperlimit = 5;
    private final int upperlimit2 = 5;

    private int endcountGlo = 0;

    private SharedPreferences sharedPreferences;

    private DatabaseHelper _helper;



    public MyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
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

    //Caused by: java.lang.ArrayIndexOutOfBoundsException: length=1; index=1
        //        at com.websarva.wings.android.wordmemorizz.MyWorker.<init>(MyWorker.java:70)


    }



    @NonNull
    @Override
    public Result doWork() {
        //behindwork true
         WordApp wordApp = WordApp.getInstance();

        //(WordApp)getApplicationContext().
       // (WordApp) getApplicationContext().getI;
        //wordApp.getI
       // wordApp.setBehindWorkOn(true);
        // This is where you define the background task
        // Do your data synchronization or other tasks here
        // Retrieve data from the server and local database
        //JSONArray toServer = _helper.getDataBetweenTimestamps("user-id","words");//serverに持っていくデータ words
        //toServerがn回連続空だったらworkmanager stop !!!

      //  SharedPreferences preferences = Context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        User postman = new User(this.userName,"pass");
        postman.setterUserId(this.userId);

        JSONArray toServer0 = _helper.getDataBetweenTimestamps(postman.getterID(),"wordlist");
        JSONArray toServer = _helper.getDataBetweenTimestamps(postman.getterID(), "words");

        Log.d("toServer0_len",String.valueOf(toServer0.length()));
        Log.d("toServer0_len2",String.valueOf(toServer.length()));

        JSONArray merged = mergeJsonArrays(toServer0,toServer);

        //User postman = new User("fakeuser","pass");
       /* if(merged.length() <= 0){
            this.endcount += 1;//こっちからおくるデータが0 もじかしてmyworkerよばれるたび新しいmyworker?
            Log.d("endcount",Integer.toString(this.endcount));
        }*/

        postman.setterSync(merged);
        Call<ApiResponse> call_2 = apiService.letsSync(postman);
//
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!なぜかここのなかのlog表記されないなぜいうえふいｂすいｖぶしｂｖｓｄヴぃｓｖｓｖ
        call_2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                //ここにとうたつしない!!!!!!!!!!!!!!!!!!!!!!!!!!!
             //   Log.d("back from server", response.toString()); // Log the entire response

                if(response.isSuccessful()) {

                    Log.d("final destination","yo");
                    ApiResponse apiResponse = response.body();
                    String shortMessage = apiResponse.getMessage();
                    Log.d("callback success", shortMessage);
                    //こっちから送ったnew dataのglobal idが入ったjsonarray
                    //JSONArray newIds = apiResponse.get_newIds();
                    //String newIds = apiResponse.get_newIds();
                    ArrayList<String> newIds = apiResponse.get_newIds();
                  //  Toast.makeText(getApplicationContext(),"yooo",Toast.LENGTH_LONG).show();
                  //  Log.d("white swan",newIds.toString());
                    if (newIds != null) {
                        addGlobalId(newIds, merged);
                        Log.d("no way","uyfufu");
                    } else {
                        // Handle the case where newIds is null
                        Log.d("no way","uyfufu2");
                    }

                    /*

                    String server2local1_str = apiResponse.get_server2local1();
                    //JSONObject server2local1;
                    /*try {//list add
                        JSONArray server2local1 = new JSONArray(server2local1_str);
                        insertIntoLocal(server2local1,1);
                        Log.d("notError","ok2");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("error12345","noooo");
                    }*/
                /*    try {
                        if (server2local1_str != null) {
                            JSONArray server2local1 = new JSONArray(server2local1_str);
                            insertIntoLocal(server2local1, 1);

                          //  Call<ApiResponse> call_3 = apiService.syncOK(postman);



                            Log.d("notError", "ok2");
                        } else {
                            Log.d("error12345", "server2local1_str is null");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("error12345", "Error parsing JSON");
                    }*/



                  //  String server2local2_str = apiResponse.get_server2local2();

                  /*  try {//word add
                        JSONArray server2local2 = new JSONArray(server2local2_str);
                        insertIntoLocal(server2local2,2);
                        Log.d("notError","ok");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("error12345","noooo2");
                    }*/

                  /*  try {
                        if (server2local2_str != null) {
                            JSONArray server2local2 = new JSONArray(server2local2_str);
                            insertIntoLocal(server2local2, 2);
                            Log.d("notError", "ok");
                        } else {
                            Log.d("error12345", "server2local2_str is null");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("error12345", "Error parsing JSON");
                    }  */

                    //updates
// Access the List of YourDataClass objects from server2local1
                    //List<Server2local1> server2local1Data = apiResponse.getServer2Local1Data();

// Now you can iterate through the list or perform other operations with the parsed data
                    /*for (Server2local1 data : server2local1Data) {
                        //System.out.println(data.toString());
                        Log.d("qwertyuiop",data.toString());
                    }*/

                    //順番ダイジ 最初にlist そしてwords
                 //   JSONArray server2local1 = apiResponse.get_server2local1();
                    //ArrayList<String> server2local1 = apiResponse.get_server2local1();
                    /*for (String item : server2local1) {
                        Log.d("Server2Local1", item);
                    }*/
                    //_helper.insertData("list",)

                //    JSONArray server2local2 = apiResponse.get_server2local2();
                    //ArrayList<String> server2local2 = apiResponse.get_server2local2();
                    //insert

/*
                    if(server2local1!= null && server2local2!=null) {

                        insertIntoLocal(server2local1,1);
                        insertIntoLocal(server2local2,2);

                        if (server2local1.length() <= 0 && server2local2.length() <= 0) {
                            MyWorker.this.endcountGlo += 1;//あっちからくえうでーたも0
                            Log.d("endcount2",Integer.toString(MyWorker.this.endcountGlo));
                            if (MyWorker.this.endcount >= MyWorker.this.upperlimit && MyWorker.this.endcountGlo >= MyWorker.this.upperlimit2) {
                                //end behind work end
                                ((WordApp) getApplicationContext()).urakataEnd(getApplicationContext());
                                MyWorker.this.endcount = 0;
                                MyWorker.this.endcountGlo = 0;
                            }
                        }

                    }*/
/*
                    if(server2local1!= null && server2local2!=null) {

                        insertIntoLocal(server2local1,1);
                        insertIntoLocal(server2local2,2);

                        if (server2local1.size() <= 0 && server2local2.size() <= 0) {
                            MyWorker.this.endcountGlo += 1;//あっちからくえうでーたも0
                            Log.d("endcount2",Integer.toString(MyWorker.this.endcountGlo));
                            if (MyWorker.this.endcount >= MyWorker.this.upperlimit && MyWorker.this.endcountGlo >= MyWorker.this.upperlimit2) {
                                //end behind work end
                                ((WordApp) getApplicationContext()).urakataEnd(getApplicationContext());
                                MyWorker.this.endcount = 0;
                                MyWorker.this.endcountGlo = 0;
                            }
                        }

                    }*/

                    //こっちから送ったデータに関してはsync_stats_1 を1にする
                    //newIds server2local
                    //and jsonarray with data stored in server db at first
                    //serverから北データに関してはsync_stats_2 を1にする
                    //そしてlocal dbにadd or update そのサイlocal_id敵なのがなかったら add otherwise update
                }else{

                    //sync_stats_2を0に戻す
                    statusBacktoZero(merged);
                    Log.d("something is wrong","i can feel it");
                    //toastとかでしらせる?

                }


            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("onfailure777","no");
                //ync_stats_1 を0にする
                Log.e("onfailure777", "Error: " + t.getMessage());
                statusBacktoZero(merged);
                //sync_stats_2を0に戻す

            }
        });

        // Return Result.SUCCESS if the task is successful, Result.FAILURE if it fails.

        //behindwork false
        wordApp.setBehindWorkOn(false);
        wordApp.urakataEnd(getApplicationContext());
        return Result.success();//Result.SUCCESS;
    }

    private boolean statusBacktoZero(JSONArray B){
        int length = B.length();

        for (int i = 0; i < length; i++) {
            try {

                JSONObject objectB = B.getJSONObject(i);

                long wordId = objectB.getLong("local_id");//listもwordも共通してgettimesytamp databasehelperにより
                //   String globalId = A.get(i);
                //   Log.d("maybe",globalId);
                String listname;
                if (objectB.length() == 14) {
                    listname = "words";
                } else if (objectB.length() == 10) {
                    listname = "wordlist";
                } else {
                    return false;
                    //sync stats 2を0にもどす from 2
                }


                //judge whether it's words or wordlist based on the number of variables in the jsonObject.
                //if it's ..., otherwise ...
                // String globalId = objectA.getString("");
                //sync stats 2を1にする必要も
                Object[] upday = {"sync_stats_2=0"};

                _helper.updateData(wordId, upday, listname);//global id をふか

                Log.d("sprinter", "not all right");

                // Modify objectB based on the values from objectA
                // For example, you can add a new key-value pair from A to B
                //objectB.put("newKey", objectA.getString("existingKey"));

                // Or you can merge the content of A and B in some way
                // For example, you might want to add all key-value pairs from A to B
                /*else{//global_idが

                }*/
            }catch (JSONException ee){
                return false;
            }
        }

        return true;
    }

    private boolean addGlobalId(ArrayList<String> A, JSONArray B){

        // Assuming jsonArrayA and jsonArrayB are your JSONArray objects

        try {
            int length = Math.min(A.size(), B.length());

            Log.d("lenOfTwo",String.valueOf(A.size())+"_"+String.valueOf(B.length()));

            for (int i = 0; i < length; i++) {

                String globalId = A.get(i);
                Log.d("maybe",globalId);

                JSONObject objectB = B.getJSONObject(i);

                long wordId = objectB.getLong("local_id");//global_word_id

                String listname = "";

                if(objectB.length() == 14){
                    listname = "words";
                    Log.d("wordsyaeh","1");
                }else if(objectB.length() == 10){
                    listname = "wordlist";
                    Log.d("wordsyaeh","2");
                }else{
                    Log.d("wordsyaeh","3");
                    //return false;
                    continue;
                    //sync stats 2を0にもどす from 2
                }
                if (!globalId.equals("")) {


                //judge whether it's words or wordlist based on the number of variables in the jsonObject.
                    //if it's ..., otherwise ...
                // String globalId = objectA.getString("");
                    //sync stats 2を1にする必要も
                Object[] upday = {"global_id=" + globalId, "sync_stats_2=1"};

                _helper.updateData(wordId, upday, listname);//global id をふか

                    Log.d("sprinter22",globalId+"_"+String.valueOf(wordId));

                // Modify objectB based on the values from objectA
                // For example, you can add a new key-value pair from A to B
                //objectB.put("newKey", objectA.getString("existingKey"));

                // Or you can merge the content of A and B in some way
                // For example, you might want to add all key-value pairs from A to B
            }else{//global_idが
                    //edit success

                    Object[] upday = {"sync_stats_2=1"};

                    _helper.updateData(wordId, upday, listname);//global id をふか

                }
            }
        } catch (JSONException e) {
            // Handle JSON parsing exceptions here
            int length = B.length();

            for (int i = 0; i < length; i++) {
                try {

                JSONObject objectB = B.getJSONObject(i);

                long wordId = objectB.getLong("local_id");//listもwordも共通してgettimesytamp databasehelperにより
                //   String globalId = A.get(i);
                //   Log.d("maybe",globalId);
                String listname;
                if (objectB.length() == 14) {
                    listname = "words";
                } else if (objectB.length() == 10) {
                    listname = "wordlist";
                } else {
                    return false;
                    //sync stats 2を0にもどす from 2
                }


                //judge whether it's words or wordlist based on the number of variables in the jsonObject.
                //if it's ..., otherwise ...
                // String globalId = objectA.getString("");
                //sync stats 2を1にする必要も
                Object[] upday = {"sync_stats_2=0"};

                _helper.updateData(wordId, upday, listname);//global id をふか

                Log.d("sprinter", "not all right");

                // Modify objectB based on the values from objectA
                // For example, you can add a new key-value pair from A to B
                //objectB.put("newKey", objectA.getString("existingKey"));

                // Or you can merge the content of A and B in some way
                // For example, you might want to add all key-value pairs from A to B
                /*else{//global_idが

                }*/
            }catch (JSONException ee){
                    return false;
                }
            }
            //return false;
        }

        //label 消去


        return true;
    }

    private boolean insertIntoLocal(JSONArray mrjson, int ver){
        try {

            ArrayList<String> successIds = new ArrayList<>();
            for (int i = 0; i < mrjson.length(); i++) {
                JSONObject jsonObject = mrjson.getJSONObject(i);

                //String list_local_id;
                String localId_str = jsonObject.getString("local_id");

                if(localId_str.isEmpty()){
                    localId_str = "0";
                }
                //String localId_str = jsonObject.getString("local_id");//
                Log.d("localId_str",localId_str);
                //localId_str = "0";

                if(ver == 2 ) {//word &&            local _id が存在しないなら

                    String value1 = jsonObject.getString("meaning"); // Replace "key1" with your actual key
                    String value2 = jsonObject.getString("sentence");
                    String value3 = jsonObject.getString("name");
                    String value4 = jsonObject.getString("listTitle");
                    String IdOflist = jsonObject.getString("parent_id");//globalなlist id


                    String glo_id_of_word = jsonObject.getString("word_global_id");
                    if(glo_id_of_word.isEmpty()){
                        glo_id_of_word = "";
                    }

                    if (localId_str.equals("0")) {//add
                    Log.d("intoLocal","here");
                    //local dbのlistからIdOflistをもとにlistのlocal idをget!
                    //long list_local_id = _helper.getlistWithGlobalIds(IdOflist);

                    long list_local_id;
                    if (!IdOflist.isEmpty()) {
                        list_local_id = _helper.getlistWithGlobalIds(IdOflist);
                    } else {
                        // Handle the case where IdOflist is empty, for example, set a default value or throw an error
                        Log.e("insertIntoLocal", "IdOflist is empty. Unable to parse as long.");
                        //return false; // Indicate failure
                        continue;
                    }
                        Log.d("intoLocal","here2");
                    //parent idで
                    // int value2 = jsonObject.getInt("key2"); // Replace "key2" with your actual key
                    //                //meaning  sentence                      name
                    Object[] eta = {value1, value2, list_local_id, value4, value3};

                    _helper.insertData("words",eta);
                    successIds.add(glo_id_of_word);
                        Log.d("intoLocal","here3");
                        //sync_stats_2 ->1 defaultで1になるから大丈夫
                    }else{//update
                        Object[] eta = {"meaning="+value1, "sentence="+value2, "listTitle="+value4, "name="+value3,"sync_stats_2=1"};
                     _helper.updateData(Long.parseLong(localId_str),eta,"words");
                     successIds.add(glo_id_of_word);
                     //sync_stats_2 ->1
                    }
                }else if(ver == 1){//list
                    //local idが0ならadd otherwise update
                    String value1 = jsonObject.getString("listName");
                    String value2 = jsonObject.getString("global_id");
                    if(localId_str.equals("0")) {//add

                        //  data.push({listName: sendData[0], global_id: sendData[1], local_id:sendData[2], testdays:sendData[3], torokuBi:sendData[4], wordsNum:sendData[5], userId:sendData[6]})
                        Object[] eta = {value1,value2};
                        _helper.insertData("wordlist", eta);//global idはどうするの??!!!!!!!!!!!!!
                        //sync stats-> defaultで1になるから大丈夫
                        successIds.add(value2);
                        Log.d("listUpdated","ok");
                    }else{//update

                        Object[] eta = {"listName="+value1,"sync_stats_2=1"};
                        _helper.updateData(Long.parseLong(localId_str),eta,"wordlist");
                        successIds.add(value2);
                        Log.d("listUpdated","ok2");
                        //sync stats
                    }
                    // _helper.insertData()
                }

                // Now you can work with the values you extracted from the JSON object
                // You can perform any operations you need inside this loop

                //sync stats 更新!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
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

    public static JSONArray mergeJsonArrays(JSONArray jsonArrayA, JSONArray jsonArrayB) {
        try {
            JSONArray combinedArray = new JSONArray();

            // Add elements from jsonArrayA
            for (int i = 0; i < jsonArrayA.length(); i++) {
                combinedArray.put(jsonArrayA.get(i));
            }

            // Add elements from jsonArrayB
            for (int i = 0; i < jsonArrayB.length(); i++) {
                combinedArray.put(jsonArrayB.get(i));
            }

            return combinedArray;
        } catch (JSONException e) {
            // Handle JSON parsing exceptions here
            e.printStackTrace(); // or log the exception
            return null; // or throw an exception or handle the error as appropriate
        }
    }
}
