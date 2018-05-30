package oneapp.incture.workbox.inbox.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.Query;

import oneapp.incture.workbox.consumers.dto.InstanceDto;
import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.pmc.services.EntityManagerProviderLocal;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.UserManagementUtil;

public class WorkBoxActionClass {
	
	@EJB
	EntityManagerProviderLocal em;

	private static final String loggedin_user = UserManagementUtil.getLoggedInUser().getName();
	private static final String loggedin_user_name = UserManagementUtil.getLoggedInUser().getName();

	public ResponseMessage claim(WorkboxRequestDto requestDto) {
//		PreparedStatement preparedStatement;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Task(s) Claim Failed");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		try {
			for (InstanceDto instanceDto : requestDto.getInstanceList()) {
//				Connection con = DBConnect.getDbCon().conn;
				String TEqueryString = "UPDATE TASK_EVENTS SET CUR_PROC = '" + requestDto.getUserId() + "', CUR_PROC_DISP = '"
						+ requestDto.getUserDisplay() + "', STATUS = 'RESERVED' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "'";
				Query TEquery = em.getEntityManager().createQuery(TEqueryString);
				int update = TEquery.executeUpdate();
//				preparedStatement = con.prepareStatement("UPDATE TASK_EVENTS SET CUR_PROC = '" + requestDto.getUserId() + "', CUR_PROC_DISP = '"
//								+ requestDto.getUserDisplay() + "', STATUS = 'RESERVED' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "'");
//				int update = preparedStatement.executeUpdate();
				System.err.println("Update Task Events for Claim Task : " + update);

				String TOqueryString = "UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getUserId()+"'";
				Query TOquery = em.getEntityManager().createQuery(TOqueryString);
				
//				preparedStatement = con.prepareStatement("UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getUserId()+"'");
				update = TOquery.executeUpdate();
				System.err.println("Update Task Owners for Claim Task : " + update);
				responseMessage.setMessage("Task(s) Claimed");
				responseMessage.setStatus("SUCCESS");
				responseMessage.setStatusCode("0");
			}
		} catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
			responseMessage.setMessage("Claim Failed with Exception : "+e.getMessage());
			responseMessage.setStatus("FAILURE");
			responseMessage.setStatusCode("1");
		}
		return responseMessage;
	}
	
	public ResponseMessage release(WorkboxRequestDto requestDto) {
//		PreparedStatement preparedStatement;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Tasks(s) Claim Failed");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		try {
			for (InstanceDto instanceDto : requestDto.getInstanceList()) {
//				Connection con = DBConnect.getDbCon().conn;
				
				String TEqueryString = "UPDATE TASK_EVENTS SET CUR_PROC = '', CUR_PROC_DISP = '', STATUS = 'READY' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "'";
				Query TEquery = em.getEntityManager().createQuery(TEqueryString);
				
//				preparedStatement = con.prepareStatement("UPDATE TASK_EVENTS SET CUR_PROC = '', CUR_PROC_DISP = '', STATUS = 'READY' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "'");
				int update = TEquery.executeUpdate();
				System.err.println("Update Task Events for Release Task : " + update);
				
				String TOqueryString = "UPDATE TASK_OWNERS SET IS_PROCESSED = '0' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getUserId()+"'";
				Query TOquery = em.getEntityManager().createQuery(TOqueryString);

//				preparedStatement = con.prepareStatement("UPDATE TASK_OWNERS SET IS_PROCESSED = '0' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getUserId()+"'");
				update = TOquery.executeUpdate();
				System.err.println("Update Task Owners for Release Task : " + update);
				responseMessage.setMessage("Task(s) Released");
				responseMessage.setStatus("SUCCESS");
				responseMessage.setStatusCode("0");
			}
		} catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
			responseMessage.setMessage("Release Failed with Exception : "+e.getMessage());
			responseMessage.setStatus("FAILURE");
			responseMessage.setStatusCode("1");
		}
		return responseMessage;
	}
	
	public ResponseMessage forward(WorkboxRequestDto requestDto) {
//		PreparedStatement preparedStatement;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Tasks(s) Claim Failed");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		DateFormat df = new SimpleDateFormat(PMCConstant.DETAILDATE_AMPM_FORMATE);
		try {
			for (InstanceDto instanceDto : requestDto.getInstanceList()) {
//				Connection con = DBConnect.getDbCon().conn;
				
				String TEqueryString = "UPDATE TASK_EVENTS SET CUR_PROC = '"+requestDto.getForwardTo()+"', CUR_PROC_DISP = '"+requestDto.getForwardTo()+"', FORWARDED_BY = '"+loggedin_user+"', STATUS = 'RESERVED', FORWARDED_AT = '"+df.format(new Date())+"' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "'";
				Query TEquery = em.getEntityManager().createQuery(TEqueryString);
				
//				preparedStatement = con.prepareStatement("UPDATE TASK_EVENTS SET CUR_PROC = '"+requestDto.getForwardTo()+"', CUR_PROC_DISP = '"+requestDto.getForwardTo()+"', FORWARDED_BY = '"+loggedin_user+"', STATUS = 'RESERVED', FORWARDED_AT = '"+df.format(new Date())+"' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "'");
				int update = TEquery.executeUpdate();
				System.err.println("Update Task Events for Forward Task : " + update);
				
				String TOqueryString = "UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getForwardTo()+"'";
				Query TOquery = em.getEntityManager().createQuery(TOqueryString);

//				preparedStatement = con.prepareStatement("UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getForwardTo()+"'");
				update = TOquery.executeUpdate();
				System.err.println("Update Task Owners for Forward Task : " + update);
				responseMessage.setMessage("Task(s) Forwarded");
				responseMessage.setStatus("SUCCESS");
				responseMessage.setStatusCode("0");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception : " + e.getMessage());
			responseMessage.setMessage("Forward Failed with Exception : "+e.getMessage());
			responseMessage.setStatus("FAILURE");
			responseMessage.setStatusCode("1");
		}
		return responseMessage;
	}
	
	public ResponseMessage complete(WorkboxRequestDto requestDto) {
//		PreparedStatement preparedStatement;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Tasks(s) Claim Failed");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		DateFormat df = new SimpleDateFormat(PMCConstant.DETAILDATE_AMPM_FORMATE);
		try {
			for (InstanceDto instanceDto : requestDto.getInstanceList()) {
//				Connection con = DBConnect.getDbCon().conn;
				WorkboxRequestDto wbRequestDto = new WorkboxRequestDto();
				List<InstanceDto> instances = new ArrayList<InstanceDto>();
				instances.add((instanceDto));
				wbRequestDto.setInstanceList(instances);
				wbRequestDto.setUserId(loggedin_user);
				wbRequestDto.setUserDisplay(loggedin_user_name);
				
				String TEqueryString = "UPDATE TASK_EVENTS SET STATUS = 'COMPLETED', COMPLETED_AT = '"+df.format(new Date())+"' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "'";
				Query TEquery = em.getEntityManager().createQuery(TEqueryString);
				
//				preparedStatement = con.prepareStatement("UPDATE TASK_EVENTS SET STATUS = 'COMPLETED', COMPLETED_AT = '"+df.format(new Date())+"' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "'");
				int update = TEquery.executeUpdate();
				System.err.println("Update Task Events for Complete Task : " + update);
				
				String TOqueryString = "UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getForwardTo()+"'";
				Query TOquery = em.getEntityManager().createQuery(TOqueryString);

//				preparedStatement = con.prepareStatement("UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getForwardTo()+"'");
				update = TOquery.executeUpdate();
				System.err.println("Update Task Owners for Complete Task : " + update);
				responseMessage.setMessage("Task(s) Completed");
				responseMessage.setStatus("SUCCESS");
				responseMessage.setStatusCode("0");
			}
		} catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
			responseMessage.setMessage("Complete Failed with Exception : "+e.getMessage());
			responseMessage.setStatus("FAILURE");
			responseMessage.setStatusCode("1");
		}
		return responseMessage;
	}
	
	public static void main(String[] args) {
		System.out.println(new Date());
	}
}
