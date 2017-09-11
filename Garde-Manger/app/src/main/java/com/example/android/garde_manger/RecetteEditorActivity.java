package com.example.android.garde_manger;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.garde_manger.data.FoodContract.FruitsLegumesEntry;
import com.example.android.garde_manger.data.FoodContract.FrigoEntry;
import com.example.android.garde_manger.data.FoodContract.CongeloEntry;
import com.example.android.garde_manger.data.FoodContract.PlacardsEntry;
import com.example.android.garde_manger.data.FoodContract.EpicesEntry;

import com.example.android.garde_manger.data.RecetteContract.RecetteEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 *
 * Allows user to create a new recette or edit an existing one.
 */
public class RecetteEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the food data loader */
    private static final int EXISTING_RECETTE_LOADER = 0;

    /** Identifiers fo the different tables */
    private static final int FRUILEG_TABLE_ID = 0;
    private static final int FRIGO_TABLE_ID = 1;
    private static final int CONGELO_TABLE_ID = 2;
    private static final int PLACARDS_TABLE_ID = 3;
    private static final int EPICES_TABLE_ID = 4;

    /** Content URI for the existing food (null if it's a new food) */
    private Uri mCurrentRecetteUri;

    // The TextViews
    private EditText mNameEditText;
    private TextView mIngredientFruitLegTextView;
    private TextView mIngredientFrigoTextView;
    private TextView mIngredientCongeloTextView;
    private TextView mIngredientPlacardsTextView;
    private TextView mIngredientEpicesTextView;

    // The Buttons
    private Button mSelectFruitLegButton;
    private Button mSelectFrigoButton;
    private Button mSelectCongeloButton;
    private Button mSelectPlacardsButton;
    private Button mSelectEpicesButton;

    // The List of checked items
    private String mCheckedFruitLeg;
    private String mCheckedFrigo;
    private String mCheckedCongelo;
    private String mCheckedPlacards;
    private String mCheckedEpices;

    /** Boolean flag that keeps track of whether the recipe has been edited (true) or not (false) */
    private boolean mRecetteHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mRecetteHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mRecetteHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recette_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_recette_name);
        mIngredientFruitLegTextView = (TextView) findViewById(R.id.ingredient_fruitleg_text_view);
        mIngredientFrigoTextView = (TextView) findViewById(R.id.ingredient_frigo_text_view);
        mIngredientCongeloTextView = (TextView) findViewById(R.id.ingredient_congelo_text_view);
        mIngredientPlacardsTextView = (TextView) findViewById(R.id.ingredient_placards_text_view);
        mIngredientEpicesTextView = (TextView) findViewById(R.id.ingredient_epices_text_view);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new recipe or editing an existing one.
        Intent intent = getIntent();
        mCurrentRecetteUri = intent.getData();

        mSelectFruitLegButton = (Button) findViewById(R.id.select_fruitleg_button);
        // Setting up the intent to launch the ingredient activity to select ingredients. Passing the tableType and the Checked List
        mSelectFruitLegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the need to create the recipe before editing it because we need the id of the recipe to update ingredients item
                if (mCurrentRecetteUri == null) {
                    Toast.makeText(RecetteEditorActivity.this, "Create the recette before editing ingredients", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RecetteEditorActivity.this, IngredientActivity.class);
                    intent.putExtra("TableType", FRUILEG_TABLE_ID);
                    intent.putExtra("CheckedList", mCheckedFruitLeg);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, FRUILEG_TABLE_ID);
                    }
                }
            }
        });

        mSelectFrigoButton = (Button) findViewById(R.id.select_frigo_button);
        mSelectFrigoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentRecetteUri == null) {
                    Toast.makeText(RecetteEditorActivity.this, "Create the recette before editing ingredients", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RecetteEditorActivity.this, IngredientActivity.class);
                    intent.putExtra("TableType", FRIGO_TABLE_ID);
                    intent.putExtra("CheckedList", mCheckedFrigo);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, FRIGO_TABLE_ID);
                    }
                }
            }
        });

        mSelectCongeloButton = (Button) findViewById(R.id.select_congelo_button);
        mSelectCongeloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentRecetteUri == null) {
                    Toast.makeText(RecetteEditorActivity.this, "Create the recette before editing ingredients", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RecetteEditorActivity.this, IngredientActivity.class);
                    intent.putExtra("TableType", CONGELO_TABLE_ID);
                    intent.putExtra("CheckedList", mCheckedCongelo);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, CONGELO_TABLE_ID);
                    }
                }
            }
        });

        mSelectPlacardsButton = (Button) findViewById(R.id.select_placards_button);
        mSelectPlacardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentRecetteUri == null) {
                    Toast.makeText(RecetteEditorActivity.this, "Create the recette before editing ingredients", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RecetteEditorActivity.this, IngredientActivity.class);
                    intent.putExtra("TableType", PLACARDS_TABLE_ID);
                    intent.putExtra("CheckedList", mCheckedPlacards);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, PLACARDS_TABLE_ID);
                    }
                }
            }
        });

        mSelectEpicesButton = (Button) findViewById(R.id.select_epices_button);
        mSelectEpicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentRecetteUri == null) {
                    Toast.makeText(RecetteEditorActivity.this, "Create the recette before editing ingredients", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RecetteEditorActivity.this, IngredientActivity.class);
                    intent.putExtra("TableType", EPICES_TABLE_ID);
                    intent.putExtra("CheckedList", mCheckedEpices);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, EPICES_TABLE_ID);
                    }
                }
            }
        });

        // If the intent DOES NOT contain a recipe content URI, then we know that we are
        // creating a new recipe.
        if (mCurrentRecetteUri == null) {
            // This is a new recipe, so change the app bar to say "Add a Recipe"
            setTitle(getString(R.string.editor_activity_title_new_recipe));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a recipe that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing recipe, so change app bar to say "Edit Recipe"
            setTitle(getString(R.string.editor_activity_title_edit_recipe));
            // Initialize a loader to read the recipe data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_RECETTE_LOADER, null, this);
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mSelectFruitLegButton.setOnTouchListener(mTouchListener);
        mSelectFrigoButton.setOnTouchListener(mTouchListener);
        mSelectCongeloButton.setOnTouchListener(mTouchListener);
        mSelectPlacardsButton.setOnTouchListener(mTouchListener);
        mSelectEpicesButton.setOnTouchListener(mTouchListener);
    }

    // This method is called when the ingredient activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FRUILEG_TABLE_ID:
                    // get String data from Intent
                    String listFruitLegString = data.getStringExtra("itemsListedString");
                    mCheckedFruitLeg = data.getStringExtra("itemsCheckedString");
                    // Update ingredient db
                    updateIngredient(listFruitLegString, mCheckedFruitLeg, FRUILEG_TABLE_ID);
                    String nameFruitLegString = data.getStringExtra("nameString");
                    mIngredientFruitLegTextView.setText(nameFruitLegString.replace(",", ", ").replace("_", " "));
                    break;
                case FRIGO_TABLE_ID:
                    // get String data from Intent
                    String listFrigoString = data.getStringExtra("itemsListedString");
                    mCheckedFrigo = data.getStringExtra("itemsCheckedString");
                    // Update ingredient db
                    updateIngredient(listFrigoString, mCheckedFrigo, FRIGO_TABLE_ID);
                    String nameFrigoString = data.getStringExtra("nameString");
                    mIngredientFrigoTextView.setText(nameFrigoString.replace(",", ", ").replace("_", " "));
                    break;
                case CONGELO_TABLE_ID:
                    // get String data from Intent
                    String listCongeloString = data.getStringExtra("itemsListedString");
                    mCheckedCongelo = data.getStringExtra("itemsCheckedString");
                    // Update ingredient db
                    updateIngredient(listCongeloString, mCheckedCongelo, CONGELO_TABLE_ID);
                    String nameCongeloString = data.getStringExtra("nameString");
                    mIngredientCongeloTextView.setText(nameCongeloString.replace(",", ", ").replace("_", " "));
                    break;
                case PLACARDS_TABLE_ID:
                    // get String data from Intent
                    String listPlacardsString = data.getStringExtra("itemsListedString");
                    mCheckedPlacards = data.getStringExtra("itemsCheckedString");
                    // Update ingredient db
                    updateIngredient(listPlacardsString, mCheckedPlacards, PLACARDS_TABLE_ID);
                    String namePlacardsString = data.getStringExtra("nameString");
                    mIngredientPlacardsTextView.setText(namePlacardsString.replace(",", ", ").replace("_", " "));
                    break;
                case EPICES_TABLE_ID:
                    // get String data from Intent
                    String listEpicesString = data.getStringExtra("itemsListedString");
                    mCheckedEpices = data.getStringExtra("itemsCheckedString");
                    // Update ingredient db
                    updateIngredient(listEpicesString, mCheckedEpices, EPICES_TABLE_ID);
                    String nameEpicesString = data.getStringExtra("nameString");
                    mIngredientEpicesTextView.setText(nameEpicesString.replace(",", ", ").replace("_", " "));
                    break;
            }
        }
    }

    // Method called when returning from the ingredient activity to update the food db accordingly with their selection or not.
    public void updateIngredient(String listString, String checkedString, int tableId){
        Uri recetteUri = Uri.parse(mCurrentRecetteUri.toString());
        String[] segments = recetteUri.getPath().split("/");
        // Parse the id of the food to update
        String idRecetteString = segments[segments.length-1];
        String[] listItems = listString.split(",");
        String[] checkedItems = checkedString.split(",");
        switch (tableId){
            case FRUILEG_TABLE_ID:
                for (int j = 0; j < listItems.length; j++) {
                    ContentValues values = new ContentValues();
                    String[] projectionFruitLeg = {FruitsLegumesEntry._ID, FruitsLegumesEntry.COLUMN_RECETTE_NAME};
                    String selectionFruitsLegumes = FruitsLegumesEntry._ID + "=?";
                    // Handle the case where the food is in the list and is checked
                    if (!listItems[j].equals("-1") && checkedItems[j].equals("1")) {
                        String[] selectionArgsFruitsLegumes = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorFruitLeg = getContentResolver().query(FruitsLegumesEntry.CONTENT_URI, projectionFruitLeg, selectionFruitsLegumes, selectionArgsFruitsLegumes, null);
                        if (cursorFruitLeg.moveToFirst()) {
                            int recetteNameFruitLegColumnIndex = cursorFruitLeg.getColumnIndex(FruitsLegumesEntry.COLUMN_RECETTE_NAME);
                            String recetteNameFruitLegString = cursorFruitLeg.getString(recetteNameFruitLegColumnIndex);
                            List<String> recetteNameFruitLeg = Arrays.asList(recetteNameFruitLegString.split(",", -1));
                            // Update the recetteName List to be transform into a String for the db
                            recetteNameFruitLeg.set(Integer.parseInt(idRecetteString) -1, mNameEditText.getText().toString().replace(" ", "_").trim());
                            values.put(FruitsLegumesEntry.COLUMN_RECETTE_NAME, recetteNameFruitLeg.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        // Don't forget to close the cursor when finish
                        cursorFruitLeg.close();
                        // Update the db with the new values
                        getContentResolver().update(FruitsLegumesEntry.CONTENT_URI, values, selectionFruitsLegumes, selectionArgsFruitsLegumes);
                        // Handle the case where the food is in the list but is not selected (even if it was the case before
                    } else if (!listItems[j].equals("-1") && checkedItems[j].equals("0")){
                        String[] selectionArgsFruitsLegumes = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorFruitLeg = getContentResolver().query(FruitsLegumesEntry.CONTENT_URI, projectionFruitLeg, selectionFruitsLegumes, selectionArgsFruitsLegumes, null);
                        if (cursorFruitLeg.moveToFirst()) {
                            int recetteNameFruitLegColumnIndex = cursorFruitLeg.getColumnIndex(FruitsLegumesEntry.COLUMN_RECETTE_NAME);
                            String recetteNameFruitLegString = cursorFruitLeg.getString(recetteNameFruitLegColumnIndex);
                            List<String> recetteNameFruitLeg = Arrays.asList(recetteNameFruitLegString.split(",", -1));
                            // Put empty string into the List and update the food db
                            recetteNameFruitLeg.set(Integer.parseInt(idRecetteString) -1, "");
                            values.put(FruitsLegumesEntry.COLUMN_RECETTE_NAME, recetteNameFruitLeg.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorFruitLeg.close();
                        getContentResolver().update(FruitsLegumesEntry.CONTENT_URI, values, selectionFruitsLegumes, selectionArgsFruitsLegumes);
                    }
                }
                break;
            case FRIGO_TABLE_ID:
                for (int j = 0; j < listItems.length; j++) {
                    ContentValues values = new ContentValues();
                    String[] projectionFrigo = {FrigoEntry._ID, FrigoEntry.COLUMN_RECETTE_NAME};
                    String selectionFrigo = FrigoEntry._ID + "=?";
                    if (!listItems[j].equals("-1") && checkedItems[j].equals("1")) {
                        String[] selectionArgsFrigo = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorFrigo = getContentResolver().query(FrigoEntry.CONTENT_URI, projectionFrigo, selectionFrigo, selectionArgsFrigo, null);
                        if (cursorFrigo.moveToFirst()) {
                            int recetteNameFrigoColumnIndex = cursorFrigo.getColumnIndex(FrigoEntry.COLUMN_RECETTE_NAME);
                            String recetteNameFrigoString = cursorFrigo.getString(recetteNameFrigoColumnIndex);
                            List<String> recetteNameFrigo = Arrays.asList(recetteNameFrigoString.split(",", -1));
                            recetteNameFrigo.set(Integer.parseInt(idRecetteString) -1, mNameEditText.getText().toString().replace(" ", "_").trim());
                            values.put(FrigoEntry.COLUMN_RECETTE_NAME, recetteNameFrigo.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorFrigo.close();
                        getContentResolver().update(FrigoEntry.CONTENT_URI, values, selectionFrigo, selectionArgsFrigo);
                    } else if (!listItems[j].equals("-1") && checkedItems[j].equals("0")){
                        String[] selectionArgsFrigo = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorFrigo = getContentResolver().query(FrigoEntry.CONTENT_URI, projectionFrigo, selectionFrigo, selectionArgsFrigo, null);
                        if (cursorFrigo.moveToFirst()) {
                            int recetteNameFrigoColumnIndex = cursorFrigo.getColumnIndex(FrigoEntry.COLUMN_RECETTE_NAME);
                            String recetteNameFrigoString = cursorFrigo.getString(recetteNameFrigoColumnIndex);
                            List<String> recetteNameFrigo = Arrays.asList(recetteNameFrigoString.split(",", -1));
                            recetteNameFrigo.set(Integer.parseInt(idRecetteString) -1, "");
                            values.put(FrigoEntry.COLUMN_RECETTE_NAME, recetteNameFrigo.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorFrigo.close();
                        getContentResolver().update(FrigoEntry.CONTENT_URI, values, selectionFrigo, selectionArgsFrigo);
                    }
                }
                break;
            case CONGELO_TABLE_ID:
                for (int j = 0; j < listItems.length; j++) {
                    ContentValues values = new ContentValues();
                    String[] projectionCongelo = {CongeloEntry._ID, CongeloEntry.COLUMN_RECETTE_NAME};
                    String selectionCongelo = CongeloEntry._ID + "=?";
                    if (!listItems[j].equals("-1") && checkedItems[j].equals("1")) {
                        String[] selectionArgsCongelo = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorCongelo = getContentResolver().query(CongeloEntry.CONTENT_URI, projectionCongelo, selectionCongelo, selectionArgsCongelo, null);
                        if (cursorCongelo.moveToFirst()) {
                            int recetteNameCongeloColumnIndex = cursorCongelo.getColumnIndex(CongeloEntry.COLUMN_RECETTE_NAME);
                            String recetteNameCongeloString = cursorCongelo.getString(recetteNameCongeloColumnIndex);
                            List<String> recetteNameCongelo = Arrays.asList(recetteNameCongeloString.split(",", -1));
                            recetteNameCongelo.set(Integer.parseInt(idRecetteString) -1, mNameEditText.getText().toString().replace(" ", "_").trim());
                            values.put(CongeloEntry.COLUMN_RECETTE_NAME, recetteNameCongelo.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorCongelo.close();
                        getContentResolver().update(CongeloEntry.CONTENT_URI, values, selectionCongelo, selectionArgsCongelo);
                    } else if (!listItems[j].equals("-1") && checkedItems[j].equals("0")){
                        String[] selectionArgsCongelo = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorCongelo = getContentResolver().query(CongeloEntry.CONTENT_URI, projectionCongelo, selectionCongelo, selectionArgsCongelo, null);
                        if (cursorCongelo.moveToFirst()) {
                            int recetteNameCongeloColumnIndex = cursorCongelo.getColumnIndex(CongeloEntry.COLUMN_RECETTE_NAME);
                            String recetteNameCongeloString = cursorCongelo.getString(recetteNameCongeloColumnIndex);
                            List<String> recetteNameCongelo = Arrays.asList(recetteNameCongeloString.split(",", -1));
                            recetteNameCongelo.set(Integer.parseInt(idRecetteString) -1, "");
                            values.put(CongeloEntry.COLUMN_RECETTE_NAME, recetteNameCongelo.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorCongelo.close();
                        getContentResolver().update(CongeloEntry.CONTENT_URI, values, selectionCongelo, selectionArgsCongelo);
                    }
                }
                break;
            case PLACARDS_TABLE_ID:
                for (int j = 0; j < listItems.length; j++) {
                    ContentValues values = new ContentValues();
                    String[] projectionPlacards = {PlacardsEntry._ID, PlacardsEntry.COLUMN_RECETTE_NAME};
                    String selectionPlacards = PlacardsEntry._ID + "=?";
                    if (!listItems[j].equals("-1") && checkedItems[j].equals("1")) {
                        String[] selectionArgsPlacards = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorPlacards = getContentResolver().query(PlacardsEntry.CONTENT_URI, projectionPlacards, selectionPlacards, selectionArgsPlacards, null);
                        if (cursorPlacards.moveToFirst()) {
                            int recetteNamePlacardsColumnIndex = cursorPlacards.getColumnIndex(PlacardsEntry.COLUMN_RECETTE_NAME);
                            String recetteNamePlacardsString = cursorPlacards.getString(recetteNamePlacardsColumnIndex);
                            List<String> recetteNamePlacards = Arrays.asList(recetteNamePlacardsString.split(",", -1));
                            recetteNamePlacards.set(Integer.parseInt(idRecetteString) -1, mNameEditText.getText().toString().replace(" ", "_").trim());
                            values.put(PlacardsEntry.COLUMN_RECETTE_NAME, recetteNamePlacards.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorPlacards.close();
                        getContentResolver().update(PlacardsEntry.CONTENT_URI, values, selectionPlacards, selectionArgsPlacards);
                    } else if (!listItems[j].equals("-1") && checkedItems[j].equals("0")){
                        String[] selectionArgsPlacards = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorPlacards = getContentResolver().query(PlacardsEntry.CONTENT_URI, projectionPlacards, selectionPlacards, selectionArgsPlacards, null);
                        if (cursorPlacards.moveToFirst()) {
                            int recetteNamePlacardsColumnIndex = cursorPlacards.getColumnIndex(PlacardsEntry.COLUMN_RECETTE_NAME);
                            String recetteNamePlacardsString = cursorPlacards.getString(recetteNamePlacardsColumnIndex);
                            List<String> recetteNamePlacards = Arrays.asList(recetteNamePlacardsString.split(",", -1));
                            recetteNamePlacards.set(Integer.parseInt(idRecetteString) -1, "");
                            values.put(PlacardsEntry.COLUMN_RECETTE_NAME, recetteNamePlacards.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorPlacards.close();
                        getContentResolver().update(PlacardsEntry.CONTENT_URI, values, selectionPlacards, selectionArgsPlacards);
                    }
                }
                break;
            case EPICES_TABLE_ID:
                for (int j = 0; j < listItems.length; j++) {
                    ContentValues values = new ContentValues();
                    String[] projectionEpices = {EpicesEntry._ID, EpicesEntry.COLUMN_RECETTE_NAME};
                    String selectionEpices = EpicesEntry._ID + "=?";
                    if (!listItems[j].equals("-1") && checkedItems[j].equals("1")) {
                        String[] selectionArgsEpices = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorEpices = getContentResolver().query(EpicesEntry.CONTENT_URI, projectionEpices, selectionEpices, selectionArgsEpices, null);
                        if (cursorEpices.moveToFirst()) {
                            int recetteNameEpicesColumnIndex = cursorEpices.getColumnIndex(EpicesEntry.COLUMN_RECETTE_NAME);
                            String recetteNameEpicesString = cursorEpices.getString(recetteNameEpicesColumnIndex);
                            List<String> recetteNameEpices = Arrays.asList(recetteNameEpicesString.split(",", -1));
                            recetteNameEpices.set(Integer.parseInt(idRecetteString) -1, mNameEditText.getText().toString().replace(" ", "_").trim());
                            values.put(EpicesEntry.COLUMN_RECETTE_NAME, recetteNameEpices.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorEpices.close();
                        getContentResolver().update(EpicesEntry.CONTENT_URI, values, selectionEpices, selectionArgsEpices);
                    } else if (!listItems[j].equals("-1") && checkedItems[j].equals("0")){
                        String[] selectionArgsEpices = new String[]{String.valueOf(listItems[j])};
                        Cursor cursorEpices = getContentResolver().query(EpicesEntry.CONTENT_URI, projectionEpices, selectionEpices, selectionArgsEpices, null);
                        if (cursorEpices.moveToFirst()) {
                            int recetteNameEpicesColumnIndex = cursorEpices.getColumnIndex(EpicesEntry.COLUMN_RECETTE_NAME);
                            String recetteNameEpicesString = cursorEpices.getString(recetteNameEpicesColumnIndex);
                            List<String> recetteNameEpices = Arrays.asList(recetteNameEpicesString.split(",", -1));
                            recetteNameEpices.set(Integer.parseInt(idRecetteString) -1, "");
                            values.put(EpicesEntry.COLUMN_RECETTE_NAME, recetteNameEpices.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
                        }
                        cursorEpices.close();
                        getContentResolver().update(EpicesEntry.CONTENT_URI, values, selectionEpices, selectionArgsEpices);
                    }
                }
                break;
        }
    }

    /**
     * Get user input from editor and save recette into database.
     */
    private boolean saveRecette() {
        // Crete a List of String of 0 values to put into a new recipe
        List<String> checkedItems = new ArrayList<>(Collections.nCopies( 50, "0"));
        // Create a ContentValues object where column names are the keys,
        // and recipe attributes from the editor are the values.
        ContentValues values = new ContentValues();

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String fruitlegIngredientString = mIngredientFruitLegTextView.getText().toString();
        String frigoIngredientString = mIngredientFrigoTextView.getText().toString();
        String congeloIngredientString = mIngredientCongeloTextView.getText().toString();
        String placardsIngredientString = mIngredientPlacardsTextView.getText().toString();
        String epicesIngredientString = mIngredientEpicesTextView.getText().toString();

        // Check if this is supposed to be a new food
        // and check if all the fields in the editor are blank
        if (mCurrentRecetteUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(fruitlegIngredientString) &&
                TextUtils.isEmpty(frigoIngredientString) && TextUtils.isEmpty(congeloIngredientString) &&
                TextUtils.isEmpty(placardsIngredientString) && TextUtils.isEmpty(epicesIngredientString)) {
            // Since no fields were modified, we can return early without creating a new recipe.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return true;
        }

        // Inform the user that the name must be filled
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.recipe_name_required), Toast.LENGTH_SHORT).show();
            return false;
        }
        // Put the name value in the ContentValues
        values.put(RecetteEntry.COLUMN_NAME, nameString);

        // If the fruitleg ingredient is not provided put "No ingredient selected" in the database
        if (TextUtils.isEmpty(fruitlegIngredientString)) {
            fruitlegIngredientString = "No ingredient selected";
            values.put(RecetteEntry.COLUMN_INGREDIENT_FRUITLEG, fruitlegIngredientString);
        } else {
            values.put(RecetteEntry.COLUMN_INGREDIENT_FRUITLEG, fruitlegIngredientString);
        }

        // If the frigo ingredient is not provided put "No ingredient selected" in the database
        if (TextUtils.isEmpty(frigoIngredientString)) {
            frigoIngredientString = "No ingredient selected";
            values.put(RecetteEntry.COLUMN_INGREDIENT_FRIGO, frigoIngredientString);
        } else {
            values.put(RecetteEntry.COLUMN_INGREDIENT_FRIGO, frigoIngredientString);
        }

        // If the congelo ingredient is not provided put "No ingredient selected" in the database
        if (TextUtils.isEmpty(congeloIngredientString)) {
            congeloIngredientString = "No ingredient selected";
            values.put(RecetteEntry.COLUMN_INGREDIENT_CONGELO, congeloIngredientString);
        } else {
            values.put(RecetteEntry.COLUMN_INGREDIENT_CONGELO, congeloIngredientString);
        }

        // If the placards ingredient is not provided put "No ingredient selected" in the database
        if (TextUtils.isEmpty(placardsIngredientString)) {
            placardsIngredientString = "No ingredient selected";
            values.put(RecetteEntry.COLUMN_INGREDIENT_PLACARDS, placardsIngredientString);
        } else {
            values.put(RecetteEntry.COLUMN_INGREDIENT_PLACARDS, placardsIngredientString);
        }

        // If the epices ingredient is not provided put "No ingredient selected" in the database
        if (TextUtils.isEmpty(epicesIngredientString)) {
            epicesIngredientString = "No ingredient selected";
            values.put(RecetteEntry.COLUMN_INGREDIENT_EPICES, epicesIngredientString);
        } else {
            values.put(RecetteEntry.COLUMN_INGREDIENT_EPICES, epicesIngredientString);
        }

        // Determine if this is a new or existing recipe by checking if mCurrentRecetteUri is null or not
        if (mCurrentRecetteUri == null) {
            // Put CheckedLists of 0 into the db if this is a new recipe
            values.put(RecetteEntry.COLUMN_CHECKED_FRUITLEG, checkedItems.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
            values.put(RecetteEntry.COLUMN_CHECKED_FRIGO, checkedItems.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
            values.put(RecetteEntry.COLUMN_CHECKED_CONGELO, checkedItems.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
            values.put(RecetteEntry.COLUMN_CHECKED_PLACARDS, checkedItems.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
            values.put(RecetteEntry.COLUMN_CHECKED_EPICES, checkedItems.toString().replaceAll("[\\[.\\].\\s+]", "").trim());
            // This is a NEW recipe, so insert a new recipe into the provider,
            // returning the content URI for the new recipe.
            Uri newUri = getContentResolver().insert(RecetteEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_recipe_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_recipe_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            values.put(RecetteEntry.COLUMN_CHECKED_FRUITLEG, mCheckedFruitLeg);
            values.put(RecetteEntry.COLUMN_CHECKED_FRIGO, mCheckedFrigo);
            values.put(RecetteEntry.COLUMN_CHECKED_CONGELO, mCheckedCongelo);
            values.put(RecetteEntry.COLUMN_CHECKED_PLACARDS, mCheckedPlacards);
            values.put(RecetteEntry.COLUMN_CHECKED_EPICES, mCheckedEpices);
            // Otherwise this is an EXISTING recipe, so update the recipe with content URI: mCurrentRecetteUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentRecetteUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentRecetteUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_recipe_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_recipe_successful), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
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
        // If this is a new recette, hide the "Delete" menu item.
        if (mCurrentRecetteUri == null) {
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
                // Save recipe to database
                if(saveRecette()) {
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
                // If the recipe hasn't changed, continue with navigating up to parent activity
                // which is the {@link ReceteActivity}.
                if (!mRecetteHasChanged) {
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
        // If the recipe hasn't changed, continue with handling back button press
        if (!mRecetteHasChanged) {
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
        // Since the editor shows all recipe attributes, define a projection that contains
        // all columns from the Recette table
        String[] projection = {
                RecetteEntry._ID,
                RecetteEntry.COLUMN_NAME,
                RecetteEntry.COLUMN_INGREDIENT_FRUITLEG,
                RecetteEntry.COLUMN_INGREDIENT_FRIGO,
                RecetteEntry.COLUMN_INGREDIENT_CONGELO,
                RecetteEntry.COLUMN_INGREDIENT_PLACARDS,
                RecetteEntry.COLUMN_INGREDIENT_EPICES,
                RecetteEntry.COLUMN_CHECKED_FRUITLEG,
                RecetteEntry.COLUMN_CHECKED_FRIGO,
                RecetteEntry.COLUMN_CHECKED_CONGELO,
                RecetteEntry.COLUMN_CHECKED_PLACARDS,
                RecetteEntry.COLUMN_CHECKED_EPICES};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentRecetteUri,       // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
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
            // Find the columns of recipe attributes that we're interested in
            int nameRecetteColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_NAME);
            int fruitlegIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_FRUITLEG);
            int frigoIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_FRIGO);
            int congeloIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_CONGELO);
            int placardsIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_PLACARDS);
            int epicesIngredientColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_INGREDIENT_EPICES);
            int fruitlegCheckedColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_CHECKED_FRUITLEG);
            int frigoCheckedColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_CHECKED_FRIGO);
            int congeloCheckedColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_CHECKED_CONGELO);
            int placardsCheckedColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_CHECKED_PLACARDS);
            int epicesCheckedColumnIndex = cursor.getColumnIndex(RecetteEntry.COLUMN_CHECKED_EPICES);

            // Extract out the value from the Cursor for the given column index
            String nameRecette = cursor.getString(nameRecetteColumnIndex);
            String fruitlegIngredient = cursor.getString(fruitlegIngredientColumnIndex);
            String frigoIngredient = cursor.getString(frigoIngredientColumnIndex);
            String congeloIngredient = cursor.getString(congeloIngredientColumnIndex);
            String placardsIngredient = cursor.getString(placardsIngredientColumnIndex);
            String epicesIngredient = cursor.getString(epicesIngredientColumnIndex);
            String fruitlegChecked = cursor.getString(fruitlegCheckedColumnIndex);
            String frigoChecked = cursor.getString(frigoCheckedColumnIndex);
            String congeloChecked = cursor.getString(congeloCheckedColumnIndex);
            String placardsChecked = cursor.getString(placardsCheckedColumnIndex);
            String epicesChecked = cursor.getString(epicesCheckedColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(nameRecette);
            mIngredientFruitLegTextView.setText(fruitlegIngredient);
            mIngredientFrigoTextView.setText(frigoIngredient);
            mIngredientCongeloTextView.setText(congeloIngredient);
            mIngredientPlacardsTextView.setText(placardsIngredient);
            mIngredientEpicesTextView.setText(epicesIngredient);

            // Set the different Checked Variables accordingly to the db
            mCheckedFruitLeg = fruitlegChecked;
            mCheckedFrigo = frigoChecked;
            mCheckedCongelo = congeloChecked;
            mCheckedPlacards = placardsChecked;
            mCheckedEpices = epicesChecked;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mIngredientFruitLegTextView.setText("");
        mIngredientFrigoTextView.setText("");
        mIngredientCongeloTextView.setText("");
        mIngredientPlacardsTextView.setText("");
        mIngredientEpicesTextView.setText("");
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
                // and continue editing the recipe.
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
        builder.setMessage(R.string.delete_recipe_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the recipe.
                deleteRecette();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the recipe.
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
    private void deleteRecette() {
        // Only perform the delete if this is an existing recipe.
        if (mCurrentRecetteUri != null) {
            // Call the ContentResolver to delete the recipe at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPlantUri
            // content URI already identifies the recipe that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentRecetteUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_recipe_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_recipe_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
