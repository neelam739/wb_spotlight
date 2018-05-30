package oneapp.demo.fileupload;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import oneapp.incture.workbox.poadapter.dao.BaseDao;
import oneapp.incture.workbox.poadapter.dto.BaseDto;
import oneapp.incture.workbox.poadapter.entity.BaseDo;
import oneapp.incture.workbox.util.ExecutionFault;
import oneapp.incture.workbox.util.InvalidInputFault;
import oneapp.incture.workbox.util.NoResultFault;
import oneapp.incture.workbox.util.ServicesUtil;

public class FileDao extends BaseDao<BaseDo, BaseDto> {

	public FileDao(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected BaseDo importDto(BaseDto fromDto) throws InvalidInputFault, ExecutionFault, NoResultFault {
		return null;
	}

	@Override
	protected BaseDto exportDto(BaseDo entity) {
		return null;
	}
	
	public void uploadFile(FileDto fileDto) throws SerialException, SQLException {
		if(!ServicesUtil.isEmpty(fileDto) && !ServicesUtil.isEmpty(fileDto.getFileBase64())) {
			byte[] decodedByte = Base64.getDecoder().decode(fileDto.getFileBase64().toString());
			Blob blob = new SerialBlob(decodedByte);
			
			Query query = this.getEntityManager().createNativeQuery("INSERT INTO APP_FILE(ID, FILE_NAME, FILE_TYPE, FILE) VALUES(?, ?, ?, ?)");
			query.setParameter(0, UUID.randomUUID().toString().replaceAll("-", ""));
			query.setParameter(1, fileDto.getFileName());
			query.setParameter(2, fileDto.getFileType());
			query.setParameter(3, blob);
			
			int update = query.executeUpdate();
			
			System.err.println(update);
		}
		
	}

}
