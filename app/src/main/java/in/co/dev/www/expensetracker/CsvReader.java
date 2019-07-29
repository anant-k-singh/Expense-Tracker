package in.co.dev.www.expensetracker;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_APPEND;

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

    //Check
    public void WriteLines(String fileName, List<String> rows){
//        OutputStream outputStream = context.getAssets().open(fileName);
//        FileOutputStream fileOutputStream = context.openFileOutput(fileName, MODE_APPEND);
        try {
            FileWriter fstream = new FileWriter(fileName, true);
            BufferedWriter fbw = new BufferedWriter(fstream);
            for (String row : rows) {
                fbw.write(row);
                fbw.newLine();
            }
            fbw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //Check
    public void WriteLine(String fileName, String row){
        try {
            FileWriter fstream = new FileWriter(fileName, true);
            BufferedWriter fbw = new BufferedWriter(fstream);

            fbw.write(row);
            fbw.newLine();

            fbw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
