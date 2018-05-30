package oneapp.incture.workbox.pmc.services;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import oneapp.incture.workbox.pmc.dao.CollaborationDao;
import oneapp.incture.workbox.pmc.dto.CollaborationDto;
import oneapp.incture.workbox.pmc.dto.CollaborationMessagesDto;
import oneapp.incture.workbox.pmc.dto.CollaborationNotificationDto;
import oneapp.incture.workbox.pmc.dto.responses.CollaborationResponseDto;
import oneapp.incture.workbox.pmc.dto.responses.NotificationResponseDto;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;
import oneapp.incture.workbox.util.User;
import oneapp.incture.workbox.util.UserManagementUtil;

@Stateless
public class CollaborationFacade implements CollaborationFacadeLocal {

	@EJB
	EntityManagerProviderLocal em;

	/*@WebServiceRef(name = "UMEUserManagementFacadeService")
	private UMEUserManagementFacadeService userServices;*/

	public CollaborationFacade() {
	}
	
	CollaborationResponseDto collaborationMessageDto;
	ResponseMessage responseDto;

	public ResponseMessage createCollaboration(CollaborationDto dto) {
		
		responseDto = new ResponseMessage();
//		UMEManagementEngineConsumer umeConsumer = new UMEManagementEngineConsumer();
//		UserDetailsDto getLoggedInUser = umeConsumer.getLoggedInUser();
		User getLoggedInUser = UserManagementUtil.getLoggedInUser();
		dto.setCreatedAt(new Date());
		dto.setUserId(getLoggedInUser.getName());
//		dto.setUserDisplayName(getLoggedInUser.getName());
		
		if (!ServicesUtil.isEmpty(dto.getMessage()) && !ServicesUtil.isEmpty(dto.getProcessId())) {
			if (new CollaborationDao(em.getEntityManager()).createCollaborationDetail(dto).equals("SUCCESS")) {
				responseDto.setMessage("Created Successfully");
				responseDto.setStatus("SUCCESS");
				responseDto.setStatusCode("0");
				return responseDto;
			} 
		} else {
			responseDto.setMessage("Mandatory Fields are Empty");
		}
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		return responseDto;
	}

	public CollaborationResponseDto getMessageDetails( String processId, String taskId) {
		collaborationMessageDto = new CollaborationResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try{
			List<CollaborationMessagesDto>	dto = new CollaborationDao(em.getEntityManager()).getMessageDetails(processId,taskId);
			if (!ServicesUtil.isEmpty(dto)) {
				collaborationMessageDto.setResponseDtos(dto);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		}
		catch(Exception e){
			System.err.println("[PMC][CollaborationFacade][getMessageDetails][error]"+e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		collaborationMessageDto.setResponseMessage(responseDto);
		return collaborationMessageDto;
	}

	public CollaborationResponseDto getProcessLevelWithTaskLevelMessage(String processId) {
		collaborationMessageDto = new CollaborationResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try{
			List<CollaborationMessagesDto>	dto = new CollaborationDao(em.getEntityManager()).getAllDetailsOfProcessWithTask(processId);
			if (!ServicesUtil.isEmpty(dto)) {
				collaborationMessageDto.setResponseDtos(dto);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		}
		catch(Exception e){
			System.err.println("[PMC][CollaborationFacade][getProcessLevelWithTaskLevelMessage][error]"+e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		collaborationMessageDto.setResponseMessage(responseDto);
		return collaborationMessageDto;
	}

	public CollaborationResponseDto getMessageUsingOwnerId() {
		collaborationMessageDto = new CollaborationResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try{
//			UMEManagementEngineConsumer umeConsumer = new UMEManagementEngineConsumer();
//			List<CollaborationMessagesDto>	dto = new CollaborationDao(em.getEntityManager()).getMessageOfOwner(umeConsumer.getLoggedInUser().getUserId());
			List<CollaborationMessagesDto>	dto = new CollaborationDao(em.getEntityManager()).getMessageOfOwner(UserManagementUtil.getLoggedInUser().getName());
			if (!ServicesUtil.isEmpty(dto)) {
				collaborationMessageDto.setResponseDtos(dto);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		}
		catch(Exception e){
			System.err.println("[PMC][CollaborationFacade][getMessageUsingOwnerId][error]"+e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		collaborationMessageDto.setResponseMessage(responseDto);
		return collaborationMessageDto;
	}

	public NotificationResponseDto getNotification() {
		NotificationResponseDto dto = new NotificationResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try{
//			UMEManagementEngineConsumer umeConsumer = new UMEManagementEngineConsumer();
//			List<CollaborationNotificationDto> dtos = new CollaborationDao(em.getEntityManager()).getNotificationForOwner(umeConsumer.getLoggedInUser().getUserId());
			List<CollaborationNotificationDto> dtos = new CollaborationDao(em.getEntityManager()).getNotificationForOwner(UserManagementUtil.getLoggedInUser().getName());
			if (!ServicesUtil.isEmpty(dtos)) {
				dto.setResponseDto(dtos);
				responseDto.setMessage("Notifications fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		}
		catch(Exception e){
			System.err.println("[PMC][CollaborationFacade][getNotification][error]"+e.getMessage());
			responseDto.setMessage("Fetching notification failed due to " + e.getMessage());
		}
		dto.setResponseMessage(responseDto);
		return dto;

	}
	
	@Override
	public String getBasicString(String userId) {
		return (new CollaborationDao(em.getEntityManager()).getBasicString(userId));
	}
}
