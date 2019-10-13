package in.co.dev.www.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ExpenseDB";
    public static final String TABLE_NAME = "ExpensesTable";

    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_EXPENSE = "expense";
    private static final String COL_AMOUNT = "amount";

    private static final int IDX_ID = 0;
    private static final int IDX_DATE = 1;
    private static final int IDX_EXPENSE = 2;
    private static final int IDX_AMOUNT = 3;

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ","
                + COL_DATE + " DATE" + ","
                + COL_EXPENSE + " VARCHAR(30)" + ","
                + COL_AMOUNT +" INTEGER" + ")";
        Log.d("SQL.onCreate", CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Check if the database exist and can be read.
     *
     * @return true if it exists and can be read, false if it doesn't
     */
    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }

    /** Inserts values as row in Database
        @return true, when row successfully inserted
                false, otherwise
     */
    public boolean insertData(Expense expense){
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(expense.date);
        Log.d("insertData", date);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE,date);
        contentValues.put(COL_EXPENSE,expense.type);
        contentValues.put(COL_AMOUNT,expense.amount);

        long rowId = db.insert(TABLE_NAME, null, contentValues);
        if(rowId == -1)
            return false;
        else
            return true;
    }

    /**  Gets all rows in Database
        @return Cursor pointing to first row
     */
    private Cursor _getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
    }

    public void deleteLastRow(){
        if(this.size() == 0){
            Log.i("SQL.deleteLastRow", "No row to delete");
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE id = (SELECT MAX(id) FROM " + TABLE_NAME + ")");
    }

    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }

    public int size(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME,null);
        c.moveToFirst();
        return c.getInt(0);
    }

    public Date addDays(int days){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT date('now','"+ (days>=0?"+":"-")+ days +" days')",null);
        c.moveToFirst();
        String newDateStr = c.getString(0);
        Date newDate = new Date();
        try{
            newDate = new SimpleDateFormat("yyyy-mm-dd").parse(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

//    public List<String> getAllData(){
//        List<String> rows = new ArrayList<>();
//        Cursor res = _getAllData();
//        if(res.getCount() == 0){
//            Log.i("Total rows : ", "Empty DB!");
//            return rows;
//        }
//        while(res.moveToNext()){
//            String row = (res.getInt(IDX_DATE)<10?"0":"") + res.getInt(IDX_DATE)
//                    + "/" + (res.getInt(IDX_MONTH)<10?"0":"") + res.getInt(IDX_MONTH)
//                    + " :\tRs. " + res.getInt(IDX_AMOUNT)
//                    + " on " + res.getString(IDX_EXPENSE);
//            rows.add(row);
//            Log.i("Read row : ", row);
//        }
//        Log.i("Total rows : ", ""+rows.size());
//        return rows;
//    }

    /**  Gets all expenses in Database
        @return list of expenses
     */
    public ArrayList<Expense> getAllData(){
        ArrayList<Expense> expenses = new ArrayList<>();

        Cursor res = _getAllData();
        while(res.moveToNext()){
            Expense expense = new Expense(res.getString(IDX_DATE), res.getString(IDX_EXPENSE), res.getInt(IDX_AMOUNT));
            expenses.add(expense);
            Log.d("SQLiteHelper.getAllData","Read row : " + expense);
        }
        return expenses;
    }

//    public List<String> getAllCsvData(boolean separateDDMM){
//        List<String> rows = new ArrayList<>();
//        Cursor res = _getAllData();
//        if(res.getCount() == 0){
//            Log.i("Total rows : ", "Empty DB!");
//            return rows;
//        }
//        while(res.moveToNext()){
//            String row = (res.getInt(IDX_DATE)<10?"0":"") + res.getInt(IDX_DATE)
//                    + (separateDDMM?",":"") + (res.getInt(IDX_MONTH)<10?"0":"") + res.getInt(IDX_MONTH)
//                    + "," + res.getString(IDX_EXPENSE)
//                    + "," + res.getInt(IDX_AMOUNT);
//            rows.add(row);
//            Log.i("Read csv row : ", row);
//        }
//        Log.i("Total csv rows : ", ""+rows.size());
//        return rows;
//    }
}
