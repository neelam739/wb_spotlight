package oneapp.incture.workbox.inbox.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.consumers.services.ConsumerActionFacadeLocal;
import oneapp.incture.workbox.pmc.services.EntityManagerProviderLocal;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;


/**
 * Session Bean implementation class WorkboxFacade
 */
@Stateless
public class WorkboxActionFacade implements WorkboxActionFacadeLocal {


	@EJB
	EntityManagerProviderLocal em;

	@EJB
	ConsumerActionFacadeLocal odataActionServices;
	
//	@EJB
//	WorkboxActionSLFacadeLocal slFacade;

	public WorkboxActionFacade() {
	}

	@Override
	public ResponseMessage executeAction(WorkboxRequestDto requestDto) {
		
		WorkboxActionSLFacade slFacade = new WorkboxActionSLFacade();
		
//		WorkBoxActionClass workBoxActionClass = new WorkBoxActionClass();
		if(requestDto.getActionType().equalsIgnoreCase("claim")) {
			return slFacade.claim(requestDto);
		} else if(requestDto.getActionType().equalsIgnoreCase("release")) {
			return slFacade.release(requestDto);
		} else if(requestDto.getActionType().equalsIgnoreCase("forward")) {
			return slFacade.forward(requestDto);
		} else if(requestDto.getActionType().equalsIgnoreCase("decision")) {
			return slFacade.complete(requestDto);
		}
		

		/*if(requestDto.getOrigin().equals("BPM") || requestDto.getOrigin().equals("ECC")){
			return	odataActionServices.executeAction(requestDto);
		}else if(requestDto.getOrigin().equals("CUSTOM")){
			if(requestDto.getActionType().equals("Claim")){
//				return	taskMgmtServices.claim(requestDto);
			}else if(requestDto.getActionType().equals("Release")){
//				return	taskMgmtServices.release(requestDto);
			}else if(requestDto.getActionType().equals("Forward")){
//				return taskMgmtServices.nominate(requestDto);
			}

		}else if(requestDto.getOrigin().equals("BPM_EXISTS")){

		}*/

		return new ResponseMessage();
	}
}
