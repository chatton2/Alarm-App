package gmu.cs.cs477.alarmproj;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class NewAlarm extends AppCompatActivity {


    private PendingIntent pendingIntent;
    private AlarmManager manager;
    public final static String NAME = "gmu.cs.cs477.alarmproj.NAME";
    public final static String PINTENT = "gmu.cs.cs477.alarmproj.PINTENT";
    public final static String MESSAGE = "gmu.cs.cs477.alarmproj.MESSAGE";
    MyDbHelper mDbHelper;
    SQLiteDatabase mDb;
    int id;
    public String array_spinner[];
    Spinner s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        Intent intent = getIntent();
        id = Integer.parseInt(intent.getStringExtra(MainActivity.ALARMID));
                // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        array_spinner=new String[6];
        array_spinner[0]="Eat";
        array_spinner[1]="Sleep";
        array_spinner[2]="Take Medicine";
        array_spinner[3]="Shower";
        array_spinner[4]="Relax";
        array_spinner[5]="Custom";
        s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, array_spinner);
        s.setAdapter(adapter);
        String cancel = intent.getStringExtra(MainActivity.TOCANCEL);
        mDbHelper = new MyDbHelper(this);
        mDb = mDbHelper.getWritableDatabase();
        if(cancel.equals("yes")){
            cancelAlarm();
        }
    }

    public void startAlarm(View view) {
        EditText namey = (EditText)findViewById(R.id.editText);
        String[] COLUMNS = new String[]{"Type", "Hour", "Minute", "Repeat"};
        String selection = "Name =?";
        String[] args = {namey.getText().toString()};
        Cursor result = mDb.query(mDbHelper.TABLE_NAME, COLUMNS, selection, args, null, null, null, null);
        if(result.moveToFirst())//if fist and next records are not empty
        {
            Toast.makeText(getApplicationContext(), "There is already an alarm with that name",
                    Toast.LENGTH_LONG).show();
        }
        else if(namey.getText().toString().matches("")){
            Toast.makeText(getApplicationContext(), "You did not enter a name",
                    Toast.LENGTH_LONG).show();
        }
        else {
            RadioButton yesButton = (RadioButton) findViewById(R.id.radioButton);
            RadioButton noButton = (RadioButton) findViewById(R.id.radioButton2);
            TimePicker picky = (TimePicker) findViewById(R.id.timePicker);
            picky.clearFocus();
            String checky = "No";
            mDbHelper = new MyDbHelper(this);
            mDb = mDbHelper.getWritableDatabase();
            PendingIntent pendingIntent;
            ContentValues f = new ContentValues();
            int j = s.getSelectedItemPosition();
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            f.put(MyDbHelper.COL_TYPE, s.getSelectedItem().toString());
            if (picky.getCurrentHour() < 10) {
                f.put(MyDbHelper.COL_HOUR, "0" + Integer.toString(picky.getCurrentHour()));
            } else {
                f.put(MyDbHelper.COL_HOUR, Integer.toString(picky.getCurrentHour()));
            }
            if (picky.getCurrentMinute() < 10) {
                f.put(MyDbHelper.COL_MINUTE, "0" + Integer.toString(picky.getCurrentMinute()));
            } else {
                f.put(MyDbHelper.COL_MINUTE, Integer.toString(picky.getCurrentMinute()));
            }
            f.put(MyDbHelper.COL_REPEAT, checky);
            if(j == 0){
                f.put(MyDbHelper.COL_INTERVAL, "Get something to eat!");
                alarmIntent.putExtra(MESSAGE, "Get something to eat!");
            }
            else if (j == 1){
                f.put(MyDbHelper.COL_INTERVAL, "Time to get some sleep!");
                alarmIntent.putExtra(MESSAGE, "Time to get some sleep!");
            }
            else if (j == 2){
                f.put(MyDbHelper.COL_INTERVAL, "Make sure you take your medicine!");
                alarmIntent.putExtra(MESSAGE, "Make sure you take your medicine!");
            }
            else if (j == 3){
                f.put(MyDbHelper.COL_INTERVAL, "Take a shower!");
                alarmIntent.putExtra(MESSAGE, "Take a shower!");
            }
            else if (j == 4){
                f.put(MyDbHelper.COL_INTERVAL, "Take some time for yourself!");
                alarmIntent.putExtra(MESSAGE, "Take some time for yourself!");
            }
            else if (j == 5){
                EditText namey2 = (EditText)findViewById(R.id.editText2);
                if(namey2.getText().toString().matches("")){
                    f.put(MyDbHelper.COL_INTERVAL, "Here's a reminder!");
                    alarmIntent.putExtra(MESSAGE, "Here's a reminder!");
                }
                else {
                    f.put(MyDbHelper.COL_INTERVAL, namey2.getText().toString());
                    alarmIntent.putExtra(MESSAGE, namey2.getText().toString());
                }
            }
            pendingIntent = PendingIntent.getBroadcast(this, id, alarmIntent, 0);
            manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            int interval = 10000;
            Calendar cal = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            int hour  = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            cal.set(Calendar.HOUR_OF_DAY, picky.getCurrentHour());
            cal.set(Calendar.MINUTE, picky.getCurrentMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long millis = cal.getTimeInMillis();

            if(cal.getTimeInMillis() <= now.getTimeInMillis())
                millis = cal.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1);
            else
                millis = cal.getTimeInMillis();
  /*          if(picky.getCurrentHour() > hour){
            //    cal.add(Calendar.DATE, 1);
                millis = millis + 86000000;
                System.out.println("KOKOKO" + hour + " " + picky.getCurrentHour());
            }
            else if((picky.getCurrentHour() == hour) && (picky.getCurrentMinute() == minute)){
             //   cal.add(Calendar.DATE, 1);
                millis = millis + 86000000;
            }*/
            //cal.getTimeInMillis()
            if (yesButton.isChecked()) {
                checky = "Yes";
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, millis,
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            } else if (noButton.isChecked()) {
                checky = "No";
                manager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
            }
            f.put(MyDbHelper.COL_REPEAT, checky);
            f.put(MyDbHelper.COL_INTENT, Integer.toString(id));
            f.put(MyDbHelper.COL_NAME, namey.getText().toString());
            mDb.insert(MyDbHelper.TABLE_NAME, null, f);
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra(NAME, namey.getText().toString());
            //      intent.putExtra(PINTENT, pendingIntent);
            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
    public void cancelAlarm() {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent displayIntent = PendingIntent.getBroadcast(this, id, alarmIntent, PendingIntent.FLAG_NO_CREATE);
        manager.cancel(displayIntent);
        displayIntent.cancel();
        finish();
    }

    public void backButton(View view) {
        Intent intent = new Intent();
        intent.putExtra(NAME, "");
        //      intent.putExtra(PINTENT, pendingIntent);
        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
