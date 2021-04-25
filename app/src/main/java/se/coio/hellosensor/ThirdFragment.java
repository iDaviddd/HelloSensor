package se.coio.hellosensor;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class ThirdFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView accelerometerTV;
    private boolean wasFlat = false;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        accelerometerTV = view.findViewById(R.id.accelerometerTV);

        view.findViewById(R.id.accelerometer_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ThirdFragment.this)
                        .navigate(R.id.action_ThirdFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        // to stop the listener and save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = Math.round(event.values[0]);
        float y = Math.round(event.values[1]);
        float z = Math.round(event.values[2]);

        if(z >= 9 && z <= 11 && Math.abs(x) < 1 && Math.abs(y) < 1) {
            wasFlat = true;
            if(!wasFlat){

            }
            accelerometerTV.setText("Phone is flat!");
            getView().setBackgroundColor(Color.GREEN);
        } else {
            wasFlat = false;
            accelerometerTV.setText("Accelerometer values: \nX: " + x + "\nY: " + y + "\nZ: " + z);
            getView().setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not in use
    }
}
