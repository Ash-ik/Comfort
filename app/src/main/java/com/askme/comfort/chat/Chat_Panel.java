package com.askme.comfort.chat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.askme.comfort.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat_Panel extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth Firebase_Auth;
    private FirebaseUser Firebase_User;
    private static final int REQUEST_CODE = 1234;
    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "Chat_Panel";
    public static final String MESSAGES_CHILD = "messages";

    private String User_Name;
    private String Photo_URL;

    private GoogleApiClient Google_API_Client;

    private ProgressBar Progress_Bar;
    private RecyclerView Message_Recycler_View;
    private LinearLayoutManager Linear_Layout_Manager;
    private Button Send_Button;
    private EditText Message_Edit_Text;
    private ImageButton speakButton;

    private DatabaseReference Firebase_Database_Reference;
    private FirebaseRecyclerAdapter<Chat_Message, Chat_Message_View_Holder> Firebase_Adapter;
    private FirebaseRemoteConfig Firebase_Remote_Config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__panel);
        speakButton=(ImageButton)findViewById(R.id.speakButton);

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
        }
        //Set username to anonymous
        User_Name = ANONYMOUS;

        Google_API_Client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        Progress_Bar = (ProgressBar) findViewById(R.id.Progress_Bar);
        Message_Recycler_View = (RecyclerView) findViewById(R.id.Recycler_View_Chat_Message);
        Linear_Layout_Manager = new LinearLayoutManager(this);
        Linear_Layout_Manager.setStackFromEnd(true);
        Message_Recycler_View.setLayoutManager(Linear_Layout_Manager);

        Firebase_Remote_Config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings Remote_Config_Settings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(false).build();
        Map<String,Object> Default_Config_Map = new HashMap<>();
        Default_Config_Map.put("comfort_chat_msg_length", 100);

        Firebase_Remote_Config.setConfigSettings(Remote_Config_Settings);
        Firebase_Remote_Config.setDefaults(Default_Config_Map);

        Fetch_Config();

        // Initialize Database Ref
        Firebase_Database_Reference = FirebaseDatabase.getInstance().getReference();
        Firebase_Adapter = new FirebaseRecyclerAdapter<Chat_Message, Chat_Message_View_Holder>(Chat_Message.class, R.layout.chat_message_layout, Chat_Message_View_Holder.class, Firebase_Database_Reference.child(MESSAGES_CHILD)) {
            @Override
            protected void populateViewHolder(Chat_Message_View_Holder viewHolder, Chat_Message model, int position) {
                Progress_Bar.setVisibility(Progress_Bar.INVISIBLE);
                viewHolder.TextView_Message.setText(model.Get_Chat_Text());
                viewHolder.TextView_Person_Name.setText(model.Get_Chat_Person_Name());

                if (model.Get_Photo_URL() == null)
                {
                    viewHolder.Image_View_Person.setImageDrawable(ContextCompat.getDrawable(Chat_Panel.this, R.mipmap.ic_profile_blank_image));
                }
                else
                {
                    Glide.with(Chat_Panel.this).load(model.Get_Photo_URL()).into(viewHolder.Image_View_Person);
                }
            }
        };

        Firebase_Adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int Messages_Count = Firebase_Adapter.getItemCount();
                int Last_Visible_Position = Linear_Layout_Manager.findLastCompletelyVisibleItemPosition();
                if (Last_Visible_Position == -1 || (positionStart >= (Messages_Count -1 ) && Last_Visible_Position == (positionStart - 1)))
                {
                    Message_Recycler_View.scrollToPosition(positionStart);
                }
            }
        });

        Message_Recycler_View.setLayoutManager(Linear_Layout_Manager);
        Message_Recycler_View.setAdapter(Firebase_Adapter);

        Message_Edit_Text = (EditText) findViewById(R.id.EditText_Message);
        //Message_Edit_Text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
        //       .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        Message_Edit_Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    Send_Button.setEnabled(true);
                } else {
                    Send_Button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        Send_Button = (Button) findViewById(R.id.Button_Send);
        Send_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Chat_Message My_Msg = new Chat_Message(Message_Edit_Text.getText().toString(), User_Name, Photo_URL);
                Firebase_Database_Reference.child(MESSAGES_CHILD).push().setValue(My_Msg);
                Message_Edit_Text.setText("");
            }
        });

        //Init Firebase Auth
        Firebase_Auth = FirebaseAuth.getInstance();
        Firebase_User = Firebase_Auth.getCurrentUser();

        if (Firebase_User == null)
        {
            startActivity(new Intent(this, Chat_Login_Selection.class));
            finish();
            return;
        }
        else
        {
            User_Name = Firebase_User.getDisplayName();
            if (Firebase_User.getPhotoUrl() != null)
            {
                Photo_URL = Firebase_User.getPhotoUrl().toString();
            }
        }

    }

    public void Apply_Length_Limit()
    {
        Long Msg_Length = Firebase_Remote_Config.getLong("comfort_chat_msg_length");
        Message_Edit_Text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Msg_Length.intValue())});
        Log.d(TAG, "Msg Length : " + Msg_Length);
    }

    public void Fetch_Config()
    {
        long Cache_Expiration = 3600;
        Firebase_Remote_Config.fetch(Cache_Expiration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Firebase_Remote_Config.activateFetched();
                Apply_Length_Limit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error Fetching Config : " + e.getMessage());
                Apply_Length_Limit();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_sign_out:
                Firebase_Auth.signOut();
                Auth.GoogleSignInApi.signOut(Google_API_Client);
                User_Name = ANONYMOUS;
                startActivity(new Intent(this, Chat_Login_Selection.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            int I;
            String s = Message_Edit_Text.getText().toString();
            if (matches.size() > 0)
                s = s + " " + matches.get(0);
            Message_Edit_Text.setText(s);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
