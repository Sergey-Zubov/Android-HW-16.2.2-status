package com.szubov.android_hw_162;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText mEditTextPhoneNumber;
    private EditText mEditTextMessage;
    private Button mButton;
    private static final String LOG_TAG = "MyLog";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 11;
    private static final int MY_PERMISSIONS_REQUEST_SEND_MESSAGE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mEditTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        mEditTextMessage = findViewById(R.id.editTextMessage);

        final String phoneNumber = mEditTextPhoneNumber.getText().toString().trim();
        Uri uriCall = Uri.parse("tel:" + phoneNumber);
        final Intent intentCall = new Intent(Intent.ACTION_CALL, uriCall);

        final String message =mEditTextMessage.getText().toString().trim();
        final SmsManager smgr = SmsManager.getDefault();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mButton.getId()) {
                    case R.id.btnCall:
                        if (phoneNumber.length() > 0) {
                            if (intentCall.resolveActivity(getPackageManager()) != null) {
                                if (ContextCompat.checkSelfPermission(MainActivity.this,
                                        Manifest.permission.CALL_PHONE) !=
                                        PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.CALL_PHONE},
                                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                                }else {
                                    startActivity(intentCall);
                                }
                            } else {
                                Log.d(LOG_TAG, "Activity not found");
                            }
                        } else {
                            toastPhoneNumber();
                        }
                        break;
                    case R.id.btnSendMessage:
                        if (message.length() > 0) {
                            if (phoneNumber.length() > 0) {
                                smgr.sendTextMessage(phoneNumber,null,
                                        message, null, null);
                            } else {
                                toastPhoneNumber();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Enter message!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                startActivity();
        }
    }

    private void toastPhoneNumber() {
        Toast.makeText(MainActivity.this, "Enter phone number!",
                Toast.LENGTH_SHORT).show();
    }
}