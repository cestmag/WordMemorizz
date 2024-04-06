package com.websarva.wings.android.wordmemorizz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListDetail extends AppCompatActivity {
  //  private boolean[] daysOfWeek = new boolean[7]; // daysOfWeek[0] is Sunday, daysOfWeek[1] is Monday, etc.
 // private boolean[] daysOfWeek ={false,false,false,false,false,false,false};
    private int notificationHour = 0; // default notification time is 9:00 AM
    private int notificationMinute = 0;
    private DatabaseHelper _helper;
    private String title="";
    private String torokubi="--";
    private long idy=-1;
    private String testsche="00000000";
    private String firstTime="0000";
    List<CheckBox> checkarray=new ArrayList<>();

    private static final String CHANNEL_ID = "weekly_notification";
   // private PendingIntent pendingIntent;
   // private PendingIntent pendingIntent2;
  //  private AlarmManager alarmManager;
  //  private PendingIntent pendingIntent;
   /* private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Create and display the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Weekly Reminder")
                    .setContentText("It's time for your weekly reminder!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            notificationManager.notify(0, builder.build());
            Log.d("nowayout","pien");
        }
    };*/





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        _helper = new DatabaseHelper(ListDetail.this);

        Intent intent22 = getIntent();

        title = intent22.getStringExtra("title");



        idy = Long.valueOf(intent22.getStringExtra("id"));

        Log.d("listdetailId",String.valueOf(idy));

        testsche = intent22.getStringExtra("testdays");

        torokubi = intent22.getStringExtra("torokubi");

        firstTime=intent22.getStringExtra("time");

        Log.d("qwer",firstTime);

        int[] toki=dissolve(firstTime);

        notificationHour=toki[0];
        notificationMinute=toki[1];

        Button addbutton = findViewById(R.id.button5o);
        HelloListener hell = new HelloListener();
        addbutton.setOnClickListener(hell);

        TextView toroku = findViewById(R.id.textView12);

        toroku.setText("Registered on " + torokubi);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);

        Switch activate = findViewById(R.id.switch1);

        activate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(activate.isChecked()) {
                    //mSwitch : Off -> On の時の処理
                   changeState(1);
                    testsche=changeLastLetter(testsche,'1');
                    updatesTestDays(testsche,firstTime);
                    //Log.d("changedd","succesexx");
                    //button checkbox timepicker activated
                } else {
                    //alart to check if user really want to de-activate
                    //8番目の数字0->nonactivated 1->activated

                    cancelMane();

                    //button check box 非活性化
                    changeState(0);

                    testsche=changeLastLetter(testsche,'0');
                    updatesTestDays(testsche,timeLetter(notificationHour,notificationMinute));


                }
            }
        });

      /*  TimePicker timePicker = findViewById(R.id.simpleTimePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                notificationHour = hourOfDay;
                notificationMinute = minute;
                Log.d("changedTime",String.valueOf(minute));
            }
        });*/



       /* alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(ListDetail.this, MemoBroadcast.class);//notificationReceiver.getClass()
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/


        TimePicker timePicker = findViewById(R.id.simpleTimePicker);
        timePicker.setIs24HourView(true);

        timePicker.setHour(toki[0]);
        timePicker.setMinute(toki[1]);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                notificationHour = hourOfDay;
                notificationMinute = minute;
                //Toast.makeText(ListDetail.this,String.valueOf(notificationMinute),Toast.LENGTH_LONG).show();
                //Log.d("changedTime",String.valueOf(minute));
            }
        });

        CheckBox sundayCheckBox = findViewById(R.id.sundayCheckBox);
        CheckBox mondayCheckBox = findViewById(R.id.mondayCheckBox);
        CheckBox tuesdayCheckBox = findViewById(R.id.tuesdayCheckBox);
        CheckBox wednesdayCheckBox = findViewById(R.id.wednesdayCheckBox);
        CheckBox thursdayCheckBox = findViewById(R.id.thursdayCheckBox);
        CheckBox fridayCheckBox = findViewById(R.id.fridayCheckBox);
        CheckBox saturdayCheckBox = findViewById(R.id.saturdayCheckBox);



        checkarray=new ArrayList<>();

        checkarray.add(sundayCheckBox);
        checkarray.add(mondayCheckBox);
        checkarray.add(tuesdayCheckBox);
        checkarray.add(wednesdayCheckBox);
        checkarray.add(thursdayCheckBox);
        checkarray.add(fridayCheckBox);
        checkarray.add(saturdayCheckBox);

        if (testsche.charAt(7) == '1') {

            activate.setChecked(true);
            changeState(1);
        }else{
            activate.setChecked(false);
            changeState(0);
        }

        for(int i=0;i<checkarray.size();i++){//size must be 7, otherwise hell
            char c=testsche.charAt(i);
            final int index = i;
            CheckBox tom=checkarray.get(i);
            if(c=='1'){
                tom.setChecked(true);
            }else{
                tom.setChecked(false);
            }
            tom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        StringBuilder modifiedWord = new StringBuilder(testsche);
                        modifiedWord.setCharAt(index, '1');
                        testsche=modifiedWord.toString();
                        Log.d("14617494",String.valueOf(index));
                       // String result = modifiedWord.toString();
                        // CheckBox is checked, perform your action here
                        //Toast.makeText(MainActivity.this, "CheckBox is checked", Toast.LENGTH_SHORT).show();
                    } else {
                        StringBuilder modifiedWord = new StringBuilder(testsche);
                        modifiedWord.setCharAt(index, '0');
                        testsche=modifiedWord.toString();
                        Log.d("146174941",String.valueOf(index));
                        // CheckBox is unchecked, perform another action here
                        //Toast.makeText(MainActivity.this, "CheckBox is unchecked", Toast.LENGTH_SHORT).show();
                    }
                    //ここupdateいる?いらなくないか
                    //updatesTestDays(testsche,timeLetter(notificationHour,notificationMinute));
                }
            });

        }

      /*  sundayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(sundayCheckBox.isChecked()==true){
                    daysOfWeek[0] = true;
                }else{
                    daysOfWeek[0] = false;
                }
            }
        });
        mondayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mondayCheckBox.isChecked()==true){
                    daysOfWeek[1] = true;
                }else{
                    daysOfWeek[1] = false;
                }

            }
        });
        tuesdayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(tuesdayCheckBox.isChecked()==true){
                    daysOfWeek[2] = true;
                }else{
                    daysOfWeek[2] = false;
                }
            }
        });
        wednesdayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(wednesdayCheckBox.isChecked()==true){
                    daysOfWeek[3] = true;
                }else{
                    daysOfWeek[3] = false;
                }
            }
        });
        thursdayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(thursdayCheckBox.isChecked()==true){
                    daysOfWeek[4] = true;
                }else{
                    daysOfWeek[4] = false;
                }
            }
        });
        fridayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(fridayCheckBox.isChecked()==true){
                    daysOfWeek[5] = true;
                }else{
                    daysOfWeek[5] = false;
                }
            }
        });
        saturdayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(saturdayCheckBox.isChecked()==true){
                    daysOfWeek[6] = true;
                }else{
                    daysOfWeek[6] = false;
                }
            }
        });*/

    }
    private void changeState(int mode){
        TimePicker timePicker = findViewById(R.id.simpleTimePicker);
        Button addbutton = findViewById(R.id.button5o);
       switch (mode){
           case 0://inert
               testsche=changeLastLetter(testsche,'0');
               for(int o=0;o<checkarray.size();o++){
                   checkarray.get(o).setEnabled(false);
               }
               timePicker.setEnabled(false);
               addbutton.setEnabled(false);
               break;
           case 1://active
               testsche=changeLastLetter(testsche,'1');
               for(int o=0;o<checkarray.size();o++){
                   checkarray.get(o).setEnabled(true);
               }
               timePicker.setEnabled(true);
               addbutton.setEnabled(true);
               break;
       }
        updatesTestDays(testsche,timeLetter(notificationHour,notificationMinute));
    }
    private String changeLastLetter(String str, char lastLetter) {
        if (str == null || str.isEmpty()) {
            return str; // handle null or empty input string
        }
        char[] chars = str.toCharArray(); // convert the string to a character array
        chars[chars.length - 1] = lastLetter; // replace the last character with the specified letter
        return new String(chars); // convert the character array back to a string and return it
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        modoru();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal=true;

        int itemId=item.getItemId();

        switch (itemId) {
            case R.id.menuListOption22:
                //delete
                //3kaiも確認する
                _helper=new DatabaseHelper(ListDetail.this);

                SQLiteDatabase dbb = _helper.getWritableDatabase();

                String sqlInsert = "DELETE FROM wordlist WHERE _id=" + idy;

                //open wordlist and decrease Numwords by 1

                SQLiteStatement stmt=dbb.compileStatement(sqlInsert);

                stmt.executeUpdateDelete();//update

                SQLiteDatabase db=_helper.getWritableDatabase();
                //listに含まれる単語も全部消す
                try {
                //文字列000000(テストしていない)でなかったら、alarmもcancelする必要がある!!!!!!!!!!!!!!!!!
                   // ListDetail a=new ListDetail();

                    db.beginTransaction();
                    //maru ka batuno hyouzi
                    //correctRate=correctRate+" + correctOrNot + "
                    String forupdates = "DELETE FROM words WHERE parent_id="+idy;
                    SQLiteStatement stmtt = db.compileStatement(forupdates);

                    stmtt.executeUpdateDelete();//update

                    db.setTransactionSuccessful(); //try and catch 構文を使う
                }catch(Exception e){
                    //what the fucking hell shit
                } finally {
                    db.endTransaction();
                }
                break;
            case R.id.menuListOption11:
                //edit


                break;

            case android.R.id.home:
                //back home
                modoru();
                break;
        }
        return returnVal;
    }

    private void modoru(){
        Intent hypeboy=new Intent(ListDetail.this, MainActivity.class);

        startActivity(hypeboy);

        finish();
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){//もとはO(オー)だった
            //CharSequence name="PASTICCINO";
            String name = "test";
            String description="ahahaha";
            int importance =NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

            //return true;
        }/*else{
            Toast.makeText(this,"something is wring with it",Toast.LENGTH_LONG).show();
        }*/
        //return false;
    }
    private String timeLetter(int a,int b){
        String ay=String.valueOf(a);
        String oy=String.valueOf(b);
        if(a<10){
             ay="0"+ay;
        }
        if(b<10){
            oy="0"+oy;
        }
        return ay+oy;
    }
    private int[] dissolve(String tar){
        int[] ha=new int[2];
        String str1 = "0";
        String str2 = "0";
        if(tar.length()==4){
            str1=tar.substring(0,2);
            str2=tar.substring(2,4);
            if(str1.charAt(0)=='0'){
               str1=str1.substring(1,2);
            }

            if(str2.charAt(0)=='0'){
                str2=str2.substring(1,2);
            }
        }

        ha[0]=Integer.valueOf(str1);
        ha[1]=Integer.valueOf(str2);


        return ha;
    }

    private void startWeeklyNotifications(int dayofweek) {
        // Set up a calendar object to represent the notification time

        //boolean gook =
        createNotificationChannel();

        //if (gook) {


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayofweek);//add

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        calendar.set(Calendar.HOUR_OF_DAY, notificationHour);//notificationHour
        calendar.set(Calendar.MINUTE, notificationMinute);//notificationMinute
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(ListDetail.this, MemoBroadcast.class);//notificationReceiver.getClass()
        intent.putExtra("title", title);
        intent.putExtra("id", idy);
        Log.d("setidy", String.valueOf(idy));
        intent.putExtra("day", dayofweek);

        int idForpending = Integer.valueOf(String.valueOf(idy) + String.valueOf(dayofweek));
        //!!!!!!!!!!!!!!!PendingIntentが問題
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ListDetail.this, idForpending, intent, PendingIntent.FLAG_IMMUTABLE);//PendingIntent.FLAG_UPDATE_CURRENT

        // PendingIntent pendingIntent2;


        //  PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
       AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//もとはMだった
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        //Log.d("kiminonawa",title);
    //}






        // Set up a repeating alarm for the selected days of the week
      /*  for (int i = 0; i < 7; i++) {
            Log.d("nakami",String.valueOf(daysOfWeek[i]));
            if (daysOfWeek[i]) {
                calendar.set(Calendar.DAY_OF_WEEK, i + 1);
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                }
               // AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                Log.d("oddy7","qwe");
            }
        }*/
    }
    private int currentTimeMillis() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    private void stopWeeklyNotifications() {
        // Cancel the repeating alarm
      // alarmManager.cancel(pendingIntent);
    }


   /* private void scheduleAlarm(int dayOfWeek) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        // Set this to whatever you were planning to do at the given time
        Intent intent=new Intent(ListDetail.this, fQuiz.class);

        PendingIntent yourIntent=PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

       AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, yourIntent);
    }*/

   /* private void setUpAlarms() {

        scheduleAlarm(Calendar.MONDAY);
        scheduleAlarm(Calendar.FRIDAY);

    }*/
    private void cancelMane(){
        for(int i=0;i<testsche.length()-1;i++){//-1 ha ichiban saigo wa active or not
            char c=testsche.charAt(i);
            if(c=='1'){
                int pendId=Integer.valueOf(String.valueOf(idy)+String.valueOf(i+1));
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent intent = new Intent(ListDetail.this, MemoBroadcast.class);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(ListDetail.this, pendId, intent, PendingIntent.FLAG_IMMUTABLE);//PendingIntent.FLAG_UPDATE_CURRENT

                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }
        }
    }
    private void updatesTestDays(String sched,String thetime){
        SQLiteDatabase db=_helper.getWritableDatabase();
        try {



            db.beginTransaction();
            //maru ka batuno hyouzi
            //correctRate=correctRate+" + correctOrNot + "
            String forupdates = "UPDATE wordlist SET testdays=?,time=? WHERE _id="+idy;
            SQLiteStatement stmtt = db.compileStatement(forupdates);
            //Log.d("peter",thetime);
            stmtt.bindString(1,sched);
            stmtt.bindString(2,thetime);

            stmtt.executeUpdateDelete();//update

            db.setTransactionSuccessful(); //try and catch 構文を使う
        }catch(Exception e){
            //what the fucking hell shit
        } finally {
            db.endTransaction();
        }

        //also change time
    }

    private class HelloListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //曜日の情報も読み込む
            cancelMane();//!!!!!!!!!!!!!問題1 alarmmanegaer関連?->PendingIntent android 12 error

            String testdays="";
            for(int i=0;i<7;i++){
                if(checkarray.get(i).isChecked()){
                    startWeeklyNotifications(i+1);
                    //Log.d("whichdays",String.valueOf(i+1));//問題2 alarmmanager関連阿?
                    testdays+="1";
                }else{
                    testdays+="0";
                }

            }

            testdays+="1";//8文字目 1:active 0:inert

            if(testdays.length()!=8){//just in case
              testdays="00000000";
            }

            testsche=testdays;

            updatesTestDays(testsche,timeLetter(notificationHour,notificationMinute));

            Toast.makeText(ListDetail.this,"Test set!",Toast.LENGTH_LONG).show();
         //  startWeeklyNotifications(Calendar.MONDAY);
           //sun 1 mon 2 tue 3 wed 4 thur 5 fri 6 satur 7 int
          // Log.d("nahhhh","hey");
        }
    }

}