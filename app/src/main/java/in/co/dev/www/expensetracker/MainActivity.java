package in.co.dev.www.expensetracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    final Context c = this;
    private String sourceFileName = "Expenses.csv";
    public File sourceFile;
    public SQLiteHelper dbHelper;
    protected final int permsRequestCode = 1;
    protected final int indexOfExpenseDate = 0;
    protected final int indexOfExpenseType = 1;
    protected final int indexOfExpenseAmount = 2;
    protected List<String> past_expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if permission granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // Permission not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permsRequestCode);
        }

//        String path = this.getExternalFilesDir(null)+"/"+sourceFileName;
        String path = "/storage/emulated/0/" + sourceFileName;
        sourceFile = new File(path);
        Log.v("path", path);

        // Set DB
        dbHelper = new SQLiteHelper(getBaseContext());
        if(dbHelper.size() == 0) Log.e("DB size:", "empty DB!");
        else Log.i("DB size:", ""+dbHelper.size());

        // Set past expenses from CSV
//        CsvReader csvReader = new CsvReader(this);
//        past_expenses = csvReader.GetLines(sourceFile);

        if(dbHelper.size() == 0){
            Log.i("csvToDB","DB empty, loading from CSV file.");
            int recordCount = csvToDB();
            if(recordCount >= 0)
                Toast.makeText(getApplicationContext(),"Loaded "+recordCount+"records", Toast.LENGTH_SHORT)
                        .show();
            else
                Toast.makeText(getApplicationContext(),"Loading from CSV failed!", Toast.LENGTH_SHORT)
                        .show();
        }

        populatePastExpenses();

        // Pop-Up for Add New Expense Dialog
        Button addButton = findViewById(R.id.add_expense);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Render the Pop-up for adding new expense
                LayoutInflater layoutInflater = LayoutInflater.from(c);
                View dialogBox = layoutInflater.inflate(R.layout.add_expense_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput.setView(dialogBox);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String date, expenseType="fails";
                                int amount;

                                // Get expense type
                                Spinner spinner = ((AlertDialog) dialogBox).findViewById(R.id.input_expense_type_spinner);
                                if(spinner == null)
                                    Log.e("null","findViewById returned null");
                                else expenseType = spinner.getSelectedItem().toString();

                                // Get expense amount
                                EditText edit = ((AlertDialog) dialogBox).findViewById(R.id.input_expense_amount);
                                String amountString = edit.getText().toString();
                                if(amountString.equals("")) amount = 0;
                                else amount = Integer.parseInt(amountString);

                                // Get current Date
                                date = new SimpleDateFormat("ddMM", Locale.getDefault()).format(new Date());

                                // Add expense to File
                                appendExpense(date, expenseType, amount);
                                // Update listview
                                populatePastExpenses();
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });

        Button removeExpense = findViewById(R.id.remove_last_expense);
        removeExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLastExpense();
                populatePastExpenses();
            }
        });

//        Log.i("CSV size:", ""+past_expenses.size());

        Button csvtoDB = findViewById(R.id.csv_to_db_button);
        csvtoDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int recordCount = csvToDB();
                if(recordCount >= 0) {
                    populatePastExpenses();
                    Toast.makeText(getApplicationContext(), "Loaded " + recordCount + " records", Toast.LENGTH_SHORT)
                            .show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Loading from CSV failed!", Toast.LENGTH_SHORT)
                            .show();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Refresh expense data after permission is granted
        populatePastExpenses();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case permsRequestCode:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.i("perm", "Granted");
                } else {
                    // permission denied, Disable the
                    // functionality that depends on this permission.
                    Log.i("perm", "Denied");
                    Toast.makeText(getApplicationContext(),"Write Permission required!", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
                return;
            }
        }
    }

    // Add expense to csv file and DB
    public void appendExpense(String date, String type, int amount){
        Toast.makeText(getApplicationContext(),"On "+date +", Spent Rs."+amount+" on "+type, Toast.LENGTH_SHORT)
             .show();
        String expense = date + "," + type + "," + amount;
        CsvReader csvReader = new CsvReader(this);
        csvReader.WriteLine(sourceFile, expense);

        String day = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
        String month = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        dbHelper.insertData(Integer.parseInt(day), Integer.parseInt(month), type, amount);
    }

    // Read expenses from CSV file and show in listview
//    protected void populatePastExpenses(){
//        CsvReader csvReader = new CsvReader(this);
//        past_expenses = csvReader.GetLines(sourceFile);
//
//        // Convert expense string from CSV to readable format
//        List<String> formated_expenses = new ArrayList<>();
//        for(String expense: past_expenses)
//            formated_expenses.add(csvToDisplayFormat(expense));
//
//        // Reverse, so latest expense is on top
//        Collections.reverse(formated_expenses);
//        // Refresh last month total value
//        lastMonthTotal();
//
//        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.list_item, formated_expenses);
//        ListView listView = findViewById(R.id.expense_list);
//        listView.setAdapter(itemsAdapter);
//    }

    // Read expenses from Database and show in listview
    protected  void populatePastExpenses(){
        dbHelper = new SQLiteHelper(getBaseContext());
        List<String> formated_expenses = dbHelper.getAllData();

        // Reverse, so latest expense is on top
        Collections.reverse(formated_expenses);
        // Refresh last month total value
        lastMonthTotal();

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.list_item, formated_expenses);
        ListView listView = findViewById(R.id.expense_list);
        listView.setAdapter(itemsAdapter);
    }

    protected String csvToDisplayFormat(String csvExpense){
        String[] eArr = csvExpense.split(",");
        return String.format("%s :  Rs. %s\t  on %s",  eArr[indexOfExpenseDate],
                                                eArr[indexOfExpenseAmount],
                                                eArr[indexOfExpenseType]);
    }

    // Update this month total expense
    protected void lastMonthTotal(){

        if(dbHelper.size() == 0) Log.e("lastMonthTotal DB size:", "empty DB!");
        else Log.i("lastMonthTotal DB size:", ""+dbHelper.size());
        List<String> allExpenses = dbHelper.getAllCsvData(true);
        int totalExpense = getLastMonthTotal(allExpenses);

        // add expenses to textview
        TextView textView = findViewById(R.id.monthly_total_textview);
        textView.setText("Total: " + totalExpense + " Avg: " + (int)((0.0+totalExpense)/30.0));
    }

    // Get last month total
    protected int getLastMonthTotal(List<String> allExpenses){
        int totalExpense = 0;
        String day = allExpenses.get(allExpenses.size()-1).split(",")[0];
        int lastMonthInt = Integer.parseInt(allExpenses.get(allExpenses.size()-1).substring(3,5))-1;
        String lastMonth = (lastMonthInt<10 ? "0" : "") + lastMonthInt;
        String lastMonthDate = day + "," + lastMonth + "---";

        int count = 0;
        for(String row: allExpenses){
            if(compareLessThanOrEqual(lastMonthDate,row)) {
                totalExpense += Integer.parseInt(row.split(",")[3]);
                Log.d("rowg:",row);
                count += 1;
            }
        }
        Log.d("count:",""+count);
        return totalExpense;
    }

    protected boolean compareLessThanOrEqual(String LDate, String RDate){
        int Lday = Integer.parseInt(LDate.substring(0,2));
        int Lmonth = Integer.parseInt(LDate.substring(3,5));

        int Rday = Integer.parseInt(RDate.substring(0,2));
        int Rmonth = Integer.parseInt(RDate.substring(3,5));

        if(Lmonth == Rmonth)    return Lday <= Rday;
        else    return Lmonth <= Rmonth;
    }

    // Remove last expense from source CSV file
    protected void removeLastExpense(){
        if(past_expenses.size() > 0){
            Toast.makeText(getApplicationContext(),"Removed "+past_expenses.get(past_expenses.size()-1), Toast.LENGTH_SHORT)
                    .show();
            past_expenses.remove(past_expenses.size()-1);
            Log.i("sIZE", "final "+past_expenses.size());
            CsvReader csvReader = new CsvReader(this);
            csvReader.WriteLines(sourceFile, past_expenses, false);

            csvToDB();
        }
        else{
            Toast.makeText(getApplicationContext(),"No expense to remove!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     *  Transfers expense records from CSV to DB
        @return     count of records transferred, -1 if failed
     */
    protected int csvToDB(){
        dbHelper = new SQLiteHelper(getBaseContext());
        dbHelper.clearTable();

        CsvReader csvReader = new CsvReader(this);
        past_expenses = csvReader.GetLines(sourceFile);

        int count = 0;
        for(String row: past_expenses){
            String[] arr = row.split(",");
            int date = Integer.parseInt(arr[indexOfExpenseDate].substring(0,2));
            int month = Integer.parseInt(arr[indexOfExpenseDate].substring(2,4));
            String expenseType = arr[indexOfExpenseType];
            int amount = Integer.parseInt(arr[indexOfExpenseAmount]);

            if( !dbHelper.insertData(date,month,expenseType,amount))
                return -1;
            else count += 1;
        }
        return count;
    }

//    // Transfer expenses from DB to CSV
//    public void dbToCSV(){
//        dbHelper = new SQLiteHelper(getBaseContext());
//        List<String> rows = dbHelper.getAllCsvData(false);
//
//        CsvReader csvReader = new CsvReader(this);
//        csvReader.WriteLines(sourceFile,rows,false);
//    }
}