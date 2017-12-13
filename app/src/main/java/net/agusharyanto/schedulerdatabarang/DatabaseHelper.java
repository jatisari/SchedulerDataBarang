package net.agusharyanto.schedulerdatabarang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by agus on 10/25/17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME ="dbbarang";
    private final static int DATABASE_VERSION = 1;
    private final static String BARANG_TABLE = "tbl_barang";
    private final static String FIELD_ID="_id";
    private final static String FIELD_KODE ="kode";
    private final static String FIELD_NAMA="nama";
    private final static String FIELD_HARGA ="harga";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creat table dan init data
        String SQL_CREATE_TABLE = "create table "+ BARANG_TABLE +" ("+FIELD_ID+" integer primary key, "
                + FIELD_KODE + " text not null, "+FIELD_NAMA+ " text not null,"
                + FIELD_HARGA +" text not null);";
        db.execSQL(SQL_CREATE_TABLE);
        initData(db);


    }

    private void initData(SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_ID,"1");
        contentValues.put(FIELD_KODE,"B001");
        contentValues.put(FIELD_NAMA,"Mouse M238");
        contentValues.put(FIELD_HARGA,"150000");
        db.insert(BARANG_TABLE,null,contentValues);
        ContentValues contentValues1 = new ContentValues();
        contentValues.put(FIELD_ID,"2");
        contentValues1.put(FIELD_KODE,"B002");
        contentValues1.put(FIELD_NAMA,"Mouse B175");
        contentValues1.put(FIELD_HARGA, "100000");
        db.insert(BARANG_TABLE, null, contentValues1);
        ContentValues contentValues2 = new ContentValues();
        contentValues.put(FIELD_ID,"3");
        contentValues2.put(FIELD_KODE,"B003");
        contentValues2.put(FIELD_NAMA,"Mouse M170");
        contentValues2.put(FIELD_HARGA, "120000");
        db.insert(BARANG_TABLE,null,contentValues2);
    }

    public ArrayList<Barang> getDataBarang(SQLiteDatabase db){
        ArrayList<Barang> barangArrayList = new ArrayList<Barang>();
        String[] allColumns = {FIELD_ID, FIELD_KODE, FIELD_NAMA, FIELD_HARGA};

        Cursor cursor = db.query(BARANG_TABLE,allColumns,null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Barang barang = cursorToBarang(cursor);
            barangArrayList.add(barang);
            cursor.moveToNext();
        }

        return  barangArrayList;

    }

    public long updateBarang(Barang barang, SQLiteDatabase db) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(FIELD_KODE, barang.getKode());
        initialValues.put(FIELD_NAMA, barang.getNama());
        initialValues.put(FIELD_HARGA, barang.getHarga());
        long rowaffect =db.update(BARANG_TABLE, initialValues, FIELD_ID + "=" + barang.getId(), null);
        return rowaffect;
    }

    public void deleteBarang(Barang barang, SQLiteDatabase db) {
        String id = barang.getId();
        db.delete(BARANG_TABLE, FIELD_ID + " = " + id, null);
    }

    private Barang cursorToBarang(Cursor cursor) {
        Barang barang = new Barang();
        barang.setId(cursor.getString(0));
        barang.setKode(cursor.getString(1));
        barang.setNama(cursor.getString(2));
        barang.setHarga(cursor.getString(3));
        return barang;
    }


    public long insertBarang(Barang barang, SQLiteDatabase db) {


        ContentValues initialValues = new ContentValues();

        initialValues.put(FIELD_ID, barang.getId());
        initialValues.put(FIELD_KODE, barang.getKode());
        initialValues.put(FIELD_NAMA, barang.getNama());
        initialValues.put(FIELD_HARGA, barang.getHarga());
        /*kita gunakan insertWithConflict dengan SQLiteDatabase.CONFLICT_REPLACE
        agar jika ada duplikat data maka akan direplace (update) dan
        jika tidak duplikat maka insert seperti biasa
         */
        long insertId = db.insertWithOnConflict(BARANG_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
        return insertId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}