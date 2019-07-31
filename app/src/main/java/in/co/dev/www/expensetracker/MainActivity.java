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
        Button addButton = (Button) findViewById(R.id.add_expense);
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
                                String date,expenseType="fails";
                                int amount;

                                // Get expense type
                                Spinner spinner = ((AlertDialog) dialogBox).findViewById(R.id.input_expense_type_spinner);
                                if(spinner == null)
                                    Log.e("null","findViewById returned null");
                                else expenseType = spinner.getSelectedItem().toString();

                                // Get expense amount
                                EditText edit = (EditText) ((AlertDialog) dialogBox).findViewById(R.id.input_expense_amount);
                                String amountString = edit.getText().toString();
                                if(amountString.equals("")) amount = 0;
                                else amount = Integer.parseInt(amountString);

                                // Get current Date
                                date = new SimpleDateFormat("ddMM", Locale.getDefault()).format(new Date());

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

    // Add expense to csv file and refresh listview
    public void appendExpense(String date, String type, int amount){
        Toast.makeText(getApplicationContext(),"On "+date +", Spent Rs."+amount+" on "+type, Toast.LENGTH_SHORT)
             .show();
        String expense = date + "," + type + "," + amount;
        CsvReader csvReader = new CsvReader(this);
        csvReader.WriteLine(sourceFile, expense);

        populatePastExpenses();
    }

    // Read expenses file and show in listview
    protected void populatePastExpenses(){
        CsvReader csvReader = new CsvReader(this);
        past_expenses = csvReader.GetLines(sourceFile);

        // Convert expense string from CSV to readable format
        List<String> formated_expenses = new ArrayList<>();
        for(String expense: past_expenses)
            formated_expenses.add(csvToDisplayFormat(expense));

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

    // Update the last 1 month total expense
    protected void lastMonthTotal(){
        int totalExpense = 0;
        // TODO: Modify to sum for last 30 days only
        for(String row: past_expenses){
            String[] arr = row.split(",");
            totalExpense += Integer.parseInt(arr[indexOfExpenseAmount]);
        }
        TextView textView = (TextView)  findViewById(R.id.monthly_total_textview);
        textView.setText(Integer.toString(totalExpense));
    }

    // Remove last expense from source CSV file
    protected void removeLastExpense(){
        // TODO: Remove only last line from source CSV file
        if(past_expenses.size() > 0){
            Toast.makeText(getApplicationContext(),"Removed "+past_expenses.get(past_expenses.size()-1), Toast.LENGTH_SHORT)
                    .show();
            // Remove last expense
            Log.i("sIZE", "init "+past_expenses.size());
            past_expenses.remove(past_expenses.size()-1);
            Log.i("sIZE", "final "+past_expenses.size());
            CsvReader csvReader = new CsvReader(this);
            csvReader.WriteLines(sourceFile, past_expenses, false);
        }
        else{
            Toast.makeText(getApplicationContext(),"No expense to remove!", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
