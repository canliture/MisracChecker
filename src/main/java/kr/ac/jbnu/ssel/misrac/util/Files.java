package kr.ac.jbnu.ssel.misrac.util;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Set;

/**
 * Created by Liture on 2021/7/16
 */
@Log4j2
public class Files {

    public static void listFilesWithExtends(String file, String[] suffixes, Set<String> result) {
        File f = new File(file);
        if (f.isFile()) {
            String name = f.getName();
            for (String suffix : suffixes) {
                if (name.toLowerCase().endsWith(suffix)) {
                    try {
                        String path = f.getCanonicalPath();
                        result.add(path);
                        break;
                    } catch (IOException e) {
                        log.error("Failed to list files");
                    }
                }
            }
        } else if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    listFilesWithExtends(file1.getPath(), suffixes, result);
                }
            }
        }
    }

    public static int lineCounts(String filePath) {
        int lines = 0;
        try(BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            while (r.readLine() != null) {
                lines++;
            }
        } catch (Exception e) {
            log.error("Can't read file from {}", filePath);
        }
        return lines;
    }
}
