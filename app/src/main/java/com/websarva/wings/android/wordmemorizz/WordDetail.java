package com.websarva.wings.android.wordmemorizz;
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!FATAL ERROR!!!!!!!!!!!!!!!!!!!!!!!
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//error !!!!!!!!!!!!listにて単語追加をした後その単語をくりっくすると違う単語が現れる!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class WordDetail extends AppCompatActivity {

    private DatabaseHelper _helper;
    private TextToSpeech tts;

    RecyclerView lvMenu2;
    RecyclerListAdapter adapter;
    LinearLayoutManager layout;
    List<Map<String, Object>> menuList;

    List<Map<String, Object>> newwords;

    List<Long> newids;
    List<Long> plusNewIds;//=new ArrayList<>();

    List<Long> changedWordIds;
    //List<Long> pluschangedWordIds;
    //List<Long> sendids;

    private String namey="";
    private String meaningg="";
    private String sentence="Not registered";
    private long idd=-1;
    private int pos=-1;

    private int validLen=2;

    private long listId=-1;
    private String listtitle="";
    private int mode=-1;

    TextView titlel;
    TextView meaning;
    TextView exsen;
    //Button buttony;

   // private String[] explanation;



    //private MyViewModel viewModel;

    private boolean edited=false;

    ActivityResultLauncher<Intent> resultLauncher2=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent intenty=result.getData();
                        if(intenty!=null) {
                            int fromwhere=intenty.getIntExtra("fromwhere",0);
                            switch (fromwhere) {
                                case 0://単語編集から

                            namey = intenty.getStringExtra("adddata");
                            meaningg = intenty.getStringExtra("adddata2");
                            sentence = intenty.getStringExtra("adddata3");
                            if (titlel != null && meaning != null) {
                                titlel.setText(namey);
                                // meaning.setText(meaningg+" "+sentence);

                                //breakdown(meaningg + " " + sentence);
                                edited = true;
                                machineStart();
                            }
                                    break;
                                case 1://別の単語のdetailから
                                    if (intenty.hasExtra("longList")/*&&mody==6*/) {
                                        // Retrieve the List of Long values from the Intent
                                        Log.d("fredagainn","yeah");
                                        //plusNewIds=new ArrayList<>();
                                        //plusNewIds= (List<Long>)intenty.getSerializableExtra("longList");
                                        newids.addAll((List<Long>)intenty.getSerializableExtra("longList"));
                                        Log.d("fredagain","yeah");


                                        // Now, 'receivedLongList' contains the passed List of Long values
                                    }
                                    if(intenty.hasExtra("editedId")){
                                        //long leId=intenty.getLongExtra("editedId",-1);
                                       //changedWordIds.add(leId);
                                        changedWordIds.addAll((List<Long>)intenty.getSerializableExtra("editedId"));
                                    }
                                    machineStart();

                                    break;


                        }
                        }

                    }
                }
            }
    );

   /* ActivityResultLauncher<Intent> resultLauncher3=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent intenty=result.getData();
                        if(intenty!=null){


                        }

                    }
                }
            }
    );*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        //listからかsearchから判断する必要あり。
        //menuList=new ArrayList<>();
        newwords=new ArrayList<>();
        newids=new ArrayList<>();

        changedWordIds=new ArrayList<>();

        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
        //if there are words user don't know and exist in lists, display them in lists.

        ArrayList<View> viewsToFadeIn = new ArrayList<View>();

        FloatingActionButton fab=findViewById(R.id.floatingActionButton2);
        WordDetail.Clicked lis=new WordDetail.Clicked();

        FloatingActionButton fab2=findViewById(R.id.floatingActionButton8);
        FloatingActionButton fab3=findViewById(R.id.floatingActionButton9);

        viewsToFadeIn.add(fab);
        viewsToFadeIn.add(fab2);
        viewsToFadeIn.add(fab3);

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


        Intent intent=getIntent();
        String title=intent.getStringExtra("word");
        String imi=intent.getStringExtra("imi");
        listId=intent.getLongExtra("listid",-1);
        listtitle=intent.getStringExtra("listTitle");
        mode=intent.getIntExtra("fromWhere",-1);//1:search list   0:normal list kara 2:worddetailのlist kara
        idd=intent.getLongExtra("id",-1);
        pos=intent.getIntExtra("pos",-1);




        //buttony=findViewById(R.id.button11);
        //buttony.setOnClickListener(lis);

        switch (mode){
            case 1:
                break;
            case 0:
            case 2:
                fab2.setImageResource(R.drawable.ic_baseline_west_24);
                break;
        }



        _helper=new DatabaseHelper(WordDetail.this);

        SQLiteDatabase dbb = _helper.getWritableDatabase();

        String fuck = "SELECT * FROM words WHERE _id=" + idd;
        Cursor cursor = dbb.rawQuery(fuck, null);



        while (cursor.moveToNext()) {//its length must be one, otherwise fuck
            int idxNote = cursor.getColumnIndex("name");//楯列
            int idxNote2 = cursor.getColumnIndex("meaning");
            int idxNote3= cursor.getColumnIndex("sentence");
            //listId = cursor.getInt(idxNo);
            namey = cursor.getString(idxNote);
            meaningg = cursor.getString(idxNote2);
            /*String calvin*/sentence = cursor.getString(idxNote3);
            /*if(calvin.length()>=1){
                sentence=calvin;
            }*/
        }
        //  !!!!!!!なぜか一部の単語の詳細がTextviewになるうぜええなおい

        titlel=findViewById(R.id.textView3);
        meaning=findViewById(R.id.textView9);
        exsen=findViewById(R.id.textView17);

        titlel.setText(namey);
     //   meaning.setText(meaningg);
     //   exsen.setText(sentence);
       // titlel.setText(namey);
      //  meaning.setText(meaningg);
      //  exsen.setText(sentence);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.machineStart();

        //編集削除etc... 音声読み上げ機能も付ける


}
private void machineStart(){
    //区切り線のリセット
    RecyclerView recyclerView = findViewById(R.id.lvM);

    DividerItemDecoration existingDecoration = getExistingDividerItemDecoration(recyclerView);

    if (existingDecoration != null) {
        recyclerView.removeItemDecoration(existingDecoration);
    }
    breakdown(meaningg+" "+sentence);
    meaning.setTextIsSelectable(true);
    exsen.setTextIsSelectable(true);
}
    private DividerItemDecoration getExistingDividerItemDecoration(RecyclerView recyclerView) {
        for (int i = 0; i < recyclerView.getItemDecorationCount(); i++) {
            RecyclerView.ItemDecoration itemDecoration = recyclerView.getItemDecorationAt(i);
            if (itemDecoration instanceof DividerItemDecoration) {
                return (DividerItemDecoration) itemDecoration;
            }
        }
        return null;
    }

private void breakdown(String aqz){
    String[] explanation=aqz.trim().split(" ");
    int leen= explanation.length;
    for(int q=0;q< explanation.length;q++){
        explanation[q]=explanation[q].replaceAll("[,;.)(]", "").trim();
        if(explanation[q].length()<=validLen/*||explanation[q].equals(namey)*/){
            leen--;
        }
    }

    if(leen>=1){
        String[] anotherr=new String[leen];
        int fredAgain=0;
        for(int q=0;q< explanation.length;q++){
            if(explanation[q].length()>validLen/*&&!explanation[q].equals(namey)*/){
                anotherr[fredAgain]=explanation[q];
                fredAgain++;
            }
        }
        ListSetUpp(anotherr);
    }else{
        meaning.setText(meaningg);
        exsen.setText("Ex) "+sentence);
    }

}

    @Override
    protected void onDestroy(){
        _helper.close();
        super.onDestroy();
    }
    private class RecyclerListViewHolder extends RecyclerView.ViewHolder{
        public TextView _tvMenuWordRow;
        public TextView _tvMenuMeaningRow;
        public LinearLayout _onrRow;
        public CardView _cardview;

        public RecyclerListViewHolder(View itemView){
            super(itemView);
            _tvMenuWordRow=itemView.findViewById(R.id.nom);
            _tvMenuMeaningRow=itemView.findViewById(R.id.meaningy);
            _onrRow=itemView.findViewById(R.id.linearCAr_1);
           // _cardview=itemView.findViewById(R.id.cadiBB);
        }


    }

    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder>{
        private List<Map<String, Object>> _listData;
        public RecyclerListAdapter(List<Map<String, Object>> listData){
            _listData = listData;
        }
        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater=LayoutInflater.from(WordDetail.this);
            View view = inflater.inflate(R.layout.simple_item4, parent, false);

            view.setOnClickListener(new ItemClickListener());
        //    view.setOnLongClickListener(new MainActivity.ItemLongClickListener());

            RecyclerListViewHolder holder=new RecyclerListViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(RecyclerListViewHolder holder, int position){
          /*  if(holder._tvMenuMeaningRow.getVisibility()==View.GONE) {
                holder._tvMenuMeaningRow.setVisibility(View.VISIBLE);
            }else{
                holder._tvMenuMeaningRow.setVisibility(View.GONE);
            }*/
            //if the position is invisible(means checkIfVisible=0),setText("")
            int lenOf=menuList.size();
            Log.d("23745",String.valueOf(lenOf));
           if(lenOf==position+1){//リストの一番下の要素だったらマージンを500に設定する。
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder._onrRow.getLayoutParams();
                layoutParams.bottomMargin=0;//500
                holder._onrRow.setLayoutParams(layoutParams);
                // layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                //  holder._onrRow.setLayoutParams(layoutParams);

            }else{
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder._onrRow.getLayoutParams();
                layoutParams.bottomMargin=0;
                holder._onrRow.setLayoutParams(layoutParams);
            }

            Map<String, Object> item = _listData.get(position);
            String wordd = (String) item.get("name");
            String meanin = (String) item.get("meaning");
            holder._tvMenuWordRow.setText(wordd);
            holder._tvMenuWordRow.setTextSize(20);
            holder._tvMenuMeaningRow.setText(meanin);
            holder._tvMenuMeaningRow.setTextSize(20);
            //holder._tvMenuMeaningRow.setText(meanin);



            //Color.rgb(a,b,c)


        }
        @Override
        public int getItemCount(){
            return _listData.size();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.word_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal=true;

        int itemId=item.getItemId();

        switch (itemId){
            case R.id.menuListOption3:
               //edit open AddWord
                Intent intent=new Intent(WordDetail.this, AddWord.class);
                intent.putExtra("id",idd);
                intent.putExtra("word",namey);
                intent.putExtra("meaning",meaningg);
                intent.putExtra("pos",pos);
                intent.putExtra("sentence",sentence);
                intent.putExtra("listId",listId);
                intent.putExtra("titlel",listtitle);

                resultLauncher2.launch(intent);

                //startActivity(intent);

                break;
            case R.id.menuListOption4:

                //show dialog to confirm user really wants to delete

                //delete
                _helper.deleteItem(idd);
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!特定のリスト内の単語の数のみ調べたい
                int numberr=_helper.countRowsWithParentId(listId);
                Object[] dataarra={"wordsNum_"+numberr};
                _helper.updateData(listId,dataarra,"wordlist");

                /*_helper=new DatabaseHelper(WordDetail.this);

                SQLiteDatabase dbb = _helper.getWritableDatabase();

                String sqlInsert = "DELETE FROM words WHERE _id=" + idd;



                SQLiteStatement stmt=dbb.compileStatement(sqlInsert);

                stmt.executeUpdateDelete();//update

                SQLiteDatabase db=_helper.getWritableDatabase();*/
               /* try {



                    db.beginTransaction();

                    String forupdates = "UPDATE wordlist SET wordsNum=wordsNum-1 WHERE _id="+listId;
                    SQLiteStatement stmtt = db.compileStatement(forupdates);

                    stmtt.executeUpdateDelete();

                    db.setTransactionSuccessful();
                }catch(Exception e){

                } finally {
                    db.endTransaction();
                }*/





                Intent yeah=new Intent();
                yeah.putExtra("id",idd);
                yeah.putExtra("mode",2);
                yeah.putExtra("adddata","-");
                yeah.putExtra("adddata2","-");
                yeah.putExtra("pos",pos);
                setResult(RESULT_OK,yeah);

                finish();

                //toast


                break;
            case android.R.id.home:
                Log.d("124124122","wegewh");
                modoru();
                break;
        }
        return returnVal;
    }
    private void modoru() {
        Intent backWhereIBelong = new Intent();
        Log.d("12412411",String.valueOf(mode));
        if (mode == 0 || mode == 1) {

            int modee = 4;
            if (edited == true) {
                modee = 3; //everything has changed
                backWhereIBelong.putExtra("id", idd);
                //Log.d("qazxsw",String.valueOf(modee));
            }

            if(!newids.isEmpty()){
                modee = 6;
                backWhereIBelong.putExtra("longList", new ArrayList<>(newids));
            }
            //Log.d("2985792",String.valueOf(changedWordIds.isEmpty()));
            if(!changedWordIds.isEmpty()){
                modee = 6;
                backWhereIBelong.putExtra("editedOnes", new ArrayList<>(changedWordIds));
            }


            backWhereIBelong.putExtra("mode", modee);
            backWhereIBelong.putExtra("adddata", namey);
            backWhereIBelong.putExtra("adddata2", meaningg);

            //setResult(RESULT_OK,backWhereIBelong);
        }else if(mode==2){
            //worddetailに戻る
            //backWhereIBelong.pu
            //新しくintentを政せいするか　or resultbackなんちゃらみたいなのを使うか　どっちか
            if(edited==true){
                //do something
                //idで管理
                changedWordIds.add(idd);
                //backWhereIBelong.putExtra("editedId",idd);

            }
            backWhereIBelong.putExtra("editedId",new ArrayList<>(changedWordIds));
            Log.d("1241241111","wegewh");
            backWhereIBelong.putExtra("fromwhere",1);
            Log.d("1241241111","wegewh");
            if(!newids.isEmpty()){
                backWhereIBelong.putExtra("longList",new ArrayList<>(newids));
            }
            Log.d("1241241","wegewh");
        }
        setResult(RESULT_OK,backWhereIBelong);
        //   backWhereIBelong.putExtra("adddata3",meaningg);
        //ここで追加された単語の情報も入れたい
        // newWord="";
        //  newMeaning="";
        //setResult(RESULT_OK,backWhereIBelong);
        finish();
    }


    private class ItemClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            RecyclerView recy=findViewById(R.id.lvM);
            TextView meaningg=view.findViewById(R.id.meaningy);
            TextView kotob=view.findViewById(R.id.nom);

            int pos = recy.getChildLayoutPosition(view);

            long realId = (long) menuList.get(pos).get("id");
            String meanings=(String) menuList.get(pos).get("meaning");
            String namee=(String) menuList.get(pos).get("name");
            long oyaId=(long) menuList.get(pos).get("parent_id");
            String listtitly=(String) menuList.get(pos).get("listTitle");

            Intent detail = new Intent(WordDetail.this, WordDetail.class);




            detail.putExtra("word",namee);
            detail.putExtra("imi",meanings);
            detail.putExtra("listid",oyaId);
            detail.putExtra("id",realId);
            detail.putExtra("listTitle",listtitly);
            detail.putExtra("fromWhere",2);
            //detail.putExtra("pos",listtitly);
            //detail.putExtra("mode",0);
            //startActivity(detail);
            resultLauncher2.launch(detail);
        }

    }

   /* private void finishActivityAA() {
        WordList activityA = null;
        Activity parentActivity = getParent();
        if (parentActivity instanceof WordList) {
            activityA = (WordList) parentActivity;
        } else if (parentActivity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) parentActivity;
            Fragment parentFragment = fragmentActivity.getSupportFragmentManager().getPrimaryNavigationFragment();
            if (parentFragment instanceof WordList) {
                activityA = (WordList) parentFragment;
            }
        }
        if (activityA != null) {
            activityA.finishWordList();
        }
    }
    private void finishActivityA() {
        WordList activityA = null;
        Activity parentActivity = getParent();
        if (parentActivity instanceof WordList) {
            activityA = (WordList) parentActivity;
        } else if (parentActivity instanceof androidx.appcompat.app.AppCompatActivity) {
            androidx.appcompat.app.AppCompatActivity compatActivity = (androidx.appcompat.app.AppCompatActivity) parentActivity;
            Fragment parentFragment = compatActivity.getSupportFragmentManager().getPrimaryNavigationFragment();
            if (parentFragment instanceof WordList) {
                activityA = (WordList) parentFragment;
            }
        }
        if (activityA != null) {
            activityA.finishWordList();
        }
    }*/
   protected void ListSetUpp(String[] words){
     /*  for(int a=0;a< words.length;a++){
           Log.d("01847","qweR"+words[a]+"Rkfj");
       }*/
       // Log.d("njgf","ererg");
       menuList=new ArrayList<>();
       lvMenu2 = findViewById(R.id.lvM);
       layout = new LinearLayoutManager(WordDetail.this);
       lvMenu2.setLayoutManager(layout);
       // Log.d("njgf","ererg");
       //  ListView lvMenu=findViewById(R.id.lvMenu);

       // menuList=new ArrayList<>();

       Map<String, Object> listMenu=new HashMap<>();
       List<String> stockedWords=new ArrayList<>();
       Log.d("njgfa","ererg");
       //the final goal is to read sql database and add it to m
       for(int a=0;a< words.length;a++){
          String targety=words[a];
           Cursor cursor2 = _helper.getRowsWithWord(targety,"name");
           if (cursor2 != null && cursor2.moveToFirst()) {
               int columnIndex = cursor2.getColumnIndex("name");
               int columnIdex2 = cursor2.getColumnIndex("meaning");
               int columnIdex3=cursor2.getColumnIndex("_id");
               int columnIdex4=cursor2.getColumnIndex("parent_id");
               int columnIdex5=cursor2.getColumnIndex("listTitle");
               do {
                   // Retrieve the data from the cursor using the columnIndex
                   String data = cursor2.getString(columnIndex);
                   stockedWords.add(data);

                   // Process the data as needed (e.g., display it in a TextView, add it to a list, etc.)
                   //dataがmeaningかsentenceにふくまれるか判断
                   //含まれるならいろつける
                   if((meaningg.contains(data)||sentence.contains(data))&&!data.equals(namey)&&!this.isWordIncluded(menuList,data)){//!!!!!!!重複してしまう!!
                       listMenu.put("name", data);
                       String data_2=cursor2.getString(columnIdex2);
                       listMenu.put("meaning", data_2);
                       Log.d("ohgodnoplease",data+","+data_2);
                       long data_3=cursor2.getLong(columnIdex3);
                       listMenu.put("id",data_3);
                       long data_4=cursor2.getLong(columnIdex4);
                       listMenu.put("parent_id",data_4);
                       String data_5=cursor2.getString(columnIdex5);
                       listMenu.put("listTitle", data_5);
                       menuList.add(listMenu);
                       listMenu=new HashMap<>();
                   }
               } while (cursor2.moveToNext());
               cursor2.close();
           }
       }

      // public static SpannableStringBuilder getHighlightedSentence(String[] phrasesArray, String sentence) {
       SpannableStringBuilder highlightedSentence = getHighlightedSentence(stockedWords.toArray(new String[0]), meaningg,namey);
       SpannableStringBuilder highlightedSentence2 = getHighlightedSentence(stockedWords.toArray(new String[0]), sentence,namey);

      /*SQLiteDatabase db=_helper.getWritableDatabase();



       String sql="SELECT * FROM words WHERE name IN ( ";

       for(int a=0;a< words.length;a++){
           if(a<words.length-1){
               sql+="\""+words[a]+"\" , ";
           }else{
               sql+="\""+words[a]+"\")";
           }

       }


        Log.d("njgf",sql);
       Cursor cursor=db.rawQuery(sql, null);
       String namae="";
       String listIy="";



       boolean already=false;
       boolean already2=false;



       SpannableString spannableString=new SpannableString(meaningg);
       SpannableString spannableString2=new SpannableString(sentence);
       SpannableString spannableString3=new SpannableString("this is not the fact");



       while(cursor.moveToNext()) {
           int idxNote = cursor.getColumnIndex("name");//楯列
           int idxNote2 = cursor.getColumnIndex("meaning");

           namae = cursor.getString(idxNote);
           listIy = cursor.getString(idxNote2);



           if (meaningg.contains(namae)) {
               //Log.d("837485",namae);
               if (already == false) {
                   already = true;
               }
               int startIndex = meaningg.indexOf(namae);
               int endIndex = startIndex + namae.length();


               spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

           }
           if (sentence.contains(namae)) {
               if (already2 == false) {
                   already2 = true;
               }
              int startIndexx = sentence.indexOf(namae);
              int endIndexx = startIndexx + namae.length();
               if (namae.equals(namey)) {
                   Log.d("156386","title word is:"+namae+":");
                   spannableString2.setSpan(new ForegroundColorSpan(Color.YELLOW), startIndexx, endIndexx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                   Log.d("57483","fuuuuuck");
               } else {
                   Log.d("156386","yeah:"+namae+":");
                   spannableString2.setSpan(new ForegroundColorSpan(Color.GREEN), startIndexx, endIndexx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                   Log.d("574831","fuuuuuck2");
               }
           }



           if (!namae.equals(namey)) {

           listMenu.put("name", namae);

           listMenu.put("meaning", listIy);
           menuList.add(listMenu);
       }


           listMenu=new HashMap<>();

       }



       if(already==true){
           Log.d("12343212","0987890");
           meaning.setText(spannableString);
       }else{
           meaning.setText(meaningg);
       }


       if(already2==true){
           Log.d("1234321","0987891");
           exsen.setText(spannableString2);
       }else{
           exsen.setText("Ex) "+sentence);
       }*/

       meaning.setText(highlightedSentence);
       exsen.setText(highlightedSentence2);

       adapter = new RecyclerListAdapter(menuList);
       lvMenu2.setAdapter(adapter);

       DividerItemDecoration decoration = new DividerItemDecoration(WordDetail.this/*WordDetail.this*/, /*LinearLayoutManager.VERTICAL*/layout.getOrientation());
       lvMenu2.addItemDecoration(decoration);

     //  }

   }
   /* public class MyDividerItemDecoration extends DividerItemDecoration {



        private int mExcludePosition;



        public MyDividerItemDecoration(Context context, int orientation, int excludePosition) {

            super(context, orientation);

            mExcludePosition = excludePosition;

        }



        @Override

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            int position = parent.getChildAdapterPosition(view);

            if (position != mExcludePosition) {

                super.getItemOffsets(outRect, view, parent, state);

            } else {


                outRect.setEmpty();
                Log.d("18574","suss");
            }

        }

    }*/
   public boolean isWordIncluded(List<Map<String, Object>> wordList, String searchWord) {
       for (Map<String, Object> map : wordList) {
           String name = (String)map.get("name");

           if (name != null && name.contains(searchWord)) {
               return true; // Word found, return true
           }
       }

       return false; // Word not found in any map
   }
   public static SpannableStringBuilder getHighlightedSentence(String[] phrasesArray, String sentence,String targ) {
       SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(sentence);

       for (String phrase : phrasesArray) {
           int startPos = sentence.indexOf(phrase);
           if (startPos != -1) {
               int endPos = startPos + phrase.length();
               if(phrase.equals(targ)){
                   spannableBuilder.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
               }else{
                   spannableBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
               }

           }
       }

       return spannableBuilder;
   }
    private class Clicked implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                case R.id.floatingActionButton2:
                    tts.speak(namey,TextToSpeech.QUEUE_FLUSH,null);
                    Log.d("speaking of which",namey);
                    break;

                case R.id.floatingActionButton8://mode 0 normal 1 search!これはサーチからしか押せないはずview.goneで消す clicked a button that leads user to the list
                    /*if(viewModel!=null) {
                        viewModel.setFinishActivityA(false);
                        finishWordList();
                        Log.d("allfinished","man");
                    }*/
                    switch (mode){
                        case 1://go to list from search->word
                            ///*
                            /*WordList activityA = (WordList) getParent();
                            if(activityA!=null){
                                activityA.finishWordList();
                                Log.d("allfinishedd","man");
                            }
                            //Log.d("allfinishedd","man");
                            //ここでワードからリストでの位置も把握したい。
                            Intent intent= new Intent(WordDetail.this, WordList.class);
                            intent.putExtra("mode",2);//ここを2にしていたがlistの0と2は同じ原ら記してるから0にした
                            intent.putExtra("title",listtitle);
                            intent.putExtra("idNum", listId);
                            //!!!!!!!!!!!listId kara tango no kazuwo shirabenakereba
                            //intent.putExtra("kazu",kazu);
                            intent.putExtra("wordId",idd);
                            startActivity(intent);
                            */
                            //*/
                            //break;
                        case 0://back to list
                        case 2:
                            modoru();
                            break;
                    }

                    finish();
                    break;
                case R.id.floatingActionButton9://word search youni
                   /* Intent hypeboy=new Intent(WordDetail.this, MainActivity.class);

                    startActivity(hypeboy);

                    finish();*/
                    this.showInputDialog();
                    break;

            }
        }

        private void showInputDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(WordDetail.this);
            View dialogView = getLayoutInflater().inflate(R.layout.search_dialog, null);
            EditText editText = dialogView.findViewById(R.id.editText);

            builder.setView(dialogView)
                    .setTitle("Enter a Word")
                    .setPositiveButton("OK", (dialog, which) -> {
                        String inputText = editText.getText().toString().trim();
                        // Do something with the inputText (e.g., process or display it)
                        ForAPIs apiAccess = new ForAPIs();
                        String urly = apiAccess.getter();
                        boolean yesoryes=_helper.CheckIfValid(inputText);
                        if(inputText.length()>0&&yesoryes) {
                            String meaningy = apiAccess.receiveMeaning(urly + inputText);
                            Log.d("2124124", urly);
                            Object a[] = {meaningy, "", listId, listtitle, inputText};
                            Log.d("676577", meaningy);




                             long theid = _helper.insertData("words", a);


                           // Map<String, Object> novelword=new HashMap<>();
                            //newwordsに保管
                            newids.add(theid);

                           // newwords.add(novelword);
                            //せんとかがかさなるようになってしまう　更新の仕方
                            machineStart();
                        }else{
                            if(yesoryes) {
                                //show message that says "input something"
                                Toast toast = Toast.makeText(getApplicationContext(), "Yo, input something", Toast.LENGTH_SHORT);
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(), "u already registered that word before", Toast.LENGTH_SHORT);
                            }
                        }





                        //reload
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }


}