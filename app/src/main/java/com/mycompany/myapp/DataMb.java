package com.mycompany.myapp;

import android.graphics.drawable.Drawable;

import java.text.DateFormat;

/**
 * Created by ndesh on 3/29/15.
 */
public class DataMb {
    public String name;
    public double received;
    public double send;
    public long time;
    public Drawable icon;
    public String lastuse;






    public DataMb(String name, double received, double send,long time,Drawable icon,String lastuse) {
        this.name = name;
        this.received = received;
        this.send = send;
        this.time=time;
        this.icon=icon;
        this.lastuse=lastuse;


    }
    public long getTime(){
        return time;
    }
    public String getName() {
        return name;
    }
    public Drawable getIcon(){
        return icon;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastuse(){return lastuse;}
    public double getReceived() {
        return received;
    }

    public void setReceived(double  received) {
        this.received = received;
    }

    public double  getSend() {
        return send;
    }

    public void setSend(double  send) {
        this.send = send;
    }
}
