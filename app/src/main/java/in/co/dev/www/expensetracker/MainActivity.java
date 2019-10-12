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
import android.text.format.DateUtils;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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
        Log.i("check", path);
        // Set DB
        dbHelper = new SQLiteHelper(getBaseContext());

        if(dbHelper.size() == 0) csvToDb();
        // Pop-Up for Add New Expense Dialog
        Button addButton = (Button) findViewById(R.id.add_expense);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Render the Pop-up for adding new expense
                addExpensePopUp();
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

        populatePastExpenses();

        // DB to CSV backup file
        Button dbToCsv = findViewById(R.id.db_to_csv_button);
        dbToCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbToCSV();
            }
        });

        // DB to CSV backup file
        Button csvToDb = findViewById(R.id.csv_to_db_button);
        csvToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csvToDb();
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

    public void addExpensePopUp(){
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
                            Log.e("spinner null","findViewById returned null");
                        else expenseType = spinner.getSelectedItem().toString();

                        // Get expense amount
                        EditText edit = ((AlertDialog) dialogBox).findViewById(R.id.input_expense_amount);
                        String amountString = edit.getText().toString();
                        if(amountString.equals("")) amount = 0;
                        else amount = Integer.parseInt(amountString);

                        // Get current Date
                        date = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault()).format(new Date());

                        // Add expense to File
                        appendExpense(date, expenseType, amount);
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

    // Add expense to DB and csv file then refresh listview
    public void appendExpense(String date, String type, int amount){
        dbHelper.insertData(new Expense(date, type, amount));

        String expense = date + "," + type + "," + amount;
        CsvReader csvReader = new CsvReader(this);
        csvReader.WriteLine(sourceFile, expense);

        Toast.makeText(getApplicationContext(),"On "+date+", Spent Rs."+amount+" on "+type, Toast.LENGTH_SHORT)
                .show();
        populatePastExpenses();
    }

    // Read expenses file and show in listview
//    protected void populatePastExpenses(){
//        CsvReader csvReader = new CsvReader(this);
//        past_expenses = csvReader.GetLines(sourceFile);
//
//        // Convert expense string from CSV to readable format
//        ArrayList<String> formated_expenses = new ArrayList<>();
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
        ArrayList<Expense> expenses = dbHelper.getAllData();
        ArrayList<String> formated_expenses = new ArrayList<>();
        for(Expense e: expenses)
            formated_expenses.add(e.toString());


        // Reverse, so latest expense is on top
        Collections.reverse(formated_expenses);

        // Refresh last month total value
        lastMonthTotal();

        // populate listview
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.list_item, formated_expenses);
        ListView listView = findViewById(R.id.expense_list);
        listView.setAdapter(itemsAdapter);
    }

    // Update the last 1 month total expense
    protected void lastMonthTotal(){
        int totalExpense = 0;
        // add expenses to
        if(dbHelper.size() == 0) Log.e("MainActivity", "empty DB!");
        ArrayList<Expense> allExpenses = dbHelper.getAllData();

//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MONTH, -1);
//        Date dateBefore30Days = cal.getTime();

        Date dateBefore30Days = dbHelper.addDays(30);

        // Todo: getlastmonth in sql itself

        TextView textView = findViewById(R.id.monthly_total_textview);
        textView.setText(Integer.toString(totalExpense));
    }

    // Remove last expense from source CSV file
    protected void removeLastExpense(){
        if(past_expenses.size() > 0){
            Toast.makeText(getApplicationContext(),"Removed "+past_expenses.get(past_expenses.size()-1), Toast.LENGTH_SHORT)
                    .show();
            // Remove last expense
//            Log.i("sIZE", "init "+past_expenses.size());
            past_expenses.remove(past_expenses.size()-1);
            Log.i("sIZE", "final "+past_expenses.size());
            CsvReader csvReader = new CsvReader(this);
            csvReader.WriteLines(sourceFile, past_expenses, false);

            csvToDb();
        }
        else{
            Toast.makeText(getApplicationContext(),"No expense to remove!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    // Transfer expenses from CSV to DB
    protected void csvToDb(){
        dbHelper = new SQLiteHelper(getBaseContext());
        dbHelper.clearTable();

        CsvReader csvReader = new CsvReader(this);
        past_expenses = csvReader.GetLines(sourceFile);

        int count = 0;
        for(String row: past_expenses){
            String[] arr = row.split(",");
            String date = arr[indexOfExpenseDate];
            String expenseType = arr[indexOfExpenseType];
            int amount = Integer.parseInt(arr[indexOfExpenseAmount]);

            if( !dbHelper.insertData(new Expense(date,expenseType,amount))){
                Log.i("insertError", "csvToDB error");
                return;
            }
            ++count;
        }
        Toast.makeText(getApplicationContext(),""+ count +" records moved to DB", Toast.LENGTH_SHORT)
                .show();
    }

    // Transfer expenses from DB to CSV
    public void dbToCSV(){
        dbHelper = new SQLiteHelper(getBaseContext());
        if(dbHelper.size() == 0)
            return;
        ArrayList<Expense> expenses = dbHelper.getAllData();
        ArrayList<String> formated_expenses = new ArrayList<>();
        for(Expense e: expenses)
            formated_expenses.add(e.toCsv());

        CsvReader csvReader = new CsvReader(this);
        csvReader.WriteLines(sourceFile,formated_expenses,false);
    }
}