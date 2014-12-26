package com.dobi.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
	private DatabaseHelper helper;
	private SQLiteDatabase db;
	private Map<String, String> mapNetList;

	public DBManager(Context context) {
		helper = new DatabaseHelper(context);
	}

	// add data
	public void add(String name, String values) {
		db = helper.getWritableDatabase();
		db.execSQL("insert into dobi(name, info) values('" + name + "', '"
				+ values + "')");
		db.close();
	}

	// clear db
	public void clear() {
		db = helper.getWritableDatabase();
		db.execSQL("delete from 'dobi'");
		db.close();
	}

	public boolean isEmpty() {
		db = helper.getWritableDatabase();
		Cursor cursor = db.query("dobi", new String[] {"name"}, null, null,
				null, null, null);
		if (cursor.getCount() > 0) {
			return false;
		}
		return true;

	}

	public Map<String, String> getInfo() {
		mapNetList=Collections.synchronizedMap(new HashMap<String, String>());
		db = helper.getWritableDatabase();
		Cursor cursor = db.query("dobi", new String[] { "name", "info" }, null,
				null, null, null, null);
		while (cursor.moveToNext()) {
			int nameColumn = cursor.getColumnIndex("name");
			int infoColumn = cursor.getColumnIndex("info");
			String key = cursor.getString(nameColumn);
			String value = cursor.getString(infoColumn);
			mapNetList.put(key, value);
		}
		cursor.close();
		db.close();
		return mapNetList;
	}
	public int getInt(String path){
		db = helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select info from dobi where name=?", new String[]{path});
		if(cursor.moveToNext()) {
			int infoColumn = cursor.getColumnIndex("info");
			String values = cursor.getString(infoColumn);
			cursor.close();
			db.close();
			return Integer.parseInt(values);
		}else{
			cursor.close();
			db.close();
			return -1;
		}
	}
	public int getVersion(String name){
		db = helper.getWritableDatabase();
		Cursor cursor=db.rawQuery("select info from dobi where name=?", new String[]{name});
		if(cursor.moveToNext()) {
			int infoColumn = cursor.getColumnIndex("info");
			String values = cursor.getString(infoColumn);
			cursor.close();
			db.close();
			return Integer.parseInt(values);
		}else{
			cursor.close();
			db.close();
			return -1;
		}
	}
}
