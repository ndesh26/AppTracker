package com.mycompany.myapp;



import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by ndesh on 2/4/15.
 */
public class Data extends SQLiteOpenHelper {
    public static final int database_version=1;
    public String CREATE_QUERY="create table "+Table.TableInfo.TABLE_NAME+" ("+ Table.TableInfo.APPDATA+" TEXT," +Table.TableInfo.TIME+" LONG,"+ Table.TableInfo.LASTUSE+" TEXT)";
    public Data(Context context) {
        super(context, Table.TableInfo.DATABASE_NAME, null, database_version);
    }
       long time;

    @Override
    public void onCreate(SQLiteDatabase sdb){
        sdb.execSQL(CREATE_QUERY);
        Log.d("database operations","table created");
    }
    public void putInformation(Data dop,String value,long time1,String lastuse_new){
        int count=0;
        SQLiteDatabase SQ =dop.getWritableDatabase();
        Cursor CR=getInformation(dop);
        CR.moveToFirst();
        while(CR.moveToNext()){
           if(CR.getString(0).equals(value)){
                time=CR.getLong(1);
                time=time+time1;
                update(dop,CR.getString(0),time,lastuse_new);
                count++;
                break;
           }

        }
        CR.close();

        if(count==0){
            ContentValues cv=new ContentValues();
            cv.put(Table.TableInfo.APPDATA,value);
            cv.put(Table.TableInfo.TIME,0);
            cv.put(Table.TableInfo.LASTUSE,DateFormat.getDateTimeInstance().format(new Date()));
            SQ.insert(Table.TableInfo.TABLE_NAME,null,cv);


        }
        SQ.close();



    }
    public Cursor getInformation(Data dop){
        SQLiteDatabase sq=dop.getReadableDatabase();
        String coloumns[]={Table.TableInfo.APPDATA, Table.TableInfo.TIME,  Table.TableInfo.LASTUSE};
        Cursor CR = sq.query(Table.TableInfo.TABLE_NAME,coloumns,null,null,null,null,null);
        return CR;}
    public void update(Data dop,String appname,long time,String lastuse){
        SQLiteDatabase SQ=dop.getWritableDatabase();
        String selection= Table.TableInfo.APPDATA+" LIKE ?";
        String args[]={appname};
        ContentValues cv=new ContentValues();
        cv.put(Table.TableInfo.TIME,time);
        cv.put(Table.TableInfo.LASTUSE, lastuse);
        SQ.update(Table.TableInfo.TABLE_NAME,cv,selection,args);
        SQ.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
