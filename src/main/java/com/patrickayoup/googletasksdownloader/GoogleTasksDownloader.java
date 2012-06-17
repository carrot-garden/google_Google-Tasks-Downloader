package com.patrickayoup.googletasksdownloader;

import com.patrickayoup.googletasksdownloader.controller.AuthViewController;
import com.patrickayoup.googletasksdownloader.utils.ApplicationInitializer;
import com.patrickayoup.util.exception.FeatureNotImplementedException;
import com.patrickayoup.util.google.oauth.OAuth2Authorizer;
import com.patrickayoup.util.parser.ConfigParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author patrickayoup
 *
 */
public class GoogleTasksDownloader {

    private static final String LEG_1_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
    private static final String SCOPE = "https://www.googleapis.com/auth/tasks.readonly";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final String RESPONSE_TYPE = "code";
    private static final URL CONFIG_FILE =
            GoogleTasksDownloader.class.getResource("/googleTasksDownloader.conf");

    public static void main(String[] args) {
        
        try {
            
            //Initialize the directory structure if it does not exist.
            ApplicationInitializer.initializeDirectoryStructure();
        } catch (IOException ex) {
            
            //Nothing has been done yet. Exit.
            System.exit(1);
        }
        
        //Make the initial request and get the access code.
        OAuth2Authorizer auth;

        LinkedHashMap<String, String> credentials = null;

        try {

            credentials = getCredentials();
        } catch (Exception ex) {

            Logger.getLogger(GoogleTasksDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            auth = new OAuth2Authorizer(credentials.get("client_id"), credentials.get("client_secret"), LEG_1_ENDPOINT,
                    SCOPE, REDIRECT_URI, RESPONSE_TYPE);
            AuthViewController authViewController = new AuthViewController(auth);

        } catch (Exception ex) {

            Logger.getLogger(GoogleTasksDownloader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
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

        // TODO Implement fileMode().
        throw new FeatureNotImplementedException();
    }

    //def fileMode():
//    '''Method for print to file mode. Prints all lists in seperate files.'''
//    #Retrieve all task lists from the user.
//    tasklists = service.tasklists().list().execute()
//
//    idTitleList = []
//
//    #Store the titles and ids as tuples in a list.
//    for tasklist in tasklists['items']:
//        idTitleList.append((tasklist['id'], tasklist['title']))
//        fileName = "taskLists/" + tasklist['title'] + ".txt"
//        listObject = taskWriter.TaskList()
//        outputFile = open(fileName,'w')
//        listObject.writeTitle(tasklist['title'],outputFile)
//        tasks = service.tasks().list(tasklist=tasklist['id']).execute()
//
//        for task in tasks['items']:
//
//            if 'parent' in task:
//                if task['parent'] == listObject.lastParent[-1]:
//                    listObject.writeSameLevelTask(task['title'],task['id'],task['status'],outputFile)
//                else:
//                    listObject.writeUpLevelTask(task['title'],task['id'],task['status'],outputFile)
//            else:
//                listObject.writeDownLevelTask(task['title'],task['id'],task['status'],outputFile)
//
//        outputFile.close()

    private static LinkedList<LinkedList<String>> getTaskLists() throws FeatureNotImplementedException {

        // TODO Implement getTaskLists()
        throw new FeatureNotImplementedException();
    }

    private static void usageMessage() throws FeatureNotImplementedException {

        //TODO: Implement usageMessage().
        throw new FeatureNotImplementedException();
    }

    private static LinkedHashMap<String, String> getCredentials() throws URISyntaxException, FileNotFoundException {

        File configFile = new File(CONFIG_FILE.toURI());

        ConfigParser parser = new ConfigParser(configFile);
        return parser.parseConfig();
    }
}