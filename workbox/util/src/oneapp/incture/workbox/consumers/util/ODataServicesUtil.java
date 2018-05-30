package oneapp.incture.workbox.consumers.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;
import oneapp.incture.workbox.consumers.util.DestinationUtil;



/**
 * Contains utility functions to be used for consuming oData Services
 * 
 * @version R1
 */
public class ODataServicesUtil {

	public static String USERNAME;
	public static String PASSWORD; 


	public static NodeList xPathOdata(String serviceUrl, String usedFormatXml,String type ,String expression,String authentication ,String destinationName,String tenantId){

		NodeList nodeList = null ;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputStream inputFile = null;
			if(!ServicesUtil.isEmpty(destinationName)){
				inputFile = DestinationUtil.executeWithDest(destinationName,serviceUrl, type,usedFormatXml,authentication,tenantId);
			}
			else{
				inputFile = ConnectionsUtil.execute(authentication,serviceUrl,usedFormatXml, type);
			}
			if(!ServicesUtil.isEmpty(inputFile)){
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();
				XPath xPath =  XPathFactory.newInstance().newXPath();
				nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
				return nodeList;
			}

		} catch (Exception e) {
			System.err.println("[PMC][ODataServicesUtil][xPathOdata][error]"+e.getMessage());
		} 
		return null;
	}

	public static String readActions(String serviceUri, String contentType,String authentication)
			throws IOException, ODataException {
		String actions="";
		try{

			InputStream inputStream = ConnectionsUtil.execute(authentication,serviceUri, contentType, PMCConstant.HTTP_METHOD_GET);

			String st = ServicesUtil.convertInputStreamToString(inputStream);

			if(st.contains("Submit")){
				actions = actions +"Submit";
			}
			if(st.contains("Approve")){
				if(!ServicesUtil.isEmpty(actions)){
					actions = actions +",";	
				}
				actions = actions +"Approve";
			}
			if(st.contains("Reject")){
				if(!ServicesUtil.isEmpty(actions)){
					actions = actions +",";	
				}
				actions = actions +"Reject";
			}
		}
		catch(Exception e){
			System.err.println("[PMC][ODataServicesUtil][readActions][error]  "+ e.getMessage());
		}
		//	System.err.println("[PMC][ODataServicesUtil][readActions][result]  "+ actions);
		return actions;
	}


	public static String getCountInString(String authentication,String serviceUri,String contentType){

		InputStream inputStream;
		try {
			inputStream = ConnectionsUtil.execute(authentication,serviceUri, "text/plain", PMCConstant.HTTP_METHOD_GET);
			//return ServicesUtil.convertInputStreamToString(inputStream);

			return new String(ConnectionsUtil.streamToArray(inputStream));
		} catch (IOException e) {
			System.err.println("[PMC][ODataServicesUtil][getCountInString][error]  "+ e.getMessage());
		}
		return null;

	}
	
	public static String getCountInStringWithDest(String authentication,String serviceUri,String contentType,String destinationName,String tenantId){

		InputStream inputStream;
		try {
			
			inputStream =	DestinationUtil.executeWithDest(destinationName, serviceUri, PMCConstant.HTTP_METHOD_GET, contentType , authentication, tenantId);
			//return ServicesUtil.convertInputStreamToString(inputStream);

			return new String(ConnectionsUtil.streamToArray(inputStream));
		} catch (IOException e) {
			System.err.println("[PMC][ODataServicesUtil][getCountInString][error]  "+ e.getMessage());
		}
		return null;

	}

}