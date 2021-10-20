/*
package learn.gc.demooneapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tencent.mapsdk.raster.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Database
{
    public static MyDatabaseHelper dbHelper;
    public static SQLiteDatabase db;
    public static Cursor cursor;
    private ContentValues values;

    Database(){
        dbHelper = new MyDatabaseHelper(MainActivity.theContext, "LatLngCollector.db", null, 1);
    }

    public static String action;

    public static List<LatLng> findLatLng(int cI){
        List<LatLng> latLngList = new ArrayList<>();
        LatLng latLng;

        db = dbHelper.getWritableDatabase();
        cursor = db.query("LatLngList", null, " countid = ?", new String[]{cI - 1 + ""}, null, null, null, null);
        while (cursor.moveToNext())
        {
            String type = cursor.getString(cursor.getColumnIndex("type"));
            int countid = cursor.getInt(cursor.getColumnIndex("countid"));
            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lng = cursor.getDouble(cursor.getColumnIndex("lng"));
            action = type;
            latLng = new LatLng(lat, lng);
            latLngList.add(latLng);

            Log.e("From DB: id", countid + " Type: " + type + " Lat: " + lat + " Lng: " + lng);
        }

        return latLngList;
    }

    public static String findTotalList (){
        String a = "";

        return a;
    }

    public static int getLast(){
        int i = -1;
        db = dbHelper.getWritableDatabase();
        cursor = db.query("LatLngList", null, null, null, null, null, null, null);
        if (cursor.moveToLast()){
            int countid = cursor.getInt(cursor.getColumnIndex("countid"));
             i = countid+1;
        }
        return i;
    }
}
*/
