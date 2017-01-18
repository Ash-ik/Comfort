package com.askme.comfort;

/**
 * Created by Fahim Al Mahmud on 10/31/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class alarmForBlind extends Activity implements TextToSpeech.OnInitListener {

    public static final String introSpeech = "Slide left and right to browse between numbers.Slide up to add the selected number.Double Tap to Delete last digit.Single Tap to listen the number.Slide down to return to visually impaired menu.";
    private TextToSpeech mTts;
    private static final int MY_DATA_CHECK_CODE = 1234;
    LinearLayout startupLayout;
    public int selectedPosition = 0;
    boolean neverSelectedMenu = true;
    String dialingNumber = "";
    Button button1, button2, button3, button4, button5, button6,buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_for_blind_layout);


        if (getActionBar() != null)
            getActionBar().hide();

        startupLayout = (LinearLayout) findViewById(R.id.startupLayout);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        startupLayout.setOnTouchListener(new OnSwipeTouchListener(alarmForBlind.this) {

            @Override
            public void onLongClick() {
                super.onLongClick();

/*                if(dialingNumber.length()==0)
                    speakBuddy("Please Enter some number first",0);
                else speakBuddy(dialingNumber,1);*/
                // your on click here
            }

            @Override
            public void onDoubleClick() {
                super.onDoubleClick();

/*                if(dialingNumber.length()!=0)
                {
                    speakBuddy(dialingNumber.charAt(dialingNumber.length() - 1) + " deleted",0);
                    dialingNumber = dialingNumber.substring(0, dialingNumber.length() - 1);
                }
                else speakBuddy("Please Enter some number first",0);

                ((EditText)findViewById(R.id.phone_number)).setText(dialingNumber);
                initializeDialPad();*/
                // your on click here
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
/*                Character[] arr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '0', '#'};
                if (selectedPosition == 13)
                    callNumber();
                else {
                    dialingNumber = dialingNumber + arr[selectedPosition];
                    speakBuddy(dialingNumber.charAt(dialingNumber.length() - 1) + " entered",0);
                    ((EditText)findViewById(R.id.phone_number)).setText(dialingNumber);
                    neverSelectedMenu = true;
                    selectedPosition=1;
                    initializeDialPad();
                }*/
                // your swipe up here
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
                if (neverSelectedMenu) {
                    selectedPosition = 1;
                    animateSelectedActivity();
                    neverSelectedMenu = false;
                } else if (--selectedPosition < 1)
                    selectedPosition = 7;
                animateSelectedActivity();
                // your swipe left here.
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (neverSelectedMenu) {
                    selectedPosition = 1;
                    animateSelectedActivity();
                    neverSelectedMenu = false;
                } else if (++selectedPosition > 7)
                    selectedPosition = 1;
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

    public void speakBuddy(String speech,int cmd) {
        if(cmd==1)
            for(int i=0;i<speech.length();i++)
            {
                mTts.stop();
                mTts.speak(speech.charAt(i)+"", TextToSpeech.QUEUE_FLUSH, null);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        else
        {
            mTts.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    public void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }


    public void initializeDialPad() {
        button1.setBackgroundResource(R.drawable.button_bg_round);
        button1.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        button2.setBackgroundResource(R.drawable.button_bg_round);
        button2.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        button3.setBackgroundResource(R.drawable.button_bg_round);
        button3.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        button4.setBackgroundResource(R.drawable.button_bg_round);
        button4.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        button5.setBackgroundResource(R.drawable.button_bg_round);
        button5.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        button6.setBackgroundResource(R.drawable.button_bg_round);
        button6.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        buttonSubmit.setBackgroundResource(R.drawable.set_alarm_background);
    }

    public void animateSelectedActivity() {
        initializeDialPad();
        switch (selectedPosition) {
            case 1:
                button1.setBackgroundResource(R.drawable.button_bg_selected);
                button1.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("Date",0);
                break;
            case 2:
                button2.setBackgroundResource(R.drawable.button_bg_selected);
                button2.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("Month",0);
                break;
            case 3:
                button3.setBackgroundResource(R.drawable.button_bg_selected);
                button3.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("Year",0);
                break;
            case 4:
                button4.setBackgroundResource(R.drawable.button_bg_selected);
                button4.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("Hour",0);
                break;
            case 5:
                button5.setBackgroundResource(R.drawable.button_bg_selected);
                button5.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("Minute",0);
                break;
            case 6:
                button6.setBackgroundResource(R.drawable.button_bg_selected);
                button6.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("Am.Or PM",0);
                break;
            case 7:
                buttonSubmit.setBackgroundResource(R.drawable.set_alarm_pressed_background);
                speakBuddy("Set.Alarm",0);
                break;
            default:
                break;
        }
    }


}

