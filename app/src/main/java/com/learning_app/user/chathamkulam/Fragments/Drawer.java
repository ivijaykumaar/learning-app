package com.learning_app.user.chathamkulam.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.learning_app.user.chathamkulam.EditProfileStatus;
import com.learning_app.user.chathamkulam.Feedback.FMFeedBack;
import com.learning_app.user.chathamkulam.GCMRegistrationIntentService;
import com.learning_app.user.chathamkulam.Model.BackgroundWork.VersionChecker;
import com.learning_app.user.chathamkulam.Model.DbBitmapUtility;
import com.learning_app.user.chathamkulam.Model.InternetDetector;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.RegisterMember;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;
import com.rom4ek.arcnavigationview.ArcNavigationView;
import com.tonyodev.fetch.Fetch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //    InternetConnection Items
    InternetDetector internetDetector;
    Boolean isConnectionExist = false;

    //    SearchView Items
    Toolbar toolbar;

    Fragment fragment = null;

    ImageView editProfile;
    CircleImageView circleImageView;

    RegisterMember registerMember;
    Cursor cursorResult;
    DbBitmapUtility dbBitmapUtility;
    private BroadcastReceiver broadcastReceiver;

    public static void setBadge(Context context, int count) {

        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

//        setBadge(this,5);

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
        editProfile = (ImageView) headerView.findViewById(R.id.editProfilePic_img);
        circleImageView = (CircleImageView) headerView.findViewById(R.id.circle_imageView);

        dbBitmapUtility = new DbBitmapUtility();
        registerMember = RegisterMember.getInstance(this);
        cursorResult = registerMember.getDetails();

        Fetch.startService(this);

        while (cursorResult.moveToNext()) {

            String userName = cursorResult.getString(1);
            String emailId = cursorResult.getString(2);
            byte[] profile = cursorResult.getBlob(4);

            Log.d("profileStatus", cursorResult.getCount() + " " + userName + "  " + emailId + " " + Arrays.toString(profile));

            navUserName.setText(userName);
            navEmailId.setText(emailId);
            circleImageView.setImageBitmap(dbBitmapUtility.getImage(profile));

        }

        StoreEntireDetails databaseHelperStore = new StoreEntireDetails(getApplicationContext());

        Cursor Result = databaseHelperStore.getSubjectFromTable();

        if (Result.getCount() == 0) {

            NavigationItemSelected(R.id.nav_store);
            Toast.makeText(getApplicationContext(),"Please download our subjects", Toast.LENGTH_LONG).show();

        } else {

            NavigationItemSelected(R.id.nav_dashboard);

        }

//        startActivity(new Intent(this, Test.class));

        internetDetector = new InternetDetector(getApplicationContext());
        isConnectionExist = internetDetector.checkMobileInternetConn();

        if (isConnectionExist) {

            Log.v("internetConnection", "Yeah ! Internet Found !!");

            VersionChecker versionChecker = new VersionChecker(this);
            versionChecker.execute();

        } else {

            Log.v("internetConnection", "Your device doesn't have mobile internet");
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), EditProfileStatus.class));
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            for (Fragment f : fragmentList) {
                if (f instanceof FMDashboard) {
                    handled = f.isAdded();
                    if (handled) {
                        backButtonHandler();
                        break;
                    }
                }
            }
            if (!handled) {
                super.onBackPressed();
            }
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
                String shareBody = "https://play.google.com/store/apps/details?id=com.learning_app.user.chathamkulam&hl=en";
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

    public boolean NavigationItemSelected(int getItemId) {


        switch (getItemId) {

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

                ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

                final PackageManager pm = getPackageManager();
                List<PackageInfo> packs = pm.getInstalledPackages(0);
                for (PackageInfo pi : packs) {
                    if (pi.packageName.toLowerCase().contains("calcul")) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("appName", pi.applicationInfo.loadLabel(pm));
                        map.put("packageName", pi.packageName);
                        items.add(map);
                    }
                }

                if (items.size() >= 1) {
                    String packageName = (String) items.get(0).get("packageName");
                    Intent i = pm.getLaunchIntentForPackage(packageName);
                    if (i != null)
                        startActivity(i);
                } else {
                    // Application not found
                }


                break;

            case R.id.nav_feedback:

                fragment = new FMFeedBack();

                break;

        }

        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

    @Override
    protected void onDestroy() {

        Process.killProcess(Process.myPid());
        super.onDestroy();
    }
}

