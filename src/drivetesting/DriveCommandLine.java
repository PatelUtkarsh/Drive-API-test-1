package drivetesting;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;


public class DriveCommandLine implements java.io.Serializable{

  private static String CLIENT_ID = "381533935857-s2hlsrukv1egju0lhp3lk4f7ejegqc5d.apps.googleusercontent.com";
  private static String CLIENT_SECRET = "cSdn1GxdWWW9GDqTs--TySHV";

  private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
  
  public static void main(String[] args) throws IOException {
    HttpTransport httpTransport = new NetHttpTransport();
    JsonFactory jsonFactory = new JacksonFactory();
   
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
        .setAccessType("online")
        .setApprovalPrompt("auto").build();
    
    String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    System.out.println("Please open the following URL in your browser then type the authorization code:");
    System.out.println("  " + url);
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String code = br.readLine();
    
    GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
    GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
    System.out.println(credential.getAccessToken().toString());
    System.out.println(credential.getRefreshToken());
    
    //credential.setExpiresInSeconds((long) 15);
    /*GoogleCredential credential = new GoogleCredential.Builder().setJsonFactory(jsonFactory)
            .setTransport(httpTransport).setClientSecrets(CLIENT_ID, CLIENT_SECRET).build();
    credential.setAccessToken("ya29.1.AADtN_UJb5O9Gue8Y4CQe79fYkfOi0Jc7dIim1kxYHhgJCOFp_EDFSy2kv8TsrY");
    credential.refreshToken();*/
    
    //System.out.println(credential.getExpiresInSeconds());
    //System.out.println(credential.getRefreshToken().toString());
    //System.out.println(credential.getAccessToken());
    
    //Create a new authorized API client
    Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
    
    //Insert a file  
    File body = new File();
    body.setTitle("My document");
    body.setDescription("A test document");
    body.setMimeType("text/plain");
    
    java.io.File fileContent = new java.io.File("document1.txt");
    FileContent mediaContent = new FileContent("text/plain", fileContent);

    File file = service.files().insert(body, mediaContent).execute();
    System.out.println("File ID: " + file.getId());
  }
}