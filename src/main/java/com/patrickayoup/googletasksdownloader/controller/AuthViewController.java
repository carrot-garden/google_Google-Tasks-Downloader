package com.patrickayoup.googletasksdownloader.controller;

import com.patrickayoup.googletasksdownloader.GoogleTasksDownloader;
import com.patrickayoup.googletasksdownloader.view.AuthView;
import com.patrickayoup.util.google.oauth.OAuth2Authorizer;
import com.patrickayoup.util.parser.JSONParser;
import com.patrickayoup.util.writer.ConfigWriter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
    private final OAuth2Authorizer authorizer;

    /**
     * Constructor.
     */
    public AuthViewController(OAuth2Authorizer authorizer) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            UnsupportedLookAndFeelException {

        this.authorizer = authorizer;
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
        authorizer.initiateRequest();
    }

    private void submitPressed() throws IllegalStateException, URISyntaxException, MalformedURLException, IOException {

        String code = view.getAuthorizationCodeTextField().getText();

        if (code.equals("")) {

            //Display alert to user.
            JOptionPane.showMessageDialog(view,
                    "ERROR: Please fill in the authorization code field.");

        } else {

            authorizer.setAuthorizationCode(code);

            try {

                String response = authorizer.requestSecondLeg("https://accounts.google.com/o/oauth2/token?",
                    "application/x-www-form-urlencoded");
                LinkedHashMap<String, String> responseMap = parseResponse(response);
                authorizer.setCredentials(responseMap);
                writeCredentials(authorizer);
                JOptionPane.showMessageDialog(view, "Authorization Complete. Your task lists will be downloaded the next time you run Google Tasks Downloader.");
                view.dispose();
            } catch (IOException ex) {

                //Display alert to user.
                JOptionPane.showMessageDialog(view,
                    "ERROR: Authorization code was invalid, please try again.");
            }
        }
    }

    private LinkedHashMap<String, String> parseResponse(String response) throws IOException {

        JSONParser parser = new JSONParser();

        return parser.simpleParse(response);
    }
    
    private static void writeCredentials(OAuth2Authorizer auth) throws URISyntaxException, FileNotFoundException {

        File currentDir = new File("foo.txt");
        
        ConfigWriter confWrite = new ConfigWriter(new URI(currentDir.getAbsolutePath()));
        confWrite.writeConfig(auth.getCredentials());
    }
}
