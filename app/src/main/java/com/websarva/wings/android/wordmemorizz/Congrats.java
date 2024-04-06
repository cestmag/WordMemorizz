package com.websarva.wings.android.wordmemorizz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class Congrats extends AppCompatActivity {
    private String title="";
    private int listid=-1;
    private boolean fromN=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats);
        //Log.d("why","hey why why why fuck you bitch step off");
        Intent intent22 = getIntent();
        title = intent22.getStringExtra("listT");
        listid = intent22.getIntExtra("id", 0);
        fromN = intent22.getBooleanExtra("fromN",false);

        Button button = findViewById(R.id.button10);
        Listenerr lis = new Listenerr();
        button.setOnClickListener(lis);

        TextView resultt = findViewById(R.id.textView11);
        int a = intent22.getIntExtra("entire", 0);
        int b = intent22.getIntExtra("corrects", 0);
        Log.d("safeOrNot",title+" : "+listid+" : "+a+" : "+b);
        //double b/a
        double seitoritsu =0;
        if (a != 0) {

        seitoritsu = (double) b / a;
        Log.d("hiii","my name is asshole");
        }
        String disp=b+"out of "+a;
        String message="You gotta study way harder than now bro";
        if(0<=seitoritsu&&seitoritsu<0.6){
            //failed rank F
            disp+=" : F";
        }else if(0.6<=seitoritsu&&seitoritsu<0.7){
            // rank C
            disp+=" :C";
        }else if(0.7<=seitoritsu&&seitoritsu<0.8){
            //rank B
            disp+=" :B";
        }else if(0.8<=seitoritsu&&seitoritsu<0.9){
            //rank A
            disp+=" :A";
        }else if(0.9<=seitoritsu&&seitoritsu<=1){
            //rank S
            disp+=" :S";
        }

        resultt.setText(disp);

        Log.d("hiii","my name is assholeyay");



    }
    //back to word list when user click button

    private class Listenerr implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                //back
                case R.id.button10:

                 Intent intent=new Intent();
                //  intent.putExtra("idNum",listid);
                //  intent.putExtra("title",title);
              //    startActivity(intent);
                    //Log.d("hiii","my name is yooassholeyay");
                   setResult(RESULT_OK,intent);
                    finish();

                break;

            }

        }
    }
}