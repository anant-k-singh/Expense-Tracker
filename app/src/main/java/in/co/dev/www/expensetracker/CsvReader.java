package in.co.dev.www.expensetracker;

import android.content.Context;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    Context context;

    public CsvReader(Context context){
        this.context = context;
    }

//    public List<String[]> GetCsv(String fileName) throws IOException{
//        List<String[]> rows= new ArrayList<>();
//        InputStream inputStream = context.getAssets().open(fileName);
//        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//        String line, delimiter = ",";
//
//        bufferedReader.readLine();
//
//        while((line = bufferedReader.readLine()) != null){
//            String[] row = line.split(delimiter);
//            rows.add(row);
//        }
//        return rows;
//    }

    public List<String> GetLines(File sourceFile) {
        List<String> fileRows = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(sourceFile);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dis));

            String line;

            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                fileRows.add(line);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return fileRows;
    }

    public void WriteLines(File sourceFile, List<String> rows, boolean append){
        try {
            FileOutputStream fos = new FileOutputStream(sourceFile,append);
            for (String row : rows) {
                fos.write((row+"\n").getBytes());
            }
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void WriteLine(File sourceFile, String row){
        try {
            FileOutputStream fos = new FileOutputStream(sourceFile,true);
            fos.write((row+"\n").getBytes());
            fos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
