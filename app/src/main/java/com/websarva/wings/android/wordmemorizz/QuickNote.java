package com.websarva.wings.android.wordmemorizz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class QuickNote extends AppCompatActivity {

    EditText multipleone;
    private String[] eachWord={};
    private String[] eachFull={};
    private DatabaseHelper _helper;
    private ArrayList<Integer> ids;
    private long destinationId;
    private long theid=-1;
    private String name;
    private String previousOne="";

    Chatgpt robot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_note);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        actionBar.setTitle("Quick Note");
        Intent intenty=getIntent();
        theid=Long.valueOf(intenty.getStringExtra("id"));

        _helper=new DatabaseHelper(QuickNote.this);
        multipleone=findViewById(R.id.editTextTextPersonName4);

        robot =new Chatgpt();

        ArrayList<View> viewsToFadeIn = new ArrayList<View>();

        FloatingActionButton fab=findViewById(R.id.floatingActionButton12);
        FloatingActionButton fab10=findViewById(R.id.floatingActionButton13);
        FloatingActionButton fab11=findViewById(R.id.floatingActionButton14);

        viewsToFadeIn.add(fab);
        viewsToFadeIn.add(fab10);
        viewsToFadeIn.add(fab11);

        HelloListener lis=new HelloListener();

        for (View v : viewsToFadeIn)
        {
            v.setOnClickListener(lis);

            v.setAlpha(0); // make invisible to start
        }

        for (View v : viewsToFadeIn)
        {
            // 3 second fade in time
            v.animate().alpha(1.0f).setDuration(1000).start();
        }

        SQLiteDatabase db22=_helper.getWritableDatabase();

        String pullout = "SELECT * FROM memo WHERE _id="+theid;//must be one

        Cursor cursor2 = db22.rawQuery(pullout, null);


        int alreadyExist=0;
        while (cursor2.moveToNext()) {//its length must be one, otherwise fuck
            int idxNo = cursor2.getColumnIndex("content");
            previousOne=cursor2.getString(idxNo);
            alreadyExist++;
        }



        /*if(alreadyExist==0){
            SQLiteDatabase dby=_helper.getWritableDatabase();
            String a="INSERT INTO memo (_id,content ) VALUES(?,?)";
            SQLiteStatement stmt=dby.compileStatement(a);

            stmt.bindString(2,"");
          //  stmt.bindString(3,"memo");
            stmt.executeInsert();
        }else{*/
            multipleone.setText(previousOne,TextView.BufferType.EDITABLE);

            int textLength = multipleone.getText().length();
            Spannable spannable = multipleone.getText();
            Selection.setSelection(spannable, textLength);
       // }



        Button addToButton=findViewById(R.id.button6);
        HelloListener hell=new HelloListener();
        addToButton.setOnClickListener(hell);

        Spinner spinner = findViewById(R.id.spinner);

        SQLiteDatabase db2=_helper.getWritableDatabase();

        String pulll = "SELECT _id, listName FROM wordlist";//parent idの条件も!!

        Cursor cursor = db2.rawQuery(pulll, null);

        ArrayList<String> arrayList = new ArrayList<>();
        ids=new ArrayList<>();

        while (cursor.moveToNext()) {//its length must be one, otherwise fuck
            int idxNo = cursor.getColumnIndex("_id");
            int idxNo2=cursor.getColumnIndex("listName");
            Log.d("qqq", String.valueOf(idxNo));
           // listId = cursor.getInt(idxNo);
            arrayList.add(cursor.getString(idxNo2));
            ids.add(cursor.getInt(idxNo));
        }

        //arrayList.add("Register list");    //ここなくした
        //ids.add(-1);
        //int lenOfOp = arrayList.size();
        //Toast.makeText(this,String.valueOf(lenOfOp),Toast.LENGTH_LONG).show();


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(QuickNote.this,android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tutorialsName = parent.getItemAtPosition(position).toString();
                destinationId=ids.get(position);
                name=parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
                destinationId=ids.get(0);
                name=arrayList.get(0);
            }
        });

       // int lenOfOp = arrayList.size();
        //Toast.makeText(this,"end",Toast.LENGTH_LONG).show();

    }
    private void endAndHozon(int mode){
        String updatedOne=multipleone.getText().toString();
        //sqliteDatabaseに保存(update)
        SQLiteDatabase dba=_helper.getWritableDatabase();

        String sqlInsert=  "UPDATE memo SET content=? "+" WHERE _id="+theid;

        SQLiteStatement stmt=dba.compileStatement(sqlInsert);

        stmt.bindString(1,updatedOne);

        stmt.executeInsert();

        Intent intent;

        switch (mode){
            case 0://search
                intent = new Intent(QuickNote.this, WordList.class);
                intent.putExtra("mode",1);
                break;
            case 1://home
                intent = new Intent(QuickNote.this, MainActivity.class);
                break;
            case 3:
            default://memolist
                intent = new Intent(QuickNote.this, MemoListt.class);
        }



        startActivity(intent);


        finish();
    }
    @Override//下の戻るボタンが押された時
    public void onBackPressed() {
        super.onBackPressed();
        endAndHozon(3);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId=item.getItemId();
        switch (itemId){
            case android.R.id.home:
               endAndHozon(3);
                break;
        }
        return true;
    }
    @Override
    protected void onDestroy(){
        _helper.close();
        super.onDestroy();
    }

    private class HelloListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                case R.id.button6:
                    int registeredNum = 0;
                    int unfound = 0;
                    if (multipleone != null) {
                        eachWord = multipleone.getText().toString().split("\\n");


                eachFull = eachWord;
                ForAPIs apiAccess = new ForAPIs();

                String urly = apiAccess.getter();//+<word>


                //data baseに書き込み
                SQLiteDatabase db = _helper.getWritableDatabase();


                //String meaning = "";
                //String sentence = "";

                for (int i = 0; i < eachWord.length; i++) {
                    if ((!eachWord[i].contains("!")) && eachWord[i].length() > 0) {
                        String word = "";
                        String meaningy = "";
                        if (eachWord[i].contains("/")) {
                            String[] intoTwo = eachWord[i].split("/");
                            word = intoTwo[0];
                            meaningy = intoTwo[1];
                        } else {
                            word = eachWord[i];
                            //既にリストに含まれているか確認する必要あり

                            //chatgpt api呼ぶ

                      /*      Chatgpt.MyCallback callback=new Chatgpt.MyCallback() {
                                @Override
                                public void onSuccess(String[] responseBody) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(responseBody[0].equals("0")){
                                                //setTexx(responseBody[1]);
                                            }else if(responseBody[0].equals("1")){
                                                //setTexx2(responseBody[1]);
                                            }


                                        }
                                    });

                                }

                                @Override
                                public void onFailure(int typ) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("Chatgpt", "Error in API call: " + typ);
                                            //setTexx("bro error broo");

                                        }
                                    });
                                }
                            };

                            meaning = robot.callAPI(eachWord[i],callback,0);//意味
                            sentence = robot.callAPI(eachWord[i],callback,1);//例文
                    */
                            //setTexx(robot.callAPI(output,callback,0));
                            //setTexx2(robot.callAPI(output,callback,1));


                            //dictionary api 呼ぶ

                            meaningy = apiAccess.receiveMeaning(urly + eachWord[i]);

                            if (meaningy.length() <= 0) {
                                //user chatgpt here
                                meaningy = "NotFound";
                                unfound++;
                            }

                            //将来的にどっちも使えなくなるかもしれんから
                            //対策しておく


                        }

                 //dictionary libの時はこっち
                         Object[] inserteddata={meaningy,"",destinationId,name,word};////data :meaning sentence parent_id listTitle name
                     //chatgptの時はこｘっち   Object[] inserteddata={meaning,sentence,destinationId,name,word};

                        _helper.insertData("words",inserteddata);

                      /*  String sqlInsert = "";
                        sqlInsert = "INSERT INTO words (_id,parent_id, correctRate,entire,name, meaning,result,listTitle,sentence ) VALUES(?,?,?,?,?,?,?,?,?)";
                        SQLiteStatement stmt = db.compileStatement(sqlInsert);


                        stmt.bindLong(2, destinationId);

                        stmt.bindLong(3, 0);
                        stmt.bindLong(4, 0);
                        stmt.bindString(5, word);
                        stmt.bindString(6, meaningy);
                        stmt.bindString(7, "33333");
                        stmt.bindString(8, name);
                        stmt.bindString(9, "sentence");
                        stmt.executeInsert();*/

                        registeredNum++;
                        eachFull[i] = "";

                        //wordlistNo単語数変化させる!!!!!!!!!!!!!

                    }


                }

                int numofwords=_helper.countRowsWithParentId(destinationId);
                Object[] koshin={"wordsNum="+numofwords};

                Log.d("nooooah",String.valueOf(numofwords));
                _helper.updateData(destinationId,koshin,"wordlist");

                if(((WordApp)getApplication()).getSync()) {
                    ((WordApp) getApplication()).urakataStart(getApplication());
                }

                        //worker 起動
                //
                /*SQLiteDatabase db2 = _helper.getWritableDatabase();

                try {

                    db2.beginTransaction();
                    String sqlInsert = "";
                    sqlInsert = "UPDATE wordlist SET wordsNum=wordsNum+" + registeredNum + " WHERE _id=" + destinationId;
                    SQLiteStatement stmt2 = db2.compileStatement(sqlInsert);
                    stmt2.executeUpdateDelete();
                    db2.setTransactionSuccessful(); //try and catch 構文を使う

                } catch (Exception e) {
                    //what the fucking hell shit

                } finally {
                    db2.endTransaction();
                }*/


                String resetText = "";

                for (int j = 0; j < eachFull.length; j++) {
                    if (eachFull[j].length() > 0) {


                        resetText += eachFull[j] + "\n";

                    }
                }

               /* if (resetText.endsWith("\n")) {
                  resetText=resetText.substring(0,resetText.length()-2);
                }*/

                if (registeredNum > 0) {

                    String mess = "Added " + registeredNum + " word(s) to " + name + " List. \n";
                    if (unfound > 0) {
                        mess += "There are(is) " + unfound + " words with un meanings";
                    }
                    multipleone.setText(resetText, TextView.BufferType.EDITABLE);
                    Toast.makeText(QuickNote.this, mess, Toast.LENGTH_LONG).show();
                }

            }
                    break;
                case R.id.floatingActionButton13://back to home
                   endAndHozon(1);
                    break;
                case R.id.floatingActionButton14://search
                   endAndHozon(0);
                    break;
                case R.id.floatingActionButton12://back to the memolist
                    endAndHozon(3);
                    break;

        }
        }
    }
}