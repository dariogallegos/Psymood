package com.example.psymood.Helpers;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;

import com.example.psymood.R;

public class MyActionBarDrawerToggle  extends ActionBarDrawerToggle {

    public MyActionBarDrawerToggle(Activity activity, final DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);

        setDrawerIndicatorEnabled(true);
        setHomeAsUpIndicator(R.drawable.ic_menu_camera);
        setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }
}
