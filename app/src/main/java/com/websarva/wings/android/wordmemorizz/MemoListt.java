package com.websarva.wings.android.wordmemorizz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//エラー有メモ変更すると変
public class MemoListt extends AppCompatActivity implements MemoDeleteComfirm.DialogListener{
    private DatabaseHelper _helper;
    List<Map<String,String>> menuList=new ArrayList<>();
    RecyclerListAdapter adapter;
    RecyclerView lvMenu22;
    LinearLayoutManager layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_listt);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("MemoList");

        _helper=new DatabaseHelper(MemoListt.this);
        ArrayList<View> viewsToFadeIn = new ArrayList<View>();

        FloatingActionButton fab=findViewById(R.id.floatingActionButton10);
        HelloListener lis=new HelloListener();
        viewsToFadeIn.add(fab);


        FloatingActionButton fab2=findViewById(R.id.floatingActionButton11);
        viewsToFadeIn.add(fab2);

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
        ListSetUpp();
        /*SQLiteDatabase db22=_helper.getWritableDatabase();

        String fuck2 = "SELECT content FROM memo";//must be one

        Cursor cursor2 = db22.rawQuery(fuck2, null);*/


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
            _tvMenuWordRow=itemView.findViewById(R.id.titlee);

            _onrRow=itemView.findViewById(R.id.linearcar22);
            _cardview=itemView.findViewById(R.id.sprinter);
        }


    }
    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder>{
        private List<Map<String,String>> _listData;
        public RecyclerListAdapter(List<Map<String,String>> listData){
            _listData = listData;
        }
        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater=LayoutInflater.from(MemoListt.this);
            View view = inflater.inflate(R.layout.simple_item3, parent, false);

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


            Map<String,String> qwe= _listData.get(position);
            String thetitle=qwe.get("con");

            holder._tvMenuWordRow.setText(firstletters(thetitle,10));


        }
        @Override
        public int getItemCount(){
            return _listData.size();
        }


    }
    private String firstletters(String content, int num) {
        if (content.length() >= num) {

        String a = content.substring(0, num);
        return a;
        }else{
            return content;
        }
    }
    private class ItemClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){//リストタップ
            RecyclerView recy=findViewById(R.id.recyclememo);
            //TextView meaningg=view.findViewById(R.id.daimei);
            //TextView kotob=view.findViewById(R.id.tangosu);
            //database 更新

            //String namy=kotob.getText().toString();
            //positionを得てcheckIfvisibleを更新1 visible 0 invisible
            int pos=recy.getChildLayoutPosition(view);
            String keyy=menuList.get(pos).get("id");
            if(keyy.equals("-1")){
                Toast.makeText(MemoListt.this, "That's already deleted:(", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(MemoListt.this, QuickNote.class);
                intent.putExtra("id", menuList.get(pos).get("id"));
                startActivity(intent);
                finish();
            }




            //meaningg.setText("");
           /* Intent intent= new Intent(MainActivity.this, WordList.class);
            intent.putExtra("mode",0);
            intent.putExtra("title",taitoru);
            intent.putExtra("idNum", Integer.valueOf(menuList.get(pos).get("id")));
            intent.putExtra("kazu", Integer.valueOf(menuList.get(pos).get("kazu")));
            startActivity(intent);
            finish();*/


        }

    }

    protected void ListSetUpp(){
        lvMenu22 = findViewById(R.id.recyclememo);
        layout = new LinearLayoutManager(MemoListt.this);
        lvMenu22.setLayoutManager(layout);

        SQLiteDatabase db22=_helper.getWritableDatabase();

        String fuck2 = "SELECT * FROM memo";//must be one

        Cursor cursor2 = db22.rawQuery(fuck2, null);
        int alreadyExist=0;
        menuList=new ArrayList<>();
        Map<String,String> suby=new HashMap<>();
        while (cursor2.moveToNext()) {//its length must be one, otherwise fuck
            int idxNo = cursor2.getColumnIndex("content");
            int primenum=cursor2.getColumnIndex("_id");
            String previousOne=cursor2.getString(idxNo);
            String bango=String.valueOf(cursor2.getLong(primenum));

            suby.put("id",bango);
            suby.put("con",previousOne);

            menuList.add(suby);
            suby=new HashMap<>();
            alreadyExist++;
        }
        adapter = new RecyclerListAdapter(menuList);
        lvMenu22.setAdapter(adapter);

    }
    private void showConfirmationDialog(final long idToDelete, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation bro");
        builder.setMessage("Yo sup, u really sure wanna delete this data, huh?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the positive button click (User wants to delete the data)
                _helper.deleteItem(idToDelete);
                menuList.get(pos).put("id","-1");
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the negative button click (User canceled the deletion)
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private class HelloListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            int id=view.getId();
            Intent intent;
            switch (id){

                case R.id.floatingActionButton10://add memo
                 /*   SQLiteDatabase dby=_helper.getWritableDatabase();
                    String a="INSERT INTO memo (_id,content ) VALUES(?,?)";
                    SQLiteStatement stmt=dby.compileStatement(a);

                    stmt.bindString(2,"");
                    //  stmt.bindString(3,"memo");
                    stmt.executeInsert();*/
                    //どうやってidを得るか
                    String a[]={""};
                    long theIdOf=_helper.insertData("memo",a);


                    intent= new Intent(MemoListt.this, QuickNote.class);
                    intent.putExtra("id",String.valueOf(theIdOf));
                    break;


                case R.id.floatingActionButton11://back home
                    intent=new Intent(MemoListt.this, MainActivity.class);
                  //  intent.putExtra("mode",1);
                    Log.d("huh","why");
                    break;


                default:
                    intent=new Intent(MemoListt.this, QuickNote.class);

            }
            startActivity(intent);
            finish();
        }
    }
    @Override//confirm button no ok button osaretatoki
    public void onOkButtonClicked(int a) {
        // Handle the OK button click here
        // For example, proceed with the operation
        // when the user clicks OK.
    }

    private class ItemLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view){
          //  MemoDeleteComfirm letsdelete=new MemoDeleteComfirm();
         /*   RecyclerView recy = findViewById(R.id.recyclememo);
            int pos = recy.getChildLayoutPosition(view);

            Map<String, String> item = menuList.get(pos);
            long realId = Long.parseLong(item.get("id"));
            Log.d("2935235:",String.valueOf(realId));

            _helper.deleteItem(realId);*/
            RecyclerView recy = findViewById(R.id.recyclememo);
            int pos = recy.getChildLayoutPosition(view);

            Map<String, String> item = menuList.get(pos);
            long realId = Long.parseLong(item.get("id"));
            showConfirmationDialog(realId, pos);
            return true;
        }
    }
}