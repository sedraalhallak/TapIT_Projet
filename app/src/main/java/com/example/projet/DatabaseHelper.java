package com.example.projet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "User.db";
    private static final int DATABASE_VERSION = 6;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SCORE = "score";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                "email TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                "display_name TEXT, " +
                "bio TEXT, " +

                "avatar_id INTEGER DEFAULT 1, " +
                COLUMN_SCORE + " INTEGER DEFAULT 0)";

        db.execSQL(createTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN display_name TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN avatar_id INTEGER DEFAULT 1");
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN bio TEXT");
        }

    }


    // add new user
    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put("email", email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }


    // check connection informations
    public boolean checkUser(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ID},
                "(" + COLUMN_USERNAME + "=? OR email=?) AND " + COLUMN_PASSWORD + "=?",
                new String[]{identifier, identifier, password},
                null, null, null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    // update use score
    public boolean updateScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);

        int rowsAffected = db.update(TABLE_USERS, values,
                COLUMN_USERNAME + "=?", new String[]{username});
        return rowsAffected > 0;
    }

    // get user score
    public int getScore(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_SCORE},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        int score = 0;
        if (cursor.moveToFirst()) {
            score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
        }
        cursor.close();
        return score;
    }
    public boolean updateUser(String oldUsername, String newUsername, String newDisplayName, int avatarId,String bio) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("username", newUsername);
            values.put("display_name", "");
            values.put("avatar_id", avatarId);
            values.put("bio", bio);  // 👈 ici



            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ?",
                    new String[]{oldUsername});
            boolean exists = cursor.getCount() > 0;
            cursor.close();

            if (!exists) {
                Log.e("DB_ERROR", "User not found: " + oldUsername);
                return false;
            }

            int rows = db.update(TABLE_USERS, values, "username = ?", new String[]{oldUsername});
            db.setTransactionSuccessful();
            return rows > 0;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Update failed", e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

}
