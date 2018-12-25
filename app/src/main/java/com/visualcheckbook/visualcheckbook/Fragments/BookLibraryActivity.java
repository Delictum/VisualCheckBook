package com.visualcheckbook.visualcheckbook.Fragments;

import cz.msebera.android.httpclient.Header;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.visualcheckbook.visualcheckbook.BookAPI.Book;
import com.visualcheckbook.visualcheckbook.BookAPI.BookAdapter;
import com.visualcheckbook.visualcheckbook.BookAPI.BookClient;
import com.visualcheckbook.visualcheckbook.BookDetailActivity;
import com.visualcheckbook.visualcheckbook.BooksDataBase.BookModel;
import com.visualcheckbook.visualcheckbook.BooksDataBase.CustomAdapter;
import com.visualcheckbook.visualcheckbook.BooksDataBase.DatabaseHelper;
import com.visualcheckbook.visualcheckbook.BuildConfig;
import com.visualcheckbook.visualcheckbook.Helpers.ActivityHelper;
import com.visualcheckbook.visualcheckbook.MainActivity;
import com.visualcheckbook.visualcheckbook.OnSwipeTouchListener;
import com.visualcheckbook.visualcheckbook.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookLibraryActivity extends Fragment {
    private BookAdapter bookAdapter;
    private BookClient client;

    private RelativeLayout mRelativeLayout;

    private ListView mListView;
    private ArrayList<BookModel> bookModelArrayList;
    private CustomAdapter customAdapter;
    private DatabaseHelper databaseHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_library, container, false);

        initCustomModel(view);
        return view;
    }

    private void initCustomModel(View view) {

        initDataBaseItems(view);
        initListBooks();
        initSliding(view);
    }

    private void initDataBaseItems(View view) {
        mListView = (ListView) view.findViewById(R.id.lvBooks);
        databaseHelper = new DatabaseHelper(getContext());
        bookModelArrayList = databaseHelper.getAllBooks();
        customAdapter = new CustomAdapter(getContext(), bookModelArrayList);
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
        bookAdapter = new BookAdapter(getContext(), aBooks);
    }

    private void initSliding(View view) {
        mRelativeLayout = view.findViewById(R.id.relativeLayoutBookLibrary);
        mRelativeLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {

                MainActivity.drawerResult.openDrawer();
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
                            ActivityHelper.showToast("Unfortunately the library does not contain data on the book.", getContext());
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
                        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
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
                ActivityHelper.showToast("Problem connecting to server.", getContext());
            }
        });
    }
}
