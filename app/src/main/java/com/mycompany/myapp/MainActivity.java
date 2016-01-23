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
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends ActionBarActivity {

	Context ctx = this;
	ListView list;
	List<DataMb> data = new ArrayList<DataMb>();
	private ProgressBar mProgress;
	List<UsageStats> usageStatses;
	AlarmManager alarm;
	int current_sort = 0;
	int current_list=0;
	PendingIntent pintent;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//Check if permission enabled
		if (Build.VERSION.SDK_INT >= 20) {
			if (UStats.getUsageStatsList(this).isEmpty()) {
				Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
				startActivity(intent);
			}
		}
		UStats.printCurrentUsageStatus(ctx);
		list = (ListView) findViewById(R.id.listView2);
		Intent intent = new Intent(this, MyService.class);
		pintent = PendingIntent.getService(this, 0, intent, 0);
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR, 17);
		cal.set(Calendar.MINUTE,58);
		cal.set(Calendar.SECOND, 0);
		alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if (Build.VERSION.SDK_INT < 20) {
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1000, pintent);
		} else {
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);
		}
		mProgress = (ProgressBar) findViewById(R.id.progressBar);

		// Set the adapter for the list view
		mProgress.setVisibility(View.VISIBLE);
		list.setVisibility(View.INVISIBLE);
		createList(0);
		sort(0);
		populateViews(0);
		list.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.INVISIBLE);



		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {

				DataMb currentApp = data.get(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle(currentApp.getName())
						.setIcon(currentApp.getIcon())
						.setMessage("Time of Use - " + String.valueOf(((currentApp.getTime() / 60) / 60) / 24) + "d " + String.valueOf(((currentApp.getTime() / 60) / 60) % 24) + "hr " + String.valueOf((currentApp.getTime() / 60) % 60) + "m " + String.valueOf(currentApp.getTime() % 60) + "s" + "\n" + "Received Data - " + String.format("%.2f", currentApp.getReceived()) + " MB" + "\n" + "Sent Data - " + String.format("%.2f", currentApp.getSend()) + " MB" + "\n" + "Last Time of Launch - " + currentApp.getLastuse() + "\n");
				Dialog dialog = builder.create();
				dialog.show();

			}


		});
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}




	public void createList(int i) {
		long time = 0;
		String lastuse = "";
		String package_name;
		int UID;
		ApplicationInfo app;
		data.clear();
		final PackageManager pm = getPackageManager();
		// get a list of installed apps.
		List<ApplicationInfo> packages = pm.getInstalledApplications(0);
		Calendar calendar= Calendar.getInstance();
		usageStatses = UStats.getUsageStatsList(ctx);
		// loop through the list of installed packages and see if the selected
		// app is in the list

		if (i == 1) {
			Data dop = new Data(this);
			Cursor CR = dop.getInformation(dop);
			CR.moveToFirst();
			while (CR.moveToNext()){
				package_name=CR.getString(0);
				time = CR.getLong(1);
				lastuse=CR.getString(2);
				for (ApplicationInfo packageInfo : packages) {
					if (packageInfo.packageName.equals(package_name)&& (packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
						UID = packageInfo.uid;
						app = null;
						//SourceDir = packageInfo.dataDir;
						try {
							app = pm.getApplicationInfo(package_name, 0);
						} catch (PackageManager.NameNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						String name = (String) pm.getApplicationLabel(app);
						Drawable icon = (Drawable) pm.getApplicationIcon(app);
						// internet usage for particular app(sent and received)
						double received = (double) TrafficStats.getUidRxBytes(UID)

								/ (1024 * 1024);
						double send = (double) TrafficStats.getUidTxBytes(UID)
								/ (1024 * 1024);

						data.add(new DataMb(name, received, send, time, icon, lastuse));
					}

				}
			}

		}

		else if (i == 2) {
			Data dop = new Data(this);
			Cursor CR = dop.getInformation(dop);
			CR.moveToFirst();
			while (CR.moveToNext()){
				package_name=CR.getString(0);
				time = CR.getLong(1);
				lastuse=CR.getString(2);
				for (ApplicationInfo packageInfo : packages) {
					if (packageInfo.packageName.equals(package_name)) {
						UID = packageInfo.uid;
						app = null;
						//SourceDir = packageInfo.dataDir;
						try {
							app = pm.getApplicationInfo(package_name, 0);
						} catch (PackageManager.NameNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						String name = (String) pm.getApplicationLabel(app);
						Drawable icon = (Drawable) pm.getApplicationIcon(app);
						// internet usage for particular app(sent and received)
						double received = (double) TrafficStats.getUidRxBytes(UID)

								/ (1024 * 1024);
						double send = (double) TrafficStats.getUidTxBytes(UID)
								/ (1024 * 1024);

						data.add(new DataMb(name, received, send, time, icon, lastuse));
					}

				}
			}

		}


		 else if (i == 0) {
			Data dop = new Data(this);
			Cursor CR = dop.getInformation(dop);
			CR.moveToFirst();
			while (CR.moveToNext()){
				package_name=CR.getString(0);
				time = CR.getLong(1);
				lastuse=CR.getString(2);
				for (ApplicationInfo packageInfo : packages) {
					if (packageInfo.packageName.equals(package_name)&&(!((packageInfo.flags & packageInfo.FLAG_SYSTEM) != 0))) {
						UID = packageInfo.uid;
						app = null;
						//SourceDir = packageInfo.dataDir;
						try {
							app = pm.getApplicationInfo(package_name, 0);
						} catch (PackageManager.NameNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						String name = (String) pm.getApplicationLabel(app);
						Drawable icon = (Drawable) pm.getApplicationIcon(app);
						// internet usage for particular app(sent and received)
						double received = (double) TrafficStats.getUidRxBytes(UID)

								/ (1024 * 1024);
						double send = (double) TrafficStats.getUidTxBytes(UID)
								/ (1024 * 1024);

						data.add(new DataMb(name, received, send, time, icon, lastuse));
					}

				}
			}

		}

			/*for (UsageStats usageStatus : usageStatses) {
				package_name = usageStatus.getPackageName();
				time=usageStatus.getTotalTimeInForeground()/1000;
				calendar.setTimeInMillis(usageStatus.getLastTimeUsed());
				lastuse=calendar.getTime().toString();
			*/
	}

	public void sort(int i) {
		if (i == 1) {
			Collections.sort(data, new Comparator() {
				public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
//use instanceof to verify the references are indeed of the type in question
					return ((DataMb) synchronizedListOne).name
							.compareTo(((DataMb) synchronizedListTwo).name);
				}
			});
		}
		if (i == 0) {
			Collections.sort(data, new Comparator() {
				public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
//use instanceof to verify the references are indeed of the type in question
					if (((DataMb) synchronizedListOne).time < ((DataMb) synchronizedListTwo).time)
						return 1;
					else return -1;
				}
			});
		}
		if (i == 2) {
			Collections.sort(data, new Comparator() {
				public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
//use instanceof to verify the references are indeed of the type in question
					if (((DataMb) synchronizedListOne).received < ((DataMb) synchronizedListTwo).received)
						return 1;
					else return -1;
				}
			});
		}
		if (i == 3) {
			Collections.sort(data, new Comparator() {
				public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
//use instanceof to verify the references are indeed of the type in question
					if (((DataMb) synchronizedListOne) == null) return -1;
					if (((DataMb) synchronizedListTwo) == null) return -1;
					if (((DataMb) synchronizedListOne).send < ((DataMb) synchronizedListTwo).send)
						return 1;
					else return -1;
				}
			});
		}
	}

	public void populateViews(int i) {
		ArrayAdapter<DataMb> adapter = null;
		if (i == 0) {

			adapter = new myListAdapterTime();
		}
		if (i == 1) {

			adapter = new myListAdapterName();
		}
		if (i == 2) {

			adapter = new myListAdapterReceived();
		}
		if (i == 3) {

			adapter = new myListAdapterSend();
		}

		list.setAdapter(adapter);


	}

	@Override
	public void onStop() {
		super.onStop();

	}

	private class myListAdapterTime extends ArrayAdapter<DataMb> {
		public myListAdapterTime() {
			super(MainActivity.this, R.layout.time, data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//return super.getView(position,convertView,parent);
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.time, parent, false);

			}
			DataMb currentApp = data.get(position);
			TextView name = (TextView) itemView.findViewById(R.id.name);
			name.setText(currentApp.getName());
			String s;
			TextView time = (TextView) itemView.findViewById(R.id.receieved);

			if (((currentApp.getTime() / 60) / 60) / 24 == 0 && ((currentApp.getTime() / 60) / 60) % 24 == 0) {
				s = String.valueOf((currentApp.getTime() / 60) % 60) + "m " + String.valueOf(currentApp.getTime() % 60) + "s";
			} else if (((currentApp.getTime() / 60) / 60) / 24 == 0) {
				s = String.valueOf(((currentApp.getTime() / 60) / 60) % 24) + "hr " + String.valueOf((currentApp.getTime() / 60) % 60) + "m " + String.valueOf(currentApp.getTime() % 60) + "s";
			} else {
				s = String.valueOf(((currentApp.getTime() / 60) / 60) / 24) + "d " + String.valueOf(((currentApp.getTime() / 60) / 60) % 24) + "hr " + String.valueOf((currentApp.getTime() / 60) % 60) + "m " + String.valueOf(currentApp.getTime() % 60) + "s";
			}
			time.setText(s);
			ImageView icon = (ImageView) itemView.findViewById(R.id.imageView2);
			icon.setImageDrawable(currentApp.getIcon());


			return itemView;
		}

	}

	private class myListAdapterName extends ArrayAdapter<DataMb> {
		public myListAdapterName() {
			super(MainActivity.this, R.layout.time, data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//return super.getView(position,convertView,parent);
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.time, parent, false);

			}
			DataMb currentApp = data.get(position);
			TextView name = (TextView) itemView.findViewById(R.id.name);
			name.setText(currentApp.getName());

			ImageView icon = (ImageView) itemView.findViewById(R.id.imageView2);
			icon.setImageDrawable(currentApp.getIcon());


			return itemView;
		}

	}

	private class myListAdapterReceived extends ArrayAdapter<DataMb> {
		public myListAdapterReceived() {
			super(MainActivity.this, R.layout.time, data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//return super.getView(position,convertView,parent);
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.time, parent, false);

			}
			DataMb currentApp = data.get(position);
			TextView name = (TextView) itemView.findViewById(R.id.name);
			name.setText(currentApp.getName());
			String s;
			TextView time = (TextView) itemView.findViewById(R.id.receieved);
			s = String.format("%.2f", currentApp.getReceived()) + " MB";
			time.setText(s);
			ImageView icon = (ImageView) itemView.findViewById(R.id.imageView2);
			icon.setImageDrawable(currentApp.getIcon());


			return itemView;
		}

	}

	private class myListAdapterSend extends ArrayAdapter<DataMb> {
		public myListAdapterSend() {
			super(MainActivity.this, R.layout.time, data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//return super.getView(position,convertView,parent);
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.time, parent, false);

			}
			DataMb currentApp = data.get(position);
			TextView name = (TextView) itemView.findViewById(R.id.name);
			name.setText(currentApp.getName());
			String s;
			TextView time = (TextView) itemView.findViewById(R.id.receieved);
			s = String.format("%.2f", currentApp.getSend()) + " MB";
			time.setText(s);
			ImageView icon = (ImageView) itemView.findViewById(R.id.imageView2);
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
			Dialog dialog = builder.create();
			dialog.show();
			return true;
		}
		if (id == R.id.action_refresh) {
			mProgress.setVisibility(View.VISIBLE);
			list.setVisibility(View.INVISIBLE);
			createList(current_list);
			sort(current_sort);
			populateViews(current_sort);
			mProgress.setVisibility(View.INVISIBLE);
			list.setVisibility(View.VISIBLE);

		}
		if (id == R.id.action_filter) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Filter")
					.setItems(R.array.filter, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mProgress.setVisibility(View.VISIBLE);
							list.setVisibility(View.INVISIBLE);
							final int j = which;
							// Start lengthy operation in a background thread
							createList(j);
							current_list=j;
							sort(current_sort);
							populateViews(current_sort);
							mProgress.setVisibility(View.INVISIBLE);
							list.setVisibility(View.VISIBLE);

						}
					});
			Dialog dialog = builder.create();
			dialog.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


}
