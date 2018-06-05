package oneapp.incture.workbox.inbox.services;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import oneapp.incture.workbox.consumers.dto.InstanceDto;
import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.consumers.services.ConsumeBPMDataFacadeLocal;
import oneapp.incture.workbox.dbproviders.DatabasePropertyProvider;
import oneapp.incture.workbox.inbox.dao.WorkBoxDao;
import oneapp.incture.workbox.inbox.dto.TrackingResponse;
import oneapp.incture.workbox.inbox.dto.WorkBoxDto;
import oneapp.incture.workbox.inbox.dto.WorkboxResponseDto;
import oneapp.incture.workbox.pmc.dto.TasksCountDTO;
import oneapp.incture.workbox.pmc.services.EntityManagerProviderLocal;
import oneapp.incture.workbox.pmc.wsdlconsumers.CustomAttributesConsumer;
import oneapp.incture.workbox.pmc.wsdlconsumers.UMEManagementEngineConsumer;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.util.NoResultFault;
import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;
import oneapp.incture.workbox.util.UserManagementUtil;


/**
 * Session Bean implementation class WorkboxFacade
 */
@Stateless
public class WorkboxFacade implements WorkboxFacadeLocal {

	/*@WebServiceRef(name="UMEUserManagementFacadeService")
	UMEUserManagementFacadeService umeService;

	@WebServiceRef(name="CustomAttributesServiceService")
	CustomAttributesServiceService customAttribute;*/

	UMEManagementEngineConsumer umeConsumer = null;
	CustomAttributesConsumer customAttributes = null;

	@EJB
	EntityManagerProviderLocal em;


	@EJB
	ConsumeBPMDataFacadeLocal bpmLocal;


	public WorkboxFacade() {
	}

	@Override
	public String sayHello(){
		return "Hello From EJB!";
	}

	@Override
	public WorkboxResponseDto getWorkboxFilterData(String processName, String requestId, String createdBy,
			String createdAt, String status, Integer skipCount, Integer maxCount, Integer page, String orderBy,
			String orderType, String origin) {
		//	System.err.println("[PMC][WorkBoxFacade][getWorkboxFilterData] method invoked ");

//		String fetchStatus = fetchData("6000847", "india123");

		customAttributes = new CustomAttributesConsumer();
		umeConsumer = new UMEManagementEngineConsumer();
		WorkboxResponseDto workboxResponseDto = new WorkboxResponseDto();
		ResponseMessage message = new ResponseMessage();
		//	String taskOwner = umeConsumer.getLoggedInUser().getUserId(); 
		String taskOwner = UserManagementUtil.getLoggedInUser().getName();
		if(!ServicesUtil.isEmpty(taskOwner)) {
			taskOwner = taskOwner.toUpperCase();
		}
		if (!ServicesUtil.isEmpty(taskOwner)) {
			SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");

			String dataQuery = 	"SELECT pe.REQUEST_ID AS REQUEST_ID, pe.NAME AS PROCESS_NAME ,te.EVENT_ID AS TASK_ID, te.DESCRIPTION AS DESCRIPTION, te.NAME AS TASK_NAME, te.SUBJECT AS TASK_SUBJECT, pe.STARTED_BY_DISP AS STARTED_BY, te.CREATED_AT AS TASK_CREATED_AT, te.STATUS AS TASK_STATUS,te.CUR_PROC AS CUR_PROC,'SLA' AS SLA, te.PROCESS_ID AS PROCESS_ID, te.URL AS URL,te.COMP_DEADLINE AS SLA_DUE_DATE, Te.FORWARDED_BY AS FORWARDED_BY, Te.FORWARDED_AT AS FORWARDED_AT, pct.PROCESS_DISPLAY_NAME AS PROCESS_DISPLAY_NAME, te.ORIGIN AS ORIGIN FROM TASK_EVENTS te, PROCESS_EVENTS pe, TASK_OWNERS tw , PROCESS_CONFIG_TB pct WHERE pe.PROCESS_ID = te.PROCESS_ID AND tw.EVENT_ID = te.EVENT_ID AND pct.PROCESS_NAME = pe.NAME";
			//	+ "AND pe.STATUS = 'IN_PROGRESS'";
			String query = "";
			if (!ServicesUtil.isEmpty(status)) {
				query = query + " AND te.STATUS = '" + status + "' AND tw.TASK_OWNER = '" + taskOwner + "'";
				if (status.equals("READY")) {
					query = query + "and te.status='READY'";
				} else if (status.equals("RESERVED")) {
					query = query + "AND  te.CUR_PROC = '" + taskOwner + "'";
				}
			} else {
				query = query + " AND te.STATUS <> 'COMPLETED' AND(tw.TASK_OWNER = '" + taskOwner
						+ "' and (te.status='READY' OR te.CUR_PROC = '" + taskOwner + "'))";
			}
			if (!ServicesUtil.isEmpty(processName)) {
				query = query + " AND pe.NAME";
				if (processName.indexOf(",") != -1) {
					query = query + " IN (";
					String[] processes = processName.split(",");
					for (String process : processes) {
						query = query + "'" + process + "',";
					}
					query = query.substring(0, query.length() - 1);
					query = query + ")";
				} else {
					query = query + " = '" + processName + "'";
				}
			}
			if (!ServicesUtil.isEmpty(requestId)) {
				query = query + " AND pe.REQUEST_ID LIKE '%" + requestId + "%'";
			}
			if (!ServicesUtil.isEmpty(createdBy)) {
				query = query + " AND pe.STARTED_BY = '" + createdBy + "'";
			}
			if (!ServicesUtil.isEmpty(createdAt)) {
				query = query + " AND to_char(cast(te.CREATED_AT as date),'MM/DD/YYYY')= '" + createdAt + "'";
			}
			if(!ServicesUtil.isEmpty(origin)) {
				query = query + " AND te.ORIGIN = '"+origin+"'";
			}

			String countQuery = " SELECT  COUNT(*) AS COUNT FROM TASK_EVENTS te, PROCESS_EVENTS pe, TASK_OWNERS tw WHERE pe.PROCESS_ID = te.PROCESS_ID AND tw.EVENT_ID = te.EVENT_ID"
					//		+ "AND pe.STATUS = 'IN_PROGRESS'" 
					+ query;
			System.err.println("[PMC][WorkBoxFacade][getWorkboxFilterData][countQuery]" + countQuery);
			Query cq = em.getEntityManager().createNativeQuery(countQuery.trim(), "workBoxCountResult");
			BigDecimal count = ServicesUtil.getBigDecimal(/* (Long) */ cq.getSingleResult());

			if (ServicesUtil.isEmpty(orderType) && ServicesUtil.isEmpty(orderBy))
				query = query + " ORDER BY 8 DESC";
			else {
				if (!ServicesUtil.isEmpty(orderType) && !ServicesUtil.isEmpty(orderBy)) {

					if (orderType.equals(PMCConstant.ORDER_TYPE_CREATED_AT)) {
						if (orderBy.equals(PMCConstant.ORDER_BY_ASC))
							query = query + " ORDER BY 8 ASC";
						else {
							if (orderBy.equals(PMCConstant.ORDER_BY_DESC))
								query = query + " ORDER BY 8 DESC";
						}
					} else if (orderType.equals(PMCConstant.ORDER_TYPE_SLA_DUE_DATE)) {
						if (orderBy.equals(PMCConstant.ORDER_BY_ASC))
							query = query + " ORDER BY 14 ASC";
						else {
							if (orderBy.equals(PMCConstant.ORDER_BY_DESC))
								query = query + " ORDER BY 14 DESC";
						}
					}
				} else {
					if (orderType.equals(PMCConstant.ORDER_TYPE_CREATED_AT))
						query = query + " ORDER BY 8 ASC";
					else {
						if (orderType.equals(PMCConstant.ORDER_TYPE_SLA_DUE_DATE))
							query = query + " ORDER BY 14 ASC";
					}
				}

			}

			System.err.println("[PMC][WorkBoxFacade][getWorkboxFilterData][query]" + query);

			dataQuery = dataQuery + query;
			
			System.err.println("[PMC][WorkBoxFacade][getWorkboxFilterData][dataQuery]" + dataQuery);
			Query q = em.getEntityManager().createNativeQuery(dataQuery.trim(), "workBoxResults");

			if (!ServicesUtil.isEmpty(maxCount) && maxCount > 0 && !ServicesUtil.isEmpty(skipCount)
					&& skipCount >= 0) {
				int first = skipCount;
				int last = maxCount;

				/* Commented for Pagination in HANA */
				q.setFirstResult(first);
				q.setMaxResults(last);

//				dataQuery += " LIMIT "+last+" OFFSET "+first+"";
			}
			if (!ServicesUtil.isEmpty(page) && page > 0) {
				int first = (page - 1) * PMCConstant.PAGE_SIZE;
				int last = PMCConstant.PAGE_SIZE;

				/* Commented for Pagination in HANA */
				q.setFirstResult(first);
				q.setMaxResults(last);

//				dataQuery += " LIMIT "+last+" OFFSET "+first+"";

			}

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = q.getResultList();
			if (ServicesUtil.isEmpty(resultList)) {
				try {
					throw new NoResultFault("NO RECORD FOUND");
				} catch (NoResultFault e) {
					System.err.println("NO RESULT FOUND");
					message.setStatus("NO RESULT FOUND");
					message.setStatusCode("1");
					workboxResponseDto.setResponseMessage(message);
				}
			} else {
				List<WorkBoxDto> workBoxDtos = new ArrayList<WorkBoxDto>();
				System.err.println("ResultList - " + resultList.size());
				for (Object[] obj : resultList) {
					WorkBoxDto workBoxDto = new WorkBoxDto();
					workBoxDto.setRequestId(obj[0] == null ? null : (String) obj[0]);
					workBoxDto.setProcessName(obj[1] == null ? null : (String) obj[1]);
					workBoxDto.setTaskId(obj[2] == null ? null : (String) obj[2]);
					workBoxDto.setTaskDescription(obj[3] == null ? null : (String) obj[3]);
					workBoxDto.setName(obj[4] == null ? null : (String) obj[4]);
					workBoxDto.setSubject(obj[5] == null ? null : (String) obj[5]);
					workBoxDto.setStartedBy(obj[6] == null ? null : (String) obj[6]);
					workBoxDto.setCreatedAt(obj[7] == null ? null : simpleDateFormat1.format(ServicesUtil.resultAsDate(obj[7])));
					workBoxDto.setStatus(obj[8] == null ? null : (String) obj[8]);
					workBoxDto.setSlaDisplayDate(obj[13] == null ? null : simpleDateFormat1.format(ServicesUtil.resultAsDate(obj[13])));
					workBoxDto.setDetailURL(obj[12] == null ? null : ((String) obj[12]));
					if (!ServicesUtil.isEmpty(obj[13]) && !ServicesUtil.isEmpty(obj[7])) {
						Calendar created = ServicesUtil.timeStampToCal(obj[7]);
						Calendar slaDate = ServicesUtil.timeStampToCal(obj[13]);
						String timeLeftString = ServicesUtil.getSLATimeLeft(slaDate);
						if (timeLeftString.equals("Breach")) {
							workBoxDto.setBreached(true);
						} else {
							workBoxDto.setBreached(false);
							workBoxDto.setTimeLeftDisplayString(timeLeftString);
							workBoxDto.setTimePercentCompleted(
									ServicesUtil.getPercntTimeCompleted(created, slaDate));
						}
					}
					workBoxDto.setSla(obj[10] == null ? null : (String) obj[10]);
					workBoxDto.setProcessId(obj[11] == null ? null : (String) obj[11]);
//					workBoxDto.setCustomAttributes(customAttributes.getCustomAttributes(workBoxDto.getTaskId()));
					workBoxDto.setForwardedBy(obj[14] == null ? null : (String) obj[14]);
					workBoxDto.setForwardedAt(obj[15] == null ? null
							: simpleDateFormat1.format(ServicesUtil.resultAsDate(obj[15])));
					workBoxDto.setProcessDisplayName(obj[16] == null ? (String) obj[1] : (String) obj[16]);
					// workBoxDto.setUrl();
					workBoxDto.setOrigin(obj[17] == null ? null : (String) obj[17]);
					
					System.err.println("setProcessDisplayName" + workBoxDto.getProcessDisplayName());
					workBoxDtos.add(workBoxDto);
				}
				workboxResponseDto.setPageCount(PMCConstant.PAGE_SIZE);
				workboxResponseDto.setCount(count);
				workboxResponseDto.setWorkBoxDtos(workBoxDtos);
				message.setStatus("Success");
				message.setStatusCode("0");
				message.setMessage("Process Details Fetched Successfully");
				workboxResponseDto.setResponseMessage(message);
			}
			return workboxResponseDto;
		}
		message.setStatus("FAILURE");
		message.setStatusCode("1");
		message.setMessage("NO USER FOUND");
		workboxResponseDto.setResponseMessage(message);
		return workboxResponseDto;

	}


	@Override
	public WorkboxResponseDto getWorkboxCompletedFilterData(String processName, String requestId, String createdBy, String createdAt, String completedAt, String period, Integer skipCount, Integer maxCount, Integer page) {
		umeConsumer = new UMEManagementEngineConsumer();
		WorkBoxDao dao = new WorkBoxDao(em.getEntityManager());
		String userId = null;
//		userId = umeConsumer.getLoggedInUser().getUserId();
		userId = UserManagementUtil.getLoggedInUser().getName();
		if(ServicesUtil.isEmpty(userId)) {
			userId = userId.toUpperCase();
		}
		return dao.getWorkboxCompletedFilterData(userId, processName, requestId, createdBy, createdAt, completedAt, period, skipCount, maxCount, page);
	}


	@Override
	public TrackingResponse getTrackingResults() {

		umeConsumer = new UMEManagementEngineConsumer();
		TrackingResponse response = new TrackingResponse();
		ResponseMessage respMessage = new ResponseMessage();
		String userId = null;
//		userId = umeConsumer.getLoggedInUser().getUserId();
		userId = UserManagementUtil.getLoggedInUser().getName();
//		DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if(!ServicesUtil.isEmpty(userId)){

			BigDecimal compOnSlaCount = null;
			BigDecimal compOffSlaCount = null;
			BigDecimal inProgOnSlaCount = null;
			BigDecimal inProgOffSlaCount = null;
			BigDecimal inProgApproachingCount = null;

			List<TasksCountDTO> responseList = new ArrayList<TasksCountDTO>();

			String completedCountQry1 = "SELECT COUNT(EVENT_ID) AS COUNT FROM TASK_EVENTS WHERE STATUS = 'COMPLETED' AND COMP_DEADLINE <= COMPLETED_AT and CUR_PROC = '"
					+ userId + "'";
			String completedCountQry2 = "SELECT COUNT(EVENT_ID) AS COUNT FROM TASK_EVENTS WHERE STATUS = 'COMPLETED' AND COMP_DEADLINE > COMPLETED_AT and CUR_PROC = '"
					+ userId + "'";

			Query comCountQry1 = em.getEntityManager().createNativeQuery(completedCountQry1, "countResult");
			compOffSlaCount = ServicesUtil.getBigDecimal(/* (Long) */ comCountQry1.getSingleResult());

			Query comCountQry2 = em.getEntityManager().createNativeQuery(completedCountQry2, "countResult");
			compOnSlaCount = ServicesUtil.getBigDecimal(/* (Long) */ comCountQry2.getSingleResult());

			System.err.println("Completed off Sla Count : " + compOffSlaCount);
			System.err.println("Completed on Sla Count : " + compOnSlaCount);

			Date currDate = new Date();
			DateFormat df = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");


			String inProgressCountQry1 = "SELECT COUNT(DISTINCT(TE.EVENT_ID)) AS COUNT " +
					"FROM TASK_EVENTS TE " +
					"JOIN TASK_OWNERS TW " +
					"ON TE.EVENT_ID    = TW.EVENT_ID " +
					"AND ((TE.STATUS   = 'RESERVED' " +
					"AND TE.CUR_PROC   = '"+userId+"'  AND TW.IS_PROCESSED = '1') " +
					"OR (TE.STATUS     = 'READY' " +
					"AND TW.TASK_OWNER ='"+userId+"')) " +
					"AND (TO_DATE('"+df.format(currDate)+"', 'DD/MM/YY hh:mi:ss AM') > TE.COMP_DEADLINE)";

			Query resInProgressCountQry1 = em.getEntityManager().createNativeQuery(inProgressCountQry1, "countResult");
			inProgOffSlaCount = ServicesUtil.getBigDecimal(/* (Long) */ resInProgressCountQry1.getSingleResult());
			System.err.println("[pmc][tracking] : "+inProgOnSlaCount);

			String inProgressCountQry2 = "SELECT COUNT(DISTINCT(TE.EVENT_ID)) AS COUNT " +
					"FROM TASK_EVENTS TE " +
					"JOIN TASK_OWNERS TW " +
					"ON TE.EVENT_ID    = TW.EVENT_ID " +
					"AND ((TE.STATUS   = 'RESERVED' " +
					"AND TE.CUR_PROC   = '"+userId+"' AND TW.IS_PROCESSED = '1') " +
					"OR (TE.STATUS     = 'READY' " +
					"AND TW.TASK_OWNER ='"+userId+"')) " +
					"AND (TO_DATE('"+df.format(currDate)+"', 'DD/MM/YY hh:mi:ss AM')  < TE.COMP_DEADLINE-1)";

			Query resInProgressCountQry2 = em.getEntityManager().createNativeQuery(inProgressCountQry2, "countResult");
			inProgOnSlaCount = ServicesUtil.getBigDecimal(/* (Long) */ resInProgressCountQry2.getSingleResult());
			System.err.println("[pmc][tracking] : "+inProgOnSlaCount);

			String inProgressApproching = "SELECT COUNT(DISTINCT(TE.EVENT_ID)) AS COUNT " +
					"FROM TASK_EVENTS TE " +
					"JOIN TASK_OWNERS TW " +
					"ON TE.EVENT_ID    = TW.EVENT_ID " +
					"AND ((TE.STATUS   = 'RESERVED' " +
					"AND TE.CUR_PROC   = '"+userId+"' AND TW.IS_PROCESSED = '1') " +
					"OR (TE.STATUS     = 'READY' " +
					"AND TW.TASK_OWNER ='"+userId+"')) " +
					"AND (TO_DATE('"+df.format(currDate)+"', 'DD/MM/YY hh:mi:ss AM') > TE.COMP_DEADLINE - 1 AND (TO_DATE('"+df.format(currDate)+"', 'DD/MM/YY hh:mi:ss AM') <= TE.COMP_DEADLINE))";

			Query resInProgressApproaching = em.getEntityManager().createNativeQuery(inProgressApproching, "countResult");
			inProgApproachingCount = ServicesUtil.getBigDecimal(/* (Long) */ resInProgressApproaching.getSingleResult());
			System.err.println("[pmc][tracking] : "+inProgApproachingCount);

			if ((!ServicesUtil.isEmpty(compOnSlaCount) && (!ServicesUtil.isEmpty(compOffSlaCount)
					&& (!ServicesUtil.isEmpty(inProgOnSlaCount) && (!ServicesUtil.isEmpty(inProgOffSlaCount)))))) {

				TasksCountDTO resp = new TasksCountDTO();
				resp.setStatus("Completed - Past Due");
				resp.setCount(compOffSlaCount);
				responseList.add(resp);
				TasksCountDTO resp1 = new TasksCountDTO();
				resp1.setStatus("Completed - On Track");
				resp1.setCount(compOnSlaCount);
				responseList.add(resp1);
				TasksCountDTO resp2 = new TasksCountDTO();
				resp2.setStatus("In Progress - Past Due");
				resp2.setCount(inProgOffSlaCount);
				responseList.add(resp2);
				TasksCountDTO resp3 = new TasksCountDTO();
				resp3.setStatus("In Progress - On Track");
				resp3.setCount(inProgOnSlaCount);
				responseList.add(resp3);
				TasksCountDTO resp4 = new TasksCountDTO();
				resp4.setStatus("In Progress - Approaching");
				resp4.setCount(inProgApproachingCount);
				responseList.add(resp4);


				response.setTasksCount(responseList);
				respMessage.setMessage("Tasks Count Sent Successfully");
				respMessage.setStatus("Success");
				respMessage.setStatusCode("0");
				response.setResponseMessage(respMessage);
				return response;
			} else {
				respMessage.setMessage(PMCConstant.NO_RESULT);
				respMessage.setStatus("Failure");
				respMessage.setStatusCode("1");
				response.setResponseMessage(respMessage);
			}
			respMessage.setMessage("No Logged In User Found");
			respMessage.setStatus("Failure");
			respMessage.setStatusCode("1");
			response.setResponseMessage(respMessage);
		}
		return response;

	}

	@SuppressWarnings("unused")
	private String fetchData(String userId , String password){


		Thread bpmInProgress = null ,bpmCompleted= null;
		try
		{
			bpmInProgress =	new Thread(new Runnable() {public void run() {
				bpmLocal.getDataFromOdata(userId ,password,"Murphy User","ne");
			}});
			bpmInProgress.start();

			bpmCompleted =	new Thread(new Runnable() {public void run() {
				bpmLocal.getDataFromOdata(userId ,password,"Murphy User","eq");
			}});
			bpmCompleted.start();
			while(bpmInProgress.isAlive() && bpmCompleted.isAlive()){

			}

		}
		catch(Exception e)
		{
			System.err.println("[PMC][WorkboxFacade][getWorkboxFilterData][error bpm getDataFromOdata thread]"+e.getMessage());
			e.printStackTrace();
		}

		return "SUCCESS";
	}

	@Override
	public ResponseMessage claim(WorkboxRequestDto requestDto) {
		
//		PreparedStatement preparedStatement;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Task(s) Claim Failed");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		try {
			EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("workbox_pu", DatabasePropertyProvider.getConnectionProperties("oracle"));
			EntityManager emanager = emFactory.createEntityManager();
			for (InstanceDto instanceDto : requestDto.getInstanceList()) {
//				Connection con = DBConnect.getDbCon().conn;
				emanager.getTransaction().begin();
				String TEqueryString = "UPDATE TaskEventsDo SET currentProcessor = '" + requestDto.getUserId() + "', currentProcessorDisplayName = '"
						+ requestDto.getUserDisplay() + "', status = 'RESERVED' WHERE taskEventsDoPK.eventId = '"
						+ instanceDto.getInstanceId() + "'";
				
				Query TEquery = emanager.createQuery(TEqueryString);
				int update = TEquery.executeUpdate();
				
//				preparedStatement = con.prepareStatement("UPDATE TASK_EVENTS SET CUR_PROC = '" + requestDto.getUserId() + "', CUR_PROC_DISP = '"
//								+ requestDto.getUserDisplay() + "', STATUS = 'RESERVED' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "'");
//				int update = preparedStatement.executeUpdate();
				System.err.println("Update Task Events for Claim Task : " + update);

				String TOqueryString = "UPDATE TaskOwnersDo SET isProcessed = '1' WHERE taskOwnersDoPK.eventId = '"
						+ instanceDto.getInstanceId() + "' AND taskOwnersDoPK.taskOwner = '"+requestDto.getUserId()+"'";
				Query TOquery = emanager.createQuery(TOqueryString);
				
//				preparedStatement = con.prepareStatement("UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getUserId()+"'");
				update = TOquery.executeUpdate();
				emanager.getTransaction().commit();
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
	
	@Override
	public ResponseMessage release(WorkboxRequestDto requestDto) {
//		PreparedStatement preparedStatement;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Tasks(s) Release Failed");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		try {
			EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("workbox_pu", DatabasePropertyProvider.getConnectionProperties("oracle"));
			EntityManager emanager = emFactory.createEntityManager();			
			for (InstanceDto instanceDto : requestDto.getInstanceList()) {
//				Connection con = DBConnect.getDbCon().conn;
				emanager.getTransaction().begin();
				String TEqueryString = "UPDATE TASK_EVENTS SET CUR_PROC = '', CUR_PROC_DISP = '', STATUS = 'READY' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "'";
				Query TEquery = emanager.createNativeQuery(TEqueryString);
				
//				preparedStatement = con.prepareStatement("UPDATE TASK_EVENTS SET CUR_PROC = '', CUR_PROC_DISP = '', STATUS = 'READY' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "'");
				int update = TEquery.executeUpdate();
				System.err.println("Update Task Events for Release Task : " + update);
				
				String TOqueryString = "UPDATE TASK_OWNERS SET IS_PROCESSED = '0' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getUserId()+"'";
				Query TOquery = emanager.createNativeQuery(TOqueryString);

//				preparedStatement = con.prepareStatement("UPDATE TASK_OWNERS SET IS_PROCESSED = '0' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getUserId()+"'");
				update = TOquery.executeUpdate();
				emanager.getTransaction().commit();
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
	@Override
	public ResponseMessage complete(WorkboxRequestDto requestDto) {
		
		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("workbox_pu", DatabasePropertyProvider.getConnectionProperties("oracle"));
		EntityManager emanager = emFactory.createEntityManager();
		
		String loggedin_user = UserManagementUtil.getLoggedInUser().getName();
		String loggedin_user_name = UserManagementUtil.getLoggedInUser().getName();
		
//		PreparedStatement preparedStatement;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Tasks(s) Claim Failed");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		DateFormat df = new SimpleDateFormat(PMCConstant.DETAILDATE_AMPM_FORMATE);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			for (InstanceDto instanceDto : requestDto.getInstanceList()) {
//				Connection con = DBConnect.getDbCon().conn;
				WorkboxRequestDto wbRequestDto = new WorkboxRequestDto();
				List<InstanceDto> instances = new ArrayList<InstanceDto>();
				instances.add((instanceDto));
				wbRequestDto.setInstanceList(instances);
				wbRequestDto.setUserId(loggedin_user);
				wbRequestDto.setUserDisplay(loggedin_user_name);
				
				emanager.getTransaction().begin();
				
				if(this.claim(requestDto).getStatus().equals("SUCCESS")) {
					String TEqueryString = "UPDATE TASK_EVENTS SET STATUS = 'COMPLETED', COMPLETED_AT = '"+df.format(new Date())+"' WHERE EVENT_ID = '"
							+ instanceDto.getInstanceId() + "'";
					Query TEquery = emanager.createNativeQuery(TEqueryString);
					
//					preparedStatement = con.prepareStatement("UPDATE TASK_EVENTS SET STATUS = 'COMPLETED', COMPLETED_AT = '"+df.format(new Date())+"' WHERE EVENT_ID = '"
//									+ instanceDto.getInstanceId() + "'");
					int update = TEquery.executeUpdate();
					System.err.println("Update Task Events for Complete Task : " + update);
					
					String TOqueryString = "UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
							+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getUserId()+"'";
					Query TOquery = emanager.createNativeQuery(TOqueryString);

//					preparedStatement = con.prepareStatement("UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
//									+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getForwardTo()+"'");
					update = TOquery.executeUpdate();
					emanager.getTransaction().commit();
					System.err.println("Update Task Owners for Complete Task : " + update);
					responseMessage.setMessage("Task(s) Completed");
					responseMessage.setStatus("SUCCESS");
					responseMessage.setStatusCode("0");
				}
			}
		} catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
			responseMessage.setMessage("Complete Failed with Exception : "+e.getMessage());
			responseMessage.setStatus("FAILURE");
			responseMessage.setStatusCode("1");
		}
		return responseMessage;
	}
	
	@Override
	public ResponseMessage forward(WorkboxRequestDto requestDto) {
		
		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("workbox_pu", DatabasePropertyProvider.getConnectionProperties("oracle"));
		EntityManager emanager = emFactory.createEntityManager();
		
		String loggedin_user = UserManagementUtil.getLoggedInUser().getName();
//		String loggedin_user_name = UserManagementUtil.getLoggedInUser().getName();
		
//		PreparedStatement preparedStatement;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Tasks(s) Forward Failed");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		DateFormat df = new SimpleDateFormat(PMCConstant.DETAILDATE_AMPM_FORMATE);
		try {
			for (InstanceDto instanceDto : requestDto.getInstanceList()) {
//				Connection con = DBConnect.getDbCon().conn;
				
				emanager.getTransaction().begin();
				
				String TEqueryString = "UPDATE TASK_EVENTS SET CUR_PROC = '"+requestDto.getForwardTo()+"', CUR_PROC_DISP = '"+requestDto.getUserDisplay()+"', FORWARDED_BY = '"+loggedin_user+"', STATUS = 'RESERVED', FORWARDED_AT = '"+df.format(new Date())+"' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "'";
				Query TEquery = emanager.createNativeQuery(TEqueryString);
				
//				preparedStatement = con.prepareStatement("UPDATE TASK_EVENTS SET CUR_PROC = '"+requestDto.getForwardTo()+"', CUR_PROC_DISP = '"+requestDto.getForwardTo()+"', FORWARDED_BY = '"+loggedin_user+"', STATUS = 'RESERVED', FORWARDED_AT = '"+df.format(new Date())+"' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "'");
				int update = TEquery.executeUpdate();
				System.err.println("Update Task Events for Forward Task : " + update);
				
				String TOqueryString = "UPDATE TASK_OWNERS SET IS_PROCESSED = '1', TASK_OWNER = '"+requestDto.getForwardTo()+"', TASK_OWNER_DISP = '"+requestDto.getUserDisplay()+"' WHERE EVENT_ID = '"
						+ instanceDto.getInstanceId() + "' ";
				Query TOquery = emanager.createNativeQuery(TOqueryString);

//				preparedStatement = con.prepareStatement("UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"
//								+ instanceDto.getInstanceId() + "' AND TASK_OWNER = '"+requestDto.getForwardTo()+"'");
				update = TOquery.executeUpdate();
				System.err.println("Update Task Owners for Forward Task : " + update);
				emanager.getTransaction().commit();
				
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

}
