package com.websarva.wings.android.wordmemorizz;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MemoBroadcast extends BroadcastReceiver {
    private DatabaseHelper _helper;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Create and display the notification
         String title=intent.getStringExtra("title");
         int idy=(int)intent.getLongExtra("id",-1);
        // int idy_int = Integer.
         int day=intent.getIntExtra("day",-1);
         int idForpending=Integer.valueOf(String.valueOf(idy)+String.valueOf(day));

         String desc="It's time to study ! WordList:"+title;
         Log.d("39489",String.valueOf(idy));



     /*  SQLiteDatabase dbb = _helper.getWritableDatabase();
        String bun = "SELECT * FROM wordlist WHERE _id='" + idy + "' ";
        Cursor cursor;
        cursor = dbb.rawQuery(bun, null);*/

        //int maxwords=4;

        //while (cursor.moveToNext()) {//its length must be one, otherwise f**k
        /*    int idxNo = cursor.getColumnIndex("wordsNum");

            maxwords = cursor.getInt(idxNo);
        }*/

         Intent quizStart=new Intent(context, fQuiz.class);
         quizStart.putExtra("listId",idy);
         Log.d("notify_id",String.valueOf(idy));
         quizStart.putExtra("titlel",title);
       //  quizStart.putExtra("max",maxwords);
         quizStart.putExtra("fromNotification",true);

         quizStart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,idForpending/*idy*/,quizStart,PendingIntent.FLAG_UPDATE_CURRENT);
    //    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Notification")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Memorizz")
                .setContentText(desc)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

      NotificationManagerCompat notificationMan=NotificationManagerCompat.from(context);

        notificationMan.notify(idy, builder.build());//id リストごとに異なる固有
        Log.d("nowayout",title);
    }
    private int currentTimeMillis() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
}
