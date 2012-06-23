package com.patrickayoup.googletasksdownloader.controller;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.patrickayoup.googletasksdownloader.view.AuthView;
import com.patrickayoup.util.writer.ConfigWriter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Controller for the AuthView.
 * @author patrickayoup
 */
public class AuthViewController extends Thread implements ActionListener {

    private final AuthView view;
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private String authorizationCode;
    private String authorizationURL;
    private String clientId;
    private String clientSecret;
    private Tasks service;

    /**
     * Constructor.
     */
    public AuthViewController(String authorizationURL, String clientId, String clientSecret) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {

        //TODO: Encapsulate into model
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationURL = authorizationURL;
        view = new AuthView();
        registerListeners();
        launchView();
    }

    /**
     * Displays a window asking the user for the code from Google.
     */
    private void launchView() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {

        view.setLocationRelativeTo(null);
        view.setTitle("Google Tasks Downloader");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        view.setVisible(true);
    }

    /**
     * Register the listeners.
     */
    private void registerListeners() {

        view.getLaunchSiteButton().addActionListener(this);
        view.getSubmitButton().addActionListener(this);
    }

    /**
     * Event listener for buttons.
     * @param ae The action event.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource().equals(view.getLaunchSiteButton())) {

            try {

                launchSitePressed();
            } catch (Exception ex) {

                Logger.getLogger(AuthViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (ae.getSource().equals(view.getSubmitButton())) {
            try {
                submitPressed();
            } catch (Exception ex) {

                Logger.getLogger(AuthViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Launches the website.
     * @throws IOException
     * @throws URISyntaxException
     */
    private void launchSitePressed() throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

        view.getAuthorizationCodeTextField().requestFocus();
        java.awt.Desktop.getDesktop().browse(new URI(authorizationURL));
    }

    private void submitPressed() throws IllegalStateException, URISyntaxException, MalformedURLException, IOException {

        String code = view.getAuthorizationCodeTextField().getText();

        if (code.equals("")) {

            //Display alert to user.
            JOptionPane.showMessageDialog(view,
                    "ERROR: Please fill in the authorization code field.");

        } else {

            authorizationCode = code;

            try {

                HttpTransport httpTransport = new NetHttpTransport();
                JacksonFactory jsonFactory = new JacksonFactory();
                
                TokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                        httpTransport, jsonFactory, clientId, clientSecret,
                        authorizationCode, REDIRECT_URI).execute();
                
                LinkedHashMap<String, String> tokenInfo = new LinkedHashMap<String, String>();
                tokenInfo.put("access_token", response.getAccessToken());
                tokenInfo.put("token_type", response.getTokenType());
                tokenInfo.put("expires_in", Long.toString(response.getExpiresInSeconds()));
                tokenInfo.put("refresh_token", response.getRefreshToken());

                writeCredentials(tokenInfo);               
                
                JOptionPane.showMessageDialog(view, "Authorization Complete. Your task lists will be downloaded the next time you run Google Tasks Downloader.");
                view.dispose();
            } catch (IOException ex) {

                //Display alert to user.
                JOptionPane.showMessageDialog(view,
                    "ERROR: Authorization code was invalid, please try again.");
            }
        }
    }

    private static void writeCredentials(LinkedHashMap<String, String> tokenInfo) throws URISyntaxException, FileNotFoundException {

        File configFile = new File("appData/authToken.conf");
        ConfigWriter confWrite = new ConfigWriter(configFile);
        confWrite.writeConfig(tokenInfo);
    }
}
