package net.disktree.irrlicht;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Switch;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "irrlicht";
    private static final Boolean LOG_ENABLED = true;

    private static final void trace( String msg ) {
        if( LOG_ENABLED ) {
            Log.d( LOG_TAG, msg );
        }
    }

    private Boolean hasFlash;
    private Boolean isFlashOn = false;
    private Camera camera;
    private Parameters params;
    private ImageButton btn;
    private MediaPlayer btnSound;

    @Override
    public void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        setContentView( R.layout.main );

        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature( PackageManager.FEATURE_CAMERA_FLASH );
        //trace( "Device has flashlight: "+hasFlash );

        if( hasFlash ) {

            getCamera();
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );

            btn = (ImageButton) findViewById( R.id.btn );
            btn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( isFlashOn ) turnOffFlash(); else turnOnFlash();
                }
            });

            //showNotification();

        } else {

            AlertDialog alert = new AlertDialog.Builder( MainActivity.this ).create();
            alert.setTitle( "Error" );
            alert.setMessage( "Your device doesn't support flash light control" );
            alert.setButton( "OK", new DialogInterface.OnClickListener() {
                public void onClick( DialogInterface dialog, int which ) {
                    finish();
                }
            });
            alert.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffFlash();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if( hasFlash ) turnOnFlash();
        turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCamera(); // Get camera params on start
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
                trace( "Camera Error. Failed to Open. Error: "+ e.getMessage() );
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
            toggleButtonImage();
            playSound( R.raw.on );
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
            toggleButtonImage();
            playSound( R.raw.off );
        }
    }

    private void toggleButtonImage() {
        int id;
        if( isFlashOn ) {
            id = R.drawable.btn_on;
        } else {
            id = R.drawable.btn_off;
        }
        btn.setImageResource( id );
    }

    private void playSound( int id ) {
        MediaPlayer mp = MediaPlayer.create( MainActivity.this, id );
        mp.setOnCompletionListener( new OnCompletionListener() {
            @Override
            public void onCompletion( MediaPlayer mp ) {
                mp.release();
            }
        });
        mp.start();
    }

    /*
    private void showNotification() {

        Notification.Builder mBuilder = new Notification.Builder( this )
            .setSmallIcon( R.drawable.ic_stat_image_flare )
            .setContentTitle( "Irrlicht" );
            //.setContentText( "Hello World!" );

        Intent resultIntent = new Intent( this, MainActivity.class );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int mId = 222;
        mNotificationManager.notify(mId, mBuilder.build());
    }
    */
}
