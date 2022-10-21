package com.example.alarmmangaer.service;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.alarmmangaer.activity.R;
import com.example.alarmmangaer.receiver.AlarmReceiver;
import com.example.alarmmangaer.ultil.Constants;

import butterknife.BindView;

public class AlarmService extends Service {
    private static final String TAG = "";
    MediaPlayer mediaPlayer; // this object to manage media
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String on_Off = intent.getExtras().getString("ON_OFF");
        switch (on_Off) {
            case Constants.ADD_INTENT: // if string like this set start media
                // this is system default alarm alert uri
                Uri uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                // create mediaPlayer object
                mediaPlayer = MediaPlayer.create(this, uri);
                mediaPlayer.start();
                //Notification
                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

                    NotificationChannel channel= new NotificationChannel("My Notification","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager =getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                String message = getString(R.string.alarm_name);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"My Notification");
                builder.setContentTitle("Alarm Manager");
                builder.setContentText(message);
                builder.setSmallIcon(R.drawable.clock);
                builder.setAutoCancel(true);
                NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
                managerCompat.notify(1,builder.build());

                Log.d(" builder error", "onStartCommand: ");
                break;
            case Constants.OFF_INTENT:
                // this check if user pressed cancel
                // get the alarm cancel id to check if equal the
                // pendingIntent'trigger id(pendingIntent request code)
                // the AlarmReceiver.pendingIntentId is taken from AlarmReceiver
                // when one pendingIntent trigger
                int alarmId = intent.getExtras().getInt("AlarmId");
                // check if mediaPlayer created or not and if media is playing and id of
                // alarm and trigger pendingIntent is same  then stop music and reset it
                if (mediaPlayer != null && mediaPlayer.isPlaying() && alarmId == AlarmReceiver.pendingId) {
                    // stop media
                    mediaPlayer.stop();
                    // reset it
                    mediaPlayer.reset();
                }
                break;


        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}