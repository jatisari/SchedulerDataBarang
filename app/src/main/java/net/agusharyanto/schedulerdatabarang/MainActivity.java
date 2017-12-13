package net.agusharyanto.schedulerdatabarang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BarangAdapter rvAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Barang> barangList = new ArrayList<Barang>();
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(MainActivity.this);
        db = databaseHelper.getWritableDatabase();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewBarang);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        gambarDatakeRecyclerView();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);

        Intent intent = new Intent(this, BarangService.class);

        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //for 30 mint 30*60*1000
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3*60*1000, pintent);

        //Receiver untuk menerima perintah dari service
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(BarangService.BARANG_MESSAGE);
                Log.d("TAG","receives:"+s);
                gambarDatakeRecyclerView();
            }
        };

    }

    //Dimethod onResume ini kita register receiver agar bisa menangkap broadcast dari service
    @Override
    protected void onResume() {
         super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(BarangService.BARANG_RESULT)
        );
    }

    //Dimethod ini kita unregister receivernya agar kalau activity tidak aktif tidak perlu menangkap broadcast
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    private void gambarDatakeRecyclerView(){
        barangList.clear();
        barangList = databaseHelper.getDataBarang(db);
        rvAdapter = new BarangAdapter(barangList);
        mRecyclerView.setAdapter(rvAdapter);
    }

}
