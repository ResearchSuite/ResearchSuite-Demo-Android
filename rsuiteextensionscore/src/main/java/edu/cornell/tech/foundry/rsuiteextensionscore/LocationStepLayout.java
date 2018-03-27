package edu.cornell.tech.foundry.rsuiteextensionscore;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchsuite.rsuiteextensionscore.R;

/**
 * Created by Christina on 6/21/17.
 */

public class LocationStepLayout extends FrameLayout implements StepLayout, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GeocodeAsyncTask.AsyncResponse {

    private LocationStep step;
    private StepResult stepResult;
    private TextView titleText;
    private TextView questionDetail;
    private EditText locationField;
    private GoogleMap mGoogleMap;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private StepCallbacks callbacks;
    private MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest mLocationRequest;
    private Button mSubmitButton;
    private String userInput;
    private String formattedAddress;



    public LocationStepLayout(@NonNull Context context) {
        super(context);
    }

    public LocationStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public LocationStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mMapView.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        mMapView.onResume();
        mGoogleApiClient.connect();
        //setUpMapIfNeeded();
    }


    @Override
    public void initialize(Step step, StepResult result) {

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);


        this.step = (LocationStep) step;


        //this.stepResult = result;
        this.stepResult = result == null ? new StepResult<>(step) : result;
        this.initializeStep(this.step, this.stepResult);

    }

    public void initializeStep(LocationStep step, StepResult result) {
        this.initStepLayout(step);
    }

    @Override
    public View getLayout() {

        return this;

    }


    private void initStepLayout(LocationStep step) {


        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.location_layout, this, true);

        mMapView = (MapView) findViewById(R.id.myMap);
        mMapView.onCreate(null);

        mMapView.getMapAsync(this);

        TextAnswerFormat format = (TextAnswerFormat) ((LocationStep) step).getAnswerFormat();

        titleText = (TextView) findViewById(R.id.question_title);
        titleText.setText(step.getTitle());

        questionDetail = (TextView) findViewById(R.id.question_detail);
        questionDetail.setText(step.getText());


        mSubmitButton = (Button) findViewById(R.id.button_submit);
        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                onSubmitClicked();

            }
        });


        locationField = (EditText) findViewById(R.id.location_result);
        locationField.setSingleLine(!format.isMultipleLines());
        locationField.setMaxWidth(format.getMaximumLength());
        locationField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    userInput = locationField.getText().toString();
                    if (userInput != "") {
                        setGivenLocation(userInput);
                        Log.d("geocode called","called");
                    }
                }
                return false;
            }
        });

        step.setAnswerFormat(format);

    }

    public StepResult getStepResult() {
        if (locationField.getText().toString() == "") {
            stepResult.setResult(null);
        } else {
         //   LocationStepResult result = new LocationStepResult(step.getIdentifier());
            LocationStepResult result = new LocationStepResult(step);
            result.setStartDate(stepResult.getStartDate());
            result.setEndDate(stepResult.getEndDate());
            result.setLongLat(longitude, latitude);
            result.setUserInput(userInput);
            result.setAddress(formattedAddress);
            stepResult.setResult(result);
        }

        return stepResult;
    }

    @Override
    public boolean isBackEventConsumed() {
        //this will cancel the event
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV,this.step,this.stepResult);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;

    }

    public Step getStep(){
        return this.step;
    }

    public void onSubmitClicked(){
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,this.getStep(),this.getStepResult());
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, getStep(), this.getStepResult());
        return super.onSaveInstanceState();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // handled in main
            return;
        }

        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Log.d("last location: ",String.valueOf(location));



        if (location != null) {
            String currentLatString = String.valueOf(location.getLatitude());
            String currentLngString = String.valueOf(location.getLongitude());

            setGivenLocation(currentLatString,currentLngString);

            handleNewLocation(location);

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);

    }

    private void handleNewLocation(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng currentLocation = new LatLng(currentLatitude,currentLongitude);
        mGoogleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation)      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void processFinish(String output) {

        double lng = 0;
        double lat = 0;
        String formattedAddress = "";

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(output);
            lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");


            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            formattedAddress = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getString("formatted_address");
            Log.d("formatted address", formattedAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double newLatitude = lat;
        double newLongitude = lng;

        Log.d("newlat: ", String.valueOf(newLatitude));
        Log.d("newLong: ", String.valueOf(newLongitude));

        this.longitude = lng;
        this.latitude = lat;
        this.formattedAddress = formattedAddress;

        LatLng newLocation = new LatLng(newLatitude, newLongitude);
        mGoogleMap.addMarker(new MarkerOptions().position(newLocation).title("New Location"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(newLocation)
                .zoom(17)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void setGivenLocation(String address){

        GeocodeAsyncTask task = new GeocodeAsyncTask(this);
        String urlAddress = address.replace(" ", "%20");
        task.execute("http://maps.google.com/maps/api/geocode/json?address=" + urlAddress + "&sensor=false");

    }

    private void setGivenLocation(String lat, String lng){

        GeocodeAsyncTask task = new GeocodeAsyncTask(this);
        task.execute("http://maps.google.com/maps/api/geocode/json?address=" + lat+"%20"+lng + "&sensor=false");


    }
}



