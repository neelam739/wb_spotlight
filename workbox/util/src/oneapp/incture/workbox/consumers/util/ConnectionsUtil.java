package oneapp.incture.workbox.consumers.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;



/**
 * Contains utility functions to be used for consuming oData Services
 * 
 * @version R1
 */
public class ConnectionsUtil {


	public static String AUTH ;

	public static InputStream execute(String authentication ,String relativeUri, String contentType, String httpMethod) throws IOException {

		if(!setAuth(authentication).equals("SUCCESS")){
			return null;
		}

		InputStream content  = null;
		HttpURLConnection connection = null;
		try {
			connection = initializeConnection(relativeUri, contentType, httpMethod);
			connection.connect();
			checkStatus(connection);
			content = connection.getInputStream();
			content = logRawContent( content);

		}catch(Exception e){
			System.err.println("[PMC][ConnectionsUtil][execute][error]  "+ e.getMessage());
		}
		finally{
			if(!ServicesUtil.isEmpty(connection)){
				connection.disconnect();
			}
		}
		return content;
	}


	private static HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod)
			throws MalformedURLException, IOException {
		System.err.println(" absolutUri " + absolutUri);
		URL url = new URL(absolutUri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod(httpMethod);
		connection.setRequestProperty(PMCConstant.HTTP_HEADER_ACCEPT, contentType);
		System.err.println("Authorisation " +AUTH);
		connection.setRequestProperty("Authorization", AUTH);

		if (PMCConstant.HTTP_METHOD_POST.equals(httpMethod) || PMCConstant.HTTP_METHOD_PUT.equals(httpMethod)) {
			connection.setDoOutput(true);
			connection.setRequestProperty(PMCConstant.HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		return connection;
	}

	private static HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {

		HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		System.err.println(" httpStatusCode.getStatusCode() " + httpStatusCode.getStatusCode());
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
			throw new RuntimeException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " "
					+ httpStatusCode.toString());
		}
		return httpStatusCode;
	}

	public static InputStream logRawContent(InputStream content) throws IOException {
		if (PMCConstant.PRINT_RAW_CONTENT) {
			byte[] buffer = streamToArray(content);
			System.err.println("PMC"+new String(buffer));
			return new ByteArrayInputStream(buffer);
		}
		return content;
	}

	public static byte[] streamToArray(InputStream stream) throws IOException {
		byte[] result = new byte[0];
		byte[] tmp = new byte[8192];
		int readCount = stream.read(tmp);
		while (readCount >= 0) {
			byte[] innerTmp = new byte[result.length + readCount];
			System.arraycopy(result, 0, innerTmp, 0, result.length);
			System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
			result = innerTmp;
			readCount = stream.read(tmp);
		}
		stream.close();
		return result;
	}

	/*public static String getCsrfToken(String url,String userName,String  scode){
		HttpClient httpclient = new HttpClient();
		String X_CSRF_TOKEN = "FAILURE"; 
		try
		{
			HttpMethod httpget = new GetMethod(url);
			httpget.setRequestHeader("Authorization",ServicesUtil.getBasicAuth(userName, scode));
			httpget.setRequestHeader("x-csrf-token", "Fetch");
			int responseCode  = httpclient.executeMethod(httpget);                           
			Header	headers[]  =  httpget.getResponseHeaders();
			for (Header h : headers) {
				if (h.getName().toLowerCase().equals("x-csrf-token")) {
					X_CSRF_TOKEN = h.getValue();
					System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][csrf] " +X_CSRF_TOKEN);
				}
			}
		}catch(Exception e){
			System.err.println("[PMC][ConnectionsUtil][getCsrfToken][error]"+e.getMessage());
		}
		return X_CSRF_TOKEN;
	}
*/



	public static ResponseDto executeActionHttp(String url,String authorisation,String locationId ,String tenant,String csrfToken) throws IOException
	{       
		System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][entry] " +url);
		ResponseDto returnMessage = new ResponseDto();
		HttpClient httpclient = new HttpClient();
		HttpMethod postRequest = new PostMethod(url);
		try{
			//                                    csrfToken = getCsrfToken( url , userName , scode, locationId, tenant);
			/*if(ServicesUtil.isEmpty(csrfToken) || csrfToken.equals("FAILURE")){
                                                        csrfToken = getCsrfToken( url , userName , scode, locationId, tenant);
                                                        if(ServicesUtil.isEmpty(csrfToken) || csrfToken.equals("FAILURE")){
                                                                        returnMessage.setMessage("Failed to fetch the csrf token");
                                                                        returnMessage.setStatus("FAILURE");
                                                                        returnMessage.setStatusCode("1");
                                                                        return returnMessage;
                                                        }
                                        }*/

			// GET Request for X-CSRF Token

			String X_CSRF_TOKEN = "";

			HttpMethod httpget = new GetMethod(url);
			httpget.setRequestHeader("Authorization",authorisation);
			httpget.setRequestHeader("x-csrf-token", "Fetch");
			if(!ServicesUtil.isEmpty(locationId) && !ServicesUtil.isEmpty(tenant)) {
				httpget.setRequestHeader("SAP-Connectivity-ConsumerAccount", tenant);
				httpget.setRequestHeader("SAP-Connectivity-SCC-Location_ID", locationId);

				String PROXY_HOST = System.getenv("HC_OP_HTTP_PROXY_HOST");
				int PROXY_PORT = Integer.parseInt(System.getenv("HC_OP_HTTP_PROXY_PORT"));

				HostConfiguration config = httpclient.getHostConfiguration();
				config.setProxy(PROXY_HOST, PROXY_PORT);
				httpclient.setHostConfiguration(config);
//				AuthScope authScope = new AuthScope(PROXY_HOST, PROXY_PORT);
//				Credentials credentials = new UsernamePasswordCredentials(userName, scode);
//				httpclient.getState().setProxyCredentials(authScope, credentials);
			}
			int responseCodeGet  = httpclient.executeMethod(httpget);                           
			Header headers[]  =  httpget.getResponseHeaders();
			for (Header h : headers) {
				if (h.getName().toLowerCase().equals("x-csrf-token")) {
					X_CSRF_TOKEN = h.getValue();
					System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][csrf] " +X_CSRF_TOKEN + " responseCode  "+responseCodeGet);
				}
			}

			postRequest.addRequestHeader("Authorization", authorisation);
			postRequest.addRequestHeader("x-csrf-token", X_CSRF_TOKEN);

			if(!ServicesUtil.isEmpty(tenant) && !ServicesUtil.isEmpty(locationId)){
				postRequest.addRequestHeader("SAP-Connectivity-ConsumerAccount", tenant);
				postRequest.addRequestHeader("SAP-Connectivity-SCC-Location_ID", locationId);

				String PROXY_HOST = System.getenv("HC_OP_HTTP_PROXY_HOST");
				int PROXY_PORT = Integer.parseInt(System.getenv("HC_OP_HTTP_PROXY_PORT"));

				HostConfiguration config = httpclient.getHostConfiguration();
				config.setProxy(PROXY_HOST, PROXY_PORT);
				httpclient.setHostConfiguration(config);
			}
			
			int responseCode = (httpclient.executeMethod(postRequest));
			String result = postRequest.getResponseBodyAsString();
			int responseCodeStatusLine = postRequest.getStatusLine().getStatusCode();

			System.err.println("Http Response: Response code " + responseCode +"result.toString() from actionType" +result + "responseCodeStatusLine : "+responseCodeStatusLine);
			returnMessage.setStatusCode(Integer.toString(responseCode));
			if(400 <= responseCode && responseCode <= 599){
				returnMessage.setStatus("FAILURE");
			}
			else{
				returnMessage.setStatus("SUCCESS");
			}

			if((400 <= responseCode && responseCode <= 599) && result.toString().contains("error")){
				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc;
					InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8.name()));
					doc = dBuilder.parse(stream);
					doc.getDocumentElement().normalize();
					XPath xPath =  XPathFactory.newInstance().newXPath();
					XPathExpression expr1=xPath.compile("error/message/text()"); 
					NodeList node1 = (NodeList) expr1.evaluate(doc, XPathConstants.NODESET);
					returnMessage.setMessage(node1.item(0).getNodeValue());

				} catch (Exception e1) {
					System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][togetError]"+e1.getMessage());
				}
			}

		}catch(Exception e ){
			System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][error]"+e.getMessage());
			e.printStackTrace();
			returnMessage.setMessage(e.getMessage());
			returnMessage.setStatus("FAILURE");
			returnMessage.setStatusCode("1");
		}
		System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][exit]"+returnMessage);
		return returnMessage;
	}


	private static String setAuth(String authentication){
		if(!ServicesUtil.isEmpty(authentication))
		{
			AUTH = authentication;
			return "SUCCESS";
		}
		else{
			System.err.println("[PMC][ConnectionsUtil][setUserPassword] either usernameor password is empty");
			return "FAILURE";
		}

	}

}