package oneapp.incture.workbox.pmc.services;

import javax.ejb.Local;

import oneapp.incture.workbox.pmc.dto.ProcessAgeingResponse;
import oneapp.incture.workbox.pmc.dto.ProcessDetailsDto;
import oneapp.incture.workbox.pmc.dto.ProcessDetailsResponse;
import oneapp.incture.workbox.pmc.dto.UserProcessDetailRequestDto;
import oneapp.incture.workbox.pmc.dto.responses.UserDetailsResponse;
import oneapp.incture.workbox.poadapter.dto.ProcessEventsDto;

@Local
public interface ProcessFacadeLocal {

	// List<AgingGraphDto> getProcessAgeingGraph(String graphTrendType);
	//
	// AgingResponseDto getProcessAgingTable(String ageingType);

	ProcessDetailsResponse getProcessesByDuration(ProcessDetailsDto processDetailsDto);

	ProcessEventsDto getProcessDetailsByInstance(String processId);

	ProcessDetailsResponse getProcessesByTaskOwner(UserProcessDetailRequestDto request);

	ProcessAgeingResponse getProcessAgeing(String ageingType, String processName);

	UserDetailsResponse getCreatedByList(String inputValue);

	// byte[] generateExcelByDuration(ProcessDetailsDto processDetailsDto);

}
