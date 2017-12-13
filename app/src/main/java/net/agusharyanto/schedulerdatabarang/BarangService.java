package net.agusharyanto.schedulerdatabarang;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BarangService extends Service {
    private ArrayList<Barang> barangList = new ArrayList<Barang>();
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Context context;
    LocalBroadcastManager broadcaster;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub

        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        databaseHelper = new DatabaseHelper(this);
        db= databaseHelper.getWritableDatabase();
        context = this;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        db.close();
        databaseHelper.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d("TAG","Service Running");
            loadDataServerVolley();

        return super.onStartCommand(intent, flags, startId);
    }

    private void loadDataServerVolley(){

        String url = "http://192.168.0.102/barang/listdata.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       Log.d("TAG","response:"+response);
                        processResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    private void processResponse(String response){

        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArray = jsonObj.getJSONArray("data");
           // Log.d("TAG", "data length: " + jsonArray.length());
            Barang objectbarang = null;
            barangList.clear();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                objectbarang= new Barang();
                objectbarang.setId(obj.getString("id"));
                objectbarang.setNama(obj.getString("nama"));
                objectbarang.setKode(obj.getString("kode"));
                objectbarang.setHarga(obj.getString("harga"));
                long result = databaseHelper.insertBarang(objectbarang, db);
                Log.d("TAG","result:"+result);
            }
            showNotif();
            sendResult("update");
        } catch (JSONException e) {
            Log.d("BarangService", "errorJSON");
        }

    }

    static final public String BARANG_RESULT = "net.agusharyanto.schedulerdatabarang.REQUEST_PROCESSED";

    static final public String BARANG_MESSAGE = "net.agusharyanto.schedulerdatabarang.BARANG_MSG";

    //Method yang akan mentrigger method yang dipanggil dibroadcast receiver onrecive diactivity
    public void sendResult(String message) {
        Intent intent = new Intent(BARANG_RESULT);
        if(message != null)
            intent.putExtra(BARANG_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }


    private  void showNotif(){

        NotificationManager mgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context,MainActivity.class);

        String title=context.getResources().getString(R.string.app_name);

        String message = "Ada 2 data";
        Notification note = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 1, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
             //   .setLargeIcon(bm)
                .setTicker(title)
                .setWhen(System.currentTimeMillis())
                .build();
        //new No
        //note.setLatestEventInfo(this, getString(R.string.notify_new_bill_title), msg, pi);
        note.defaults |= Notification.DEFAULT_SOUND;
        note.flags |= Notification.FLAG_AUTO_CANCEL;

        //An issue could occur if user ever enters over 2,147,483,647 tasks. (Max int value).
        //I highly doubt this will ever happen. But is good to note.
        int id = 1;
        mgr.notify(id, note);
    }



}
