package in.co.dev.www.expensetracker;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    final Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton;
        addButton = (Button) findViewById(R.id.add_expense);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(c);
                View dialogBox = layoutInflater.inflate(R.layout.add_expense_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput.setView(dialogBox);

//                final EditText userInputDialogEditText = (EditText) dialogBox.findViewById(R.id.input_expense_type);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String date,expenseType;
                                int amount;

                                EditText edit;
                                edit = (EditText) ((AlertDialog) dialogBox).findViewById(R.id.input_expense_type);
                                expenseType = edit.getText().toString();

                                edit = (EditText) ((AlertDialog) dialogBox).findViewById(R.id.input_expense_amount);
                                String amountString = edit.getText().toString();
                                if(amountString.equals("")) amount = 0;
                                else amount = Integer.parseInt(amountString);

                                date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
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
        String sourceFileName = getString(R.string.source_csv_filename);
        populatePastExpenses(sourceFileName);
    }

    public void appendExpense(String date, String type, int amount){
        Toast.makeText(getApplicationContext(),"Spent Rs."+amount+" on "+type, Toast.LENGTH_SHORT)
             .show();
    }

    protected void populatePastExpenses(String sourceFileName){
        List<String> rows = new ArrayList<>();
        CsvReader csvReader = new CsvReader(this);
        try{
            rows = csvReader.GetLines(sourceFileName);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        for (int i = 0; i < rows.size(); i++) {
            Log.d("row:", rows.get(i));
        }

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, R.layout.list_item, rows);
        ListView listView = (ListView) findViewById(R.id.expense_list);
        listView.setAdapter(itemsAdapter);
    }
}
