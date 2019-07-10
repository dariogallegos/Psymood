package com.example.psymood.Helpers;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.example.psymood.R;

public class MyActionBarDrawerToggle  extends ActionBarDrawerToggle {

    public MyActionBarDrawerToggle(AppCompatActivity activity, final DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
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
