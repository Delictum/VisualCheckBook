package com.visualcheckbook.visualcheckbook.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.visualcheckbook.visualcheckbook.BookAPI.Book;
import com.visualcheckbook.visualcheckbook.BookAPI.BookAdapter;
import com.visualcheckbook.visualcheckbook.BookAPI.BookClient;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.FirebaseVision;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.visualcheckbook.visualcheckbook.BuildConfig;
import com.visualcheckbook.visualcheckbook.Fragments.BookLibraryFragment;
import com.visualcheckbook.visualcheckbook.Fragments.HelperTabFragment;
import com.visualcheckbook.visualcheckbook.Fragments.SettingsFragment;
import com.visualcheckbook.visualcheckbook.Helpers.ActivityHelper;
import com.visualcheckbook.visualcheckbook.Helpers.CustomSettingsHelper;
import com.visualcheckbook.visualcheckbook.Helpers.ImageHelper;
import com.visualcheckbook.visualcheckbook.IsbnParser;
import com.visualcheckbook.visualcheckbook.LockOrientation;
import com.visualcheckbook.visualcheckbook.OnSwipeTouchListener;
import com.visualcheckbook.visualcheckbook.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {
    private LinearLayout mainLinearLayout;

    private ImageView mCameraImageView;
    private Button mTextRecognitionButton;
    private Button mCameraButton;
    private Button mRotationButton;
    private Bitmap mSelectedImage;
    private AlertDialog.Builder mExitDialog;

    public static Toolbar mToolbar;
    public static Drawer.Result drawerResult = null;

    private Uri outputFileUri;
    private BookClient client;
    private BookAdapter bookAdapter;

    private Integer angleRotate = 0;
    private final Integer ROTATE_IMAGE = 90;

    private Integer currentPositionDrawerMenu;
    private Fragment currentFragment;

    private String pictureImagePath = "";

    private final String fileSaveImageName = "temp.jpg";
    public static final String BOOK_DETAIL_KEY = "book";
    private static final Integer REQUEST_IMAGE_CAPTURE = 1;

    public static final String TAG = "VisualCheckBook";
    public static final String VERSION = "1.0.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initCustomModel();
    }

    private void initListBooks() {
        ArrayList<Book> aBooks = new ArrayList<Book>();
        bookAdapter = new BookAdapter(this, aBooks);
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
                            ActivityHelper.showToast(getString(R.string.does_not_contain_book), getApplicationContext());
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
                        Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                        intent.putExtra(BOOK_DETAIL_KEY, bookAdapter.getItem(0));
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Invalid JSON format", e);
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ActivityHelper.showToast(getString(R.string.problem_server_connection), getApplicationContext());
            }
        });
    }

    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        mTextRecognitionButton.setEnabled(false);
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                mTextRecognitionButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mTextRecognitionButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            ActivityHelper.showToast(getString(R.string.text_not_found), getApplicationContext());
            return;
        }

        String allText = "";

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    allText += elements.get(k).getText() + ";";
                }
            }
        }

        String ISBN = IsbnParser.ParserISBN(allText);
        if (ISBN == null)
            ActivityHelper.showToast(getString(R.string.incorrect_recognition_isbn), getApplicationContext());
        else
            queryBooks(ISBN);
    }

    private void RotateImage() {
        angleRotate += ROTATE_IMAGE;
        mCameraImageView.animate().rotation(angleRotate).start();
        mSelectedImage = ImageHelper.rotateImage(angleRotate, mSelectedImage);
    }

    private void initCustomModel() {

        setContentView(R.layout.activity_main);
        new LockOrientation(this).lock();
        ActivityHelper.initLocaleHelper(this);

        initRecognition();
        initCamera();
        initRotate();

        initToolbar();
        initDrawerMenu();
        initSliding();

        initListBooks();
        initQuestionExitDialog();

        mainLinearLayout = findViewById(R.id.main_liner_layout);

        currentPositionDrawerMenu = initStartScreen();
    }

    private int initStartScreen() {
        int valueStartScreen = CustomSettingsHelper.getPositionStartScreen(this);
        setEnabledDrawerItem(valueStartScreen == 0 ? 1 : valueStartScreen, false);
        switch (valueStartScreen) {
            case (0): {

                break;
            }
            case (1): {
                setVisibilityMainLayout(View.INVISIBLE);
                setEnabledDrawerItem(1, true);
                setEnabledDrawerItem(2, false);
                currentFragment = new BookLibraryFragment();
                break;
            }
        }

        if (valueStartScreen != 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, currentFragment)
                    .commit();
        }
        return ++valueStartScreen;
    }

    private void initRotate() {

        mRotationButton = findViewById(R.id.rotate_button);
        mRotationButton.setEnabled(false);
        mRotationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateImage();
            }
        });
    }

    private void initSliding() {

        mCameraImageView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {

                drawerResult.openDrawer();
            }
            public void onSwipeLeft() {

                dispatchTakePictureIntent();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void initRecognition() {

        mCameraImageView = findViewById(R.id.image_view);
        mTextRecognitionButton = findViewById(R.id.button_text);

        //Set event for button
        mTextRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTextRecognition();
            }
        });

        mTextRecognitionButton.setEnabled(false);
    }

    private void initCamera() {

        mCameraButton = findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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
                .addDrawerItems(ActivityHelper.initDrawerItems(0))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {

                        if (drawerItem instanceof Nameable) {

                            setEnabledDrawerItem(position, false);
                            setEnabledDrawerItem(currentPositionDrawerMenu, true);
                            if (position == 1) {

                                setVisibilityMainLayout(View.VISIBLE);
                                getSupportFragmentManager().beginTransaction()
                                        .remove(currentFragment)
                                        .commit();
                            } else if (position == 2) {

                                setVisibilityMainLayout(View.INVISIBLE);
                                currentFragment = new BookLibraryFragment();
                            } else if (position == 4) {

                                setVisibilityMainLayout(View.INVISIBLE);
                                currentFragment = new SettingsFragment();
                            }
                            else if (position == 5) {

                                setVisibilityMainLayout(View.INVISIBLE);
                                currentFragment = new HelperTabFragment();
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
                        if (position != 0) {
                            if (position == 4) {
                                SettingsFragment settingsFragment = (SettingsFragment) currentFragment;
                                settingsFragment.first = true;
                                currentFragment = settingsFragment;
                            }
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, currentFragment)
                                    .commit();
                            currentPositionDrawerMenu = position;
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

    private void initQuestionExitDialog() {

        mExitDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setIcon(R.drawable.ic_book)
                .setMessage(R.string.message_question_exit_dialog);

        mExitDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick (DialogInterface dialog,int arg1){
                        finish();
                    }
                });
        mExitDialog.setNegativeButton(R.string.no, null);
    }

    private void setVisibilityMainLayout(int condition) {
        mainLinearLayout.setVisibility(condition);
        mCameraImageView.setVisibility(condition);
    }

    private void setEnabledDrawerItem(int position, boolean condition) {
        switch (position) {
            case 1: {
                drawerResult.updateItem(new PrimaryDrawerItem()
                        .withName(R.string.drawer_item_home)
                        .withIcon(FontAwesome.Icon.faw_camera)
                        .setEnabled(condition), position);
                break;
            }
            case 2: {
                drawerResult.updateItem(new PrimaryDrawerItem()
                        .withName(R.string.drawer_item_library_book)
                        .withIcon(FontAwesome.Icon.faw_book)
                        .setEnabled(condition), position);
                break;
            }
            case 4: {
                drawerResult.updateItem(new SecondaryDrawerItem()
                        .withName(R.string.drawer_item_settings)
                        .withIcon(FontAwesome.Icon.faw_cog)
                        .setEnabled(condition), position);
                break;
            }
            case 5: {
                drawerResult.updateItem(new SecondaryDrawerItem()
                        .withName(R.string.drawer_item_help)
                        .withIcon(FontAwesome.Icon.faw_question)
                        .setEnabled(condition), position);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                File imgFile = new File(pictureImagePath);
                if (imgFile.exists()) {
                    mCameraImageView.setImageResource(0);
                    mCameraImageView.setImageURI(outputFileUri);
                    mSelectedImage = ((BitmapDrawable) mCameraImageView.getDrawable()).getBitmap();

                    if (!mTextRecognitionButton.isEnabled()) {
                        mTextRecognitionButton.setEnabled(true);
                        mRotationButton.setEnabled(true);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                ActivityHelper.showToast(getString(R.string.cancel_operation), getApplicationContext());
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            ActivityHelper.showToast(getString(R.string.error_receiving_photos), getApplicationContext());
            if (BuildConfig.DEBUG) {
                Log.w(TAG, getString(R.string.error_receiving_photos), e);
            }
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            //Create place for temp img on absolute dir
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            pictureImagePath = storageDir.getAbsolutePath() + "/" + fileSaveImageName;
            File file = new File(pictureImagePath);
            outputFileUri = Uri.fromFile(file);

            //Get img with open camera
            Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

        } catch (ActivityNotFoundException e) {
            ActivityHelper.showToast(getString(R.string.device_not_support_shooting), getApplicationContext());
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Device does not support shooting", e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            mExitDialog.show();
        }
    }
}