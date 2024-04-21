package com.example.coursetracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private FloatingActionButton graph_button;
    private ImageView empty_view;
    private TextView no_data;
    private DBHelper myDB;
    // Declare ArrayLists for course information
    private ArrayList<String> course_id;
    private ArrayList<String> course_title;
    private ArrayList<String> course_description;
    private ArrayList<String> course_percent;
    private RecyclerView customAdapter;
    private Switch enableSMSSwitch;
    private User user = User.getInstance();
    private String username = user.getUsername();
    private String phone = user.getPhone();
    private static final int SMS_PERMISSION_REQUEST_CODE = 123;
    private boolean smsSwitchState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DBHelper(MainActivity.this);
        System.out.println(user);
        // load data to the ui
        loadUIData();
        // handle sms checks
        handleSMS();


        // Open the graph activity and set the intent
        graph_button.setOnClickListener(view -> {
            Intent graphIntent = new Intent(MainActivity.this, GraphActivity.class);

            // send the arraylist data to the intent for use in the graph activity
            graphIntent.putExtra("title", course_title);
            graphIntent.putExtra("description", course_description);
            graphIntent.putExtra("percent", course_percent);

            // start the activity
            startActivity(graphIntent);
        });
    }
    private void loadUIData(){
        // Initialize GUI components
        recyclerView = findViewById(R.id.recycler_view);
        graph_button = findViewById(R.id.graph_button);
        empty_view = findViewById(R.id.view_holder);
        no_data = findViewById(R.id.place_holder);

        // Create array lists to store values
        course_id = new ArrayList<>();
        course_title = new ArrayList<>();
        course_description = new ArrayList<>();
        course_percent = new ArrayList<>();

        Cursor cursor = myDB.readAllData();
        if (cursor != null) {
            if (cursor.getCount() == 0) { // if there is nothing in the cursor
                empty_view.setVisibility(View.VISIBLE);  //show the empty_view
                no_data.setVisibility(View.VISIBLE);

            } else { // if there is data, define the data at the current index
                int columnIndexId = cursor.getColumnIndex(DBHelper.COLUMN_ID);
                int columnIndexTitle = cursor.getColumnIndex(DBHelper.COLUMN_TITLE);
                int columnIndexDescription = cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION);
                int columnIndexPercent = cursor.getColumnIndex(DBHelper.COLUMN_PERCENT);
                if (cursor.moveToFirst()) { // while there is a next item
                    do {
                        // get the variables at the current position
                        String id = cursor.getString(columnIndexId);
                        String title = cursor.getString(columnIndexTitle);
                        String description = cursor.getString(columnIndexDescription);
                        String percent = cursor.getString(columnIndexPercent);

                        // add the them to the array lists
                        course_id.add(id);
                        course_title.add(title);
                        course_description.add(description);
                        course_percent.add(percent);
                    } while (cursor.moveToNext());
                }

                empty_view.setVisibility(View.GONE); // remove the empty_view
                no_data.setVisibility(View.GONE);
            }
            cursor.close();
        } else {
            Log.e("Tag", "Cursor is null");
        }

        myDB.close();
        // Add arrays to the recycler view
        customAdapter = new RecyclerView(MainActivity.this, this, course_id, course_title, course_description, course_percent);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    private void handleSMS(){
        // SMS Preferences
        enableSMSSwitch = findViewById(R.id.enable_sms);

        // save sms preferences in shared preferences as a boolean value, "sms_switch_state"
        SharedPreferences sharedPreferences = getSharedPreferences("my_settings", MODE_PRIVATE);
        smsSwitchState = sharedPreferences.getBoolean("sms_switch_state", false);
        enableSMSSwitch.setChecked(smsSwitchState);

        // set the listener to the sms button
        enableSMSSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();  // edit the user preference
            editor.putBoolean("sms_switch_state", isChecked);  // state is checked on
            editor.apply(); // save the preference

            // if sms is checked enabled
            if (isChecked) {
                // Check if the permission is granted in the manifest
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission from the user
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
                }
                SMSService sms = new SMSService(this);
                sms.setParameters(); // if it is on, set the parameters
                sms.startNotifications(); // start the notification thread
                Toast.makeText(this, "SMSService functionality enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMSService functionality disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Refresh the UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu); // add the dropdown menu
        return true;
    }

    // Menu option to remove all records or log out
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) { // add the dropdown menu: remove all classes
            confirmDeleteDialog();
            return true;
        }
        if (item.getItemId() == R.id.menu_logout) {  // add the dropdown menu: logout user
            confirmLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Confirm delete dialog
    private void confirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // dialog 1
        builder.setTitle("Remove All?");
        // dialog 2
        builder.setMessage("Are you sure you want to remove all courses?");  // confirm before deletion
        // if user selects yes
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            DBHelper myDB = new DBHelper(MainActivity.this);
            myDB.removeAllCourses(username);  // remove all courses from the user table
            myDB.close();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent); // Restart the activity

            finish();
        });
        // if user selects no
        builder.setNegativeButton("No", (dialogInterface, i) -> {}); // do nothing if the user cancels

        builder.create().show();
    }

    private void confirmLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // dialog 1
        builder.setTitle("Log Out?");
        // dialog 2
        builder.setMessage("Are you sure you want to log out?");
        // if yes dialog
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            // edit the shared preferences
            SharedPreferences preferences = getSharedPreferences("login_preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            // set "Remember Me" to false
            editor.putBoolean("rememberMe", false);
            editor.apply();
            // start the login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);  // restart
            startActivity(intent);
            finish();
        });
        // if no dialog
        builder.setNegativeButton("No", (dialogInterface, i) -> {}); // cancel if no
        builder.create().show();
    }
}
