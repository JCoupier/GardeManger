package com.example.android.garde_manger;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.garde_manger.data.FoodContract.FruitsLegumesEntry;
import com.example.android.garde_manger.data.FoodContract.FrigoEntry;
import com.example.android.garde_manger.data.FoodContract.CongeloEntry;
import com.example.android.garde_manger.data.FoodContract.PlacardsEntry;
import com.example.android.garde_manger.data.FoodContract.EpicesEntry;

import com.example.android.garde_manger.data.CourseContract.CourseListEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 *
 * Allows user to create a new food or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the food data loader */
    private static final int EXISTING_FOOD_LOADER = 0;

    /** Content URI for the existing food (null if it's a new food) */
    private Uri mCurrentFoodUri;

    // The table we are in
    private int mTableType;

    // The name of the food
    private EditText mNameEditText;

    // The scaleType for the spinner
    private Spinner mScaleTypeSpinner;

    // The Quantity TextView for the food
    private TextView mQuantityTextView;
    // Quantity variable
    private int mQuantity = 0;

    // Array of String for the custom scale of the quantity
    private String[] mQuantityCustom = {"0", "+", "++", "+++"};
    // Custom Quantity variable
    private int mQuantityCustomNumber = 0;

    // The Plus button
    private Button mPlusButton;
    // The Minus Button
    private Button mMinusButton;

    // The Expiry Date EditText
    private EditText mExpDateEditText;

    // The isInList TextView indicator
    private TextView mIsInListTextView;
    // The add to shopping list pickquer
    private LinearLayout mAddCourseQuantityEditor;
    // The EditText of the quantity to put into the shopping list
    private EditText mAddCourseQuantityEditText;
    // The add to shopping list button
    private Button mAddCourseList;

    // The TextView whioch display the recipes the food is in
    private TextView mRecetteListTextView;

    // Is in shopping list variable
    private int mIsInCourseList = FruitsLegumesEntry.NOT_IN_LIST;

    // The scaleType variable
    private int mScaleType = FruitsLegumesEntry.SCALE_TYPE_NUMERIC;

    /** Boolean flag that keeps track of whether the food has been edited (true) or not (false) */
    private boolean mFoodHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mFoodHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mFoodHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_food_name);
        mScaleTypeSpinner = (Spinner) findViewById(R.id.spinner_type);
        mQuantityTextView = (TextView) findViewById(R.id.edit_quantity_text_view);
        mRecetteListTextView = (TextView) findViewById(R.id.editor_recette_text_view);

        mPlusButton = (Button) findViewById(R.id.button_plus);
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusButtonClicked();
            }
        });

        mMinusButton = (Button) findViewById(R.id.button_minus);
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusButtonClicked();
            }
        });

        mExpDateEditText = (EditText) findViewById(R.id.edit_food_exp_date);

        mIsInListTextView = (TextView) findViewById(R.id.item_in_list_text_view);

        mAddCourseQuantityEditor = (LinearLayout) findViewById(R.id.add_course_quantity_editor);

        mAddCourseQuantityEditText = (EditText) findViewById(R.id.edit_add_course_quantity);

        mAddCourseList = (Button) findViewById(R.id.add_course_list_button);
        mAddCourseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToCourseList();
            }
        });

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new food or editing an existing one.
        Intent intent = getIntent();
        mCurrentFoodUri = intent.getData();
        Bundle bundle = getIntent().getExtras();
        mTableType = bundle.getInt("TableType");

        // If the intent DOES NOT contain a food content URI, then we know that we are
        // creating a new food.
        if (mCurrentFoodUri == null) {
            // This is a new food, so change the app bar to say "Add a Food"
            setTitle(getString(R.string.editor_activity_title_new_food));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a food that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing food, so change app bar to say "Edit Food"
            setTitle(getString(R.string.editor_activity_title_edit_food));
            // Initialize a loader to read the food data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_FOOD_LOADER, null, this);
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mScaleTypeSpinner.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mExpDateEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the quantity type of the food.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter scaleTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_scale_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        scaleTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mScaleTypeSpinner.setAdapter(scaleTypeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mScaleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.scale_type_numeric))) {
                        mScaleType = FruitsLegumesEntry.SCALE_TYPE_NUMERIC;
                        displayQuantity();
                    } else {
                        mScaleType = FruitsLegumesEntry.SCALE_TYPE_CUSTOM;
                        displayQuantityCustom();
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mScaleType = FruitsLegumesEntry.SCALE_TYPE_NUMERIC;
            }
        });
    }

    /**
     * Get user input from editor and save food into database.
     */
    private boolean saveFood() {
        // Create a List of String of fixed values to put into new food
        List<String> recetteNameList = new ArrayList<>(Collections.nCopies(200, ""));
        // Create a ContentValues object where column names are the keys,
        // and food attributes from the editor are the values.
        ContentValues values = new ContentValues();

        // Swith between the tables
        switch (mTableType){
            case 0:
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                String nameString = mNameEditText.getText().toString().trim();
                String quantityString = mQuantityTextView.getText().toString();
                String expDateString = mExpDateEditText.getText().toString().trim();

                // Check if this is supposed to be a new food
                // and check if all the fields in the editor are blank
                if (mCurrentFoodUri == null &&
                        TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                        TextUtils.isEmpty(expDateString) && mScaleType == FruitsLegumesEntry.SCALE_TYPE_NUMERIC) {
                    // Since no fields were modified, we can return early without creating a new food.
                    // No need to create ContentValues and no need to do any ContentProvider operations.
                    return true;
                }

                // Inform the user that the name must be filled
                if (TextUtils.isEmpty(nameString)) {
                    Toast.makeText(this, getString(R.string.food_name_required), Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Put the name value in the ContentValues
                values.put(FruitsLegumesEntry.COLUMN_NAME, nameString);

                // If the quantity in gram is not provided put 0 in the database
                if (TextUtils.isEmpty(quantityString)) {
                    quantityString = "0";
                    values.put(FruitsLegumesEntry.COLUMN_QUANTITY, quantityString);
                } else {
                    values.put(FruitsLegumesEntry.COLUMN_QUANTITY, quantityString);
                }

                // If the quantity in mL is not provided put Unknown in the database
                if (TextUtils.isEmpty(expDateString)) {
                    expDateString = "NA";
                    values.put(FruitsLegumesEntry.COLUMN_EXPIRY_DATE, expDateString);
                } else {
                    values.put(FruitsLegumesEntry.COLUMN_EXPIRY_DATE, expDateString);
                }

                // Put the scaleType variable value into the database
                values.put(FruitsLegumesEntry.COLUMN_SCALETYPE, mScaleType);
                // Put the is in shopping list variable value into the database
                values.put(FruitsLegumesEntry.COLUMN_IN_COURSE_LIST, mIsInCourseList);

                // Determine if this is a new or existing plant by checking if mCurrentFoodUri is null or not
                if (mCurrentFoodUri == null) {
                    // Put the empty List of string for names of recipes into the new food database
                    values.put(FruitsLegumesEntry.COLUMN_RECETTE_NAME, recetteNameList.toString()
                            .replaceAll("[\\[.\\].\\s+]", ""));
                    // This is a NEW food, so insert a new food into the provider,
                    // returning the content URI for the new food.
                    Uri newUri = getContentResolver().insert(FruitsLegumesEntry.CONTENT_URI, values);

                    // Show a toast message depending on whether or not the insertion was successful.
                    if (newUri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(this, getString(R.string.editor_insert_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_insert_food_successful), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Otherwise this is an EXISTING food, so update the food with content URI: mCurrentFoodUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentFoodUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentFoodUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(this, getString(R.string.editor_update_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_update_food_successful), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            case 1:
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                nameString = mNameEditText.getText().toString().trim();
                quantityString = mQuantityTextView.getText().toString();
                expDateString = mExpDateEditText.getText().toString().trim();

                // Check if this is supposed to be a new food
                // and check if all the fields in the editor are blank
                if (mCurrentFoodUri == null &&
                        TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                        TextUtils.isEmpty(expDateString) && mScaleType == FrigoEntry.SCALE_TYPE_NUMERIC) {
                    // Since no fields were modified, we can return early without creating a new food.
                    // No need to create ContentValues and no need to do any ContentProvider operations.
                    return true;
                }

                // Inform the user that the name must be filled
                if (TextUtils.isEmpty(nameString)) {
                    Toast.makeText(this, getString(R.string.food_name_required), Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Put the name value in the ContentValues
                values.put(FrigoEntry.COLUMN_NAME, nameString);

                // If the quantity in gram is not provided put 0 in the database
                if (TextUtils.isEmpty(quantityString)) {
                    quantityString = "0";
                    values.put(FrigoEntry.COLUMN_QUANTITY, quantityString);
                } else {
                    values.put(FrigoEntry.COLUMN_QUANTITY, quantityString);
                }

                // If the quantity in mL is not provided put Not provided in the database
                if (TextUtils.isEmpty(expDateString)) {
                    expDateString = "NA";
                    values.put(FrigoEntry.COLUMN_EXPIRY_DATE, expDateString);
                } else {
                    values.put(FrigoEntry.COLUMN_EXPIRY_DATE, expDateString);
                }

                values.put(FrigoEntry.COLUMN_SCALETYPE, mScaleType);
                values.put(FrigoEntry.COLUMN_IN_COURSE_LIST, mIsInCourseList);

                // Determine if this is a new or existing plant by checking if mCurrentFoodUri is null or not
                if (mCurrentFoodUri == null) {
                    values.put(FrigoEntry.COLUMN_RECETTE_NAME, recetteNameList.toString()
                            .replaceAll("[\\[.\\].\\s+]", ""));
                    // This is a NEW food, so insert a new food into the provider,
                    // returning the content URI for the new food.
                    Uri newUri = getContentResolver().insert(FrigoEntry.CONTENT_URI, values);

                    // Show a toast message depending on whether or not the insertion was successful.
                    if (newUri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(this, getString(R.string.editor_insert_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_insert_food_successful), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Otherwise this is an EXISTING food, so update the plant with content URI: mCurrentFoodUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentFoodUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentFoodUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(this, getString(R.string.editor_update_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_update_food_successful), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            case 2:
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                nameString = mNameEditText.getText().toString().trim();
                quantityString = mQuantityTextView.getText().toString();
                expDateString = mExpDateEditText.getText().toString().trim();

                // Check if this is supposed to be a new food
                // and check if all the fields in the editor are blank
                if (mCurrentFoodUri == null &&
                        TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                        TextUtils.isEmpty(expDateString) && mScaleType == CongeloEntry.SCALE_TYPE_NUMERIC) {
                    // Since no fields were modified, we can return early without creating a new food.
                    // No need to create ContentValues and no need to do any ContentProvider operations.
                    return true;
                }

                // Inform the user that the name must be filled
                if (TextUtils.isEmpty(nameString)) {
                    Toast.makeText(this, getString(R.string.food_name_required), Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Put the name value in the ContentValues
                values.put(CongeloEntry.COLUMN_NAME, nameString);

                // If the quantity in gram is not provided put 0 in the database
                if (TextUtils.isEmpty(quantityString)) {
                    quantityString = "0";
                    values.put(CongeloEntry.COLUMN_QUANTITY, quantityString);
                } else {
                    values.put(CongeloEntry.COLUMN_QUANTITY, quantityString);
                }

                // If the quantity in mL is not provided put Not provided in the database
                if (TextUtils.isEmpty(expDateString)) {
                    expDateString = "NA";
                    values.put(CongeloEntry.COLUMN_EXPIRY_DATE, expDateString);
                } else {
                    values.put(CongeloEntry.COLUMN_EXPIRY_DATE, expDateString);
                }

                values.put(CongeloEntry.COLUMN_SCALETYPE, mScaleType);
                values.put(CongeloEntry.COLUMN_IN_COURSE_LIST, mIsInCourseList);

                // Determine if this is a new or existing plant by checking if mCurrentFoodUri is null or not
                if (mCurrentFoodUri == null) {
                    values.put(CongeloEntry.COLUMN_RECETTE_NAME, recetteNameList.toString()
                            .replaceAll("[\\[.\\].\\s+]", ""));
                    // This is a NEW food, so insert a new food into the provider,
                    // returning the content URI for the new food.
                    Uri newUri = getContentResolver().insert(CongeloEntry.CONTENT_URI, values);

                    // Show a toast message depending on whether or not the insertion was successful.
                    if (newUri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(this, getString(R.string.editor_insert_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_insert_food_successful), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Otherwise this is an EXISTING food, so update the plant with content URI: mCurrentFoodUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentFoodUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentFoodUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(this, getString(R.string.editor_update_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_update_food_successful), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            case 3:
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                nameString = mNameEditText.getText().toString().trim();
                quantityString = mQuantityTextView.getText().toString();
                expDateString = mExpDateEditText.getText().toString().trim();

                // Check if this is supposed to be a new food
                // and check if all the fields in the editor are blank
                if (mCurrentFoodUri == null &&
                        TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                        TextUtils.isEmpty(expDateString) && mScaleType == PlacardsEntry.SCALE_TYPE_NUMERIC) {
                    // Since no fields were modified, we can return early without creating a new food.
                    // No need to create ContentValues and no need to do any ContentProvider operations.
                    return true;
                }

                // Inform the user that the name must be filled
                if (TextUtils.isEmpty(nameString)) {
                    Toast.makeText(this, getString(R.string.food_name_required), Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Put the name value in the ContentValues
                values.put(PlacardsEntry.COLUMN_NAME, nameString);

                // If the quantity in gram is not provided put 0 in the database
                if (TextUtils.isEmpty(quantityString)) {
                    quantityString = "0";
                    values.put(PlacardsEntry.COLUMN_QUANTITY, quantityString);
                } else {
                    values.put(PlacardsEntry.COLUMN_QUANTITY, quantityString);
                }

                // If the quantity in mL is not provided put Not provided in the database
                if (TextUtils.isEmpty(expDateString)) {
                    expDateString = "NA";
                    values.put(PlacardsEntry.COLUMN_EXPIRY_DATE, expDateString);
                } else {
                    values.put(PlacardsEntry.COLUMN_EXPIRY_DATE, expDateString);
                }

                values.put(PlacardsEntry.COLUMN_SCALETYPE, mScaleType);
                values.put(PlacardsEntry.COLUMN_IN_COURSE_LIST, mIsInCourseList);

                // Determine if this is a new or existing plant by checking if mCurrentFoodUri is null or not
                if (mCurrentFoodUri == null) {
                    values.put(PlacardsEntry.COLUMN_RECETTE_NAME, recetteNameList.toString()
                            .replaceAll("[\\[.\\].\\s+]", ""));
                    // This is a NEW food, so insert a new food into the provider,
                    // returning the content URI for the new food.
                    Uri newUri = getContentResolver().insert(PlacardsEntry.CONTENT_URI, values);

                    // Show a toast message depending on whether or not the insertion was successful.
                    if (newUri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(this, getString(R.string.editor_insert_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_insert_food_successful), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Otherwise this is an EXISTING food, so update the plant with content URI: mCurrentFoodUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentFoodUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentFoodUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(this, getString(R.string.editor_update_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_update_food_successful), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            case 4:
                // Read from input fields
                // Use trim to eliminate leading or trailing white space
                nameString = mNameEditText.getText().toString().trim();
                quantityString = mQuantityTextView.getText().toString();
                expDateString = mExpDateEditText.getText().toString().trim();

                // Check if this is supposed to be a new food
                // and check if all the fields in the editor are blank
                if (mCurrentFoodUri == null &&
                        TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                        TextUtils.isEmpty(expDateString) && mScaleType == EpicesEntry.SCALE_TYPE_NUMERIC) {
                    // Since no fields were modified, we can return early without creating a new food.
                    // No need to create ContentValues and no need to do any ContentProvider operations.
                    return true;
                }

                // Inform the user that the name must be filled
                if (TextUtils.isEmpty(nameString)) {
                    Toast.makeText(this, getString(R.string.food_name_required), Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Put the name value in the ContentValues
                values.put(EpicesEntry.COLUMN_NAME, nameString);

                // If the quantity in gram is not provided put 0 in the database
                if (TextUtils.isEmpty(quantityString)) {
                    quantityString = "0";
                    values.put(EpicesEntry.COLUMN_QUANTITY, quantityString);
                } else {
                    values.put(EpicesEntry.COLUMN_QUANTITY, quantityString);
                }

                // If the quantity in mL is not provided put Not provided in the database
                if (TextUtils.isEmpty(expDateString)) {
                    expDateString = "NA";
                    values.put(EpicesEntry.COLUMN_EXPIRY_DATE, expDateString);
                } else {
                    values.put(EpicesEntry.COLUMN_EXPIRY_DATE, expDateString);
                }

                values.put(EpicesEntry.COLUMN_SCALETYPE, mScaleType);
                values.put(EpicesEntry.COLUMN_IN_COURSE_LIST, mIsInCourseList);

                // Determine if this is a new or existing plant by checking if mCurrentFoodUri is null or not
                if (mCurrentFoodUri == null) {
                    values.put(EpicesEntry.COLUMN_RECETTE_NAME, recetteNameList.toString()
                            .replaceAll("[\\[.\\].\\s+]", ""));
                    // This is a NEW food, so insert a new food into the provider,
                    // returning the content URI for the new food.
                    Uri newUri = getContentResolver().insert(EpicesEntry.CONTENT_URI, values);

                    // Show a toast message depending on whether or not the insertion was successful.
                    if (newUri == null) {
                        // If the new content URI is null, then there was an error with insertion.
                        Toast.makeText(this, getString(R.string.editor_insert_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the insertion was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_insert_food_successful), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Otherwise this is an EXISTING food, so update the plant with content URI: mCurrentFoodUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentFoodUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentFoodUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(this, getString(R.string.editor_update_food_failed), Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_update_food_successful), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            default:
                return false;
        }
    }

    /**
     * Add the current food to the Shopping list database.
     */
    private void addFoodToCourseList() {
        if (mIsInCourseList == FruitsLegumesEntry.IS_IN_LIST){
            Toast.makeText(this, getString(R.string.already_in_shopping_list), Toast.LENGTH_SHORT).show();
        } else {

            // Check if this is supposed to be a new food
            // and check if all the fields in the editor are blank
            if (mCurrentFoodUri == null) {
                Toast.makeText(this, getString(R.string.editor_add_new_to_list), Toast.LENGTH_SHORT).show();
                return;
            }

            mIsInCourseList = FruitsLegumesEntry.IS_IN_LIST;

            // Create a ContentValues object where column names are the keys,
            // and food attributes from the editor are the values.
            ContentValues courseValues = new ContentValues();

            // Read from input fields
            // Use trim to eliminate leading or trailing white space
            String nameString = mNameEditText.getText().toString().trim();
            String quantityString = mAddCourseQuantityEditText.getText().toString().trim();

            // Inform the user that the name must be filled
            if (TextUtils.isEmpty(nameString)) {
                Toast.makeText(this, getString(R.string.food_name_required), Toast.LENGTH_SHORT).show();
            }
            // Put the name value in the ContentValues
            courseValues.put(CourseListEntry.COLUMN_NAME, nameString);
            // Put the food uri in the ContentValues
            courseValues.put(CourseListEntry.COLUMN_FOOD_URI, mCurrentFoodUri.toString());

            // If no quantity is provided, put a default values into the ContentValues
            if (TextUtils.isEmpty(quantityString)){
                quantityString = "NA";
                courseValues.put(CourseListEntry.COLUMN_FOOD_QUANTITY, quantityString);
            } else {
                courseValues.put(CourseListEntry.COLUMN_FOOD_QUANTITY, quantityString);
            }

            // Inssert the the new shop item into the shopping list database
            Uri newUri = getContentResolver().insert(CourseListEntry.CONTENT_URI, courseValues);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_food_in_list_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_food_in_list_successful), Toast.LENGTH_SHORT).show();
            }

            // Update the corresponding food we are in to notify that the food is already in the Shopping List (is in list indicator)
            ContentValues values = new ContentValues();
            switch (mTableType) {
                case 0:
                    values.put(FruitsLegumesEntry.COLUMN_IN_COURSE_LIST, FruitsLegumesEntry.IS_IN_LIST);
                    getContentResolver().update(mCurrentFoodUri, values, null, null);
                    break;
                case 1:
                    values.put(FrigoEntry.COLUMN_IN_COURSE_LIST, FrigoEntry.IS_IN_LIST);
                    getContentResolver().update(mCurrentFoodUri, values, null, null);
                    break;
                case 2:
                    values.put(CongeloEntry.COLUMN_IN_COURSE_LIST, CongeloEntry.IS_IN_LIST);
                    getContentResolver().update(mCurrentFoodUri, values, null, null);
                    break;
                case 3:
                    values.put(PlacardsEntry.COLUMN_IN_COURSE_LIST, PlacardsEntry.IS_IN_LIST);
                    getContentResolver().update(mCurrentFoodUri, values, null, null);
                    break;
                case 4:
                    values.put(EpicesEntry.COLUMN_IN_COURSE_LIST, EpicesEntry.IS_IN_LIST);
                    getContentResolver().update(mCurrentFoodUri, values, null, null);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new food, hide the "Delete" menu item.
        if (mCurrentFoodUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save food to database
                if(saveFood()) {
                    // Exit activity
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the food hasn't changed, continue with navigating up to parent activity
                // which is the {@link StockActivity}.
                if (!mFoodHasChanged) {
                    finish();
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the food hasn't changed, continue with handling back button press
        if (!mFoodHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (mTableType) {
            case 0:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the FruitsLegumes table
                String[] projectionFruitsLegumes = {
                        FruitsLegumesEntry._ID,
                        FruitsLegumesEntry.COLUMN_NAME,
                        FruitsLegumesEntry.COLUMN_QUANTITY,
                        FruitsLegumesEntry.COLUMN_SCALETYPE,
                        FruitsLegumesEntry.COLUMN_EXPIRY_DATE,
                        FruitsLegumesEntry.COLUMN_IN_COURSE_LIST,
                        FruitsLegumesEntry.COLUMN_RECETTE_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        mCurrentFoodUri,       // Query the content URI for the current pet
                        projectionFruitsLegumes,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
            case 1:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the Frigo table
                String[] projectionFrigo = {
                        FrigoEntry._ID,
                        FrigoEntry.COLUMN_NAME,
                        FrigoEntry.COLUMN_QUANTITY,
                        FrigoEntry.COLUMN_SCALETYPE,
                        FrigoEntry.COLUMN_EXPIRY_DATE,
                        FrigoEntry.COLUMN_IN_COURSE_LIST,
                        FrigoEntry.COLUMN_RECETTE_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        mCurrentFoodUri,       // Query the content URI for the current pet
                        projectionFrigo,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
            case 2:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the Congelo table
                String[] projectionCongelo = {
                        CongeloEntry._ID,
                        CongeloEntry.COLUMN_NAME,
                        CongeloEntry.COLUMN_QUANTITY,
                        CongeloEntry.COLUMN_SCALETYPE,
                        CongeloEntry.COLUMN_EXPIRY_DATE,
                        CongeloEntry.COLUMN_IN_COURSE_LIST,
                        CongeloEntry.COLUMN_RECETTE_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        mCurrentFoodUri,       // Query the content URI for the current pet
                        projectionCongelo,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
            case 3:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the Placards table
                String[] projectionPlacards = {
                        PlacardsEntry._ID,
                        PlacardsEntry.COLUMN_NAME,
                        PlacardsEntry.COLUMN_QUANTITY,
                        PlacardsEntry.COLUMN_SCALETYPE,
                        PlacardsEntry.COLUMN_EXPIRY_DATE,
                        PlacardsEntry.COLUMN_IN_COURSE_LIST,
                        PlacardsEntry.COLUMN_RECETTE_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        mCurrentFoodUri,       // Query the content URI for the current pet
                        projectionPlacards,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
            case 4:
                // Since the editor shows all food attributes, define a projection that contains
                // all columns from the Epices table
                String[] projectionEpices = {
                        EpicesEntry._ID,
                        EpicesEntry.COLUMN_NAME,
                        EpicesEntry.COLUMN_QUANTITY,
                        EpicesEntry.COLUMN_SCALETYPE,
                        EpicesEntry.COLUMN_EXPIRY_DATE,
                        EpicesEntry.COLUMN_IN_COURSE_LIST,
                        EpicesEntry.COLUMN_RECETTE_NAME};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        mCurrentFoodUri,       // Query the content URI for the current pet
                        projectionEpices,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            switch (mTableType) {
                case 0:
                    // Find the columns of food attributes that we're interested in
                    int nameFruitsLegumesColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_NAME);
                    int quantityFruitsLegumesColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_QUANTITY);
                    int scaleTypeFruitsLegumesColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_SCALETYPE);
                    int expiryDateFruitsLegumesColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_EXPIRY_DATE);
                    int inCourseListFruitsLegumesColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_IN_COURSE_LIST);
                    int recetteNameFruitsLegumesColumnIndex = cursor.getColumnIndex(FruitsLegumesEntry.COLUMN_RECETTE_NAME);

                    // Extract out the value from the Cursor for the given column index
                    String nameFruitsLegumes = cursor.getString(nameFruitsLegumesColumnIndex);
                    String quantityFruitsLegumes = cursor.getString(quantityFruitsLegumesColumnIndex);
                    int scaleTypeFruitsLegumes = cursor.getInt(scaleTypeFruitsLegumesColumnIndex);
                    String expiryDateFruitsLegumes = cursor.getString(expiryDateFruitsLegumesColumnIndex);
                    int inCourseListFruitsLegumes = cursor.getInt(inCourseListFruitsLegumesColumnIndex);
                    String recetteNameFruitsLegumes = cursor.getString(recetteNameFruitsLegumesColumnIndex);

                    // Update the views on the screen with the values from the database
                    mNameEditText.setText(nameFruitsLegumes);
                    mQuantityTextView.setText(quantityFruitsLegumes);

                    // Type is a dropdown spinner, so map the constant value from the database
                    // into one of the dropdown options (1 is Numeric, 2 is Custom).
                    // Then call setSelection() so that option is displayed on screen as the current selection.
                    switch (scaleTypeFruitsLegumes) {
                        case FruitsLegumesEntry.SCALE_TYPE_NUMERIC:
                            mScaleTypeSpinner.setSelection(0);
                            mScaleType = 1;
                            break;
                        case FruitsLegumesEntry.SCALE_TYPE_CUSTOM:
                            mScaleTypeSpinner.setSelection(1);
                            mScaleType = 2;
                            break;
                    }

                    // Settign the quantity variables according to the scaleType
                    if (mScaleType == 1) {
                        mQuantity = Integer.parseInt(quantityFruitsLegumes);
                    } else {
                        switch (quantityFruitsLegumes) {
                            case "0":
                                mQuantityCustomNumber = 0;
                            case "+":
                                mQuantityCustomNumber = 1;
                            case "++":
                                mQuantityCustomNumber = 2;
                            case "+++":
                                mQuantityCustomNumber = 3;
                        }
                    }

                    mExpDateEditText.setText(expiryDateFruitsLegumes);

                    mIsInCourseList = inCourseListFruitsLegumes;
                    if (inCourseListFruitsLegumes == 0) {
                        mIsInListTextView.setText(getString(R.string.no));
                        mAddCourseQuantityEditor.setVisibility(View.VISIBLE);
                        mIsInListTextView.setTextColor(Color.RED);
                    } else {
                        mIsInListTextView.setText(getString(R.string.yes));
                        mAddCourseQuantityEditor.setVisibility(View.GONE);
                        mIsInListTextView.setTextColor(Color.GREEN);
                    }

                    // Parse Recipes from the String containing all the recipes and format it to display a simple list of text
                    if (!TextUtils.isEmpty(recetteNameFruitsLegumes.replace(",", ""))) {
                        String[] recetteNameFruitsLegumesSplit = recetteNameFruitsLegumes.split(",");
                        StringBuilder recetteNameFruitsLegumesString = new StringBuilder();
                        for (int l = 0; l < recetteNameFruitsLegumesSplit.length; l++) {
                            if (!recetteNameFruitsLegumesSplit[l].isEmpty()) {
                                recetteNameFruitsLegumesString.append(recetteNameFruitsLegumesSplit[l]);
                                recetteNameFruitsLegumesString.append("\n");
                            }
                        }
                        recetteNameFruitsLegumesString.deleteCharAt(recetteNameFruitsLegumesString.length() - 1);
                        mRecetteListTextView.setText(recetteNameFruitsLegumesString.toString().replace("_", " "));
                    }

                    break;

                case 1:
                    // Find the columns of plant attributes that we're interested in
                    int nameFrigoColumnIndex = cursor.getColumnIndex(FrigoEntry.COLUMN_NAME);
                    int quantityFrigoColumnIndex = cursor.getColumnIndex(FrigoEntry.COLUMN_QUANTITY);
                    int scaleTypeFrigoColumnIndex = cursor.getColumnIndex(FrigoEntry.COLUMN_SCALETYPE);
                    int expiryDateFrigoColumnIndex = cursor.getColumnIndex(FrigoEntry.COLUMN_EXPIRY_DATE);
                    int inCourseListFrigoColumnIndex = cursor.getColumnIndex(FrigoEntry.COLUMN_IN_COURSE_LIST);
                    int recetteNameFrigoColumnIndex = cursor.getColumnIndex(FrigoEntry.COLUMN_RECETTE_NAME);

                    // Extract out the value from the Cursor for the given column index
                    String nameFrigo = cursor.getString(nameFrigoColumnIndex);
                    String quantityFrigo = cursor.getString(quantityFrigoColumnIndex);
                    int scaleTypeFrigo = cursor.getInt(scaleTypeFrigoColumnIndex);
                    String expiryDateFrigo = cursor.getString(expiryDateFrigoColumnIndex);
                    int inCourseListFrigo = cursor.getInt(inCourseListFrigoColumnIndex);
                    String recetteNameFrigo = cursor.getString(recetteNameFrigoColumnIndex);

                    // Update the views on the screen with the values from the database
                    mNameEditText.setText(nameFrigo);
                    mQuantityTextView.setText(quantityFrigo);

                    // Type is a dropdown spinner, so map the constant value from the database
                    // into one of the dropdown options (1 is Numeric, 2 is Custom).
                    // Then call setSelection() so that option is displayed on screen as the current selection.
                    switch (scaleTypeFrigo) {
                        case FrigoEntry.SCALE_TYPE_NUMERIC:
                            mScaleTypeSpinner.setSelection(0);
                            mScaleType = 1;
                            break;
                        case FrigoEntry.SCALE_TYPE_CUSTOM:
                            mScaleTypeSpinner.setSelection(1);
                            mScaleType = 2;
                            break;
                    }

                    if (mScaleType == 1) {
                        mQuantity = Integer.parseInt(quantityFrigo);
                    } else {
                        switch (quantityFrigo) {
                            case "0":
                                mQuantityCustomNumber = 0;
                            case "+":
                                mQuantityCustomNumber = 1;
                            case "++":
                                mQuantityCustomNumber = 2;
                            case "+++":
                                mQuantityCustomNumber = 3;
                        }
                    }

                    mExpDateEditText.setText(expiryDateFrigo);

                    mIsInCourseList = inCourseListFrigo;
                    if (inCourseListFrigo == 0) {
                        mIsInListTextView.setText(getString(R.string.no));
                        mAddCourseQuantityEditor.setVisibility(View.VISIBLE);
                        mIsInListTextView.setTextColor(Color.RED);
                    } else {
                        mIsInListTextView.setText(getString(R.string.yes));
                        mAddCourseQuantityEditor.setVisibility(View.GONE);
                        mIsInListTextView.setTextColor(Color.GREEN);
                    }

                    if (!TextUtils.isEmpty(recetteNameFrigo.replace(",", ""))) {
                        String[] recetteNameFrigoSplit = recetteNameFrigo.split(",");
                        StringBuilder recetteNameFrigoString = new StringBuilder();
                        for (int l = 0; l < recetteNameFrigoSplit.length; l++) {
                            if (!recetteNameFrigoSplit[l].isEmpty()) {
                                recetteNameFrigoString.append(recetteNameFrigoSplit[l]);
                                recetteNameFrigoString.append("\n");
                            }
                        }
                        recetteNameFrigoString.deleteCharAt(recetteNameFrigoString.length() - 1);
                        mRecetteListTextView.setText(recetteNameFrigoString.toString().replace("_", " "));
                    }

                    break;

                case 2:
                    // Find the columns of plant attributes that we're interested in
                    int nameCongeloColumnIndex = cursor.getColumnIndex(CongeloEntry.COLUMN_NAME);
                    int quantityCongeloColumnIndex = cursor.getColumnIndex(CongeloEntry.COLUMN_QUANTITY);
                    int scaleTypeCongeloColumnIndex = cursor.getColumnIndex(CongeloEntry.COLUMN_SCALETYPE);
                    int expiryDateCongeloColumnIndex = cursor.getColumnIndex(CongeloEntry.COLUMN_EXPIRY_DATE);
                    int inCourseListCongeloColumnIndex = cursor.getColumnIndex(CongeloEntry.COLUMN_IN_COURSE_LIST);
                    int recetteNameCongeloColumnIndex = cursor.getColumnIndex(CongeloEntry.COLUMN_RECETTE_NAME);

                    // Extract out the value from the Cursor for the given column index
                    String nameCongelo = cursor.getString(nameCongeloColumnIndex);
                    String quantityCongelo = cursor.getString(quantityCongeloColumnIndex);
                    int scaleTypeCongelo = cursor.getInt(scaleTypeCongeloColumnIndex);
                    String expiryDateCongelo = cursor.getString(expiryDateCongeloColumnIndex);
                    int inCourseListCongelo = cursor.getInt(inCourseListCongeloColumnIndex);
                    String recetteNameCongelo = cursor.getString(recetteNameCongeloColumnIndex);

                    // Update the views on the screen with the values from the database
                    mNameEditText.setText(nameCongelo);
                    mQuantityTextView.setText(quantityCongelo);

                    // Type is a dropdown spinner, so map the constant value from the database
                    // into one of the dropdown options (1 is Numeric, 2 is Custom).
                    // Then call setSelection() so that option is displayed on screen as the current selection.
                    switch (scaleTypeCongelo) {
                        case CongeloEntry.SCALE_TYPE_NUMERIC:
                            mScaleTypeSpinner.setSelection(0);
                            mScaleType = 1;
                            break;
                        case CongeloEntry.SCALE_TYPE_CUSTOM:
                            mScaleTypeSpinner.setSelection(1);
                            mScaleType = 2;
                            break;
                    }

                    if (mScaleType == 1) {
                        mQuantity = Integer.parseInt(quantityCongelo);
                    } else {
                        switch (quantityCongelo) {
                            case "0":
                                mQuantityCustomNumber = 0;
                            case "+":
                                mQuantityCustomNumber = 1;
                            case "++":
                                mQuantityCustomNumber = 2;
                            case "+++":
                                mQuantityCustomNumber = 3;
                        }
                    }

                    mExpDateEditText.setText(expiryDateCongelo);

                    mIsInCourseList = inCourseListCongelo;
                    if (inCourseListCongelo == 0) {
                        mIsInListTextView.setText(getString(R.string.no));
                        mAddCourseQuantityEditor.setVisibility(View.VISIBLE);
                        mIsInListTextView.setTextColor(Color.RED);
                    } else {
                        mIsInListTextView.setText(getString(R.string.yes));
                        mAddCourseQuantityEditor.setVisibility(View.GONE);
                        mIsInListTextView.setTextColor(Color.GREEN);
                    }

                    if (!TextUtils.isEmpty(recetteNameCongelo.replace(",", ""))) {
                        String[] recetteNameCongeloSplit = recetteNameCongelo.split(",");
                        StringBuilder recetteNameCongeloString = new StringBuilder();
                        for (int l = 0; l < recetteNameCongeloSplit.length; l++) {
                            if (!recetteNameCongeloSplit[l].isEmpty()) {
                                recetteNameCongeloString.append(recetteNameCongeloSplit[l]);
                                recetteNameCongeloString.append("\n");
                            }
                        }
                        recetteNameCongeloString.deleteCharAt(recetteNameCongeloString.length() - 1);
                        mRecetteListTextView.setText(recetteNameCongeloString.toString().replace("_", " "));
                    }

                    break;

                case 3:
                    // Find the columns of plant attributes that we're interested in
                    int namePlacardsColumnIndex = cursor.getColumnIndex(PlacardsEntry.COLUMN_NAME);
                    int quantityPlacardsColumnIndex = cursor.getColumnIndex(PlacardsEntry.COLUMN_QUANTITY);
                    int scaleTypePlacardsColumnIndex = cursor.getColumnIndex(PlacardsEntry.COLUMN_SCALETYPE);
                    int expiryDatePlacardsColumnIndex = cursor.getColumnIndex(PlacardsEntry.COLUMN_EXPIRY_DATE);
                    int inCourseListPlacardsColumnIndex = cursor.getColumnIndex(PlacardsEntry.COLUMN_IN_COURSE_LIST);
                    int recetteNamePlacardsColumnIndex = cursor.getColumnIndex(PlacardsEntry.COLUMN_RECETTE_NAME);

                    // Extract out the value from the Cursor for the given column index
                    String namePlacards = cursor.getString(namePlacardsColumnIndex);
                    String quantityPlacards = cursor.getString(quantityPlacardsColumnIndex);
                    int scaleTypePlacards = cursor.getInt(scaleTypePlacardsColumnIndex);
                    String expiryDatePlacards = cursor.getString(expiryDatePlacardsColumnIndex);
                    int inCourseListPlacards = cursor.getInt(inCourseListPlacardsColumnIndex);
                    String recetteNamePlacards = cursor.getString(recetteNamePlacardsColumnIndex);

                    // Update the views on the screen with the values from the database
                    mNameEditText.setText(namePlacards);
                    mQuantityTextView.setText(quantityPlacards);

                    // Type is a dropdown spinner, so map the constant value from the database
                    // into one of the dropdown options (1 is Numeric, 2 is Custom).
                    // Then call setSelection() so that option is displayed on screen as the current selection.
                    switch (scaleTypePlacards) {
                        case PlacardsEntry.SCALE_TYPE_NUMERIC:
                            mScaleTypeSpinner.setSelection(0);
                            mScaleType = 1;
                            break;
                        case PlacardsEntry.SCALE_TYPE_CUSTOM:
                            mScaleTypeSpinner.setSelection(1);
                            mScaleType = 2;
                            break;
                    }

                    if (mScaleType == 1) {
                        mQuantity = Integer.parseInt(quantityPlacards);
                    } else {
                        switch (quantityPlacards) {
                            case "0":
                                mQuantityCustomNumber = 0;
                            case "+":
                                mQuantityCustomNumber = 1;
                            case "++":
                                mQuantityCustomNumber = 2;
                            case "+++":
                                mQuantityCustomNumber = 3;
                        }
                    }

                    mExpDateEditText.setText(expiryDatePlacards);

                    mIsInCourseList = inCourseListPlacards;
                    if (inCourseListPlacards == 0) {
                        mIsInListTextView.setText(getString(R.string.no));
                        mAddCourseQuantityEditor.setVisibility(View.VISIBLE);
                        mIsInListTextView.setTextColor(Color.RED);
                    } else {
                        mIsInListTextView.setText(getString(R.string.yes));
                        mAddCourseQuantityEditor.setVisibility(View.GONE);
                        mIsInListTextView.setTextColor(Color.GREEN);
                    }

                    if (!TextUtils.isEmpty(recetteNamePlacards.replace(",", ""))) {
                        String[] recetteNamePlacardsSplit = recetteNamePlacards.split(",");
                        StringBuilder recetteNamePlacardsString = new StringBuilder();
                        for (int l = 0; l < recetteNamePlacardsSplit.length; l++) {
                            if (!recetteNamePlacardsSplit[l].isEmpty()) {
                                recetteNamePlacardsString.append(recetteNamePlacardsSplit[l]);
                                recetteNamePlacardsString.append("\n");
                            }
                        }
                        recetteNamePlacardsString.deleteCharAt(recetteNamePlacardsString.length() - 1);
                        mRecetteListTextView.setText(recetteNamePlacardsString.toString().replace("_", " "));
                    }

                    break;

                case 4:
                    // Find the columns of plant attributes that we're interested in
                    int nameEpicesColumnIndex = cursor.getColumnIndex(EpicesEntry.COLUMN_NAME);
                    int quantityEpicesColumnIndex = cursor.getColumnIndex(EpicesEntry.COLUMN_QUANTITY);
                    int scaleTypeEpicesColumnIndex = cursor.getColumnIndex(EpicesEntry.COLUMN_SCALETYPE);
                    int expiryDateEpicesColumnIndex = cursor.getColumnIndex(EpicesEntry.COLUMN_EXPIRY_DATE);
                    int inCourseListEpicesColumnIndex = cursor.getColumnIndex(EpicesEntry.COLUMN_IN_COURSE_LIST);
                    int recetteNameEpicesColumnIndex = cursor.getColumnIndex(EpicesEntry.COLUMN_RECETTE_NAME);

                    // Extract out the value from the Cursor for the given column index
                    String nameEpices = cursor.getString(nameEpicesColumnIndex);
                    String quantityEpices = cursor.getString(quantityEpicesColumnIndex);
                    int scaleTypeEpices = cursor.getInt(scaleTypeEpicesColumnIndex);
                    String expiryDateEpices = cursor.getString(expiryDateEpicesColumnIndex);
                    int inCourseListEpices = cursor.getInt(inCourseListEpicesColumnIndex);
                    String recetteNameEpices = cursor.getString(recetteNameEpicesColumnIndex);

                    // Update the views on the screen with the values from the database
                    mNameEditText.setText(nameEpices);
                    mQuantityTextView.setText(quantityEpices);

                    // Type is a dropdown spinner, so map the constant value from the database
                    // into one of the dropdown options (1 is Numeric, 2 is Custom).
                    // Then call setSelection() so that option is displayed on screen as the current selection.
                    switch (scaleTypeEpices) {
                        case EpicesEntry.SCALE_TYPE_NUMERIC:
                            mScaleTypeSpinner.setSelection(0);
                            mScaleType = 1;
                            break;
                        case EpicesEntry.SCALE_TYPE_CUSTOM:
                            mScaleTypeSpinner.setSelection(1);
                            mScaleType = 2;
                            break;
                    }

                    if (mScaleType == 1) {
                        mQuantity = Integer.parseInt(quantityEpices);
                    } else {
                        switch (quantityEpices) {
                            case "0":
                                mQuantityCustomNumber = 0;
                            case "+":
                                mQuantityCustomNumber = 1;
                            case "++":
                                mQuantityCustomNumber = 2;
                            case "+++":
                                mQuantityCustomNumber = 3;
                        }
                    }

                    mExpDateEditText.setText(expiryDateEpices);

                    mIsInCourseList = inCourseListEpices;
                    if (inCourseListEpices == 0) {
                        mIsInListTextView.setText(getString(R.string.no));
                        mAddCourseQuantityEditor.setVisibility(View.VISIBLE);
                        mIsInListTextView.setTextColor(Color.RED);
                    } else {
                        mIsInListTextView.setText(getString(R.string.yes));
                        mAddCourseQuantityEditor.setVisibility(View.GONE);
                        mIsInListTextView.setTextColor(Color.GREEN);
                    }

                    if (!TextUtils.isEmpty(recetteNameEpices.replace(",", ""))) {
                        String[] recetteNameEpicesSplit = recetteNameEpices.split(",");
                        StringBuilder recetteNameEpicesString = new StringBuilder();
                        for (int l = 0; l < recetteNameEpicesSplit.length; l++) {
                            if (!recetteNameEpicesSplit[l].isEmpty()) {
                                recetteNameEpicesString.append(recetteNameEpicesSplit[l]);
                                recetteNameEpicesString.append("\n");
                            }
                        }
                        recetteNameEpicesString.deleteCharAt(recetteNameEpicesString.length() - 1);
                        mRecetteListTextView.setText(recetteNameEpicesString.toString().replace("_", " "));
                    }

                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mQuantityTextView.setText("");
        mExpDateEditText.setText("");
        mIsInListTextView.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the food.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_food_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the food.
                deleteFood();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the food.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteFood() {
        // Only perform the delete if this is an existing food.
        if (mCurrentFoodUri != null) {
            // Call the ContentResolver to delete the food at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPlantUri
            // content URI already identifies the food that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentFoodUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_food_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_food_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    // Method to increment the quantity when plus button clicked and display it
    public void plusButtonClicked() {
        switch (mScaleType){
            case 1:
                mQuantityCustomNumber = 0;
                mQuantity++;
                displayQuantity();
                break;
            case 2:
                mQuantity = 0;
                if (mQuantityCustomNumber + 1 < mQuantityCustom.length) {
                    mQuantityCustomNumber++;
                    displayQuantityCustom();
                } else {
                    Toast.makeText(this, "Can't increase quantity", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // Method to decrement the quantity when plus button clicked and display it
    // Handle the case where the quantity is already at zero, prevent a negative value
    public void minusButtonClicked() {
        switch (mScaleType) {
            case 1:
                mQuantityCustomNumber = 0;
                if (mQuantity == 0) {
                    Toast.makeText(this, "Can't decrease quantity", Toast.LENGTH_SHORT).show();
                } else {
                    mQuantity--;
                    displayQuantity();
                }
                break;
            case 2:
                mQuantity = 0;
                if (mQuantityCustomNumber == 0) {
                    Toast.makeText(this, "Can't decrease quantity", Toast.LENGTH_SHORT).show();
                } else {
                    mQuantityCustomNumber--;
                    displayQuantityCustom();
                }
                break;
        }
    }

    // Method to display the quantity in the given textView
    public void displayQuantity() {
        mQuantityTextView.setText(String.valueOf(mQuantity));
    }

    // Method to display the quantity in the given textView
    public void displayQuantityCustom() {
        mQuantityTextView.setText(String.valueOf(mQuantityCustom[mQuantityCustomNumber]));
    }
}