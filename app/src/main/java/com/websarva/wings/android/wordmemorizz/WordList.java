package com.websarva.wings.android.wordmemorizz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
//import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//id関係は全部longに変える!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class WordList extends AppCompatActivity {
    private long listId=-1;
    private String listtitle="";
    private String nonNewtitle="";

    private long theIdOfword=-1;
    private int theposition=-100;

    private DatabaseHelper _helper;
    private List<Map<String,Object>> menuList=new ArrayList<>();
    //private List<Map<String,String>> showedMenuList=new ArrayList<>();
    private List<Integer> checkIfVisible=new ArrayList<>();
    private List<Double> correctRate=new ArrayList<>();
    private List<Double> correctRateFinal=new ArrayList<>();
    private List<Double> correctNormal=new ArrayList<>();
    private List<Integer> wordIds=new ArrayList<>();
    private int mode=-1;

    //private MyViewModel viewModel;

    Menu menuy;
    TextView textvy;

    private int numOfValidWords=0;
    private long numtango=0;

    private String newAddWord="";
    private String newWordM="";
    private String newsen="";
    private long newId=-1;
    private int pos=-1;
    private List<Long> newwordIds=new ArrayList<>();
    private List<Long> editedIds=new ArrayList<>();

    private int stateOfswitch=0;//0 notchecked 1 checked

    RecyclerView lvMenu2;
    RecyclerListAdapter adapter;
    LinearLayoutManager layout;
   ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent intenty=result.getData();
                        Log.d("122521551","ya");
                       if(intenty!=null) {
                           int mody = intenty.getIntExtra("mode", -1);

                           Log.d("1225215511","ya");
                           switch (mody) {
                               case 1:
                                   Toast.makeText(WordList.this, "新しい単語を追加しました。", Toast.LENGTH_LONG).show();

                               case 3:

                               case 6:

                               case 2://deleted
                                   newAddWord = intenty.getStringExtra("adddata");
                                   newWordM = intenty.getStringExtra("adddata2");
                                   newsen=intenty.getStringExtra("adddata3");

                                   if (intenty.hasExtra("longList")/*&&mody==6*/) {
                                       // Retrieve the List of Long values from the Intent
                                       newwordIds = (List<Long>)intenty.getSerializableExtra("longList");

                                       // Now, 'receivedLongList' contains the passed List of Long values
                                   }
                                   //Log.d("298579211","hey");
                                   if(intenty.hasExtra("editedOnes")){
                                       //editedOnes
                                       editedIds =(List<Long>)intenty.getSerializableExtra("editedOnes");
                                       //Log.d("29857921","hey");
                                   }
                                   //newsen="aaaa";
                                   //String codeone=intenty.getStringExtra("pos");

                                    pos=Integer.valueOf(pos);
                                   //なぜかintはgetintextraで得られない
                                   //int why=intenty.getIntExtra("why",-3);
                                   //Log.d("457937459",newAddWord+":"+codeone);
                                   if (mody == 2) {

                                       Toast.makeText(WordList.this, "単語を削除したじょ", Toast.LENGTH_LONG).show();

                                   }
                                   newId = intenty.getLongExtra("id", -1);
                                   ListSetUpp2(2, 0, 0, mody,"");
                                   break;
                               case 4:

                                   break;

                               case 5:
                                   //市、状態保存
                                   int[] pos = ichiHozon(layout, lvMenu2);
                                   ListSetUpp2(0, pos[0], pos[1], 5,"");
                                   newId = intenty.getLongExtra("id", -1);
                                   break;
                           }
                           //Log.d("12252155112","ya");
                           //newId = intenty.getLongExtra("id", -1);
                           //Log.d("122521551123",String.valueOf(newId));
                           //getintextraでどのページから戻ったのか区別
                           //1:addWordで単語追加後　2:worddetailで単語削除後 3:addwordで単語編集後worddetailを通して
                           //4:nothing has changed 5:after test

                           /*if (mody != 5) {

                           ListSetUpp2(2, 0, 0, mody,"");
                           }*/
                        }
                        //  Log.d("callbacky","success");
                        //  ListSetUpp2(0);

                    }
                }
            }
    );
   //特定の単語の位置を取得するクラス必要」!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //スクロールバー的なやつ
        //検索
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);



        _helper=new DatabaseHelper(WordList.this);

        Intent intent22=getIntent();
        //wordlistかwordsearchかを判別
        mode=intent22.getIntExtra("mode",-1);//0:home no list kara 1:search  2:home画面のsearch->word kara

        theIdOfword=intent22.getLongExtra("wordId",-1);//search->word->here
        //Log.d("39866345",String.valueOf(theIdOfword));
        textvy=findViewById(R.id.textView14);

        listtitle=intent22.getStringExtra("wordTitle");//after adding a new list nullになり得る
        //Log.d("listt",listtitle);
        listId=intent22.getLongExtra("idNum",-1);

        nonNewtitle=intent22.getStringExtra("title");//by tapping list ここで完全リセットされてるnullになり得る
        numtango=intent22.getLongExtra("kazu",0);//numofvalidで数えてるからいらない

        ArrayList<View> viewsToFadeIn = new ArrayList<View>();

        FloatingActionButton fab=findViewById(R.id.floatingActionButton);
        FloatingActionButton fab10=findViewById(R.id.floatingActionButton6);
        FloatingActionButton fab11=findViewById(R.id.floatingActionButton7);

        viewsToFadeIn.add(fab);
        viewsToFadeIn.add(fab10);
        viewsToFadeIn.add(fab11);

        Clicked lis=new Clicked();

        for (View v : viewsToFadeIn)
        {
            v.setOnClickListener(lis);

            v.setAlpha(0); // make invisible to start
        }

      //  Log.d("27837",listtitle);

        switch (mode){
            case 0://from mainactivity
            case 2://words from search
                for (View v : viewsToFadeIn)
                {
                    // 3 second fade in time
                    v.animate().alpha(1.0f).setDuration(1000).start();
                }



                textvy.setVisibility(View.GONE);
              ActionBar actionBar=getSupportActionBar();

                //actionBar.setDisplayHomeAsUpEnabled(true);
                //ここがエラーの原因だがどうして皮不明
                //by yahoo 文字列(String)に対して
                //length()で文字数取得してる箇所があるはずです。
                //ここでエラーです。
                //
                //文字列(String)が空っぽ(null)で何もないのに、メソッド呼び出ししたことが原因です。
            if(nonNewtitle!=null){
                    actionBar.setTitle(nonNewtitle);
                }else{
                actionBar.setTitle(listtitle);
            }





                ListSetUpp2(0,0,0,-1,"");
                Log.d("278371","error");
                break;
            case 1://search
                for (View v : viewsToFadeIn)
                {
                    // 3 second fade in time
                    if(v.getId()!=R.id.floatingActionButton) {
                        v.animate().alpha(1.0f).setDuration(1000).start();
                    }else{
                        v.setVisibility(View.GONE);
                    }
                }
                break;
        }


        //viewModel = new ViewModelProvider(this).get(MyViewModel.class);



    /*    modeChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(modeChange.isChecked()) {
                    //mSwitch : Off -> On の時の処理
                    //色付き
                    //testを五回以上行っている場合のみ有効
                    int[] pos=ichiHozon(layout,lvMenu2);
                    ListSetUpp2(1,pos[0],pos[1],-1);
                    stateOfswitch=1;
                   //スクロールの位置保存
                 //  adapter.notifyDataSetChanged();
                    Log.d("changedd","succesexx");
                } else {
                    int[] pos=ichiHozon(layout,lvMenu2);
                    //mSwitch : On -> Off の時の処理
                      //色なし
                    ListSetUpp2(3,pos[0],pos[1],-1);
                    stateOfswitch=0;
                 //   adapter.notifyDataSetChanged();
                }
            }
        });*/
    }
    public void finishWordList() {
        finish();
    }
    protected int[] ichiHozon(LinearLayoutManager x,RecyclerView y){

        int positionIndex=x.findFirstVisibleItemPosition();
        View startView=y.getChildAt(0);
        int positionOffset=(y==null)?0:(startView.getTop()-y.getPaddingTop());

        int[] fred={positionIndex,positionOffset};

        return fred;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
      //  MenuInflater inflater2=getMenuInflater();
      //  inflater2.inflate(R.menu.search_bar, menu);

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);



        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

       searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(/*mode!=0&&mode!=-1*/mode==1){//search no toki nomi
                    ListSetUpp2(0,0,0,-1,query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mode==0||mode==2) {
                    adapter.getFilter().filter(newText);
                }
                return false;

            }
        });
        if(menuy!=null){
           // menuy=menu;
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //search　機能!!!!!!!!!!!!!追加
        boolean returnVal=true;

        int itemId=item.getItemId();

        switch (itemId) {
            case R.id.menuListOption2:
                //tango tuika
                Intent intentt = new Intent(WordList.this, AddWord.class);

                intentt.putExtra("listId", listId);
                intentt.putExtra("titlel", listtitle);
                //   Log.d("fuckkkkk","ruehgoergerg");
                //  startActivity(intentt);
                //   finish();
                //Show ConfirmDialog

                resultLauncher.launch(intentt);
                break;
            case R.id.menuListOption1:
                //test kaisi
                //アラート掲示
                //もし単語の数が4個以下ならできないと警告



               if (numOfValidWords >= 4&&(mode==0||mode==2)) {
                //wordlistのtestを1増やす
                Intent intenttt = new Intent(WordList.this, fQuiz.class);//Quizzz.class
                //   Log.d("fuckkkkk","ruehgoergerg");
                intenttt.putExtra("listId", (int) listId);
                intenttt.putExtra("titlel", listtitle);
                intenttt.putExtra("fromNotification",false);
                intenttt.putExtra("max",numOfValidWords);//単語の数 numtango (これはデータベースの単語数からえたもの)もあるからどちらかに
                //intenttt.putExtra("mode",1);//listから
                // Log.d("fuckkkkk","ruehgoergerg");
                //   resultLauncher.launch(intenttt);
              resultLauncher.launch(intenttt);
             //      int[] why=ichiHozon(layout,lvMenu2);
              //  intenttt.putExtra("index",why[0]);
             //   intenttt.putExtra("offset",why[1]);
             //   startActivity(intenttt);
            //    finish();
                }else{
                   Toast toast = Toast.makeText(getApplicationContext(), "Test isn't available for there's less than four words in this list", Toast.LENGTH_SHORT);
                   toast.show();
                }
                break;
            case R.id.menuListOption3:
              Intent intent= new Intent(WordList.this, ListDetail.class);
            //    intent.putExtra("title",meaningg.getText().toString());
             //   intent.putExtra("id",menuList.get(pos).get("id"));
             //   intent.putExtra("testdays",menuList.get(pos).get("testdays"));
            //    intent.putExtra("torokubi",menuList.get(pos).get("torokubi"));

                break;
            case android.R.id.home:
                //back home
               modoru();
                break;

            case R.id.action_search:
                Log.d("came here but nothing has happened","huh?");
                break;
        }
        return returnVal;
    }
    private void modoru(){
        Intent hypeboy=new Intent(WordList.this, MainActivity.class);

        startActivity(hypeboy);

        finish();
    }

    @Override//下の戻るボタンが押された時
    public void onBackPressed(){
       modoru();
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
        public ProgressBar _progress;
        public ArrayList<View> boxes;


        public RecyclerListViewHolder(View itemView){
            super(itemView);
            _tvMenuWordRow=itemView.findViewById(R.id.kotoba);
            _tvMenuMeaningRow=itemView.findViewById(R.id.imi);
            _onrRow=itemView.findViewById(R.id.linearCAr);
            _progress=itemView.findViewById(R.id.ProgressBarHorizontal);
            View box0=itemView.findViewById(R.id.box0);
            View box1=itemView.findViewById(R.id.box1);
            View box2=itemView.findViewById(R.id.box2);
            View box3=itemView.findViewById(R.id.box3);
            View box4=itemView.findViewById(R.id.box4);
            boxes=new ArrayList<>();
            boxes.add(box0);
            boxes.add(box1);
            boxes.add(box2);
            boxes.add(box3);
            boxes.add(box4);
        }


    }

    public interface OnNoteListener{
        void onNoteClick(int position);

    }

    public void startWordDetail() {
      //  viewModel.setFinishActivityA(true);
        Intent intent = new Intent(this, WordDetail.class);
        startActivity(intent);
    }

   /* public class MyViewModel extends ViewModel {
        private MutableLiveData<Boolean> finishActivityA = new MutableLiveData<>();

        public void setFinishActivityA(boolean value) {
            finishActivityA.setValue(value);
        }

        public boolean getFinishActivityA() {
            Boolean value = finishActivityA.getValue();
            return value != null ? value : false;
        }
    }*/

    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> implements Filterable {
        private List<Map<String, Object>> _listData;
        private List<Map<String, Object>> _listDataFull;
        public RecyclerListAdapter(List<Map<String, Object>> listData){

            this._listData = listData;
            _listDataFull=new ArrayList<>(listData);

        }
        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater=LayoutInflater.from(WordList.this);
            View view = inflater.inflate(R.layout.simple_item, parent, false);

            view.setOnClickListener(new ItemClickListener());
            view.setOnLongClickListener(new ItemLongClickListener());

            RecyclerListViewHolder holder=new RecyclerListViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(RecyclerListViewHolder holder, int position){


                Map<String, Object> item = _listData.get(position);
                String wordd = (String) item.get("word");
                String meanin = (String) item.get("meaning");
                long sonoid=(long) item.get("id");
                int lenOf=lenOfList();//menulist no saizu
                Log.d("valueyy",String.valueOf(position)+":"+lenOf);

                if(sonoid==theIdOfword){
                   //theposition=position;
                }

               if(lenOf==position+1){
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder._onrRow.getLayoutParams();
                    layoutParams.bottomMargin=200;
                    holder._onrRow.setLayoutParams(layoutParams);
                   // layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                  //  holder._onrRow.setLayoutParams(layoutParams);

                }else{
                   ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder._onrRow.getLayoutParams();
                   layoutParams.bottomMargin=0;
                   holder._onrRow.setLayoutParams(layoutParams);
               }
                holder._tvMenuWordRow.setText(wordd);
                holder._tvMenuMeaningRow.setText(meanin);
                //holder._tvMenuMeaningRow.setText(meanin);
               if((boolean)menuList.get(position).get("visible")){
                   holder._tvMenuMeaningRow.setVisibility(View.VISIBLE);
               }else{
                   holder._tvMenuMeaningRow.setVisibility(View.INVISIBLE);
               }
                //correctRateに応じて背景色を変える
              // double eleven=correctRate.get(position);
           //    double aaaa=correctRateFinal.get(position);
              double aaa=(double)menuList.get(position).get("correctRate");
               holder._progress.setMax(100);
               holder._progress.setProgress((int)Math.round(aaa*100));
               holder._progress.setMin(0);
               Log.d("ratetada2",String.valueOf((int)aaa*100));
          //     double valu=eleven*170;
         //   holder._onrRow.setBackgroundColor(Color.rgb(190,(int)valu,243));
            String colour=(String)menuList.get(position).get("result");
            Log.d("nuu",colour);
         for(int gh=0;gh<5;gh++){
                  char fancy=colour.charAt(gh);
                  switch (fancy){
                      case '3'://not yet
                          holder.boxes.get(gh).setBackgroundColor(getResources().getColor(R.color.koypurple,getTheme()));
                          break;
                      case '1'://correct
                          holder.boxes.get(gh).setBackgroundColor(getResources().getColor(R.color.usuiGreen,getTheme()));
                          break;
                      case '0'://incorrect
                          holder.boxes.get(gh).setBackgroundColor(getResources().getColor(R.color.koiGreen,getTheme()));
                          break;
                  }

              }




        }
        @Override
        public int getItemCount(){
            return _listData.size();
        }
        @Override
        public Filter getFilter(){
            return exampleFilter;
        }

        private Filter exampleFilter=new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint){
                List<Map<String, Object>> filteredList= new ArrayList<>();

                if(constraint==null||constraint.length()==0){
                    filteredList.addAll(_listDataFull);
                }else{
                    String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                    for(Map<String, Object> item:_listDataFull){
                        String aa=(String)item.get("word");
                        if(aa.toLowerCase(Locale.ROOT).contains(filterPattern)){
                           filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results){
                    _listData.clear();

                    _listData.addAll((List)results.values);
                Log.d("wipejustforme",String.valueOf(getItemCount()));
                    notifyDataSetChanged();
            }
        };


    }
    protected void letsAdd(Map<String,Object> yay){
        menuList.add(yay);
    }
    protected void notifyay(int position){
        adapter.notifyItemInserted(position);
    }
    protected int getListSize(){
        return menuList.size();
    }
    protected void testAvail(int num){
       // Menu mymenu=findViewById(R.id.)
        if(num>=4){
            if(menuy!=null){
                menuy.findItem(R.id.menuListOption1).setEnabled(true);
            }

        }else{
            menuy.findItem(R.id.menuListOption1).setEnabled(false);
        }

    }
    private void fillmenulist(){
        menuList = new ArrayList<>();
        checkIfVisible = new ArrayList<>();

    }
    protected void ListSetUpp2(int version,/* 位置保存1*/int additional1,/*位置保存2*/int additional2,int dore,String keyword) {//0 1:updates 2:backgroundclor change
        //RecyclerListAdapter adapter;
        //0 最初の読み込み
        //1 switchで背景商kの切り替え
        //2 単語追加後
        //3 ? 単語テスト後
        switch (version) {
            case 0:

                menuList = new ArrayList<>();
                if (dore != 5) {
                    checkIfVisible = new ArrayList<>();
                }


                if (listtitle == null && nonNewtitle != null) {//if list tapped
                    listtitle = nonNewtitle;
                }

                Cursor cursor;

                if (mode == 0||mode==2) {


                    SQLiteDatabase dbb = _helper.getWritableDatabase();

                    String fuck = "SELECT * FROM wordlist WHERE listName LIKE'" + listtitle + "' ";//!!!!!!listId parent idの条件も!!//


                cursor = dbb.rawQuery(fuck, null);
                //"idNum"
                //int whichList=-1;

                while (cursor.moveToNext()) {//its length must be one, otherwise fuck
                    int idxNo = cursor.getColumnIndex("_id");

                    listId = cursor.getInt(idxNo);
                }
                }


       // Log.d("listID", String.valueOf(listId));
                lvMenu2 = findViewById(R.id.lvMenun);

        //  lvMenu2.setLayoutManager(null);
        layout = new LinearLayoutManager(WordList.this);
        lvMenu2.setLayoutManager(layout);
        //   ListView lvMenu=findViewById(R.id.lvMenu2);
                //menuList=new ArrayList<>();
        Map<String, Object> menu = new HashMap<>();
        //the final goal is to read sql database and add it to m
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql;
        if(mode==0||mode==2){
            sql = "SELECT * FROM words WHERE parent_id=" + listId;
        }else{
            sql = "SELECT * FROM words WHERE name LIKE '%" + keyword + "%' ";

        }


        cursor = db.rawQuery(sql, null);
       // String namey = "";
       // String meaningg = "";
       // String resulty="";
        //int q = 0;
        numOfValidWords=0;
        while (cursor.moveToNext()) {
            int idxNote = cursor.getColumnIndex("name");//楯列
            int idxNote2 = cursor.getColumnIndex("meaning");
            int forId=cursor.getColumnIndex("_id");

            double correctR = 0.0;
            // if (version == 1) {
            int correctRate2 = cursor.getColumnIndex("correctRate");
            int entire = cursor.getColumnIndex("entire");
            int resultt=cursor.getColumnIndex("result");

            int parentID=cursor.getColumnIndex("parent_id");
            int listTitle=cursor.getColumnIndex("listTitle");
            String listDetitle=cursor.getString(listTitle);
            long parentIdy=cursor.getLong(parentID);

            int seikai = cursor.getInt(correctRate2);
            int sou = cursor.getInt(entire);

            long theIdr=cursor.getLong(forId);
            String resulty=cursor.getString(resultt);

            if (sou != 0) {
                correctR = (double) seikai / sou;
            }
            //   }


            String namey = cursor.getString(idxNote);
            String meaningg = cursor.getString(idxNote2);

            menu.put("word", namey);//string
            menu.put("meaning", meaningg);//string
            menu.put("correctRate",correctR);//double
            menu.put("id",theIdr);//int

            menu.put("listTitle",listDetitle);
            menu.put("parent_id",parentIdy);

            menu.put("order",numOfValidWords);//int
            menu.put("result",resulty);
            menu.put("visible",false);

            if(theIdr==theIdOfword) {

                menuList.add(0,menu);
            }else{
                menuList.add(menu);
            }

            numOfValidWords+=1;
            //ここで正答率もデータベースから引き出しそれに応じて色を変える
            //explicit correct rate from the database and change background color accordingly
            menu = new HashMap<>();
            //Log.d("alan3",String.valueOf(q));
           // q++;
        }

        if(mode==1&&textvy!=null){//search mode
            int visibility=textvy.getVisibility();
            if(numOfValidWords>=1) {
                String willbeShowed = numOfValidWords + " results hit:)";
                if (visibility == View.VISIBLE) {
                    textvy.setVisibility(View.GONE);
                }
                Toast toast = Toast.makeText(getApplicationContext(), willbeShowed, Toast.LENGTH_SHORT);
              //  toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();

            }else{
                if(visibility==View.GONE){
                    textvy.setVisibility(View.VISIBLE);
                }
                textvy.setText("The word isn't included in any lists:(");
            }
        }
        //ここで一番最後に表示されるぃすとのえいうｇぶえ
        //showedMenuList=menuList;//copy
        // lvMenu2.setAdapter(null);
        adapter = new RecyclerListAdapter(menuList);
        lvMenu2.setAdapter(adapter);

       // DividerItemDecoration decoration = new DividerItemDecoration(WordList.this, layout.getOrientation());
      //  lvMenu2.addItemDecoration(decoration);

        if(dore==5){
            //switchの状態も考える
            layout.scrollToPositionWithOffset(additional1,additional2);
        }
        if(mode==2/*&&theposition!=-100*/){
           // layout.scrollToPositionWithOffset(0,20);
           //gotoTheposition(adapter.getItemCount()-1);
        }
                Log.d("278372","error");
      //  this.testAvail(numOfValidWords);

        //adapter.notifyDataSetChanged();
        break;
            case 1:
              /*  correctRate=correctRateFinal;
                //adapter lyaoutmanager 更新
                layout=new LinearLayoutManager(WordList.this);
                lvMenu2.setLayoutManager(layout);
                layout.scrollToPositionWithOffset(additional1,additional2);*/

                break;
                //削除した後テストするときどうするか!!!!!!!!!!!!
            case 2://after adding a new word　or edit or delete->idok
                Log.d("12125125","here");
                if(newAddWord.length()!=0&&newWordM.length()!=0) {
                    Log.d("121251251","here");
                    Map<String, Object> lateNight = new HashMap<>();
                    switch (dore){//dore differs depending on the former page showed to users
                        case 3://edit !!!!!!!!!!!!!!!!!!!

                           /* lateNight.put("word", newAddWord);
                            lateNight.put("meaning", newWordM);

                            menuList.set(wordIds.indexOf(newId),lateNight);
                            break;*/
                        case 2://deleted sakujyo newaddword and newwordm are both "-"
                            lateNight.put("word", newAddWord);
                            lateNight.put("meaning", newWordM);
                            lateNight.put("correctRate",0.0);//double
                            lateNight.put("id",newId);//int->long

                            lateNight.put("listTitle","");
                            lateNight.put("parent_id",-1);

                            lateNight.put("order",-1);//int
                            lateNight.put("result","33333");
                            lateNight.put("visible",false);
                            Log.d("qwer","qwer");

                            if(dore==2){
                                numOfValidWords-=1;
                              //  this.testAvail(numOfValidWords);
                            }
                             //this.theindex(menuList,newId)
                            int orr;
                           // if(pos<=-1) {
                            orr=this.theindex(menuList, newId);

                           // }else{
                             //   orr=pos;

                           // }
                            menuList.set(orr, lateNight);

                           // int numban=wordIds.indexOf(newId);
                           // Log.d("plko",String.valueOf(numban));
                            //deletedという表示に変える

                            //全部一ずつずれる!!!からなにかしなきゃ!!!!
                            Log.d("plko","yes");
                            break;
                        case 1://tuika
                            Log.d("12252155",String.valueOf(newId));
                            numOfValidWords+=1;
                            lateNight.put("word", newAddWord);
                            lateNight.put("meaning", newWordM);
                            lateNight.put("correctRate",0.0);
                            lateNight.put("id",newId);

                            lateNight.put("listTitle","");
                            lateNight.put("parent_id",-1);

                            lateNight.put("order",numOfValidWords);
                            lateNight.put("result","33333");
                            lateNight.put("visible",false);

                            menuList.add(lateNight);

                            break;

                        case 6://worddetailで追加された単語たちをmenulistに追加する
                            List<Map<String,Object>> storedd=_helper.newDataAdded(newwordIds);
                            for(Map<String, Object> mess:storedd){
                                Map<String,Object> qwe=new HashMap<>();
                                numOfValidWords+=1;
                                qwe.put("word", mess.get("word"));
                                qwe.put("meaning", mess.get("meaning"));
                                qwe.put("correctRate",0.0);
                                qwe.put("id",mess.get("id"));

                                qwe.put("listTitle","");
                                qwe.put("parent_id",-1);

                                qwe.put("order",numOfValidWords);
                                qwe.put("result","33333");
                                qwe.put("visible",false);
                                menuList.add(qwe);
                            }

                            //編集された単語たちを反映させる
                            List<Map<String,Object>> changedOnes=_helper.newDataAdded(editedIds);

                            for(Map<String,Object> eachOne : changedOnes){
                                long targetId=(long)eachOne.get("id");
                                String newName=(String)eachOne.get("word");
                                String newMeaning=(String)eachOne.get("meaning");
                                for (Map<String, Object> component : menuList) {
                                long id = (long) component.get("id");
                                if (id == targetId) {
                                    // Modify the component
                                    component.put("word", newName);
                                    component.put("meaning", newMeaning);
                                    break; // Exit the loop once the component is found and modified
                                }
                            }
                            }

                           /* for (Map<String, Object> component : menuList) {
                                long id = (long) component.get("id");
                                if (id == targetId) {
                                    // Modify the component
                                    component.put("word", newName);
                                    component.put("meaning", newMeaning);
                                    break; // Exit the loop once the component is found and modified
                                }
                            }*/


                            break;

                    }

                    newWordM = "";
                    newAddWord = "";
                    newId=-1;

                    lvMenu2.setAdapter(null);
                    lvMenu2.setLayoutManager(null);
                    adapter = new RecyclerListAdapter(menuList);
                    lvMenu2.setAdapter(adapter);
                    layout=new LinearLayoutManager(WordList.this);
                    lvMenu2.setLayoutManager(layout);


                }

                break;
          /*  case 3:
                correctRate=correctNormal;
                layout=new LinearLayoutManager(WordList.this);
                lvMenu2.setLayoutManager(layout);
                layout.scrollToPositionWithOffset(additional1,additional2);
                break;*/
            case 4:

                break;

        /*    case 1:
                //menu = new HashMap<>();
                if (newAddWord.length() != 0 && newWordM.length() != 0) {

                Map<String, String> lese = new HashMap<>();
                // String sql2 = "SELECT * FROM words WHERE parent_id=" + listId+"AND name='"+"yaay"+"'";

                lese.put("word", newAddWord);
                lese.put("meaning", newWordM);
                menuList.add(lese);
                //lvMenu2.setAdapter(adapter);
                // cursor = dby.rawQuery(sql2, null);
             }
            break;*/


    }
    //   String[] from={"word","meaning"};
    //   int[] to={R.id.kotoba,R.id.imi};

    //  SimpleAdapter adapter=new SimpleAdapter(WordList.this, menuList, R.layout.simple_item/*android.R.layout.simple_list_item_2*/, from, to);
     //  lvMenu.setAdapter(adapter);

      // lvMenu.setOnItemClickListener(new WordList.ListItemClickListener());
    }
   /* public class CustomAdapter extends SimpleAdapter{
        LayoutInflater mLayoutInflater;
        public CustomAdapter(Context context,List<? extends Map<String,?>> data, int resource,
                             String[] from, int[] to){
            super(context,data,resource, from, to);
        }

        @Override
        public View getView(int position,View convertView, ViewGroup parent){

            mLayoutInflater=LayoutInflater.from(WordList.this);
            convertView=mLayoutInflater.inflate(R.layout.simple_item,parent,false);
            ListView listview=(ListView) parent;
            Map<String,String> data = (Map<String,String>)listview.getItemAtPosition(position);


            return convertView;
        }
    }*/
    /*private Map<String,Object> insertyy(){
        return
    }*/
    private void gotoTheposition(int destiny) {
        if (lvMenu2 != null) {

        lvMenu2.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll//自動でスクロール
                lvMenu2.smoothScrollToPosition(destiny);
            }
        });
    }
    }
    private int lenOfList(){
        return menuList.size();
    }
    private int theindex(List<Map<String,Object>> listy,long idd){
        for (int i = 0; i < listy.size(); i++) {
            Map<String, Object> map = listy.get(i);
            if((long)map.get("id")==idd){
                return i;
            }
        }
        return -1;
    }

    private class memoryFunction{
        public double memoryfun(){

            return 0.5;
        }
   }
    private class ItemLongClickListener implements View.OnLongClickListener{
        @Override
       public boolean onLongClick(View view) {
            RecyclerView recy = findViewById(R.id.lvMenun);
            TextView meaningg = view.findViewById(R.id.imi);
            TextView kotob = view.findViewById(R.id.kotoba);
            String ti=kotob.getText().toString();
            if (!ti.equals("-")) {

            int pos = recy.getChildLayoutPosition(view);
            //get id in database
            //int realId=wordIds.get(pos);
            long realId = (long) menuList.get(pos).get("id");
            //tangoの詳細ページ
            Intent detail = new Intent(WordList.this, WordDetail.class);
            detail.putExtra("imi", meaningg.getText().toString());
            detail.putExtra("word", ti);


            detail.putExtra("id", realId);
            detail.putExtra("pos", pos);

            switch (mode){
                case 0://normal list
                case 2://home-> search->word kara
                    detail.putExtra("listid", listId);
                    detail.putExtra("listTitle", listtitle);
                    detail.putExtra("fromWhere", 0);
                    break;
                case 1://search
                    long theId = (long) menuList.get(pos).get("parent_id");
                    detail.putExtra("listid", theId);
                    String daimey = (String) menuList.get(pos).get("listTitle");
                    detail.putExtra("listTitle", daimey);

                    //detail.putExtra("numtango",(int)menuList.get(pos).get("listTitle"));
                    detail.putExtra("fromWhere", 1);
                    break;
                default:
            }


            //search wordsからかどうかの判断

            //tangoの情報
            resultLauncher.launch(detail);
        }
           // startActivity(detail);
            return true;//by returning true onclick turns invalid
        }
   }


    private class ItemClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            RecyclerView recy=findViewById(R.id.lvMenun);
            TextView meaningg=view.findViewById(R.id.imi);
            TextView kotob=view.findViewById(R.id.kotoba);
            //database 更新

            //String namy=kotob.getText().toString();
            //positionを得てcheckIfvisibleを更新1 visible 0 invisible
            int pos=recy.getChildLayoutPosition(view);
            Log.d("ditto","newjeans"+pos);
            //meaningg.setText("");
            //
            if((boolean)menuList.get(pos).get("visible")){
                meaningg.setVisibility(View.INVISIBLE);
                menuList.get(pos).put("visible",false);
            }else{
                meaningg.setVisibility(View.VISIBLE);
                menuList.get(pos).put("visible",true);
            }
        /*  switch (menuList.get(pos).get("visible")){
                case 0:
                    meaningg.setVisibility(View.VISIBLE);
                    checkIfVisible.set(pos,1);
                    break;
                case 1:
                    meaningg.setVisibility(View.INVISIBLE);
                    checkIfVisible.set(pos,0);
                    break;
            }*/


        }

    }

    //listがタップされると意味が表示される、もう一度タップされると消える　の繰り返し
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            //positionで
            Map<String,String> clickedItem=(Map<String,String>)parent.getItemAtPosition(position);
            String kotoba=clickedItem.get("word");
            String imy=clickedItem.get("meaning");
            //positionでワードを割り出す!!!!!!!
            // showedmenulistを編集しその都度アダプタを設定すんの?
            TextView afterLike=view.findViewById(R.id.imi);
            Log.d("tapped",String.valueOf(position)+" : "+kotoba+" : "+imy);

               if (afterLike.getVisibility() == View.INVISIBLE) {
                    afterLike.setVisibility(View.VISIBLE);
                } else {
                    afterLike.setVisibility(View.INVISIBLE);
                }

        }
    }



    private class Clicked implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                case R.id.floatingActionButton://add word
                    Intent intentt= new Intent(WordList.this, AddWord.class);

                    intentt.putExtra("listId",listId);
                    intentt.putExtra("titlel",listtitle);
                    resultLauncher.launch(intentt);
                    break;

                case R.id.floatingActionButton6://back to home
                    modoru();
                    break;
                case R.id.floatingActionButton7://search
                    Intent intent=new Intent(WordList.this, WordList.class);
                    intent.putExtra("mode",1);
                    //validnumofwords
                    startActivity(intent);
                    finish();
                    break;


            }
        }
    }
}