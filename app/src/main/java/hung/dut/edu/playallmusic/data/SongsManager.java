package hung.dut.edu.playallmusic.data;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

/**
 * Created by Administrator on 27/09/2016.
 */
public class SongsManager  {
    // SDCard Path
    //final String MEDIA_PATH = new String(MediaStore.Audio.Media.getContentUri("external").toString());
    final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/Music/";
    public static final String PATH_SONG = "songPath";
    public static final String TITLE_SONG = "songTitle";
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private String mp3Pattern = ".mp3";
    private Context content;

    // Constructor
    public SongsManager() {

    }

    /**
     * Function to read all mp3 files and store the details in
     * ArrayList
     */
    public ArrayList<HashMap<String, String>> getPlayList() {
        Log.e("TAG11", MEDIA_PATH);
        if (MEDIA_PATH != null) {
            File home = new File(MEDIA_PATH);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {

                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }
                }
            }
        }
        // return songs list array
        Log.e("TAG11", songsList.size() + "");
        return songsList;
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }

                }
            }
        }
    }

    private void addSongToList(File song) {
        if (song.getName().endsWith(mp3Pattern)) {
            HashMap<String, String> songMap = new HashMap<String, String>();
            songMap.put(TITLE_SONG,
                    song.getName().substring(0, (song.getName().length() - 4)));
            songMap.put(PATH_SONG, song.getPath());
            // Adding each song to SongList
            songsList.add(songMap);
        }
    }
 /* note:
 *  cố gắng  get all information of file
 *  Đễ  upsload  activity đầy đủ  thông tin hơn
 *  https://developer.android.com/guide/topics/providers/content-provider-basics.html
 *  http://stackoverflow.com/questions/3750903/how-can-getcontentresolver-be-called-in-android
 *  http://stackoverflow.com/questions/2389225/android-how-to-get-a-files-creation-date
 *  http://stackoverflow.com/questions/31399122/how-to-access-storage-emulated-0
 *  */

    /*public ArrayList<HashMap<String, String>> informationSong() {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/");
            Log.e("TAG11", Environment.getExternalStorageDirectory().getPath() + "/Music/");
            cursor = content.getContentResolver().query(uri, projection, selection, null, sortOrder);
            Log.e("TAG11", "content:" + content.toString());
            if (songsList != null && cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put(projection[0], cursor.getString(0));
                    song.put(projection[1], cursor.getString(1));
                    song.put(projection[2], cursor.getString(2));
                    song.put(projection[3], cursor.getString(3));
                    song.put(projection[4], cursor.getString(4));
                    arrayList.add(song);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e("TAG11", e.toString());
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        Log.e("TAG11", "return arraylist of: " + arrayList.size());
        return arrayList;

    }

    Log.e("TAG11","return Null");
    return null;
}*/

    public void setContent(Context content) {
        this.content = content;
    }
}

