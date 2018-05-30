package oneapp.incture.workbox.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class RestUtil {

	/*public static JSONObject callRest(String inputEntity, String serviceUrl, String methodType, String auth) {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject obj = null;
		HttpPost httpPost = null;
		HttpGet httpGet = null;
		try {
			if(methodType.equalsIgnoreCase("POST")) {
				httpPost = new HttpPost(serviceUrl);
				StringEntity params = new StringEntity(inputEntity);
				httpPost.addHeader("content-type", "application/json; charset=UTF-8");
				httpPost.setEntity(params);
				HttpResponse response = httpClient.execute(httpPost);
				String json = EntityUtils.toString(response.getEntity());
				obj = new JSONObject(json);
			} else if(methodType.equalsIgnoreCase("GET")) {
				httpGet = new HttpGet(serviceUrl);
				HttpResponse response = httpClient.execute(httpGet);
				String json = EntityUtils.toString(response.getEntity());
				obj = new JSONObject(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception : "+e.getMessage());
		}
		return obj;
	}*/
	
	public static RestResponse callRest(String inputEntity, String entityType, String serviceUrl, String methodType, String auth, String userId, String scode) {
		RestResponse restResponse = new RestResponse();
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject obj = null;
		HttpPost httpPost = null;
		HttpGet httpGet = null;
		Integer responseCode = 0;
		try {
			if(methodType.equalsIgnoreCase("POST")) {
				httpPost = new HttpPost(serviceUrl);
				if(!ServicesUtil.isEmpty(inputEntity)) {
					StringEntity params = new StringEntity(inputEntity);
					httpPost.addHeader("content-type", entityType);
					httpPost.setEntity(params);
				}
				if(!ServicesUtil.isEmpty(auth)) {
					httpPost.addHeader("Authorization", auth);
				}
				if(!ServicesUtil.isEmpty(userId) && !ServicesUtil.isEmpty(scode)) {
					auth = ServicesUtil.getBasicAuth(userId, ServicesUtil.getDecryptedText(scode));
					httpPost.addHeader("Authorization", auth);
				}
				HttpResponse response = httpClient.execute(httpPost);
				try {
					String json = EntityUtils.toString(response.getEntity());
					obj = new JSONObject(json);
				} catch (Exception e) {
					System.err.println("Exception in JSON Convertion : "+e.getMessage());
				}
				responseCode = response.getStatusLine().getStatusCode();
			} else if(methodType.equalsIgnoreCase("GET")) {
				httpGet = new HttpGet(serviceUrl);
				if(!ServicesUtil.isEmpty(auth)) {
					httpGet.addHeader("Authorization", auth);
				}
				if(!ServicesUtil.isEmpty(userId) && !ServicesUtil.isEmpty(scode)) {
					auth = ServicesUtil.getBasicAuth(userId, ServicesUtil.getDecryptedText(scode));
					httpGet.addHeader("Authorization", auth);
				}
				HttpResponse response = httpClient.execute(httpGet);
				try {
					String json = EntityUtils.toString(response.getEntity());
					obj = new JSONObject(json);
				} catch (Exception e) {
					System.err.println("Exception in JSON Convertion : "+e.getMessage());
				}
				responseCode = response.getStatusLine().getStatusCode();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception : "+e.getMessage());
		}
		restResponse.setJsonObject(obj);
		restResponse.setResponseCode(responseCode);
		return restResponse;
	}
	
	public static void main(String[] args) {
		RestResponse restResponse = RestUtil.callRest(null, null, "https://aiiha1kww.accounts.ondemand.com/service/users/password", "POST", null, "P000015", "QW1pdEAyMDE0");
		System.out.println(restResponse);
	}
}
