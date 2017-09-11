package com.example.android.garde_manger;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.garde_manger.data.RecetteContract.RecetteEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class RecetteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Tag for the log messages */
    public static final String LOG_TAG = RecetteActivity.class.getSimpleName();

    /** Identifier for the recettes data loader */
    private static final int RECETTE_LOADER = 0;

    // The recyclerView
    private RecyclerView mRecyclerView;

    // The cursorAdapter
    private RecetteCursorAdapter mCursorAdapter;

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recette);

        setTitle(R.string.recipes);

        // Lookup the recyclerView in activity layout
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_recette);
        // Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Find the emptyStateTextView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view_recette);

        // Create adapter with null cursor and set the listener creating the intent
        mCursorAdapter = new RecetteCursorAdapter(this, null, new RecetteCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long id) {
                Intent intent = new Intent(RecetteActivity.this, RecetteEditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(RecetteEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecetteActivity.this, RecetteEditorActivity.class);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // Attach the adapter to the recyclerView to populate items
        mRecyclerView.setAdapter(mCursorAdapter);

        // Kick off the loader
        getLoaderManager().initLoader(RECETTE_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded plant data into the database. For debugging purposes only.
     */
    private void insertRecette() {
        List<String> checkedItems = new ArrayList<>(Collections.nCopies( 50, "0"));
        ContentValues values = new ContentValues();
        // Create a ContentValues object where column names are the keys,
        // and a dummy recette attributes are the values.
        values.put(RecetteEntry.COLUMN_NAME, getString(R.string.dummy_recipe_name));
        values.put(RecetteEntry.COLUMN_INGREDIENT_FRUITLEG, "Dummy fruitleg ingredient");
        values.put(RecetteEntry.COLUMN_INGREDIENT_FRIGO, "Dummy frigo ingredient");
        values.put(RecetteEntry.COLUMN_INGREDIENT_CONGELO, "Dummy congelo ingredient");
        values.put(RecetteEntry.COLUMN_INGREDIENT_PLACARDS, "Dummy placards ingredient");
        values.put(RecetteEntry.COLUMN_INGREDIENT_EPICES, "Dummy epices ingredient");
        values.put(RecetteEntry.COLUMN_CHECKED_FRUITLEG, checkedItems.toString().replaceAll("[\\[.\\]]", "").trim());
        values.put(RecetteEntry.COLUMN_CHECKED_FRIGO, checkedItems.toString().replaceAll("[\\[.\\]]", "").trim());
        values.put(RecetteEntry.COLUMN_CHECKED_CONGELO, checkedItems.toString().replaceAll("[\\[.\\]]", "").trim());
        values.put(RecetteEntry.COLUMN_CHECKED_PLACARDS, checkedItems.toString().replaceAll("[\\[.\\]]", "").trim());
        values.put(RecetteEntry.COLUMN_CHECKED_EPICES, checkedItems.toString().replaceAll("[\\[.\\]]", "").trim());

        // Insert the Dummy data with the ContentValues into the database
        Uri fruitLegUri = getContentResolver().insert(RecetteEntry.CONTENT_URI, values);
        Log.v(LOG_TAG, "Uri of new recette: " + fruitLegUri);
    }

    private void deleteAllRecette() {
        int rowsRecetteDeleted = getContentResolver().delete(RecetteEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsRecetteDeleted + " rows deleted from Recette database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_stock.xml.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertRecette();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllRecette();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                RecetteEntry._ID,
                RecetteEntry.COLUMN_NAME,
                RecetteEntry.COLUMN_INGREDIENT_FRUITLEG,
                RecetteEntry.COLUMN_INGREDIENT_FRIGO,
                RecetteEntry.COLUMN_INGREDIENT_CONGELO,
                RecetteEntry.COLUMN_INGREDIENT_PLACARDS,
                RecetteEntry.COLUMN_INGREDIENT_EPICES};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                RecetteEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                RecetteEntry.COLUMN_NAME); // Alpha sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()) {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
        }
        // / Update {@link HerbCursorAdapter} with this new cursor containing updated plant data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
