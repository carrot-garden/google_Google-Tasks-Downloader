package com.patrickayoup.googletasksdownloader.utils;

import com.patrickayoup.util.google.oauth.OAuth2Authorizer;
import com.patrickayoup.util.parser.ConfigParser;
import com.patrickayoup.util.parser.JSONParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

/**
 *
 * @author patrick
 */
public class ApplicationInitializer {
    
    private LinkedHashMap<String, String> tokenResponse;    
    private static final File AUTH_TOKEN = new File("appData/authToken.conf");
    private OAuth2Authorizer auth;
    private LinkedHashMap<String, String> credentials;
    
    
    public ApplicationInitializer(OAuth2Authorizer auth, LinkedHashMap<String, String> credentials) throws URISyntaxException, FileNotFoundException,
            IllegalStateException, MalformedURLException, IOException {
                        
        this.auth = auth;
        this.credentials = credentials;
        tokenResponse = refreshToken();
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
    
    public final LinkedHashMap<String, String> refreshToken() throws FileNotFoundException, IllegalStateException, URISyntaxException, MalformedURLException, IOException {
        
        ConfigParser confParser = new ConfigParser(AUTH_TOKEN);
        LinkedHashMap<String, String> confData = confParser.parseConfig();
        
        if (confData.isEmpty()) {
            
            throw new IllegalStateException("The token file is empty.");
        } else {
            
            String refreshString = auth.refreshToken("https://accounts.google.com/o/oauth2/token?",
                   "refresh_token=" + confData.get("refresh_token"), "application/x-www-form-urlencoded");
            
            JSONParser jsonParser = new JSONParser();
            return jsonParser.simpleParse(refreshString);
        }
    }
 
    public LinkedHashMap<String, String> getTokenInfo() {
        
        return tokenResponse;
    }
}
