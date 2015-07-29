package com.mycompany.myapp;



	import android.provider.BaseColumns;

	/**
	 * Created by ndesh on 2/4/15.
	 */
	public class Table {
		public Table()
		{

		}
		public static abstract class TableInfo implements BaseColumns{
			public static final String APPDATA="data";
			public static final String TIME="time";
			public static final String RECEIVED="received";
			public static final String SENT="sent";
			public static final String LASTUSE="lastuse";
			public static final String DATABASE_NAME="number";
			public static final String TABLE_NAME="reg_info";


		}

	}
