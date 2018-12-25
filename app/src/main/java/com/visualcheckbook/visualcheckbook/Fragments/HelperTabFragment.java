package com.visualcheckbook.visualcheckbook.Fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.visualcheckbook.visualcheckbook.Helpers.ActivityHelper;
import com.visualcheckbook.visualcheckbook.MainActivity;
import com.visualcheckbook.visualcheckbook.OnSwipeTouchListener;
import com.visualcheckbook.visualcheckbook.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class HelperTabFragment extends Fragment {

    private LinearLayout mLinearLayout;
    private ImageView mGifImageView;
    private TextView mHelperText;
    private CircleImageView mCircleImageView1;
    private CircleImageView mCircleImageView2;
    private CircleImageView mCircleImageView3;

    private Integer currentPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hepler_tab, container, false);

        initCustomModel(view);
        return view;
    }

    private void initCustomModel(View view) {
        currentPage = 0;

        initSliding(view);
        initGifImageView(view);
        initViewComponents(view);
    }

    private void initViewComponents(View view) {
        mHelperText = view.findViewById(R.id.textInstruction);
        mHelperText.setText(R.string.HelperTextCameraActivity);

        mCircleImageView1 = view.findViewById(R.id.circle_tab_1);
        mCircleImageView2 = view.findViewById(R.id.circle_tab_2);
        mCircleImageView3 = view.findViewById(R.id.circle_tab_3);

        mCircleImageView1.setImageResource(R.drawable.sky_blue);
        mCircleImageView2.setImageResource(R.drawable.white);
        mCircleImageView3.setImageResource(R.drawable.white);
    }

    private void initGifImageView(View view) {
        mGifImageView = (ImageView) view.findViewById(R.id.gifInstruction);
        Glide.with(this).load("http://bestanimations.com/Books/pretty-book-bench-nature-water-outdoors-animated-gif.gif").into(mGifImageView);
    }

    private void initSliding(View view) {

        View.OnTouchListener sliding = new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                if (currentPage == 0) {
                    MainActivity.drawerResult.openDrawer();
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
        };

        mLinearLayout = (LinearLayout) view.findViewById(R.id.helper_linear_helper_tab);
        mLinearLayout.setOnTouchListener(sliding);

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view_helper_tab);
        scrollView.setOnTouchListener(sliding);
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
}
