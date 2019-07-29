package in.co.dev.www.expensetracker;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    Context context;
//    List<String[]> rows;

    public CsvReader(   Context context
//                        String fileName
                    ){
        this.context = context;
//        this.rows = new ArrayList<>();
    }

    public List<String[]> GetCsv(String fileName) throws IOException{
        List<String[]> rows= new ArrayList<>();
        InputStream inputStream = context.getAssets().open(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line, delimiter = ",";

        bufferedReader.readLine();

        while((line = bufferedReader.readLine()) != null){
            String[] row = line.split(delimiter);
            rows.add(row);
        }
        return rows;
    }

    public List<String> GetLines(String fileName) throws IOException{
        List<String> fileRows= new ArrayList<>();
        InputStream inputStream = context.getAssets().open(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;

        bufferedReader.readLine();

        while((line = bufferedReader.readLine()) != null){
            fileRows.add(line);
        }
        return fileRows;
    }
}
