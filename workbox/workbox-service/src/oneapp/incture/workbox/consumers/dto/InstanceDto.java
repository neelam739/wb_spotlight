package oneapp.incture.workbox.consumers.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstanceDto {
	
	private String instanceId;
	private String sapOrigin;
	private String requestId;
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getSapOrigin() {
		return sapOrigin;
	}
	public void setSapOrigin(String sapOrigin) {
		this.sapOrigin = sapOrigin;
	}
	
	@Override
	public String toString() {
		return "InstanceDto [instanceId=" + instanceId + ", sapOrigin=" + sapOrigin + ", requestId=" + requestId + "]";
	}
	
}
