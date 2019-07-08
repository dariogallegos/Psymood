package com.example.psymood.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.psymood.Fragments.AudioFragment;
import com.example.psymood.Fragments.HomeFragment;
import com.example.psymood.Fragments.ProfileFragment;
import com.example.psymood.Fragments.SettingsFragment;
import com.example.psymood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class NavigationHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_home);

        //Firebase instace and obtain current user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //Barra con las opciones para abrir el menu y mas cosas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Barra menu bottom
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                return onNavigationBottomItemSelected(menuItem);
            }
        });


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle mDrawertoggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawertoggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.GreyTitle));
        drawer.addDrawerListener(mDrawertoggle);
        mDrawertoggle.syncState();

        // initViewNavigation marca en el menu la primera opcion , de forma que cuando lo abres ya esta un item seleccionado.
        initViewNavigation(savedInstanceState,navigationView);
        updateNavHeader();
    }

    private void initViewNavigation(Bundle savedInstanceState, NavigationView navigationView) {
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserMail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        //Usremos Glide para cargar la photo.

        //Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
        Glide.with(this).load(currentUser.getPhotoUrl()).placeholder(R.drawable.support).into(navUserPhoto);
    }

    //flechita de adnroid para atras para cerrar el menu lateral
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }

    //Menu principal
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_home, menu);
        return true;
    }

    //Menu de opciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container,new HomeFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container,new ProfileFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container,new SettingsFragment()).commit();
                break;
            case R.id.nav_singOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent =  new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container,new HomeFragment()).commit();
                break;
            default:
                Toast.makeText(this,"Other opcion",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean onNavigationBottomItemSelected(MenuItem menuItem){
        Fragment selectFragment = null;
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                selectFragment = new HomeFragment();
                break;
            case R.id.nav_add:
                selectFragment = new ProfileFragment();
                break;
            case R.id.nav_camera:
                selectFragment = new SettingsFragment();
                break;
            case R.id.nav_audio:
                selectFragment = new AudioFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container,selectFragment).commit();
        return true;
    }

}
