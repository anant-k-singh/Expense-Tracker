package in.co.dev.www.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABSE_VERSION = 1;
    public static final String DATABASE_NAME = "ExpenseDB";
    public static final String TABLE_NAME = "Expenses_Table";

    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_MONTH = "month";
    private static final String COL_EXPENSE = "expense";
    private static final String COL_AMOUNT = "amount";

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
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
    }
}
