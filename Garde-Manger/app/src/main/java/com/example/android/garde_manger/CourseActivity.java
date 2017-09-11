package com.example.android.garde_manger;

import android.content.ContentValues;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.garde_manger.data.FoodContract.FruitsLegumesEntry;
import com.example.android.garde_manger.data.FoodContract.FrigoEntry;
import com.example.android.garde_manger.data.FoodContract.CongeloEntry;
import com.example.android.garde_manger.data.FoodContract.PlacardsEntry;
import com.example.android.garde_manger.data.FoodContract.EpicesEntry;

import com.example.android.garde_manger.data.CourseContract.CourseListEntry;

import java.util.List;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class CourseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the course list data loader */
    private static final int COURSE_LOADER = 0;

    // The recyclerView
    private RecyclerView mRecyclerView;

    // The cursorAdapter
    private CourseListCursorAdapter mCursorAdapter;

    // Done button
    private Button mDoneButton;

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    // LinearLayout containing recyclerView and Done button
    private LinearLayout mWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        setTitle(R.string.list_shopping);

        // Lookup the recyclerView in activity layout
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_course_list);
        // Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Find the done Button
        mDoneButton = (Button) findViewById(R.id.course_done_button);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseDone();
            }
        });

        // Find the emptyStateTextView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view_course_list);

        // Find the LinearLayout
        mWrapper = (LinearLayout) findViewById(R.id.wrapper);

        // Create adapter with null cursor and set the listener creating the intent
        mCursorAdapter = new CourseListCursorAdapter(this, null);

        // Attach the adapter to the recyclerView to populate items
        mRecyclerView.setAdapter(mCursorAdapter);

        // Kick off the loader
        getLoaderManager().initLoader(COURSE_LOADER, null, this);
    }

    // Method called on Done Button pressed to update the Course DB dans each Food DB
    private void courseDone(){
        // Retrieving data from the cursorAdapter
        List<Integer> itemsChecked = mCursorAdapter.checkedItems;
        List<String> foodUriItems = mCursorAdapter.itemsFoodUri;
        // Loop with itemsChecked which is a list of strings (-1 is default values, otherwise it contain the id of the food)
        for (int i = 0; i < itemsChecked.size(); i++){
            if (!itemsChecked.get(i).equals(-1)){
                int itemId = itemsChecked.get(i);
                String selection = CourseListEntry._ID + "=?";
                String[] selectionArgs = new String[] { String.valueOf(itemId) };
                // Delete the food checked from the shopping list (course list)
                getContentResolver().delete(CourseListEntry.CONTENT_URI, selection, selectionArgs);

                // Parse the uri of the food and get the different part of the uri
                Uri foodUri = Uri.parse(foodUriItems.get(i));
                String[] segments = foodUri.getPath().split("/");
                // Get the table name
                String tableName = segments[segments.length-2];
                // Get the id of the food
                String idString = segments[segments.length-1];
                int foodId = Integer.parseInt(idString);
                ContentValues values = new ContentValues();
                switch (tableName){
                    case "fruitlegumes":
                        values.put(FruitsLegumesEntry.COLUMN_IN_COURSE_LIST, FruitsLegumesEntry.NOT_IN_LIST);
                        String selectionFruitsLegumes = FruitsLegumesEntry._ID + "=?";
                        String[] selectionArgsFruitsLegumes = new String[] { String.valueOf(foodId) };
                        // Update the specific food data to notify the taht the food is not anymore in the shopping list
                        getContentResolver().update(FruitsLegumesEntry.CONTENT_URI, values, selectionFruitsLegumes, selectionArgsFruitsLegumes);
                        break;

                    case "frigo":
                        values.put(FrigoEntry.COLUMN_IN_COURSE_LIST, FrigoEntry.NOT_IN_LIST);
                        String selectionFrigo = FrigoEntry._ID + "=?";
                        String[] selectionArgsFrigo = new String[] { String.valueOf(foodId) };
                        getContentResolver().update(FrigoEntry.CONTENT_URI, values, selectionFrigo, selectionArgsFrigo);
                        break;

                    case "congelo":
                        values.put(CongeloEntry.COLUMN_IN_COURSE_LIST, CongeloEntry.NOT_IN_LIST);
                        String selectionCongelo = CongeloEntry._ID + "=?";
                        String[] selectionArgsCongelo = new String[] { String.valueOf(foodId) };
                        getContentResolver().update(CongeloEntry.CONTENT_URI, values, selectionCongelo, selectionArgsCongelo);
                        break;

                    case "placards":
                        values.put(PlacardsEntry.COLUMN_IN_COURSE_LIST, PlacardsEntry.NOT_IN_LIST);
                        String selectionPlacards = PlacardsEntry._ID + "=?";
                        String[] selectionArgsPlacards = new String[] { String.valueOf(foodId) };
                        getContentResolver().update(PlacardsEntry.CONTENT_URI, values, selectionPlacards, selectionArgsPlacards);
                        break;

                    case "epices":
                        values.put(EpicesEntry.COLUMN_IN_COURSE_LIST, EpicesEntry.NOT_IN_LIST);
                        String selectionEpices = EpicesEntry._ID + "=?";
                        String[] selectionArgsEpices = new String[] { String.valueOf(foodId) };
                        getContentResolver().update(EpicesEntry.CONTENT_URI, values, selectionEpices, selectionArgsEpices);
                        break;
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                CourseListEntry._ID,
                CourseListEntry.COLUMN_NAME,
                CourseListEntry.COLUMN_FOOD_URI,
                CourseListEntry.COLUMN_FOOD_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                CourseListEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                CourseListEntry.COLUMN_NAME); // Alpha sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()) {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mWrapper.setVisibility(View.GONE);
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
            mWrapper.setVisibility(View.VISIBLE);
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
