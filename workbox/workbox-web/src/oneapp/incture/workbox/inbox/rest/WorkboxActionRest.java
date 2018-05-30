package oneapp.incture.workbox.inbox.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import oneapp.incture.workbox.consumer.rest.ConsumerActionRest;
import oneapp.incture.workbox.consumers.dto.InstanceDto;
import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.inbox.dto.RequestIdListDto;
import oneapp.incture.workbox.inbox.dto.WorkBoxActionDto;
import oneapp.incture.workbox.inbox.dto.WorkBoxActionListDto;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.util.ServicesUtil;

@Path("/workboxAction")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class WorkboxActionRest {

	/*@EJB
	private WorkBoxActionFacadeWsdlConsumerLocal workboxAction;*/
//	WorkBoxActionsConsumer wbActionConsumer = null;
	
	ConsumerActionRest consumerAction = new ConsumerActionRest();
	
	private static List<InstanceDto> convertInstaceList(List<RequestIdListDto> reqIdListDto) {
		List<InstanceDto> list = new ArrayList<InstanceDto>();
		for(RequestIdListDto inst : reqIdListDto) {
			InstanceDto instanceDto = new InstanceDto();
			instanceDto.setInstanceId(inst.getEventId());
			instanceDto.setRequestId(inst.getRequestId());
			list.add(instanceDto);
		}
		return list;
	}
	
	@POST
	@Path("/claim")
	 public ResponseMessage claimTask(WorkBoxActionDto dto) {
//		wbActionConsumer = new WorkBoxActionsConsumer();
		WorkboxRequestDto requestDto = new WorkboxRequestDto();
		requestDto.setActionType("claim");
		requestDto.setUserId(dto.getUserId());
		requestDto.setUserDisplay(dto.getUserDisplayName());
		if(!ServicesUtil.isEmpty(dto.getTaskInstanceId())) {
			RequestIdListDto req = new RequestIdListDto();
			req.setEventId(dto.getTaskInstanceId());
			dto.getInstanceList().add(req);
			requestDto.setInstanceList(WorkboxActionRest.convertInstaceList(dto.getInstanceList()));
		} else {
			requestDto.setInstanceList(WorkboxActionRest.convertInstaceList(dto.getInstanceList()));
		}
		System.err.println("[PMC][WorkBoxAction][Rest][claim] method invoked");
    	return consumerAction.action(requestDto);
	}
	
	@POST
	@Path("/release")
    public ResponseMessage release(WorkBoxActionDto dto) {
//		wbActionConsumer = new WorkBoxActionsConsumer();
		System.err.println("[PMC][WorkBoxAction][Rest][release] method invoked");
		WorkboxRequestDto requestDto = new WorkboxRequestDto();
		requestDto.setActionType("release");
		requestDto.setUserId(dto.getUserId());
		requestDto.setUserDisplay(dto.getUserDisplayName());
		if(!ServicesUtil.isEmpty(dto.getTaskInstanceId())) {
			RequestIdListDto req = new RequestIdListDto();
			req.setEventId(dto.getTaskInstanceId());
			dto.getInstanceList().add(req);
			requestDto.setInstanceList(WorkboxActionRest.convertInstaceList(dto.getInstanceList()));
		} else {
			requestDto.setInstanceList(WorkboxActionRest.convertInstaceList(dto.getInstanceList()));
		}
		System.err.println("[PMC][WorkBoxAction][Rest][claim] method invoked");
    	return consumerAction.action(requestDto);
	}

	@POST
	@Path("/delegate")
    public ResponseMessage delegate(WorkBoxActionDto dto) {
//		wbActionConsumer = new WorkBoxActionsConsumer();
		System.err.println("[PMC][WorkBoxAction][Rest][delegate] method invoked");
		WorkboxRequestDto requestDto = new WorkboxRequestDto();
		requestDto.setActionType("forward");
		requestDto.setUserId(dto.getUserId());
		requestDto.setForwardTo(dto.getUserId());
		requestDto.setUserDisplay(dto.getUserDisplayName());
		requestDto.setInstanceList(WorkboxActionRest.convertInstaceList(dto.getInstanceList()));
		System.err.println("[PMC][WorkBoxAction][Rest][claim] method invoked");
    	return consumerAction.action(requestDto);
	}
	@POST
	@Path("/nominate")
    public ResponseMessage nominate(WorkBoxActionDto dto) {
//		wbActionConsumer = new WorkBoxActionsConsumer();
		System.err.println("[PMC][WorkBoxAction][Rest][nominate] method invoked");
		WorkboxRequestDto requestDto = new WorkboxRequestDto();
		requestDto.setActionType("forward");
		requestDto.setUserId(dto.getUserId());
		requestDto.setForwardTo(dto.getUserId());
		requestDto.setUserDisplay(dto.getUserDisplayName());
		requestDto.setInstanceList(WorkboxActionRest.convertInstaceList(dto.getInstanceList()));
		System.err.println("[PMC][WorkBoxAction][Rest][claim] method invoked");
    	return consumerAction.action(requestDto);
	}

	@POST
	@Path("/addNote")
    public ResponseMessage addNote(WorkBoxActionDto dto) {
//		wbActionConsumer = new WorkBoxActionsConsumer();
		System.err.println("[PMC][WorkBoxAction][Rest][addNote] method invoked");
		WorkboxRequestDto requestDto = new WorkboxRequestDto();
		requestDto.setActionType("addnote");
		requestDto.setUserId(dto.getUserId());
		requestDto.setUserDisplay(dto.getUserDisplayName());
		requestDto.setInstanceList(WorkboxActionRest.convertInstaceList(dto.getInstanceList()));
		System.err.println("[PMC][WorkBoxAction][Rest][claim] method invoked");
    	return consumerAction.action(requestDto);
	}
	
	@POST
	@Path("/complete")
    public ResponseMessage complete(WorkBoxActionListDto dto) {
//		wbActionConsumer = new WorkBoxActionsConsumer();
		System.err.println("[PMC][WorkBoxAction][Rest][complete] method invoked");
		WorkboxRequestDto requestDto = new WorkboxRequestDto();
		List<InstanceDto> list = new ArrayList<InstanceDto>();
		for(WorkBoxActionDto actionDto : dto.getTaskInstanceList()) {
			list.addAll(WorkboxActionRest.convertInstaceList(actionDto.getInstanceList()));
		}
		System.err.println("[PMC][WorkBoxAction][Rest][claim] method invoked");
		requestDto.setActionType("decision");
		requestDto.setInstanceList(list);
		return consumerAction.action(requestDto);
	}

	@POST
	@Path("/claimDelegate")
    public ResponseMessage claimAndDelegate(WorkBoxActionDto dto) {
//		wbActionConsumer = new WorkBoxActionsConsumer();
		System.err.println("[PMC][WorkBoxAction][Rest][claimDelegate] method invoked");
//    	return wbActionConsumer.claimAndDelegate(dto);
		return null;
	}
}
