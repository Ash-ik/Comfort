package com.askme.comfort;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;


public class blindHomeActivity extends Activity implements CircleLayout.OnItemSelectedListener,
		CircleLayout.OnRotationFinishedListener, CircleLayout.OnCenterClickListener,TextToSpeech.OnInitListener,CircleLayout.OnItemOpenListener{
	public static final String ARG_LAYOUT = "layout";
	public String selectedItem="";
	public static final String introSpeech="Menu for Visually impaired people.Slide left and right to browse between options.Slide up to choose the selected item.Slide down to return to previous menu.";

	private TextView selectedTextView;
	private TextToSpeech mTts;
	private static final int MY_DATA_CHECK_CODE = 1234;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sample);

		//TTS
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);


		// Set listeners
		CircleLayout circleMenu = (CircleLayout) findViewById(R.id.main_circle_layout);
		circleMenu.setOnItemSelectedListener(this);
		circleMenu.setOnItemOpenListener(this);
		circleMenu.setOnRotationFinishedListener(this);
		circleMenu.setOnCenterClickListener(this);

		selectedTextView = (TextView) findViewById(R.id.main_selected_textView);
		selectedTextView.setText(((CircleImageView) circleMenu
				.getSelectedItem()).getName());
	}

	@Override
	public void onItemSelected(View view, String name) {
		selectedTextView.setText(name);

		switch (view.getId()) {
			case R.id.blindCaller:
				speakBuddy("Call");
				selectedItem="Call";
				break;
			case R.id.blindMessage:
				speakBuddy("Messages");
				selectedItem="Messages";
				break;
			case R.id.blindMusic:
				speakBuddy("Music");
				selectedItem="Music";
				break;
			case R.id.blindVoiceRecorder:
				speakBuddy("Voice Recorder");
				selectedItem="Voice Recorder";
				break;
			case R.id.blindAlarm:
				speakBuddy("Alarm");
				selectedItem="Alarm";
				break;
			case R.id.blindSettings:
				speakBuddy("Settings");
				selectedItem="Settings";
				break;
		}
	}

	@Override
	public void onRotationFinished(View view, String name) {
		Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2,
				view.getHeight() / 2);
		animation.setDuration(250);
		view.startAnimation(animation);
	}

	@Override
	public void onCenterClick() {
		speakBuddy(selectedItem);
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

	@Override
	public void onItemOpened(View view, String name) {
		selectedTextView.setText(name);

		switch (view.getId()) {
			case R.id.blindCaller:
				speakBuddy("Call");
				selectedItem="Call";
				startActivity(new Intent(blindHomeActivity.this,callerForBlind.class));
				break;
			case R.id.blindMessage:
				speakBuddy("Messages");
				selectedItem="Messages";
				speakBuddy("This activity is under construction");
				break;
			case R.id.blindMusic:
//				speakBuddy("Music");
				selectedItem="Music";
				startActivity(new Intent(blindHomeActivity.this,musicPlayerForBlind.class));

				break;
			case R.id.blindVoiceRecorder:
				speakBuddy("Voice Recorder");
				selectedItem="Voice Recorder";
				speakBuddy("This activity is under construction");
				break;
			case R.id.blindAlarm:
				speakBuddy("Alarm");
				selectedItem="Alarm";
				startActivity(new Intent(blindHomeActivity.this,alarmForBlind.class));
				break;
			case R.id.blindSettings:
				speakBuddy("Settings");
				selectedItem="Settings";
				speakBuddy("This activity is under construction");
				break;
		}
	}
}
