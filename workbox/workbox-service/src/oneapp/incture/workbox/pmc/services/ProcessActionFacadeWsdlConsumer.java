package oneapp.incture.workbox.pmc.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import oneapp.incture.workbox.dbproviders.DatabasePropertyProvider;
import oneapp.incture.workbox.pmc.dto.ProcessActionDto;
import oneapp.incture.workbox.pmc.wsdlconsumers.ProcessActionConsumer;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.util.ServicesUtil;

/**
 * Session Bean implementation class UserManagementFacadeWsdlConsumer
 */
@Stateless
public class ProcessActionFacadeWsdlConsumer implements ProcessActionFacadeWsdlConsumerLocal {

	public ProcessActionFacadeWsdlConsumer() {
	}

	/*@WebServiceRef(name = "ProcessActionFacadeService")
	private ProcessActionFacadeService actionServices;*/
	
	ProcessActionConsumer processActionConsumer = null;

	@Override
	public ResponseMessage cancelProcess(ProcessActionDto processList) {
		System.err.println("[PMC][services][processAction][cancelProcess] method invoked with input" + processList);
		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("workbox_pu", DatabasePropertyProvider.getConnectionProperties("oracle"));
		EntityManager emanager = emFactory.createEntityManager();
		emanager.getTransaction().begin();
		ResponseMessage responseDto = new ResponseMessage();
		processActionConsumer = new ProcessActionConsumer();
		List<String> processInstanceList = processList.getProcessInstanceList(); 
		if(!ServicesUtil.isEmpty(processInstanceList)){
			for(String processInstance : processInstanceList){
				System.err.println("[PMC][services][processAction][cancelProcess] in loop  with instance " + processInstance);
				if (!ServicesUtil.isEmpty(processInstance)) {
					try {
						System.err.println("[PMC][services][processAction][cancelProcess][taskInstanceId] " + processInstance);
//						responseDto.setMessage(processActionConsumer.cancelProcess(processInstance));
						String query = "UPDATE PROCESS_EVENTS SET STATUS = 'CANCELLED' WHERE PROCESS_ID = '"+processInstance+"'";
						Query nativeQuery = emanager.createNativeQuery(query);
						int update = nativeQuery.executeUpdate();
						if(update == 1) {
							responseDto.setMessage("SUCCESS");
						}
					} catch (Exception e) {
						responseDto.setMessage("Process cancellation Failed because" + e.getMessage());
					}
				} else {
					responseDto.setMessage("Instance Id required to cancel");
					break;
				}
			} 
		}
		else {
			responseDto.setMessage(" No Instance Id sent to to cancel");
		}
		if(responseDto.getMessage().equals("SUCCESS")){
			responseDto.setMessage("Process Cancelled Successfully");
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");	
		}else{
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		emanager.getTransaction().commit();
		return responseDto;
	}


}
