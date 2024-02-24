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

# Data Structures work in this project

## Hash Table Considerations
Hash Tables are good for fast searches, but data degradation could become an unavoidable issue with large datasets.  
Hash table search speed is very good with small data sets, but they do not perform as well as the data size grows.
According to the IONOS team, Hash tables “can degrade if they go through a large number of collisions” (2023). 

## Vector Considerations
Vectors are expensive in terms of memory allocation, and they require a large amount of storage to be allocated. 
If the vector is to be sorted, the vector would likely need to be sorted after each insertion or deletion. 
Over many courses, sorting after each insertion or deletion would prove to be unnecessarily expensive.

## Binary Search Tree Considerations
Vectors are expensive in terms of memory allocation, and they require a large amount of storage to be allocated. 
If the vector is to be sorted, the vector would likely need to be sorted after each insertion or deletion. 
Over many courses, sorting after each insertion or deletion would prove to be unnecessarily expensive.

Hash Table, Vector, and Binary Search Tree data types all yield similar worst-case scenario runtime results for the specific function that was compared. 
The difference between each of these data structures lies primarily in the Insert() function cost.
For the vector data structure, the cost of insertion is 0(1), for the hash table data structure it is 0(N), and for the binary search tree structure, it is 0(log(N)). 

**BST Pros**
- A binary search tree provides a more efficient loading mechanism compared to an array. 
- A binary search tree can offer faster insertion and retrieval times during the loading process.
- A binary search tree also provides dynamic sorting during loading.
- A binary search tree is extremely efficient and yields fast search results.
- Binary search trees are good for applications that prioritize fast search efficiency. 
- Binary search trees are good when growth and modification in the future are expected.
- Binary Search Trees have a worst-case space complexity O(N) where N is the number of elements in the tree. 
- Binary search trees minimize our overall application overhead when compared to vectors and array types.

**BST Cons**
- The binary search tree data type requires storage for the tree itself.

## Justifications for using BST

A binary search tree is a strong choice for many applications that prioritize fast search results and efficiency. Because insertions and deletions are expected in this application and large data sets can be expected, a Binary Search Tree is an appropriate structure for this use. The binary search tree data type requires storage for the tree itself, and this could be a potential drawback to this structure if it is known that the data is expected to be small and is not expected to grow. 


## The BST

Each course is stored as a bid with attributes coursed, title, and prerequisites. 

```java
class Bid {
    public String title;
    public String description;
    public List<String> prerequisites;

    public Bid(String title, String description, List<String> prerequisites) {
        this.title = title;
        this.description = description;
        this.prerequisites = prerequisites;
    }
    public String toString() {
        return String.format("Course ID: %s, Course Name: %s, Prerequisites: %s",
                this.title, this.description, String.join(", ", this.prerequisites));
    }
}
```

Our courses.txt file is loaded and values are separated by commas. 
The first value is the course title (string).
The second is the description (string)
The third is prerequisites  (an array list of strings).
Prerequisites may be empty, one, or many strings.

```java
public class BinarySearchTree {
    public Node root;
    public BinarySearchTree() {
        this.root = null;
    }
    public void insert(Bid bid) {
        Node node = new Node(bid);
        if (this.root == null) {
            this.root = node;
        } else {
            Node current = this.root;
            while (current != null) {
                if (bid.title.compareTo(current.bid.title) < 0) {
                    if (current.left == null) {
                        current.left = node;
                        break;
                    } else {
                        current = current.left;
                    }
                } else {
                    if (current.right == null) {
                        current.right = node;
                        break;
                    } else {
                        current = current.right;
                    }
                }
            }
        }
    }
```

## Some of the highlights of this project include:

- GUI elements that follow UX design best practices
- SQLite database functionality that interacts with the courses.txt dynamically 
- Data separation for users
- Convenience features like "remember me" autofill
- Password hashing and salting for security
- Graphs to allow the user to visually interact with the data through graphs
- SMS notification service that alerts the user when the course list is updated

<div style="text-align: center;">
  <p><strong>Proudly crafted with ❤️ by <a href="https://github.com/sheraadams" target="_blank">Shera Adams</a>.</strong></p>
</div>

