package hung.dut.edu.playallmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import hung.dut.edu.playallmusic.adapter.OfficeListAdapter;
import hung.dut.edu.playallmusic.data.SongsManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String BACK_SONG = "BACK";
    public static final String NEXT_SONG = "NEXT";
    public static final String CHANG_SONG = "CHANG SONG";
    boolean PLAYING, SERVICE_RUNNING;
    static Handler handler;
    ImageView bt_play, bt_back, bt_next;
    ListView listview;
    TextView txtname, txt_total, txt_current;
    OfficeListAdapter listoffice;
    Intent intent;
    private int indexSong;
    SongsManager songsManager;
    private static Intent serviceIntent;
    int duration;
    BroadcastReceiver mReceiver;
    SeekBar seekBar;
    HashMap<String, String> song;
    ArrayList<HashMap<String, String>> songlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UI();
        intent = new Intent();
        serviceIntent = new Intent(MainActivity.this, PlayMusiceService.class);
        bt_play.setOnClickListener(this);
        bt_back.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        songsManager = new SongsManager();
        songsManager.setContent(getApplicationContext());
        listview = (ListView) findViewById(R.id.listView);
        songlist = songsManager.getPlayList();
        listoffice = new OfficeListAdapter(getApplicationContext(), songlist);
        listview.setAdapter(listoffice);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (!PLAYING) {
                    HashMap<String, String> song = listoffice.getItem(position);
                    txtname.setText(song.get(SongsManager.TITLE_SONG));
                    indexSong = position;
                }  //kích bất kì bài hát nào trên list khi  đang chơi 1 bài hát khác -> chơi luôn
                else {
//                    bt_play.setImageResource(R.drawable.ic_pause_circle24dp);
                    song = listoffice.getItem(position);
                    txtname.setText(song.get(SongsManager.TITLE_SONG));
                    indexSong = position;
                    // chang  song
                    intent.setAction(MainActivity.CHANG_SONG);
                    intent.putExtra(CHANG_SONG, indexSong);
                    sendBroadcast(intent);
                    Log.e("TAG11", " click  1  item /listview  để phát dù  đang phát  1 bài hát khác  ");
                }
            }
        });
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_TIME_CHANGED));
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("TAG111", " get value  of MEDIA_RUNNING");
                if (intent.getAction().equals(PlayMusiceService.SERVICERUN)) {
                    SERVICE_RUNNING = intent.getBooleanExtra(PlayMusiceService.SERVICERUN, false);
                    if (SERVICE_RUNNING)Log.e("TAG11","aaaaaaaa");
                } else if (intent.getAction().equals(PlayMusiceService.SONG_PLAYING)) {
                    PLAYING = intent.getBooleanExtra(PlayMusiceService.SONG_PLAYING, false);
                } else if (intent.getAction().equals(PlayMusiceService.DURATION)) {
                    duration = intent.getIntExtra(PlayMusiceService.DURATION, -1);
                    uploadProgressBar(duration);
                }
            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int a = msg.arg1;
                seekBar.setMax(a);
                super.handleMessage(msg);
            }
        };
    }

    private void UI() {
        bt_play = (ImageView) findViewById(R.id.bt_play);
        bt_back = (ImageView) findViewById(R.id.bt_back);
        bt_next = (ImageView) findViewById(R.id.bt_next);
        txtname = (TextView) findViewById(R.id.txt_namesong);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txt_current = (TextView) findViewById(R.id.txt_timesend);
        txt_total = (TextView) findViewById(R.id.txt_timestart);
        bt_play.setImageResource(R.drawable.ic_play_circle);
    }

    // Chuyển số lượng milli giây thành một String có ý nghĩa.
    private String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) milliseconds);
        return minutes + ":" + seconds;
    }

    private void uploadProgressBar(final int duration) {
        if (duration != 0) {
            handler.postDelayed(new Runnable() {
                int t = 0;
                String total;

                @Override
                public void run() {
                    total = millisecondsToString(t);
                    Message message = new Message();
                    message.obj = total;
                    handler.sendMessage(message);
                    handler.postDelayed(this, 1000);
                    if (t == duration) handler.removeCallbacksAndMessages(null);
                    t += 1000;
                }
            }, 1000);
            Log.e("TAG11", " upload seekbar");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_play:
                if (!PLAYING) {
                    bt_play.setImageResource(R.drawable.ic_pause_circle24dp);
                    //   nếu  service chạy lần đầuthì =>> startService() đễ gọi tới  hàm  onstartCommand()/PlayMusicService();
                    if (!SERVICE_RUNNING) {
                        // chay  lan  dau
                        serviceIntent.putExtra("index", indexSong);
                        this.startService(serviceIntent);
                        // intent.putParcelableArrayListExtra(LIST, (ArrayList<? extends Parcelable>) songlist);
                        Log.e("TAG11", indexSong + "...send data/inten->service");
                    } // nếu service đang chạy, và chưa phát bài nào thì ném  broadcaset Activity.PLAY -> service
                    else {
                        intent.setAction(PLAY);
                        sendBroadcast(intent);
                        Log.e("TAG11", "Play song index /list:" + indexSong);
                    }
                }
                //Pause ...
                else {
                    bt_play.setImageResource(R.drawable.ic_play_circle);
                    Log.e("TAG11", "pause/activity");
                    intent.setAction(PAUSE);
                    sendBroadcast(intent);
                    PLAYING = false;
                }
                break;
            case R.id.bt_back:
                txtname.setText(song.get(SongsManager.TITLE_SONG));
                intent.setAction(PLAY);
                intent.putExtra(BACK_SONG, --indexSong);
                sendBroadcast(intent);
                Log.e("TAG11", "back song ");
                break;
            case R.id.bt_next:
                intent.setAction(PLAY);
                intent.putExtra(NEXT_SONG, ++indexSong);
                sendBroadcast(intent);
                Log.e("TAG11", "next song");
                break;
        }
    }


  /*  */

    /**
     * Show notification
     *//*
    // loại   bình thương
    private void showAlarmNotification() {
// Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(mInformationAlarm.getTime() + " " + getString(R.string.text_notification_stop_alarm))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setOngoing(false);
// Creates an Intent for the Activity
        Intent notifyIntent =
                new Intent(this, MainActivity.class);
        notifyIntent.putExtra("mId", mInformationAlarm.getId());
        notifyIntent.putExtra("mIsCheckNotification", true);
// Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
// Puts the PendingIntent into the notification builder
        builder.setContentIntent(notifyPendingIntent);
// Notifications are issued by sending them to the
// NotificationManager system service.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// Builds an anonymous Notification object from the builder, and
// passes it to the NotificationManager
        mNotificationManager.notify(0, builder.build());
    }*/
    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        Log.e("TAG11", " stop service  and unregister Receiver");
        super.onDestroy();
    }
}

