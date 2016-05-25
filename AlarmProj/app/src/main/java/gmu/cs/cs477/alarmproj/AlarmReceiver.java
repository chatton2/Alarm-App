package gmu.cs.cs477.alarmproj;

/**
 * Created by Chynna on 11/13/2015.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmReceiver extends BroadcastReceiver {
    Ringtone r;
    Timer t;
    public final static String TODELETE = "gmu.cs.cs477.alarmproj.TODELETE";
    public final static String GONE = "gmu.cs.cs477.alarmproj.GONE";
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        //this will update the UI with message

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        final String mess = arg1.getStringExtra(NewAlarm.MESSAGE);
        final Context whyMe = arg0;
        NotificationManager mNotificationManager = (NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification mBuilder = new Notification.Builder(arg0)
                .setContentTitle("Alarm")
                .setContentText(mess)
                .setSmallIcon(R.drawable.bwah).build();
        mNotificationManager.notify(
                1,
                mBuilder);


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
  /*      ringtone = RingtoneManager.getRingtone(arg0, alarmUri);
        ringtone.play();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ringtone.stop();*/
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(arg0, notification);
        Toast.makeText(arg0, mess, Toast.LENGTH_LONG).show();
        r.play();
        t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                r.stop();
                t.cancel();
                MyDbHelper mDbHelper;
                SQLiteDatabase mDb;
                mDbHelper = new MyDbHelper(whyMe);
                mDb = mDbHelper.getWritableDatabase();
                String[] COLUMNS = new String[]{"Name", "Repeat"};
                String selection = "Interval =?";
                String[] args = {mess};
                Cursor result = mDb.query(mDbHelper.TABLE_NAME, COLUMNS, selection, args, null, null, null, null);
                result.moveToFirst();
                if (result.getString(1).equals("No")) {
                    Intent intent = new Intent(whyMe, MainActivity.class);
                    intent.putExtra(TODELETE, result.getString(0));
                    intent.putExtra(GONE, "Yes");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mDbHelper = new MyDbHelper(whyMe);
                    mDb = mDbHelper.getWritableDatabase();
                    mDb.delete(mDbHelper.TABLE_NAME, "Name =?", new String[]{result.getString(0)});
                    whyMe.startActivity(intent);
                }
            }
        }, 10000);

    }
}