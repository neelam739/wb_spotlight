package oneapp.incture.workbox.consumers.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.ejb.Stateless;

import com.sap.cloud.account.TenantContext;

import oneapp.incture.workbox.consumers.dto.ActionsRequestDto;
import oneapp.incture.workbox.consumers.dto.InstanceDto;
import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.consumers.util.ConnectionsUtil;
import oneapp.incture.workbox.consumers.util.ResponseDto;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;

/**
 * Session Bean implementation class ConsumeODataFacade
 */
@Stateless
public class ConsumerActionFacade implements ConsumerActionFacadeLocal {

	@Resource
	private TenantContext  tenantContext;

	public ConsumerActionFacade() {
	}

	@Override
	public ResponseMessage executeAction(WorkboxRequestDto requestDto){

		System.err.println("[PMC][ConsumerActionFacade][actions][executeAction][init] " +requestDto);
		ResponseMessage returnMessage = new ResponseMessage();
		boolean isSuccess = true;
		StringBuffer successfulTasks = new StringBuffer("");

		if( requestDto.getActionType().equals("AddComment")){
			returnMessage.setMessage("Failed to Add Comment");	
		}else{
			returnMessage.setMessage("Failed to "+ requestDto.getActionType());
		}
		returnMessage.setStatus("FAILURE");
		returnMessage.setStatusCode("1");

		if(!ServicesUtil.isEmpty(requestDto.getUserId()) && !ServicesUtil.isEmpty(requestDto.getScode()))
		{
			if(!ServicesUtil.isEmpty(requestDto.getInstanceList()) && !ServicesUtil.isEmpty(requestDto.getActionType())){

				String	serviceUri = getBaseUrl(requestDto.getOrigin())+requestDto.getActionType();
				try {
					if(!ServicesUtil.isEmpty(requestDto.getDecisionKey())){
						serviceUri=serviceUri+ checkIfKeyAlreadyExists(serviceUri)	+"DecisionKey='"+requestDto.getDecisionKey()+"'";
					}
					if(!ServicesUtil.isEmpty(requestDto.getForwardTo())){
						serviceUri=serviceUri+	 checkIfKeyAlreadyExists(serviceUri)+"ForwardTo='"+requestDto.getForwardTo()+"'";
					}
					if(!ServicesUtil.isEmpty(requestDto.getText())){
						serviceUri=serviceUri+	 checkIfKeyAlreadyExists(serviceUri)+"Text='"+ URLEncoder.encode(requestDto.getText() , "UTF-8")+"'";
					}

					if(!ServicesUtil.isEmpty(requestDto.getActions())){
						serviceUri = serviceUri+ checkIfKeyAlreadyExists(serviceUri)+"sap-client=260&Comments='";
						if(!ServicesUtil.isEmpty(requestDto.getComments())){
							serviceUri = serviceUri+ URLEncoder.encode(requestDto.getComments() , "UTF-8"); 
						}
						serviceUri = serviceUri+"%20*AllItems*%20";
						for(ActionsRequestDto dto : requestDto.getActions()){
							serviceUri = serviceUri+"ItemNo%20eq%20"+dto.getItemNo()+"%20and%20Accept/Reject%20eq%20"; 
							if(dto.getAction().equals("APPROVE")){
								serviceUri = serviceUri+"TRUE";	
							}
							else{
								serviceUri = serviceUri+"FALSE";
							}
							serviceUri = serviceUri+",";
						}
						serviceUri = serviceUri.substring(0,serviceUri.length()-1);
						serviceUri = serviceUri+"'";
					}
					if(!ServicesUtil.isEmpty(requestDto.getComments()) && ServicesUtil.isEmpty(requestDto.getActions())){
						serviceUri=serviceUri+	 checkIfKeyAlreadyExists(serviceUri)+"Comments='"+URLEncoder.encode(requestDto.getComments() , "UTF-8")+"'";
					}

				} catch (UnsupportedEncodingException e1) {
					System.err.println("[PMC][ODataServicesUtil][actions][executeAction][error][URLEncoder] " +e1.getMessage());
					returnMessage.setMessage("URL Encoding Failed");
					return returnMessage;
				}
				System.err.println("[PMC][ODataServicesUtil][Xpath][actions][username]"+requestDto.getUserId()+"[serviceUri] "+serviceUri);

				try {
					String auth = ServicesUtil.getBasicAuth(requestDto.getUserId(),requestDto.getScode());

					//	String csrfToken = ConnectionsUtil.getCsrfToken( getBaseUrl(requestDto.getOrigin()),requestDto.getUserId(),requestDto.getScode());
					for(InstanceDto instanceDto : requestDto.getInstanceList()){
						String url = serviceUri +  checkIfKeyAlreadyExists(serviceUri)+"SAP__Origin='"+instanceDto.getSapOrigin()+"'&InstanceID='"+instanceDto.getInstanceId()+"'";
						System.err.println("[PMC][ConsumerActionFacade][executeAction][url]" + url);
						ResponseDto status = ConnectionsUtil.executeActionHttp(url,auth,PMCConstant.BPM_LOCATION,tenantContext.getTenant().getAccount().getId(),"");
						if (!status.getStatus().equals("SUCCESS")) {
							isSuccess = false;
							break;
						} else {
							successfulTasks = successfulTasks.append((ServicesUtil.isEmpty(instanceDto.getRequestId()) ? "":instanceDto.getRequestId() + ","));
						}
					}

					if (isSuccess) {
						returnMessage.setMessage("Task(s) "+requestDto.getActionType()+" successful");
						returnMessage.setStatus("SUCCESS");
						returnMessage.setStatusCode("0");
						return returnMessage;
					} else {
						if (ServicesUtil.isEmpty(returnMessage.getMessage())) {
							if (!ServicesUtil.isEmpty(successfulTasks.toString())) {
								returnMessage.setMessage("Task(s) with RequestId "
										+ successfulTasks.toString().substring(0, successfulTasks.length() - 1)
										+ "has been "+requestDto.getActionType()+"ed successfully . While the remaining tasks failed");
							} else {
								returnMessage.setMessage("Task(s) "+requestDto.getActionType()+" failed");
							}
						}
					}
				} catch (Exception e) {
					returnMessage.setMessage(e.getMessage());
					e.printStackTrace();
				}
			}
			else{

				returnMessage.setMessage("BAD REQUEST");
				System.err.println("[PMC][ODataServicesUtil][actions][executeAction][exit] " +returnMessage);
				return returnMessage;
			}
		}
		else {
			returnMessage.setMessage("AUTHORISATION FAILED");
			System.err.println("[PMC][ODataServicesUtil][actions][executeAction][exit] " +returnMessage);
			return returnMessage;
		}

		System.err.println("[PMC][ODataServicesUtil][actions][executeAction][exit] " +returnMessage);
		return returnMessage;
	}


	private String getBaseUrl(String origin){
		if(origin.equals("BPM")){
			return PMCConstant.BPM_URL;
		}
		else if(origin.equals("ECC")){
			return PMCConstant.ECC_URL;
		}
		else if(origin.equals("WF")){
			return PMCConstant.WF_URL;
		}
		return null;
	}


	/*@Override
	public ResponseMessage bulkAction(WorkboxRequestDto dto) {
		ResponseMessage responseDto = new ResponseMessage();
		StringBuffer successfulTasks = new StringBuffer("");
		boolean isSuccess = true;
		if (!ServicesUtil.isEmpty(dto.getInstanceList())) {
			try {
				for(InstanceDto instanceDto : dto.getInstanceList()){
					dto.setInstanceId(instanceDto.getInstanceId());
					dto.setSapOrigin(instanceDto.getSapOrigin());
					ResponseDto status = this.executeAction(dto);
					if (!status.getStatus().equals("SUCCESS")) {
						isSuccess = false;
						break;
					} else {
						successfulTasks = successfulTasks.append((ServicesUtil.isEmpty(instanceDto.getRequestId()) ? "":instanceDto.getRequestId() + ","));
					}
				}
			} catch (Exception e) {
				responseDto.setMessage("Task(s) "+dto.getActionType()+" Failed");
			}
			if (isSuccess) {
				responseDto.setMessage("Task(s) "+dto.getActionType()+" successfull");
				responseDto.setStatus("SUCCESS");
				responseDto.setStatusCode("0");
				return responseDto;
			} else {
				if (ServicesUtil.isEmpty(responseDto.getMessage())) {
					if (!ServicesUtil.isEmpty(successfulTasks.toString())) {
						responseDto.setMessage("Task(s) with RequestId "
								+ successfulTasks.toString().substring(0, successfulTasks.length() - 1)
								+ "has been "+dto.getActionType()+"ed successfully . While the remaining tasks failed");
					} else {
						responseDto.setMessage("Task(s) "+dto.getActionType()+" failed");
					}
				}
			}
		} else {
			responseDto.setMessage("Instance Id required to "+dto.getActionType());
		}
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");

		return responseDto;
	} 
	 */

	private String checkIfKeyAlreadyExists(String baseUrl){
		if(baseUrl.contains("?")){
			return "&";
		}
		else{
			return "?";
		}

	}


	private String getPayload (String taskId,String requestor,String requestId,String requestType,String country,String region,String storageLoc,String approverRole,String isApproved,String rejectionReason){

		String st = "{\"Taskid\":\""+taskId+"\",\"Body\":\"{\"DO_Sloc\":{\"Requestor\":\""+requestor+"\",\"RequestId\":\""+requestId+"\",\"RequestType\":\""+requestType+"\",\"Country\":\""+country+"\",\"Region\":\""+region+"\",\"StorageLocation\":\""+storageLoc+"\",\"ApproverRole\":\""+approverRole+"\",\"IsApproved\":"+isApproved+",\"RejectionReason\":\""+rejectionReason+"\"}}\"}";
		return st;
	}
}
