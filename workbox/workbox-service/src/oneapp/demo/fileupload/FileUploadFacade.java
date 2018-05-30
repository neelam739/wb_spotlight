package oneapp.demo.fileupload;

import java.sql.SQLException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.rowset.serial.SerialException;

import oneapp.incture.workbox.pmc.services.EntityManagerProviderLocal;


/**
 * Session Bean implementation class WorkboxFacade
 */
@Stateless
public class FileUploadFacade implements FileUploadFacadeLocal {

	@EJB
	EntityManagerProviderLocal em;

	@Override
	public void uploadFile(FileDto fileDto) {
		FileDao fileDao = null;
		try {
			fileDao = new FileDao(em.getEntityManager());
			fileDao.uploadFile(fileDto);
		} catch (SerialException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
