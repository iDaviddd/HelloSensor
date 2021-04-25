package se.coio.hellosensor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import static androidx.core.content.ContextCompat.getSystemService;

public class SecondFragment extends Fragment implements SensorEventListener {

    private SensorManager SensorManage;
    // define the compass picture that will be use
    private ImageView compassimage;
    // record the angle turned of the compass picture
    private float DegreeStart = 0f;
    TextView DegreeTV;

    private boolean wasPointingNorthState = false;

    private SoundPool soundPool;
    private int sound1;
    Vibrator vib;

    static final float ALPHA = 0.25f;
    protected float[] magSensorVals;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compassimage = (ImageView) view.findViewById(R.id.compass_image);
        // TextView that will display the degree
        DegreeTV = (TextView) view.findViewById(R.id.DegreeTV);
        // initialize your android device sensor capabilities
        SensorManage = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        soundPool = new SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build();
        sound1 = soundPool.load(this.getContext(), R.raw.sound1, 1);

        vib = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        SensorManage.registerListener(this, SensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        // to stop the listener and save battery
        SensorManage.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get angle around the z-axis rotated
        magSensorVals = lowPass(event.values.clone(), magSensorVals);

        float degree = Math.round(magSensorVals[0]);
        if (degree <= 15 || degree >= 345) {
            DegreeTV.setText("Heading: North");
            if (wasPointingNorthState == false) {
                wasPointingNorthState = true;
                soundPool.play(sound1, 1, 1, 0, 0, 1);

                // Start without a delay
                // Vibrate for 100 milliseconds
                // Sleep for 1000 milliseconds
                long[] pattern = {0, 100, 1000};

                // The '0' here means to repeat indefinitely
                // '0' is actually the index at which the pattern keeps repeating from (the start)
                // To repeat the pattern from any other point, you could increase the index, e.g. '1'
                vib.vibrate(150);
                getView().setBackgroundColor(Color.GREEN);
            }
        } else {
            if(wasPointingNorthState = true) {
                //We set color to back to normal.
                getView().setBackgroundColor(Color.TRANSPARENT);
            }
            wasPointingNorthState = false;
            DegreeTV.setText("Heading: " + Float.toString(degree) + " degrees");
        }
        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassimage.startAnimation(ra);
        DegreeStart = -degree;
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        if(Math.abs(output[0] - input[0]) > 180) { //Necessary to prevent a flips when going from 360 to 0 degrees.
            return input;
        }
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}