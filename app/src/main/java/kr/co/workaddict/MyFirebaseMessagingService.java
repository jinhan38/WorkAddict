package kr.co.workaddict;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import kr.co.workaddict.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String token = "";

    @Override
    public void onCreate() {
        super.onCreate();
        if (SaveSharedPreferences.getMarketingAgreement(getApplicationContext()).equals("y")) {
            PushUtils.acquireWakeLock(getApplicationContext());
        }
    }

    //아래 부분이 없어도 토큰값 받을 수 있음
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (SaveSharedPreferences.getMarketingAgreement(getApplicationContext()).equals("y")) {
            if (remoteMessage.getNotification() != null) {
                Log.e("getNotification", "Message Notification Body: "
                        + remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage.getNotification().getTitle()
                        , remoteMessage.getNotification().getBody());
            }
        }

    }


    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, BottomNavi.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.app_name);

        PushUtils.releaseWakeLock();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder
                = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle style
                = new NotificationCompat.BigTextStyle(notificationBuilder);
        style.bigText(messageBody);
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.app_name);
            NotificationChannel channel
                    = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakelock
                = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        notificationManager.notify(0, notificationBuilder.build());
    }
}

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class MyJobService extends JobService {

    private static final String TAG = "MyJobService";

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");

        PowerManager powerManager;
        PowerManager.WakeLock wakeLock;
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");
        wakeLock.acquire(); // WakeLock 깨우기

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

class PushUtils {
    private static PowerManager.WakeLock mWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    public static void acquireWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKEUP");
        mWakeLock.acquire();
    }

    public static void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}

