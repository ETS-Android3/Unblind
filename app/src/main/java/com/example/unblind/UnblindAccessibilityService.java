package com.example.unblind;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class UnblindAccessibilityService extends AccessibilityService {
    private static final String TAG = "UnblindAccessibilitySer";
    DatabaseService mService;
    private boolean mbound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            DatabaseService.LocalBinder binder = (DatabaseService.LocalBinder) service;
            mService = binder.getService();
            mbound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "onServiceDisconnected");
            mbound = false;
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG, "onAccessibilityEvent: " + event.getClass().getName());

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        if (event.getText() == null) {
            Log.e(TAG, "event text " + event.getText());
            if (mbound) {
                mService.insertDatabase("event.getText()", "no description");
            }
        } else {
            Log.e(TAG, "event text: none");
        }
        
        if (source.getContentDescription() == null) {
            Log.e(TAG, "description: " + "custom added description");
        } else {
            Log.e(TAG, "description: " + source.getContentDescription());
        }

        if (source.getText() == null) {
            Log.e(TAG, "view text: none");
        } else {
            Log.e(TAG, "view text: " + source.getText());
        }


        source.recycle();

    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt: something went wrong");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        // Set the type of events that this service wants to listen to. Others
        // won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;


        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated. This service *is*
        // application-specific, so the flag isn't necessary. If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        // info.flags = AccessibilityServiceInfo.DEFAULT;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);
        Log.e(TAG, "onServiceConnected: ");

        // Bind DatabaseService
        Intent intent = new Intent(this, DatabaseService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
}
