package com.dobi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//继承SQLiteOpenHelper类
public class DatabaseHelper extends SQLiteOpenHelper{
  private  static String TABLE_NAME="dobi";
  // 构造函数，调用父类SQLiteOpenHelper的构造函数
	public DatabaseHelper(Context context){
      super(context, "dobi.db", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("CREATE TABLE [" + TABLE_NAME + "] (");
        sBuffer.append("_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("name char(50),");
        sBuffer.append("info char(12))");
        // 执行创建表的SQL语句
        db.execSQL(sBuffer.toString());
        
//        db.execSQL("CREATE TABLE dob_material_disguise(id INTEGER NOT NULL," +
//        		"name VARCHAR(50) NOT NULL,type_num INTEGER(5) NOT NULL ," +
//        		"cTime INTEGER(11) NOT NULL ," +
//        		"isDel TINYINT(1) NOT NULL DEFAULT '0'," +
//        		"PRIMARY KEY (`id`) AUTOINCREMENT)");
        
        
        
        

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
}
