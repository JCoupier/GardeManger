package com.example.android.garde_manger.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
 * Database helper for Garde-Manger app. Manages database creation and version management.
 */
public class FoodDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "gardemanger.db";

    /** Database version. If you change the database schema, you must increment the database version. */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link FoodDbHelper}.
     *
     * @param context of the app
     */
    public FoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** This is called when the database is created for the first time. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the Fruits&Legumes table
        String SQL_CREATE_FRUITS_LEGUMES_TABLE =  "CREATE TABLE " + FruitsLegumesEntry.TABLE_NAME + " ("
                + FruitsLegumesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FruitsLegumesEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + FruitsLegumesEntry.COLUMN_QUANTITY + " TEXT NOT NULL DEFAULT 0, "
                + FruitsLegumesEntry.COLUMN_SCALETYPE + " INTEGER NOT NULL, "
                + FruitsLegumesEntry.COLUMN_EXPIRY_DATE + " TEXT, "
                + FruitsLegumesEntry.COLUMN_IN_COURSE_LIST + " INTEGER NOT NULL, "
                + FruitsLegumesEntry.COLUMN_RECETTE_NAME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_FRUITS_LEGUMES_TABLE);

        // Create a String that contains the SQL statement to create the Frigo table
        String SQL_CREATE_FRIGO_TABLE =  "CREATE TABLE " + FrigoEntry.TABLE_NAME + " ("
                + FrigoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FrigoEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + FrigoEntry.COLUMN_QUANTITY + " TEXT NOT NULL DEFAULT 0, "
                + FrigoEntry.COLUMN_SCALETYPE + " INTEGER NOT NULL, "
                + FrigoEntry.COLUMN_EXPIRY_DATE + " TEXT, "
                + FrigoEntry.COLUMN_IN_COURSE_LIST + " INTEGER NOT NULL, "
                + FrigoEntry.COLUMN_RECETTE_NAME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_FRIGO_TABLE);

        // Create a String that contains the SQL statement to create the Congelo table
        String SQL_CREATE_CONGELO_TABLE =  "CREATE TABLE " + CongeloEntry.TABLE_NAME + " ("
                + CongeloEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CongeloEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + CongeloEntry.COLUMN_QUANTITY + " TEXT NOT NULL DEFAULT 0, "
                + CongeloEntry.COLUMN_SCALETYPE + " INTEGER NOT NULL, "
                + CongeloEntry.COLUMN_EXPIRY_DATE + " TEXT, "
                + CongeloEntry.COLUMN_IN_COURSE_LIST + " INTEGER NOT NULL, "
                + CongeloEntry.COLUMN_RECETTE_NAME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CONGELO_TABLE);

        // Create a String that contains the SQL statement to create the Placards table
        String SQL_CREATE_PLACARDS_TABLE =  "CREATE TABLE " + PlacardsEntry.TABLE_NAME + " ("
                + PlacardsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PlacardsEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + PlacardsEntry.COLUMN_QUANTITY + " TEXT NOT NULL DEFAULT 0, "
                + PlacardsEntry.COLUMN_SCALETYPE + " INTEGER NOT NULL, "
                + PlacardsEntry.COLUMN_EXPIRY_DATE + " TEXT, "
                + PlacardsEntry.COLUMN_IN_COURSE_LIST + " INTEGER NOT NULL, "
                + PlacardsEntry.COLUMN_RECETTE_NAME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PLACARDS_TABLE);

        // Create a String that contains the SQL statement to create the Epices table
        String SQL_CREATE_EPICES_TABLE =  "CREATE TABLE " + EpicesEntry.TABLE_NAME + " ("
                + EpicesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EpicesEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + EpicesEntry.COLUMN_QUANTITY + " TEXT NOT NULL DEFAULT 0, "
                + EpicesEntry.COLUMN_SCALETYPE + " INTEGER NOT NULL, "
                + EpicesEntry.COLUMN_EXPIRY_DATE + " TEXT, "
                + EpicesEntry.COLUMN_IN_COURSE_LIST + " INTEGER NOT NULL, "
                + EpicesEntry.COLUMN_RECETTE_NAME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_EPICES_TABLE);

        // Create a String that contains the SQL statement to create the CourseList table
        String SQL_CREATE_COURSES_TABLE =  "CREATE TABLE " + CourseListEntry.TABLE_NAME + " ("
                + CourseListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CourseListEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + CourseListEntry.COLUMN_FOOD_URI + " TEXT NOT NULL, "
                + CourseListEntry.COLUMN_FOOD_QUANTITY + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_COURSES_TABLE);

        // Create a String that contains the SQL statement to create the Recette table
        String SQL_CREATE_RECETTES_TABLE =  "CREATE TABLE " + RecetteEntry.TABLE_NAME + " ("
                + RecetteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RecetteEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_INGREDIENT_FRUITLEG + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_INGREDIENT_FRIGO + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_INGREDIENT_CONGELO + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_INGREDIENT_PLACARDS + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_INGREDIENT_EPICES + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_CHECKED_FRUITLEG + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_CHECKED_FRIGO + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_CHECKED_CONGELO + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_CHECKED_PLACARDS + " TEXT NOT NULL, "
                + RecetteEntry.COLUMN_CHECKED_EPICES + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_RECETTES_TABLE);
    }

    /** This is called when the database needs to be upgraded. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_FL_ENTRIES = "DELETE TABLE IF EXISTS " + FruitsLegumesEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_FL_ENTRIES);
        String SQL_DELETE_FRIGO_ENTRIES = "DELETE TABLE IF EXISTS " + FrigoEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_FRIGO_ENTRIES);
        String SQL_DELETE_CONGELO_ENTRIES = "DELETE TABLE IF EXISTS " + CongeloEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_CONGELO_ENTRIES);
        String SQL_DELETE_PLACARDS_ENTRIES = "DELETE TABLE IF EXISTS " + PlacardsEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_PLACARDS_ENTRIES);
        String SQL_DELETE_EPICES_ENTRIES = "DELETE TABLE IF EXISTS " + EpicesEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_EPICES_ENTRIES);
        String SQL_DELETE_COURSES_ENTRIES = "DELETE TABLE IF EXISTS " + CourseListEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_COURSES_ENTRIES);
        String SQL_DELETE_RECETTES_ENTRIES = "DELETE TABLE IF EXISTS " + RecetteEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_RECETTES_ENTRIES);
        onCreate(db);
    }
}