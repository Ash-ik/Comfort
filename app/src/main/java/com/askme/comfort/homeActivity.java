package com.askme.comfort;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.askme.comfort.chat.Chat_Panel;


/**
 * Created by Fahim Al Mahmud on 10/29/2016.
 */

public class homeActivity extends Activity implements TextToSpeech.OnInitListener{
    public static final String introSpeech="Welcome to the Comfort.An app developed by kuweit 32 bit. Slide left and right to browse between options.Slide up to choose the selected item.Slide down to close our app. If your device has a physical keyboard, you can use the directional keys.";
    private TextToSpeech mTts;
    private TextView blindTextView,dumbTextView,deafTextView;
    public int selectedPosition=0;
    boolean neverSelectedMenu=true;
    public View selectedView;
    LinearLayout startupLayout;
    private static final int MY_DATA_CHECK_CODE = 1234;
    private RelativeLayout blindLayout,dumbLayout,deafLayout;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_layout);
        startupLayout=(LinearLayout)findViewById(R.id.startupLayout);
        Log.d("Testing","pp");

        if(getActionBar()!=null)
            getActionBar().hide();

        blindLayout=(RelativeLayout)findViewById(R.id.home_blind_menu);
        dumbLayout=(RelativeLayout)findViewById(R.id.home_dumb_menu);
        deafLayout=(RelativeLayout) findViewById(R.id.home_deaf_menu);

        blindTextView=(TextView)findViewById(R.id.home_blind_textView);
        dumbTextView=(TextView)findViewById(R.id.home_dumb_textView);
        deafTextView=(TextView)findViewById(R.id.home_deaf_textView);
        //TTS
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);



        startupLayout.setOnTouchListener(new OnSwipeTouchListener(homeActivity.this) {

            @Override
            public void onClick() {
                super.onClick();
                if(neverSelectedMenu)
                {
                    selectedPosition=0;
                    animateSelectedActivity();
                    neverSelectedMenu=false;
                }
                animateSelectedActivity();
                // your on click here
            }


            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                if(neverSelectedMenu)
                {
                    selectedPosition=0;
                    animateSelectedActivity();
                    neverSelectedMenu=false;
                }
                // your swipe up here
                openSelectedActivity();
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
                if(neverSelectedMenu)
                {
                    selectedPosition=0;
                    animateSelectedActivity();
                    neverSelectedMenu=false;
                }
                else if(--selectedPosition<0)
                    selectedPosition=2;
                animateSelectedActivity();
                // your swipe left here.
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();

                if(neverSelectedMenu)
                {
                    selectedPosition=0;
                    animateSelectedActivity();
                    neverSelectedMenu=false;
                }

                else if(++selectedPosition>2)
                    selectedPosition=0;
                animateSelectedActivity();
                // your swipe right here.
            }
        });

    }
    @Override
    public void onInit(int status) {
        mTts.speak(introSpeech,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);
    }
    public void speakBuddy(String speech)
    {
        mTts.speak(speech,TextToSpeech.QUEUE_FLUSH,null);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            }
            else
            {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    public void onDestroy()
    {
        // Don't forget to shutdown!
        if (mTts != null)
        {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }
    public void openSelectedActivity()
    {
        Intent intent = null;
        switch (selectedPosition)
        {
            case 0:intent=new Intent(homeActivity.this,blindHomeActivity.class);
                break;
            case 1:intent=new Intent(homeActivity.this,Chat_Panel.class);break;
            case 2:speakBuddy("This activity is under construction");break;
            default:break;
        }
        if(intent!=null)
        startActivity(intent);
    }
    public void animateSelectedActivity()
    {
        blindTextView.setTextColor(getResources().getColor(android.R.color.white));
        dumbTextView.setTextColor(getResources().getColor(android.R.color.white));
        deafTextView.setTextColor(getResources().getColor(android.R.color.white));
        switch (selectedPosition)
        {
            case 0:
                blindTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                speakBuddy("Menu for Visually impaired people");
                break;
            case 1:
                dumbTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                speakBuddy("Menu for Voice impaired people");
                break;
            case 2:
                deafTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                speakBuddy("Menu for Hearing impaired people");
                break;
            default:break;
        }
    }

}
