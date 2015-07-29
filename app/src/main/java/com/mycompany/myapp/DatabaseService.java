package com.mycompany.myapp;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService extends Service {
    public DatabaseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<DataMb> data = new ArrayList<DataMb>();
        Data dop=new Data(this);
        long time = 0;
        data.clear();
        final PackageManager pm = getPackageManager();
        // get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        String s = "";
        // loop through the list of installed packages and see if the selected
        // app is in the list
        for (ApplicationInfo packageInfo : packages) {
            //if(packageInfo.sourceDir.startsWith("/data/app/")){
            // get the UID for the selected app
            int UID = packageInfo.uid;
            String package_name = packageInfo.packageName;
            ApplicationInfo app = null;

            try {
                app = pm.getApplicationInfo(package_name, 0);
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String name = (String) pm.getApplicationLabel(app);
            Drawable icon = (Drawable) pm.getApplicationIcon(app);
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
// Scale it to 50 x 50
            Drawable icon_resized = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 60, 60, true));

            // internet usage for particular app(sent and received)
            double received = (double) TrafficStats.getUidRxBytes(UID)

                    / (1024 * 1024);
            double send = (double) TrafficStats.getUidTxBytes(UID)
                    / (1024 * 1024);
            //double total = received + send;

            int count=0;
            SQLiteDatabase SQ =dop.getWritableDatabase();
            Cursor CR=dop.getInformation(dop);
            CR.moveToFirst();
            while(CR.moveToNext()){
                if(CR.getString(0).equals(name)){

                    count++;
                }

            }
            CR.close();

        /*if(count==0){
            ContentValues cv=new ContentValues();
            cv.put(Table.TableInfo.APPDATA,value);
            cv.put(Table.TableInfo.TIME,0);

            SQ.insert(Table.TableInfo.TABLE_NAME,null,cv);
            count=0;

        }*/
            SQ.close();



            time = 0;

            // s = s + name + "  " + String.format("%.2f MB", received) + "  " + String.format("%.2f MB", send) + "\n";


        }

        return START_STICKY;
    }
}
