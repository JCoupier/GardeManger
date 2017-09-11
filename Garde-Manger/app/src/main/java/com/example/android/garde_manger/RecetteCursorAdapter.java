package com.example.android.garde_manger;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.garde_manger.data.RecetteContract.RecetteEntry;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class RecetteCursorAdapter extends CursorRecyclerAdapter<RecetteCursorAdapter.ViewHolder> {

    // The Listener
    private OnItemClickListener mListener;

    public RecetteCursorAdapter(Context context, Cursor cursor, OnItemClickListener listener) {
        super(context,cursor);
        this.mListener = listener;
    }

    interface OnItemClickListener {
        void onItemClick(long id);
    }

    // The ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView recetteNameTextView;
        public TextView recetteFruitLegTextView;
        public TextView recetteFrigoTextView;
        public TextView recetteCongeloTextView;
        public TextView recettePlacardsTextView;
        public TextView recetteEpicesTextView;

        public ViewHolder(View itemView){
            super(itemView);

            // Find the different component of the viewHolder
            recetteNameTextView = (TextView) itemView.findViewById(R.id.recette_name);
            recetteFruitLegTextView = (TextView) itemView.findViewById(R.id.recette_fruitleg_text_view);
            recetteFrigoTextView = (TextView) itemView.findViewById(R.id.recette_frigo_text_view);
            recetteCongeloTextView = (TextView) itemView.findViewById(R.id.recette_congelo_text_view);
            recettePlacardsTextView = (TextView) itemView.findViewById(R.id.recette_placards_text_view);
            recetteEpicesTextView = (TextView) itemView.findViewById(R.id.recette_epices_text_view);
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recette_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

        final int id = cursor.getInt(cursor.getColumnIndex(RecetteEntry._ID));

        // Get the index of each column
        int nameColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_NAME);
        int fruitlegIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_FRUITLEG);
        int frigoIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_FRIGO);
        int congeloIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_CONGELO);
        int placardsIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_PLACARDS);
        int epicesIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_EPICES);

        // Get the values of each column
        String recetteName = cursor.getString(nameColumnIndex);
        String fruitlegIngredient = cursor.getString(fruitlegIngredientColumnIndex);
        String frigoIngredient = cursor.getString(frigoIngredientColumnIndex);
        String congeloIngredient = cursor.getString(congeloIngredientColumnIndex);
        String placardsIngredient = cursor.getString(placardsIngredientColumnIndex);
        String epicesIngredient = cursor.getString(epicesIngredientColumnIndex);

        // Populate the item with the values from the database
        holder.recetteNameTextView.setText(recetteName);
        holder.recetteFruitLegTextView.setText(fruitlegIngredient);
        holder.recetteFrigoTextView.setText(frigoIngredient);
        holder.recetteCongeloTextView.setText(congeloIngredient);
        holder.recettePlacardsTextView.setText(placardsIngredient);
        holder.recetteEpicesTextView.setText(epicesIngredient);

        // Bind a listener to the item
        holder.listenerBinder(id, mListener);
    }
}

