# Android Course Tracker App
[![GitHub](https://img.shields.io/github/license/sheraadams/CS499-Course-Tracker)](https://img.shields.io/github/license/sheraadams/CS499-Course-Tracker) 
![Static Badge](https://img.shields.io/badge/MP%20Android%20-%20Chart%20-%20blue?link=https%3A%2F%2Fgithub.com%2FPhilJay%2FMPAndroidChart)
![Static Badge](https://img.shields.io/badge/Android%20-%20Studio%20-%20purple?link=https%3A%2F%2Fdeveloper.android.com%2Fstudio)

# About the Project

This Android Mobile course tracker application was inspired by my work for CS300 Data Structures and Algorithms. The project in CS300 was a C++ course management console application. I reverse-engineered my CS300 program from C++ to Java while adding and modifying much of the functionality in order to track my progress in my Computer Science program at SNHU visually through a convenient mobile app.

<p align="center">
  <img width="80%" height="80%" src="https://github.com/sheraadams/Course-Tracker-Android-App/assets/110789514/8c68ae77-91db-497c-bc11-391038f6160c"
</p>

## Data separation and the singleton design pattern

The user class uses the singleton design pattern to help maintain the separation of user data and to associate data with the user. 
Rather than passing user data as an intent (this is not a secure practice), we can create and return an instance of the current user and associate data with the proper user.

The update_button onClickListener responds to the button click and calls the updateDate function when pressed. A class is marked complete by adding the course to the user-specific COMPLETED_TABLE with the updateData function. The delete button functions in reverse to this process. 

```java
      update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("UpdateActivity", "Updating data: id=" + id + ", title=" + title +
                        ", description=" + description + ", percent=" + percent +
                        ", username=" + username + ", prerequisites=" + prerequisites);
                Log.d("UpdateActivity", "Rows with username " + username + ": " + myDB.getTotalCompleted(username));

                myDB.updateData(title, description, username);
                int totalCompleted = myDB.getTotalCompleted(username);
                Log.d("UpdateActivity", "Updating data: id=" + id + ", title=" + title +
                        ", totalCompleted=" + totalCompleted);

            }
        });
```
A class is marked complete by adding the course to the user-specific COMPLETED_TABLE with the updateData function.
The function checks for empty and null fields first.

```java
    public void updateData(String course, String description, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // only add if not empty
        if (!username.isEmpty()) {
            cv.put(COLUMN_USERNAME, username);
        }
        if (!course.isEmpty()) {
            cv.put(COLUMN_TITLE, course);
        }
        if (!description.isEmpty()) {
            cv.put(COLUMN_DESCRIPTION, description);
        }
        cv.put(COLUMN_PERCENT, percent);
        cv.put(COLUMN_COMPLETED, "1");
        String whereClause = COLUMN_USERNAME + " = ? AND " + COLUMN_TITLE + " = ?";
        String[] whereArgs = {username, course};
        Cursor cursor = db.query(COMPLETED_TABLE, null, whereClause, whereArgs, null, null, null);
        if (cursor.getCount() == 0) {  // if the course does not exist in the completed table
            long result = db.insert(COMPLETED_TABLE, null, cv);
            if (result != -1) {   // successfully added
                Toast.makeText(context, "New row inserted", Toast.LENGTH_SHORT).show();
            }
        } else {  // duplicate, not added
            Toast.makeText(context, "Class exists already.", Toast.LENGTH_SHORT).show();
        }
        notifyDataChanged(username);  // dynamically update the data
        cursor.close();
    }
```
## Marking Complete
When a course is marked complete, the course is displayed in a lavender. When a course is not complete, it is displayed as teal.

## Graph Representations
The Graph Activity shows a graph of the number of courses in the COMPLETED_TABLE / all courses.
We set some color preferences and label preferences and use the MPAndroidChart library to create a pie chart. We can plot the data with the help of the [MPAndroidChart library](https://github.com/PhilJay/MPAndroidChart/tree/master).

## Notifications
When the courses database and courses.txt are updated, the user can choose to receive SMS notifications. 

<p align="center">
  <img width="80%" height="80%" src="https://github.com/sheraadams/Course-Tracker-Android-App/assets/110789514/15201436-24d9-4b24-ad06-00fde1de37b2">
</p>

## Some of the highlights of this project include:

- GUI elements that follow UX design best practices
- SQLite database functionality 
- Data separation for users
- Convenience features like "remember me" autofill
- Password hashing and salting for security
- Graphs to allow the user to visually interact with the data through graphs
- SMS notification service that alerts the user when the course list is updated

<div style="text-align: center;">
  <p><strong>Proudly crafted with ❤️ by <a href="https://github.com/sheraadams" target="_blank">Shera Adams</a>.</strong></p>
</div>

