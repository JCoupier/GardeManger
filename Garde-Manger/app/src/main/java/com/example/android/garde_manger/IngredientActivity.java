package com.example.android.garde_manger;

import android.content.Intent;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.garde_manger.data.FoodContract.FruitsLegumesEntry;
import com.example.android.garde_manger.data.FoodContract.FrigoEntry;
import com.example.android.garde_manger.data.FoodContract.CongeloEntry;
import com.example.android.garde_manger.data.FoodContract.PlacardsEntry;
import com.example.android.garde_manger.data.FoodContract.EpicesEntry;

import java.util.List;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class IngredientActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the course list data loader */
    private static final int INGREDIENT_LOADER = 0;

    // The recyclerView
    private RecyclerView mRecyclerView;

    // The table we call in
    private int mTableType;

    public String mCheckedList;

    // The cursorAdapter
    private IngredientCursorAdapter mCursorAdapter;

    // Done button
    private Button mFinishButton;

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    // LinearLayout containing recyclerView and Done button
    private LinearLayout mWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        setTitle(R.string.choose_ingredient);

        // Lookup the recyclerView in activity layout
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_ingredient);
        // Set layout manager to position the items
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        mTableType = bundle.getInt("TableType");
        mCheckedList = bundle.getString("CheckedList");

        //Find the done Button
        mFinishButton = (Button) findViewById(R.id.ingredient_finish_button);
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSelection();
            }
        });

        // Find the emptyStateTextView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view_ingredient);

        // Find the LinearLayout
        mWrapper = (LinearLayout) findViewById(R.id.wrapper);

        // Create adapter with null cursor and set the listener creating the intent passing the mCheckedList
        mCursorAdapter = new IngredientCursorAdapter(this, null, mCheckedList);

        // Attach the adapter to the recyclerView to populate items
        mRecyclerView.setAdapter(mCursorAdapter);

        // Kick off the loader
        getLoaderManager().initLoader(INGREDIENT_LOADER, null, this);
    }

    // Method called when finish button pressed. Retrieve components of the adapter and passing it back to the activity with an intent
    private void finishSelection(){
        // Get the different components from the CursorAdapter
        List<Integer> itemsListed = mCursorAdapter.listedItems;
        List<String> itemsChecked = mCursorAdapter.checkedItems;
        List<String> nameItems = mCursorAdapter.itemsName;

        Intent intent = new Intent();

        intent.putExtra("itemsListedString", itemsListed.toString().replaceAll("[\\[.\\].\\s+]", ""));

        intent.putExtra("itemsCheckedString", itemsChecked.toString().replaceAll("[\\[.\\].\\s+]", ""));

        StringBuilder nameString = new StringBuilder();
        for (int j = 0; j < nameItems.size(); j++){
            if (!nameItems.get(j).isEmpty()){
                String itemName = nameItems.get(j);
                nameString.append(itemName);
                nameString.append(",");
            }
        }
        if (!TextUtils.isEmpty(nameString)) {
            nameString.deleteCharAt(nameString.length() - 1);
            intent.putExtra("nameString", nameString.toString().replace(" ", "_").trim());
        } else {
            intent.putExtra("nameString", "No ingredient selected");
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    // React to the user tapping the back/up icon in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (mTableType){
            case 0:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the FruitsLegumes table
                String[] projectionFruitsLegumes = {
                        FruitsLegumesEntry._ID,
                        FruitsLegumesEntry.COLUMN_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        FruitsLegumesEntry.CONTENT_URI,       // Query the content URI for the current pet
                        projectionFruitsLegumes,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        FruitsLegumesEntry.COLUMN_NAME);     // Alpha sort order
            case 1:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the Frigo table
                String[] projectionFrigo = {
                        FrigoEntry._ID,
                        FrigoEntry.COLUMN_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        FrigoEntry.CONTENT_URI,       // Query the content URI for the current pet
                        projectionFrigo,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        FrigoEntry.COLUMN_NAME);     // Alpha sort order
            case 2:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the Congelo table
                String[] projectionCongelo = {
                        CongeloEntry._ID,
                        CongeloEntry.COLUMN_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        CongeloEntry.CONTENT_URI,       // Query the content URI for the current pet
                        projectionCongelo,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        CongeloEntry.COLUMN_NAME);     // Alpha sort order
            case 3:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the Placards table
                String[] projectionPlacards = {
                        PlacardsEntry._ID,
                        PlacardsEntry.COLUMN_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        PlacardsEntry.CONTENT_URI,       // Query the content URI for the current pet
                        projectionPlacards,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        PlacardsEntry.COLUMN_NAME);     // Alpha sort order
            case 4:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the Epices table
                String[] projectionEpices = {
                        EpicesEntry._ID,
                        EpicesEntry.COLUMN_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        EpicesEntry.CONTENT_URI,       // Query the content URI for the current pet
                        projectionEpices,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        EpicesEntry.COLUMN_NAME);     // Alpha sort order
            default:
                return null;
        }
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
        // / Update {@link IngredientCursorAdapter} with this new cursor containing updated ingredient data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
