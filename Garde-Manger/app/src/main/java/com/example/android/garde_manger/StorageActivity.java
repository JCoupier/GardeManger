package com.example.android.garde_manger;

import android.content.ContentValues;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.garde_manger.data.FoodContract.FruitsLegumesEntry;
import com.example.android.garde_manger.data.FoodContract.FrigoEntry;
import com.example.android.garde_manger.data.FoodContract.CongeloEntry;
import com.example.android.garde_manger.data.FoodContract.PlacardsEntry;
import com.example.android.garde_manger.data.FoodContract.EpicesEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class StorageActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = StorageActivity.class.getSimpleName();

    // The viewpager
    private ViewPager mViewPager;

    // Current fragment displayed
    private int mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        // Find the view pager that will allow the user to swipe between fragments
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        FoodFragmentPagerAdapter adapter = new FoodFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        mViewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * Helper method to insert hardcoded food data into the database. For debugging purposes only.
     */
    private void insertFood() {
        // Get current Fragment
        mCurrentFragment = mViewPager.getCurrentItem();
        // create a List of String of empty recipes name to put into the dummy insertions
        List<String> recetteNameList = new ArrayList<>(Collections.nCopies(200, ""));
        ContentValues values = new ContentValues();
        switch (mCurrentFragment){
            case 0:
                // Create a ContentValues object where column names are the keys,
                // and a dummy food attributes are the values.
                values.put(FruitsLegumesEntry.COLUMN_NAME, getString(R.string.dummy_food_name));
                values.put(FruitsLegumesEntry.COLUMN_QUANTITY, getString(R.string.dummy_food_quantity));
                values.put(FruitsLegumesEntry.COLUMN_SCALETYPE, 1);
                values.put(FruitsLegumesEntry.COLUMN_EXPIRY_DATE, getString(R.string.dummy_food_expiry_date));
                values.put(FruitsLegumesEntry.COLUMN_IN_COURSE_LIST, 0);
                values.put(FruitsLegumesEntry.COLUMN_RECETTE_NAME, recetteNameList.toString().replaceAll("[\\[.\\].\\s+]", ""));

                // Insert the Dummy data with the ContentValues into the database
                Uri fruitLegUri = getContentResolver().insert(FruitsLegumesEntry.CONTENT_URI, values);
                Log.v(LOG_TAG, "Uri of new product: " + fruitLegUri);
                break;
            case 1:
                // Create a ContentValues object where column names are the keys,
                // and a dummy food attributes are the values.
                values.put(FrigoEntry.COLUMN_NAME, getString(R.string.dummy_food_name));
                values.put(FrigoEntry.COLUMN_QUANTITY, getString(R.string.dummy_food_quantity));
                values.put(FrigoEntry.COLUMN_SCALETYPE, 1);
                values.put(FrigoEntry.COLUMN_EXPIRY_DATE, getString(R.string.dummy_food_expiry_date));
                values.put(FrigoEntry.COLUMN_IN_COURSE_LIST, 0);
                values.put(FrigoEntry.COLUMN_RECETTE_NAME, recetteNameList.toString().replaceAll("[\\[.\\].\\s+]", ""));

                // Insert the Dummy data with the ContentValues into the database
                Uri frigoUri = getContentResolver().insert(FrigoEntry.CONTENT_URI, values);
                Log.v(LOG_TAG, "Uri of new product: " + frigoUri);
                break;
            case 2:
                // Create a ContentValues object where column names are the keys,
                // and a dummy food attributes are the values.
                values.put(CongeloEntry.COLUMN_NAME, getString(R.string.dummy_food_name));
                values.put(CongeloEntry.COLUMN_QUANTITY, getString(R.string.dummy_food_quantity));
                values.put(CongeloEntry.COLUMN_SCALETYPE, 1);
                values.put(CongeloEntry.COLUMN_EXPIRY_DATE, getString(R.string.dummy_food_expiry_date));
                values.put(CongeloEntry.COLUMN_IN_COURSE_LIST, 0);
                values.put(CongeloEntry.COLUMN_RECETTE_NAME, recetteNameList.toString().replaceAll("[\\[.\\].\\s+]", ""));

                // Insert the Dummy data with the ContentValues into the database
                Uri congeloUri = getContentResolver().insert(CongeloEntry.CONTENT_URI, values);
                Log.v(LOG_TAG, "Uri of new product: " + congeloUri);
                break;
            case 3:
                // Create a ContentValues object where column names are the keys,
                // and a dummy food attributes are the values.
                values.put(PlacardsEntry.COLUMN_NAME, getString(R.string.dummy_food_name));
                values.put(PlacardsEntry.COLUMN_QUANTITY, getString(R.string.dummy_food_quantity));
                values.put(PlacardsEntry.COLUMN_SCALETYPE, 1);
                values.put(PlacardsEntry.COLUMN_EXPIRY_DATE, getString(R.string.dummy_food_expiry_date));
                values.put(PlacardsEntry.COLUMN_IN_COURSE_LIST, 0);
                values.put(PlacardsEntry.COLUMN_RECETTE_NAME, recetteNameList.toString().replaceAll("[\\[.\\].\\s+]", ""));

                // Insert the Dummy data with the ContentValues into the database
                Uri placardsUri = getContentResolver().insert(PlacardsEntry.CONTENT_URI, values);
                Log.v(LOG_TAG, "Uri of new product: " + placardsUri);
                break;
            case 4:
                // Create a ContentValues object where column names are the keys,
                // and a dummy food attributes are the values.
                values.put(EpicesEntry.COLUMN_NAME, getString(R.string.dummy_food_name));
                values.put(EpicesEntry.COLUMN_QUANTITY, getString(R.string.dummy_food_quantity));
                values.put(EpicesEntry.COLUMN_SCALETYPE, 1);
                values.put(EpicesEntry.COLUMN_EXPIRY_DATE, getString(R.string.dummy_food_expiry_date));
                values.put(EpicesEntry.COLUMN_IN_COURSE_LIST, 0);
                values.put(EpicesEntry.COLUMN_RECETTE_NAME, recetteNameList.toString().replaceAll("[\\[.\\].\\s+]", ""));

                // Insert the Dummy data with the ContentValues into the database
                Uri epicesUri = getContentResolver().insert(EpicesEntry.CONTENT_URI, values);
                Log.v(LOG_TAG, "Uri of new product: " + epicesUri);
                break;
        }
    }

    private void deleteAllFood() {
        // Get current Fragment
        mCurrentFragment = mViewPager.getCurrentItem();
        switch (mCurrentFragment) {
            case 0:
                int rowsFruitLegDeleted = getContentResolver().delete(FruitsLegumesEntry.CONTENT_URI, null, null);
                Log.v(LOG_TAG, rowsFruitLegDeleted + " rows deleted from Fruits and Legumes database");
                break;
            case 1:
                int rowsFrigoDeleted = getContentResolver().delete(FrigoEntry.CONTENT_URI, null, null);
                Log.v(LOG_TAG, rowsFrigoDeleted + " rows deleted from Frigo database");
                break;
            case 2:
                int rowsCongeloDeleted = getContentResolver().delete(CongeloEntry.CONTENT_URI, null, null);
                Log.v(LOG_TAG, rowsCongeloDeleted + " rows deleted from Congelo database");
                break;
            case 3:
                int rowsPlacardsDeleted = getContentResolver().delete(PlacardsEntry.CONTENT_URI, null, null);
                Log.v(LOG_TAG, rowsPlacardsDeleted + " rows deleted from Placards database");
                break;
            case 4:
                int rowsEpicesDeleted = getContentResolver().delete(EpicesEntry.CONTENT_URI, null, null);
                Log.v(LOG_TAG, rowsEpicesDeleted + " rows deleted from Epices database");
                break;
        }
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
                insertFood();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllFood();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
