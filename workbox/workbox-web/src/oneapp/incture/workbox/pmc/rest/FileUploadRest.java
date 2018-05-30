package oneapp.incture.workbox.pmc.rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import oneapp.demo.fileupload.FileDto;
import oneapp.demo.fileupload.FileUploadFacadeLocal;

@Path("/file")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class FileUploadRest {

	@EJB
	private FileUploadFacadeLocal fileUploadFacadeLocal;
	
	@Path("/upload")
	@POST
	public void uploadFile(FileDto dto) {
		fileUploadFacadeLocal.uploadFile(dto);
	}
}
