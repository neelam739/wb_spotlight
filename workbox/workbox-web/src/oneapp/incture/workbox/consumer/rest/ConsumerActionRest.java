package oneapp.incture.workbox.consumer.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.inbox.services.WorkboxActionFacade;
import oneapp.incture.workbox.inbox.services.WorkboxActionSLFacade;
import oneapp.incture.workbox.inbox.services.WorkboxActionSLFacadeLocal;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;

@Path("/consumer")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class ConsumerActionRest {

//	@EJB
//	private WorkboxActionFacadeLocal actionLocal;



	@POST
	@Path("/action")
	public ResponseMessage action(WorkboxRequestDto requestDto) {
//		WorkboxActionFacade actionFacade = new WorkboxActionFacade();
		
		WorkboxActionSLFacadeLocal slFacade = new WorkboxActionSLFacade();
		
		if(requestDto.getActionType().equalsIgnoreCase("claim")) {
			return slFacade.claim(requestDto);
		} else if(requestDto.getActionType().equalsIgnoreCase("release")) {
			return slFacade.release(requestDto);
		} else if(requestDto.getActionType().equalsIgnoreCase("forward")) {
			return slFacade.forward(requestDto);
		} else if(requestDto.getActionType().equalsIgnoreCase("decision")) {
			return slFacade.complete(requestDto);
		} else {
			return null;
		}
		
//		return actionFacade.executeAction(requestDto);
	}

}
