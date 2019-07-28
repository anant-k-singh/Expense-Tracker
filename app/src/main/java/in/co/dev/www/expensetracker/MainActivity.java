package in.co.dev.www.expensetracker;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    final Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.add_expense);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(c);
                View dialogBox = layoutInflater.inflate(R.layout.add_expense_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput.setView(dialogBox);

                final EditText userInputDialogEditText = (EditText) dialogBox.findViewById(R.id.input_expense_type);
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
                                if(amountString == "") amount = 0;
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

    }

    public void appendExpense(String date, String type, int amount){
        Toast.makeText(getApplicationContext(),"Spent Rs."+amount+" on "+type, Toast.LENGTH_SHORT).show();
    }
}
