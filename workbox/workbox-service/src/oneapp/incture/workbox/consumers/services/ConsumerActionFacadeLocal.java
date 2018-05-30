package oneapp.incture.workbox.consumers.services;

import javax.ejb.Local;

import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;

@Local
public interface ConsumerActionFacadeLocal {

	ResponseMessage executeAction(WorkboxRequestDto requestDto);
	
}
