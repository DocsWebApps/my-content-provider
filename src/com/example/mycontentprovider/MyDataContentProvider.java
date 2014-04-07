package com.example.mycontentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MyDataContentProvider extends ContentProvider{
	
	private SQLiteDatabase database;
	private static final String DATABASE_NAME="mytestdb";
	private static final int DATABASE_VERSION=3;
	
	private static final String CREATE_PARENT_TABLE="CREATE TABLE "
			+ MyDBSchema.mParentTable + " ("
			+ MyDBSchema._parent_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ MyDBSchema.parent_desc + " TEXT NOT NULL);";
	
	private static final String CREATE_CHILD_TABLE="CREATE TABLE "
			+ MyDBSchema.mChildTable + " ("
			+ MyDBSchema._child_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ MyDBSchema.child_desc + " TEXT NOT NULL,"
			+ MyDBSchema.parent_ID + " INTEGER,"
			+ " FOREIGN KEY ("+ MyDBSchema.parent_ID +") REFERENCES "+MyDBSchema.mChildTable+" ("+ MyDBSchema._child_ID +"));";
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);			
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_CHILD_TABLE);
			db.execSQL(CREATE_PARENT_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MyDBSchema.mParentTable);
			db.execSQL("DROP TABLE IF EXISTS " + MyDBSchema.mChildTable);
		}
	}
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		DatabaseHelper dbHelper=new DatabaseHelper(getContext());
		database = dbHelper.getWritableDatabase();
		
		// Test DB Schema - comment out when tested!!
		//testDBSchema();
		
		return (database!=null);
	}
	
	private boolean testDBSchema() {
		ContentValues mData=new ContentValues();
		String mDescription="This is a record";
		
		//Delete all rows from child_table
		int rows=deleteData(MyDBSchema.mChildTable, MyDBSchema.mDBUriChildTable, null, null);
		
		//Delete all rows from parent_table
		rows=deleteData(MyDBSchema.mParentTable, MyDBSchema.mDBUriParentTable, null, null);
		
		//Insert into parent_table
		mData.put(MyDBSchema.parent_desc, mDescription);
		insertData(MyDBSchema.mParentTable, MyDBSchema.mDBUriParentTable, mData);
		mData.clear();
		
		//Select rows from parent_table
		Cursor rowCursor = selectData(MyDBSchema.mParentTable, MyDBSchema.mDBUriParentTable, null, null, null, null, null, null);
		rows=rowCursor.getCount();

		//Insert into child_table
		rowCursor.moveToFirst();
		int foreign_key=rowCursor.getInt(rowCursor.getColumnIndex(MyDBSchema._parent_ID));
		mData.put(MyDBSchema.child_desc, mDescription);
		mData.put(MyDBSchema.parent_ID, foreign_key);
		for(int i=0 ; i<5 ; i++) {
			insertData(MyDBSchema.mChildTable, MyDBSchema.mDBUriChildTable, mData);
		}
		rowCursor.close();
		
		//Select rows from parent_table
		rowCursor = selectData(MyDBSchema.mParentTable, MyDBSchema.mDBUriChildTable, null, null, null, null, null, null);
		rows=rowCursor.getCount();
		rowCursor.close();
		
		//Delete all rows from child_table
		rows=deleteData(MyDBSchema.mChildTable, MyDBSchema.mDBUriChildTable, null, null);
		
		//Delete all rows from parent_table
		rows=deleteData(MyDBSchema.mParentTable, MyDBSchema.mDBUriParentTable, null, null);
		
		//Clean up
		mData.clear();
		mData=null;
		rowCursor.close();
		rowCursor=null;
		
		return true;
	}
	
	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		//Returns the MIME type of the Uri argument
		return null;
	}
	
	// My custom CRUD methods for this class - Using multiple tables so I need custom methods
	public Uri insertData(String tableName, Uri authority, ContentValues values) {
		long rowID=database.insert(tableName, null, values);
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(authority, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to add record into " + tableName);
	}
	
	public Cursor selectData(String tableName, Uri authority, String[] projection, String whereClause, String[] whereArgs, String groupBy, String havingClause, String orderBy) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(tableName);
		Cursor selectCursor = qb.query(database, projection, whereClause, whereArgs, groupBy, havingClause, orderBy);
		selectCursor.setNotificationUri(getContext().getContentResolver(), authority);
		return selectCursor;
	}
	
	public int deleteData(String tableName, Uri authority, String whereClause, String[] whereArgs) {
		int intRowsDeleted = database.delete(tableName, whereClause, whereArgs);
		getContext().getContentResolver().notifyChange(authority, null);
		return intRowsDeleted;
	}
	
	// Default CRUD methods for this class - If using a single table implement these
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
}