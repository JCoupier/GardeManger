package com.example.android.garde_manger;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.garde_manger.data.FoodContract.FruitsLegumesEntry;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class FoodCursorAdapter extends CursorRecyclerAdapter<FoodCursorAdapter.ViewHolder> {

    // The Listener
    private OnItemClickListener mListener;

    public FoodCursorAdapter(Context context, Cursor cursor, OnItemClickListener listener) {
        super(context,cursor);
        this.mListener = listener;
    }

    interface OnItemClickListener {
        void onItemClick(long id);
    }

    // The ViewHolder which caches the ImageView, the sell Button and the three TextViews
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView foodNameTextView;
        public TextView expiryDateTextView;
        public TextView quantityTextView;

        public ViewHolder(View itemView){
            super(itemView);

            // Find the different component of the viewHolder
            foodNameTextView = (TextView) itemView.findViewById(R.id.food_name);
            expiryDateTextView = (TextView) itemView.findViewById(R.id.expiry_date);
            quantityTextView = (TextView) itemView.findViewById(R.id.quantity);
        }
        // Bind a listener to an item (Food product)
        private void listenerBinder(final long id, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(id);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

        final int id = cursor.getInt(cursor.getColumnIndex(FruitsLegumesEntry._ID));

        // Get the index of each column
        int nameColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_NAME);
        int expiryDateColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_EXPIRY_DATE);
        int quantityColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_QUANTITY);

        // Get the values of each column
        String foodName = cursor.getString(nameColumnIndex);
        String expiryDate = cursor.getString(expiryDateColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        // Populate the item with the values from the database
        holder.foodNameTextView.setText(foodName);
        holder.expiryDateTextView.setText(expiryDate);
        holder.quantityTextView.setText(quantity);

        // Bind a listener to the item
        holder.listenerBinder(id, mListener);
    }
}

