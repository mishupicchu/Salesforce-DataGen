
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.*;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

/**
 * The SalesForceClient class generates a secure http client as well as an access token
 * based on the credentials listed in Constants.java (i.e. environment, client_id, etc.)
 * @author  Mishika Vora
 */
public class SalesForceClient {
    public HttpClient httpclient;
    public String accessToken;
    public boolean isBadClient; //to make sure client is good
    private static Logger logger = Logger.getLogger(SalesForceClient.class);
    public MyProperties prop;


    /**
     * Creates a new SalesforceClient object to be passed into creating a record object
     * Each object has an httpclient and an access token that can be access outside the class
     */
	public SalesForceClient(String propFile) {
		httpclient = null;
		accessToken = null;
		isBadClient = false;
		prop = new MyProperties(propFile);
		try {
			this.httpclient = secureHttpClient();
			logger.info("Secure client successfully created from: " + propFile);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			isBadClient = true;
			logger.error(e);
			e.printStackTrace();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			isBadClient = true;
			logger.error(e);
			e.printStackTrace();
		}
		
		try {
			this.accessToken = retrieveAccessToken();
			if (accessToken != "Error")
				logger.info("Successfully retrieved access token");
			else
				logger.error("Error in retrieving access token");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			isBadClient = true;
			logger.error(e);
			e.printStackTrace();
		}
		
    
		
		
		
	}
	
	/**
	 * creates secure http client to connect to https 
	 * @return  httpclient object that can connect to https 
	 * @exception  KeyManagementException 
	 * @exception NoSuchAlgorithmException
	 */
	private HttpClient secureHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
		logger.info("Creating SSL client (secureHttpClient())");
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
    	 
                 public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                         //System.out.println("getAcceptedIssuers =============");
                         return null;
                 }

                 @Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
					// TODO Auto-generated method stub
					
				}
     } }, new SecureRandom());

     SSLSocketFactory sf = new SSLSocketFactory(sslContext);
     Scheme httpsScheme = new Scheme("https", 443, sf);
     SchemeRegistry schemeRegistry = new SchemeRegistry();
     schemeRegistry.register(httpsScheme);

     // Apache HttpClient version > 4.2 should use BasicClientConnectionManager
     //pooling allows for multi-threading --changed from basicclientcm
     ClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
     HttpClient hclient = new DefaultHttpClient(cm);
	 return hclient;
		
	}
	
	/**
	 * Returns the object at the top of this stack without removing it. 
	 * @String  access token/session id to be used in oauth restapi calls 
	 * @exception  generic if cannot execute post request
	 */
    public String retrieveAccessToken() throws Exception {
    	logger.info("Retrieving Access Token (retrieveAccessToken())");
    	
        HttpPost post = new HttpPost(prop.environment + Constants.tokenUrl);
        LinkedList<BasicNameValuePair> parameters = new LinkedList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("grant_type", "password"));
        parameters.add(new BasicNameValuePair("client_id", prop.CLIENT_ID));
        parameters.add(new BasicNameValuePair("client_secret", prop.CLIENT_SECRET));
	    parameters.add(new BasicNameValuePair("username", prop.USERNAME));
		parameters.add(new BasicNameValuePair("password", prop.PASSWORD));
        parameters.add(new BasicNameValuePair("redirect_uri", prop.callbackUrl));
        post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
       
        try {
            HttpResponse httpResponse = httpclient.execute(post);
            String response = new String();
       	 	HttpEntity responseEntity = httpResponse.getEntity();
       	 	if(responseEntity != null) {
       	 		response = EntityUtils.toString(responseEntity);
       	 		if (!response.contains("error"))
       	 		{
                JSONObject authResponse = new JSONObject(response);
                String at = authResponse.getString("access_token");
                this.accessToken = at;
                return at;
       	 		}
       	 	//response with error message
       	 	 logger.error(response);
       	     isBadClient = true;
       	     String at = "Error";
       	 	 this.accessToken = at;
       	 	 return at;
       	 	}
       	  //empty response	
       	  else {
       		  logger.error("HTTP Response from Oauth is empty");
       		  isBadClient = true;
       		  String at = "Error";
       		  this.accessToken = at;
       		  return at;
       	  }
        } finally {
            post.releaseConnection();
        }
    }
    
	

}