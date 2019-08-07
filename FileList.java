import java.io.*;
import java.util.*;
import java.io.File;

public class FileList {
    public static String getList() {
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();
        String jsonText = "";
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                jsonText += "|" + listOfFiles[i].getName();
            }
        }

        jsonText += "]";
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                jsonText += "|" + listOfFiles[i].getName();
            }
        }
        return jsonText;
    }

    public static ArrayList<String> getFileArray(String json) {
        ArrayList<String> files = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(json, "]");

        if (st.countTokens() < 1) {
            files.add("no files found");
            System.out.println(st.countTokens());

            return files;
        }
        try {
            StringTokenizer stf = new StringTokenizer(st.nextToken(), "|");
            while (stf.hasMoreTokens()) {
                files.add(stf.nextToken());
            }
        } catch (Exception e) {
            //no files may be or something else

        }
        return files;


    }

}





