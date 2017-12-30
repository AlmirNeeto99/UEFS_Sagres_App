package com.forcetower.uefs.view.class_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;
import com.forcetower.uefs.view.UEFSBaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassDetailsActivity extends UEFSBaseActivity implements ClassDetailsCallback {
    private static final String CLASS_CODE_KEY = "class_code";
    private static final String SEMESTER_KEY = "semester";
    private static final String GROUP_KEY = "group";

    @BindView(R.id.fab_activity_action)
    FloatingActionButton fabActivityActions;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.fragment_container)
    ViewPager viewPager;

    public static void startActivity(Context context, String classCode, String semester, String group) {
        Intent intent = new Intent(context, ClassDetailsActivity.class);
        intent.putExtra(CLASS_CODE_KEY, classCode);
        intent.putExtra(SEMESTER_KEY, semester);
        intent.putExtra(GROUP_KEY, group);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);
        ButterKnife.bind(this);

        if (!makeSureEverythingIsOkayOrFinish()) return;

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Utils.isLollipop()) {
            findViewById(R.id.app_bar_layout).setElevation(10);
        }

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.class_details);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageSelected(int position) {
                if (fabActivityActions.getVisibility() != View.VISIBLE) {
                    fabActivityActions.show();
                }

                if (position == 0) {
                    fabActivityActions.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_black_24dp));
                } else if (position == 1) {
                    fabActivityActions.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
                }
            }
        });

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        tabLayout.getTabAt(0).setText(R.string.class_overview);
        tabLayout.getTabAt(1).setText(R.string.class_todo_list);
        tabLayout.getTabAt(0).select();

        //replaceFragmentContainer(getSupportFragmentManager(), new TodoListFragment(), R.id.fragment_container, "overview");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean makeSureEverythingIsOkayOrFinish() {
        if (SagresProfile.getCurrentProfile() == null) {
            boolean loaded = SagresProfileManager.getInstance().loadCurrentProfile();
            if (!loaded) {
                Toast.makeText(this, R.string.this_is_and_error, Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0 && fabActivityActions.getVisibility() == View.VISIBLE) fabActivityActions.hide();
        else if (dy < 0 && fabActivityActions.getVisibility() != View.VISIBLE) fabActivityActions.show();
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new OverviewFragment());
            fragments.add(new TodoListFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
