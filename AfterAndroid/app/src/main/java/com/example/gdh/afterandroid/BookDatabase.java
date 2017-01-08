package com.example.gdh.afterandroid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookDatabase {

	/**
	 * TAG for debugging
	 */
	public static final String TAG = "BookDatabase";

	/**
	 * Singleton instance
	 */
	private static BookDatabase database;


	/**
	 * database name
	 */
	public static String DATABASE_NAME = "book3.db";

	/**
	 * table name for BOOK_INFO
	 */
	public static String TABLE_BOOK_INFO = "BOOK_INFO";

    /**
     * version
     */
	public static int DATABASE_VERSION = 1;


    /**
     * Helper class defined
     */
    private DatabaseHelper dbHelper;

    /**
     * Database object
     */
    private SQLiteDatabase db;


    private Context context;

    /**
     * Constructor
     */
	private BookDatabase(Context context) {
		this.context = context;
	}


	public static BookDatabase getInstance(Context context) {
		if (database == null) {
			database = new BookDatabase(context);
		}

		return database;
	}

	/**
	 * open database
	 *
	 * @return
	 */
    public boolean open() {
    	println("opening database [" + DATABASE_NAME + "].");

    	dbHelper = new DatabaseHelper(context);
    	db = dbHelper.getWritableDatabase();

    	return true;
    }

    /**
     * close database
     */
    public void close() {
    	println("closing database [" + DATABASE_NAME + "].");
    	db.close();
    	database = null;
    }

    /**
     * execute raw query using the input SQL
     * close the cursor after fetching any result
     *
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
		println("\nexecuteQuery called.\n");

		Cursor c1 = null;
		try {
			c1 = db.rawQuery(SQL, null);
			println("cursor count : " + c1.getCount());
		} catch(Exception ex) {
    		Log.e(TAG, "Exception in executeQuery", ex);
    	}

		return c1;
	}

    public boolean execSQL(String SQL) {
		println("\nexecute called.\n");

		try {
			Log.d(TAG, "SQL : " + SQL);
			db.execSQL(SQL);
	    } catch(Exception ex) {
			Log.e(TAG, "Exception in executeQuery", ex);
			return false;
		}

		return true;
	}




    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase _db) {
        	// TABLE_BOOK_INFO
        	println("creating table [" + TABLE_BOOK_INFO + "].");

        	// drop existing table
        	String DROP_SQL = "drop table if exists " + TABLE_BOOK_INFO;
        	try {
        		_db.execSQL(DROP_SQL);
        	} catch(Exception ex) {
        		Log.e(TAG, "Exception in DROP_SQL", ex);
        	}

        	// create table
        	String CREATE_SQL = "create table " + TABLE_BOOK_INFO + "("
		        			+ "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
		        			+ "  NAME TEXT, "
		        			+ "  AUTHOR TEXT, "
		        			+ "  PUBLISHER TEXT, "
		        			+ "  RELEASE_DATE TEXT, "
		        			+ "  PRICE TEXT, "
		        			+ "  IMAGE_NAME TEXT, "
		        			+ "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
		        			+ ")";
            try {
            	_db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
        		Log.e(TAG, "Exception in CREATE_SQL", ex);
        	}
			try {
			//	_db.delete(TABLE_BOOK_INFO,null,null);
			} catch(Exception ex) {
				Log.e(TAG, "Exception in executing insert SQL.", ex);
			}
			// insert 5 book records
			insertRecord(_db, "Do it! 안드로이드 앱 프로그래밍", "정재곤", "이지스퍼블리싱", "2011년 12월", "40,000원", "book01");
			insertRecord(_db, "Programming Android", "Mednieks, Zigurd", "Oreilly Associates Inc", "2011년 04월", "62,080원", "book02");
			insertRecord(_db, "센차터치 모바일 프로그래밍", "이병옥,최성민 공저", "에이콘출판사", "2011년 10월", "40,000원", "book03");
			insertRecord(_db, "시작하세요! 안드로이드 게임 프로그래밍", "마리오 제흐너 저", "위키북스", "2011년 09월", "36,000원", "book04");
			insertRecord(_db, "실전! 안드로이드 시스템 프로그래밍 완전정복", "박선호,오영환 공저", "DW Wave", "2010년 10월", "28,000원", "book05");

        }

        private void insertRecord(SQLiteDatabase _db, String name, String author, String publisher, String releaseDate, String price, String imageName) {
        	try {
        		_db.execSQL( "insert into " + TABLE_BOOK_INFO + "(NAME, AUTHOR, PUBLISHER, RELEASE_DATE, PRICE, IMAGE_NAME) values ('" + name + "', '" + author + "', '" + publisher + "', '" + releaseDate + "', '" + price + "', '" + imageName + "');" );
        	} catch(Exception ex) {
        		Log.e(TAG, "Exception in executing insert SQL.", ex);
        	}
       	}

        public void onOpen(SQLiteDatabase db) {
        	println("opened database [" + DATABASE_NAME + "].");

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");

        	if (oldVersion < 2) {   // version 1

        	}

        }

    }

    private void println(String msg) {
    	Log.d(TAG, msg);
    }


}
