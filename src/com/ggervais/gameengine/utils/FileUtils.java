package com.ggervais.gameengine.utils;

import java.io.*;

public class FileUtils {

    public static String getFileContents(String filename) throws FileNotFoundException, IOException {
        
        StringBuilder contents = new StringBuilder();
        
        FileInputStream fis = new FileInputStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        String line;
        while((line = reader.readLine()) != null) {
            contents.append(line + "\n");
        }
        
        reader.close();
        
        return contents.toString();
    }
}
