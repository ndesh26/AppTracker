package com.mycompany.myapp;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
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

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.util.Timer
        ;
import java.util.TimerTask;
import java.lang.String;



public class MyService extends Service {
    String foregroundTaskPackageName="";
    List<UsageStats> usageStatses;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    Context ctx=this;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < 20) {
            store(this);
        }
        else{
            usageStatses=UStats.getUsageStatsList(ctx);
            final Data dp=new Data(this);
            for (UsageStats u : usageStatses){
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(u.getLastTimeUsed());
                dp.putInformation(dp, u.getPackageName().toString(),u.getTotalTimeInForeground()/1000,calendar.getTime().toString());
            }

        }
        stopSelf();


        return START_STICKY;

    }

    public void store(Context c){
        final Data dp=new Data(c);
        final ActivityManager am = (ActivityManager) c.getSystemService(ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT < 20){
            foregroundTaskPackageName =am.getRunningAppProcesses().get(0).processName;
        }
        else{
            foregroundTaskPackageName =   am.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dp.putInformation(dp, foregroundTaskPackageName.toString(),1,calendar.getTime().toString());

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