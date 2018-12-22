package com.visualcheckbook.visualcheckbook.BooksDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "book_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_BOOK = "book";
    private static final String TABLE_BOOK_NAME = "book_name";
    private static final String TABLE_BOOK_AUTHOR = "book_author";
    private static final String KEY_ID = "id";
    private static final String KEY_BOOK_AUTHOR = "author";
    private static final String KEY_BOOK_NAME = "name";
    private static final String KEY_ISBN = "isbn";

    /*CREATE TABLE table_1 ( id_1 INTEGER PRIMARY KEY AUTOINCREMENT, value_1 TEXT, value_2 TEXT......);*/

    private static final String CREATE_TABLE_BOOK = "CREATE TABLE "
            + TABLE_BOOK + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ISBN + " TEXT );";

    private static final String CREATE_TABLE_BOOK_NAME = "CREATE TABLE "
            + TABLE_BOOK_NAME + "(" + KEY_ID + " INTEGER,"+ KEY_BOOK_NAME + " TEXT );";

    private static final String CREATE_TABLE_BOOK_AUTHOR = "CREATE TABLE "
            + TABLE_BOOK_AUTHOR + "(" + KEY_ID + " INTEGER,"+ KEY_BOOK_AUTHOR + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOK);
        db.execSQL(CREATE_TABLE_BOOK_NAME);
        db.execSQL(CREATE_TABLE_BOOK_AUTHOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_BOOK + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_BOOK_NAME + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_BOOK_AUTHOR + "'");
        onCreate(db);
    }

    public void addBook(String isbn, String name, String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        //adding user name in users table
        ContentValues values = new ContentValues();
        values.put(KEY_ISBN, isbn);
        // db.insert(TABLE_USER, null, values);
        long id = db.insertWithOnConflict(TABLE_BOOK, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        //adding user hobby in users_hobby table
        ContentValues valuesName = new ContentValues();
        valuesName.put(KEY_ID, id);
        valuesName.put(KEY_BOOK_NAME, name);
        db.insert(TABLE_BOOK_NAME, null, valuesName);

        //adding user city in users_city table
        ContentValues valuesAuthor = new ContentValues();
        valuesAuthor.put(KEY_ID, id);
        valuesAuthor.put(KEY_BOOK_AUTHOR, author);
        db.insert(TABLE_BOOK_AUTHOR, null, valuesAuthor);
    }

    public ArrayList<BookModel> getAllBooks() {
        ArrayList<BookModel> booksModelArrayList = new ArrayList<BookModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_BOOK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                BookModel bookModel = new BookModel();
                bookModel.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                bookModel.setIsbn(c.getString(c.getColumnIndex(KEY_ISBN)));

                //getting user hobby where id = id from user_hobby table
                String selectNameQuery = "SELECT  * FROM " + TABLE_BOOK_NAME + " WHERE " +KEY_ID+ " = " + bookModel.getId();

                //SQLiteDatabase dbhobby = this.getReadableDatabase();
                Cursor cursorName = db.rawQuery(selectNameQuery, null);

                if (cursorName.moveToFirst()) {
                    do {
                        bookModel.setName(cursorName.getString(cursorName.getColumnIndex(KEY_BOOK_NAME)));
                    } while (cursorName.moveToNext());
                }

                //getting user city where id = id from user_city table
                String selectAuthorQuery = "SELECT  * FROM " + TABLE_BOOK_AUTHOR + " WHERE " + KEY_ID + " = " + bookModel.getId();;
                //SQLiteDatabase dbCity = this.getReadableDatabase();
                Cursor cursorAuthor = db.rawQuery(selectAuthorQuery, null);

                if (cursorAuthor.moveToFirst()) {
                    do {
                        bookModel.setAuthor(cursorAuthor.getString(cursorAuthor.getColumnIndex(KEY_BOOK_AUTHOR)));
                    } while (cursorAuthor.moveToNext());
                }

                // adding to Students list
                booksModelArrayList.add(bookModel);
            } while (c.moveToNext());
        }
        return booksModelArrayList;
    }

    public void updateBook(int id, String isbn, String name, String author) {
        SQLiteDatabase db = this.getWritableDatabase();

        // updating name in users table
        ContentValues values = new ContentValues();
        values.put(KEY_ISBN, isbn);
        db.update(TABLE_BOOK, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        // updating hobby in users_hobby table
        ContentValues valuesName = new ContentValues();
        valuesName.put(KEY_BOOK_NAME, name);
        db.update(TABLE_BOOK_NAME, valuesName, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        // updating city in users_city table
        ContentValues valuesAuthor = new ContentValues();
        valuesAuthor.put(KEY_BOOK_AUTHOR, author);
        db.update(TABLE_BOOK_AUTHOR, valuesAuthor, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteBook(int id) {

        // delete row in students table based on id
        SQLiteDatabase db = this.getWritableDatabase();

        //deleting from users table
        db.delete(TABLE_BOOK, KEY_ID + " = ?",new String[]{String.valueOf(id)});

        //deleting from users_hobby table
        db.delete(TABLE_BOOK_NAME, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        //deleting from users_city table
        db.delete(TABLE_BOOK_AUTHOR, KEY_ID + " = ?",new String[]{String.valueOf(id)});
    }

}