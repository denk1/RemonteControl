package com.example.den.remontecontrol;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v4.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.maps.model.JointType.ROUND;
import static com.example.den.remontecontrol.MapUtils.getBearing;
import static java.lang.Math.round;


/**
 * A demonstration about car movement on google map
 by @Shihab Uddin

 TO RUN -> GIVE YOUR GOOGLE API KEY to >  google_maps_api.xml file
 -> GIVE YOUR SERVER URL TO FETCH LOCATION UPDATE

 */

public class CarMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final long DELAY = 4500;
    private static final long ANIMATION_TIME_PER_ROUTE = 3000;
    String polyLine = "q`epCakwfP_@EMvBEv@iSmBq@GeGg@}C]mBS{@KTiDRyCiBS";
    GoogleMap googleMap;
    private PolylineOptions polylineOptions;
    private Polyline greyPolyLine;
    private SupportMapFragment mapFragment;
    private Handler handler;
    private Marker carMarker;
    private Marker marker;
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
    Button button2;
    Button button1;
    Button buttonVelocity;
    ImageView imageViewButton;
    ImageButton buttonTarget;
    List<LatLng> polyLineList;
    private double lat, lng;
    // banani
    double latitude = 23.7877649;
    double longitude = 90.4007049;
    private String TAG = "CapMapActivity";

    // Give your Server URL here >> where you get car location update
    public static final String URL_DRIVER_LOCATION_ON_RIDE = "http://192.168.4.1:8080";

    private boolean isFirstPosition = true;
    private boolean flagTarget = true;
    private double startLatitude;
    private double startLongitude;
    private Client mClient;
    private ConnectTask connectTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_map);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        button2 = (Button) findViewById(R.id.button2);
        button1 = (Button) findViewById(R.id.button1);
        buttonVelocity = (Button) findViewById(R.id.button3);
        buttonTarget = (ImageButton) findViewById(R.id.buttonTarget) ;
        connectTask = new ConnectTask();
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//               staticPolyLine();
//                dynamicPolyLine();
//                startGettingOnlineDataFromCar();

                connectTask.execute("");

            }
        });

        buttonTarget.setBackgroundResource(R.drawable.icon_navigator);
        buttonTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagTarget = !flagTarget;
            }
        });

        handler = new Handler();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        boolean cancel = connectTask.cancel(false);
        if(connectTask.isCancelled()) {
            Log.i(TAG, "Task is canceled:" + cancel);
        }
        super.onDestroy();

    }

    void staticPolyLine() {

        googleMap.clear();

        polyLineList = MapUtils.decodePoly(polyLine);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        googleMap.animateCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(5);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = googleMap.addPolyline(polylineOptions);

        startCarAnimation(latitude, longitude);

    }

    Runnable staticCarRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "staticCarRunnable handler called...");
            if (index < (polyLineList.size() - 1)) {
                index++;
                next = index + 1;
            } else {
                index = -1;
                next = 1;
                stopRepeatingTask();
                return;
            }

            if (index < (polyLineList.size() - 1)) {
//                startPosition = polyLineList.get(index);
                startPosition = carMarker.getPosition();
                endPosition = polyLineList.get(next);
            }

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

//                    Log.i(TAG, "Car Animation Started...");

                    v = valueAnimator.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v)
                            * startPosition.longitude;
                    lat = v * endPosition.latitude + (1 - v)
                            * startPosition.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f, 0.5f);
                    carMarker.setRotation(getBearing(startPosition, newPos));
                    googleMap.moveCamera(CameraUpdateFactory
                            .newCameraPosition
                                    (new CameraPosition.Builder()
                                            .target(newPos)
                                            .zoom(18.5f)
                                            .build()));


                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 5000);

        }
    };

    private void startCarAnimation(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        carMarker = googleMap.addMarker(new MarkerOptions().position(latLng).
                flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));


        index = -1;
        next = 1;
        handler.postDelayed(staticCarRunnable, 3000);
    }

    void stopRepeatingTask() {

        if (staticCarRunnable != null) {
            handler.removeCallbacks(staticCarRunnable);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(false);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);
        //googleMap.getUiSettings().setZoomControlsEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    private void getDriverLocationUpdate() {

        StringRequest request = new StringRequest(Request.Method.POST, URL_DRIVER_LOCATION_ON_RIDE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("PartnerInfoRes::", response);
                JSONObject jObj;
                try {
                    jObj = new JSONObject(response);
                    String ApiSuccess = jObj.getString("success");
                    if (ApiSuccess.trim().equals("true")) {

                        JSONObject jObj2 = new JSONObject(jObj.getString("data"));
                        JSONObject jObj3 = new JSONObject(jObj2.getString("driver"));

                        startLatitude = Double.valueOf(jObj3.getString("lat"));
                        startLongitude = Double.valueOf(jObj3.getString("lng"));

                        Log.d(TAG, startLatitude + "--" + startLongitude);

                        if (isFirstPosition) {
                            int height = 100;
                            int width = 100;

                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, b.getWidth()/5, b.getHeight()/5, false);
                            startPosition = new LatLng(startLatitude, startLongitude);

                            carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                                    flat(true).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                            carMarker.setAnchor(0.5f, 0.5f);

                            googleMap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition
                                            (new CameraPosition.Builder()
                                                    .target(startPosition)
                                                    .zoom(15.5f)
                                                    .build()));

                            isFirstPosition = false;

                        } else {
                            endPosition = new LatLng(startLatitude, startLongitude);

                            Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);

                            if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {

                                Log.e(TAG, "NOT SAME");
                                startBikeAnimation(startPosition, endPosition);

                            } else {

                                Log.e(TAG, "SAMME");
                            }
                        }

                    }
                    if (jObj.getString("message").trim().equals("Unauthorized")) {

                        Log.e(TAG, "--- Unauthorized ---");

                    }

                } catch (Exception e) {
                    Log.d("jsonError::", e + "");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //params.put("driver_id", driverId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //Log.d("acc::", ClientAccToken);
                //params.put("authorization", "ClientAccToken");

                return params;
            }

        };

        App.getAppInstance().addToRequestQueue(request, TAG);
    }

    private void startBikeAnimation(final LatLng start, final LatLng end) {

        Log.i(TAG, "startBikeAnimation called...");

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(ANIMATION_TIME_PER_ROUTE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                Log.i(TAG, "Car Animation Started...");
                v = valueAnimator.getAnimatedFraction();
                lng = v * end.longitude + (1 - v)
                        * start.longitude;
                lat = v * end.latitude + (1 - v)
                        * start.latitude;

                LatLng newPos = new LatLng(lat, lng);
                Log.i(TAG, lat + "--" + lng);
                carMarker.setPosition(newPos);
                carMarker.setAnchor(0.5f, 0.5f);
                carMarker.setRotation(getBearing(start, end));

                // todo : Shihab > i can delay here
                if(flagTarget)
                    googleMap.moveCamera(CameraUpdateFactory
                            .newCameraPosition
                                    (new CameraPosition.Builder()
                                            .target(newPos)
                                            .zoom(17.5f)
                                            .build()));

                startPosition = carMarker.getPosition();

            }

        });
        valueAnimator.start();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                getDriverLocationUpdate();


            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            handler.postDelayed(mStatusChecker, DELAY);

        }
    };

    void startGettingOnlineDataFromCar() {
        handler.post(mStatusChecker);
    }

    void CreatePolyLineOnly() {

        googleMap.clear();

        polyLineList = MapUtils.decodePoly(polyLine);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        googleMap.animateCamera(mCameraUpdate);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.width(5);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = googleMap.addPolyline(polylineOptions);


    }


    public class ConnectTask extends AsyncTask<String, Double[], Client> {

        @Override
        protected Client doInBackground(String... message) {

            //we create a TCPClient object and
            mClient = new Client(new Client.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(Double[] message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }, this);
            mClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(Double[]... values) {
            super.onProgressUpdate(values);
            startLatitude = Double.valueOf(values[0][1])/* 180.0/3.14*/;
            startLongitude = Double.valueOf(values[0][2])/* 180.0/3.14*/;


            Log.i(TAG, startLatitude + "--" + startLongitude);
            isConnectedIndicator(mClient.isConnected());
            String strVelocity = String.valueOf(round(values[0][7]));
            buttonVelocity.setText(String.valueOf(strVelocity));
            if (isFirstPosition) {
                int height = 100;
                int width = 100;

                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, b.getWidth() / 5, b.getHeight() / 5, false);
                startPosition = new LatLng(startLatitude, startLongitude);
                //marker = googleMap.addMarker(new MarkerOptions().position(startPosition).title("Marker"));
                carMarker = googleMap.addMarker(new MarkerOptions().position(startPosition).
                        flat(true).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                carMarker.setAnchor(0.5f, 0.5f);

                googleMap.moveCamera(CameraUpdateFactory
                        .newCameraPosition
                                (new CameraPosition.Builder()
                                        .target(startPosition)
                                        .zoom(17.5f)
                                        .build()));

                isFirstPosition = false;

            } else {

                endPosition = new LatLng(startLatitude, startLongitude);
                Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);

                if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {

                    Log.e(TAG, "NOT SAME");
                    startBikeAnimation(startPosition, endPosition);

                } else {

                    Log.e(TAG, "SAME");
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Client client) {
            super.onPostExecute(client);
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            this.cancel(true);
        }

        void setIndicatorConnectionFailed() {
            button1.setBackgroundColor(Color.parseColor("#ff0000"));
        }

        void setIndicatorConnectionOk() {
            button1.setBackgroundColor(Color.parseColor("#00ff00"));
        }

        private void isConnectedIndicator(boolean connectionState) {
            if(connectionState) {
                setIndicatorConnectionOk();
            }
            else {
                setIndicatorConnectionFailed();
            }
        }


    }
}