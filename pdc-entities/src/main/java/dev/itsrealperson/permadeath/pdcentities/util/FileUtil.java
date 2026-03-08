package dev.itsrealperson.permadeath.pdcentities.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {

    public static void copyResources(Class<?> clazz, String resourcePath, File targetFolder) {
        try {
            CodeSource src = clazz.getProtectionDomain().getCodeSource();
            if (src == null) return;
            
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null) break;
                
                String name = e.getName();
                if (name.startsWith(resourcePath + "/") && !e.isDirectory()) {
                    String relativePath = name.substring(resourcePath.length() + 1);
                    File targetFile = new File(targetFolder, relativePath);
                    
                    if (!targetFile.exists()) {
                        targetFile.getParentFile().mkdirs();
                        Files.copy(clazz.getResourceAsStream("/" + name), targetFile.toPath());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
