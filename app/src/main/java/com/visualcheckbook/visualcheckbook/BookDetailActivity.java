package com.visualcheckbook.visualcheckbook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.picasso.Picasso;
import com.visualcheckbook.visualcheckbook.BookAPI.Book;
import com.visualcheckbook.visualcheckbook.BookAPI.BookClient;
import com.visualcheckbook.visualcheckbook.BooksDataBase.DatabaseHelper;
import com.visualcheckbook.visualcheckbook.Helpers.ActivityHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.mime.Header;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvPageCount;
    private BookClient client;

    private Button mAddButton;
    private DatabaseHelper databaseHelper;

    private Toolbar mToolbar;
    private Drawer.Result drawerResult = null;
    private ScrollView mScrollView;

    public static String Isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        initCustomModel();
    }

    private void initCustomModel() {
        new LockOrientation(this).lock();

        initComponents();
        initToolbar();
        initDrawerMenu();
        initSliding();
    }

    private void initComponents() {
        new LockOrientation(this).lock();
        // Fetch views
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvPublisher = (TextView) findViewById(R.id.tvPublisher);
        tvPageCount = (TextView) findViewById(R.id.tvPageCount);
        // Use the book to populate the data into our views

        //Book book = (Book) getIntent().getSerializableExtra(BookLibraryActivity.BOOK_DETAIL_KEY);
        Book book = (Book) getIntent().getSerializableExtra(MainActivity.BOOK_DETAIL_KEY);
        loadBook(book);

        databaseHelper = new DatabaseHelper(this);

        mAddButton = (Button) findViewById(R.id.add_button);
        if (databaseHelper.tryGetBook(Isbn)) {
            mAddButton.setText("Del");
        }
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button currentStateButton = (Button)v;
                String btnName = currentStateButton.getText().toString();
                if (btnName.contains("Add")) {
                    databaseHelper.addBook(Isbn, tvTitle.getText().toString(), tvAuthor.getText().toString());
                    ActivityHelper.showToast("Saved successfully!", getApplicationContext());
                    mAddButton.setText("Del");
                } else {
                    databaseHelper.deleteBook(Isbn);
                    ActivityHelper.showToast("Successfully deleted!", getApplicationContext());
                    mAddButton.setText("Add");
                }
            }
        });
    }

    private void initDrawerMenu() {
        //Sliding menu
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(ActivityHelper.initDrawerItems(5))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            if (position == 1) {
                                Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else if (position == 2) {
                                Intent intent = new Intent(BookDetailActivity.this, BookLibraryActivity.class);
                                startActivity(intent);
                            } else if(position == 5) {
                                Intent intent = new Intent(BookDetailActivity.this, HelperTabFragment.class);
                                startActivity(intent);
                            }
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            if (badgeable.getBadge() != null) {
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

    private void initSliding() {
        mScrollView = findViewById(R.id.scrollViewBookDetail);
        mScrollView.setOnTouchListener(new OnSwipeTouchListener(BookDetailActivity.this) {
            public void onSwipeRight() {

                drawerResult.openDrawer();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    // Populate data for the book
    private void loadBook(Book book) {
        //change activity title
        this.setTitle(book.getTitle());
        // Populate data
        Picasso.with(this).load(Uri.parse(book.getLargeCoverUrl())).error(R.drawable.ic_nocover).into(ivBookCover);
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        // fetch extra book data from books API
        client = new BookClient();
        client.getExtraBookDetails(book.getOpenLibraryId(), new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("publishers")) {
                        // display comma separated list of publishers
                        final JSONArray publisher = response.getJSONArray("publishers");
                        final int numPublishers = publisher.length();
                        final String[] publishers = new String[numPublishers];
                        for (int i = 0; i < numPublishers; ++i) {
                            publishers[i] = publisher.getString(i);
                        }
                        tvPublisher.setText(TextUtils.join(", ", publishers));
                    }
                    if (response.has("number_of_pages")) {
                        tvPageCount.setText(Integer.toString(response.getInt("number_of_pages")) + " pages");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
