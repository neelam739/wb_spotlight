package oneapp.incture.workbox.util;

import org.json.JSONObject;

public class RestResponse {

	private JSONObject jsonObject;
	private Integer responseCode;

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public String toString() {
		return "RestResponse [jsonObject=" + jsonObject + ", responseCode=" + responseCode + "]";
	}

}
