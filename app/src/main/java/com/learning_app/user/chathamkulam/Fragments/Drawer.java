package com.learning_app.user.chathamkulam.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.learning_app.user.chathamkulam.DbBitmapUtility;
import com.learning_app.user.chathamkulam.InternetDetector;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.rom4ek.arcnavigationview.ArcNavigationView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.learning_app.user.chathamkulam.Sqlite.RegisterMember.DATABASE_NAME;

public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //    InternetConnection Items
    InternetDetector internetDetector;
    Boolean isConnectionExist = false;

    //    SearchView Items
    Toolbar toolbar;

    Fragment fragment = null;
    boolean click = true;

    ImageView profilePic;
    CircleImageView circleImageView;
    private static final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ArcNavigationView navigationView = (ArcNavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = (TextView) headerView.findViewById(R.id.navUserName);
        TextView navEmailId = (TextView) headerView.findViewById(R.id.navEmailId);
        profilePic = (ImageView)headerView.findViewById(R.id.editProfilePic_img);
        circleImageView = (CircleImageView) headerView.findViewById(R.id.circle_imageView);

        RegisterMember registerMember = RegisterMember.getInstance(this);;
        Cursor cursorResult = registerMember.getDetails();

        while (cursorResult.moveToNext()) {

            String userName = cursorResult.getString(0);
            String emailId = cursorResult.getString(1);
            navUserName.setText(userName);
            navEmailId.setText(emailId);

        }

        final StoreEntireDetails databaseHelperStore = new StoreEntireDetails(getApplicationContext());

        Cursor Result = databaseHelperStore.getSubjectFromTable();

        if (Result.getCount() == 0) {

            NavigationItemSelected(R.id.nav_store);
            Toast.makeText(getApplicationContext(),"Please download our subjects",Toast.LENGTH_LONG).show();

        } else {

            NavigationItemSelected(R.id.nav_dashboard);

        }

        internetDetector = new InternetDetector(getApplicationContext());

        isConnectionExist = internetDetector.checkMobileInternetConn();
        if (isConnectionExist) {

            Log.v("Internet Connection","Yeah ! Internet Found !!");
        } else {

            Log.v( "No Internet Connection", "Your device doesn't have mobile internet");
        }



        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
        switch (requestCode) {
            case SELECT_PHOTO:

                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

//                    dbBitmapUtility.
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);


                    circleImageView.setImageURI(selectedImage);// To display selected image in image view
                }

                break;

        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationItemSelected(R.id.nav_dashboard);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!NavigationItemSelected(R.id.nav_dashboard)){
            backButtonHandler();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Select your option";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        NavigationItemSelected(item.getItemId());
        return true;
    }

    public boolean NavigationItemSelected(int  getItemId){


        switch (getItemId){

            case R.id.nav_dashboard:

                fragment = new FMDashboard();

                break;

            case R.id.nav_store:

//                if (isConnectionExist){

                fragment = new FMStore();

//                }else{
//                    Toast.makeText(getApplicationContext(),"Please connect to internet",Toast.LENGTH_LONG).show();
//                }

                break;

            case R.id.nav_calc:

                boolean isCalcInstalled = appInstalledOrNot("com.android.calculator2");

                if(isCalcInstalled) {

                    Intent LaunchIntent = getPackageManager()
                            .getLaunchIntentForPackage("com.android.calculator2");
                    startActivity(LaunchIntent);

                    Toast.makeText(getBaseContext(),"Calculator Available On Your Device",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getBaseContext(),"Calculator NotAvailable Please Download", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.nav_dictionary:

                boolean isDicInstalled = appInstalledOrNot("com.android.calculator");

                if(isDicInstalled) {

                    Intent LaunchIntent = getPackageManager()
                            .getLaunchIntentForPackage("com.android.calculator");
                    startActivity(LaunchIntent);

                    Toast.makeText(getBaseContext(),"Dictionary Available On Your Device",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getBaseContext(),"Dictionary NotAvailable Please Download", Toast.LENGTH_LONG).show();
                }

                break;

        }

        if (fragment !=null){

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

//        mainActivity.bg_video(bg_video_view,bg_video_path);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return false;
    }

    public void backButtonHandler() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                Drawer.this);
        alertDialog.setTitle("     Leave application !!");
        alertDialog.setMessage("Are you sure you want to leave the application?");
        alertDialog.setIcon(R.drawable.ic_question);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void logoutPermission() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                Drawer.this);
        alertDialog.setTitle("     Logout !!");
        alertDialog.setMessage("Are you sure you want to logout the session?");
        alertDialog.setIcon(R.drawable.ic_question);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (deleteDatabase(DATABASE_NAME)){

                            Toast.makeText(getApplicationContext(),"You are successfully Logout",Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            startActivity(intent);

                        }else {

                            Toast.makeText(getApplicationContext(),"Operation unsuccessful",Toast.LENGTH_LONG).show();
                        }

                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {

        Process.killProcess(Process.myPid());
        super.onDestroy();
    }

}

