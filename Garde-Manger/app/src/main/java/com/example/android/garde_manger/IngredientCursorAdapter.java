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

import com.example.android.garde_manger.data.RecetteContract.RecetteEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class IngredientCursorAdapter extends CursorRecyclerAdapter<IngredientCursorAdapter.ViewHolder> {

    // Create the Lists to pass it back to the activity. checkedItems List is already into to db so it is just called back
    public List<Integer> listedItems = new ArrayList<>(Collections.nCopies(50, -1));
    public List<String> checkedItems;
    public List<String> itemsName = new ArrayList<>(Collections.nCopies(50, ""));

    public IngredientCursorAdapter(Context context, Cursor cursor, String checkedList) {
        super(context,cursor);
        this.checkedItems = Arrays.asList(checkedList.split(","));
    }

    // The ViewHolder which caches the ImageView, the sell Button and the three TextViews
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ingredientNameTextView;
        public CheckBox ingredientCheckBox;

        public ViewHolder(View itemView){
            super(itemView);

            // Find the different component of the viewHolder
            ingredientNameTextView = (TextView) itemView.findViewById(R.id.course_name);
            ingredientCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_course_item);
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

        final int id = cursor.getInt(cursor.getColumnIndex(RecetteEntry._ID));

        // Get the index of each column
        int nameColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_NAME);

        // Get the values of each column
        final String ingredientName = cursor.getString(nameColumnIndex);

        // Populate the item with the values from the database
        holder.ingredientNameTextView.setText(ingredientName);

        listedItems.set(position, id);

        // Handle the case checked unchecked of the checkboxes with the checkedItems List from the db
        if (checkedItems.get(position).equals("1")){
            holder.ingredientCheckBox.setChecked(true);
            itemsName.set(position, ingredientName);
        } else {
            holder.ingredientCheckBox.setChecked(false);
            itemsName.set(position, "");
        }
        // Checked listener on the checkBoxes to update the checkedItems List accordingly
        holder.ingredientCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    checkedItems.set(position, "1");
                    itemsName.set(position, ingredientName);
                    holder.ingredientCheckBox.setChecked(isChecked);
                }
                else
                {
                    checkedItems.set(position, "0");
                    itemsName.set(position, "");
                    holder.ingredientCheckBox.setChecked(isChecked);
                }

            }
        });
    }
}

