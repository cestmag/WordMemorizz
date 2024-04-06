package com.websarva.wings.android.wordmemorizz;
//login
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//71から74 buttonお専用にしてる
public class MainActivity2 extends AppCompatActivity {

    private DatabaseHelper _helper;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private Button logoutButton;
    private FloatingActionButton floatingBack;

    private Switch mySwitch;

    private final String PREF_NAME = "app_prefs";

    private ApiService apiService;

    //private boolean loginState=false;
    //private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize Retrofit and ApiService
        Retrofit retrofit = RetrofitClient.getClient();

        apiService = retrofit.create(ApiService.class);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        logoutButton = findViewById(R.id.logoutButton);

        floatingBack = findViewById(R.id.floatingActionButton15);

        HelloListener listener = new HelloListener();

        loginButton.setOnClickListener(listener);
        registerButton.setOnClickListener(listener);
        logoutButton.setOnClickListener(listener);
        floatingBack.setOnClickListener(listener);
        mySwitch = findViewById(R.id.switch3);

        _helper = new DatabaseHelper(MainActivity2.this);

        updateStatusText();

        //ネットとのsyncをいろいろtryしたがむりだったのでいまのとこは
      /*  mySwitch.setEnabled(false);
        logoutButton.setEnabled(false);
        registerButton.setEnabled(false);
        loginButton.setEnabled(false);*/


        //mySwitch = findViewById(R.id.switch3);

       if(((WordApp)getApplication()).isLoggedIn() && mySwitch != null ){
        //ここを少し変える
            if(((WordApp)getApplication()).getSync()){
                mySwitch.setChecked(true);
            }else{
                mySwitch.setChecked(false);
            }

        }

        if(!((WordApp)getApplication()).isLoggedIn() && mySwitch != null){
            mySwitch.setEnabled(false);
        }
//サーバーとのsyncしているか否か
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // isChecked will be true if the switch is in the "on" position
                // and false if it's in the "off" position

                if (isChecked) {
                    if(/*(!((WordApp) getApplication()).getBehindWork()) &&*/ ((WordApp) getApplication()).isLoggedIn()) {
                        //((WordApp) getApplication()).urakataStart(getApplication());
                        //show message
                       // ((WordApp) getApplication()).setSync(true);
                        ((WordApp) getApplication()).urakataStart(getApplication());
                        ((WordApp) getApplication()).setSync(true);
                        updateToken(null,null,null,null,true);
                        //!!!!!!!!!!!!!!!!!!!!preferenceのsyncも変更
                        Toast.makeText(MainActivity2.this, "Sync started !", Toast.LENGTH_LONG).show();
                    }
                } else {

                    if(/*((WordApp) getApplication()).getBehindWork() &&*/ ((WordApp) getApplication()).isLoggedIn()){
                       // ((WordApp) getApplication()).urakataEnd(getApplication());
                        ((WordApp) getApplication()).setSync(false);
                        updateToken(null,null,null,null,false);
                        //show message
                      //  ((WordApp) getApplication()).setSync(false);
                        //!!!!!!!!!!!!!!!!!!!!preferenceのsyncも変更
                        Toast.makeText(MainActivity2.this, "Sync suspended !", Toast.LENGTH_LONG).show();
                    }
                    // Handle switch OFF state
                    // For example, disable a feature
                }
            }
        });

    }

    private void cancelWorkBehind(UUID inf){

        WorkManager.getInstance(this).cancelWorkById(inf);
    }

    private class HelloListener implements View.OnClickListener{
        @Override
        public void onClick(View view){

            int id = view.getId();
//ここでローカルのデータとクラウドデータの引っ越しする
            switch (id){

                case R.id.loginButton:

                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    //confirm
                    // Send login request
                    if(!((WordApp) getApplication()).isLoggedIn()){
                        //confirm
                        loginUser(username, password);



                        //data get


                    }else{
                        //
                        Toast.makeText(MainActivity2.this, "You already logged in", Toast.LENGTH_LONG).show();
                    }


                    break;
                case R.id.registerButton:

                    String Newusername = usernameEditText.getText().toString();
                    String Newpassword = passwordEditText.getText().toString();

                    if(!((WordApp) getApplication()).isLoggedIn()){
                        //confirm
                        registerUser(Newusername,Newpassword);

                    }else{
                        //
                        Toast.makeText(MainActivity2.this, "Please log out first before registering", Toast.LENGTH_LONG).show();
                    }



                    break;
                case R.id.logoutButton://local dataけす どうきされてなかったら警告

                    if(((WordApp) getApplication()).isLoggedIn()) {

                        boolean okay = _helper.deleteAllDataWithWarning(0,"sync_stats_2");
                     //   boolean okay2 = _helper.deleteAllDataWithWarning(0,"wordlist","sync_stats_2");
                        boolean doubleokay = _helper.deleteAllDataWithWarning(2,"sync_stats_2");

                        if(okay&&doubleokay){

                           // boolean doubleokay = _helper.deleteAllDataWithWarning(2,"sync_stats_2");


                            ((WordApp) getApplication()).setLoggedIn(false);
                            Toast.makeText(MainActivity2.this, "You logged out", Toast.LENGTH_LONG).show();
                            // Log.d("logout 1234", "wer");
                            String[] userdataa = {"","",""};
                            //storeToken(userdataa,false,false);

                            //  cancelWorkBehind("");
                            if(((WordApp)getApplication()).getBehindWork()){//behindwork stop
                                ((WordApp)getApplication()).urakataEnd(getApplication());
                            }

                            storeToken(userdataa,false,false);
                            mySwitch.setEnabled(false);

                        }else{

                            // 警告メッセージを出力
                            //Log.warning("特定のコラムが特定の値である行があります. Please re sync");

                            // ここでユーザーに対して警告を表示するか、適切な処理を行う
                            OrderConfirmDialogFragment3 warningDialog = new OrderConfirmDialogFragment3();
                            warningDialog.show(getSupportFragmentManager(),"WarningDialog");
                            // Pass the warning message to the dialog using arguments

                            // Show the dialog using the context passed to the method
                           // warningDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "WarningDialog");

                        }


                     //  WorkManager.getInstance(this).cancelWorkById();

                        //dataの入れ替え
                    }else{
                        Toast.makeText(MainActivity2.this, "You haven't even logged in lol", Toast.LENGTH_LONG).show();
                    }
                    updateStatusText();

                    break;

                case R.id.floatingActionButton15:
                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    break;
            }


        }


    }
    private void updateStatusText() {
        TextView statusTextView = findViewById(R.id.statusTextView);
        if (((WordApp) getApplication()).isLoggedIn()) {
            statusTextView.setText("Status: logged in with " + ((WordApp) getApplication()).user.getterName());
        } else {
            statusTextView.setText("Status: not logged in");
        }

        //syncしてるかいなか
        if(((WordApp)getApplication()).isLoggedIn() && mySwitch != null ){
            //ここを少し変える
            if(((WordApp)getApplication()).getBehindWork()){
                mySwitch.setChecked(true);
            }else{
                mySwitch.setChecked(false);
            }

        }

        if(!((WordApp)getApplication()).isLoggedIn() && mySwitch != null){
            mySwitch.setEnabled(false);
        }


    }

    private void registerUser(String username, String password/*, String secretKey*/){
        User user = new User(username, password);
        Call<ApiResponse> call = apiService.register(user);
        //Log.d("registereddd","yeah");
        call.enqueue(new Callback<ApiResponse>(){
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response){
               if(response.isSuccessful()){

                   ApiResponse apiResponse = response.body();
                   String message = apiResponse.getMessage();
                   Log.d("registereddd",message);

                   if ("Register successful".equals(message)) {
                       // Store the token securely (SharedPreferences)
                       //String token = response.headers().get("Authorization");
                      // storeToken(token);
                      // ((WordApp) getApplication()).setLoggedIn(true);
                       //loginState=true;
                       Toast.makeText(MainActivity2.this, "Register success, mate!! Please log in now!!", Toast.LENGTH_LONG).show();

                       // Navigate to the main app screen
                       //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                       // startActivity(intent);
                       // finish();
                   } else if("username already used".equals(message)){

                       Toast.makeText(MainActivity2.this, "The username already exists, mate", Toast.LENGTH_LONG).show();

                   } else if("User not found".equals(message)){

                       Toast.makeText(MainActivity2.this, "username not found, mate", Toast.LENGTH_LONG).show();

                   } else{
                       //ここには到達しないはず
                       Toast.makeText(MainActivity2.this, "something is wrong, mate", Toast.LENGTH_LONG).show();

                   }

               }else{

                   Toast.makeText(MainActivity2.this, "Register failed", Toast.LENGTH_LONG).show();

               }
            }

            @Override
            public void onFailure(Call<ApiResponse> call,Throwable t){
                   Log.d("pity","shame");
            }
        });
    }

    private void getUserAlldata(String username, String password){

        User postman = new User(username,password);

        if(((WordApp)getApplication()).user!=null){
            String leID = ((WordApp)getApplication()).user.getterID();
            postman.setterUserId(leID);
        }

        Call<ApiResponse> call = apiService.syncOK(postman);

        Log.d("getUSerAlldata","1");

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("getUSerAlldata","2");
                if (response.isSuccessful()) {
                    Log.d("getUSerAlldata","3");
                    ApiResponse apiResponse = response.body();
                    String message = apiResponse.getMessage();

                    String server2local1_str = apiResponse.get_server2local1();

                    try{//list add
                        JSONArray server2local1 = new JSONArray(server2local1_str);
                       // insertIntoLocal(server2local1,1);
                       // Log.d("notError","ok2");
                        Log.d("getUSerAlldata","4");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("getUSerAlldata","5");
                    }

                    String server2local2_str = apiResponse.get_server2local2();

                    try{//word add
                        JSONArray server2local2 = new JSONArray(server2local2_str);
                      //  insertIntoLocal(server2local2,2);
                        // Log.d("notError","ok2");
                        Log.d("getUSerAlldata","6");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("getUSerAlldata","7");
                    }



                }else{

                    Log.d("getUSerAlldata","8");

                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle network or server error
                Log.d("getUSerAlldata","9");
                Toast.makeText(MainActivity2.this, "Network error", Toast.LENGTH_LONG).show();
            }


        });


    }



    private void loginUser(String username, String password) {

        //if((WordApp) getApplication()))
        //User user = new User(username, password);
        if(((WordApp) getApplication()).isGenerated()){

        }

        ((WordApp) getApplication()).userGenerate(username,password);
        // Make an HTTP POST request to the login endpoint
        Call<ApiResponse> call = apiService.login(((WordApp) getApplication()).user);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    String message = apiResponse.getMessage();
                    Log.d("tomlikesher",message);
                    // Check the message for "Login successful"
                    if ("Login successful".equals(message)) {
                        // Store the token securely (SharedPreferences)
                        String token = apiResponse.get_token();//response.headers().get("Authorization");
                        String userId = apiResponse.getUserId();
                        String username = ((WordApp) getApplication()).user.getterName();

                        ((WordApp)getApplication()).user.setterUserId(userId);

                        Log.d("1332011userId:",userId);
                        Log.d("1332011userToken:",token);

                        String userdata[] = {token,username,userId};

                        ((WordApp) getApplication()).setLoggedIn(true);
                        storeToken(userdata,((WordApp)getApplication()).isLoggedIn(),((WordApp)getApplication()).getSync());//((WordApp)getApplication()).getBehindWork()
                        //loginState=true;
                        Toast.makeText(MainActivity2.this, "Login success, mate!!", Toast.LENGTH_LONG).show();
                        updateStatusText();

                        //getUserAlldata(username,password);
                        ((WordApp) getApplication()).installUserData(MainActivity2.this);
                        //behindworkで

                        mySwitch.setEnabled(true);
                        // Navigate to the main app screen
                        //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                       // startActivity(intent);
                       // finish();
                    } else if("wrong password".equals(message)){

                        Toast.makeText(MainActivity2.this, "nah password wrong, mate", Toast.LENGTH_LONG).show();

                    } else if("User not found".equals(message)){

                        Toast.makeText(MainActivity2.this, "username not found, mate", Toast.LENGTH_LONG).show();

                    } else{
                        //ここには到達しないはず
                        Toast.makeText(MainActivity2.this, "something is wrong, mate", Toast.LENGTH_LONG).show();

                    }
                } else {
                    // Handle HTTP error
                    Toast.makeText(MainActivity2.this, "Login failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle network or server error

                Toast.makeText(MainActivity2.this, "Network error", Toast.LENGTH_LONG).show();
            }
        });
    }
//login 情報 store
    private void storeToken(String userdata[] ,Boolean login, Boolean sync) {
        SharedPreferences.Editor editor = getSharedPreferences(this.PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("token", userdata[0]);
        editor.putString("username", userdata[1]);
        editor.putString("userId", userdata[2]);
        editor.putBoolean("login",login);
        editor.putBoolean("sync",sync);
        editor.apply();
    }

    private void updateToken(String newToken, String newUsername, String newUserId, Boolean newLoginStatus, Boolean newSyncStatus) {
        SharedPreferences.Editor editor = getSharedPreferences(this.PREF_NAME, Context.MODE_PRIVATE).edit();

        // Update values if the new value is not null
        if (newToken != null) {
            editor.putString("token", newToken);
        }
        if (newUsername != null) {
            editor.putString("username", newUsername);
        }
        if (newUserId != null) {
            editor.putString("userId", newUserId);
        }
        if (newLoginStatus != null) {
            editor.putBoolean("login", newLoginStatus);
        }
        if (newSyncStatus != null) {
            editor.putBoolean("sync", newSyncStatus);
        }

        editor.apply();
    }


    //private static final String BASE_URL = "https://wordd-appy.onrender.com";//"http://10.0.2.2:4000/";//"http://localhost:4000/"; // Replace with your server's base URL
}
