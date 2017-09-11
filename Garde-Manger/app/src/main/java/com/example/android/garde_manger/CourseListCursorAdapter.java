package com.example.android.garde_manger;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.garde_manger.data.CourseContract.CourseListEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class CourseListCursorAdapter extends CursorRecyclerAdapter<CourseListCursorAdapter.ViewHolder> {

    // Create two List to export in the activity
    public List<Integer> checkedItems= new ArrayList<>(Collections.nCopies( 50, -1));
    public List<String> itemsFoodUri= new ArrayList<>(Collections.nCopies(50, ""));

    public CourseListCursorAdapter(Context context, Cursor cursor) {
        super(context,cursor);
    }

    // The ViewHolder which caches the ImageView, the sell Button and the three TextViews
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView courseNameTextView;
        public CheckBox courseCheckBox;
        public TextView courseQuantityTextView;

        public ViewHolder(View itemView){
            super(itemView);

            // Find the different component of the viewHolder
            courseNameTextView = (TextView) itemView.findViewById(R.id.course_name);
            courseCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_course_item);
            courseQuantityTextView = (TextView) itemView.findViewById(R.id.course_quantity);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

        final int position = holder.getAdapterPosition();

        final int id = cursor.getInt(cursor.getColumnIndex(CourseListEntry._ID));

        // Get the index of each column
        int nameColumnIndex = cursor.getColumnIndex(CourseListEntry.COLUMN_NAME);
        int foodUriColumnIndex = cursor.getColumnIndex(CourseListEntry.COLUMN_FOOD_URI);
        int quantityColumnIndex = cursor.getColumnIndex(CourseListEntry.COLUMN_FOOD_QUANTITY);

        // Get the values of each column
        String courseName = cursor.getString(nameColumnIndex);
        String foodUri = cursor.getString(foodUriColumnIndex);
        String courseQuantity = cursor.getString(quantityColumnIndex);

        // Populate the item with the values from the database
        holder.courseNameTextView.setText(courseName);
        holder.courseQuantityTextView.setText(courseQuantity);

        // Put hte foodUri into the corresponding List
        itemsFoodUri.set(position, foodUri);

        holder.courseCheckBox.setChecked(false);
        // Set a listener on checkboxes and put values into the corresponding List
        holder.courseCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    checkedItems.set(position, id);
                    holder.courseCheckBox.setChecked(isChecked);
                }
                else
                {
                    checkedItems.set(position, -1);
                    holder.courseCheckBox.setChecked(isChecked);
                }

            }
        });
    }
}

