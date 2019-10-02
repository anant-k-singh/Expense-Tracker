package in.co.dev.www.expensetracker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Expense {

    public Date date;
    public String type;
    public int amount;

    public Expense(Date date, String type, int amount){
        this.date = date;
        this.type = type;
        this.amount = amount;
    }

    public Expense(String dateStr, String type, int amount){
        try{
            this.date = new SimpleDateFormat("yyyy-mm-dd").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.type = type;
        this.amount = amount;
    }

    public int compareTo(Expense obj){
        return date.compareTo(obj.date);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Expense)) {
            return false;
        }
        Expense o = (Expense) obj;
        return date==o.date && type.equals(o.type) && amount==o.amount;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM");
        String strDt = simpleDate.format(date);

        return strDt + " :\tRs. " + amount + " on " + type;
    }

    public String toCsv() {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM");
        String strDt = simpleDate.format(date);

        return strDt + "," + amount + "," + type;
    }
}
