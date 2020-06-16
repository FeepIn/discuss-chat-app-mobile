package fr.feepin.justchat;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;


public class Database extends SQLiteOpenHelper {

    private static final String NAME = "discuss.db";
    private static final int VERSION = 1;

    public Database(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE subjects (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, liked INTEGER NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertSubject(String name, boolean liked){
        int numLiked = liked ? 1 : 0;
        String sql = "INSERT INTO subjects (name, liked) VALUES ('"+name+"', "+numLiked+")";
        this.getWritableDatabase().execSQL(sql);
    }

    public void deleteSubject(String name){
        String sql = "DELETE FROM subjects WHERE name='"+name+"' ";
        this.getWritableDatabase().execSQL(sql);
    }

    public HashMap<String, Boolean> getFavoriteSubjects(){
        HashMap<String, Boolean> subjects = new HashMap<>();

        //Get cursor
        Cursor cursor = getReadableDatabase().rawQuery("SELECT name, liked FROM subjects", null);

        while (!cursor.isLast()){
            cursor.moveToNext();
            subjects.put(cursor.getString(0), cursor.getInt(1) == 1 );
        }
        cursor.close();
        return subjects;
    }
}
