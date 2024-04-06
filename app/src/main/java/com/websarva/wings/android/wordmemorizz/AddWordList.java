package com.websarva.wings.android.wordmemorizz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddWordList extends AppCompatActivity {

    private DatabaseHelper _helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word_list);

        Button button2=findViewById(R.id.button2);
        Button button3=findViewById(R.id.button3);
        Clickk lisner=new Clickk();
        button2.setOnClickListener(lisner);
        button3.setOnClickListener(lisner);

        Intent intent=getIntent();

        _helper=new DatabaseHelper(AddWordList.this);

    }
    @Override
    protected void onDestroy(){
        _helper.close();
        super.onDestroy();
    }

    private class Clickk implements View.OnClickListener{
        @Override
        public void onClick(View view){
            int id=view.getId();

            switch (id) {
                //add a word list
                case R.id.button2:
                    EditText input = findViewById(R.id.editTextTextPersonName);
                    String output = input.getText().toString();

                    //add the data to sql database
                    if (output.length() > 0) {// and there's no identical list name
                        Object[] qwe={output};
                       long backid=_helper.insertData("wordlist",qwe);

                        if(((WordApp)getApplication()).getSync()&&!((WordApp)getApplication()).getBehindWork()) {
                            ((WordApp) getApplication()).urakataStart(getApplication());
                        }

                   /*    SQLiteDatabase db=_helper.getWritableDatabase();


                        String sqlInsert="INSERT INTO wordlist (_id,listName,wordsNum,testdays,torokuBi,time) VALUES(?,?,?,?,?,?)";

                        LocalDate currentDate = LocalDate.now();

                        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                        SQLiteStatement stmt=db.compileStatement(sqlInsert);*/
                        //int wanton=1;
                        //stmt.bindLong(1,2);
                    /*    stmt.bindString(2,output);
                        stmt.bindLong(3,0);
                        stmt.bindString(4,"00000000");
                        stmt.bindString(5,formattedDate);
                        stmt.bindString(6,"0000");*/
                      //  stmt.bindString(7,"33333");
                      //  stmt.executeInsert();

                     /*  SQLiteDatabase db2=_helper.getWritableDatabase();

                     String sql2= "SELECT * FROM wordlist WHERE rowid=last_insert_rowid()";   //listName ="+output;

                       Cursor cursor=db2.rawQuery(sql2, null);

                      int numOfList=0;

                        while(cursor.moveToNext()){
                          int  nu=cursor.getColumnIndex("_id");
                          numOfList=cursor.getInt(nu)  ;
                            Log.i("looping","yeah");
                        }*/

                        Intent intent2 = new Intent(AddWordList.this, WordList.class);
                        intent2.putExtra("wordTitle", output);
                        intent2.putExtra("idNum",backid);
                        intent2.putExtra("mode",0);

                        startActivity(intent2);
                        finish();
                    }else{
                       OrderConfirmDialogFragment dialogFragment=new OrderConfirmDialogFragment();
                       dialogFragment.show(getSupportFragmentManager(),"OrderConfirmDialogFragment");
                    }

                    break;
                //cancel
                case R.id.button3:
                    finish();
                    break;
            }
        }
    }


}