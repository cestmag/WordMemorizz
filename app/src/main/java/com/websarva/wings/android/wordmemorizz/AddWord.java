package com.websarva.wings.android.wordmemorizz;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddWord extends AppCompatActivity {
    private long listId=0;
    private String titleOflist="";
    private long idOfword=-1;
    private String protagonist="";
    private String implicit="";
    private String exsenty="";
    private int posi=-1;
    private DatabaseHelper _helper;
    private int mode=0;
    private static final String DICT_API_URL1="https://api.dictionaryapi.dev/api/v2/entries/en/";
    private static final String BUG="AsyncSample";
    EditText input ;//word
    EditText input2;//meaning
    EditText input3;//ex
    Chatgpt robot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        _helper=new DatabaseHelper(AddWord.this);

        Button button4=findViewById(R.id.button4);
        Button button7=findViewById(R.id.button7);
        Button button5=findViewById(R.id.button5);
        Button button12=findViewById(R.id.button12);
        AddWord.Clickk lisner=new AddWord.Clickk();
        button4.setOnClickListener(lisner);
        button7.setOnClickListener(lisner);
        button5.setOnClickListener(lisner);
        button12.setOnClickListener(lisner);

        Intent intent=getIntent();

        robot =new Chatgpt();
        listId=intent.getLongExtra("listId",0);
        titleOflist=intent.getStringExtra("titlel");

        //edit
        protagonist=intent.getStringExtra("word");
        implicit=intent.getStringExtra("meaning");
        exsenty=intent.getStringExtra("sentence");
        idOfword=intent.getLongExtra("id",-1);
        posi=intent.getIntExtra("pos",-1);
        //Log.d("1231241",String.valueOf(posi)+":"+String.valueOf(mode));
        input = findViewById(R.id.editTextTextPersonName2);//word
        input2= findViewById(R.id.editTextTextPersonName3);//meaning
        input3= findViewById(R.id.editTextTextPersonName5);

        //judge if it's adding newly or edit
        if(protagonist!=null){
            //edit
            mode=1;
            input.setText(protagonist, TextView.BufferType.EDITABLE);
            input2.setText(implicit, TextView.BufferType.EDITABLE);

            input3.setText(exsenty, TextView.BufferType.EDITABLE);
            button4.setText("Edit");

        }else{
            //add
            mode=0;
        }
        Log.d("1231241",String.valueOf(posi)+":"+String.valueOf(mode));

    }
    protected String getterOfUrl(){
        return this.DICT_API_URL1;
    }

    @Override
    protected void onDestroy(){
        _helper.close();
        super.onDestroy();
    }
    @UiThread
    private void receiveMeaning(final String urlfull){
        //外部のQuickNoteから呼ばれたかどうか
        Looper mainLooper = Looper.getMainLooper();
        Handler handler= HandlerCompat.createAsync(mainLooper);
         MeaningInfoReceiver backgroundReceiver=new MeaningInfoReceiver(handler,urlfull);
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        executorService.submit(backgroundReceiver);

    }
    protected void whatIsMeaning(final String urll){
        //外部のQuickNoteから呼ばれたかどうか
        receiveMeaning(urll);
        //return "a";
    }
    private class MeaningInfoReceiver implements Runnable{

        private final Handler _handler;

        private final String _urlFull;

        public MeaningInfoReceiver(Handler handler,String urlFull){
          _handler=handler;
          _urlFull=urlFull;
        }

        @WorkerThread
        @Override
        public void run(){
            HttpURLConnection con =null;

            InputStream is=null;

            String result="";

            try{
                URL url=new URL(_urlFull);
                con =(HttpURLConnection) url.openConnection();
                con.setConnectTimeout(1000);
                con.setReadTimeout(1000);
                con.setRequestMethod("GET");
                con.connect();
                is=con.getInputStream();
                result=is2String(is);
            }catch (MalformedURLException ex){
                Log.e(BUG,"convertion failed",ex);
            }catch (SocketTimeoutException ex){
                Log.w(BUG, "connection run out",ex);
            }catch (IOException ex){
                Log.e(BUG,"connection fail",ex);
            }finally{
                if(con!=null){
                    con.disconnect();
                }

                if(is!=null){
                    try{
                        is.close();
                    }catch (IOException ex){
                        Log.e(BUG,"release inputStream fail",ex);
                    }
                }
            }

           MeaningPostExecutor postExecutor=new MeaningPostExecutor(result);
           _handler.post(postExecutor);
        }

        private String is2String(InputStream is) throws IOException{
            BufferedReader reader =new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb=new StringBuffer();
            char[] b= new char[1024];
            int line;
            while(0<=(line=reader.read(b))){
                sb.append(b,0,line);
            }
            return sb.toString();
        }


    }
    private class MeaningPostExecutor implements Runnable{

        private final String _result;

        public MeaningPostExecutor(String result){
            _result=result;
        }
        @UiThread
        @Override
        public void run(){
            String example="";
            String meaning="";
            String type="";
            try{
                Log.d("came here",_result);
                //JSONObject rootJSON=new JSONObject(_result);
              /*  JSONObject jso=new JSONObject(_result);
                JSONArray jsonArray=jso.getJSONArray("meanings");
                JSONArray jsonArray1=jsonArray.getJSONArray(0);
                JSONArray jsonArray2=jsonArray1.g*/


                JSONArray jsonArray=new JSONArray(_result);
                JSONObject numZero=jsonArray.getJSONObject(0);
                JSONArray meanings=numZero.getJSONArray("meanings");
                JSONObject firstOne=meanings.getJSONObject(0);
                type=firstOne.getString("partOfSpeech");
                JSONArray definitions=firstOne.getJSONArray("definitions");
                JSONObject ichi=definitions.getJSONObject(0);
             //   example=ichi.getString("example");
                meaning=ichi.getString("definition");
            //    Log.d("searchResult1",example);
                Log.d("searchResult2",  " type:"+type+":"+meaning);
                setTexx(meaning);
               // input2.setText(meaning, TextView.BufferType.EDITABLE);
                //ここでQuickから呼ばれたのならQuickに値を返す(Quickの中のメソッドを実行する)setter getter
            }catch (JSONException ex){
               // Log.e(BUG, "dissect json fail",ex);
                Log.d("searchResult2","noooo");
                Toast.makeText(AddWord.this,"Sorry, couldn't find",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setTexx(String a){
        if(input2!=null){
            input2.setText(a,TextView.BufferType.EDITABLE);
        }
    }
    private void setTexx2(String b){
        if(input3!=null){
            input3.setText(b,TextView.BufferType.EDITABLE);
        }
    }

    private class Clickk implements View.OnClickListener{
        @Override
        public void onClick(View view){
            int id=view.getId();
            boolean already=true;
            String output = input.getText().toString();//word
            String output2= input2.getText().toString();//meaning
            String output3= input3.getText().toString();
            switch (id) {
                //add a word
                case R.id.button4:


                    //String forupdates = "UPDATE words SET correctRate=correctRate+"+correctOrNot+",entire=entire+1 "+" WHERE parent_id="+listId;//ここrealansではない
                    already=_helper.CheckIfValid(output/*,listId,idOfword*/);

                    if ((output.length() > 0&&output2.length()>0&&already&&mode!=1)||(output.length() > 0&&output2.length()>0&&mode==1)) {// and there's no identical list name
                        //add the data to sql database use listId
                       // SQLiteDatabase db=_helper.getWritableDatabase();

                        //String sqlInsert="INSERT INTO wordlist (_id, listName) VALUES(?,?)";
                        String sqlInsert ="";
                        long lastinsertedone=-1;
                        switch (mode) {
                            case 1://edit
                                //  sqlInsert=  "UPDATE words SET name=?,meaning=?,sentence=? "+" WHERE _id="+idOfword;
                                String one = "name=" + output;
                                String two = "meaning=" + output2;
                               //Log.d("ohgodno",output2);
                                String three = "sentence=" + output3;
                                String four = "sync_stats_1=0";//server dbのsync 0
                                String five = "sync_stats_2=0";
                                Object[] inserteddata = {one, two, three, four,five};
                                _helper.updateData(idOfword, inserteddata, "words");
                                Log.d("wordEdited",String.valueOf(idOfword));
                                break;
                            default://add

                                              //meaning  sentence                      name
                                Object[] eta = {output2, output3, listId, titleOflist, output};

                                lastinsertedone = _helper.insertData("words", eta);
                                //sqlInsert = "INSERT INTO words (_id,parent_id, correctRate,entire,name, meaning,result,listTitle,sentence ) VALUES(?,?,?,?,?,?,?,?,?)";


                        }

                        //behinfwork on

                        //((WordApp)getApplication()).urakataStart(getApplication());
                        //SQLiteStatement stmt=db.compileStatement(sqlInsert);
                        //int wanton=1;
                        //myworker OneTimeWorkRequest発動
                        //


                        Intent databack;
                        switch (mode){
                            case 1://back to worddetail edit
                             /*   stmt.bindString(1,output);
                                stmt.bindString(2,output2);
                                stmt.bindString(3,output3);
                                stmt.executeUpdateDelete();*/

                                databack=new Intent();
                                databack.putExtra("adddata",output);
                                databack.putExtra("adddata2",output2);
                                databack.putExtra("adddata3",output3);
                                databack.putExtra("pos",String.valueOf(posi));
                                databack.putExtra("id",idOfword);
                                databack.putExtra("mode",1);
                               // databack.putExtra("why",111);
                                Log.d("4579374591",String.valueOf(lastinsertedone));
                                setResult(RESULT_OK,databack);

                                break;
                            default://back to wordlist add
                               /* stmt.bindLong(2,listId);

                                stmt.bindLong(3,0);
                                stmt.bindLong(4,0);
                                stmt.bindString(5,output);
                                stmt.bindString(6, output2);
                                stmt.bindString(7,"33333");
                                stmt.bindString(8,titleOflist);
                                stmt.bindString(9,output3);
                                stmt.executeInsert();

                                db=_helper.getWritableDatabase();

                                String getlastid="SELECT last_insert_rowid()";

                                Cursor cursor=db.rawQuery(getlastid,null);
                                long soleId=-1;
                                if( cursor.moveToFirst() ){
                                   //soleId=cursor.getColumnIndex("_id");
                                    soleId=cursor.getInt(0);
                                    Log.d("qwert",String.valueOf(soleId));
                                }

                              //  String anotherone="UPDATE words SET wordsNum=wordsNum+"+1+" WHERE _id="+listId;
                                db=_helper.getWritableDatabase();
                                try {
                                    //_helper.updateData();


                                    db.beginTransaction();
                                    //maru ka batuno hyouzi
                                    //correctRate=correctRate+" + correctOrNot + "
                                    String forupdates = "UPDATE wordlist SET wordsNum=wordsNum+1 WHERE _id="+listId;
                                    SQLiteStatement stmtt = db.compileStatement(forupdates);

                                    stmtt.executeUpdateDelete();

                                    db.setTransactionSuccessful(); //try and catch 構文を使う
                                }catch(Exception e){
                                    //what the fucking hell shit
                                } finally {
                                    db.endTransaction();
                                }*/

                                Log.d("4579374591",String.valueOf(lastinsertedone));
                                databack=new Intent();
                                databack.putExtra("adddata",output);
                                databack.putExtra("adddata2",output2);
                                databack.putExtra("adddata3",output3);
                                databack.putExtra("mode",1);
                                databack.putExtra("id",lastinsertedone);
                                databack.putExtra("pos",posi);
                                //databack.putExtra (id!!!!!!!!)

                                setResult(RESULT_OK,databack);

                        }

                     //   Intent backk=new Intent(AddWord.this, WordList.class);
                     //   backk.putExtra("idNum",listId);
                     //   backk.putExtra("title",titleOflist);
                      //  startActivity(backk);
                        finish();
                    }else{
                        if(already==false){
                            OrderConfirmDialogFragment2 dialogFragment2=new OrderConfirmDialogFragment2();
                            dialogFragment2.show(getSupportFragmentManager(),"OrderConfirmDialogFragment2");
                        }else{
                            OrderConfirmDialogFragment dialogFragment=new OrderConfirmDialogFragment();
                            dialogFragment.show(getSupportFragmentManager(),"OrderConfirmDialogFragment");

                        }

                    }

                    break;
                //cancel
                case R.id.button7:

                    setResult(RESULT_OK);
                    finish();
                    break;
                //use dictionary api
                case R.id.button12:
                    String outputTrimed=output.trim();
                    if(outputTrimed.length()<=100&&outputTrimed.length()>=1&&robot!=null) {
                        //Chatgpt robot = new Chatgpt();
                        Chatgpt.MyCallback callback=new Chatgpt.MyCallback() {
                            @Override
                            public void onSuccess(String[] responseBody) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(responseBody[0].equals("0")){
                                            setTexx(responseBody[1]);
                                        }else if(responseBody[0].equals("1")){
                                            setTexx2(responseBody[1]);
                                        }


                                    }
                                });

                            }

                            @Override
                            public void onFailure(int typ/*IOException e*/) {
                                 runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         Log.e("Chatgpt", "Error in API call: " + typ);
                                         setTexx("bro error broo");

                                     }
                                 });
                            }
                        };

                        setTexx(robot.callAPI(output,callback,0));
                        setTexx2(robot.callAPI(output,callback,1));

                    }else{
                        Log.d("too long Or null","shorten it bro");
                    }
                    break;
                case R.id.button5:

                    if(output.length()>0){
                       String fullUrl = DICT_API_URL1+output;
                        receiveMeaning(fullUrl);
                      /*
                        don't know why but it takes more time with the code below than the one above

                        ForAPIs accessTo=new ForAPIs();
                        String urly=accessTo.getter();

                        String resulty=accessTo.receiveMeaning(urly+output);

                        input2.setText(resulty,TextView.BufferType.EDITABLE);*/

                    }else{
                        OrderConfirmDialogFragment dialogFragment=new OrderConfirmDialogFragment();
                        dialogFragment.show(getSupportFragmentManager(),"OrderConfirmDialogFragment");
                    }

                    break;
            }
            if(((WordApp)getApplication()).getSync()&&!((WordApp)getApplication()).getBehindWork()) {
                ((WordApp) getApplication()).urakataStart(getApplication());
            }


        }


        /*protected boolean CheckIfValid(String target,long targetId,long idOfTheword){
            SQLiteDatabase db=_helper.getWritableDatabase();

            String stat="SELECT * FROM words WHERE name='"+target+"' AND parent_id="+targetId+" AND _id!="+idOfTheword +" LIMIT 1";
            //LIMIT 1個見つかった時点で終了
            Cursor cursor=db.rawQuery(stat,null);
            int count=0;
            while (cursor.moveToNext()){
                count++;
            }

            if(count>0){
                return false;
            }else{
                return true;
            }







        }*/


    }
}