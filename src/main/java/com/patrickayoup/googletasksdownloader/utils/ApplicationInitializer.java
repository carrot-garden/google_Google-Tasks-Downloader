package com.patrickayoup.googletasksdownloader.utils;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author patrick
 */
public class ApplicationInitializer {
    
    /**
     * Initializes the directory structure to ensure that the appropriate files
     * are present.
     */
    public static void initializeDirectoryStructure() throws IOException {
        
        File authToken = new File("appData/authToken.conf");
        File appData = new File("appData");
        
        if (!authToken.exists()) {
            
            if (!appData.exists()) {
                
                appData.mkdir();
            }
            
            authToken.createNewFile();
        }
    }
}
