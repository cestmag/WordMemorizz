package com.websarva.wings.android.wordmemorizz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper _helper;
    //private User user;
    RecyclerView lvMenu2;
    RecyclerListAdapter adapter;
    LinearLayoutManager layout;
    List<Map<String, String>> menuList=new ArrayList<>();
   /* private ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode()==Activity.RESULT_OK){
                      ListSetUpp();
                    }
                }
            }
    );*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        boolean alreadyLogin = prefs.getBoolean("login",false);

        if(alreadyLogin == true){//loginしていたら

            String token = prefs.getString("token","");
            String username = prefs.getString("username","");
            String userId = prefs.getString("userId","");
            boolean syncGo = prefs.getBoolean("sync",false);//syncしてるかor not


            ((WordApp)getApplication()).userGenerate(username,"erger");
            ((WordApp)getApplication()).user.setterUserId(userId);

           // ((WordApp)getApplication()).setSync(syncGo);

            ((WordApp)getApplication()).setLoggedIn(true);

           //behindworkやってるかsharedpreferenceをしらべることでわｋる
            //まだやっていたらwordappに情報引継ぎ
            if (/*((WordApp)getApplication()).doesLabelExist(getApplicationContext())&&*/syncGo) {//behindworkやってる
               // if (syncGo == true) {//behindworkやってる
               // ((WordApp)getApplication()).setBehindWorkOn(true);
                ((WordApp)getApplication()).setSync(true);
                //workRequestId
                String uraId = prefs.getString("workRequestId","");//get behindwork id

                Log.d("hello","yo");
                Log.d("4343245",uraId);

              //  ((WordApp)getApplication()).setUrakataId(UUID.fromString(uraId));//prefに書いてる内容をwordappにかく

                // The label/key exists in SharedPreferences
                // Add your logic here
            }/*else if(!(((WordApp)getApplication()).doesLabelExist(getApplicationContext()))&&syncGo){//behinfworkおわってるけどsyncはon
              //  ((WordApp)getApplication()).setBehindWorkOn(true);
                ((WordApp)getApplication()).setSync(true);
               // ((WordApp)getApplication()).urakataStart(getApplicationContext());
            }*//*else{//やっていなかったらかつsyncGo=trueなら始める

                if(syncGo == false){
                    ((WordApp)getApplication()).urakataStart(getApplicationContext());
                }
               // ((WordApp)getApplication()).urakataStart(getApplicationContext());


            }*/


          //  Gson gson = new Gson();
          //  String json = gson.toJson(((WordApp)getApplication()).user);

         //   Data inputData = new Data.Builder()
                  //  .putString("my_data_json", json)
                 //   .build();
            /*Data inputData = new Data.Builder()
                    .putString("argument_key", userId)
                    .build();

            scheduleBackgroundWork(this, inputData);*/

        }

        //user=new User("","");
        //Log.d("fuckinhell","dewfwfewf");
        _helper = new DatabaseHelper(MainActivity.this);

        ArrayList<View> viewsToFadeIn = new ArrayList<View>();

        FloatingActionButton fab=findViewById(R.id.floatingActionButton3);
        HelloListener lis=new HelloListener();
        viewsToFadeIn.add(fab);


        FloatingActionButton fab2=findViewById(R.id.floatingActionButton4);
        //HelloListener lis2=new HelloListener();
        viewsToFadeIn.add(fab2);


        FloatingActionButton fab3=findViewById(R.id.floatingActionButton5);
        //HelloListener lis3=new HelloListener();
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

       // Button addbutton=findViewById(R.id.button);
      //  Button quicknote=findViewById(R.id.button8);
       // Button serchyy=findViewById(R.id.button9);
        //HelloListener hell=new HelloListener();
      //  addbutton.setOnClickListener(hell);
      //  quicknote.setOnClickListener(hell);
      //  serchyy.setOnClickListener(hell);
        //scheduleBackgroundWork(this);

        ListSetUpp();
       // Log.d("fuckinhell","dewfwfewf");

    }
    public void scheduleBackgroundWork(Context context, Data data) {//If you are calling it from an Activity
        // Create a periodic work request

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                MyWorker.class, // Your worker class
                1, // Repeat interval in minutes
                TimeUnit.MINUTES
        ).setInputData(data).build();

        // Enqueue the work request
        WorkManager.getInstance(context).enqueue(workRequest);

        UUID workRequestId = workRequest.getId();
        String workRequestIdString = workRequestId.toString();
        Log.d("word_id", workRequestIdString);



      //  ((WordApp)getApplication()).user.setterWorkreq(workRequestId);
    }
    @Override
    protected void onDestroy(){
        _helper.close();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //  MenuInflater inflater2=getMenuInflater();
        //  inflater2.inflate(R.menu.search_bar, menu);

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);



        MenuItem searchItem = menu.findItem(R.id.action_search2);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal = true;

        int itemId = item.getItemId();

        Intent intent;

        switch (itemId){
           // case R.id.menuListOption111:
                //excel file への書き出し
            //    break;
            case R.id.loginlogoutregister:
                intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
                break;
        }
        return returnVal;
    }
    private class RecyclerListViewHolder extends RecyclerView.ViewHolder{
        public TextView _tvMenuWordRow;
        public TextView _tvMenuMeaningRow;
        public LinearLayout _onrRow;
        public CardView _cardview;

        public RecyclerListViewHolder(View itemView){
            super(itemView);
            _tvMenuWordRow=itemView.findViewById(R.id.daimei);
            _tvMenuMeaningRow=itemView.findViewById(R.id.tangosu);
            _onrRow=itemView.findViewById(R.id.linearCAr2);
            _cardview=itemView.findViewById(R.id.cadiBB);
        }


    }

    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder>{
        private List<Map<String, String>> _listData;
        private List<Map<String, String>> _listDataFull;
        public RecyclerListAdapter(List<Map<String, String>> listData){
            this._listData = listData;
            _listDataFull=new ArrayList<>(listData);
        }
        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.simple_item2, parent, false);

            view.setOnClickListener(new ItemClickListener());
            view.setOnLongClickListener(new ItemLongClickListener());

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
            if(lenOf==position+1){
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder._cardview.getLayoutParams();
                layoutParams.bottomMargin=220;
                holder._cardview.setLayoutParams(layoutParams);
                // layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                //  holder._onrRow.setLayoutParams(layoutParams);

            }else{
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder._cardview.getLayoutParams();
                layoutParams.bottomMargin=10;
                holder._cardview.setLayoutParams(layoutParams);
            }

            Map<String, String> item = _listData.get(position);
            String wordd = (String) item.get("listName");
            String meanin = (String) item.get("kazu");
            String testdayy=(String) item.get("testdays");//8 letter word only with either '1' or '0'
            Log.d("sprinter",wordd+":"+testdayy);
            holder._tvMenuWordRow.setText(wordd);
            holder._tvMenuMeaningRow.setText(meanin+" words");

            if(testdayy.charAt(7)=='1'){//the last letter represents whether it's active or not
                Log.d("sprinteryyy",wordd+":"+testdayy);
                holder._tvMenuWordRow.setBackgroundColor(getResources().getColor(R.color.koypurple,getTheme()));
                holder._tvMenuMeaningRow.setBackgroundColor(getResources().getColor(R.color.koypurple,getTheme()));
            }else{
                holder._tvMenuWordRow.setBackgroundColor(getResources().getColor(R.color.thePurple,getTheme()));
                holder._tvMenuMeaningRow.setBackgroundColor(getResources().getColor(R.color.thePurple,getTheme()));
            }
            //holder._tvMenuMeaningRow.setText(meanin);



            //Color.rgb(a,b,c)


        }
        @Override
        public int getItemCount(){
            return _listData.size();
        }
        //dont know why but it seems it doesn't override properly
        public Filter getFilter(){
            return exampleFilter;
        }

        private Filter exampleFilter=new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint){
                List<Map<String, String>> filteredList= new ArrayList<>();

                if(constraint==null||constraint.length()==0){
                    filteredList.addAll(_listDataFull);
                }else{
                    String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();

                    for(Map<String, String> item:_listDataFull){
                        String aa=(String)item.get("listName");
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
                Log.d("137483",String.valueOf(getItemCount()));
                notifyDataSetChanged();
            }
        };


    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case 1:

            break;
        }
    }*/
    /*private final ActivityResultLauncher<Intent> activityResultLauncher= registerForActivity(new ActivityResultContracts.StartActivityForResult(),
            result->{
              if(result.getResultCode()== Activity.RESULT_OK){
                  if(result.getData() != null){//遷移先で決める?

                  }
              }

            });*/
   private class ItemClickListener implements View.OnClickListener{
       @Override
       public void onClick(View view){//リストタップ
           RecyclerView recy=findViewById(R.id.lvRecycle);
           TextView meaningg=view.findViewById(R.id.daimei);
           TextView kotob=view.findViewById(R.id.tangosu);
           //database 更新

           //String namy=kotob.getText().toString();
           //positionを得てcheckIfvisibleを更新1 visible 0 invisible
           int pos=recy.getChildLayoutPosition(view);



           Log.d("ditto","newjeans"+pos);
           String taitoru=meaningg.getText().toString();
           //meaningg.setText("");
           Intent intent= new Intent(MainActivity.this, WordList.class);
           intent.putExtra("mode",0);
           intent.putExtra("title",taitoru);
           intent.putExtra("idNum", Long.valueOf(menuList.get(pos).get("id")));
           intent.putExtra("kazu", Long.valueOf(menuList.get(pos).get("kazu")));
           startActivity(intent);
           finish();


       }

   }
    protected void ListSetUpp(){
       // Log.d("njgf","ererg");
        lvMenu2 = findViewById(R.id.lvRecycle);
        layout = new LinearLayoutManager(MainActivity.this);
        lvMenu2.setLayoutManager(layout);
       // Log.d("njgf","ererg");
      //  ListView lvMenu=findViewById(R.id.lvMenu);

       // menuList=new ArrayList<>();

        Map<String, String> listMenu=new HashMap<>();
        Log.d("njgfa","ererg");
        //the final goal is to read sql database and add it to m
        SQLiteDatabase db=_helper.getWritableDatabase();//!!!!!!!!!!!!!!!!!!!
       // Log.d("njgf","ererg");ここから下がひゅお維持されない
        Log.d("njgfb","ererg");
        String sql="SELECT * FROM wordlist";
//Log.d("njgf","ererg");
        Cursor cursor=db.rawQuery(sql, null);
        String listnamae="";
        String testwhen="";
        String hinichi="--";
        String thetiming="0000";
        long listIy=-1;
        long eachid=-1;
        while(cursor.moveToNext()){
            int idxNote=cursor.getColumnIndex("listName");//楯列
            int idxNote2=cursor.getColumnIndex("wordsNum");
            int harry=cursor.getColumnIndex("_id");
            int testdays=cursor.getColumnIndex("testdays");
            int torokubi=cursor.getColumnIndex("torokuBi");
            int thetime=cursor.getColumnIndex("time");

            listnamae=cursor.getString(idxNote);
            listIy=cursor.getInt(idxNote2);
            eachid=cursor.getLong(harry);
            testwhen=cursor.getString(testdays);
            hinichi=cursor.getString(torokubi);
            thetiming=cursor.getString(thetime);

            listMenu.put("listName",listnamae);
            listMenu.put("kazu",String.valueOf(listIy));
            listMenu.put("id",String.valueOf(eachid));
            listMenu.put("testdays",testwhen);
            listMenu.put("torokubi",hinichi);
            listMenu.put("time",thetiming);
            menuList.add(listMenu);
            //その単語リストの単語数も知りたい
           // String numOfWord="SELECT COUNT(*) FROM words WHERE parent_id="+listIy;
          //  Cursor incursor=
            listMenu=new HashMap<>();
        }
        // menuList.add("fuckoff");
        //menuList.add("ohhh");
        adapter = new RecyclerListAdapter(menuList);
        lvMenu2.setAdapter(adapter);

     //   DividerItemDecoration decoration = new DividerItemDecoration(MainActivity.this, layout.getOrientation());
      //  lvMenu2.addItemDecoration(decoration);

        // ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, menuList);
       // lvMenu.setAdapter(adapter);
      //  lvMenu.setOnItemClickListener(new ListItemClickListener());
      //  lvMenu.setOnItemLongClickListener(new ItemLongClickListener());
    }
    private class ItemLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view){
            //りストがlonnguタップされ手スケジュールが開かれる
           // TextView listTitley=view.findViewById(R.id.)
            // startActivity(detail);
            RecyclerView recy=findViewById(R.id.lvRecycle);
            TextView meaningg=view.findViewById(R.id.daimei);
            TextView kotob=view.findViewById(R.id.tangosu);

            int pos=recy.getChildLayoutPosition(view);

            Intent intent= new Intent(MainActivity.this, ListDetail.class);
            intent.putExtra("title",meaningg.getText().toString());
            intent.putExtra("id",menuList.get(pos).get("id"));
            //Log.d("mainlistId",String.valueOf(menuList.get(pos).get("id")));
            intent.putExtra("testdays",menuList.get(pos).get("testdays"));
            intent.putExtra("torokubi",menuList.get(pos).get("torokubi"));
            intent.putExtra("time",menuList.get(pos).get("time"));
            //intent.putExtra("title",item);
            //  intent.putExtra("idNum",whichList);
            startActivity(intent);
            finish();

            return true;//by returning true onclick turns invalid
        }
    }
    //!使われてない
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            String item=(String)parent.getItemAtPosition(position);

            RecyclerView recy2=findViewById(R.id.lvRecycle);
            int pos=recy2.getChildLayoutPosition(view);
           // SQLiteDatabase db=_helper.getWritableDatabase();
           // String sql="SELECT * FROM wordlist WHERE listName="+item;
           // Cursor cursor=db.rawQuery(sql, null);
          /**  SQLiteDatabase dbb=_helper.getWritableDatabase();
            String fuck="SELECT * FROM wordlist WHERE listName="+item;
            Cursor cursor= dbb.rawQuery(fuck, null);
            //"idNum"
            int whichList=-1;
            while(cursor.moveToNext()){//its length must be one, otherwise fuck
                int idxNo=cursor.getColumnIndex("_id");
                whichList=cursor.getInt(idxNo);
            }**/

            Intent intent= new Intent(MainActivity.this, WordList.class);
            intent.putExtra("title",item);
            intent.putExtra("mode",2);
            Log.d("checkcheck",menuList.get(pos).get("id"));
            intent.putExtra("idNum",Long.valueOf(menuList.get(pos).get("id")));
            intent.putExtra("kazu",Long.valueOf(menuList.get(pos).get("kazu")));
           // intent.putExtra("idNum",whichList);
            startActivity(intent);
            //resultLauncher.launch(intent);



        }
    }


    private class HelloListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            int id=view.getId();
            Intent intent;
            switch (id){
               /* case R.id.button:
                    intent= new Intent(MainActivity.this, AddWordList.class);

                    break;*/
                case R.id.floatingActionButton5:
                    intent= new Intent(MainActivity.this, AddWordList.class);
                    break;

             /*   case R.id.button9: //search words
                    intent=new Intent(MainActivity.this, WordList.class);
                    intent.putExtra("mode",1);
                    Log.d("huh","why");
                    break;*/

                case R.id.floatingActionButton4://search words
                    intent=new Intent(MainActivity.this, WordList.class);
                    intent.putExtra("mode",1);
                    Log.d("huh","why");
                    break;

               // case R.id.button8:

                case R.id.floatingActionButton3://take a note

                default:
                    intent = new Intent(MainActivity.this, MemoListt.class);
                    //intent = new Intent(MainActivity.this, MainActivity2.class);

            }
            startActivity(intent);
           // finish();
        }
    }
}