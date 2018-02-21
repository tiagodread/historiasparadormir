package foureyes.com.histriasparadormir.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import foureyes.com.histriasparadormir.Model.Historia;

/**
 * Created by dev on 21/02/18.
 */

public class Banco extends SQLiteOpenHelper{


    //Estrutura da tabela
    private static final String DATABASE_NAME = "livros";
    private static final String TABLE_NAME = "historia";
    private static final int DATABASE_VERSION = 1;
    private static final List<Historia> historia = new ArrayList<Historia>();

    Banco b;

    public Banco(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        b = this;
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

    public Cursor getHistoriaId(long id) {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT titulo,conteudo,tipo,thumb FROM " + TABLE_NAME + " WHERE _id = " + id, null);

        return cursor;
    }

    public void atualizaBanco(ArrayList<Historia> historias) {

        SQLiteDatabase db = getWritableDatabase();

        for (Historia h : historias) {

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE titulo like '" + h.getTitulo() + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {
                db.execSQL("INSERT INTO historia (titulo,conteudo,tipo,thumb) VALUES ('" + h.getTitulo() + "','" + h.getConteudo() + "','" + h.getTipo() + "','" + h.getThumb() + "')");
            }
            cursor.close();
        }
    }

    public void setLastUpdate(String data){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO historia (updated) VALUES ('"+data+"')");
    }

    public String getLastUpdate(){
        String data = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT updated FROM historia;", null);
        if (cursor.moveToLast()) {
            data = cursor.getString(0);
        }
        return data;
    }

    public int getQuantDados() {
        boolean status;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM historia", null);

        return cursor.getCount();
    }

    public Cursor gethistoriasCursor() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM historia", null);
        return cursor;
    }

    public List<Historia> gethistorias() {


        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM historia", null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Historia l = new Historia(cursor.getString(cursor.getColumnIndex("titulo")), cursor.getString(cursor.getColumnIndex("conteudo")), cursor.getString(cursor.getColumnIndex("tipo")), cursor.getString(cursor.getColumnIndex("thumb")));
            historia.add(l);
            cursor.moveToNext();
        }
        cursor.close();

        return historia;
    }

    public int getQuantidade() {
        return historia.size();
    }

    public void resetaBanco() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS historia (_id INTEGER PRIMARY KEY, titulo TEXT, conteudo TEXT, tipo TEXT, thumb TEXT, updated DATE );");
    }
}
