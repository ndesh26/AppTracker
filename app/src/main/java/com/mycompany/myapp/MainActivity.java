/*package com.mycompany.myapp;

import android.app.*;
import android.os.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}*/
	package com.mycompany.myapp;

import android.app.ActionBar;
import android.app.Activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


	public class MainActivity extends ActionBarActivity {

		Context ctx=this;
		ListView list;
		List<DataMb> data=new ArrayList<DataMb>();
		private ProgressBar mProgress;
		private int mProgressStatus = 0;

		private Handler mHandler = new Handler();
		AlarmManager alarm;
		int current_sort=0;
		PendingIntent pintent;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);

			list=(ListView)findViewById(R.id.listView2);
			Intent intent = new Intent(this, MyService.class);
			pintent = PendingIntent.getService(this, 0, intent, 0);
			alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pintent);
			mProgress = (ProgressBar) findViewById(R.id.progressBar);

			// Set the adapter for the list view
			mProgress.setVisibility(View.VISIBLE);
			list.setVisibility(View.INVISIBLE);

			// Start lengthy operation in a background thread
			new Thread(new Runnable() {
				public void run() {
					while (mProgressStatus < 100) {
						createList(0);
						sort(0);

						// Update the progress bar
						mHandler.post(new Runnable() {
							public void run() {
								populateViews(0);
								mProgressStatus=100;
								list.setVisibility(View.VISIBLE);
								mProgress.setVisibility(View.INVISIBLE);

							}
						});
					}
				}
			}).start();
			mProgressStatus=0;

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
										long id) {

					DataMb currentApp =data.get(position);
					AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
					builder.setTitle(currentApp.getName())
							.setIcon(currentApp.getIcon())
							.setMessage("Time of Use - "+String.valueOf(((currentApp.getTime()/60)/60)/24)+"d "+String.valueOf(((currentApp.getTime()/60)/60)%24)+"hr "+String.valueOf((currentApp.getTime()/60)%60)+"m "+String.valueOf(currentApp.getTime()%60)+"s"+"\n"+"Received Data - "+String.format("%.2f",currentApp.getReceived())+" MB"+"\n"+"Sent Data - "+String.format("%.2f",currentApp.getSend())+" MB"+"\n"+"Last Time of Launch - "+currentApp.getLastuse()+"\n"+"App Directory - "+currentApp.getSourceDir());
					Dialog dialog =builder.create();
					dialog.show();

				}




			});
		}

		@Override
		protected void onStart(){
			super.onStart();



		}



		public void createList(int i) {
            long time = 0;
			String lastuse="",SourceDir="";
            data.clear();
            final PackageManager pm = getPackageManager();
            // get a list of installed apps.
            List<ApplicationInfo> packages = pm.getInstalledApplications(0);
            String s = "";
            // loop through the list of installed packages and see if the selected
            // app is in the list
            if(i==1){
            for (ApplicationInfo packageInfo : packages) {
                if(((packageInfo.flags & packageInfo.FLAG_SYSTEM) != 0)){
                // get the UID for the selected app
                int UID = packageInfo.uid;
                String package_name = packageInfo.packageName;
                ApplicationInfo app = null;
				SourceDir=packageInfo.dataDir;
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

                Data dop = new Data(this);
                Cursor CR = dop.getInformation(dop);
                CR.moveToFirst();
                while (CR.moveToNext()) {

                    if (CR.getString(0).equals(name)) {
                        time = CR.getLong(1);
						lastuse=CR.getString(2);
                        break;
                    }

                }
                CR.close();
                data.add(new DataMb(name, received, send, time, icon_resized,lastuse,SourceDir));
                time = 0;

                // s = s + name + "  " + String.format("%.2f MB", received) + "  " + String.format("%.2f MB", send) + "\n";


            }  }}
           else if(i==2){
                for (ApplicationInfo packageInfo : packages) {
                    //if(packageInfo.sourceDir.startsWith("/data/app/")){
                    // get the UID for the selected app
                    int UID = packageInfo.uid;
                    String package_name = packageInfo.packageName;
                    ApplicationInfo app = null;
					SourceDir=packageInfo.dataDir;
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

                    Data dop = new Data(this);
                    Cursor CR = dop.getInformation(dop);
                    CR.moveToFirst();
                    while (CR.moveToNext()) {

                        if (CR.getString(0).equals(name)) {
                            time = CR.getLong(1);
							lastuse=CR.getString(2);
                            break;
                        }

                    }
                    CR.close();
                    data.add(new DataMb(name, received, send, time, icon_resized,lastuse,SourceDir));
                    time = 0;

                    // s = s + name + "  " + String.format("%.2f MB", received) + "  " + String.format("%.2f MB", send) + "\n";


                }
                }

            else if(i==0){
                for (ApplicationInfo packageInfo : packages) {
                    if(!((packageInfo.flags & packageInfo.FLAG_SYSTEM) != 0)){
                        // get the UID for the selected app
                        int UID = packageInfo.uid;
                        String package_name = packageInfo.packageName;
                        ApplicationInfo app = null;
						SourceDir=packageInfo.dataDir;
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

                        Data dop = new Data(this);
                        Cursor CR = dop.getInformation(dop);
                        CR.moveToFirst();
                        while (CR.moveToNext()) {

                            if (CR.getString(0).equals(name)) {
                                time = CR.getLong(1);
								lastuse=CR.getString(2);
                                break;
                            }

                        }
                        CR.close();
                        data.add(new DataMb(name, received, send, time, icon_resized,lastuse,SourceDir));
                        time = 0;

                        // s = s + name + "  " + String.format("%.2f MB", received) + "  " + String.format("%.2f MB", send) + "\n";


                    }
                }
            }
        }
		public void sort(int i){
			if(i==1){
				Collections.sort(data, new Comparator() {
						public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
//use instanceof to verify the references are indeed of the type in question
							return ((DataMb) synchronizedListOne).name
								.compareTo(((DataMb) synchronizedListTwo).name);
						}
					});}
			if(i==0){
				Collections.sort(data, new Comparator() {
						public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
//use instanceof to verify the references are indeed of the type in question
							if(((DataMb)synchronizedListOne).time<((DataMb)synchronizedListTwo).time)
								return 1;
							else return -1;
						}
					});
			}
			if(i==2){
				Collections.sort(data, new Comparator() {
						public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
//use instanceof to verify the references are indeed of the type in question
							if(((DataMb)synchronizedListOne).received<((DataMb)synchronizedListTwo).received)
								return 1;
							else return -1;
						}
					});
			}
			if(i==3){
				Collections.sort(data, new Comparator() {
						public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
//use instanceof to verify the references are indeed of the type in question
							if(((DataMb)synchronizedListOne)==null)return -1;
							if(((DataMb)synchronizedListTwo)==null)return -1;
							if(((DataMb)synchronizedListOne).send<((DataMb)synchronizedListTwo).send)
								return 1;
							else return -1;
						}
					});
			}
		}

		public void populateViews(int i){
			ArrayAdapter<DataMb> adapter=null;
			if(i==0) {

				adapter = new myListAdapterTime();
			}
			if(i==1) {

				adapter = new myListAdapterName();
			}
			if(i==2) {

				adapter = new myListAdapterReceived();
			}
			if(i==3) {

				adapter = new myListAdapterSend();
			}

			list.setAdapter(adapter);


		}
		private class myListAdapterTime extends ArrayAdapter<DataMb>{
			public myListAdapterTime() {
				super(MainActivity.this,R.layout.time, data);
			}

			@Override
			public View getView(int position,View convertView,ViewGroup parent){
				//return super.getView(position,convertView,parent);
				View itemView=convertView;
				if(itemView==null){
					itemView=getLayoutInflater().inflate(R.layout.time,parent,false);

				}
				DataMb currentApp=data.get(position);
				TextView name=(TextView)itemView.findViewById(R.id.name);
				name.setText(currentApp.getName());
				String s;
				TextView time=(TextView)itemView.findViewById(R.id.receieved);

                if(((currentApp.getTime()/60)/60)/24==0&&((currentApp.getTime()/60)/60)%24==0){
                    s=String.valueOf((currentApp.getTime()/60)%60)+"m "+String.valueOf(currentApp.getTime()%60)+"s";
                }
                else if(((currentApp.getTime()/60)/60)/24==0){
                    s=String.valueOf(((currentApp.getTime()/60)/60)%24)+"hr "+String.valueOf((currentApp.getTime()/60)%60)+"m "+String.valueOf(currentApp.getTime()%60)+"s";
                }
                else{
                    s=String.valueOf(((currentApp.getTime()/60)/60)/24)+"d "+String.valueOf(((currentApp.getTime()/60)/60)%24)+"hr "+String.valueOf((currentApp.getTime()/60)%60)+"m "+String.valueOf(currentApp.getTime()%60)+"s";
                }
				time.setText(s);
				ImageView icon=(ImageView)itemView.findViewById(R.id.imageView2);
				icon.setImageDrawable(currentApp.getIcon());


				return itemView;
			}

		}
		private class myListAdapterName extends ArrayAdapter<DataMb>{
			public myListAdapterName() {
				super(MainActivity.this,R.layout.time, data);
			}

			@Override
			public View getView(int position,View convertView,ViewGroup parent){
				//return super.getView(position,convertView,parent);
				View itemView=convertView;
				if(itemView==null){
					itemView=getLayoutInflater().inflate(R.layout.time,parent,false);

				}
				DataMb currentApp=data.get(position);
				TextView name=(TextView)itemView.findViewById(R.id.name);
				name.setText(currentApp.getName());

				ImageView icon=(ImageView)itemView.findViewById(R.id.imageView2);
				icon.setImageDrawable(currentApp.getIcon());


				return itemView;
			}

		}
		private class myListAdapterReceived extends ArrayAdapter<DataMb>{
			public myListAdapterReceived() {
				super(MainActivity.this,R.layout.time, data);
			}

			@Override
			public View getView(int position,View convertView,ViewGroup parent){
				//return super.getView(position,convertView,parent);
				View itemView=convertView;
				if(itemView==null){
					itemView=getLayoutInflater().inflate(R.layout.time,parent,false);

				}
				DataMb currentApp=data.get(position);
				TextView name=(TextView)itemView.findViewById(R.id.name);
				name.setText(currentApp.getName());
				String s;
				TextView time=(TextView)itemView.findViewById(R.id.receieved);
				s=String.format("%.2f",currentApp.getReceived())+" MB";
				time.setText(s);
				ImageView icon=(ImageView)itemView.findViewById(R.id.imageView2);
				icon.setImageDrawable(currentApp.getIcon());


				return itemView;
			}

		}
		private class myListAdapterSend extends ArrayAdapter<DataMb>{
			public myListAdapterSend() {
				super(MainActivity.this,R.layout.time, data);
			}

			@Override
			public View getView(int position,View convertView,ViewGroup parent){
				//return super.getView(position,convertView,parent);
				View itemView=convertView;
				if(itemView==null){
					itemView=getLayoutInflater().inflate(R.layout.time,parent,false);

				}
				DataMb currentApp=data.get(position);
				TextView name=(TextView)itemView.findViewById(R.id.name);
				name.setText(currentApp.getName());
				String s;
				TextView time=(TextView)itemView.findViewById(R.id.receieved);
				s=String.format("%.2f",currentApp.getSend())+" MB";
				time.setText(s);
				ImageView icon=(ImageView)itemView.findViewById(R.id.imageView2);
				icon.setImageDrawable(currentApp.getIcon());


				return itemView;
			}

		}








		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.menu_main, menu);
			//getActionBar().setDisplayShowTitleEnabled(false);


			return true;
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			int id = item.getItemId();

			//noinspection SimplifiableIfStatement
			if (id == R.id.action_sort) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Sort by")
						.setItems(R.array.sort, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sort(which);
                                populateViews(which);
                                current_sort = which;
                            }
                        });
				Dialog dialog =builder.create();
				dialog.show();
				return true;
			}
			if(id==R.id.action_refresh){
				mProgress.setVisibility(View.VISIBLE);
				list.setVisibility(View.INVISIBLE);

				// Start lengthy operation in a background thread
				new Thread(new Runnable() {
					public void run() {
						while (mProgressStatus < 100) {
							createList(current_sort);
							sort(current_sort);

							// Update the progress bar
							mHandler.post(new Runnable() {
								public void run() {
									populateViews(current_sort);
									mProgressStatus=100;
									mProgress.setVisibility(View.INVISIBLE);
									list.setVisibility(View.VISIBLE);
								}
							});
						}
					}
				}).start();
				mProgressStatus=0;
			}
            if(id==R.id.action_filter){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Filter")
                        .setItems(R.array.filter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mProgress.setVisibility(View.VISIBLE);
                                list.setVisibility(View.INVISIBLE);
                                final int j=which;
                                // Start lengthy operation in a background thread
                                new Thread(new Runnable() {
                                    public void run() {
                                        while (mProgressStatus < 100) {
                                            createList(j);
                                            sort(current_sort);

                                            // Update the progress bar
                                            mHandler.post(new Runnable() {
                                                public void run() {
                                                    populateViews(current_sort);
                                                    mProgressStatus=100;
                                                    mProgress.setVisibility(View.INVISIBLE);
                                                    list.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    }
                                }).start();
                                mProgressStatus=0;

                            }
                        });
                Dialog dialog =builder.create();
                dialog.show();
                return true;
            }

			return super.onOptionsItemSelected(item);
		}
	}
