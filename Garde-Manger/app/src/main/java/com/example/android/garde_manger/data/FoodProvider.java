package com.example.android.garde_manger.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.garde_manger.data.FoodContract.FruitsLegumesEntry;
import com.example.android.garde_manger.data.FoodContract.FrigoEntry;
import com.example.android.garde_manger.data.FoodContract.CongeloEntry;
import com.example.android.garde_manger.data.FoodContract.PlacardsEntry;
import com.example.android.garde_manger.data.FoodContract.EpicesEntry;

import com.example.android.garde_manger.data.CourseContract.CourseListEntry;

import com.example.android.garde_manger.data.RecetteContract.RecetteEntry;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 *
 * {@link ContentProvider} for Garde-Manger app.
 */
public class FoodProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = FoodProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the Fruits&Legumes table */
    private static final int FRUITSLEGUMES = 100;

    /** URI matcher code for the content URI for a single fruit or legume in the Fruits&Legumes table */
    private static final int FRUITLEGUME_ID = 101;

    /** URI matcher code for the content URI for the Frigo table */
    private static final int FRIGOS = 200;

    /** URI matcher code for the content URI for a single Frigo item in the Frigo table */
    private static final int FRIGO_ID = 201;

    /** URI matcher code for the content URI for the Congelo table */
    private static final int CONGELOS = 300;

    /** URI matcher code for the content URI for a single Congelo item in the Congelo table */
    private static final int CONGELO_ID = 301;

    /** URI matcher code for the content URI for the Placards table */
    private static final int PLACARDS = 400;

    /** URI matcher code for the content URI for a single Placard item in the Placards table */
    private static final int PLACARD_ID = 401;

    /** URI matcher code for the content URI for the Epices table */
    private static final int EPICES = 500;

    /** URI matcher code for the content URI for a single Epice in the Epices table */
    private static final int EPICE_ID = 501;

    /** URI matcher code for the content URI for the CourseList table */
    private static final int COURSES = 600;

    /** URI matcher code for the content URI for a single Course in the CourseList table */
    private static final int COURSE_ID = 601;

    /** URI matcher code for the content URI for the Recette table */
    private static final int RECETTES = 700;

    /** URI matcher code for the content URI for a single Recette in the Recette table */
    private static final int RECETTE_ID = 701;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.garde_manger/fruitlegumes" will map to the
        // integer code {@link #FRUITSLEGUMES}. This URI is used to provide access to MULTIPLE rows
        // of the plants table.
        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_FRUIT_LEGUMES, FRUITSLEGUMES);

        // The content URI of the form "content://com.example.android.garde_manger/fruitlegumes/#" will map to the
        // integer code {@link #FRUITLEGUME_ID}. This URI is used to provide access to ONE single row
        // of the Fruits&Legumes table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.garde_manger/gardemanger/3" matches, but
        // "content://com.example.android.garde_manger/gardemanger" (without a number at the end) doesn't match.
        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_FRUIT_LEGUMES + "/#", FRUITLEGUME_ID);

        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_FRIGO, FRIGOS);
        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_FRIGO + "/#", FRIGO_ID);

        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_CONGELO, CONGELOS);
        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_CONGELO + "/#", CONGELO_ID);

        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_PLACARDS, PLACARDS);
        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_PLACARDS + "/#", PLACARD_ID);

        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_EPICES, EPICES);
        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_EPICES + "/#", EPICE_ID);

        sUriMatcher.addURI(CourseContract.CONTENT_AUTHORITY, CourseContract.PATH_COURSE_LIST, COURSES);
        sUriMatcher.addURI(CourseContract.CONTENT_AUTHORITY, CourseContract.PATH_COURSE_LIST + "/#", COURSE_ID);

        sUriMatcher.addURI(RecetteContract.CONTENT_AUTHORITY, RecetteContract.PATH_RECETTE, RECETTES);
        sUriMatcher.addURI(RecetteContract.CONTENT_AUTHORITY, RecetteContract.PATH_RECETTE + "/#", RECETTE_ID);
    }

    // Database helper object
    private FoodDbHelper mDbHelper;

    // The Database
    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        mDbHelper = new FoodDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        mDb = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FRUITSLEGUMES:
                // For the FRUITSLEGUMES code, query the Fruits&Legumes table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the Fruits&Legumes table.
                cursor = mDb.query(FruitsLegumesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FRUITLEGUME_ID:
                // For the FRUITLEGUME_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.garde_manger/fruitlegumes/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = FruitsLegumesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the fruitlegumes table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor =mDb.query(FruitsLegumesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FRIGOS:
                cursor = mDb.query(FrigoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FRIGO_ID:
                selection = FrigoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = mDb.query(FrigoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CONGELOS:
                cursor = mDb.query(CongeloEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CONGELO_ID:
                selection = CongeloEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = mDb.query(CongeloEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PLACARDS:
                cursor = mDb.query(PlacardsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PLACARD_ID:
                selection = PlacardsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = mDb.query(PlacardsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EPICES:
                cursor = mDb.query(EpicesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EPICE_ID:
                selection = EpicesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = mDb.query(EpicesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSES:
                cursor = mDb.query(CourseListEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSE_ID:
                selection = CourseListEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = mDb.query(CourseListEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case RECETTES:
                cursor = mDb.query(RecetteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case RECETTE_ID:
                selection = RecetteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = mDb.query(RecetteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FRUITSLEGUMES:
                return insertFruitLegume(uri, contentValues);
            case FRIGOS:
                return insertFrigo(uri, contentValues);
            case CONGELOS:
                return insertCongelo(uri, contentValues);
            case PLACARDS:
                return insertPlacard(uri, contentValues);
            case EPICES:
                return insertEpice(uri, contentValues);
            case COURSES:
                return insertCourse(uri, contentValues);
            case RECETTES:
                return insertRecette(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a FruitLegume into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertFruitLegume(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(FruitsLegumesEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Fruit or Legume requires a name");
        }

        String quantity = values.getAsString(FruitsLegumesEntry.COLUMN_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Fruit or Legume requires a quantity");
        }

        Integer scaleType = values.getAsInteger(FruitsLegumesEntry.COLUMN_SCALETYPE);
        if(scaleType == null && !FruitsLegumesEntry.isValidScaleType(scaleType)){
            throw new IllegalArgumentException("Fruit or Legume requires valid scaleType");
        }

        String expiryDate = values.getAsString(FruitsLegumesEntry.COLUMN_EXPIRY_DATE);
        if (expiryDate == null) {
            throw new IllegalArgumentException("Fruit or Legume requires an expiry date");
        }

        Integer isInList = values.getAsInteger(FruitsLegumesEntry.COLUMN_IN_COURSE_LIST);
        if(isInList == null && !FruitsLegumesEntry.isValidInList(isInList)){
            throw new IllegalArgumentException("Fruit or Legume requires valid is in list index");
        }

        String recetteName = values.getAsString(FruitsLegumesEntry.COLUMN_RECETTE_NAME);
        if (recetteName == null) {
            throw new IllegalArgumentException("Fruit or Legume requires a recette name");
        }

        // Get writable database
        mDb = mDbHelper.getWritableDatabase();

        // Insert the new fruitlegumes with the given values
        long id = mDb.insert(FruitsLegumesEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the fruitslegumes content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Close the instance of the Database
        mDb.close();

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a Frigo into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertFrigo(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(FrigoEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Frigo item requires a name");
        }

        String quantity = values.getAsString(FrigoEntry.COLUMN_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Frigo item requires a quantity");
        }

        Integer scaleType = values.getAsInteger(FrigoEntry.COLUMN_SCALETYPE);
        if(scaleType == null && !FrigoEntry.isValidScaleType(scaleType)){
            throw new IllegalArgumentException("Frigo item requires valid scaleType");
        }

        String expiryDate = values.getAsString(FrigoEntry.COLUMN_EXPIRY_DATE);
        if (expiryDate == null) {
            throw new IllegalArgumentException("Frigo item requires an expiry date");
        }

        Integer isInList = values.getAsInteger(FrigoEntry.COLUMN_IN_COURSE_LIST);
        if(isInList == null && !FrigoEntry.isValidInList(isInList)){
            throw new IllegalArgumentException("Frigo item requires valid is in list index");
        }

        String recetteName = values.getAsString(FrigoEntry.COLUMN_RECETTE_NAME);
        if (recetteName == null) {
            throw new IllegalArgumentException("Frigo item requires a recette name");
        }

        // Get writable database
        mDb = mDbHelper.getWritableDatabase();

        // Insert the new Frigo item with the given values
        long id = mDb.insert(FrigoEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Frigo item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Close the instance of the Database
        mDb.close();

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a Congelo into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertCongelo(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(CongeloEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Congelo item requires a name");
        }

        String quantity = values.getAsString(CongeloEntry.COLUMN_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Congelo item requires a quantity");
        }

        Integer scaleType = values.getAsInteger(CongeloEntry.COLUMN_SCALETYPE);
        if(scaleType == null && !CongeloEntry.isValidScaleType(scaleType)){
            throw new IllegalArgumentException("Congelo item requires valid scaleType");
        }

        String expiryDate = values.getAsString(CongeloEntry.COLUMN_EXPIRY_DATE);
        if (expiryDate == null) {
            throw new IllegalArgumentException("Congelo item requires an expiry date");
        }

        Integer isInList = values.getAsInteger(CongeloEntry.COLUMN_IN_COURSE_LIST);
        if(isInList == null && !CongeloEntry.isValidInList(isInList)){
            throw new IllegalArgumentException("Congelo item requires valid is in list index");
        }

        String recetteName = values.getAsString(CongeloEntry.COLUMN_RECETTE_NAME);
        if (recetteName == null) {
            throw new IllegalArgumentException("Congelo item requires a recette name");
        }

        // Get writable database
        mDb = mDbHelper.getWritableDatabase();

        // Insert the new Congelo item with the given values
        long id = mDb.insert(CongeloEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Congelo item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Close the instance of the Database
        mDb.close();

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a Placard into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPlacard(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(PlacardsEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Placard item requires a name");
        }

        String quantity = values.getAsString(PlacardsEntry.COLUMN_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Placard item requires a quantity");
        }

        Integer scaleType = values.getAsInteger(PlacardsEntry.COLUMN_SCALETYPE);
        if(scaleType == null && !PlacardsEntry.isValidScaleType(scaleType)){
            throw new IllegalArgumentException("Placard item requires valid scaleType");
        }

        String expiryDate = values.getAsString(PlacardsEntry.COLUMN_EXPIRY_DATE);
        if (expiryDate == null) {
            throw new IllegalArgumentException("Placard item requires an expiry date");
        }

        Integer isInList = values.getAsInteger(PlacardsEntry.COLUMN_IN_COURSE_LIST);
        if(isInList == null && !PlacardsEntry.isValidInList(isInList)){
            throw new IllegalArgumentException("Placard item requires valid is in list index");
        }

        String recetteName = values.getAsString(PlacardsEntry.COLUMN_RECETTE_NAME);
        if (recetteName == null) {
            throw new IllegalArgumentException("Placard item requires a recette id");
        }

        // Get writable database
        mDb = mDbHelper.getWritableDatabase();

        // Insert the new Placard item with the given values
        long id = mDb.insert(PlacardsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Placard item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Close the instance of the Database
        mDb.close();

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a Epice into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertEpice(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(EpicesEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Epice requires a name");
        }

        String quantity = values.getAsString(EpicesEntry.COLUMN_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Epice requires a quantity");
        }

        Integer scaleType = values.getAsInteger(EpicesEntry.COLUMN_SCALETYPE);
        if(scaleType == null && !EpicesEntry.isValidScaleType(scaleType)){
            throw new IllegalArgumentException("Epice requires valid scaleType");
        }

        String expiryDate = values.getAsString(EpicesEntry.COLUMN_EXPIRY_DATE);
        if (expiryDate == null) {
            throw new IllegalArgumentException("Epice requires an expiry date");
        }

        Integer isInList = values.getAsInteger(EpicesEntry.COLUMN_IN_COURSE_LIST);
        if(isInList == null && !EpicesEntry.isValidInList(isInList)){
            throw new IllegalArgumentException("Epice requires valid is in list index");
        }

        String recetteName = values.getAsString(EpicesEntry.COLUMN_RECETTE_NAME);
        if (recetteName == null) {
            throw new IllegalArgumentException("Epice requires a recette name");
        }

        // Get writable database
        mDb = mDbHelper.getWritableDatabase();

        // Insert the new Epice with the given values
        long id = mDb.insert(EpicesEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Epice content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Close the instance of the Database
        mDb.close();

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a Course into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertCourse(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(CourseListEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Course requires a name");
        }

        String foodUri = values.getAsString(CourseListEntry.COLUMN_FOOD_URI);
        if (foodUri == null) {
            throw new IllegalArgumentException("Course requires an uri");
        }

        String foodQuantity = values.getAsString(CourseListEntry.COLUMN_FOOD_QUANTITY);
        if (foodQuantity == null) {
            throw new IllegalArgumentException("Course requires a quantity");
        }

        // Get writable database
        mDb = mDbHelper.getWritableDatabase();

        // Insert the new Course item with the given values
        long id = mDb.insert(CourseListEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Course item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Close the instance of the Database
        mDb.close();

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a Recette into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertRecette(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(RecetteEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Course requires a name");
        }

        String ingredientFruitLeg = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_FRUITLEG);
        if (ingredientFruitLeg == null) {
            throw new IllegalArgumentException("Recette requires ingredient (FruitLeg)");
        }

        String ingredientFrigo = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_FRIGO);
        if (ingredientFrigo == null) {
            throw new IllegalArgumentException("Recette requires ingredient (Frigo)");
        }

        String ingredientCongelo = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_CONGELO);
        if (ingredientCongelo == null) {
            throw new IllegalArgumentException("Recette requires ingredient (Congelo)");
        }

        String ingredientPlacards = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_PLACARDS);
        if (ingredientPlacards == null) {
            throw new IllegalArgumentException("Recette requires ingredient (Placards)");
        }

        String ingredientEpices = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_EPICES);
        if (ingredientEpices == null) {
            throw new IllegalArgumentException("Recette requires ingredient (Epices)");
        }

        String checkedFruitLeg = values.getAsString(RecetteEntry.COLUMN_CHECKED_FRUITLEG);
        if (checkedFruitLeg == null) {
            throw new IllegalArgumentException("Recette requires checked item (FruitLeg)");
        }

        String checkedFrigo = values.getAsString(RecetteEntry.COLUMN_CHECKED_FRIGO);
        if (checkedFrigo == null) {
            throw new IllegalArgumentException("Recette requires checked item (Frigo)");
        }

        String checkedCongelo = values.getAsString(RecetteEntry.COLUMN_CHECKED_CONGELO);
        if (checkedCongelo == null) {
            throw new IllegalArgumentException("Recette requires checked item (Congelo)");
        }

        String checkedPlacards = values.getAsString(RecetteEntry.COLUMN_CHECKED_PLACARDS);
        if (checkedPlacards == null) {
            throw new IllegalArgumentException("Recette requires checked item (Placards)");
        }

        String checkedEpices = values.getAsString(RecetteEntry.COLUMN_CHECKED_EPICES);
        if (checkedEpices == null) {
            throw new IllegalArgumentException("Recette requires checked item (Epices)");
        }

        // Get writable database
        mDb = mDbHelper.getWritableDatabase();

        // Insert the new Recette item with the given values
        long id = mDb.insert(RecetteEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the Recette item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Close the instance of the Database
        mDb.close();

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FRUITSLEGUMES:
                return updateFruitLegume(uri, contentValues, selection, selectionArgs);
            case FRUITLEGUME_ID:
                // For the FRUITLEGUME_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = FruitsLegumesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateFruitLegume(uri, contentValues, selection, selectionArgs);
            case FRIGOS:
                return updateFrigo(uri, contentValues, selection, selectionArgs);
            case FRIGO_ID:
                selection = FrigoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateFrigo(uri, contentValues, selection, selectionArgs);
            case CONGELOS:
                return updateCongelo(uri, contentValues, selection, selectionArgs);
            case CONGELO_ID:
                selection = CongeloEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCongelo(uri, contentValues, selection, selectionArgs);
            case PLACARDS:
                return updatePlacard(uri, contentValues, selection, selectionArgs);
            case PLACARD_ID:
                selection = PlacardsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePlacard(uri, contentValues, selection, selectionArgs);
            case EPICES:
                return updateEpice(uri, contentValues, selection, selectionArgs);
            case EPICE_ID:
                selection = EpicesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateEpice(uri, contentValues, selection, selectionArgs);
            case COURSES:
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case COURSE_ID:
                selection = CourseListEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case RECETTES:
                return updateRecette(uri, contentValues, selection, selectionArgs);
            case RECETTE_ID:
                selection = RecetteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateRecette(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update fruitslegumes in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more fruitslegumes).
     * Return the number of rows that were successfully updated.
     */
    private int updateFruitLegume(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link FruitsLegumesEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(FruitsLegumesEntry.COLUMN_NAME)) {
            String name = values.getAsString(FruitsLegumesEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Fruit or Legume requires a name");
            }
        }

        // If the {@link FruitsLegumesEntry#COLUMN_QUANTITY} key is present,
        // check that the quantity value is not null.
        if (values.containsKey(FruitsLegumesEntry.COLUMN_QUANTITY)) {
            String quantity = values.getAsString(FruitsLegumesEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Fruit or Legume requires a quantity");
            }
        }

        // If the {@link FruitsLegumesEntry#COLUMN_SCALETYPE} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(FruitsLegumesEntry.COLUMN_SCALETYPE)) {
            Integer scaleType = values.getAsInteger(FruitsLegumesEntry.COLUMN_SCALETYPE);
            if (scaleType == null || !FruitsLegumesEntry.isValidScaleType(scaleType)) {
                throw new IllegalArgumentException("Fruit or Legume requires valid scale type");
            }
        }

        // If the {@link FruitsLegumesEntry#COLUMN_EXPIRY_DATE} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(FruitsLegumesEntry.COLUMN_EXPIRY_DATE)) {
            String expiryDate = values.getAsString(FruitsLegumesEntry.COLUMN_EXPIRY_DATE);
            if (expiryDate == null) {
                throw new IllegalArgumentException("Fruit or Legume requires valid expiry date");
            }
        }

        // If the {@link FruitsLegumesEntry#COLUMN_IN_COURSE_LIST} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(FruitsLegumesEntry.COLUMN_IN_COURSE_LIST)) {
            Integer isInList = values.getAsInteger(FruitsLegumesEntry.COLUMN_IN_COURSE_LIST);
            if (isInList == null || !FruitsLegumesEntry.isValidInList(isInList)) {
                throw new IllegalArgumentException("Fruit or Legume requires valid in course list index");
            }
        }

        // If the {@link FruitsLegumesEntry#COLUMN_RECETTE_ID} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(FruitsLegumesEntry.COLUMN_RECETTE_NAME)) {
            String recetteName = values.getAsString(FruitsLegumesEntry.COLUMN_RECETTE_NAME);
            if (recetteName == null) {
                throw new IllegalArgumentException("Fruit or Legume requires a recette name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        mDb = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = mDb.update(FruitsLegumesEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Close the instance of the Database
        mDb.close();

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update Frigo in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more Frigo).
     * Return the number of rows that were successfully updated.
     */
    private int updateFrigo(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link FrigoEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(FrigoEntry.COLUMN_NAME)) {
            String name = values.getAsString(FrigoEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Frigo item requires a name");
            }
        }

        // If the {@link FrigoEntry#COLUMN_QUANTITY} key is present,
        // check that the quantity value is not null.
        if (values.containsKey(FrigoEntry.COLUMN_QUANTITY)) {
            String quantity = values.getAsString(FrigoEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Frigo item requires a quantity");
            }
        }

        // If the {@link FrigoEntry#COLUMN_SCALETYPE} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(FrigoEntry.COLUMN_SCALETYPE)) {
            Integer scaleType = values.getAsInteger(FrigoEntry.COLUMN_SCALETYPE);
            if (scaleType == null || !FrigoEntry.isValidScaleType(scaleType)) {
                throw new IllegalArgumentException("Frigo item requires valid scale type");
            }
        }

        // If the {@link FrigoEntry#COLUMN_EXPIRY_DATE} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(FrigoEntry.COLUMN_EXPIRY_DATE)) {
            String expiryDate = values.getAsString(FrigoEntry.COLUMN_EXPIRY_DATE);
            if (expiryDate == null) {
                throw new IllegalArgumentException("Frigo item requires valid expiry date");
            }
        }

        // If the {@link FrigoEntry#COLUMN_IN_COURSE_LIST} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(FrigoEntry.COLUMN_IN_COURSE_LIST)) {
            Integer isInList = values.getAsInteger(FrigoEntry.COLUMN_IN_COURSE_LIST);
            if (isInList == null || !FrigoEntry.isValidInList(isInList)) {
                throw new IllegalArgumentException("Frigo item requires valid in course list index");
            }
        }

        // If the {@link FrigoEntry#COLUMN_RECETTE_ID} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(FrigoEntry.COLUMN_RECETTE_NAME)) {
            String recetteName = values.getAsString(FrigoEntry.COLUMN_RECETTE_NAME);
            if (recetteName == null) {
                throw new IllegalArgumentException("Frigo item requires a recette name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        mDb = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = mDb.update(FrigoEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Close the instance of the Database
        mDb.close();

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update Congelo in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more Congelo).
     * Return the number of rows that were successfully updated.
     */
    private int updateCongelo(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link CongeloEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(CongeloEntry.COLUMN_NAME)) {
            String name = values.getAsString(CongeloEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Congelo item requires a name");
            }
        }

        // If the {@link CongeloEntry#COLUMN_QUANTITY} key is present,
        // check that the quantity value is not null.
        if (values.containsKey(CongeloEntry.COLUMN_QUANTITY)) {
            String quantity = values.getAsString(CongeloEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Congelo item requires a quantity");
            }
        }

        // If the {@link CongeloEntry#COLUMN_SCALETYPE} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(CongeloEntry.COLUMN_SCALETYPE)) {
            Integer scaleType = values.getAsInteger(CongeloEntry.COLUMN_SCALETYPE);
            if (scaleType == null || !CongeloEntry.isValidScaleType(scaleType)) {
                throw new IllegalArgumentException("Congelo item requires valid scale type");
            }
        }

        // If the {@link CongeloEntry#COLUMN_EXPIRY_DATE} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(CongeloEntry.COLUMN_EXPIRY_DATE)) {
            String expiryDate = values.getAsString(CongeloEntry.COLUMN_EXPIRY_DATE);
            if (expiryDate == null) {
                throw new IllegalArgumentException("Congelo item requires valid expiry date");
            }
        }

        // If the {@link CongeloEntry#COLUMN_IN_COURSE_LIST} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(CongeloEntry.COLUMN_IN_COURSE_LIST)) {
            Integer isInList = values.getAsInteger(CongeloEntry.COLUMN_IN_COURSE_LIST);
            if (isInList == null || !CongeloEntry.isValidInList(isInList)) {
                throw new IllegalArgumentException("Congelo item requires valid in course list index");
            }
        }

        // If the {@link CongeloEntry#COLUMN_RECETTE_ID} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(CongeloEntry.COLUMN_RECETTE_NAME)) {
            String recetteName = values.getAsString(CongeloEntry.COLUMN_RECETTE_NAME);
            if (recetteName == null) {
                throw new IllegalArgumentException("Congelo item requires a recette name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        mDb = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = mDb.update(CongeloEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Close the instance of the Database
        mDb.close();

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update Placard in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more Placards).
     * Return the number of rows that were successfully updated.
     */
    private int updatePlacard(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PlacardsEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PlacardsEntry.COLUMN_NAME)) {
            String name = values.getAsString(PlacardsEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Placard item requires a name");
            }
        }

        // If the {@link PlacardsEntry#COLUMN_QUANTITY} key is present,
        // check that the quantity value is not null.
        if (values.containsKey(PlacardsEntry.COLUMN_QUANTITY)) {
            String quantity = values.getAsString(PlacardsEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Placard item requires a quantity");
            }
        }

        // If the {@link PlacardsEntry#COLUMN_SCALETYPE} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(PlacardsEntry.COLUMN_SCALETYPE)) {
            Integer scaleType = values.getAsInteger(PlacardsEntry.COLUMN_SCALETYPE);
            if (scaleType == null || !PlacardsEntry.isValidScaleType(scaleType)) {
                throw new IllegalArgumentException("Placard item requires valid scale type");
            }
        }

        // If the {@link PlacardsEntry#COLUMN_EXPIRY_DATE} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(PlacardsEntry.COLUMN_EXPIRY_DATE)) {
            String expiryDate = values.getAsString(PlacardsEntry.COLUMN_EXPIRY_DATE);
            if (expiryDate == null) {
                throw new IllegalArgumentException("Placard item requires valid expiry date");
            }
        }

        // If the {@link PlacardsEntry#COLUMN_IN_COURSE_LIST} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(PlacardsEntry.COLUMN_IN_COURSE_LIST)) {
            Integer isInList = values.getAsInteger(PlacardsEntry.COLUMN_IN_COURSE_LIST);
            if (isInList == null || !PlacardsEntry.isValidInList(isInList)) {
                throw new IllegalArgumentException("Placard item requires valid in course list index");
            }
        }

        // If the {@link PlacardsEntry#COLUMN_RECETTE_ID} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(PlacardsEntry.COLUMN_RECETTE_NAME)) {
            String recetteName = values.getAsString(PlacardsEntry.COLUMN_RECETTE_NAME);
            if (recetteName == null) {
                throw new IllegalArgumentException("Placard item requires a recette name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        mDb = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = mDb.update(PlacardsEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Close the instance of the Database
        mDb.close();

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update Epice in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more Epices).
     * Return the number of rows that were successfully updated.
     */
    private int updateEpice(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link EpicesEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(EpicesEntry.COLUMN_NAME)) {
            String name = values.getAsString(EpicesEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Epice requires a name");
            }
        }

        // If the {@link EpicesEntry#COLUMN_QUANTITY} key is present,
        // check that the quantity value is not null.
        if (values.containsKey(EpicesEntry.COLUMN_QUANTITY)) {
            String quantity = values.getAsString(EpicesEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Epice requires a quantity");
            }
        }

        // If the {@link EpicesEntry#COLUMN_SCALETYPE} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(EpicesEntry.COLUMN_SCALETYPE)) {
            Integer scaleType = values.getAsInteger(EpicesEntry.COLUMN_SCALETYPE);
            if (scaleType == null || !EpicesEntry.isValidScaleType(scaleType)) {
                throw new IllegalArgumentException("Epice requires valid scale type");
            }
        }

        // If the {@link EpicesEntry#COLUMN_EXPIRY_DATE} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(EpicesEntry.COLUMN_EXPIRY_DATE)) {
            String expiryDate = values.getAsString(EpicesEntry.COLUMN_EXPIRY_DATE);
            if (expiryDate == null) {
                throw new IllegalArgumentException("Epice requires valid expiry date");
            }
        }

        // If the {@link EpicesEntry#COLUMN_IN_COURSE_LIST} key is present,
        // check that the scale type value is valid.
        if (values.containsKey(EpicesEntry.COLUMN_IN_COURSE_LIST)) {
            Integer isInList = values.getAsInteger(EpicesEntry.COLUMN_IN_COURSE_LIST);
            if (isInList == null || !EpicesEntry.isValidInList(isInList)) {
                throw new IllegalArgumentException("Epice requires valid in course list index");
            }
        }

        // If the {@link EpicesEntry#COLUMN_RECETTE_ID} key is present,
        // check that the expiry date value is valid.
        if (values.containsKey(EpicesEntry.COLUMN_RECETTE_NAME)) {
            String recetteName = values.getAsString(EpicesEntry.COLUMN_RECETTE_NAME);
            if (recetteName == null) {
                throw new IllegalArgumentException("Epice requires a recette name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        mDb = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = mDb.update(EpicesEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Close the instance of the Database
        mDb.close();

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update Course in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more Courses).
     * Return the number of rows that were successfully updated.
     */
    private int updateCourse(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link CourseListEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(CourseListEntry.COLUMN_NAME)) {
            String name = values.getAsString(CourseListEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Course requires a name");
            }
        }

        // If the {@link CourseListEntry#COLUMN_FOOD_URI} key is present,
        // check that the name value is not null.
        if (values.containsKey(CourseListEntry.COLUMN_FOOD_URI)) {
            String foodUri = values.getAsString(CourseListEntry.COLUMN_FOOD_URI);
            if (foodUri == null) {
                throw new IllegalArgumentException("Course requires an uri");
            }
        }

        // If the {@link CourseListEntry#COLUMN_FOOD_QUANTITY} key is present,
        // check that the name value is not null.
        if (values.containsKey(CourseListEntry.COLUMN_FOOD_QUANTITY)) {
            String foodQuantity = values.getAsString(CourseListEntry.COLUMN_FOOD_QUANTITY);
            if (foodQuantity == null) {
                throw new IllegalArgumentException("Course requires a quantity");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        mDb = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = mDb.update(CourseListEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Close the instance of the Database
        mDb.close();

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Update Recette in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more Courses).
     * Return the number of rows that were successfully updated.
     */
    private int updateRecette(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link RecetteEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_NAME)) {
            String name = values.getAsString(RecetteEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Course requires a name");
            }
        }

        // If the {@link RecetteEntry#COLUMN_INGREDIENT_FRUITLEG} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_INGREDIENT_FRUITLEG)) {
            String ingredientFruitLeg = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_FRUITLEG);
            if (ingredientFruitLeg == null) {
                throw new IllegalArgumentException("Recette requires ingredient (FruitLeg)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_INGREDIENT_FRIGO} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_INGREDIENT_FRIGO)) {
            String ingredientFrigo = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_FRIGO);
            if (ingredientFrigo == null) {
                throw new IllegalArgumentException("Recette requires ingredient (Frigo)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_INGREDIENT_CONGELO} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_INGREDIENT_CONGELO)) {
            String ingredientCongelo = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_CONGELO);
            if (ingredientCongelo == null) {
                throw new IllegalArgumentException("Recette requires ingredient (Congelo)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_INGREDIENT_PLACARDS} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_INGREDIENT_PLACARDS)) {
            String ingredientPlacards = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_PLACARDS);
            if (ingredientPlacards == null) {
                throw new IllegalArgumentException("Recette requires ingredient (Placards)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_INGREDIENT_EPICES} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_INGREDIENT_EPICES)) {
            String ingredientEpices = values.getAsString(RecetteEntry.COLUMN_INGREDIENT_EPICES);
            if (ingredientEpices == null) {
                throw new IllegalArgumentException("Recette requires ingredient (Epices)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_CHECKED_FRUITLEG} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_CHECKED_FRUITLEG)) {
            String checkedFruitLeg = values.getAsString(RecetteEntry.COLUMN_CHECKED_FRUITLEG);
            if (checkedFruitLeg == null) {
                throw new IllegalArgumentException("Recette requires checked item (FruitLeg)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_CHECKED_FRIGO} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_CHECKED_FRIGO)) {
            String checkedFrigo = values.getAsString(RecetteEntry.COLUMN_CHECKED_FRIGO);
            if (checkedFrigo == null) {
                throw new IllegalArgumentException("Recette requires checked item (Frigo)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_CHECKED_CONGELO} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_CHECKED_CONGELO)) {
            String checkedCongelo = values.getAsString(RecetteEntry.COLUMN_CHECKED_CONGELO);
            if (checkedCongelo == null) {
                throw new IllegalArgumentException("Recette requires checked item (Congelo)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_CHECKED_PLACARDS} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_CHECKED_PLACARDS)) {
            String checkedPlacards = values.getAsString(RecetteEntry.COLUMN_CHECKED_PLACARDS);
            if (checkedPlacards == null) {
                throw new IllegalArgumentException("Recette requires checked item (Placards)");
            }
        }

        // If the {@link RecetteEntry#COLUMN_CHECKED_EPICES} key is present,
        // check that the name value is not null.
        if (values.containsKey(RecetteEntry.COLUMN_CHECKED_EPICES)) {
            String checkedEpices = values.getAsString(RecetteEntry.COLUMN_CHECKED_EPICES);
            if (checkedEpices == null) {
                throw new IllegalArgumentException("Recette requires checked item (Epices)");
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        mDb = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = mDb.update(RecetteEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Close the instance of the Database
        mDb.close();

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        mDb = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FRUITSLEGUMES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = mDb.delete(FruitsLegumesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FRUITLEGUME_ID:
                // Delete a single row given by the ID in the URI
                selection = FruitsLegumesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = mDb.delete(FruitsLegumesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FRIGOS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = mDb.delete(FrigoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FRIGO_ID:
                // Delete a single row given by the ID in the URI
                selection = FrigoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = mDb.delete(FrigoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CONGELOS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = mDb.delete(CongeloEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CONGELO_ID:
                // Delete a single row given by the ID in the URI
                selection = CongeloEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = mDb.delete(CongeloEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLACARDS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = mDb.delete(PlacardsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLACARD_ID:
                // Delete a single row given by the ID in the URI
                selection = PlacardsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = mDb.delete(PlacardsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EPICES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = mDb.delete(EpicesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EPICE_ID:
                // Delete a single row given by the ID in the URI
                selection = EpicesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = mDb.delete(EpicesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = mDb.delete(CourseListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE_ID:
                // Delete a single row given by the ID in the URI
                selection = CourseListEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = mDb.delete(CourseListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECETTES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = mDb.delete(RecetteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECETTE_ID:
                // Delete a single row given by the ID in the URI
                selection = RecetteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = mDb.delete(RecetteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Close the instance of the Database
        mDb.close();

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FRUITSLEGUMES:
                return FruitsLegumesEntry.CONTENT_LIST_TYPE;
            case FRUITLEGUME_ID:
                return FruitsLegumesEntry.CONTENT_ITEM_TYPE;
            case FRIGOS:
                return FrigoEntry.CONTENT_LIST_TYPE;
            case FRIGO_ID:
                return FrigoEntry.CONTENT_ITEM_TYPE;
            case CONGELOS:
                return CongeloEntry.CONTENT_LIST_TYPE;
            case CONGELO_ID:
                return CongeloEntry.CONTENT_ITEM_TYPE;
            case PLACARDS:
                return PlacardsEntry.CONTENT_LIST_TYPE;
            case PLACARD_ID:
                return PlacardsEntry.CONTENT_ITEM_TYPE;
            case EPICES:
                return EpicesEntry.CONTENT_LIST_TYPE;
            case EPICE_ID:
                return EpicesEntry.CONTENT_ITEM_TYPE;
            case COURSES:
                return CourseListEntry.CONTENT_LIST_TYPE;
            case COURSE_ID:
                return CourseListEntry.CONTENT_ITEM_TYPE;
            case RECETTES:
                return RecetteEntry.CONTENT_LIST_TYPE;
            case RECETTE_ID:
                return RecetteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
