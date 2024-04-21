package com.example.coursetracker;
//https://www.geeksforgeeks.org/android-recyclerview/
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class RecyclerView extends androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.MyViewHolder> {

    private Context context;
    private Activity activity;
    private User user = User.getInstance();
    private String username = user.getUsername();
    private ArrayList<String> course_id;
    private ArrayList<String> course_title;
    private ArrayList<String> course_description;
    private ArrayList<String> course_percent;
    private OnDeleteClickListener onDeleteClickListener; // Interface for delete

    RecyclerView(Activity activity, Context context, ArrayList<String> course_id,
                 ArrayList<String> course_title, ArrayList<String> course_description,
                 ArrayList<String> course_percent) {

        this.activity = activity;
        this.context = context;
        this.course_id = course_id;
        this.course_title = course_title;
        this.course_description = course_description;
        this.course_percent = course_percent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_recycler, parent, false);
        return new MyViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.M) // minimum api level is 23
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
            DBHelper myDB = new DBHelper(context);
            String coursePercent = myDB.percent;
            // get the data from the array lists
            String title = String.valueOf(course_title.get(position));
            String description = String.valueOf(course_description.get(position));
            String percent = String.valueOf(coursePercent);

            // if the course is completed, it should be lavender
            boolean isInDatabase = myDB.checkIfIsInDatabase(username, title);
            if (isInDatabase) {
                holder.course_title_text.setTextColor(ContextCompat.getColor(context, R.color.purple_200));
            }
            else {  // if it is not completed, it is teal
                holder.course_title_text.setTextColor(ContextCompat.getColor(context, R.color.teal_200));
            }
            myDB.close();
            // Set the course_id based on the position
            holder.course_id_text.setText(String.valueOf(position + 1));
            holder.course_title_text.setText(title);
            holder.course_description_text.setText(description);
            holder.course_percent_text.setText(percent);

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, EditActivity.class);

                        // send data through intent to the edit activity
                        intent.putExtra("id", String.valueOf(course_id.get(adapterPosition)));
                        intent.putExtra("course", course_title.get(adapterPosition));
                        intent.putExtra("description", course_description.get(adapterPosition));
                        intent.putExtra("percent", "4.35");

                        // start the activity
                        activity.startActivityForResult(intent, 1);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return course_id.size();
    }

    class MyViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView course_id_text;
        TextView course_title_text;
        TextView course_description_text;
        TextView course_percent_text;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // define the ui data variables
            course_id_text = itemView.findViewById(R.id.course_id_text);
            course_title_text = itemView.findViewById(R.id.course_title_text);
            course_description_text = itemView.findViewById(R.id.course_description_text);
            course_percent_text = itemView.findViewById(R.id.course_percent_text);

            // define the layout
            mainLayout = itemView.findViewById(R.id.home_layout);
        }
    }

    // Setter for OnDeleteClickListener
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    // Interface for delete action
    public interface OnDeleteClickListener {
        void onDeleteClick(int position, String courseId);
    }
}
