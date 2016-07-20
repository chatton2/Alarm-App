package gmu.cs.cs477.alarmproj;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    MyDbHelper mDbHelper;
    SQLiteDatabase mDb;
    static final String STATE_SCORE = "actuallyID";
    int id = 0;
    public final static String ALARMID = "gmu.cs.cs477.alarmproj.ALARMID";
    public final static String TOCANCEL = "gmu.cs.cs477.alarmproj.TOCANCEL";
    ListView listView;
    ArrayAdapter myAdapter;
    AlertDialog actions;
    int currentPos;
    public String toDelete;
    Map<String, PendingIntent> alarmList = new HashMap<String, PendingIntent>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            id = savedInstanceState.getInt(STATE_SCORE);
        } else {
            id = 0;
        }
        setContentView(R.layout.activity_main);
        mDbHelper = new MyDbHelper(this);
        mDb = mDbHelper.getWritableDatabase();
        listView = (ListView) findViewById(R.id.dblist);
        myAdapter = new ArrayAdapter<String>(this, R.layout.line);
        myAdapter.clear();
        myAdapter.notifyDataSetChanged();
        Cursor cur=mDb.rawQuery("SELECT " +mDbHelper.COL_NAME+" from "+mDbHelper.TABLE_NAME,new String [] {});
        if (cur.moveToFirst()) {
            do {
                myAdapter.add(cur.getString(0));
            } while (cur.moveToNext());
        }
        myAdapter.notifyDataSetChanged();
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String toFind = ((TextView) view).getText().toString();
                String[] COLUMNS = new String[]{"Type", "Hour", "Minute", "Repeat"};
                String selection = "Name =?";
                String[] args = {toFind};
                Cursor result = mDb.query(mDbHelper.TABLE_NAME, COLUMNS, selection, args, null, null, null, null);
                result.moveToFirst();
                TextView viewy1 = (TextView) findViewById(R.id.nameText);
                TextView viewy2 = (TextView) findViewById(R.id.typeText);
                TextView viewy3 = (TextView) findViewById(R.id.timeText);
                TextView viewy4 = (TextView) findViewById(R.id.repeatText);
                viewy1.setText(toFind);
                viewy2.setText(result.getString(0));
                viewy3.setText(result.getString(1) + ":" + result.getString(2));
                viewy4.setText(result.getString(3));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                                    currentPos = position;
                                                    toDelete = ((TextView) view).getText().toString();
                                                    System.out.println(toDelete);
                                                    actions.show();
                                                    return true;
                                                }
                                            }
        );
        DialogInterface.OnClickListener actionListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                String[] COLUMNS = new String[]{"Intent"};
                                String selection = "Name =?";
                                String[] args = {toDelete};
                                Cursor result = mDb.query(mDbHelper.TABLE_NAME, COLUMNS, selection, args, null, null, null, null);
                                result.moveToFirst();

                                mDb.delete(mDbHelper.TABLE_NAME, "Name =?", new String[]{toDelete});
                                myAdapter.remove(myAdapter.getItem(currentPos));
                                alarmList.remove(toDelete);
                                Intent intent = new Intent(MainActivity.this, NewAlarm.class);
                                intent.putExtra(ALARMID, result.getString(0));
                                intent.putExtra(TOCANCEL, "yes");
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete this item?");
        String[] options = {"Delete"};
        builder.setItems(options, actionListener);
        builder.setNegativeButton("Cancel", null);
        actions = builder.create();
    }
    public void newAlarm(View v){
        Intent intent = new Intent(this, NewAlarm.class);
        intent.putExtra(ALARMID, Integer.toString(id));
        intent.putExtra(TOCANCEL, "no");
        startActivityForResult(intent, 9);
    }
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data){
       String namey = data.getExtras().getString(NewAlarm.NAME);
        if(namey.equals("")){

        }
        else {
            myAdapter.add(namey);
            alarmList.put(namey, (PendingIntent) (data.getExtras().get(NewAlarm.PINTENT)));
            id = id + 1;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_SCORE, id);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public  void doneAlarm(String name){
        mDbHelper = new MyDbHelper(this);
        mDb = mDbHelper.getWritableDatabase();
        mDb.delete(mDbHelper.TABLE_NAME, "Name =?", new String[]{name});
        alarmList.remove(toDelete);
        myAdapter.remove(name);
        myAdapter.notifyDataSetChanged();

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra(AlarmReceiver.GONE).equals("Yes")){
            doneAlarm(intent.getStringExtra(AlarmReceiver.TODELETE));
        }
    }

}
