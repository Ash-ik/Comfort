package com.askme.comfort.chat;

/**
 * Created by Amit on 10/28/2016.
 */

public class Chat_Message {

    public String text;
    public String name;
    public String photoURL;

    public Chat_Message()
    {
    }

    public Chat_Message(String t, String n, String purl)
    {
        this.text = t;
        this.name = n;
        this.photoURL = purl;
    }

    public String Get_Chat_Text()
    {
        return this.text;
    }

    public void Set_Chat_Text(String t)
    {
        this.text = t;
    }

    public String Get_Chat_Person_Name()
    {
        return this.name;
    }

    public void Set_Chat_Person_Name(String n)
    {
        this.name = n;
    }

    public String Get_Photo_URL()
    {
        return this.photoURL;
    }

    public void Set_Photo_URL(String purl)
    {
        this.photoURL = purl;
    }
}
