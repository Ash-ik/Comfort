package com.askme.comfort;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.askme.comfort.musicPlayerForBlindHelper.Song;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Fahim Al Mahmud on 10/29/2016.
 */

public class musicPlayerForBlind extends Activity  implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{
    boolean neverSelectedSong=true;
    ValueAnimator colorAnimation;
    private ArrayList<Song> songList=new ArrayList<Song>();
    TextView textViewSong,textViewSinger;
    int currentSongPosition=0;
    Song currentSong;
    RelativeLayout musicPlayerForBlind;
    MediaPlayer player=new MediaPlayer();
    private Chronometer chronometer;
    long timeWhenStopped = 0;
    Bitmap bitmap;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player_for_blind_layout);


        chronometer = (Chronometer) findViewById(R.id.chronometer);
        musicPlayerForBlind=(RelativeLayout)findViewById(R.id.blindLayoutID);
        if(getActionBar()!=null)
        getActionBar().hide();

        textViewSong=(TextView)findViewById(R.id.textViewSong);
        textViewSinger=(TextView)findViewById(R.id.textViewSinger);
        getSongList();

/*        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                if(b.isLiked() && !a.isLiked())
                    return -1;
                return a.getTitle().compareTo(b.getTitle());
            }
        });*/
        currentSong=songList.get(0);
        textViewSong.setText(currentSong.getTitle());
        textViewSinger.setText(currentSong.getArtist());
        initMusicPlayer();
//        loadInitialCover();
        int colorFrom = getResources().getColor(android.R.color.holo_red_dark);
        int colorTo = getResources().getColor(android.R.color.holo_purple);
        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(10000); // milliseconds
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int backgroundColor=(int) animator.getAnimatedValue();

                findViewById(R.id.blindLayoutID).setBackgroundColor(backgroundColor);
            }

        });
        colorAnimation.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            colorAnimation.pause();
        }


/*        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSong.isLiked()==false)
                currentSong.setLike(true);
                else
                    currentSong.setLike(false);
            }
        });*/

        musicPlayerForBlind.setOnTouchListener(new OnSwipeTouchListener(musicPlayerForBlind.this) {

            @Override
            public void onDoubleClick() {
                super.onDoubleClick();

                if (player.isPlaying()) {
                    pausePlayer();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        colorAnimation.pause();
                    }
                } else {
                    resumePlayer();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        colorAnimation.resume();
                    }

                }
                // your on double click here
            }


            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                // your swipe up here
                playSong();
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                finish();
                // your swipe down here.
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                playPrev();
                // your swipe left here.
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();

                playNext();
                // your swipe right here.
            }
        });

    }

    private void loadInitialCover() {
        Song playSong = songList.get(0);
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                playSong.getID());
        ((info.abdolahi.CircularMusicProgressBar)findViewById(R.id.albam_cover)).setImageBitmap(getAlbumart(trackUri));
    }

    //method to retrieve song info from device
        public void getSongList(){
            //query external audio
            ContentResolver musicResolver = getContentResolver();
            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
            //iterate over results if valid
            if(musicCursor!=null && musicCursor.moveToFirst()){
                //get columns
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int albumIdColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM_ID);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                //add songs to list
                do {
                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    Log.d("SONGS",thisTitle);
                    songList.add(new Song(thisId, albumIdColumn,thisTitle, thisArtist,false));
                }
                while (musicCursor.moveToNext());
            }
        }
    //play a song
    public void playSong(){
        //play
        player.reset();
        //get song
        Song playSong = songList.get(currentSongPosition);
        currentSong=playSong;
        textViewSong.setText(currentSong.getTitle());
        textViewSinger.setText(currentSong.getArtist());

        //get id
        long currSong = playSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        //set the data source
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        ((info.abdolahi.CircularMusicProgressBar)findViewById(R.id.albam_cover)).setImageBitmap(getAlbumart(trackUri));
        player.start();
    }
    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
    //skip to previous track
    public void playPrev(){
        currentSongPosition--;
        if(currentSongPosition<0) currentSongPosition=songList.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
            currentSongPosition++;
            if(currentSongPosition>=songList.size()) currentSongPosition=0;
        playSong();
    }




    public void pausePlayer(){
        player.pause();
        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
    }

    public void resumePlayer(){
        player.start();
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.start();
    }

    public Bitmap getAlbumart(Uri path) {
        bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.comfort_temp_icon);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getApplicationContext(),path);
        byte [] data = mmr.getEmbeddedPicture();
        if(data!=null)
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        player.reset();
        super.onDestroy();
    }
}
