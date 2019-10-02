package in.co.dev.www.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABSE_VERSION = 1;
    public static final String DATABASE_NAME = "ExpenseDB";
    public static final String TABLE_NAME = "Expenses_Table";

    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_MONTH = "month";
    private static final String COL_EXPENSE = "expense";
    private static final String COL_AMOUNT = "amount";

    private static final int IDX_ID = 0;
    private static final int IDX_DATE = 1;
    private static final int IDX_MONTH = 2;
    private static final int IDX_EXPENSE = 3;
    private static final int IDX_AMOUNT = 4;

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ","
                + COL_DATE + " INTEGER" + ","
                + COL_MONTH + " INTEGER" + ","
                + COL_EXPENSE + " VARCHAR(30)" + ","
                + COL_AMOUNT +" INTEGER" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*  Inserts values as row in Database
        Returns: true, when row successfully inserted
                false, otherwise
     */
    public boolean insertData(Integer date, Integer month, String expense, Integer amount){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE,date);
        contentValues.put(COL_MONTH,month);
        contentValues.put(COL_EXPENSE,expense);
        contentValues.put(COL_AMOUNT,amount);

        long rowId = db.insert(TABLE_NAME, null, contentValues);
        if(rowId == -1)
            return false;
        else
            return true;
    }
    /*  Gets all rows in Database
        Returns: Cursor pointing to first row
     */
    private Cursor _getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
    }

    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public int size(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME,null);
        c.moveToFirst();
        return c.getInt(0);
    }

    public List<String> getAllData(){
        List<String> rows = new ArrayList<>();
        Cursor res = _getAllData();
        if(res.getCount() == 0){
            Log.i("Total rows : ", "Empty DB!");
            return rows;
        }
        while(res.moveToNext()){
            String row = (res.getInt(IDX_DATE)<10?"0":"") + res.getInt(IDX_DATE)
                    + "/" + (res.getInt(IDX_MONTH)<10?"0":"") + res.getInt(IDX_MONTH)
                    + " :\tRs. " + res.getInt(IDX_AMOUNT)
                    + " on " + res.getString(IDX_EXPENSE);
            rows.add(row);
            Log.i("Read row : ", row);
        }
        Log.i("Total rows : ", ""+rows.size());
        return rows;
    }

    // Returns list of csv strings with values: "Date,Month,ExpenseType,Amount"
    public List<String> getAllCsvData(boolean separateDDMM){
        List<String> rows = new ArrayList<>();
        Cursor res = _getAllData();
        if(res.getCount() == 0){
            Log.d("Total DB rows : ", "Empty DB!");
            return rows;
        }
        while(res.moveToNext()){
            String row = (res.getInt(IDX_DATE)<10?"0":"") + res.getInt(IDX_DATE)
                    + (separateDDMM?",":"") + (res.getInt(IDX_MONTH)<10?"0":"") + res.getInt(IDX_MONTH)
                    + "," + res.getString(IDX_EXPENSE)
                    + "," + res.getInt(IDX_AMOUNT);
            rows.add(row);
            Log.v("Read csv row : ", row);
        }
        Log.i("Total csv rows : ", ""+rows.size());
        return rows;
    }
}
