package com.patrickayoup.googletasksdownloader;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.patrickayoup.googletasksdownloader.utils.ApplicationInitializer;
import com.patrickayoup.googletasksdownloader.writer.TaskWriter;
import com.patrickayoup.util.exception.FeatureNotImplementedException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author patrickayoup
 *
 */

public class GoogleTasksDownloader {
    
    private static GoogleCredential credential;
    private static HttpTransport httpTransport;
    private static JacksonFactory jsonFactory;
    private static Tasks service;
    
    public static void main(String[] args) throws FeatureNotImplementedException {

        ApplicationInitializer init = null;
        boolean initRun = false;
                
        try {
           
            init = new ApplicationInitializer();
            
            //Initialize the directory structure if it does not exist.
            ApplicationInitializer.initializeDirectoryStructure();

            //Try refreshing first.
            credential = init.refreshToken();
            
        httpTransport = new NetHttpTransport();
        jsonFactory = new JacksonFactory();
            
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
            
            if (args.length == 0) {
                
                usageMessage();
            } else if (args[0].equals("-f")) {
                
                try {
                    
                    fileMode();
                } catch (IOException ex) {
                    
                    Logger.getLogger(GoogleTasksDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            } else if (args[0].equals("-p")) {
                
                try {
                    
                    printMode(args[1]);
                } catch (IOException ex) {
                    
                    Logger.getLogger(GoogleTasksDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            } else if (args[0].equals("-l")) {
                
                try {
                    
                    printLists();
                } catch (IOException ex) {
                    
                    Logger.getLogger(GoogleTasksDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            } else {
                
                usageMessage();
            }
        }
    }

    private static void printLists() throws FeatureNotImplementedException, IOException {
        
        TaskLists taskLists = getTaskLists();
        
        for (TaskList taskList : taskLists.getItems()) {
            
            System.out.println(taskList.getTitle());
        }       
    }

    private static TaskLists getTaskLists() throws IOException {
        
        service = new Tasks(httpTransport, jsonFactory, credential);
        TaskLists taskLists = service.tasklists().list().execute();
        return taskLists;
    }

    private static void printMode(String listName) throws FeatureNotImplementedException, IOException {

        //Get task lists.
        TaskLists taskLists = getTaskLists();
        String id = "";
        String title = "";
        
        
        for (TaskList taskList : taskLists.getItems()) {
            
            if (listName.equals(taskList.getTitle())) {
                
                id = taskList.getId();
                title = taskList.getTitle();
            }
        }

        if (id.isEmpty() || title.isEmpty()) {
            
            System.out.println("Task list not found, please check the name and spelling and try again.");
        } else {
    
            TaskWriter writer = new TaskWriter();
            com.google.api.services.tasks.model.Tasks tasks = service.tasks().list(id).execute();
            
            LinkedList<LinkedHashMap<String, String>> idParentTaskCompList = new LinkedList<LinkedHashMap<String, String>>();
            LinkedHashMap<String, String> temp;        
            
            for (Task task : tasks.getItems()) {

                    temp = new LinkedHashMap<String, String>();
                    temp.put("id", task.getId());
                    temp.put("title", task.getTitle());
                    temp.put("status", task.getStatus());

                if (task.getParent() == null) {
                    
                    temp.put("parent", "noParent");
                } else {
                    
                    temp.put("parent", task.getParent());
                }
                
                idParentTaskCompList.add(temp);
            }
            
            System.out.println(title + "\n");
            
            for (LinkedHashMap<String, String> map : idParentTaskCompList) {
                
                if (map.get("parent").equals(writer.getParent())) {
                    
                    writer.printSameLevelTask(map.get("title"), map.get("id"), map.get("status"));
                } else if (map.get("parent").equals(writer.getLastTask())) {
                    
                    writer.printUpLevelTask(map.get("title"), map.get("id"), map.get("status"));
                } else {
                 
                    writer.printDownLevelTask(map.get("title"), map.get("id"), map.get("status"));
                }
            }
        }
    }

    private static void fileMode() throws FeatureNotImplementedException, IOException {

        //Get task lists.
        TaskLists taskLists = getTaskLists();
        File file;
        TaskWriter writer;
        com.google.api.services.tasks.model.Tasks tasks;
        String parent;
        
        for (TaskList taskList : taskLists.getItems()) {
   
            file = new File("taskLists/" + taskList.getTitle() + ".txt");
            writer = new TaskWriter();
            writer.writeTitle(taskList.getTitle(), file);
            
            tasks = service.tasks().list(taskList.getId()).execute();
            
            for (Task task : tasks.getItems()) {
                            
                parent = task.getParent();
                
                if (parent == null) {
                    
                    parent = "noParent";
                }

                if (parent.equals(writer.getParent())) {
                    
                    writer.writeSameLevelTask(task.getTitle(), task.getId(), task.getStatus(), file);
                } else if (task.get("parent").equals(writer.getLastTask())) {
                    
                    writer.writeUpLevelTask(task.getTitle(), task.getId(), task.getStatus(), file);
                } else {
                 
                    writer.writeDownLevelTask(task.getTitle(), task.getId(), task.getStatus(), file);
                }
            }
        }      
    }

    private static void usageMessage() throws FeatureNotImplementedException {

        System.out.println("Usage: java -jar googleTasksDownloader.jar -option listName\n" +
            "-f: File Mode: All lists will be saved in seperate text files in the dir ./taskLists\n" +
            "-p: Print Mode: The specified list is printed to screen.\n" +
            "-l: List Mode: The names of your task lists are printed to screen.");
    }   
}