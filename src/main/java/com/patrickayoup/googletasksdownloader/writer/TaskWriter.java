/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patrickayoup.googletasksdownloader.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * Contains functionality to take a task list name and its tasks, and write them
 * to a file with proper formatting.
 * @author patrickayoup
 */
public class TaskWriter {
    
    private int tabs;
    private Stack<String> parent;
    private String lastTask;
    
    public TaskWriter() {
        
        tabs = 0;
        parent = new Stack<String>();
        lastTask = "";
    }
    
    /**
     * Writes the title of the task list.
     * @param title The task list's title.
     * @param listFile The file to write to.
     * @throws IOException
     */
    public void writeTitle(String title, File listFile) throws IOException {
        
        title += "\n\n";
        PrintWriter writer = new PrintWriter(new FileWriter(listFile), true);
        writer.println(title);
    }
    
    /**
     * Writes the task in the file at the same level as the last entry.
     * If it is the first entry, it will be written in at the lowest level.
     * @param task The task to write.
     * @param taskId The associated task id.
     * @param status The task's status.
     * @param listFile The file to write to.
     * @throws IOException
     */
    public void writeSameLevelTask(String task, String taskId,
            String status, File listFile) throws IOException {
        
        PrintWriter writer = new PrintWriter(new FileWriter(listFile), true);
        
        String taskString = prepareTask(status, task);
        
        writer.println(taskString);
        writer.close();
        
        lastTask = taskId;
    }

    /**
     * Generates a string representing the task.
     * @param task The task to write.
     * @param status The task's status.
     * @return String representation of the task entry.
     */
    private String prepareTask(String task, String status) {
        
        String taskString = "";
        
        for (int i = 0; i < tabs; i++) {
            
            taskString += "\t";
        }
        
        if (status.equals("needsAction")) {
            
            taskString += "- " + task + "\n";
        } else {
            
            taskString += "- " + task + " COMPLETE\n";
        }
        
        return taskString;
    }
    
    /**
     * Writes the task in the file one level higher than the last entry.
     * @param task The task to write.
     * @param taskId The associated task id.
     * @param status The task's status.
     * @param listFile The file to write to.
     * @throws IOException
     * @throws IllegalStateException
     */
    public void writeUpLevelTask(String task, String taskId,
            String status, File listFile)
                throws IOException, IllegalStateException {
        
        if (lastTask.equals("")) {
            
            throw new IllegalStateException("The first task must be at "
                    + "indentation level 0.");
        }
        
        tabs++;
        parent.push(lastTask);
        writeSameLevelTask(task, taskId, status, listFile);
    }
    
    /**
     * Writes the task in the file one level lower than the last entry.
     * @param task The task to write.
     * @param taskId The associated task id.
     * @param status The task's status.
     * @param listFile The file to write to.
     * @throws IOException
     * @throws IllegalStateException 
     */
    public void writeDownLevelTask(String task, String taskId,
            String status, File listFile)
                throws IOException, IllegalStateException {
        
        if (tabs == 0) {
            
            throw new IllegalStateException("The indentation level is already at "
                    + "its minimum.");
        }
        
        tabs--;
        parent.pop();
        writeSameLevelTask(task, taskId, status, listFile);
    }
    
    /**
     * Prints the task at the same level as the last entry. If it is the first
     * entry, it will be written at the lowest level.
     * @param task The task to write.
     * @param taskId The associated task id.
     * @param status The task's status.
     */
    public void printSameLevelTask(String task, String taskId, String status) {
        
        String taskString = prepareTask(task, status);
        
        System.out.println(taskString);
        
        lastTask = taskId;
    }
    
    /**
     * Prints the task one level higher than the last entry.
     * @param task The task to write.
     * @param taskId The associated task id.
     * @param status The task's status.
     * @throws IllegalStateException 
     */
    public void printUpLevelTask(String task, String taskId, String status)
        throws IllegalStateException {
        
        if (lastTask.equals("")) {
            
            throw new IllegalStateException("The first task must be at "
                    + "indentation level 0.");
        }
        
        tabs++;
        parent.push(lastTask);
        printSameLevelTask(task, taskId, status);
    }
    
    /**
     * Prints the task one level lower than the last entry.
     * @param task The task to write.
     * @param taskId The associated task id.
     * @param status The task's status.
     * @throws IllegalStateException 
     */
    public void printDownLevelTask(String task, String taskId, String status) 
        throws IllegalStateException {
        
        if (tabs == 0) {
            
            throw new IllegalStateException("The indentation level is already at "
                    + "its minimum.");
        }
        
        tabs--;
        parent.pop();
        printSameLevelTask(task, taskId, status);
    }
}