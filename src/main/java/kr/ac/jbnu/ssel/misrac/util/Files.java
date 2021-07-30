package kr.ac.jbnu.ssel.misrac.util;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
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
}
