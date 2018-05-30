package oneapp.demo.fileupload;

import javax.ejb.Local;


@Local
public interface FileUploadFacadeLocal {

	void uploadFile(FileDto fileDto);

}
