package learn.gc.demooneapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.Circle;
import com.tencent.mapsdk.raster.model.CircleOptions;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.mapsdk.raster.model.Polygon;
import com.tencent.mapsdk.raster.model.PolygonOptions;
import com.tencent.mapsdk.raster.model.Polyline;
import com.tencent.mapsdk.raster.model.PolylineOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.Projection;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TencentLocationListener
{
    @Override
    public void onStatusUpdate(String s, int i, String s1)
    {
        String desc = "";
        switch (i)
        {
            case STATUS_DENIED:
                desc = "权限被禁止";
                break;
            case STATUS_DISABLED:
                desc = "模块关闭";
                break;
            case STATUS_ENABLED:
                desc = "模块开启";
                break;
            case STATUS_GPS_AVAILABLE:
                desc = "GPS可用，代表GPS开关打开，且搜星定位成功";
                break;
            case STATUS_GPS_UNAVAILABLE:
                desc = "GPS不可用，可能 gps 权限被禁止或无法成功搜星";
                break;
            case STATUS_LOCATION_SWITCH_OFF:
                desc = "位置信息开关关闭，在android M系统中，此时禁止进行wifi扫描";
                break;
            case STATUS_UNKNOWN:
                break;
        }
        Log.e("location", "location status:" + s + ", " + s1 + " " + desc);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.hide();
        }
        x.view().inject(this);

        initial();
        bindListener();
    }

    @Override
    public void onLocationChanged(TencentLocation tLocation, int i, String s)
    {
        if (i == TencentLocation.ERROR_OK)
        {
            LatLng latLng1 = new LatLng(23.095655, 113.322592);
            LatLng latLng = new LatLng(tLocation.getLatitude(), tLocation.getLongitude());
            if (latLngList.size() == 0)
            {
                latLngList.add(latLng1);
                latLngList.add(latLng);
            } else
            {
                if ((latLngList.get(latLngList.size() - 1).getLatitude() == latLng.getLatitude()) && (latLngList.get(latLngList.size() - 1).getLongitude() == latLng.getLongitude()))
                {
                } else
                {
                    latLngList.add(latLng);
                }
            }

            if (myLocation == null)
            {
                myLocation = tencentMap.addMarker(new MarkerOptions().
                        position(latLng).
                        icon(BitmapDescriptorFactory.fromResource(R.mipmap.here)).
                        anchor(0.5f, 0.5f));
                tencentMap.setCenter(latLng);
                tencentMap.setZoom(14);
            }
            if (accuracy == null)
            {
                accuracy = tencentMap.addCircle(new CircleOptions().
                        center(latLng).
                        radius((double) tLocation.getAccuracy()).
                        fillColor(0x440000ff).
                        strokeWidth(0f));
            }

            myLocation.setPosition(latLng);
            myLocation.setRotation(tLocation.getBearing());
            accuracy.setCenter(latLng);
            accuracy.setRadius(tLocation.getAccuracy());
        }
    }

    private TencentMap tencentMap;
    private MapView mapView;
    private Marker myLocation;
    private Projection projection;

    private TencentLocationManager locationManager;
    private TencentLocationRequest locationRequest;
    private Circle accuracy;

    private ImageButton sLB;
    private Button disButton, areaButton, sLButton, rLgButton, undoButton, clearButton;
    private EditText chooseId;

    public static String action;
    private Marker marker;

    Polyline polyline;
    Polygon polygon;

    List<Marker> markerList;
    List<LatLng> latLngList;
    public static List<LatLng> lineList;
    public static List<LatLng> areaList;

    public static MyDatabaseHelper dbHelper;
    public static SQLiteDatabase db;
    private ContentValues values;
    public static Cursor cursor;

    public Fragment mContext = new Fragment();
    public DataFragment dataFragment = new DataFragment();
    public DatabaseFragment databaseFragment = new DatabaseFragment();
    public FragmentManager fragmentManager;
    public static Context theContext;
    public RadioGroup radioGroup;
    public RadioButton dataRadio;
    public RadioButton databaseRadio;

    public static String dataOutput;

    public void initial()
    {
        mapView = findViewById(R.id.map);
        tencentMap = mapView.getMap();
        locationManager = TencentLocationManager.getInstance(this);
        locationRequest = TencentLocationRequest.create();
        locationRequest.setInterval(3000);
        projection = mapView.getProjection();
        action = "";
        latLngList = new ArrayList<>();

        lineList = new ArrayList<>();
        areaList = new ArrayList<>();
        markerList = new ArrayList<>();

        dbHelper = new MyDatabaseHelper(this, "LatLngCollector.db", null, 1);

        radioGroup = findViewById(R.id.rGroup);
        dataRadio = findViewById(R.id.dataRadio);
        databaseRadio = findViewById(R.id.databaseRadio);
        fragmentManager = getFragmentManager();


        sLB = findViewById(R.id.startGPS);
        disButton = findViewById(R.id.distanceButton);
        areaButton = findViewById(R.id.areaButton);
        sLButton = findViewById(R.id.sLog);
        rLgButton = findViewById(R.id.rLog);
        clearButton = findViewById(R.id.clearMap);
        undoButton = findViewById(R.id.undoButton);
        chooseId = findViewById(R.id.recordid);
        chooseId.setText("0");
        radioGroup.setOnCheckedChangeListener(pageChange);
        theContext = this;

        dataOutput = "";
        dbOutput = new ArrayList<>();
        findLast();

        switchFragment(mContext, databaseFragment);
        switchFragment(mContext, dataFragment);
        setDBOutput();
    }

    RadioGroup.OnCheckedChangeListener pageChange = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
            switch (checkedId)
            {
                case R.id.dataRadio:
                    Toast.makeText(MainActivity.this, "DATA changed", Toast.LENGTH_SHORT).show();
                    switchFragment(mContext, dataFragment);
                    break;
                case R.id.databaseRadio:
                    Toast.makeText(MainActivity.this, "database changed", Toast.LENGTH_SHORT).show();
                    switchFragment(mContext, databaseFragment);
                    break;
                default:
                    break;
            }
        }
    };
    public void bindListener()
    {
        sLB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int error = locationManager.requestLocationUpdates(locationRequest, MainActivity.this);
                String TAG = "location";
                if (latLngList.size() >= 1)
                {
                    tencentMap.setCenter(latLngList.get(latLngList.size() - 1));
                    tencentMap.setZoom(14);
                }
                switch (error)
                {
                    case 0:
                        Log.e(TAG, "成功注册监听器");
                        break;
                    case 1:
                        Log.e(TAG, "设备缺少使用腾讯定位服务需要的基本条件");
                        break;
                    case 2:
                        Log.e(TAG, "manifest 中配置的 key 不正确");
                        break;
                    case 3:
                        Log.e(TAG, "自动加载libtencentloc.so失败");
                        break;
                    default:
                        break;
                }
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.distanceButton:
                        if (action.equals("area"))
                        {
                            Toast.makeText(MainActivity.this, "Please Save Log or Clean All", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            action = "line";
                            Toast.makeText(MainActivity.this, "Find Distance Activated.\n Tab for more point", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.areaButton:
                        if (action.equals("line"))
                        {
                            Toast.makeText(MainActivity.this, "Please Save Log or Clean All", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            action = "area";
                            Toast.makeText(MainActivity.this, "Find Area Activated.\n Tab for more point", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.undoButton:
                        if (action.equals("line"))
                        {
                            if (lineList.size() < 2)
                            {
                                Toast.makeText(MainActivity.this, "work " + lineList.size(), Toast.LENGTH_SHORT).show();
                                return;
                            } else
                            {
                                marker = markerList.get(markerList.size() - 1);
                                marker.remove();
                                lineList.remove(lineList.size() - 1);
                                markerList.remove(markerList.size() - 1);
                                Toast.makeText(MainActivity.this, "work " + lineList.size(), Toast.LENGTH_SHORT).show();
                                drawLine();
                                return;
                            }
                        } else if (action.equals("area"))
                        {
                            if (areaList.size() < 2)
                            {
                                Toast.makeText(MainActivity.this, "work " + lineList.size(), Toast.LENGTH_SHORT).show();
                                if (polygon != null)
                                {
                                    polygon.remove();
                                }
                            } else
                            {
                                marker = markerList.get(markerList.size() - 1);
                                marker.remove();
                                areaList.remove(areaList.size() - 1);
                                markerList.remove(markerList.size() - 1);
                                Toast.makeText(MainActivity.this, "work " + lineList.size(), Toast.LENGTH_SHORT).show();
                                drawArea();
                                return;
                            }
                        }
                        break;
                    case R.id.clearMap:
                        dataOutput = "Data";
                        DataFragment.set();
                        tencentMap.clearAllOverlays();
                        initial();
                        break;
                    case R.id.sLog:
                        saveLog();
                        break;
                    case R.id.rLog:
                        readLog(-1);
                        break;
                    default:
                        break;
                }
            }
        };

        disButton.setOnClickListener(onClickListener);
        areaButton.setOnClickListener(onClickListener);
        undoButton.setOnClickListener(onClickListener);
        clearButton.setOnClickListener(onClickListener);
        sLButton.setOnClickListener(onClickListener);
        rLgButton.setOnClickListener(onClickListener);

        tencentMap.setOnMapClickListener(new TencentMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                if (action.equals("line"))
                {
                    lineList.add(latLng);
                    marker = tencentMap.addMarker(new MarkerOptions().position(latLng).title(markerList.size() + 1 + "\nClick to change"));
                    markerList.add(marker);
                    markerList.get(markerList.size() - 1).showInfoWindow();
                    Toast.makeText(MainActivity.this, "work: " + lineList.size(), Toast.LENGTH_SHORT).show();
                    drawLine();
                    return;
                } else if (action.equals("area"))
                {
                    areaList.add(latLng);
                    marker = tencentMap.addMarker(new MarkerOptions().position(latLng).title(markerList.size() + 1 + "\nClick to change"));
                    markerList.add(marker);
                    markerList.get(markerList.size() - 1).showInfoWindow();
                    Toast.makeText(MainActivity.this, "work: " + areaList.size(), Toast.LENGTH_SHORT).show();
                    drawArea();
                } else if (action.equals("lineA"))
                {
                    lineList.set(need, latLng);
                    markerList.get(need).setPosition(latLng);
                    marker = markerList.get(need);
                    marker.setTitle((need + 1) + "\nClick to change");

                    drawLine();
                    action = "line";
                } else if (action.equals("areaA"))
                {
                    areaList.set(need, latLng);
                    markerList.get(need).setPosition(latLng);
                    marker = markerList.get(need);
                    marker.setTitle((need + 1) + "\nClick to change");

                    drawArea();
                    action = "area";
                } else
                {
                    Toast.makeText(MainActivity.this, "Please Choose A Function", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tencentMap.setOnInfoWindowClickListener(new TencentMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                LatLng latLngOnCliked = marker.getPosition();
                marker.setTitle("Move");
                for (int i = 0; i < markerList.size(); i++)
                {
                    if (markerList.get(i).getPosition() == latLngOnCliked)
                    {
                        need = i;
                        if (action.equals("line"))
                        {
                            action = "lineA";
                        } else if (action.equals("area"))
                        {
                            action = "areaA";
                        }
                    }
                }
            }
        });
    }

    public int need;

    public String findDistance()
    {
        double finalDistance = 0;
        if (lineList.size() > 1)
        {
            for (int i = 1; i < lineList.size(); i++)
            {
                double distance = Math.floor(projection.distanceBetween(lineList.get(i - 1), lineList.get(i)));
                finalDistance += distance;
            }
        }
        String a = "";
        if (finalDistance > 10000)
        {
            double dis = finalDistance / 1000;
            a = dis + "Km";
        } else
        {
            a = finalDistance + "m";
        }
        return a;
    }

    public void drawLine()
    {
        if (lineList.size() > 0)
        {
            if (polyline != null)
            {
                polyline.remove();
                polyline = tencentMap.addPolyline(new PolylineOptions().width(8).color(0xAAFF0000).addAll(lineList));
                String a = "\n";
                for (int z = 0; z < lineList.size(); z++)
                {
                    a += (z + 1) + " Lat: " + lineList.get(z).getLatitude() + " Lng: " + lineList.get(z).getLongitude() + "\n";
                }
                dataOutput = "Total Distance:" + findDistance() + "\n" + a;
                DataFragment.set();
            } else
            {
                polyline = tencentMap.addPolyline(new PolylineOptions().width(8).color(0xAAFF0000).addAll(lineList));
            }
        }
    }

    public String findArea()
    {
        double finalArea = 0;
        if (areaList.size() > 2)
        {
            for (int i = 2; i < areaList.size(); i++)
            {
                double a = Math.floor(projection.distanceBetween(areaList.get(i - 1), areaList.get(i)));
                double b = Math.floor(projection.distanceBetween(areaList.get(i - 1), areaList.get(0)));
                double c = Math.floor(projection.distanceBetween(areaList.get(i), areaList.get(0)));
                double p = ((a + b + c) / 2);
                double area = Math.floor(Math.sqrt(p * (p - a) * (p - b) * (p - c)));
                finalArea += area;
            }
        }

        String a = "";
        if (finalArea > 1000000)
        {
            double area = finalArea / 1000000;
            a = area + "Km2";
        } else
        {
            a = finalArea + "M2";
        }
        return a;
    }

    public void drawArea()
    {
        if (areaList.size() > 2)
        {
            if (polygon != null)
            {
                polygon.remove();
                polygon = tencentMap.addPolygon(new PolygonOptions().addAll(areaList).fillColor(0x66ccff00).strokeColor(0xff00000).strokeWidth(15));
                String a = "\n";
                for (int z = 0; z < areaList.size(); z++)
                {
                    a += (z + 1) + " Lat: " + areaList.get(z).getLatitude() + " Lng: " + areaList.get(z).getLongitude() + "\n";
                }
                dataOutput = "Total Distance:" + findArea() + "\n" + a;
                DataFragment.set();
            } else
            {
                polygon = tencentMap.addPolygon(new PolygonOptions().addAll(areaList).fillColor(0x66ccff00).strokeColor(0xff00000).strokeWidth(15));
                dataOutput = "Total Distance:" + findArea() + "\n";
                DataFragment.set();
            }
        }
    }

    public static int noCollector;

    public void saveLog()
    {
        if (lineList.size() > 0 || areaList.size() > 0)
        {
            db = dbHelper.getWritableDatabase();
            values = new ContentValues();
            if (action.equals("line"))
            {
                for (int i = 0; i < lineList.size(); i++)
                {
                    values.put("type", action);
                    values.put("countid", noCollector);
                    values.put("listid", i);
                    values.put("lat", lineList.get(i).getLatitude());
                    values.put("lng", lineList.get(i).getLongitude());
                    db.insert("LatLngList", null, values);
                }
            } else if (action.equals("area"))
            {
                for (int i = 0; i < areaList.size(); i++)
                {
                    values.put("type", action);
                    values.put("countid", noCollector);
                    values.put("listid", i);
                    values.put("lat", areaList.get(i).getLatitude());
                    values.put("lng", areaList.get(i).getLongitude());
                    db.insert("LatLngList", null, values);
                }
            }
            noCollector++;

/*        values.put("type", "such Type");
        values.put("lat", 10);
        values.put("lng", 20);
        db.insert("LatLngList", null, values);*/
        }
        setDBOutput();
        Toast.makeText(this, "Data Recorded", Toast.LENGTH_SHORT).show();
    }

    public void readLog(int choose)
    {
        int cI;
        if (choose == -1)
        {
            cI = Integer.parseInt(chooseId.getText().toString()) - 1;
        }
        else
            {
                cI = choose;
            }
        db = dbHelper.getWritableDatabase();
        cursor = db.query("LatLngList", null, " countid = ?", new String[]{cI+ ""}, null, null, null, null);

        while (cursor.moveToNext())
        {
            String type = cursor.getString(cursor.getColumnIndex("type"));
            int countid = cursor.getInt(cursor.getColumnIndex("countid"));
            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lng = cursor.getDouble(cursor.getColumnIndex("lng"));

            if (type.equals("line"))
            {
                LatLng latLng = new LatLng(lat, lng);
                lineList.add(latLng);
                drawLine();
            } else if (type.equals("area"))
            {
                LatLng latLng = new LatLng(lat, lng);
                areaList.add(latLng);
                drawArea();
            }
        }
        Toast.makeText(this, "Total Record: " + (noCollector), Toast.LENGTH_SHORT).show();
        setDBOutput();
    }

    public void findLast()
    {
        db = dbHelper.getWritableDatabase();
        cursor = db.query("LatLngList", null, null, null, null, null, null, null);

        if (cursor.moveToLast()){
            int countid = cursor.getInt(cursor.getColumnIndex("countid"));
            Log.e("find Last", "has Last"+countid );
            noCollector = countid+1;
        }
    }

    static List<String> dbOutput;
    public void setDBOutput(){
        db = dbHelper.getWritableDatabase();
        cursor = db.query("LatLngList", null, " listid = ?", new String[]{0 + ""}, null, null, null, null);

        while (cursor.moveToNext())
        {
            String type = cursor.getString(cursor.getColumnIndex("type"));
            int countid = cursor.getInt(cursor.getColumnIndex("countid"));
            int listid = cursor.getInt(cursor.getColumnIndex("listid"));
            double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
            double lng = cursor.getDouble(cursor.getColumnIndex("lng"));

            if (countid >= dbOutput.size())
            {
                String string = (countid+1) + ": Has type: " + type;
                dbOutput.add(string);
                DatabaseFragment dbFr = new DatabaseFragment();
//                dbFr.dbAction();
            }

            Log.e("List Id: ", listid+" Count Id: "+countid+1+" Type: "+type );
   /*         if (type.equals("line"))
            {
                LatLng latLng = new LatLng(lat, lng);
                lineList.add(latLng);
    //            drawLine();
            } else if (type.equals("area"))
            {
                LatLng latLng = new LatLng(lat, lng);
                areaList.add(latLng);
    //            drawArea();
            }*/

        }
    }


    public void switchFragment(Fragment from, Fragment to)
    {
        if (mContext != to)
        {
            mContext = to;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (!to.isAdded()){
                fragmentTransaction.hide(from).add(R.id.innerWindow, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            }
            else {
                fragmentTransaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    /*void showDB(){
        ListView listView;
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dbOutput);
    }*/
}
