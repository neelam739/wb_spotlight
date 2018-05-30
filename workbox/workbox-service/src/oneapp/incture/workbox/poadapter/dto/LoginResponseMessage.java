package oneapp.incture.workbox.poadapter.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LoginResponseMessage {
	
	private String status;
	private String statusCode;
	private String message;
	private String basicString;
	
	@Override
	public String toString() {
		return "LoginResponseMessage [status=" + status + ", statusCode=" + statusCode + ", message=" + message
				+ ", basicString=" + basicString + "]";
	}

	public String getBasicString() {
		return basicString;
	}

	public void setBasicString(String basicString) {
		this.basicString = basicString;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
