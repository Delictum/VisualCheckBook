package com.visualcheckbook.visualcheckbook;

import cz.msebera.android.httpclient.Header;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.visualcheckbook.visualcheckbook.BookAPI.Book;
import com.visualcheckbook.visualcheckbook.BookAPI.BookAdapter;
import com.visualcheckbook.visualcheckbook.BookAPI.BookClient;
import com.visualcheckbook.visualcheckbook.BooksDataBase.BookModel;
import com.visualcheckbook.visualcheckbook.BooksDataBase.CustomAdapter;
import com.visualcheckbook.visualcheckbook.BooksDataBase.DatabaseHelper;
import com.visualcheckbook.visualcheckbook.Helpers.ActivityHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookLibraryActivity extends AppCompatActivity {
    private BookAdapter bookAdapter;
    private BookClient client;

    private Toolbar mToolbar;
    private Drawer.Result drawerResult = null;
    private RelativeLayout mRelativeLayout;

    private ListView mListView;
    private ArrayList<BookModel> bookModelArrayList;
    private CustomAdapter customAdapter;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_library);

        initCustomModel();
    }

    private void initCustomModel() {
        new LockOrientation(this).lock();

        initToolbar();
        initDrawerMenu();
        initDataBaseItems();
        initListBooks();
        initSliding();
    }

    private void initDrawerMenu() {
        //Sliding menu
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(ActivityHelper.initDrawerItems(1))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            if (position == 1) {
                                Intent intent = new Intent(BookLibraryActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else if (position == 5) {
                                Intent intent = new Intent(BookLibraryActivity.this, HeplerTabActivity.class);
                                startActivity(intent);
                            }
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            if (badgeable.getBadge() != null) {
                                // учтите, не делайте так, если ваш бейдж содержит символ "+"
                                try {
                                    int badge = Integer.valueOf(badgeable.getBadge());
                                    if (badge > 0) {
                                        drawerResult.updateBadge(String.valueOf(badge - 1), position);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {

                        }
                        return false;
                    }
                })
                .build();
    }

    private  void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDataBaseItems() {
        mListView = (ListView) findViewById(R.id.lvBooks);
        databaseHelper = new DatabaseHelper(this);
        bookModelArrayList = databaseHelper.getAllBooks();
        customAdapter = new CustomAdapter(this, bookModelArrayList);
        mListView.setAdapter(customAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                queryBooks(customAdapter.getItem(position).getIsbn());
            }
        });
    }

    private void initListBooks() {
        ArrayList<Book> aBooks = new ArrayList<Book>();
        bookAdapter = new BookAdapter(this, aBooks);
    }

    private void initSliding() {
        mRelativeLayout = findViewById(R.id.relativeLayoutBookLibrary);
        mRelativeLayout.setOnTouchListener(new OnSwipeTouchListener(BookLibraryActivity.this) {
            public void onSwipeRight() {

                drawerResult.openDrawer();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void queryBooks(final String searchString) {

        client = new BookClient();
        client.getBooks(searchString, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray docs = null;
                    if(response != null) {
                        // Get the docs json array
                        docs = response.getJSONArray("docs");

                        // Parse json array into array of model objects
                        final ArrayList<Book> books = Book.fromJson(docs);

                        if (books == null) {
                            ActivityHelper.showToast("Unfortunately the library does not contain data on the book.", getApplicationContext());
                            return;
                        }

                        if (bookAdapter != null)
                            bookAdapter.clear();
                        // Load model objects into the adapter
                        for (Book book : books) {
                            bookAdapter.add(book); // add book through the adapter
                        }
                        if (bookAdapter != null)
                            bookAdapter.notifyDataSetChanged();

                        BookDetailActivity.Isbn = searchString;
                        // Launch the detail view passing book as an extra
                        Intent intent = new Intent(BookLibraryActivity.this, BookDetailActivity.class);
                        intent.putExtra(MainActivity.BOOK_DETAIL_KEY, bookAdapter.getItem(0));
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(MainActivity.TAG, "Invalid JSON format", e);
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ActivityHelper.showToast("Problem connecting to server.", getApplicationContext());
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
