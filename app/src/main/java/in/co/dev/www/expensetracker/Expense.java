package in.co.dev.www.expensetracker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Expense {

    public Date date;
    public String type;
    public int amount;

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
}
