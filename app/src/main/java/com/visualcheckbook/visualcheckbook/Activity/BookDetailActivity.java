package com.visualcheckbook.visualcheckbook.Activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.visualcheckbook.visualcheckbook.BookAPI.Book;
import com.visualcheckbook.visualcheckbook.BookAPI.BookClient;
import com.visualcheckbook.visualcheckbook.BooksSQLiteDataBase.DatabaseHelper;
import com.visualcheckbook.visualcheckbook.Helpers.ActivityHelper;
import com.visualcheckbook.visualcheckbook.LockOrientation;
import com.visualcheckbook.visualcheckbook.R;

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
                    ActivityHelper.showToast(getString(R.string.save_complete), getApplicationContext());
                    mAddButton.setText("Del");
                } else {
                    databaseHelper.deleteBook(Isbn);
                    ActivityHelper.showToast(getString(R.string.del_complete), getApplicationContext());
                    mAddButton.setText("Add");
                }
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
        if (MainActivity.drawerResult.isDrawerOpen()) {
            MainActivity.drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
