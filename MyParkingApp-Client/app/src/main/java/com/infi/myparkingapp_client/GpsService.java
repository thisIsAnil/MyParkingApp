package com.infi.myparkingapp_client;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import android.os.Bundle;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class GpsService extends Service implements LocationListener,GpsStatus.Listener {
    long LOCATION_REFRESH_TIME = 2000;
    float LOCATION_REFRESH_DISTANCE = 1;
    int i=1;
    double lat,lon;
    int sideOfPolygon=1;
    List<GPS_data_class> list=new ArrayList<>();
    TimerAdapter dbHelper;
    LocationManager lm;
    private GpsStatus mStatus;
    int flag=2;
    long[] modes=new long[4];
    private int check=0;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;  int[] requestCodes=new int[100];
    public GpsService() {
    }
    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */

    @Override
    public void onCreate() {
        lm=(LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        /*if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lm.addGpsStatusListener(this);
        }*/
        super.onCreate();

        //LocationManager mlocManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
       // isLocationEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p/>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p/>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p/>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p/>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) { lm.addGpsStatusListener(this);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);

        }
        /*new CountDownTimer(60*60*1000,60*1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE,GpsService.this);

                }
            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        }.start();*/
        for(int i=0;i<4;i++)modes[i]=0;
       Location loc=GiveLocation();
        //Toast.makeText(getApplicationContext(),loc.getLatitude()+"  ",Toast.LENGTH_SHORT).show();
        check=0;i=0;
        if(loc!=null) {
            check=2;
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            //AudioManager audioManager=(AudioManager)getApplicationContext().getSystemService(AUDIO_SERVICE);
            getDataIntoList();
            //if(list.size()==0){flag=0;stopSelf();}
            double x=lat,y=lon;
            lat=new BigDecimal(x).setScale(7,BigDecimal.ROUND_HALF_UP).doubleValue();
            lon=new BigDecimal(y).setScale(7,BigDecimal.ROUND_HALF_UP).doubleValue();
            //if(list.size()==0){flag=0;stopSelf();}
            if (CanSilentDevice(lat, lon)) {

                if(modes[0]==1){
                    AudioManager audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);}
                else if(modes[1]==1){
                    AudioManager audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                if(modes[2]==1){
                    WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                    if(!wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(true);
                }
                else if (modes[3]==1){
                    WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                    if(wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(false);

                }
            }
            else{
                if(modes[0]==1){
                    AudioManager audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    }
                else if(modes[1]==1){
                    AudioManager audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
                if(modes[2]==1){
                    WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                    if(wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(false);
                }
                else if (modes[3]==1){
                    WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                    if(!wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(true);

                }
            }
        }else{
                //Toast.makeText(getApplicationContext(),"No Location Found ",Toast.LENGTH_SHORT).show();
            //RestartService(20*1000);
           // stopSelf();

        }




        return START_STICKY;
    }
    public long[] convertMode(String days){
        char[] s=days.toCharArray();
        long a[]=new long[4];
        for (int i=0;i<4;i++){
            if(s[i]=='1'){a[i]=1;}
        }
        return a;

    }
    public Location GiveLocation() {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        lm = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (gps_loc != null && net_loc != null) {

            if (gps_loc.getAccuracy() >= net_loc.getAccuracy())
                finalLoc = gps_loc;
            else
                finalLoc = net_loc;


        } else {

            if (gps_loc != null) {
                finalLoc = net_loc;
            } else if (net_loc != null) {
                finalLoc = gps_loc;
            }
        }
            return finalLoc;
        }



        /**
         * Called by the system to notify a Service that it is no longer used and is being removed.  The
         * service should clean up any resources it holds (threads, registered
         * receivers, etc) at this point.  Upon return, there will be no more calls
         * in to this Service object and it is effectively dead.  Do not call this method directly.
         */
        @Override
        public void onDestroy () {
            Intent intent=new Intent(getApplicationContext(),OnProximityBr.class);
            for(int i=0;i<requestCodes.length;i++){
              if(requestCodes[i]>=0){
                    PendingIntent p=PendingIntent.getBroadcast(getApplicationContext(),requestCodes[i],intent,0);
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {lm.removeProximityAlert(p);}
               }
            }
            AudioManager audioManager=(AudioManager)getApplicationContext().getSystemService(AUDIO_SERVICE);
            if(audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT)audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            lm.removeGpsStatusListener(this);
            if(flag==1) {
               RestartService(1000);
            }
            else{
                RestartService(100);
            }
        }

        @Override
        public IBinder onBind (Intent intent){
            // TODO: Return the communication channel to the service.
            return null;
        }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        sendBroadcast(new Intent("YouCantSeeMe"));

        super.onTaskRemoved(rootIntent);
    }

    public boolean CanSilentDevice(double latitude, double longitude){

        for(int i=0;i<list.size();i++) {
            GPS_data_class data = list.get(i);
            modes=convertMode(list.get(i).getMode());
            List<LatLng> PolyPoints=new ArrayList<>();
            double radius = Integer.parseInt(data.getRadius());
            //if (radius == 0) {
                double[] lat = returnDouble(data.getLatitude());
                double[] lon = returnDouble(data.getLongitude());
                if (lat.length != lon.length) {
                    return false;
                }
                for(int j=0;j<lat.length;j++){
                PolyPoints.add(new LatLng(lon[j],lat[j]));
                }
                PolyPoints.add(new LatLng(lon[0],lat[0]));

                sideOfPolygon = lat.length - 1;
                if(radius==0) {
                    switch (sideOfPolygon) {
                        case 0:
                            return false;
                        case 1:
                            if (radius == 0) radius = 5;
                            boolean val = isInsideCircle(lat[0], lon[0], latitude, longitude, radius);
                            return val;
                        case 2:
                            return (((lat[0] < latitude && lon[0] < longitude && lat[1] > latitude && lon[1] > longitude) ||
                                    (lat[0] > latitude && lon[0] < 0 && lat[1] < latitude && lon[1] > longitude) ||
                                    (lat[0] > latitude && lon[0] > longitude && lat[1] < latitude && lon[1] < longitude) ||
                                    (lat[0] > latitude && lon[0] < 0 && lat[1] < latitude && lon[1] > longitude) ||
                                    (lat[0] == latitude && lon[0] < longitude && lon[1] > longitude) ||
                                    (lon[0] == longitude && lat[0] < latitude && lat[1] > latitude) ||
                                    (lat[0] == latitude && lon[0] > longitude && lon[1] < longitude) ||
                                    (lon[0] == longitude && lat[0] > latitude && lat[1] < latitude))
                            );
                        default:
                            boolean isInside = containsLocation(new LatLng(latitude, longitude), PolyPoints, true);
                            // Toast.makeText(getApplicationContext(),isInside+"",Toast.LENGTH_SHORT).show();
                            //boolean val=isPointInPolygon(latitude,longitude,lon,lat);
                            //Toast.makeText(getApplicationContext(),isInside+"",Toast.LENGTH_SHORT).show();
                            if (!isInside) {
                                isInside = isPointInPolygon(new LatLng(latitude, longitude), PolyPoints);
                            }
                            return (isInside);

                    }
                }
            }
        //}
        return false;
    }

    private static double tanLatGC(double lat1, double lat2, double lng2, double lng3) {
        return (Math.tan(lat1) * Math.sin(lng2 - lng3) + Math.tan(lat2) * Math.sin(lng3)) / Math.sin(lng2);
    }

    private static double mercatorLatRhumb(double lat1, double lat2, double lng2, double lng3) {
        return (MathUtil.mercator(lat1) * (lng2 - lng3) + MathUtil.mercator(lat2) * lng3) / lng2;
    }

    private static boolean intersects(double lat1, double lat2, double lng2, double lat3, double lng3, boolean geodesic) {
        if((lng3 < 0.0D || lng3 < lng2) && (lng3 >= 0.0D || lng3 >= lng2)) {
            if(lat3 <= -1.5707963267948966D) {
                return false;
            } else if(lat1 > -1.5707963267948966D && lat2 > -1.5707963267948966D && lat1 < 1.5707963267948966D && lat2 < 1.5707963267948966D) {
                if(lng2 <= -3.141592653589793D) {
                    return false;
                } else {
                    double linearLat = (lat1 * (lng2 - lng3) + lat2 * lng3) / lng2;
                    return lat1 >= 0.0D && lat2 >= 0.0D && lat3 < linearLat?false:(lat1 <= 0.0D && lat2 <= 0.0D && lat3 >= linearLat?true:(lat3 >= 1.5707963267948966D?true:(geodesic?Math.tan(lat3) >= tanLatGC(lat1, lat2, lng2, lng3):MathUtil.mercator(lat3) >= mercatorLatRhumb(lat1, lat2, lng2, lng3))));
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean containsLocation(LatLng point, List<LatLng> polygon, boolean geodesic) {
        int size = polygon.size();
        if(size == 0) {
            return false;
        } else {
            double lat3 = Math.toRadians(point.latitude);
            double lng3 = Math.toRadians(point.longitude);
            LatLng prev = (LatLng)polygon.get(size - 1);
            double lat1 = Math.toRadians(prev.latitude);
            double lng1 = Math.toRadians(prev.longitude);
            int nIntersect = 0;

            double lng2;
            for(Iterator var14 = polygon.iterator(); var14.hasNext(); lng1 = lng2) {
                LatLng point2 = (LatLng)var14.next();
                double dLng3 = MathUtil.wrap(lng3 - lng1, -3.141592653589793D, 3.141592653589793D);
                if(lat3 == lat1 && dLng3 == 0.0D) {
                    return true;
                }

                double lat2 = Math.toRadians(point2.latitude);
                lng2 = Math.toRadians(point2.longitude);
                if(intersects(lat1, lat2, MathUtil.wrap(lng2 - lng1, -3.141592653589793D, 3.141592653589793D), lat3, dLng3, geodesic)) {
                    ++nIntersect;
                }

                lat1 = lat2;
            }
            return (nIntersect & 1) != 0;
        }
    }

    private boolean isPointInPolygon(LatLng tap, List<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }
    public boolean isInsideCircle(double Clat,double Clon,double lat,double lon,double radius){
        //Toast.makeText(getApplicationContext(),"In Circle Test",Toast.LENGTH_SHORT).show();
        Location Center=new Location("point C");
        Location Me=new Location("Point ME");
        Center.setLatitude(Clat);
        Center.setLongitude(Clon);
        Me.setLatitude(lat);
        Me.setLongitude(lon);
        double dist=Center.distanceTo(Me);
        Toast.makeText(getApplicationContext(),"Distance"+dist,Toast.LENGTH_SHORT).show();
        if(radius>=Math.abs(dist))return true;
        else return false;
        /*float[] result=new float[4];
        Location.distanceBetween(Clat,Clon,lat,lon,result);
        Toast.makeText(getApplicationContext(),radius+" raf"+result[0],Toast.LENGTH_SHORT).show();
        if(radius>=result[0])return true;
        else return false;*/
    }

    public double[] returnDouble(String string){
        double[] doubles;
        String[] s=string.split("//_//");
        doubles=new double[s.length+1];
        double[] ret=new double[s.length];
        int i=0;
        for(i=0;i<s.length;i++){
            doubles[i]=Double.parseDouble(s[i]);
            ret[i]=new BigDecimal(doubles[i]).setScale(7,BigDecimal.ROUND_HALF_UP).doubleValue();

        }
        //Toast.makeText(getApplicationContext(), ret[0]+" and \n"+ret[1], Toast.LENGTH_SHORT).show();
        return ret;
    }

    public void getDataIntoList(){
        Intent in = new Intent(getApplicationContext(), OnProximityBr.class);

        PendingIntent p = PendingIntent.getBroadcast(getApplicationContext(), 0, in,0);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {lm.removeProximityAlert(p);}
        dbHelper=new TimerAdapter(getApplicationContext());
        try{dbHelper.open();}catch (java.sql.SQLException e){}
        list.clear();
        Cursor c=dbHelper.getAllGpsEvents();
        if(c!=null) {

            do {
                GPS_data_class event = new GPS_data_class();
                event.setTitle(c.getString(c.getColumnIndex(TimerAdapter.KEY_GPS_TITLE)));
                event.setLatitude(c.getString(c.getColumnIndex(TimerAdapter.KEY_LAT)));
                event.setLongitude(c.getString(c.getColumnIndex(TimerAdapter.KEY_LON)));
                event.setAddress(c.getString(c.getColumnIndex(TimerAdapter.KEY_ADDRESS)));
                event.setStatus(c.getInt(c.getColumnIndex(TimerAdapter.KEY_STATUS_GPS)));
                event.setRadius(c.getString(c.getColumnIndex(TimerAdapter.KEY_RADIUS)));
                event.setId(c.getLong(c.getColumnIndex(TimerAdapter.KEY_ID_GPS)));
                event.setMode(c.getString(c.getColumnIndex(TimerAdapter.KEY_MODE_GPS)));
                //timer.setIsDaily(c.getString(c.getColumnIndex(TimerAdapter.KEY_DAILY)));
                //Toast.makeText(getActivity(), "Displaying "+event.getAddress()+"", Toast.LENGTH_SHORT).show();
                if (event.getStatus() == 1) {
                    if(event.getRadius().equals("0"))list.add(event);
                    double radius = Integer.parseInt(event.getRadius());
                    if (radius != 0) {
                        double[] lat = returnDouble(event.getLatitude());
                        double[] lon = returnDouble(event.getLongitude());
                        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED ) {
                            Intent intent = new Intent(getApplicationContext(), OnProximityBr.class);
                            long[] data=new long[4];
                            char[] t=event.getMode().toCharArray();
                            for (int j=0;j<4;j++){if (t[j]=='1')data[j]=1;else data[j]=0;}

                            intent.putExtra("MODES",data);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), i, intent,PendingIntent.FLAG_CANCEL_CURRENT);
                            lm.addProximityAlert(lat[0], lon[0], (float) radius, -1, pendingIntent);
                           requestCodes[i]=i;i++;
                        }
                    }

                }
            } while (c.moveToNext());
            c.close();
            dbHelper.close();
        }else{
            c.close();
            dbHelper.close();
            stopSelf();
        }


    }

    public void getData() {
        dbHelper = new TimerAdapter(getApplicationContext());
        try {
            dbHelper.open();
        } catch (java.sql.SQLException e) {
        }
        list.clear();
        Cursor c = dbHelper.getAllGpsEvents();
        if (c != null) {
            do {
                GPS_data_class event = new GPS_data_class();
                event.setTitle(c.getString(c.getColumnIndex(TimerAdapter.KEY_GPS_TITLE)));
                event.setLatitude(c.getString(c.getColumnIndex(TimerAdapter.KEY_LAT)));
                event.setLongitude(c.getString(c.getColumnIndex(TimerAdapter.KEY_LON)));
                event.setAddress(c.getString(c.getColumnIndex(TimerAdapter.KEY_ADDRESS)));
                event.setStatus(c.getInt(c.getColumnIndex(TimerAdapter.KEY_STATUS_GPS)));
                event.setRadius(c.getString(c.getColumnIndex(TimerAdapter.KEY_RADIUS)));
                event.setId(c.getLong(c.getColumnIndex(TimerAdapter.KEY_ID_GPS)));
                event.setMode(c.getString(c.getColumnIndex(TimerAdapter.KEY_MODE_GPS)));
                //timer.setIsDaily(c.getString(c.getColumnIndex(TimerAdapter.KEY_DAILY)));
                //Toast.makeText(getActivity(), "Displaying "+event.getAddress()+"", Toast.LENGTH_SHORT).show();
                if (event.getStatus() == 1) {
                    list.add(event);
                }

            } while (c.moveToNext());
            c.close();
            dbHelper.close();
        } else {
            c.close();
            dbHelper.close();
            flag=0;
            stopSelf();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

            Location loc=GiveLocation();
            if(loc==null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }else {
                lat=loc.getLatitude();lon=loc.getLongitude();
            }
            getDataIntoList();
            //if(check==0){getDataIntoList();check=check+2;}
            //else if(check==1) {getData();check++;}
        //if(list.size()==0){flag=0;stopSelf();}
            double x=lat,y=lon;
            lat=new BigDecimal(x).setScale(7,BigDecimal.ROUND_HALF_UP).doubleValue();
            lon=new BigDecimal(y).setScale(7,BigDecimal.ROUND_HALF_UP).doubleValue();
        if (CanSilentDevice(lat, lon)) {
            if(modes[0]==1){

                AudioManager audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);}
            else if(modes[1]==1){
                AudioManager audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
            if(modes[2]==1){
                WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                if(!wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(true);
            }
            else if (modes[3]==1){
                WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                if(wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(false);

            }
        }
        else {

                if (modes[0] == 1) {
                    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                } else if (modes[1] == 1) {
                    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
                if (modes[2] == 1) {
                    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                    if (wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(false);
                } else if (modes[3] == 1) {
                    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                    if (!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);

                }
            }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
            //stopSelf();
            //flag=1;
        }

    public void RestartService(long time){
            Intent i = new Intent(getApplicationContext(), GpsBroadcastReciever.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis()+(time));
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }


    @Override
    public void onGpsStatusChanged(int event) {
        mStatus = lm.getGpsStatus(mStatus);
        flag=0;
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                //stopSelf();
                //flag=1;
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) { lm.addGpsStatusListener(this);
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);

                }

                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                AudioManager audioManager=(AudioManager)getApplicationContext().getSystemService(AUDIO_SERVICE);
                if(audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);}
                Location loc=GiveLocation();
                if(loc!=null) {
                    lat = loc.getLatitude();
                    lon = loc.getLongitude();
                    getDataIntoList();
                    if(list.size()==0)stopSelf();
                    double x=lat,y=lon;
                    lat=new BigDecimal(x).setScale(7,BigDecimal.ROUND_HALF_UP).doubleValue();
                    lon=new BigDecimal(y).setScale(7,BigDecimal.ROUND_HALF_UP).doubleValue();
                    if (CanSilentDevice(lat, lon)) {

                        if(modes[0]==1){

                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);}
                        else if(modes[1]==1){
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                        if(modes[2]==1){
                            WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                            if(!wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(true);
                        }
                        else if (modes[3]==1){
                            WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                            if(wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(false);

                        }
                    }
                    else{
                        if(modes[0]==1){
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }
                        else if(modes[1]==1){
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }
                        if(modes[2]==1){
                            WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                            if(wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(false);
                        }
                        else if (modes[3]==1){
                            WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                            if(!wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(true);

                        }
                    }
                }else{
                    break;}

                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                // Do Something with mStatus info
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // Do Something with mStatus info
                break;

        }
        if(flag==1){stopSelf();}
    }


}
