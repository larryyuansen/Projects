package learn.gc.demooneapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper
{
    public static final String CREATE_LATLNGCOLLECTOR = "create table LatLngList (" +
            "id integer primary key autoincrement," +
            "countid integer," +
            "listid ingeger," +
            "type text," +
            "lat double," +
            "lng double)";
    public static final String CREATE_COLLECTOR = "create table Collector (" +
            "id integer primary key autoincrement," +
            "collector_name text," +
            "collector_code integer)";

    Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_LATLNGCOLLECTOR);
        db.execSQL(CREATE_COLLECTOR);
        Toast.makeText(mContext, "db created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists LatLngList");
        onCreate(db);
    }
}
