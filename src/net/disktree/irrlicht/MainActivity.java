package net.disktree.irrlicht;

import android.app.Activity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    private static final String TAG = "irrlicht";

    private boolean hasFlash;
    private boolean isFlashOn;
    private Camera camera;
    private Parameters params;
    private TextView btn;

    @Override
    public void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        Log.d( TAG, "IRRLICHT" );

        setContentView( R.layout.main );

        isFlashOn = false;

        getCamera();

        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature( PackageManager.FEATURE_CAMERA_FLASH );

        Log.d( TAG, "Device has flashlight "+hasFlash );

        if( !hasFlash ) {
            AlertDialog alert = new AlertDialog.Builder( MainActivity.this ).create();
            alert.setTitle( "Error" );
            alert.setMessage( "Your device doesn't support flash light" );
            alert.setButton( "OK", new DialogInterface.OnClickListener() {
                public void onClick( DialogInterface dialog, int which ) {
                    finish();
                }
            });
            alert.show();
            return;
        }

        btn = (TextView) findViewById( R.id.lightSwitch );
        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    turnOffFlash();
                } else {
                    turnOnFlash();
                }
                Log.d( TAG, "FLASHLIGHT "+isFlashOn );
            }
        });

        //turnOnFlash();
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffFlash();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( hasFlash ) turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( camera != null ) {
            camera.release();
            camera = null;
        }
    }

    private void getCamera() {
        if( camera == null ) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch( RuntimeException e ) {
                Log.e( "Camera Error. Failed to Open. Error: ", e.getMessage() );
            }
        }
    }

    private void turnOnFlash() {
        if( !isFlashOn ) {
            if( camera == null || params == null ) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode( Parameters.FLASH_MODE_TORCH );
            camera.setParameters( params );
            camera.startPreview();
            isFlashOn = true;
            // changing button/switch image
            //toggleButtonImage();
        }
    }

    private void turnOffFlash() {
        if( isFlashOn ) {
            if( camera == null || params == null ) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode( Parameters.FLASH_MODE_OFF );
            camera.setParameters( params );
            camera.stopPreview();
            isFlashOn = false;
            // changing button/switch image
            //toggleButtonImage();
        }
    }

    /*
    private void playSound(){
        if(isFlashOn){
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
        }else{
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
        }
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }
    */
}
