package com.example.android.garde_manger.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 *
 * API Contract for the Garde-Manger app.
 */
public class FoodContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private FoodContract() {}

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
    public static final String PATH_FRUIT_LEGUMES = "fruitlegumes";
    public static final String PATH_FRIGO = "frigo";
    public static final String PATH_CONGELO = "congelo";
    public static final String PATH_PLACARDS = "placards";
    public static final String PATH_EPICES = "epices";

    /**
     * Inner class that defines constant values for the Fruits&Legumes database table.
     * Each entry in the table represents a single plant product.
     */
    public static final class FruitsLegumesEntry implements BaseColumns {

        /** The content URI to access the Fruits&Legumes data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FRUIT_LEGUMES);

        /** Name of database table for fruits and legumes */
        public final static String TABLE_NAME = "fruitlegumes";

        /**
         * Unique ID number for the Fruits&Legumes(only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Fruits&Legumes.
         * Type: TEXT
         */
        public final static String COLUMN_NAME ="name";

        /**
         * Quantity of the Fruits&Legumes.
         * Type: TEXT
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Scale type of the Fruits&Legumes.
         * Type: INTEGER
         */
        public final static String COLUMN_SCALETYPE = "scale_type";

        /**
         * Expiry date of the Fruits&Legumes.
         * Type: TEXT
         */
        public final static String COLUMN_EXPIRY_DATE = "expiry_date";

        /**
         * Is in course list indicator of the Fruits&Legumes.
         * Type: INTEGER
         */
        public final static String COLUMN_IN_COURSE_LIST = "in_course_list";

        /**
         * Name of the recipes this Fruits&Legumes is in.
         * Type: TEXT
         */
        public final static String COLUMN_RECETTE_NAME = "recette_name";

        /**
         * Possible values for the scaleType of the Fruits&Legumes.
         */
        public static final int SCALE_TYPE_NUMERIC = 1;
        public static final int SCALE_TYPE_CUSTOM = 2;

        /**
         * Possible values for the in_course_list of the Fruits&Legumes.
         */
        public static final int NOT_IN_LIST = 0;
        public static final int IS_IN_LIST = 1;

        /**
         * Returns whether or not the given scaleType is {@link #SCALE_TYPE_NUMERIC}
         * or {@link #SCALE_TYPE_CUSTOM}.
         */
        public static boolean isValidScaleType(int type) {
            if (type == SCALE_TYPE_NUMERIC || type == SCALE_TYPE_CUSTOM) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given is_course_list is {@link #NOT_IN_LIST}
         * or {@link #IS_IN_LIST}.
         */
        public static boolean isValidInList(int type) {
            if (type == NOT_IN_LIST || type == IS_IN_LIST) {
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Fruits&Legumes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRUIT_LEGUMES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Fruits&Legumes.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRUIT_LEGUMES;
    }

    /**
     * Inner class that defines constant values for the Frigo item database table.
     * Each entry in the table represents a single plant product.
     */
    public static final class FrigoEntry implements BaseColumns {

        /** The content URI to access the Frigo data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FRIGO);

        /** Name of database table for frigo item */
        public final static String TABLE_NAME = "frigo";

        /**
         * Unique ID number for the Frigo item(only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Frigo item.
         * Type: TEXT
         */
        public final static String COLUMN_NAME ="name";

        /**
         * Quantity of the Frigo item.
         * Type: TEXT
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Scale type of the Frigo item.
         * Type: INTEGER
         */
        public final static String COLUMN_SCALETYPE = "scale_type";

        /**
         * Expiry date of the Frigo item.
         * Type: TEXT
         */
        public final static String COLUMN_EXPIRY_DATE = "expiry_date";

        /**
         * Is in course list indicator of the Frigo item.
         * Type: INTEGER
         */
        public final static String COLUMN_IN_COURSE_LIST = "in_course_list";

        /**
         * Name of the recipes this Frigo item is in.
         * Type: TEXT
         */
        public final static String COLUMN_RECETTE_NAME = "recette_name";

        /**
         * Possible values for the scaleType of the Frigo item.
         */
        public static final int SCALE_TYPE_NUMERIC = 1;
        public static final int SCALE_TYPE_CUSTOM = 2;

        /**
         * Possible values for the in_course_list of the Frigo item.
         */
        public static final int NOT_IN_LIST = 0;
        public static final int IS_IN_LIST = 1;

        /**
         * Returns whether or not the given scaleType is {@link #SCALE_TYPE_NUMERIC}
         * or {@link #SCALE_TYPE_CUSTOM}.
         */
        public static boolean isValidScaleType(int type) {
            if (type == SCALE_TYPE_NUMERIC || type == SCALE_TYPE_CUSTOM) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given is_course_list is {@link #NOT_IN_LIST}
         * or {@link #IS_IN_LIST}.
         */
        public static boolean isValidInList(int type) {
            if (type == NOT_IN_LIST || type == IS_IN_LIST) {
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Frigo items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRIGO;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Frigo item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FRIGO;
    }

    /**
     * Inner class that defines constant values for the Congelo item database table.
     * Each entry in the table represents a single plant product.
     */
    public static final class CongeloEntry implements BaseColumns {

        /** The content URI to access the Congelo data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONGELO);

        /** Name of database table for Congelo item */
        public final static String TABLE_NAME = "congelo";

        /**
         * Unique ID number for the Congelo item(only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Congelo item.
         * Type: TEXT
         */
        public final static String COLUMN_NAME ="name";

        /**
         * Quantity of the Congelo item.
         * Type: TEXT
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Scale type of the Congelo item.
         * Type: INTEGER
         */
        public final static String COLUMN_SCALETYPE = "scale_type";

        /**
         * Expiry date of the Congelo item.
         * Type: TEXT
         */
        public final static String COLUMN_EXPIRY_DATE = "expiry_date";

        /**
         * Is in course list indicator of the Congelo item.
         * Type: INTEGER
         */
        public final static String COLUMN_IN_COURSE_LIST = "in_course_list";

        /**
         *Name of the recipes this Congelo item is in.
         * Type: TEXT
         */
        public final static String COLUMN_RECETTE_NAME = "recette_name";

        /**
         * Possible values for the scaleType of the Congelo item.
         */
        public static final int SCALE_TYPE_NUMERIC = 1;
        public static final int SCALE_TYPE_CUSTOM = 2;

        /**
         * Possible values for the in_course_list of the Congelo item.
         */
        public static final int NOT_IN_LIST = 0;
        public static final int IS_IN_LIST = 1;

        /**
         * Returns whether or not the given scaleType is {@link #SCALE_TYPE_NUMERIC}
         * or {@link #SCALE_TYPE_CUSTOM}.
         */
        public static boolean isValidScaleType(int type) {
            if (type == SCALE_TYPE_NUMERIC || type == SCALE_TYPE_CUSTOM) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given is_course_list is {@link #NOT_IN_LIST}
         * or {@link #IS_IN_LIST}.
         */
        public static boolean isValidInList(int type) {
            if (type == NOT_IN_LIST || type == IS_IN_LIST) {
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Congelo items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONGELO;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Congelo item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONGELO;
    }

    /**
     * Inner class that defines constant values for the Placards item database table.
     * Each entry in the table represents a single plant product.
     */
    public static final class PlacardsEntry implements BaseColumns {

        /** The content URI to access the Placards item data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLACARDS);

        /** Name of database table for Placards items */
        public final static String TABLE_NAME = "placards";

        /**
         * Unique ID number for the Placards item(only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Placards item.
         * Type: TEXT
         */
        public final static String COLUMN_NAME ="name";

        /**
         * Quantity of the Placards item.
         * Type: TEXT
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Scale type of the Placards item.
         * Type: INTEGER
         */
        public final static String COLUMN_SCALETYPE = "scale_type";

        /**
         * Expiry date of the Placards item.
         * Type: TEXT
         */
        public final static String COLUMN_EXPIRY_DATE = "expiry_date";

        /**
         * Is in course list indicator of the Placards item.
         * Type: INTEGER
         */
        public final static String COLUMN_IN_COURSE_LIST = "in_course_list";

        /**
         * Name of the recipes this Placards item is in.
         * Type: TEXT
         */
        public final static String COLUMN_RECETTE_NAME = "recette_name";

        /**
         * Possible values for the scaleType of the Placards item.
         */
        public static final int SCALE_TYPE_NUMERIC = 1;
        public static final int SCALE_TYPE_CUSTOM = 2;

        /**
         * Possible values for the in_course_list of the Placards item.
         */
        public static final int NOT_IN_LIST = 0;
        public static final int IS_IN_LIST = 1;

        /**
         * Returns whether or not the given scaleType is {@link #SCALE_TYPE_NUMERIC}
         * or {@link #SCALE_TYPE_CUSTOM}.
         */
        public static boolean isValidScaleType(int type) {
            if (type == SCALE_TYPE_NUMERIC || type == SCALE_TYPE_CUSTOM) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given is_course_list is {@link #NOT_IN_LIST}
         * or {@link #IS_IN_LIST}.
         */
        public static boolean isValidInList(int type) {
            if (type == NOT_IN_LIST || type == IS_IN_LIST) {
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Placards item.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACARDS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Placards item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACARDS;
    }

    /**
     * Inner class that defines constant values for the Epices database table.
     * Each entry in the table represents a single plant product.
     */
    public static final class EpicesEntry implements BaseColumns {

        /** The content URI to access the Epices data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EPICES);

        /** Name of database table for Epices */
        public final static String TABLE_NAME = "epices";

        /**
         * Unique ID number for the Epices(only for use in the database table).
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Epices.
         * Type: TEXT
         */
        public final static String COLUMN_NAME ="name";

        /**
         * Quantity of the Epices.
         * Type: TEXT
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Scale type of the Epices.
         * Type: INTEGER
         */
        public final static String COLUMN_SCALETYPE = "scale_type";

        /**
         * Expiry date of the Epices.
         * Type: TEXT
         */
        public final static String COLUMN_EXPIRY_DATE = "expiry_date";

        /**
         * Is in course list indicator of the Epices.
         * Type: INTEGER
         */
        public final static String COLUMN_IN_COURSE_LIST = "in_course_list";

        /**
         * Name of the recipes this Epices is in.
         * Type: TEXT
         */
        public final static String COLUMN_RECETTE_NAME = "recette_name";

        /**
         * Possible values for the scaleType of the Epices.
         */
        public static final int SCALE_TYPE_NUMERIC = 1;
        public static final int SCALE_TYPE_CUSTOM = 2;

        /**
         * Possible values for the in_course_list of the Fruits&Legumes.
         */
        public static final int NOT_IN_LIST = 0;
        public static final int IS_IN_LIST = 1;

        /**
         * Returns whether or not the given scaleType is {@link #SCALE_TYPE_NUMERIC}
         * or {@link #SCALE_TYPE_CUSTOM}.
         */
        public static boolean isValidScaleType(int type) {
            if (type == SCALE_TYPE_NUMERIC || type == SCALE_TYPE_CUSTOM) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given is_course_list is {@link #NOT_IN_LIST}
         * or {@link #IS_IN_LIST}.
         */
        public static boolean isValidInList(int type) {
            if (type == NOT_IN_LIST || type == IS_IN_LIST) {
                return true;
            }
            return false;
        }

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Epices.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EPICES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Epice.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EPICES;
    }
}
