package com.patrickayoup.googletasksdownloader;

import com.patrickayoup.googletasksdownloader.utils.ApplicationInitializer;
import com.patrickayoup.util.exception.FeatureNotImplementedException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author patrickayoup
 *
 */

public class GoogleTasksDownloader {
        
    public static void main(String[] args) {

        LinkedHashMap<String, String> credentials;
        ApplicationInitializer init = null;
        boolean initRun = false;
                
        try {
           
            init = new ApplicationInitializer();
            
            //Initialize the directory structure if it does not exist.
            ApplicationInitializer.initializeDirectoryStructure();

            //Try refreshing first.
            credentials = init.refreshToken();
        } catch (Exception ex) {

            //There was something wrong with the token file.
            initRun = true;            
        }
        
        if (initRun) {
            
            try {
                
                //Case where refresh failed.
                init.getToken();
            } catch (Exception ex) {
                
                Logger.getLogger(GoogleTasksDownloader.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        } else {
            
            //TODO: IMPLEMENT APP LOGIC
            System.out.println("Refreshed");
        }
    }

    private static void printLists() throws FeatureNotImplementedException {

        // TODO Implement printLists().
        throw new FeatureNotImplementedException();
    }

    private static void printMode(String string) throws FeatureNotImplementedException {

        // TODO Implement printMode().
        throw new FeatureNotImplementedException();
    }

    private static void fileMode() throws FeatureNotImplementedException {

        throw new FeatureNotImplementedException();
    }

    private static LinkedList<LinkedList<String>> getTaskLists() throws FeatureNotImplementedException {

        // TODO Implement getTaskLists()
        throw new FeatureNotImplementedException();
    }

    private static void usageMessage() throws FeatureNotImplementedException {

        //TODO: Implement usageMessage().
        throw new FeatureNotImplementedException();
    }   
}