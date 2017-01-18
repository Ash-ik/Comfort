package com.askme.comfort.musicPlayerForBlindHelper;

/**
 * Created by Fahim Al Mahmud on 10/29/2016.
 */

public class Song {

    private long id;
    private String title;
    private String artist;
    private boolean like;
    private long albumID;


    public void setLike(boolean like) {
        this.like = like;
    }

    public Song(long songID, long albumID,String songTitle, String songArtist, boolean isLiked){
        id=songID;
        this.albumID=albumID;
        title=songTitle;
        artist=songArtist;

        like=isLiked;
    }

    public long getID(){return id;}
    public long getAlbumID(){return albumID;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public boolean isLiked(){return like;}

}
