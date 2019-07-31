package in.co.dev.www.expensetracker;

import android.content.Context;
import android.util.Log;

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

    public int countLines(File sourceFile){
        int count = -1;
        try {
            FileInputStream fis = new FileInputStream(sourceFile);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dis));

            int r;

            while ((r = bufferedReader.read()) != -1) {
                if(r==10)   ++count;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return count;
    }

    public List<String> GetLines(File sourceFile) {
        List<String> fileRows = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(sourceFile);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dis));
            String line;

            // Column headings for CSV
//            String temp =
                    bufferedReader.readLine();
//            Log.i("GetLines()", temp);
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                fileRows.add(line);
                count++;
            }
            Log.i("GetLines()", "read "+count+" lines");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return fileRows;
    }

    public void WriteLines(File sourceFile, List<String> rows, boolean append){
        try {
            FileOutputStream fos = new FileOutputStream(sourceFile,append);
            if(!append) {
                // column headings for CSV
                fos.write("date,expense,amount\n".getBytes());
            }
            for (String row : rows) {
                fos.write((row+"\n").getBytes());
            }
            fos.close();
            Log.i("WriteLines()", "written "+countLines(sourceFile)+" lines");
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
