package com.patrickayoup.googletasksdownloader.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.patrickayoup.googletasksdownloader.GoogleTasksDownloader;
import com.patrickayoup.googletasksdownloader.controller.AuthViewController;
import com.patrickayoup.util.parser.ConfigParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import javax.swing.UnsupportedLookAndFeelException;

public class ApplicationInitializer {
    
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final LinkedList<String> SCOPES = new LinkedList<String>(Arrays.asList("https://www.googleapis.com/auth/tasks.readonly"));
    private String clientId, clientSecret;
    private static final URL CONFIG_FILE =
        GoogleTasksDownloader.class.getResource("/googleTasksDownloader.conf");
    private static final File AUTH_TOKEN = new File("appData/authToken.conf");    
    private LinkedHashMap<String, String> tokenResponse;    
     
    public ApplicationInitializer() throws URISyntaxException, FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
        getCredentials();
    }
    
    /**
     * Initializes the directory structure to ensure that the appropriate files
     * are present.
     */
    public static void initializeDirectoryStructure() throws IOException {
        
        File appData = new File("appData");
        
        if (!AUTH_TOKEN.exists()) {
            
            if (!appData.exists()) {
                
                appData.mkdir();
            }
            
            AUTH_TOKEN.createNewFile();
        }
    }
    
    public void getToken() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
        String authorizationURL = new GoogleAuthorizationCodeRequestUrl(clientId, REDIRECT_URI, SCOPES).build();
        
        new AuthViewController(authorizationURL, clientId, clientSecret);
    }
    
    public LinkedHashMap<String, String> refreshToken() {
        
        return new LinkedHashMap<String, String>();
    }
 
    public LinkedHashMap<String, String> getTokenInfo() {
        
        return tokenResponse;
    }
    
    private void getCredentials() throws URISyntaxException, FileNotFoundException {

        LinkedHashMap<String, String> config;
        File configFile = new File(CONFIG_FILE.toURI());
        
        ConfigParser parser = new ConfigParser(configFile);
        config = parser.parseConfig();
        
        clientId = config.get("client_id");
        clientSecret = config.get("client_secret");
    }
}
