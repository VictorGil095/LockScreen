package com.victor.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        //при выключенном экране запускатся LockScreen
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            wasScreenOn = false;
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}