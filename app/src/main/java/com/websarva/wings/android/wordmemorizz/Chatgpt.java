package com.websarva.wings.android.wordmemorizz;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//method callAPIにpromptがある.
public class Chatgpt /*extends AppCompatActivity*/ {
    String model;
    final private String apiKey = "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";
    //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
    //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
    //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
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    //String resultText="a";
    private String reponseBody;

    Chatgpt(){
        this.model="gpt-3.5-turbo";
    }

    public /*static*/ String Answer(String message) {//使ってない
        //not work dont know why
        String url = "https://api.openai.com/v1/chat/completions";


        //String model = "gpt-3.5-turbo";

        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            String question="Meaning of \""+message+"\" with 20 word limit";

            // Build the request body
          //  String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + question + "\"}],\"max_tokens\":50}";
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + question + "\"}]}";
            con.setDoOutput(true);
            //Log.d("id1543","yay");
            //ここから下がエラー
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            Log.d("id1543","yay");
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return (response.toString().split("\"content\":\"")[1].split("\"")[0]).substring(4);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }finally{
            return "Unexpected errorrrr sorrow sorry";
        }

    }

    public void handleResponse(String responseBody){
        this.reponseBody=responseBody;
        Log.d("9685",this.reponseBody);
    }

    public String getResponseBody(){
       // Log.d("96851",reponseBody);
        if(reponseBody!=null) {
            return reponseBody;
        }else{
            return "error";
        }
    }
    public interface MyCallback{
        void onSuccess(String[] responseBody);
        void onFailure(int typ);
    }

    public String callAPI(String what,MyCallback callback,int num){
        String resultText="Wait a minute...";
        JSONObject jsonBody =new JSONObject();
        JSONObject innermost=new JSONObject();
        JSONArray innerarray=new JSONArray();
        String question="Define \""+what+"\" in 10 words";//"Meaning of \""+what+"\" with 10 word limit";
        String question2="Provide a <10 word sentence with \""+what+"\"" ; //"Define \""+what+"\" in 10 words Give a <10 word example";
        //Define "bloom" in 10 words Give a <10 word example
        //Define "welsh on" in <15 words
        try{
          innermost.put("role","user");
          switch (num){
              case 0:
                  innermost.put("content", question);
                  break;
              case 1:
                  innermost.put("content", question2);
                  break;
              default:
          }

          innerarray.put(innermost);


          jsonBody.put("model",this.model);
          jsonBody.put("messages",innerarray);
       //   jsonBody.put("max_tokens",50);
        //  jsonBody.put("temperature",1);
        }catch (JSONException e){
         e.printStackTrace();
        }
        RequestBody body=RequestBody.create(jsonBody.toString(),JSON);
        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer "+apiKey)
                .post(body)
                .build();

          client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(0);
              //  resultText="sorry error0";
                Log.d("1249","successss");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                 if(response.isSuccessful()){
                     JSONObject jsonObject= null;
                     try {
                         jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray=jsonObject.getJSONArray("choices");
                       JSONObject fina =jsonArray.getJSONObject(0).getJSONObject("message");

                       //handleResponse(fina.getString("content").trim());
                       // resultText=fina.getString("content");
                         String res=fina.getString("content").trim();

                         String[] kaito=new String[2];

                        switch (num){
                            case 0:
                                kaito[0]="0";
                                break;
                            case 1:
                                kaito[0]="1";
                                break;
                        }
                        kaito[1]=res;

                        callback.onSuccess(kaito);
                       //  Log.d("1246",resultText.trim());
                     } catch (JSONException e) {
                         e.printStackTrace();
                         callback.onFailure(1);
                       //  resultText="sorry error1";
                         Log.d("1247",e.getMessage());
                     }

                 }else{
                     callback.onFailure(2);
                   //  resultText="sorry error2";
                     Log.d("1248","successss");

                     if (response.body() != null) {
                         Log.d("1248", "Response Body: " + response.body().string());
                     } else {
                         Log.d("1248", "Response Body is null");
                     }
                 }
            }
        });

       return resultText;

    }
}
