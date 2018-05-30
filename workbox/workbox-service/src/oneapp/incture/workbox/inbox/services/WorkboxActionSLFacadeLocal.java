package oneapp.incture.workbox.inbox.services;

import javax.ejb.Local;

import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;


@Local
public interface WorkboxActionSLFacadeLocal {

	ResponseMessage complete(WorkboxRequestDto requestDto);

	ResponseMessage forward(WorkboxRequestDto requestDto);

	ResponseMessage release(WorkboxRequestDto requestDto);

	ResponseMessage claim(WorkboxRequestDto requestDto);
}
