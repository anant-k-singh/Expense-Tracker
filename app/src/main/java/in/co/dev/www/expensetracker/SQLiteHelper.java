package in.co.dev.www.expensetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABSE_VERSION = 1;
    public static final String DATABASE_NAME = "ExpenseDB";
    public static final String TABLE_NAME = "Expenses_Table";

    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_EXPENSE = "expense";
    private static final String KEY_AMOUNT = "amount";

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + "INTERGER PRIMARY KEY AUTOINCREMENT" + ","
                + KEY_DATE + "DATE" + ","
                + KEY_EXPENSE + "VARCHAR(30)" + ","
                + KEY_AMOUNT +"INTEGER" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
