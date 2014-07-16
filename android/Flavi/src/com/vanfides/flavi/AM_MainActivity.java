package com.vanfides.flavi;



import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;



public class AM_MainActivity extends Activity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener
{
	
	 private static final String TAG = "Flavi";
	 private static final String BIKE = "BIKE";
	 private static final String CAR = "CAR";
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private final static int
    CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    //Stuff for GCM 
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    String regid;

    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;
	LocationClient mLocationClient;
	Location mCurrentLocation;
	//User Interface controls
    RadioGroup radioGroup;
	RadioButton rbBike;
	RadioButton rbCar;
	Button btStartStop;
	Chronometer chrono;

	boolean mUpdatesRequested = true;
	boolean trackingEnabled = false ;
	String selectedAction = BIKE;
	
	private void stopTracking()
	{
		btStartStop.setText(getResources().getString(R.string.am_bt_StartStop));		
		  if (mLocationClient.isConnected()) {
	            /*
	             * Remove location updates for a listener.
	             * The current Activity is the listener, so
	             * the argument is "this".
	             */
			  mLocationClient.removeLocationUpdates(this);
	        }
		mLocationClient.disconnect();
		trackingEnabled = false;
		chrono.stop();
		radioGroup.setEnabled(true);
		rbBike.setEnabled(true);
		rbCar.setEnabled(true);
			
		Log.i(TAG,"Disabling location tracking");
		
	}
	
	
	private void startTracking()
	{
		btStartStop.setText(getResources().getString(R.string.am_btStop));		
		mLocationClient.connect();
		 chrono.setBase(SystemClock.elapsedRealtime());
         chrono.start();
		trackingEnabled = true;
		radioGroup.setEnabled(false);
		rbBike.setEnabled(false);
		rbCar.setEnabled(false);
		
	}
	

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        CallAPI call = new CallAPI();
        call.execute(
            selectedAction,
            getRegistrationId(context) ,
            "android",
            Double.toString(location.getLongitude()),
            Double.toString(location.getLatitude()),
            Long.toString(location.getTime()),
            Float.toString(location.getSpeed()),
            Float.toString(location.getBearing())
            		);
    }
    /*
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
*/
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
 /*   @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
 
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    
                     * Try the request again
                     
                       break;
                }

        }

    }*/

    /*
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code
           // int errorCode = connectionResult.getErrorCode();
             int errorCode = 33;
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                Log.d("Location Updates","We should here show an errorFragment dialog");
                //errorFragment.show(
                  //      getSupportFragmentManager(),
                    //    "Location Updates");
            }
            return false;
        }
    }

	*/
	 @Override
	    public void onConnected(Bundle dataBundle) {
	        // Display the connection status
	        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
	        if (mUpdatesRequested) {
	            mLocationClient.requestLocationUpdates(mLocationRequest, this);
	        }

	    }
	    /*
	     * Called by Location Services if the connection to the
	     * location client drops because of an error.
	     */
	    @Override
	    public void onDisconnected() {
	        // Display the connection status
	        Toast.makeText(this, "Disconnected. Please re-connect.",
	                Toast.LENGTH_SHORT).show();
	    }

	    /*
	     * Called by Location Services if the attempt to
	     * Location Services fails.
	     */
	    @Override
	    public void onConnectionFailed(ConnectionResult connectionResult) {
	        /*
	         * Google Play services can resolve some errors it detects.
	         * If the error has a resolution, try sending an Intent to
	         * start a Google Play services activity that can resolve
	         * error.
	         */
	        if (connectionResult.hasResolution()) {
	            try {
	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(
	                        this,
	                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
	                /*
	                 * Thrown if Google Play services canceled the original
	                 * PendingIntent
	                 */
	            } catch (IntentSender.SendIntentException e) {
	                // Log the error
	                e.printStackTrace();
	            }
	        } else {
	            /*
	             * If no resolution is available, display a dialog to the
	             * user with the error.
	             */
	            Log.d("Location Update", "Here showing an error");
	            //showErrorDialog(connectionResult.getErrorCode());
	        }
	    }
	
	 // You need to do the Play Services APK check here too.
	    @Override
	    protected void onResume() {
	        super.onResume();
	        checkPlayServices();
	    }

	    /**
	     * Check the device to make sure it has the Google Play Services APK. If
	     * it doesn't, display a dialog that allows users to download the APK from
	     * the Google Play Store or enable it in the device's system settings.
	     */
	    private boolean checkPlayServices() {
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	        if (resultCode != ConnectionResult.SUCCESS) {
	            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                		CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
	            } else {
	                Log.i(TAG, "This device is not supported.");
	                finish();
	            }
	            return false;
	        }
	        return true;
	    }
	
	    private void initializeLocationFeatures()
	    {
	    	// Create the LocationRequest object
	        mLocationRequest = LocationRequest.create();
	        // Use high accuracy
	        mLocationRequest.setPriority(
	                LocationRequest.PRIORITY_HIGH_ACCURACY);
	        // Set the update interval to 5 seconds
	        mLocationRequest.setInterval(UPDATE_INTERVAL);
	        // Set the fastest update interval to 1 second
	        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
			

	        btStartStop = (Button) findViewById(R.id.am_btStartStop);
	        chrono = (Chronometer) findViewById(R.id.am_chronometer);
	        rbBike = (RadioButton) findViewById(R.id.am_rbBike);
	        rbCar = (RadioButton) findViewById(R.id.am_rbCar);
	        radioGroup = (RadioGroup) findViewById(R.id.am_rbg_BikeCar);        
	        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	        {
	            public void onCheckedChanged(RadioGroup group, int checkedId) {
	                // checkedId is the RadioButton selected
	            	if (checkedId == R.id.am_rbBike)
	            	{
	            		Log.i(TAG,"Bike selected");
	            		selectedAction = BIKE;
	            	}
	            	else 
	            		if (checkedId == R.id.am_rbCar)
	            		{
		            		Log.i(TAG,"Car selected");
	            			selectedAction = CAR;
	            		}
	            }
	        });
	        mLocationClient = new LocationClient(this, this, this);

	        btStartStop.setOnClickListener(new View.OnClickListener() {
	        	@Override
	        	public void onClick(View v) {
	        		if (trackingEnabled == true)
	        		{	
	        			stopTracking();
	        		}
	        		else
	        		{
	        			startTracking();
	        		}

	        	}
	        });
	    }
	    
	    /**
	     * @return Application's version code from the {@code PackageManager}.
	     */
	    private static int getAppVersion(Context context) {
	        try {
	            PackageInfo packageInfo = context.getPackageManager()
	                    .getPackageInfo(context.getPackageName(), 0);
	            return packageInfo.versionCode;
	        } catch (NameNotFoundException e) {
	            // should never happen
	            throw new RuntimeException("Could not get package name: " + e);
	        }
	    }

	    
	    
	    /**
	     * @return Application's {@code SharedPreferences}.
	     */
	    private SharedPreferences getGCMPreferences(Context context) {
	        // This sample app persists the registration ID in shared preferences, but
	        // how you store the regID in your app is up to you.
	        return getSharedPreferences(AM_MainActivity.class.getSimpleName(),
	                Context.MODE_PRIVATE);
	    }
	    
	    /**
	     * Gets the current registration ID for application on GCM service.
	     * <p>
	     * If result is empty, the app needs to register.
	     *
	     * @return registration ID, or empty string if there is no existing
	     *         registration ID.
	     */
	    private String getRegistrationId(Context context) {
	        final SharedPreferences prefs = getGCMPreferences(context);
	        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	        if (registrationId.isEmpty()) {
	            Log.i(TAG, "Registration not found.");
	            return "";
	        }
	        // Check if app was updated; if so, it must clear the registration ID
	        // since the existing regID is not guaranteed to work with the new
	        // app version.
	        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	        int currentVersion = getAppVersion(context);
	        if (registeredVersion != currentVersion) {
	            Log.i(TAG, "App version changed.");
	            return "";
	        }
	        Log.i(TAG, "Registration Id found " + registrationId);
	        return registrationId;
	    }
	    
    
	    /**
	     * Registers the application with GCM servers asynchronously.
	     * <p>
	     * Stores the registration ID and app versionCode in the application's
	     * shared preferences.
	     */
	    private void registerInBackground() {
	    	new GCMRegister(this).execute(gcm,null,null);
 	
	    }
	    
	    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.am_main);

        if (checkPlayServices()) {
        	//	Proceed with normal workflow        	
        	initializeLocationFeatures();
        		context = getApplicationContext();
                gcm = GoogleCloudMessaging.getInstance(this);
                regid = getRegistrationId(context);

                if (regid.isEmpty()) {
                    registerInBackground();
                }
        }
        else
        {
        	Log.d(TAG,"We must ask the user to use Google Play Services");
        }
    
	
	}


}
