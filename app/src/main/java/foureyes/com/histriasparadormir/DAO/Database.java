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


    //Estrutura da tabela
    private static final String DATABASE_NAME = "livros";
    private static final String TABLE_NAME = "historia";
    private static final int DATABASE_VERSION = 1;
    private static final List<Story> STORY = new ArrayList<Story>();

    Database database;

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = this;
    }


    @Override //Metodo que cria a tabela
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (_id INTEGER PRIMARY KEY, titulo TEXT, conteudo TEXT, tipo TEXT, thumb TEXT, updated DATE );");
    }

    @Override //Metodo que atualiza a tabela
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor gethistoriasCursor() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM historia", null);
        return cursor;
    }

    public Cursor getHistoriaId(long id) {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT titulo,conteudo,tipo,thumb FROM " + TABLE_NAME + " WHERE _id = " + id, null);

        return cursor;
    }

    public void updateDatabase(ArrayList<Story> stories) {

        SQLiteDatabase db = getWritableDatabase();

        for (Story h : stories) {

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE titulo like '" + h.getTitle() + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                db.execSQL("INSERT INTO historia (titulo, conteudo, tipo, thumb, updated) VALUES ('" + h.getTitle() + "','" + h.getContent() + "','" + h.getType() + "','" + h.getThumbnail() + "', '" + getToday() + "')");
            }
            cursor.close();
        }
    }

    public String getLastUpdate() {
        String data = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT updated FROM historia WHERE _id=1;", null);
        if (cursor.moveToFirst()) {
            data = cursor.getString(cursor.getColumnIndex("updated"));
        }
        return data;
    }

    public boolean isEmpty() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM historia", null);
        return cursor.getCount() == 0 ? true : false;
    }

    public List<Story> gethistorias() {


        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM historia", null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Story l = new Story(cursor.getString(cursor.getColumnIndex("titulo")), cursor.getString(cursor.getColumnIndex("conteudo")), cursor.getString(cursor.getColumnIndex("tipo")), cursor.getString(cursor.getColumnIndex("thumb")));
            STORY.add(l);
            cursor.moveToNext();
        }
        cursor.close();

        return STORY;
    }

    public int getQuantidade() {
        return STORY.size();
    }

    public void resetDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS historia (_id INTEGER PRIMARY KEY, titulo TEXT, conteudo TEXT, tipo TEXT, thumb TEXT, updated DATE );");
    }

    public String getToday() {
        Date updated = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(updated);
    }
}
