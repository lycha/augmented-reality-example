package com.lycha.example.augmentedreality;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraViewActivity extends Activity implements
		SurfaceHolder.Callback, MyCurrentLocationListener, OnAzimuthChangedListener{

	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private boolean cameraview = false;
	private AugmentedPOI poi;

	private double azimuthReal = 0;
	private double azimuthTeoretical = 0;
	private double AZIMUTH_ACCURACY = 5;
	private double lat = 0;
	private double longi = 0;

	private MyCurrentAzimuth myCurrentAzimuth;
	private MyCurrentLocation myCurrentLocation;

	TextView cameraText;
	ImageView icon;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_view);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setupListeners();
		setupLayout();
		setAugmentedRealityPoint();
	}

	private void setAugmentedRealityPoint() {
		poi = new AugmentedPOI(
				"Zamek Ojców", //poiName
				"Opis Zamku Ojców", //poiDesc
				50.21184861660004, //poiLat
				19.83051002025604 //poiLongi
		);
	}

	public double calculateTeoreticalAzimuth() {
		Location myLocation = new Location("MyLocation");
		myLocation.setLatitude(lat);
		myLocation.setLongitude(longi);

		Location locationB = new Location("POI");
		locationB.setLatitude(poi.getPoiLatitude());
		locationB.setLongitude(poi.getPoiLongitude());

		double dX = locationB.getLatitude() - myLocation.getLatitude();
		double dY = locationB.getLongitude() - myLocation.getLongitude();

		double fi;
		double tanFi;
		double A = 0;

		tanFi = Math.abs(dY / dX);
		fi = Math.atan(tanFi);
		fi = Math.toDegrees(fi);

		if (dX > 0 && dY > 0) { // I quater
			return A = fi;
		} else if (dX < 0 && dY > 0) { // II
			return A = 180 - fi;
		} else if (dX < 0 && dY < 0) { // III
			return A = 180 + fi;
		} else if (dX > 0 && dY < 0) { // IV
			return A = 360 - fi;
		}

		return fi;
	}
	
	private List<Double> calculateAzimuthAccuracy(double A) {
		double min = A - AZIMUTH_ACCURACY;
		double max = A + AZIMUTH_ACCURACY;
		List<Double> minMax = new ArrayList<Double>();

		if (min < 0)
			min += 360;

		if (max >= 360)
			max -= 360;
		minMax.clear();
		minMax.add(min);
		minMax.add(max);
		return minMax;
	}

	private boolean isBetween(double min, double max, double A) {
		if (min > max) {
			if (isBetween(0, max, A) && isBetween(min, 360, A))
				return true;
		} else {
			if (A > min && A < max)
				return true;
		}
		return false;
	}

	@Override
	public void getCurrentLocation(Location location) {
		lat = location.getLatitude();
		longi = location.getLongitude();
		cameraText.setText(poi.getPoiName() + " azimuthTeoretical "
				+ azimuthTeoretical + " azimuthReal " + azimuthReal + " lat "
				+ lat + " lon " + longi);
		azimuthTeoretical = calculateTeoreticalAzimuth();
		Toast.makeText(this,"latitude: "+location.getLatitude()+" longitude: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
		Log.d("CurrentLocation", "latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
	}

	@Override
	public void onAzimuthChanged(float azimuthFrom, float azimuthTo) {
		azimuthReal = azimuthTo;
		azimuthTeoretical = calculateTeoreticalAzimuth();

		icon = (ImageView) findViewById(R.id.icon);

		double min = calculateAzimuthAccuracy(azimuthTeoretical).get(0);
		double max = calculateAzimuthAccuracy(azimuthTeoretical).get(1);
		boolean t;
		if (isBetween(min, max, azimuthReal)) {
			icon.setVisibility(View.VISIBLE);
		} else {
			icon.setVisibility(View.INVISIBLE);
		}

		cameraText.setText(poi.getPoiName() + " azimuthTeoretical "
				+ azimuthTeoretical + " azimuthReal " + azimuthReal + " lat "
				+ lat + " lon " + longi);
		Log.d("CurrentAzimuth", "azimuth: " + azimuthTo);
	}

	@Override
	protected void onStop() {
		myCurrentAzimuth.stop();
		myCurrentLocation.stop();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		myCurrentAzimuth.start();
		myCurrentLocation.start();
	}

	private void setupListeners() {
		myCurrentLocation = new MyCurrentLocation(this);
		myCurrentLocation.buildGoogleApiClient(this);
		myCurrentLocation.start();

		myCurrentAzimuth = new MyCurrentAzimuth(this, this);
		myCurrentAzimuth.start();
	}


	private void setupLayout() {
		cameraText = (TextView) findViewById(R.id.cameraTextView);

		/** Set surface for camera view*/
		getWindow().setFormat(PixelFormat.UNKNOWN);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	//////////////////**Methods responsible for the camera view */////////////////////
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {

		if (cameraview) {
			camera.stopPreview();
			cameraview = false;
		}

		if (camera != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				cameraview = true;
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		camera = Camera.open();
		camera.setDisplayOrientation(90);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		camera.stopPreview();
		camera.release();
		camera = null;
		cameraview = false;
	}
}
