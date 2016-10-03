package hung.dut.edu.playallmusic;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import hung.dut.edu.playallmusic.data.SongsManager;

public class PlayMusiceService extends Service implements MediaPlayer.OnCompletionListener {
    public static final String SONG_PLAYING = " SONGPLAY";
    public static final String DURATION = "Duration";
    public static final String SERVICERUN = "SERVICERUN";
    public static final String SONG_PAUSE = "SONG_PAUSE";
    protected static MediaPlayer mp;
    private Intent intentService;
    private ArrayList<HashMap<String, String>> listsong;
    BroadcastReceiver sReceiver;
    private boolean isCheckStart;

    @Override
    public void onCreate() {
        mp = new MediaPlayer();
        intentService = new Intent();
        mp.setOnCompletionListener(this);
        mp.reset();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        SongsManager songsManager = new SongsManager();
        listsong = new ArrayList<>();
        listsong = songsManager.getPlayList();
        registerReceiver(sReceiver, new IntentFilter(Intent.ACTION_TIME_CHANGED));
        sReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(MainActivity.PLAY)) {
                    int index = intent.getIntExtra("index", 0);
                    playSong(index);
                } else if (intent.getAction().equals(MainActivity.PAUSE)) {
                    pauseSong();
                } else if (intent.getAction().equals(MainActivity.NEXT_SONG)) {
                    Log.e("TAG11", " next song ");
                } else if (intent.getAction().equals((MainActivity.BACK_SONG))) {
                    Log.e("TAG11", " back song ");
                }
            }
        };

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start  service  lần đầu
        int indexSong = intent.getIntExtra("index", 0);
        if (!isCheckStart) {
            isCheckStart = true;
            intentService.setAction(SERVICERUN);
            intentService.putExtra(SERVICERUN, isCheckStart);
            if(isCheckStart)
            {
                Log.e("TAG11",SERVICERUN.toString());
            }

            sendBroadcast(intentService);
            playSong(indexSong);
            Log.e("TAG11", " success start service");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public PlayMusiceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // override method MediaPlayer.OnCompletionListener
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.e("TAG11", "phát hết bài hát ");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(sReceiver);
        mp.stop();
        Log.e("TAG11", "onDestroy() -->stop service");
        super.onDestroy();
    }

    //  method  play song / list  of interger song index
    public void playSong(int songIndex) {
        mp.reset();
        try {
            if (mp.isPlaying()) mp.stop();
            mp.reset();
            mp.setDataSource(listsong.get(songIndex).get(SongsManager.PATH_SONG));
            mp.prepare();
            mp.start();
            Log.e("TAG11", "play service");
            intentService.putExtra(SONG_PLAYING, true);
            intentService.putExtra(DURATION, mp.getDuration());
            sendBroadcast(intentService);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Log.d("IllegarArgument", e.getMessage());
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
            intentService.putExtra(SONG_PLAYING, false);
            sendBroadcast(intentService);
        }
    }
  /*  *//**
     * thí method is show notifi by foreground
     *//*
    //loai    /barcgroud ->  ngăn chan kill app
    private void showForegroundNotification() {
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            showAlarmNotification();
            return;
        }
// Create intent that will bring our app to the front, as if it was tapped in the app
// launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.putExtra("mId", mInformationAlarm.getId());
        showTaskIntent.putExtra("mIsCheckNotification", true);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                1000,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText( + " " + getString(R.string.text_notification_stop_alarm))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }*/

}
