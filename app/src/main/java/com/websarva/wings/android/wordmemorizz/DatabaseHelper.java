package com.websarva.wings.android.wordmemorizz;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="wordlist.db";
    private static final String COLUMN_ID = "_id";
    private static final String TABLE_NAME = "memo";
    private static final String TABLE_NAME_2="words";
    private static final String TABLE_NAME_3="wordlist";
    private Retrofit retrofit;
   // private static final String DATABASE_NAME2="words.db";
    private static final int DATABASE_VERSION=1;
   // private static final int DATABASE_VERSION2=1;
   private Context context;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
       // super(context, DATABASE_NAME2,null,DATABASE_VERSION2);

    }
    @Override
    public void onCreate(SQLiteDatabase db){
        StringBuilder sb=new StringBuilder();
        StringBuilder sb2=new StringBuilder();
        StringBuilder sb3=new StringBuilder();

        sb.append("CREATE TABLE wordlist (");
        sb.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append("listName TEXT,");
        sb.append("wordsNum INTEGER,");
        sb.append("testdays TEXT,");
        sb.append("torokuBi TEXT,");
        sb.append("time TEXT,");
        sb.append("global_id TEXT DEFAULT '' ,");
        sb.append("sync_stats_1 INTEGER DEFAULT 0,");
        sb.append("sync_stats_2 INTEGER DEFAULT 0,");
        sb.append("timestamp DATETIME DEFAULT CURRENT_TIMESTAMP");
        sb.append(");");//何曜日にテストが起こるか文字列で0と1の文字列で表す。長さ7

        sb2.append("CREATE TABLE words (");
        sb2.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb2.append("parent_id INTEGER,");
        sb2.append("correctRate INTEGER,");//初期値0
        sb2.append("entire INTEGER,");//initially it's 0
        sb2.append("name TEXT,");
        sb2.append("meaning TEXT,");//note TEXT for example sentences
        sb2.append("result TEXT,");
        sb2.append("listTitle TEXT,");
        sb2.append("sentence TEXT,");//testで間違えた日にちを記録したい
        sb2.append("global_id TEXT DEFAULT '' ,");
        sb2.append("sync_stats_1 INTEGER DEFAULT 0,");
        sb2.append("sync_stats_2 INTEGER DEFAULT 0,");
        sb2.append("timestamp DATETIME DEFAULT CURRENT_TIMESTAMP");
        sb2.append(");");

        sb3.append("CREATE TABLE "+TABLE_NAME+" (");
        sb3.append(COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb3.append("content TEXT,");
        sb3.append("timestamp DATETIME DEFAULT CURRENT_TIMESTAMP");
        sb3.append(");");

        String sql=sb.toString();
        String sql2=sb2.toString();
        String sql3=sb3.toString();

        db.execSQL(sql);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion){

    }

//user の　入れ替え
    public boolean deleteAllDataWithWarning(int targetValue,String column_nom) {

      //  String column_nom = "sync_stats_2";
      //  int targetValue = 1;
        boolean letsgo = true;
        //String[] listnames ={"words","wordlist"};

        String[] listnames = new String[2];
        listnames[0] = "words";
        listnames[1] = "wordlist";

        for(int i = 0; i < 2; i++){

        SQLiteDatabase db = this.getWritableDatabase();

        // 特定のコラムの値が特定の整数である行があるか確認
        Cursor cursor = db.rawQuery("SELECT * FROM " + listnames[i] + " WHERE " + column_nom + " = ?", new String[]{String.valueOf(targetValue)});

        if (cursor.getCount() > 0) {
            // 警告メッセージを出力
            //Log.warning("特定のコラムが特定の値である行があります. Please re sync");

            // ここでユーザーに対して警告を表示するか、適切な処理を行う
           // OrderConfirmDialogFragment3 warningDialog = new OrderConfirmDialogFragment3();

            // Pass the warning message to the dialog using arguments

            // Show the dialog using the context passed to the method
          //  warningDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "WarningDialog");

          //  return false;
            letsgo = false;
        }else{
            db.execSQL("DELETE FROM " + listnames[i]);
        }

        // データを削除
       // db.execSQL("DELETE FROM " + listnames[i]);

        cursor.close();
        db.close();

    }
      return letsgo;
    }

    public long insertData(String databasetile,Object data[]) {//worddetail, wordadd, memo
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        switch (databasetile) {
            case TABLE_NAME://memo
                values.put("content", (String) data[0]);
                long insertedRowId = db.insert(TABLE_NAME, null, values);
                break;
            case TABLE_NAME_2://words   //data :meaning sentence parent_id listTitle name
                values.put("meaning", (String) data[0]);//meaning
                values.put("sentence", (String) data[1]);//sentence
                values.put("parent_id", (long) data[2]);
                values.put("listTitle", (String) data[3]);
                values.put("correctRate", 0);
                values.put("entire", 0);
                values.put("name", (String) data[4]);
                values.put("result", "33333");
                long insertedRowId2 = db.insert(TABLE_NAME_2, null, values);
                //単語数更新
                //Object[] abc={TABLE_NAME_3,this.getRowCount(TABLE_NAME_2)};
                //!!!!!!!!!!!!!!!!!!!!!!!!!全部の単語数を調べているが特定のリスト内の単語のみを調べたい!!!!!!!!!!!!!
                Object[] abc = {"wordsNum=" + this.countRowsWithParentId((long) data[2])};
                this.updateData((long) data[2], abc, TABLE_NAME_3);

                break;
            case TABLE_NAME_3://lists
                LocalDate currentDate = LocalDate.now();
                String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            /*    stmt.bindString(2,output);
                stmt.bindLong(3,0);
                stmt.bindString(4,"00000000");
                stmt.bindString(5,formattedDate);
                stmt.bindString(6,"0000");*/
                values.put("listName", (String) data[0]);//meaning
                values.put("wordsNum", 0);//sentence
                values.put("testdays", "00000000");
                values.put("torokuBi", formattedDate);
                values.put("time", "0000");

                if(data.length >= 2) {
                    values.put("global_id", (String) data[1]);
                }
                /*values.put("entire",0);
                values.put("name",(String)data[4]);*/


                long insertedRowId3 = db.insert(TABLE_NAME_3, null, values);
                break;
        }
        // Replace "column_name" with the actual column name.
        //long insertedRowId = db.insert(TABLE_NAME, null, values); // Replace "your_table_name" with the actual table name.

        db.close();

        //ここでlog in　and connected to net なら
        //if(WordApp.isLoggedIn)
      /*  Object[] resulty = this.userloggedin();
        if ((boolean) resulty[0]) {//loggedin しているか

            List<WordData> dataList = new ArrayList<>();
            dataList.add(new WordData((String) data[4], (String) data[0], (String) data[1], (String) data[3], (long) data[2],(String)resulty[1]));
            ConnectToServer(1, dataList);
        }*/
        WordApp wordApp = WordApp.getInstance();
        if(!wordApp.getBehindWork()){
          //  wordApp.setBehindWorkOn(true);
            //behindwork起動!
            //wordApp.urakata(this,)
        }


        long insertedRowId2 = this.getLastInsertedItemId(databasetile);
        return insertedRowId2;
    }

    public long insertData2(String databasetile,Object data[]) {//worddetail, wordadd, memo
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        switch (databasetile) {
            case TABLE_NAME://memo
                values.put("content", (String) data[0]);
                long insertedRowId = db.insert(TABLE_NAME, null, values);
                break;
            case TABLE_NAME_2://words   //data :meaning sentence parent_id listTitle name
                values.put("meaning", (String) data[0]);//meaning
                values.put("sentence", (String) data[1]);//sentence
                values.put("parent_id", (long) data[2]);
                values.put("listTitle", (String) data[3]);
                values.put("correctRate", (int)data[5]);
                values.put("entire", (int) data[6]);
                values.put("name", (String) data[4]);
                values.put("_id",(long)data[8]);

                values.put("result", (String) data[7]);
                values.put("sync_stats_2",1);
                long insertedRowId2 = db.insert(TABLE_NAME_2, null, values);
                //単語数更新
                //Object[] abc={TABLE_NAME_3,this.getRowCount(TABLE_NAME_2)};
                //!!!!!!!!!!!!!!!!!!!!!!!!!全部の単語数を調べているが特定のリスト内の単語のみを調べたい!!!!!!!!!!!!!
                Object[] abc = {"wordsNum=" + this.countRowsWithParentId((long) data[2])};
                this.updateData((long) data[2], abc, TABLE_NAME_3);

                break;
            case TABLE_NAME_3://lists
                LocalDate currentDate = LocalDate.now();
                String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            /*    stmt.bindString(2,output);
                stmt.bindLong(3,0);
                stmt.bindString(4,"00000000");
                stmt.bindString(5,formattedDate);
                stmt.bindString(6,"0000");*/
                values.put("listName", (String) data[0]);//meaning
                values.put("wordsNum", 0);//sentence
                values.put("testdays", "00000000");
                values.put("torokuBi", formattedDate);
                values.put("_id",(long) data[1]);
                values.put("time", "0000");
                values.put("sync_stats_2",1);

                /*values.put("entire",0);
                values.put("name",(String)data[4]);*/


                long insertedRowId3 = db.insert(TABLE_NAME_3, null, values);
                break;
        }
        // Replace "column_name" with the actual column name.
        //long insertedRowId = db.insert(TABLE_NAME, null, values); // Replace "your_table_name" with the actual table name.

        db.close();

        //ここでlog in　and connected to net なら
        //if(WordApp.isLoggedIn)
      /*  Object[] resulty = this.userloggedin();
        if ((boolean) resulty[0]) {//loggedin しているか

            List<WordData> dataList = new ArrayList<>();
            dataList.add(new WordData((String) data[4], (String) data[0], (String) data[1], (String) data[3], (long) data[2],(String)resulty[1]));
            ConnectToServer(1, dataList);
        }*/
        WordApp wordApp = WordApp.getInstance();
        if(!wordApp.getBehindWork()){
            //  wordApp.setBehindWorkOn(true);
            //behindwork起動!
            //wordApp.urakata(this,)
        }


        long insertedRowId2 = this.getLastInsertedItemId(databasetile);
        return insertedRowId2;
    }

    public boolean CheckIfValid(String target/*,long targetId,long idOfTheword*/){
        SQLiteDatabase db=this.getWritableDatabase();

        String stat="SELECT * FROM words WHERE name='"+target+"' LIMIT 1";
        //String stat="SELECT * FROM words WHERE name='"+target+"' AND parent_id="+targetId+" AND _id!="+idOfTheword +" LIMIT 1";
        //LIMIT 1個見つかった時点で終了
        Cursor cursor = db.rawQuery(stat,null);
        int count=0;
        while (cursor.moveToNext()){
            count++;
        }

        if(count>0){
            return false;
        }else{
            return true;
        }
    }

    public void updateData(long rowId, Object[] newValue,String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        switch (tablename){
            case TABLE_NAME://memo
                values.put("content", (String)newValue[0]);
                break;
            case TABLE_NAME_2://words

                for(int a = 0;a< newValue.length;a++){
                    String[] parts = newValue[a].toString().split("=");
                    if (parts.length == 2) {
                        String firstPart = parts[0];
                        String secondPart = parts[1];
                        if(firstPart.equals("entire")||firstPart.equals("correctRate")
                                ||firstPart.equals("sync_stats_1")||firstPart.equals("sync_stats_2")
                                ) {
                            values.put(firstPart, Integer.valueOf(secondPart));
                        }/*else if(firstPart.equals("global_id")){


                        }*/else{
                            values.put(firstPart,secondPart);
                           // Log.d("ohgod",secondPart);
                        }
                     //timestampもupdate
                    }/* else if(parts.length == 4){

                        String ver = parts[2];
                        String lastpart = parts[3];

                        if(ver.equals("1")){
                            values.put("sync_stats_1",Integer.valueOf(lastpart));
                        }else if(ver.equals("2")){
                            values.put("sync_stats_2",Integer.valueOf(lastpart));
                        }

                    }  else if(parts.length == 3){//globalId

                        String lastpart = parts[2];

                        values.put("global_id",Integer.valueOf(lastpart));//longにしなきゃ
                    }*//*else {
                        System.out.println("Input word doesn't contain the delimiter.");
                    }*/
                }

                break;
            case TABLE_NAME_3://wordlist
                /*values.put("listName",(String)newValue[0]);
                values.put("wordsNum", (String)newValue[1]);*/
                for(int a=0;a< newValue.length;a++){
                    String[] parts = newValue[a].toString().split("=");//ここ=にした方が絶対よい
                    if (parts.length == 2) {
                        String firstPart = parts[0];
                        String secondPart = parts[1];
                       if(firstPart.equals("wordsNum")
                               ||firstPart.equals("sync_stats_1")
                               ||firstPart.equals("sync_stats_2")/*||firstPart.equals("global_id")*/
                       ){
                            values.put(firstPart,Integer.valueOf(secondPart));
                        }else{
                            values.put(firstPart,secondPart);
                        }
                       //timestamp mo update
                    } /* else if(parts.length == 4){

                        String ver = parts[2];
                        String lastpart = parts[3];

                        if(ver.equals("1")){
                            values.put("sync_stats_1",Integer.valueOf(lastpart));
                        }else if(ver.equals("2")){
                            values.put("sync_stats_2",Integer.valueOf(lastpart));
                        }

                    } else if(parts.length == 3){//globalId

                        String lastpart = parts[2];

                        values.put("global_id",Integer.valueOf(lastpart));
                    }*//*else {
                        System.out.println("Input word doesn't contain the delimiter.");
                    }*/
                }

                break;
        }
        //values.put(columnName, newValue);

        String whereClause = COLUMN_ID+" = ?";
        //String whereClause = COLUMN_ID+" = "+rowId;
        String[] whereArgs = {String.valueOf(rowId)};//id in each list

      //  db.update(tablename, values, whereClause, null);
        db.update(tablename, values, whereClause, whereArgs);

        db.close();

      /*  WordApp wordApp = WordApp.getInstance();
        if(!wordApp.getBehindWork()){
          //  wordApp.setBehindWorkOn(true);

            //behindwork起動!
        }*/

        //add to database on server if loggedin
    }


    public int getRowCount(String tablename) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tablename, null);
        int rowCount = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            rowCount = cursor.getInt(0);
            cursor.close();
        }
        return rowCount;
    }

    public List<Map<String, Object>> newDataAdded(List<Long> idArray){
        Cursor cursor = this.getWordsWithIds(idArray);

        List<Map<String, Object>> result=new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex("_id");
            int wordColumnIndex = cursor.getColumnIndex("name");
            int meaningColumnIndex = cursor.getColumnIndex("meaning");

            do {
                // Check if the columns exist in the cursor
                if (idColumnIndex != -1 && wordColumnIndex != -1) {
                    // Access data from the cursor
                    long id = cursor.getLong(idColumnIndex);
                    String word = cursor.getString(wordColumnIndex);
                    String meaning=cursor.getString(meaningColumnIndex);

                    // Check for null values
                    if (word != null) {
                        Map<String, Object> yeah=new HashMap<>();
                        yeah.put("word",word);
                        yeah.put("meaning",meaning);
                        yeah.put("id",id);

                        result.add(yeah);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        this.close();

        return result;

      //databse閉じる必要ある!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    public Cursor getWordsWithIds(List<Long> idList) {
        // Create a comma-separated string of the Long values
        StringBuilder idListStr = new StringBuilder();
        for (int i = 0; i < idList.size(); i++) {
            idListStr.append(idList.get(i));
            if (i < idList.size() - 1) {
                idListStr.append(", ");
            }
        }

        // Create the SQL query with the IN clause
        String query = "SELECT * FROM words WHERE _id IN (" + idListStr.toString() + ")";

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public long getlistWithGlobalIds(String global_id) {
        // Create a comma-separated string of the Long values

        // Create the SQL query with the IN clause
        String query = "SELECT * FROM wordlist WHERE global_id = ? ";
        String[] selectionArgs = {global_id};

        SQLiteDatabase db = this.getReadableDatabase();

        long list_local_id = 0;

        try{

          Cursor cursor =  db.rawQuery(query, selectionArgs);
          if(cursor != null){
              if(cursor.moveToFirst()){
                  do{
                     int idx = cursor.getColumnIndex("_id");
                     list_local_id = cursor.getLong(idx);
                  }while(cursor.moveToNext());
              }

              cursor.close();
          }

        }catch(Exception e){
           list_local_id = -1;
        }
      return list_local_id;
    }

    public JSONArray getDataBetweenTimestamps(String userId,String listname) {//add update をjsonarrayにいれてserverにおくる
        JSONArray jsonArray = new JSONArray();

        Log.d("usernmae in getdata",userId);
        Log.d("usernmae in getdata",listname);

        String query;


        if(listname.equals("words")){
            query = "SELECT * FROM words WHERE sync_stats_2 = 0";

        }else if(listname.equals("wordlist")){
            query = "SELECT * FROM wordlist WHERE sync_stats_2 = 0";
        }else{
            query = "SELECT * FROM wordlist WHERE sync_stats_2 = 0";
        }

        Log.d("rose","haaaa");

       // String[] selectionArgs = {String.valueOf(0)};

        SQLiteDatabase db = this.getReadableDatabase();

        Log.d("rose","haaaa2");
        try {
            Cursor cursor = db.rawQuery(query, null);
            Log.d("rose","haaaa3");
            if (cursor != null) {
                Log.d("rose","haaaa4");
                if (cursor.moveToFirst()) {
                    Log.d("rose","haaaa5");
                    do {
                        Log.d("rose","haaaa6");
                        try {

                            Log.d("rose","haaaa7");
                            JSONObject jsonObject = new JSONObject();

                            long iddd = -1;

                            if(listname.equals("words")) {
                                Log.d("rose","haaaa8");
                                int nameIdx = cursor.getColumnIndex("name");
                                String name = cursor.getString(nameIdx);
                                Log.d("india Cursor Values", "Name: " + name);
                                jsonObject.put("name", name);

                                int meaningIdx = cursor.getColumnIndex("meaning");
                                String meaning = cursor.getString(meaningIdx);
                                Log.d("india Cursor Values", "Meaning: " + meaning);
                                jsonObject.put("meaning", meaning);

                                int sentenceIdx = cursor.getColumnIndex("sentence");
                                String sentence = cursor.getString(sentenceIdx);
                                Log.d("india Cursor Values", "Sentence: " + sentence);
                                jsonObject.put("sentence", sentence);

                                int resultIdx = cursor.getColumnIndex("result");
                                String result = cursor.getString(resultIdx);
                                Log.d("india Cursor Values", "Result: " + result);
                                jsonObject.put("result", result);

                                int listTitleIdx = cursor.getColumnIndex("listTitle");
                                String listTitle = cursor.getString(listTitleIdx);
                                Log.d("india Cursor Values", "List Title: " + listTitle);
                                jsonObject.put("listTitle", listTitle);

                                int correctRateIdx = cursor.getColumnIndex("correctRate");
                                int correctRate = cursor.getInt(correctRateIdx);
                                Log.d("india Cursor Values", "Correct Rate: " + correctRate);
                                jsonObject.put("correctRate", correctRate);

                                int entireIdx = cursor.getColumnIndex("entire");
                                int entire = cursor.getInt(entireIdx);
                                Log.d("india Cursor Values", "Entire: " + entire);
                                jsonObject.put("entire", entire);

                                int parentIdIdx = cursor.getColumnIndex("parent_id");
                                long parentId = cursor.getLong(parentIdIdx);
                                Log.d("india Cursor Values", "Parent ID: " + parentId);
                                jsonObject.put("parent_id", parentId);

                                int idIdx = cursor.getColumnIndex("_id");
                                iddd = cursor.getLong(idIdx);
                                Log.d("india Cursor Values", "Local ID: " + iddd);
                                jsonObject.put("local_id", iddd);

                                int idxx = cursor.getColumnIndex("global_id");
                                String globalId = cursor.getString(idxx);
                                Log.d("india Cursor Values", "Global ID: " + globalId);
                                jsonObject.put("global_id", globalId);


                                /*

                                int nameIdx = cursor.getColumnIndex("name");
                                String name = cursor.getString(nameIdx);

                                jsonObject.put("name", name);

                                int meaningIdx = cursor.getColumnIndex("meaning");
                                String meaning = cursor.getString(meaningIdx);

                                jsonObject.put("meaning", meaning);

                                int sentenceIdx = cursor.getColumnIndex("sentence");
                                String sentence = cursor.getString(sentenceIdx);

                                jsonObject.put("sentence", sentence);

                                int resultIdx = cursor.getColumnIndex("result");
                                String result = cursor.getString(resultIdx);

                                jsonObject.put("result", result);
                                // Get values from the cursor for each column

                                int listTitleIdx = cursor.getColumnIndex("listTitle");
                                String listTitle = cursor.getString(listTitleIdx);

                                jsonObject.put("listTitle", listTitle);

                                int correctRateIdx = cursor.getColumnIndex("correctRate");
                                int correctRate = cursor.getInt(correctRateIdx);
                                jsonObject.put("correctRate", correctRate);

                                int entireIdx = cursor.getColumnIndex("entire");
                                int entire = cursor.getInt(entireIdx);
                                jsonObject.put("entire", entire);

                                int parentIdIdx = cursor.getColumnIndex("parent_id");
                                long parentId = cursor.getLong(parentIdIdx);
                                jsonObject.put("parent_id", parentId);

                                //parent_id はlocalかglobalか?

                                int idIdx = cursor.getColumnIndex("_id");
                                iddd = cursor.getLong(idIdx);
                                jsonObject.put("local_id", iddd);

                                //word_global_id -> serverでせっててい
                                int idxx = cursor.getColumnIndex("global_id");
                                String globalId = cursor.getString(idxx);
                                jsonObject.put("global_id", globalId); */

                                //word_userId ->

                            }else if(listname.equals("wordlist")){

                                Log.d("rose","haaaa9");
                                int nameIdx = cursor.getColumnIndex("listName");
                                String name = cursor.getString(nameIdx);
                                Log.d("india Cursor Values", "List Name: " + name);
                                jsonObject.put("listName", name);

                                int meaningIdx = cursor.getColumnIndex("global_id");
                                String meaning = cursor.getString(meaningIdx);
                                Log.d("india Cursor Values", "Global ID: " + meaning);
                                jsonObject.put("global_id", meaning);

                                int sentenceIdx = cursor.getColumnIndex("_id");
                                iddd = cursor.getLong(sentenceIdx);
                                Log.d("india Cursor Values", "Local ID: " + iddd);
                                jsonObject.put("local_id", iddd);

                                int testdaysIdx = cursor.getColumnIndex("testdays");
                                String testdays = cursor.getString(testdaysIdx);
                                Log.d("india Cursor Values", "Test Days: " + testdays);
                                jsonObject.put("testdays", testdays);

                                int torokuBiIdx = cursor.getColumnIndex("torokuBi");
                                String torokubi = cursor.getString(torokuBiIdx);
                                Log.d("india Cursor Values", "Toroku Bi: " + torokubi);
                                jsonObject.put("torokuBi", torokubi);

                                int wordsNumIdx = cursor.getColumnIndex("wordsNum");
                                int wordsNum = cursor.getInt(wordsNumIdx);
                                Log.d("india Cursor Values", "Words Num: " + wordsNum);
                                jsonObject.put("wordsNum", wordsNum);

                                /*
                                int nameIdx = cursor.getColumnIndex("listName");
                                String name = cursor.getString(nameIdx);
                                jsonObject.put("listName", name);

                                int meaningIdx = cursor.getColumnIndex("global_id");
                                String meaning = cursor.getString(meaningIdx);
                                jsonObject.put("global_id", meaning);

                                int sentenceIdx = cursor.getColumnIndex("_id");
                                iddd = cursor.getLong(sentenceIdx);
                                jsonObject.put("local_id", iddd);

                                int testdaysIdx = cursor.getColumnIndex("testdays");
                                String testdays = cursor.getString(testdaysIdx);
                                jsonObject.put("testdays", testdays);

                                int torokuBiIdx = cursor.getColumnIndex("torokuBi");
                                String torokubi = cursor.getString(torokuBiIdx);
                                jsonObject.put("torokuBi", torokubi);

                                int wordsNumIdx = cursor.getColumnIndex("wordsNum");
                                int wordsNum = cursor.getInt(wordsNumIdx);
                                jsonObject.put("wordsNum", wordsNum);*/
                            }

                            Log.d("rose","haaaa10");

                            jsonObject.put("userId", userId);

                            int timestampidx = cursor.getColumnIndex("timestamp");
                            String timerecord = cursor.getString(timestampidx);
                            jsonObject.put("timestamp", timerecord);


                            int syncStats1Idx = cursor.getColumnIndex("sync_stats_1");
                            int syncStats1 = cursor.getInt(syncStats1Idx);
                            jsonObject.put("sync_stats_1", syncStats1);

                            int syncStats2Idx = cursor.getColumnIndex("sync_stats_2");
                            int syncStats2 = cursor.getInt(syncStats2Idx);
                            jsonObject.put("sync_stats_2", syncStats2);

                            jsonArray.put(jsonObject);

                            Object updateing[] = {"sync_stats_2=2"};//sync_stats_1 wo 2 ni
                            this.updateData(iddd, updateing, listname);

                            Log.d("rose","haaaa11");

                        } catch (JSONException e) {
                            Log.d("rose","haaaa12");
                            Log.e("JSON Error", "Error creating JSON object");
                        }
                    } while (cursor.moveToNext());
                    Log.d("rose","haaaa13");
                }
                cursor.close();
                Log.d("rose","haaaa14");
            }
        } catch (Exception e) {
            Log.d("rose","haaaa15");
            Log.d("Database Error mood", "Error querying database");
        }

        Log.d("rose","haaaa16");
        String jsonString = jsonArray.toString();

// Log the string
        Log.d("JSONArray1234", jsonString);


        return jsonArray;
    }


    //あるリスト内の数を調べるやつも必要!!!!!!!!!!!!!
    public int countRowsWithParentId(long parentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"_id"};
        String selection = "parent_id = ?";
        String[] selectionArgs = {String.valueOf(parentId)};

        Cursor cursor = db.query(
                TABLE_NAME_2,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }

    public long getLastInsertedItemId(String databasetitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_ID + ") FROM " + databasetitle, null);

        long lastInsertedId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            lastInsertedId = cursor.getLong(0);
            cursor.close();
        }

        db.close();

        return lastInsertedId;
    }
    public void deleteItem(long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(itemId)};
        db.delete(TABLE_NAME_2, selection, selectionArgs);
        db.close();
    }
    public Cursor getRowsWithWord(String word,String column_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        word = word.replace("'", "''");
        String selection = column_name + " LIKE '%" + word + "%'";
        return db.query(TABLE_NAME_2, null, selection, null, null, null, null);
    }
    public class WordData {
        private String name;
        private String meaning;
        private String sentence;
        private String listtitle;
        private long listid;
        private long userid;

        public WordData(String name, String meaning, String sentence, String listtitle,long listid,String username) {
            this.name = name;
            this.meaning = meaning;
            this.sentence = sentence;
            this.listtitle = listtitle;
            this.listid = listid;
            this.userid = 1;
        }
        // Add getters and setters as needed
    }

    private Object[] userloggedin(){
        WordApp wordApp = WordApp.getInstance();

        if(wordApp.isLoggedIn()){
           Object[] databacky = {true,wordApp.user.getterName()};
           return databacky;
        }else{
           Object[] databacky = {false};
           return databacky;
        }

    }

    private void ConnectToServer(int ver,List<WordData> datalist){//もしloginしてるand net connected なら
        retrofit = RetrofitClient.getClient();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<ApiResponse> call;

        switch (ver){
            case 0:
                call = apiService.getProtectedData();
                break;
            case 1:
                call = apiService.addwords(datalist);
                break;
            default:
                call = apiService.getProtectedData();
        }

        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");



// Set the "Authorization" header with the token
       call.request().newBuilder()
                .addHeader("Authorization", token/*token*/)
                .build();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response here
                    ApiResponse apiResponse = response.body();
                    // ...
                } else {
                    // Handle the error response here
                    // ...
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle network or server error here
                // ...
            }
    });
    }

}
