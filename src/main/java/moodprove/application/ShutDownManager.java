package moodprove.application;
import org.springframework.context.ApplicationContext;

import moodprove.google.OAuthGoogle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

/**
 * 
 * @author malikg
 * 
 */
class ShutdownManager {

    @Autowired
    private ApplicationContext appContext;
    
    /**
     * This method has been implemented so that the link 
     * file is deleted if a user has not authenticated with Google.
     * @param returnCode
     */
    public void initiateShutdown(int returnCode){
    	OAuthGoogle.deleteLinkFile();
        SpringApplication.exit(appContext, () -> returnCode);
    }
}