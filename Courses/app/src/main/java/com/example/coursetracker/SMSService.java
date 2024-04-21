package com.example.coursetracker;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
// service class to run notification
// https://stackoverflow.com/questions/48999945/android-notification-not-showing-from-service

public class SMSService extends Service {
    private Context context;
    private Timer timer;
    private String username;
    private String phoneNumber;
    DBHelper myDb;
    User user = User.getInstance();
    private SMSService sms;


    @Override
    public void onCreate() {
        super.onCreate();
        sms = new SMSService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sms.startNotifications();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        sms.stopNotifications();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public SMSService(Context context) {
        this.context = context;
        this.myDb = new DBHelper(context);  // Initialize myDb
    }

    public void setParameters() {
        this.username = user.getUsername();
        this.phoneNumber = user.getPhone();
    }

    private class SMSTaskScheduler extends TimerTask {
        private Context context;
        public SMSTaskScheduler(Context context) {
            this.context = context;
        }

        public void run() {
            // TODO: recommend courses to the user
            // sendRecommendationsSMS();
            sendUpdatedSMS();
        }
    }
    public void sendUpdatedSMS(){
        int updated = myDb.checkForUpdates(username);
        // check for notification condition is met
        if ((updated == 1)) {
            try {  // Check Permissions for sms
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    if (phoneNumber != null) {  // if phone number is not null
                        String message = "Course list has just been updated!";
                        smsManager.sendTextMessage(user.getPhone(), null, message, null, null);
                        Log.d("SMSService", "SMSService sent successfully!");
                    } else {  // if phone number is null
                        Log.d("SMSService", "Phone number not provided!");
                    }
                } else {  // case where sms permission is not granted
                    Log.d("SMSService", "Insufficient SMSService permissions!");
                }
            } catch (Exception e) {
                e.printStackTrace(); // print exception details
            }
        }
        myDb.close();
    }

    public void sendRecommendationsSMS(){

    }
    // check if the notification should be sent
    public void startNotifications() {
        if (timer!= null){  // if there is already a notification, leave it
            return;
        }
        else {
            timer = new Timer(); // if there is not, create a timer
            // Schedule the timer to check every 20 minutes
            timer.scheduleAtFixedRate(new SMSTaskScheduler(this.context), 0, 120000);
        }
    }

    public void stopNotifications() {
        if (timer != null) {   // Cancel the timer if it's not null
            timer.cancel();
            timer.purge();
        }
    }
}
