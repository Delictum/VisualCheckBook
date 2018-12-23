package com.visualcheckbook.visualcheckbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import de.hdodenhof.circleimageview.CircleImageView;


public class HeplerTabActivity extends AppCompatActivity  {

    private Toolbar mToolbar;
    private Drawer.Result drawerResult = null;
    private ScrollView mScrollView;

    private ImageView mGifImageView;
    private TextView mHelperText;
    private CircleImageView mCircleImageView1;
    private CircleImageView mCircleImageView2;
    private CircleImageView mCircleImageView3;

    private Integer currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hepler_tab);

        initCustomModel();
    }

    private void initCustomModel() {
        new LockOrientation(this).lock();

        currentPage = 0;

        initToolbar();
        initDrawerMenu();
        initSliding();

        initGifImageView();

        initViewComponents();
    }

    private void initViewComponents() {
        mHelperText = findViewById(R.id.textInstruction);
        mHelperText.setText(R.string.HelperTextCameraActivity);

        mCircleImageView1 = findViewById(R.id.circle_tab_1);
        mCircleImageView2 = findViewById(R.id.circle_tab_2);
        mCircleImageView3 = findViewById(R.id.circle_tab_3);

        mCircleImageView1.setImageResource(R.drawable.sky_blue);
        mCircleImageView2.setImageResource(R.drawable.white);
        mCircleImageView3.setImageResource(R.drawable.white);
    }

    private void initGifImageView() {
        mGifImageView = (ImageView) findViewById(R.id.gifInstruction);
        Glide.with(this).load("http://bestanimations.com/Books/pretty-book-bench-nature-water-outdoors-animated-gif.gif").into(mGifImageView);
    }

    private void initDrawerMenu() {
        //Sliding menu
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(ActivityHelper.initDrawerItems(3))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            if (position == 1) {
                                Intent intent = new Intent(HeplerTabActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else if (position == 2) {
                                Intent intent = new Intent(HeplerTabActivity.this, BookLibraryActivity.class);
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

    private void initSliding() {
        mScrollView = findViewById(R.id.scrollViewHelperTab);
        mScrollView.setOnTouchListener(new OnSwipeTouchListener(HeplerTabActivity.this) {
            public void onSwipeRight() {
                if (currentPage == 0) {
                    drawerResult.openDrawer();
                }
                else {
                    currentPage--;
                    setCurrentPageView();
                }
            }

            public void onSwipeLeft() {
                if (currentPage == 2) {
                    return;
                }
                currentPage++;
                setCurrentPageView();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void setCurrentPageView() {
        switch (currentPage) {
            case (0): {
                mHelperText.setText(R.string.HelperTextCameraActivity);
                mCircleImageView1.setImageResource(R.drawable.sky_blue);
                mCircleImageView2.setImageResource(R.drawable.white);
                mCircleImageView3.setImageResource(R.drawable.white);
                break;
            }
            case (1): {
                mHelperText.setText(R.string.HelperTextLibraryActivity);
                mCircleImageView1.setImageResource(R.drawable.white);
                mCircleImageView2.setImageResource(R.drawable.sky_blue);
                mCircleImageView3.setImageResource(R.drawable.white);
                break;
            } case (2): {
                mHelperText.setText(R.string.HelperTextDetailActivity);
                mCircleImageView1.setImageResource(R.drawable.white);
                mCircleImageView2.setImageResource(R.drawable.white);
                mCircleImageView3.setImageResource(R.drawable.sky_blue);
                break;
            }
        }
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
