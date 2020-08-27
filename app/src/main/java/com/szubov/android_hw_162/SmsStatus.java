package com.szubov.android_hw_162;

import android.os.Bundle;
import android.widget.Toast;

public class SmsStatus extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getIntent().getExtras();
        assert data != null;
        int status = data.getInt(STATUS);
        String number = data.getString(NUMBER);
        if (status == MainActivity.SENT) {
            Toast.makeText(getBaseContext(), "Message successfully Sent to " + number,
                    Toast.LENGTH_LONG).show();
        } else if (status == MainActivity.DELIVERED) {
            Toast.makeText(getBaseContext(), "Message successfully Delivered to " + number,
                    Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
