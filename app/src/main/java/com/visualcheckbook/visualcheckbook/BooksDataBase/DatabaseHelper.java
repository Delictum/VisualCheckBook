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

    public ArrayList<BookModel> getAllBooks() {
        ArrayList<BookModel> booksModelArrayList = new ArrayList<BookModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_BOOK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                BookModel bookModel = new BookModel();
                bookModel.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                bookModel.setIsbn(c.getString(c.getColumnIndex(KEY_ISBN)));

                String selectNameQuery = "SELECT  * FROM " + TABLE_BOOK_NAME + " WHERE " +KEY_ID+ " = " + bookModel.getId();

                Cursor cursorName = db.rawQuery(selectNameQuery, null);

                if (cursorName.moveToFirst()) {
                    do {
                        bookModel.setName(cursorName.getString(cursorName.getColumnIndex(KEY_BOOK_NAME)));
                    } while (cursorName.moveToNext());
                }

                String selectAuthorQuery = "SELECT  * FROM " + TABLE_BOOK_AUTHOR + " WHERE " + KEY_ID + " = " + bookModel.getId();;

                Cursor cursorAuthor = db.rawQuery(selectAuthorQuery, null);

                if (cursorAuthor.moveToFirst()) {
                    do {
                        bookModel.setAuthor(cursorAuthor.getString(cursorAuthor.getColumnIndex(KEY_BOOK_AUTHOR)));
                    } while (cursorAuthor.moveToNext());
                }

                booksModelArrayList.add(bookModel);
            } while (c.moveToNext());
        }
        return booksModelArrayList;
    }

    public Boolean tryGetBook(String isbn) {
        String selectQuery = "SELECT  * FROM " + TABLE_BOOK + " WHERE " + KEY_ISBN + "=" + isbn;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.getCount() > 0)
            return true;
        return false;
    }

    public void addBook(String isbn, String name, String author) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ISBN, isbn);

        long id = db.insertWithOnConflict(TABLE_BOOK, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        ContentValues valuesName = new ContentValues();
        valuesName.put(KEY_ID, id);
        valuesName.put(KEY_BOOK_NAME, name);
        db.insert(TABLE_BOOK_NAME, null, valuesName);

        ContentValues valuesAuthor = new ContentValues();
        valuesAuthor.put(KEY_ID, id);
        valuesAuthor.put(KEY_BOOK_AUTHOR, author);
        db.insert(TABLE_BOOK_AUTHOR, null, valuesAuthor);
    }

    public void updateBook(int id, String isbn, String name, String author) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ISBN, isbn);
        db.update(TABLE_BOOK, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesName = new ContentValues();
        valuesName.put(KEY_BOOK_NAME, name);
        db.update(TABLE_BOOK_NAME, valuesName, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        ContentValues valuesAuthor = new ContentValues();
        valuesAuthor.put(KEY_BOOK_AUTHOR, author);
        db.update(TABLE_BOOK_AUTHOR, valuesAuthor, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BOOK, KEY_ID + " = ?",new String[]{String.valueOf(id)});
        db.delete(TABLE_BOOK_NAME, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_BOOK_AUTHOR, KEY_ID + " = ?",new String[]{String.valueOf(id)});
    }

    public void deleteBook(String isbn) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_BOOK + " WHERE " + KEY_ISBN + "=" + isbn;
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        String id  = c.getString(c.getColumnIndex(KEY_ID));

        db.delete(TABLE_BOOK, KEY_ID + " = ?",new String[]{String.valueOf(id)});
        db.delete(TABLE_BOOK_NAME, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_BOOK_AUTHOR, KEY_ID + " = ?",new String[]{String.valueOf(id)});
    }
}