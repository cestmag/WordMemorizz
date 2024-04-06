package com.websarva.wings.android.wordmemorizz;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.core.os.HandlerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ForAPIs {
    private static final String DICT_API_URL1="https://api.dictionaryapi.dev/api/v2/entries/en/";
    private static final String BUG="AsyncSample";
    protected String getter(){
        return DICT_API_URL1;
    }


    @UiThread
    protected String receiveMeaning(final String urlfull){
        //外部のQuickNoteから呼ばれたかどうか
        Looper mainLooper = Looper.getMainLooper();
        Handler handler= HandlerCompat.createAsync(mainLooper);
        MeaningInfoReceiver backgroundReceiver=new MeaningInfoReceiver(handler,urlfull);
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<String> futureResult=executorService.submit(backgroundReceiver);

        String result="";//jsonarray

        try {
            // Future型は「getメソッド」で取り出す必要あり。
            result = futureResult.get();
           // System.out.println(result);
        }catch (Exception e){
            //System.out.println(e);
            result="";
        }

        MeaningPostExecutor postExecutor = new MeaningPostExecutor(result);

        result="";

        result=postExecutor.call();

        return result;

    }
   // private String[] returnMeaning() {
     /*   @UiThread
        private void receiveMeaning(final String urlfull){
            //外部のQuickNoteから呼ばれたかどうか
            Looper mainLooper = Looper.getMainLooper();
            Handler handler= HandlerCompat.createAsync(mainLooper);
            MeaningInfoReceiver backgroundReceiver=new MeaningInfoReceiver(handler,urlfull);
            ExecutorService executorService= Executors.newSingleThreadExecutor();
            executorService.submit(backgroundReceiver);

        }*/
          private class MeaningPostExecutor /*implements Runnable, Callable<String>*/ {

            private final String _result;

            public MeaningPostExecutor(String result) {
                _result = result;
            }

            @UiThread
        /*   @Override
          public void run() {
                String example = "";
                String meaning = "";
                String type = "";
                try {
                    Log.d("came here", _result);
                    //JSONObject rootJSON=new JSONObject(_result);



                    JSONArray jsonArray = new JSONArray(_result);
                    JSONObject numZero = jsonArray.getJSONObject(0);
                    JSONArray meanings = numZero.getJSONArray("meanings");
                    JSONObject firstOne = meanings.getJSONObject(0);
                    type = firstOne.getString("partOfSpeech");
                    JSONArray definitions = firstOne.getJSONArray("definitions");
                    JSONObject ichi = definitions.getJSONObject(0);
                    //   example=ichi.getString("example");
                    meaning = ichi.getString("definition");
                    //    Log.d("searchResult1",example);
                    Log.d("searchResult2", " type:" + type + ":" + meaning);
                    //  input2.setText(meaning, TextView.BufferType.EDITABLE);
                    //ここでQuickから呼ばれたのならQuickに値を返す(Quickの中のメソッドを実行する)setter getter
                } catch (JSONException ex) {
                    // Log.e(BUG, "dissect json fail",ex);
                    Log.d("searchResult2", "noooo");
                    //  Toast.makeText(AddWord.this,"Sorry, couldn't find",Toast.LENGTH_LONG).show();
                }
            }*/


            public String call(){
                String example = "";
                String meaning = "";
                String type = "";
                try {
                    Log.d("came here", _result);
                    //JSONObject rootJSON=new JSONObject(_result);


                    JSONArray jsonArray = new JSONArray(_result);
                    JSONObject numZero = jsonArray.getJSONObject(0);
                    JSONArray meanings = numZero.getJSONArray("meanings");
                    JSONObject firstOne = meanings.getJSONObject(0);
                    type = firstOne.getString("partOfSpeech");
                    JSONArray definitions = firstOne.getJSONArray("definitions");
                    JSONObject ichi = definitions.getJSONObject(0);
                    //   example=ichi.getString("example");
                    meaning = ichi.getString("definition");
                    //    Log.d("searchResult1",example);
                    Log.d("searchResult2", " type:" + type + ":" + meaning);
                    //  input2.setText(meaning, TextView.BufferType.EDITABLE);
                    //ここでQuickから呼ばれたのならQuickに値を返す(Quickの中のメソッドを実行する)setter getter
                } catch (JSONException ex) {
                    // Log.e(BUG, "dissect json fail",ex);
                    Log.d("searchResult2", "noooo");
                    //  Toast.makeText(AddWord.this,"Sorry, couldn't find",Toast.LENGTH_LONG).show();
                    meaning="";
                }
                return meaning;
            }
        }

         private class MeaningInfoReceiver implements /*Runnable*/Callable<String> {

            private final Handler _handler;

            private final String _urlFull;

            public MeaningInfoReceiver(Handler handler, String urlFull) {
                _handler = handler;
                _urlFull = urlFull;
            }

            @WorkerThread
          /*  @Override
            public void run() {
                HttpURLConnection con = null;

                InputStream is = null;

                String result = "";

                try {
                    URL url = new URL(_urlFull);
                    con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(1000);
                    con.setReadTimeout(1000);
                    con.setRequestMethod("GET");
                    con.connect();
                    is = con.getInputStream();
                    result = is2String(is);
                } catch (MalformedURLException ex) {
                    Log.e(BUG, "convertion failed", ex);
                } catch (SocketTimeoutException ex) {
                    Log.w(BUG, "connection run out", ex);
                } catch (IOException ex) {
                    Log.e(BUG, "connection fail", ex);
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }

                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ex) {
                            Log.e(BUG, "release inputStream fail", ex);
                        }
                    }
                }

                MeaningPostExecutor postExecutor = new MeaningPostExecutor(result);
                //_handler.post(postExecutor);
            }*/

            @Override
            public String call(){
                HttpURLConnection con = null;

                InputStream is = null;

                String result = "";

                try {
                    URL url = new URL(_urlFull);
                    con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(1000);
                    con.setReadTimeout(1000);
                    con.setRequestMethod("GET");
                    con.connect();
                    is = con.getInputStream();
                    result = is2String(is);
                } catch (MalformedURLException ex) {
                    Log.e(BUG, "convertion failed", ex);
                } catch (SocketTimeoutException ex) {
                    Log.w(BUG, "connection run out", ex);
                } catch (IOException ex) {
                    Log.e(BUG, "connection fail", ex);
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }

                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ex) {
                            Log.e(BUG, "release inputStream fail", ex);
                        }
                    }
                }

                return result;
            }



            private String is2String(InputStream is) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sb = new StringBuffer();
                char[] b = new char[1024];
                int line;
                while (0 <= (line = reader.read(b))) {
                    sb.append(b, 0, line);
                }
                return sb.toString();
            }


        }
   // }
}
