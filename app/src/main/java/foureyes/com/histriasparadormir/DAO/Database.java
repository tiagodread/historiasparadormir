package foureyes.com.histriasparadormir.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import foureyes.com.histriasparadormir.Model.Story;

/**
 * Created by dev on 21/02/18.
 */

public class Database extends SQLiteOpenHelper {


    // Database Schema
    private static final String DATABASE_NAME = "historiasparadormir";
    private static final String TABLE_NAME = "stories";
    private static final int DATABASE_VERSION = 1;
    private static final List<Story> STORY = new ArrayList<Story>();

    Database database;

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = this;
    }

    @Override // Create table
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (_id INTEGER PRIMARY KEY, title TEXT, content TEXT, type TEXT, thumbnail TEXT, updated DATE );");
    }

    @Override // Update table
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getStoryCursor() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }

    public Cursor getStoryId(long id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT title, content, type, thumbnail FROM " + TABLE_NAME + " WHERE _id = " + id, null);
        return cursor;
    }

    public void updateDatabase(ArrayList<Story> stories) {
        SQLiteDatabase db = getWritableDatabase();
        for (Story h : stories) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE title like '" + h.getTitle() + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                db.execSQL("INSERT INTO " + TABLE_NAME + " (title, content, type, thumbnail, updated) VALUES ('" + h.getTitle() + "','" + h.getContent() + "','" + h.getType() + "','" + h.getThumbnail() + "', '" + getToday() + "')");
            }
            cursor.close();
        }
    }

    public String getLastUpdate() {
        String data = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT updated FROM " + TABLE_NAME + " WHERE _id=1;", null);
        if (cursor.moveToFirst()) {
            data = cursor.getString(cursor.getColumnIndex("updated"));
        }
        return data;
    }

    public boolean isEmpty() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor.getCount() == 0;
    }

    public void resetDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (_id INTEGER PRIMARY KEY, title TEXT, content TEXT, type TEXT, thumbnail TEXT, updated DATE );");
    }

    public String getToday() {
        Date updated = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(updated);
    }
}
