package com.szubov.android_hw_162;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditTextPhoneNumber;
    private EditText mEditTextMessage;
    private String mPhoneNumber;
    private static final String LOG_TAG = "MyLog";
    protected static final int SENT = 0;
    protected static final int DELIVERED = 1;
    protected static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 11;
    protected static final int MY_PERMISSIONS_REQUEST_SEND_MESSAGE = 12;
    protected static final String NUMBER = "number";
    protected static final String STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        Log.d(LOG_TAG, "MainActivity -> initViews");
        mEditTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        mEditTextMessage = findViewById(R.id.editTextMessage);

        findViewById(R.id.btnCall).setOnClickListener(this);
        findViewById(R.id.btnSendMessage).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCall:
                Log.d(LOG_TAG, "MainActivity -> btnCall -> OnClick");
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }else {
                    callByNumber();
                }
                break;
            case R.id.btnSendMessage:
                Log.d(LOG_TAG, "MainActivity -> btnSendMessage -> OnClick");
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_MESSAGE);
                }else {
                    sendByNumber();
                }
                break;
        }
    }

    private void callByNumber() {
        Log.d(LOG_TAG, "MainActivity -> btnCall -> OnClick -> callByNumber");
        mPhoneNumber = mEditTextPhoneNumber.getText().toString().trim();
        if (mPhoneNumber.length() > 0) {
            Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNumber));
            if (intentCall.resolveActivity(getPackageManager()) != null) {
                startActivity(intentCall);
            } else {
                Log.d(LOG_TAG, "Activity not found");
                Toast.makeText(MainActivity.this, R.string.device_doesnt_have_app_for_call,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            toastPhoneNumber();
        }
    }

    private void sendByNumber() {
        Log.d(LOG_TAG, "MainActivity -> btnSendMessage -> OnClick -> sendByNumber");
        String message = mEditTextMessage.getText().toString().trim();
        if (message.length() > 0) {
            mPhoneNumber = mEditTextPhoneNumber.getText().toString().trim();
            if (mPhoneNumber.length() > 0) {
                PendingIntent piSent = addSentIntent(mPhoneNumber);
                //PendingIntent piDelivered = addDeliveredIntent(mPhoneNumber);
                try {
                    SmsManager smgr = SmsManager.getDefault();
                    smgr.sendTextMessage(mPhoneNumber,null,
                            message, piSent, null); //piDelivered);
                    //Toast.makeText(MainActivity.this, R.string.message_is_sent,
                            //Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Log.e(LOG_TAG, "MainActivity -> BtnSendMessage -> SmsManager",ex);
                    ex.printStackTrace();
                }
            } else {
                toastPhoneNumber();
            }
        } else {
            Toast.makeText(MainActivity.this, R.string.message_is_empty,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private PendingIntent addSentIntent(String number) {
        Intent sentIntent = new Intent("in.wptrafficanalyzer.activity.status.sent");
        sentIntent.putExtra(NUMBER, number);
        sentIntent.putExtra(STATUS, SENT);
        return PendingIntent.getActivity(getBaseContext(), 0 ,
                sentIntent, PendingIntent.FLAG_ONE_SHOT);
    }

    private PendingIntent addDeliveredIntent(String number) {
        Intent deliveredIntent = new Intent("in.wptrafficanalyzer.activity.status.delivered");
        deliveredIntent.putExtra(NUMBER, number);
        deliveredIntent.putExtra(STATUS, DELIVERED);
        return PendingIntent.getActivity(getBaseContext(), 0 ,
                deliveredIntent, PendingIntent.FLAG_ONE_SHOT);
    }

    private void toastPhoneNumber() {
        Toast.makeText(MainActivity.this, R.string.phone_is_empty,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                Log.d(LOG_TAG, "MainActivity -> onRequestPermissionsResult -> CALL_PHONE");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callByNumber();
                } else {
                    finish();
                }
            case MY_PERMISSIONS_REQUEST_SEND_MESSAGE:
                Log.d(LOG_TAG, "MainActivity -> onRequestPermissionsResult -> SEND_MESSAGE");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendByNumber();
                } else {
                    finish();
                }
        }
    }
}