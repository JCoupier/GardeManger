package com.example.android.garde_manger.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 *
 * API Contract for the Garde-Manger app.
 */
public class RecetteContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private RecetteContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.garde_manger";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_RECETTE = "recette";

    /**
     * Inner class that defines constant values for the CourseList database table.
     * Each entry in the table represents a single recette.
     */
    public static final class RecetteEntry implements BaseColumns {

        /** The content URI to access the Recettes data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECETTE);

        /** Name of database table for Recettes */
        public final static String TABLE_NAME = "recette";

        /**
         * Unique ID number for the Recette item(only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_NAME ="name";

        /**
         * FruitLeg Ingredient of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_INGREDIENT_FRUITLEG ="ingredient_fruitleg";

        /**
         * Frigo Ingredient of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_INGREDIENT_FRIGO ="ingredient_frigo";

        /**
         * Congelo Ingredient of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_INGREDIENT_CONGELO ="ingredient_congelo";

        /**
         * Placards Ingredient of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_INGREDIENT_PLACARDS ="ingredient_placards";

        /**
         * Epices Ingredient of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_INGREDIENT_EPICES ="ingredient_epices";

        /**
         * FruitLeg checked of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_CHECKED_FRUITLEG ="checked_fruitleg";

        /**
         * Frigo checked of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_CHECKED_FRIGO ="checked_frigo";

        /**
         * Congelo checked of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_CHECKED_CONGELO ="checked_congelo";

        /**
         * Placards checked of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_CHECKED_PLACARDS ="checked_placards";

        /**
         * Epices checked of the Recette item.
         * Type: TEXT
         */
        public final static String COLUMN_CHECKED_EPICES ="checked_epices";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of CourseList items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECETTE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single CourseList item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECETTE;
    }
}
