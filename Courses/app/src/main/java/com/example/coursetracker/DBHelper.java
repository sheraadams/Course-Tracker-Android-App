package com.example.coursetracker;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "courseTracker.db";
    private static final int DATABASE_VERSION = 8;
    public static final String COURSE_TABLE = "course_data"; // data table 1
    private static final String COMPLETED_TABLE = "completed_data"; // data table 2
    private static final String USER_TABLE = "user_data"; // data table 3
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "course_title";
    public static final String COLUMN_DESCRIPTION = "course_description";
    public static final String COLUMN_PERCENT = "column_percent";
    public static final String COLUMN_COMPLETED = "column_completed";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_GOAL = "goalCourse";
    private static final String COLUMN_REQUIREMENT = "requirement";
    public static final String COLUMN_PREREQUISITES = "course_prerequisites";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_USERNAME = "user_username";
    private static final String COLUMN_PASSWORD = "user_password";
    int tableLength = getCourseCount();  // get the number of courses in the course table
    // calculate the percent as 1/total courses * 100.
    // if there is no data, the default is 0
    String percent = (tableLength > 0) ? String.format("%.2f", (double) 1 / tableLength * 100) : "0.00";
    User user = User.getInstance();
    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (context != null) {
            this.context = context;
        } else {
            // Handle the case where context is null
            throw new IllegalArgumentException("Context cannot be null");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userTableQuery = "CREATE TABLE " + USER_TABLE +
                " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PHONE + " TEXT)";
        String courseTableQuery = "CREATE TABLE " + COURSE_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_PERCENT + " TEXT, " +
                COLUMN_GOAL + " TEXT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PREREQUISITES + " TEXT, " +
                COLUMN_PHONE + " TEXT)";

        String goalTableQuery = "CREATE TABLE " + COMPLETED_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_PERCENT + " TEXT, " +
                COLUMN_COMPLETED + " TEXT, " +
                COLUMN_GOAL + " TEXT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PREREQUISITES + " TEXT, " +
                COLUMN_REQUIREMENT + " TEXT, " +
                COLUMN_PHONE + " TEXT)";

        db.execSQL(userTableQuery);
        db.execSQL(courseTableQuery);
        db.execSQL(goalTableQuery);
        loadCourses(db);
    }

    public void loadCourses(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        // Course data
        String[][] courseData = {
                {"MAT241", "Modern Statistics with Software", "MAT240"},
                {"MAT243", "Applied Statistics for Science Technology Engineering and Mathematics"},
                {"MAT142", "Precalculus with Limits"},
                {"MAT225", "Calculus I: Single-Variable Calculus", "MAT140"},
                {"IT140", "Introduction to Scripting"},
                {"CS210", "Programming Languages", "IT145"},
                {"CS300", "Data Structures and Algorithms", "MAT230", "CS210"},
                {"DAD220", "Introduction to Structured Database Environments"},
                {"CS230", "Operating Platforms", "CS210"},
                {"CS250", "Software Development Lifecycle", "IT145"},
                {"CS255", "System Analysis and Design", "IT145"},
                {"CS305", "Software Security", "IT145"},
                {"CS320", "Software Testing Automation and Quality Assurance", "IT145"},
                {"CS330", "Computational Graphics and Visualization", "CS210"},
                {"CS340", "Client Server Development", "CS320"},
                {"CS360", "Mobile Architecture and Programming", "IT145"},
                {"CS370", "Current and Emerging Trends in Computer Science", "CS300"},
                {"CS465", "Full Stack Development I", "CS340"},
                {"IT145", "Foundation in Application Development", "IT140"},
                {"MAT350", "Applied Linear Algebra", "MAT225"},
                {"CS499", "Computer Science Capstone", "CS340"},
                {"MAT230", "Discrete Mathematics"},
                {"PHY150", "Introductory Physics: Mechanics", "MAT140"}
        };

        for (String[] course : courseData) {
            String title = course[0];
            String description = course[1];
            List<String> prerequisites = new ArrayList<>();
            for (int i = 2; i < course.length; i++) {
                prerequisites.add(course[i]);
            }

            cv.put(COLUMN_TITLE, title);
            cv.put(COLUMN_DESCRIPTION, description);
            cv.put(COLUMN_PREREQUISITES, prerequisites.toString());
            // Assuming percent is calculated elsewhere
            cv.put(COLUMN_PERCENT, percent);

            long result = db.insert(COURSE_TABLE, null, cv);
            if (result != -1) {
               // Toast.makeText(context, "New row inserted", Toast.LENGTH_SHORT).show();
            }
        }
        System.out.println("Course data loaded!");
    }

    public int getCourseCount() {
        SQLiteDatabase db = this.getReadableDatabase(); // initialize the database
        Cursor cursor = null;
        int numberOfCourses = 0;
        try {
            if (db != null) {  // if the database is not null
                String query = "SELECT COUNT(*) FROM " + COURSE_TABLE;
                cursor = db.rawQuery(query, null);
                if (cursor != null && cursor.moveToFirst()) {  // if there are results
                    numberOfCourses = cursor.getInt(0); // get the number of rows
                }
            }
        } catch (Exception e) { // catch exceptions
            e.printStackTrace();
        } finally {
            if (cursor != null) {  // close the cursor if not null
                cursor.close();
            }
        }
        return numberOfCourses;  // return the number of rows, which is the total number of courses
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COMPLETED_TABLE);
        onCreate(db);
    }

    // TODO: implement password hashing
    // method to add user and hashed password to the database
    void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();  // initialize the db
        ContentValues values = new ContentValues();
        if (username != null) {  // first check for null username
            values.put(COLUMN_USERNAME, username);  // add if not null
        }
        values.put(COLUMN_PASSWORD, password);  // add the password to the db
        // check for insertion
        long result = db.insert(USER_TABLE, null, values);
        if (result == -1) {  // failed to add
            //Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show();
        } else {  // successfully added
            //Toast.makeText(context, "User added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
    // TODO: implement password hashing
    // method to check user credentials against the database
    boolean checkUser(String username, String password) {
        if (username == null || password == null) {  // first check for nulls
            return false;
        }
        SQLiteDatabase db = this.getReadableDatabase(); // initialize the db
        // define the search query
        String[] columns = {COLUMN_USER_ID, COLUMN_PASSWORD};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {  // move to the first result row
            int passwordColumnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            if (passwordColumnIndex != -1) {  // if password column index is found
                String storedPassword = cursor.getString(passwordColumnIndex);  // get the stored password
                cursor.close();  // close the cursor
                return password.equals(storedPassword);  // return true if the password matches
            }
        }
        cursor.close();  // close the cursor
        return false;
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + COURSE_TABLE;   // select all the data in the course table
        SQLiteDatabase db = this.getReadableDatabase();   // initialize the db
        Cursor cursor = null;
        try {
            if (db != null) { // check that the db is not null
                cursor = db.rawQuery(query, null);
            }
        } catch (Exception e) {  // catch exceptions and log the error
            e.printStackTrace();
        }
        return cursor;
    }

    void removeCourse(String username, String title) {
        SQLiteDatabase db = this.getWritableDatabase();  // initialize the database
        // check for null or empty values
        if ((title == null || title.isEmpty()) || (username == null || username.isEmpty())) {
            //Toast.makeText(context, "Invalid title and or username", Toast.LENGTH_SHORT).show();
            return;
        }
        // define the search
        String whereClause = COLUMN_USERNAME + " = ? AND " + COLUMN_TITLE + " = ?";
        String[] whereArgs = {username, title};
        Cursor cursorQuery = db.query(COMPLETED_TABLE, null, whereClause, whereArgs, null, null, null);
        if (cursorQuery.getCount() > 0) {  // if the search is found
            ContentValues values = new ContentValues();
            values.put(COLUMN_COMPLETED, "-1");  // mark the row for deletion
            long result = db.delete(COMPLETED_TABLE, whereClause, whereArgs);  // delete
            if (result == -1) {  // if the delete result is failure, make a toast message
               // Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
            } else {  // successfully deleted
                //Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
            }
            // send the broadcast of the change and push that data to the intent
            pushBroadcast(username);
        } else {
            //Toast.makeText(context, "Course not found.", Toast.LENGTH_SHORT).show();
        }
        cursorQuery.close();   // close the cursor
    }

    public void addCourse(String course, String description, String username) {
        SQLiteDatabase db = this.getWritableDatabase();   // initialize the db
        ContentValues values = new ContentValues();
        if (!username.isEmpty()) {  // only add if not empty
            values.put(COLUMN_USERNAME, username);
        }
        if (!course.isEmpty()) {   // only add if not empty
            values.put(COLUMN_TITLE, course);
        }
        if (!description.isEmpty()) {   // only add if not empty
            values.put(COLUMN_DESCRIPTION, description);
        }
        values.put(COLUMN_PERCENT, percent);  // add the calculated values
        values.put(COLUMN_COMPLETED, "1");
        // define the search
        String whereClause = COLUMN_USERNAME + " = ? AND " + COLUMN_TITLE + " = ?";
        String[] whereArgs = {username, course};
        Cursor cursor = db.query(COMPLETED_TABLE, null, whereClause, whereArgs, null, null, null);
        if (cursor.getCount() == 0) {  // if the course does not exist in the completed table
            long result = db.insert(COMPLETED_TABLE, null, values); // insert it
            if (result != -1) {   // if successfully added, make a toast message
                //Toast.makeText(context, "New row inserted", Toast.LENGTH_SHORT).show();
            }
        } else {  // if duplicate, not added
           // Toast.makeText(context, "Course exists already.", Toast.LENGTH_SHORT).show();
        }
        // send the broadcast of the change and push that data to the intent
        pushBroadcast(username);
        cursor.close();  // close the cursor
        if (db.isOpen()){
            db.close();
        }
    }

    public void pushBroadcast(String username){
        // send the broadcast of the change and push that data to the intent
        Intent intent = new Intent("data_updated");
        intent.putExtra("username", username);
        context.sendBroadcast(intent);
    }

    String getPrerequisites(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        String prerequisites = null;
        if (db != null && title != null) {  // check for nulls first
            // search the course title provided
            String query = "SELECT " + COLUMN_PREREQUISITES + " FROM " + COURSE_TABLE +
                    " WHERE " + COLUMN_TITLE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{title});
            if (cursor != null && cursor.moveToFirst()) {  // check for null cursor and move to the first result if it exists
                int columnIndexPrerequisites = cursor.getColumnIndex(COLUMN_PREREQUISITES);  // define the column index
                if (columnIndexPrerequisites != -1) {  // if the column index is found
                    prerequisites = cursor.getString(columnIndexPrerequisites);  // get the value at the column index
                }
                cursor.close();   // close the cursor
            }
        }
        return prerequisites;
    }

    void removeAllCourses(String username) {
        SQLiteDatabase db = this.getWritableDatabase(); // initialize the db
        if (db != null) {  // first check for null db
            db.delete(COMPLETED_TABLE, COLUMN_USERNAME + "=?", new String[]{username});
        }
    }

    public int getTotalCompleted(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalCompleted = 0;
        if (db != null && username != null) {   // check for null values before proceeding
            // get the sum of the values in the completed column for the current user from the completed table
            String query = "SELECT SUM(" + COLUMN_COMPLETED + ") FROM " + COMPLETED_TABLE +
                    " WHERE " + COLUMN_USERNAME + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{username});
            if (cursor != null) {  // if cursor is not null
                if (cursor.moveToFirst()) {  // move to the first result if it exists
                    totalCompleted = cursor.getInt(0); // get the integer value of the first result
                }
                cursor.close();   // close the cursor
            }
        }
        return totalCompleted;
    }

    boolean checkIfIsInDatabase(String username, String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            // check if the provided title is in the user's table
            String query = "SELECT " + COLUMN_TITLE + " FROM " + COMPLETED_TABLE +
                    " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_TITLE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{username != null ? username : "", title != null ? title : ""});
            if (cursor != null && cursor.moveToFirst()) {  // if the cursor is not null, move to the first result if it exists
                return true; // record is in the database, return true
            }
            if (cursor != null) {  // if the cursor is not null
                cursor.close();   // close the cursor
            }
        }
        return false; // if no results, return false
    }

    // TODO
    public List<String> suggestCourses(String username){
        List<String> recommendedList = new ArrayList<>();
        return recommendedList;
    }

    // check if the database has been updated since the user was last notified
    public int checkForUpdates(String username) {
        if (DATABASE_VERSION > user.getDatabaseVersion(username))
        {
            return 1;
        }
        else{
            return 0;
        }
    }

    // get the current database version
    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}