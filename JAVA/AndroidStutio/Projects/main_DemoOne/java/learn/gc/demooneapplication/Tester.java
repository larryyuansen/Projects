package learn.gc.demooneapplication;

import android.database.sqlite.SQLiteDatabase;

import com.tencent.mapsdk.raster.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Tester {
    public static void main (String[] args) {
     List<LatLng> list;
     list = new ArrayList<>();
    LatLng latLngA = new LatLng(39.946595,116.387788);
    LatLng latLngB = new LatLng(39.985538,116.448212);
    list.add(latLngA);
    list.add(latLngB);


    }
}
