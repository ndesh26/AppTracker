package com.mycompany.myapp;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;


import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.Timer
        ;
import java.util.TimerTask;
import java.lang.String;



public class MyService extends Service {
    String foregroundTaskPackageName="";
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    Context ctx=this;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        //Timer timer = new Timer();
        //runAsForeground();

       /* TimerTask refresher = new TimerTask() {
            public void run() {
               store(am,dp,c);
            };

        };
        //TIMER RUNS EVERY 1 SECOND
        timer.scheduleAtFixedRate(refresher, 1000,1000);

        // Intent u=new Intent(this,MyService.class);
        //startService(u);



        //onDestroy();

*/
        store(this);
        stopSelf();
        return START_STICKY;

    }
   /* private void runAsForeground(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
                notificationIntent,0);

        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("Logging Data")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("is monitoring")
                .setContentIntent(pendingIntent).build();

        startForeground(101, notification);

    }*/
    public void store(Context c){
        final Data dp=new Data(c);
        final ActivityManager am = (ActivityManager) c.getSystemService(ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT > 20){
            foregroundTaskPackageName =am.getRunningAppProcesses().get(0).processName;
        }
        else{
            foregroundTaskPackageName =   am.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        //ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);



        //String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
        PackageManager pm = c.getPackageManager();
        PackageInfo foregroundAppPackageInfo = null;
        try {
            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
        //Log.i("Foreground app Service",foregroundTaskAppName);

        dp.putInformation(dp, foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // stopForeground(true);
        //Toast.makeText(this, "Service2 Destroyed", Toast.LENGTH_LONG).show();
        //Intent u=new Intent(this,MyService.class);
        //startService(u);
    }
}