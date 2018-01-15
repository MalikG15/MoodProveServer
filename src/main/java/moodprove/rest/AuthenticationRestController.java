package moodprove.rest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import moodprove.data.UserRepository;

import moodprove.calendar.GoogleCalendarEvents;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author Malik Graham
 */

@RequestMapping("/auth")
@RestController
public class AuthenticationRestController {
	
	@Autowired
	UserRepository userRepo;
	
	@RequestMapping("/googlecalendar")
	public String getOAuthGoogleCalendarLink() {
		if (!linkFileExists()) startGoogleCalendarAuthenticationThread();
		return readLinkFile();
	}
	
	// Starting a new thread that starts a new server
	// that listens and waits for the user to authenticate
	public void startGoogleCalendarAuthenticationThread() {
		Thread authenticationThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	try {
		    		GoogleCalendarEvents.main(new String[] {});	
		    	}
		    	catch (IOException ex) {
		    		System.out.println(AuthenticationRestController.class.getName());
		    		System.out.println("There was an error getting the Google calendar events");
		    	}
		    }
		});
		authenticationThread.start();
	}
	
	// The link where the user must go to authenticate
	// is written to a file, and must be read and sent
	public String readLinkFile() {
		String link = new String();
		try {
			// Sleeping the thread just in case it takes time 
			// to write to the file
			Thread.sleep(500);
			File linkFile = new File("google_calendar_oauth_link.txt");
			Scanner fileInput = new Scanner(linkFile);
			if (fileInput.hasNextLine())
				link = fileInput.nextLine();
			fileInput.close();
		}
		catch (InterruptedException ex) {
			System.out.println(AuthenticationRestController.class.getName());
			System.out.println("The main thread failed to sleep");
		}
		catch (FileNotFoundException ex) {
			System.out.println(AuthenticationRestController.class.getName());
			System.out.println("The link file was not found");
		}
		return link;
	}

	public boolean linkFileExists() {
		File linkFile = new File("google_calendar_oauth_link.txt");
		return linkFile.exists();
	}
	
}
