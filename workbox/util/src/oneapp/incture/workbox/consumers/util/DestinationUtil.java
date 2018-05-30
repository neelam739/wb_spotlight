package oneapp.incture.workbox.consumers.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.sap.cloud.account.TenantContext;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;


public class DestinationUtil  {

	@Resource
	private TenantContext  tenantContext;


	public static DestinationConfiguration getDest(String destinationName) {

		if(!ServicesUtil.isEmpty(destinationName)){
			try {
				Context ctx = new InitialContext();
				ConnectivityConfiguration configuration =
						(ConnectivityConfiguration) ctx.lookup("java:comp/env/connectivityConfiguration");

				DestinationConfiguration destConfiguration = configuration.getConfiguration(destinationName);

				return destConfiguration;

			} catch (Exception e) {
				System.err.println("[PMC][DestinationUtil][getDest][error]"+e.getMessage());
			}
		}
		return null;
	}


	public static InputStream executeWithDest(String destinationName,String absoluteUrl,String httpMethod,String contentType ,String authentication,String tenantId){

		System.err.println("[PMC][DestinationUtil][executeWithDest][init]");
		try{
			if(!ServicesUtil.isEmpty(authentication)){
				DestinationConfiguration destConfiguration  = getDest(destinationName);
				HttpURLConnection connection =  injectHeaders( destConfiguration, absoluteUrl , authentication , httpMethod,  contentType ,tenantId) ;
				return getDataFromConnection(connection);
			}
		}
		catch(Exception e){
			System.err.println("[PMC][DestinationUtil][executeWithDest][error]"+e.getMessage());
		}
		return null;
	}

	public static InputStream getDataFromConnection(HttpURLConnection urlConnection){

		try{
			if(!ServicesUtil.isEmpty(urlConnection)){
				InputStream inputStream =  urlConnection.getInputStream();
				//return inputStream;
				return  ConnectionsUtil.logRawContent(inputStream);
			}

		} catch (Exception e) {
			System.err.println("[PMC][DestinationUtil][getDataFromConnection][error]"+e.getMessage());
		}
		return null;
	}


	private static Proxy getProxy(String proxyType) {
		System.err.println("[PMC][DestinationUtil][getProxy][init]");

		Proxy proxy = Proxy.NO_PROXY;
		String proxyHost = null;
		String proxyPort = null;

		if (PMCConstant.ON_PREMISE_PROXY.equals(proxyType)) {
			// Get proxy for on-premise destinations
			proxyHost = System.getenv("HC_OP_HTTP_PROXY_HOST");
			proxyPort = System.getenv("HC_OP_HTTP_PROXY_PORT");
		} else {
			// Get proxy for internet destinations
			proxyHost = System.getProperty("https.proxyHost");
			proxyPort = System.getProperty("https.proxyPort");
		}

		if (proxyPort != null && proxyHost != null) {
			int proxyPortNumber = Integer.parseInt(proxyPort);
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPortNumber));
		}

		return proxy;
	}

	private static HttpURLConnection injectHeaders(DestinationConfiguration destConfiguration,String absoluteUrl ,String authentication ,String httpMethod, String contentType,String tenantId) {
		System.err.println("[PMC][DestinationUtil][injectHeaders][init]");

		try {
			HttpURLConnection urlConnection =  null;
			URL url  = new URL(absoluteUrl);

			if(!ServicesUtil.isEmpty(destConfiguration)){

				//				String user = destConfiguration.getProperty("User");
				//				String password = destConfiguration.getProperty("Password");

				String proxyType = destConfiguration.getProperty("ProxyType");
				urlConnection = (HttpURLConnection) url.openConnection(getProxy(proxyType));

				if (PMCConstant.ON_PREMISE_PROXY.equals(proxyType)) {
					urlConnection.setRequestProperty("SAP-Connectivity-ConsumerAccount",tenantId);
					urlConnection.setRequestProperty("SAP-Connectivity-SCC-Location_ID",  destConfiguration.getProperty("CloudConnectorLocationId"));
				}
			}
			else{
				urlConnection = (HttpURLConnection) url.openConnection();
			}

			urlConnection.setRequestMethod(httpMethod);
			urlConnection.setRequestProperty(PMCConstant.HTTP_HEADER_ACCEPT, contentType);
			urlConnection.setRequestProperty("Authorization",authentication);

			return urlConnection;

		} catch (Exception e) {
			System.err.println("[PMC][DestinationUtil][injectHeaders][error]"+e.getMessage());
		}
		return null;

	}

	public static String consumerAcc(){

	//	String st = tenantContext.getTenant().getAccount().getId();
		String st = "d998e5467";
		return st;
	}


}
