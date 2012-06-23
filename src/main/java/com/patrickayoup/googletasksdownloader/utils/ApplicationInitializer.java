package com.patrickayoup.googletasksdownloader.utils;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.patrickayoup.googletasksdownloader.GoogleTasksDownloader;
import com.patrickayoup.googletasksdownloader.controller.AuthViewController;
import com.patrickayoup.util.parser.ConfigParser;
import com.patrickayoup.util.writer.ConfigWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;

public class ApplicationInitializer {
    
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final LinkedList<String> SCOPES = new LinkedList<String>(Arrays.asList("https://www.googleapis.com/auth/tasks.readonly"));
    private String clientId, clientSecret, refreshToken;
    private static final URL CONFIG_FILE =
        GoogleTasksDownloader.class.getResource("/googleTasksDownloader.conf");
    private static final File AUTH_TOKEN = new File("appData/authToken.conf");    
    private LinkedHashMap<String, String> tokenResponse;
    private static final File LISTS = new File("taskLists");

     
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
        
        if (!LISTS.exists()) {
            
            LISTS.mkdir();
        }
    }
    
    public void getToken() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
        String authorizationURL = new GoogleAuthorizationCodeRequestUrl(clientId, REDIRECT_URI, SCOPES).build();
        
        new AuthViewController(authorizationURL, clientId, clientSecret);
    }
    
    public GoogleCredential refreshToken() throws IOException, FileNotFoundException, URISyntaxException {
        
        getRefreshToken();
        
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        
        GoogleTokenResponse response = new GoogleRefreshTokenRequest(httpTransport, jsonFactory, refreshToken, clientId, clientSecret).execute();
                
        LinkedHashMap<String, String> tokenInfo = new LinkedHashMap<String, String>();
        tokenInfo.put("access_token", response.getAccessToken());
        tokenInfo.put("token_type", response.getTokenType());
        tokenInfo.put("expires_in", Long.toString(response.getExpiresInSeconds()));
        tokenInfo.put("refresh_token", refreshToken);

        ConfigWriter confWrite = new ConfigWriter(AUTH_TOKEN);
        confWrite.writeConfig(tokenInfo);
        
        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build().setFromTokenResponse(response);
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
    
    private void getRefreshToken() throws FileNotFoundException {
        
        LinkedHashMap<String, String> token;
        
        ConfigParser parser = new ConfigParser(AUTH_TOKEN);
        token = parser.parseConfig();
        
        refreshToken = token.get("refresh_token");
    }
}
