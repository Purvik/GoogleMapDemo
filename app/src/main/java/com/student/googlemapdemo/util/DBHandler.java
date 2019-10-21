package com.student.googlemapdemo.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.student.googlemapdemo.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "GoogleMapDemo";

    // Contacts table name
    private static final String TABLE_RESTAURANT_DETAIL = "restaurants";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE_NO = "phone_no";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public DBHandler(Context contex) {
        super(contex, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESTAURANT_DETAIL_TABLE = "CREATE TABLE " + TABLE_RESTAURANT_DETAIL + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PHONE_NO + " TEXT ,"
                + KEY_LATITUDE + " DOUBLE ,"
                + KEY_LONGITUDE + " DOUBLE "
                 +")";

        db.execSQL(CREATE_RESTAURANT_DETAIL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT_DETAIL);

        // Create tables again
        onCreate(db);
    }

    // Adding new Student Information
    public void addNewRestaurant(Restaurant restaurant) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, restaurant.getName());
        values.put(KEY_PHONE_NO, restaurant.getPhone());
        values.put(KEY_LATITUDE, restaurant.getLatitude());
        values.put(KEY_LONGITUDE, restaurant.getLongitude());

        // Inserting Row
        db.insert(TABLE_RESTAURANT_DETAIL, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Students
    public List<Restaurant> getAllRestaurantList() {

        List<Restaurant> restaurantList = new ArrayList<Restaurant>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RESTAURANT_DETAIL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Restaurant restaurant = new Restaurant();
                restaurant.setId(Integer.parseInt(cursor.getString(0)));
                restaurant.setName(cursor.getString(1));
                restaurant.setPhone(cursor.getString(2));
                restaurant.setLatitude(Double.parseDouble(cursor.getString(3)));
                restaurant.setLongitude(Double.parseDouble(cursor.getString(4)));

                // Adding contact to list
                restaurantList.add(restaurant);

            } while (cursor.moveToNext());
        }

        // return contact list
        return restaurantList;
    }
}
