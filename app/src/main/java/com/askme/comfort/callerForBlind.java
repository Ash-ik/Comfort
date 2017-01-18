package com.askme.comfort;

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

/**
 * Created by Fahim Al Mahmud on 10/31/2016.
 */

public class callerForBlind extends Activity implements TextToSpeech.OnInitListener {

    public static final String introSpeech = "Slide left and right to browse between numbers.Slide up to add the selected number.Double Tap to Delete last digit.Single Tap to listen the number.Slide down to return to visually impaired menu.";
    private TextToSpeech mTts;
    private static final int MY_DATA_CHECK_CODE = 1234;
    LinearLayout dialarForBlindLayout;
    public int selectedPosition = 0;
    boolean neverSelectedMenu = true;
    String dialingNumber = "";
    Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonAstar, buttonHash, buttonCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialar_for_blind_layout);


        if (getActionBar() != null)
            getActionBar().hide();

        dialarForBlindLayout = (LinearLayout) findViewById(R.id.dialarForBlind);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9 = (Button) findViewById(R.id.button9);
        buttonAstar = (Button) findViewById(R.id.buttonAstar);
        buttonHash = (Button) findViewById(R.id.buttonHash);
        buttonCall = (Button) findViewById(R.id.buttonCall);


        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        dialarForBlindLayout.setOnTouchListener(new OnSwipeTouchListener(callerForBlind.this) {

            @Override
            public void onLongClick() {
                super.onLongClick();

                if(dialingNumber.length()==0)
                    speakBuddy("Please Enter some number first",0);
                else speakBuddy(dialingNumber,1);
                // your on click here
            }

            @Override
            public void onDoubleClick() {
                super.onDoubleClick();

                if(dialingNumber.length()!=0)
                {
                    speakBuddy(dialingNumber.charAt(dialingNumber.length() - 1) + " deleted",0);
                    dialingNumber = dialingNumber.substring(0, dialingNumber.length() - 1);
                }
                    else speakBuddy("Please Enter some number first",0);

                ((EditText)findViewById(R.id.phone_number)).setText(dialingNumber);
                initializeDialPad();
                // your on click here
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                Character[] arr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '0', '#'};
                if (selectedPosition == 13)
                    callNumber();
                else {
                    dialingNumber = dialingNumber + arr[selectedPosition];
                    speakBuddy(dialingNumber.charAt(dialingNumber.length() - 1) + " entered",0);
                    ((EditText)findViewById(R.id.phone_number)).setText(dialingNumber);
                    neverSelectedMenu = true;
                    selectedPosition=1;
                    initializeDialPad();
                }
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
                    selectedPosition = 13;
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
                } else if (++selectedPosition > 13)
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
        button0.setBackgroundResource(R.drawable.button_bg_round);
        button0.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
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
        button7.setBackgroundResource(R.drawable.button_bg_round);
        button7.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        button8.setBackgroundResource(R.drawable.button_bg_round);
        button8.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        button9.setBackgroundResource(R.drawable.button_bg_round);
        button9.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        buttonAstar.setBackgroundResource(R.drawable.button_bg_round);
        buttonAstar.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        buttonHash.setBackgroundResource(R.drawable.button_bg_round);
        buttonHash.setTextColor(getResources().getColor(android.R.color.widget_edittext_dark));
        buttonCall.setBackgroundResource(R.drawable.call_blind);
    }

    public void animateSelectedActivity() {
        initializeDialPad();
        switch (selectedPosition) {
            case 11:
                button0.setBackgroundResource(R.drawable.button_bg_selected);
                button0.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("0",0);
                break;
            case 1:
                button1.setBackgroundResource(R.drawable.button_bg_selected);
                button1.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("1",0);
                break;
            case 2:
                button2.setBackgroundResource(R.drawable.button_bg_selected);
                button2.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("2",0);
                break;
            case 3:
                button3.setBackgroundResource(R.drawable.button_bg_selected);
                button3.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("3",0);
                break;
            case 4:
                button4.setBackgroundResource(R.drawable.button_bg_selected);
                button4.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("4",0);
                break;
            case 5:
                button5.setBackgroundResource(R.drawable.button_bg_selected);
                button5.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("5",0);
                break;
            case 6:
                button6.setBackgroundResource(R.drawable.button_bg_selected);
                button6.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("6",0);
                break;
            case 7:
                button7.setBackgroundResource(R.drawable.button_bg_selected);
                button7.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("7",0);
                break;
            case 8:
                button8.setBackgroundResource(R.drawable.button_bg_selected);
                button8.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("8",0);
                break;
            case 9:
                button9.setBackgroundResource(R.drawable.button_bg_selected);
                button9.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("9",0);
                break;
            case 10:
                buttonAstar.setBackgroundResource(R.drawable.button_bg_selected);
                buttonAstar.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("*",0);
                break;
            case 12:
                buttonHash.setBackgroundResource(R.drawable.button_bg_selected);
                buttonHash.setTextColor(getResources().getColor(android.R.color.white));
                speakBuddy("#",0);
                break;
            case 13:
                buttonCall.setBackgroundResource(R.drawable.call_icon);
                speakBuddy("Call",0);
                break;
            default:
                break;
        }
    }

    public void callNumber() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + dialingNumber));
        callIntent.putExtra("com.android.phone.extra.slot", 0);
        callIntent.putExtra("com.android.phone.force.slot", true);
        callIntent.putExtra("Cdma_Supp", true);
        startActivity(callIntent);


        startActivity(callIntent);
    }

}
