package com.websarva.wings.android.wordmemorizz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class fQuiz extends AppCompatActivity {
    private DatabaseHelper _helper;
    private long listId=-1;
    private int[] orderr={};
    private int[] options={};
    private int whereNow=0;
    private final int NumOptions=4;
    private int ansNum=-1;
    private Map<Integer, Map<String,String>> wordsData;
    private int lenOfdata=-1;
    private int numOfquestions=-1;
    private String listTitle="";
    private ListView lvMenu4;
    private String realAns="";
    private String questionn="";
    private boolean fromNotifi=false;
    private int corresttimes=0;

    private int max=4;


    ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent intenty=result.getData();
                        if(intenty!=null){
                            Intent inn;
                            if(!fromNotifi) {
                                Log.d("fuuuuuuuuu", "whyyyy");
                                inn = new Intent();
                                inn.putExtra("mode", 5);
                                setResult(RESULT_OK, inn);
                                Log.d("fuuuuuuuuu", "whyyyy");
                                finish();
                            }else{//通知から始めたテストなら,,,
                                inn = new Intent(fQuiz.this, WordList.class);
                                inn.putExtra("idNum",listId);
                                inn.putExtra("title",listTitle);
                                inn.putExtra("mode",0);//max
                                inn.putExtra("kazu",max);
                              //  listId=intent22.getIntExtra("idNum",-1);
                              //  nonNewtitle=intent22.getStringExtra("title");//by tapping list

                                startActivity(inn);

                                finish();
                            }

                        }


                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fquiz);

      /*  String[] modes = {"Answer meaning", "Answer word", "blue", "black"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a mode");
        builder.setItems(modes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
              testMode=which;
            }
        });
        builder.show();*/
        // Create a NumberPicker




        Intent intentq=getIntent();

        listId=intentq.getIntExtra("listId",-1);
        Log.d("theid",String.valueOf(listId));
        fromNotifi=intentq.getBooleanExtra("fromNotification",false);
        listTitle=intentq.getStringExtra("titlel");

        _helper=new DatabaseHelper(fQuiz.this);

//Attempt to invoke virtual method 'android.database.sqlite.SQLiteDatabase com.websarva.wings.android.wordmemorizz.DatabaseHelper.getWritableDatabase()' on a null object reference
        //どうせやるならここでどの場合も単語数数えるのでいいのでは!!!!!!!!!!!!
        if(fromNotifi==true){
            SQLiteDatabase dbb = _helper.getWritableDatabase();
            String bun = "SELECT * FROM wordlist WHERE _id=" + listId ;

            Cursor cursor = dbb.rawQuery(bun, null);

            while (cursor.moveToNext()) {//its length must be one, otherwise f**k
                int idxNo = cursor.getColumnIndex("wordsNum");

                max = cursor.getInt(idxNo);
            }

        }else {
            max = intentq.getIntExtra("max", 4);
        }


        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(4);
        numberPicker.setMaxValue(max);//!!!

// Create an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a number");
        builder.setView(numberPicker);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the selected number
                int selectedNumber = numberPicker.getValue();
                // Do something with the selected number
                setUpy(selectedNumber);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the cancel action (if needed)
            }
        });

// Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }




    private void setUpy(int numy){

        numOfquestions=numy;
        lvMenu4=findViewById(R.id.lvMenu4);

        wordsData=new HashMap<>();//consists of all words in the list
        Map<String, String> duo=new HashMap<>();

        _helper=new DatabaseHelper(fQuiz.this);

      //  Intent intent=getIntent();

      //  listId=intent.getIntExtra("listId",-1);
     //   listTitle=intent.getStringExtra("titlel");
     //   fromNotifi=intent.getBooleanExtra("fromNotification",false);

     /*   if(fromNotifi==true){
            SQLiteDatabase dbb = _helper.getWritableDatabase();
        String bun = "SELECT * FROM wordlist WHERE _id='" + listId + "' ";
        Cursor cursor;
        cursor = dbb.rawQuery(bun, null);

            while (cursor.moveToNext()) {//its length must be one, otherwise f**k
           int idxNo = cursor.getColumnIndex("wordsNum");

            max = cursor.getInt(idxNo);
        }

        }*/


        //testMode=intent.getIntExtra("mode",0);


        SQLiteDatabase db=_helper.getWritableDatabase();
        String sql="SELECT * FROM words WHERE parent_id="+listId;

        Cursor cursor=db.rawQuery(sql, null);
        int kazu=0;
        while(cursor.moveToNext()){

            int idxNote=cursor.getColumnIndex("name");//楯列
            int idxNote2=cursor.getColumnIndex("meaning");
            int idxNote3=cursor.getColumnIndex("result");
            int idNote4=cursor.getColumnIndex("_id");

            String a=cursor.getString(idxNote);
            String b=cursor.getString(idxNote2);
            String c=cursor.getString(idxNote3);
            int d=cursor.getInt(idNote4);

            //  duo.put(a,b);
            duo.put("word",a);
            duo.put("meaning",b);
            duo.put("result",c);
            duo.put("id",String.valueOf(d));
            wordsData.put(Integer.valueOf(kazu),duo);//must be integer, not int
            duo=new HashMap<>();
            kazu++;
        }


        lenOfdata=kazu;
        Log.d("quizlen",String.valueOf(kazu));
        //lenOf=String.valueOf(lenOfdata);
        int excep=-1;
        //decide question order
        orderr=generateRandomIntArray(numOfquestions,lenOfdata-1,excep);//num of questions,

        for(int q=0;q<orderr.length;q++){
            Log.d("correcty", String.valueOf(orderr[q]));
        }
        //第一問目の表示 orderr[whereNow] int
        realAns=questionUpdates();
    }
    @Override
    protected void onDestroy(){
        _helper.close();
        super.onDestroy();
    }
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //正解かどうか
            // 今の問い wherenow/全体の問い lenofdata表示
            String choseAns = (String) parent.getItemAtPosition(position);
            Log.d("chosed", choseAns);
            Log.d("chosed", realAns);
            //databaseにアクセスして内容更新 テストに出た回数+1
            int correctOrNot = 0;
            if (realAns.equals(choseAns)) {
                //正解!
                //databaseにアクセスして内容更新 正解数+1
                correctOrNot += 1;
                corresttimes++;
                Log.d("chosed", "correct!");
            } else {
                Log.d("chosed", "notcorrect!");
            }
            //下の文面いるのか?
            SQLiteDatabase dbb = _helper.getWritableDatabase();
            Log.d("iyfii",wordsData.get(whereNow).get("result"));
            try {
                String newone=wordsData.get(ansNum).get("result");
                int theidy=Integer.valueOf(wordsData.get(ansNum).get("id"));

                dbb.beginTransaction();

                String ity=updatesResult(newone,String.valueOf(correctOrNot));



                //maru ka batuno hyouzi
                //correctRate=correctRate+" + correctOrNot + "
                //sync_statsもかえる!!!!!!!!!!!!!!!!!!!!!
                String forupdates = "UPDATE words SET correctRate=correctRate+" + correctOrNot + ",entire=entire+1,result='"+ity+ "',sync_stats_2 = 0 WHERE _id=" + theidy;//ここrealansではない

                SQLiteStatement stmtt = dbb.compileStatement(forupdates);

                stmtt.executeUpdateDelete();

                dbb.setTransactionSuccessful(); //try and catch 構文を使う


            } catch (Exception e) {
                //what the fucking hell shit
            } finally {
                dbb.endTransaction();
            }

            Log.d("whereNow", String.valueOf(whereNow) + "" + String.valueOf(lenOfdata));
            whereNow++;
            if (whereNow >= numOfquestions) {// -1だと正常に動くが、これがないと正常に動かんwhyyyyyyy
                //finish intentで結果発表のページを出して終わり finish()
                if(((WordApp)getApplication()).getSync()&&!((WordApp) getApplication()).getBehindWork()) {
                    ((WordApp) getApplication()).urakataStart(getApplication());
                }


                Log.d("finishedd", whereNow + "yaay" + lenOfdata);
                Intent inten = new Intent(fQuiz.this, Congrats.class);
                inten.putExtra("entire", numOfquestions);
                inten.putExtra("corrects", corresttimes);
                inten.putExtra("listT", listTitle);
                inten.putExtra("id", listId);
                inten.putExtra("fromN", fromNotifi);
                Log.d("finishedd", listId + "yaay" + corresttimes);
                //  startActivity(inten);
                resultLauncher.launch(inten);
                Log.d("finishedd", "finished2");
                //  finish();

            } else {

            // realAns=questionUpdates();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TextView nanmonme=findViewById(R.id.textViewoah);
                    // nanmonme.setText(String.valueOf(whereNow+1)+"/"+lenOf);
                    realAns = questionUpdates();
                }
            }, 500); //3 byogo ni carry out
        }

        }
    }
    private String updatesResult(String a,String extraLetter){
        String newString=a;
        Log.d("ohbefore1",newString);
        if(a.length()==5) {
           // String firstLetter = a.substring(0, 1); // the first letter
            String secondToFourthLetters = a.substring(1, 5);

            newString = secondToFourthLetters + extraLetter;
        }
        Log.d("ohbefore2",newString);
        return newString;
    }

    protected String questionUpdates() {
        //whereNow

        ansNum = orderr[whereNow];
        int excep=ansNum;
        Map<String, String> tentative = wordsData.get(ansNum);//map

        String targetword = "";
        String theAns = "";


                theAns=tentative.get("meaning");
                targetword=tentative.get("word");



      /*  for (String key : tentative.keySet()) {//1 syu //test modeによって答えが変わる
            theAns = tentative.get(key);//value=answer, key = question
            targetword = key;
        }*/
        //問題の単語表示
        TextView question = findViewById(R.id.textView7);
        TextView whereAmI= findViewById(R.id.textView10);
        questionn=targetword;
        question.setText(targetword);

        String questionNumEn=String.valueOf(numOfquestions);
        String questionNum=String.valueOf(whereNow+1);
        String disp=questionNum+"/"+questionNumEn;

        whereAmI.setText(disp);

        List<String> menuList4 = new ArrayList<>();


        options = generateRandomIntArray(NumOptions - 1, lenOfdata - 1, excep);

        excep = -1;
        int[] whenToCutin = generateRandomIntArray(1, NumOptions - 1, excep);
        int cutin = whenToCutin[0];

        for (int i = 0; i < options.length + 1; i++) {
            if (i == cutin) {
                menuList4.add(theAns);
            }
            String fakeAns="";
            if(i<options.length) {
                tentative = wordsData.get(options[i]);

                fakeAns = tentative.get("meaning");


                //String starget="";

               /* for (String key : tentative.keySet()) {//1 syu
                    fakeAns = tentative.get(key);//value=answer, key = question
                    //starget=key;
                }*/
                menuList4.add(fakeAns);
            }
        }
        //選択肢の表示
        ArrayAdapter<String> adapter = new ArrayAdapter<>(fQuiz.this, android.R.layout.simple_list_item_1, menuList4);
        lvMenu4.setAdapter(adapter);
        lvMenu4.setOnItemClickListener(new fQuiz.ListItemClickListener());


        return theAns;

        //return 0 or 1 0 no 1 yes
    }
    public static int[] generateRandomIntArray(int n, int m, int x) {
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();
        while (list.size() < n) {
            int num = random.nextInt(m + 1);
            if (num != x && !list.contains(num)) {
                list.add(num);
            }
        }
        Collections.shuffle(list);
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}