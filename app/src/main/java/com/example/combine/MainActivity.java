package com.example.combine;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.io.OutputStream;
import java.net.HttpURLConnection;

// for dbconnection to app
//import kotlinx.coroutines.CoroutineScope;
//import kotlinx.coroutines.Dispatchers;
//import kotlinx.coroutines.launch;
//import kotlinx.coroutines.withContext;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private static final String TAG ="MainActivity.java";


    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private TextView Xaccelerometer;
    private TextView Yaccelerometer;
    private TextView Zaccelerometer;
    private TextView soundValue;
    private TextView jerkTextView;
    private TextView speedTextView;

//    private TextView longitude;

    private MediaRecorder mediaRecorder;
    private boolean isMicrophoneRecording = false;
    private final Handler handler = new Handler();

    private float[] previousAcceleration = new float[3];
    private long previousTime;

//    private static final int MICROPHONE_PERMISSION_CODE = 99;
//    private static final int PERMISSION_FINE_LOCATION = 100;
    private static final int PERMISSION_CODE=100;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    apiinterface  apiInterface;
    requestModel data;
    float jerk,speed,noise;
    String street_address;
    String pincode,traffic_measure,noise_measure,road_condition,phoneno;


    TextView streetTextView;

    String ANDROID_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Xaccelerometer = findViewById(R.id.textView7);
        Yaccelerometer = findViewById(R.id.textView11);
        Zaccelerometer = findViewById(R.id.textView12);
        jerkTextView = findViewById(R.id.textView6);
        soundValue = findViewById(R.id.textView2);
        speedTextView = findViewById(R.id.textView4);
//       longitude = findViewById(R.id.textView9);
        streetTextView=findViewById(R.id.textView10);

        ANDROID_ID = getAndroidId(this);
        Toast.makeText(this, "ANDROID_ID is  " + ANDROID_ID, Toast.LENGTH_SHORT).show();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometerSensor == null) {
                Toast.makeText(this, "Accelerometer is not present", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No Sensors are present", Toast.LENGTH_SHORT).show();
        }

        initializeLocationRequest();

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location locationUpdate = locationResult.getLastLocation();
//                Toast.makeText(this, "updateUIValues *******************", Toast.LENGTH_LONG).show();
                if (locationUpdate != null) {
                    updateUIValues(locationUpdate);
//                    Toast.makeText(this, "Location in updateGPSLocation is null", Toast.LENGTH_LONG).show();
                }
            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED
       || ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET}, PERMISSION_CODE);
        }
//        else {
//            updateGPSLocation();
//        }
    }

    private void initializeLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500)
                .setMinUpdateIntervalMillis(300)
                .build();
    }

    private void updateGPSLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    Toast.makeText(this, "updateGPS hello heloo hello", Toast.LENGTH_LONG).show();
                    updateUIValues(location);
                } else {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    Toast.makeText(this, "Location in updateGPSLocation is null", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        }
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    private void updateUIValues(Location location) {

        if(location != null) {
            double speed = location.getSpeed(); // Speed in meters/seconds
            speedTextView.setText(String.format(Locale.getDefault(), "%.2fm/s", speed));
            Log.d(TAG, "Speed: " + speed);
            Toast.makeText(this, "Speed updated:+++++" + speed + " m/s", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "1111111111111111111111111111111", Toast.LENGTH_SHORT).show();
        } else {
            speedTextView.setText(getString(R.string.spd_na));
            Log.d(TAG, "Location is null");
            Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(this, "*************", Toast.LENGTH_LONG).show();

        Geocoder geocoder=new Geocoder(this);
        Toast.makeText(this, "***************", Toast.LENGTH_LONG).show();
        try {
            assert location != null;
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

            assert addresses != null;
            streetTextView.setText(addresses.get(0).getAddressLine(0));
            pincode=addresses.get(0).getPostalCode();
            phoneno=addresses.get(0).getPhone();
        }
        catch(Exception e){
            streetTextView.setText("Address Not found");
        }
        street_address=streetTextView.getText().toString();
//        jerk_value=Float.parseFloat(jerkTextView.getText().toString());
//        speed_value=Float.parseFloat(speedTextView.getText().toString());
//        noise_value=Float.parseFloat(soundValue.getText().toString());


        try {
            jerk = Float.parseFloat(jerkTextView.getText().toString());
        } catch (Exception e) {
            jerk = 0;
        }Toast.makeText(this,"32323232",Toast.LENGTH_SHORT).show();

        try {
            speed = Float.parseFloat(speedTextView.getText().toString());
        } catch (Exception e) {
            speed = 0;
        }

        try {
            String noiseText = soundValue.getText().toString();
            noise = Float.parseFloat(noiseText.replace("dB", "").trim());
        } catch (Exception e) {
            noise = 0;
        }

        Toast.makeText(this, "################$$$$$$$$$$$$$$$$$$$$ " + phoneno, Toast.LENGTH_LONG).show();
        requestModel data = new requestModel(jerk,speed,noise,street_address);
        Call<model> call=apiController.getInstance()
                .getApiInterface()
//                .sendmodel(jerk,speed,noise,street_address);
                .sendmodel(data);
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.237.203:3002/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        Toast.makeText(this, "$$$$$$$$$$$$$$$$$$$$", Toast.LENGTH_LONG).show();
//
//        apiInterface = retrofit.create(apiinterface.class);
//
//        ModelData data=new ModelData(jerk,noise,speed,street_address);
//
//        Call<model> call = apiInterface.sendModel(data);

        Toast.makeText(this, "################33333333  "+speed, Toast.LENGTH_LONG).show();

        TextView responseTextView=findViewById(R.id.textView14);

        call.enqueue(new Callback<model>() {
            @Override
            public void onResponse(@NonNull Call<model> call, @NonNull Response<model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    model obj = response.body();
                    responseTextView.setText(obj.getMessage());
                } else {
                    responseTextView.setText("Response failed: " + response.message());
//                    Toast.makeText(this, "bbew", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<model> call, @NonNull Throwable t) {
                Log.e(TAG, "Request failed", t);
                responseTextView.setText(t.toString());
//                streetTextView.setText(t.toString());
            }
        });



    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void btnStartPressed(View view) {

        if (isMicrophonePresent()) {
            getMicrophonePermission();

        } else {
            Toast.makeText(this, "Working Microphone is not present", Toast.LENGTH_LONG).show();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            updateGPSLocation();
            Toast.makeText(this, "updateUIValues @@@@@@@@@@@@@@@@@@@@@@@@@@", Toast.LENGTH_LONG).show();
        }
    }

    public void btnStopPressed(View view) {
        if (sensorManager != null && accelerometerSensor != null) {
            sensorManager.unregisterListener(this);
            Toast.makeText(this, "btnStopPressed has unregisterListener", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Sensor is Not working in btnStopPressed", Toast.LENGTH_LONG).show();
            Xaccelerometer.setText(getString(R.string.btn_str));
            Yaccelerometer.setText(getString(R.string.btn_str));
            Zaccelerometer.setText(getString(R.string.btn_str));
        }
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            isMicrophoneRecording = false;
            mediaRecorder = null;
        }
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }


        //logic for opening the custom_dialog
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        View modalView =getLayoutInflater().inflate(R.layout.custom_dialog2,null);

        EditText traffic=modalView.findViewById(R.id.editTextText2);
        EditText noise=modalView.findViewById(R.id.editTextText4);
        EditText road=modalView.findViewById(R.id.editTextText5);
        Button btn_Save=modalView.findViewById(R.id.button3);

        alert.setView(modalView);

        final AlertDialog alertDialog=alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_Save.setOnClickListener(new View.OnClickListener() {

            String userSuggestion;
            @Override
            public void onClick(View v) {
//                String pincode,traffic_measure,noise_measure,road_condition;
                traffic_measure =traffic.getText().toString();
                noise_measure=noise.getText().toString();
                road_condition=road.getText().toString();

                userRequestModel data =new userRequestModel(pincode,traffic_measure,noise_measure,road_condition);

                Call<userResponseModel> call =apiController.getInstance()
                                .getApiInterface()
                                        .sendUserRequest(data);

                call.enqueue(new Callback<userResponseModel>() {
                    @Override
                    public void onResponse(Call<userResponseModel> call, Response<userResponseModel> response) {
                        userResponseModel res=response.body();
                        traffic.setText(res.getMessage());
                        userSuggestion=res.getMessage();
                    }

                    @Override
                    public void onFailure(Call<userResponseModel> call, Throwable t) {
                       traffic.setText(t.toString());
                        userSuggestion=t.toString();
                    }
                });
//        Toast.makeText(this,"User suggestion : " + userSuggestion,Toast.LENGTH_LONG ).show();
                alertDialog.dismiss();
                finish();
            }
        });


        alertDialog.show();

    }

    private boolean isMicrophonePresent() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void getMicrophonePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
        } else {
            startRecording();
            if (sensorManager != null && accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                Toast.makeText(this, "btnStartPressed has registerListener", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Sensor is Not working in btnStartPressed", Toast.LENGTH_LONG).show();
                Xaccelerometer.setText(getString(R.string.btn_str));
                Yaccelerometer.setText(getString(R.string.btn_str));
                Zaccelerometer.setText(getString(R.string.btn_str));
            }
        }
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(getRecordingFilePath());

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isMicrophoneRecording = true;
            updateDecibels();
        } catch (Exception e) {
            Log.e(TAG, "MediaRecorder prepare failed", e);
        }
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "recordingtest" + ".mp3");
        return file.getPath();
    }

    private void updateDecibels() {
        handler.postDelayed(() -> {
            if (isMicrophoneRecording) {
                double amplitude = mediaRecorder.getMaxAmplitude();
                if (amplitude > 5) {
                    double db = 20 * Math.log10(amplitude);
                    soundValue.setText(String.format(Locale.getDefault(), "%.2f dB", db));
                } else {
                    soundValue.setText("0.00 dB"); // Handle the case where amplitude is zero
                }
                updateDecibels();
            }
        }, 1000);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Xaccelerometer.setText(String.valueOf(event.values[0]));
            Yaccelerometer.setText(String.valueOf(event.values[1]));
            Zaccelerometer.setText(String.valueOf(event.values[2]));

            long currentTime = System.currentTimeMillis();
            float[] currentAcceleration = event.values;

            if (previousTime != 0) {
                float deltaTime = (currentTime - previousTime) / 1000.0f;
                if (deltaTime > 0) { // Ensure deltaTime is not zero or negative
                    float jerkX = (currentAcceleration[0] - previousAcceleration[0]) / deltaTime;
                    float jerkY = (currentAcceleration[1] - previousAcceleration[1]) / deltaTime;
                    float jerkZ = (currentAcceleration[2] - previousAcceleration[2]) / deltaTime;
                    float totalJerk = (float) Math.sqrt(jerkX * jerkX + jerkY * jerkY + jerkZ * jerkZ);
                    jerkTextView.setText(String.valueOf(totalJerk));
                }
            }
            previousTime = currentTime;
            System.arraycopy(currentAcceleration, 0, previousAcceleration, 0, currentAcceleration.length);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (fusedLocationProviderClient != null) {
//            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            int i=0;
            for (int result : grantResults) {
                i++;
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
          if (allPermissionsGranted) {
//                updateGPSLocation();
              }
               else {
                Toast.makeText(this, "Permissions not granted " + i, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}