package oneapp.incture.workbox.pmc.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import oneapp.incture.workbox.pmc.dto.SlaListDto;
import oneapp.incture.workbox.pmc.dto.responses.SlaProcessNamesResponse;
import oneapp.incture.workbox.pmc.wsdlconsumers.UMEManagementEngineConsumer;
import oneapp.incture.workbox.poadapter.dao.SlaManagementDao;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.util.UserManagementUtil;

/**
 * Session Bean implementation class ConfigurationFacade
 */
@Stateless
public class SlaManagementFacade implements SlaManagementFacadeLocal {

	@EJB
	EntityManagerProviderLocal em;
	

	/*@EJB
	UserManagementFacadeWsdlConsumerLocal ume;*/
	
	UMEManagementEngineConsumer umeConsumer = null;

	@Override
	public SlaProcessNamesResponse getAllProcessNames() {
		SlaProcessNamesResponse response = new SlaProcessNamesResponse();
		ResponseMessage responseMessage = new ResponseMessage();
		umeConsumer = new UMEManagementEngineConsumer();
//		response.setSlaProcessNames(new SlaManagementDao(em.getEntityManager())
//		response.setSlaProcessNames(new SlaManagementDao(em.getEntityManager()).getSlaProcessList(umeConsumer.getLoggedInUser().getUserId()));
		response.setSlaProcessNames(new SlaManagementDao(em.getEntityManager()).getSlaProcessList(UserManagementUtil.getLoggedInUser().getName()));
		responseMessage.setMessage("Sla Processes List Fetched Successfully");
		responseMessage.setStatus("SUCCESS");
		responseMessage.setStatusCode("1");
		response.setResponseMessage(responseMessage);
		return response;
	}

	@Override
	public SlaListDto getSlaDetails(String processName) {
		SlaManagementDao slaManagementDao = new SlaManagementDao(em.getEntityManager());
		SlaListDto  slaList = slaManagementDao.getDetailSla(processName);
		return slaList;
	}

	@Override
	public ResponseMessage updateSla(SlaListDto dto) {
		return new SlaManagementDao(em.getEntityManager()).updateSla(dto);
	}
	
}
