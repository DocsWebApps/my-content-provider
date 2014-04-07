package com.example.mycontentprovider;

import android.net.Uri;

public class MyDBSchema {
	// define a class to define the schema of the DB in the content provider. Use public static final to lock fields.
	
	//Setup Uri object to point to the DB and Table Names
	public static final String mAuthority="com.example.mycontentprovider.myprovider";
	public static final Uri mBaseUri=Uri.parse("content://"+ mAuthority +"/");
	public static final String mParentTable="parent_table";
	public static final String mChildTable="child_table";
	public static final Uri mDBUriParentTable=Uri.withAppendedPath(mBaseUri,mParentTable);
	public static final Uri mDBUriChildTable=Uri.withAppendedPath(mBaseUri,mChildTable);
	
	// Define Columns for Parent Table
	public static final String _parent_ID="_id";
	public static final String parent_desc="description";
	
	// Define columns for Child Table
	public static final String _child_ID="_id";
	public static final String child_desc="description";
	public static final	String parent_ID="parent_id";
}
