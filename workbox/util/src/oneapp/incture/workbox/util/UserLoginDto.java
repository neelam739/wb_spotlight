package oneapp.incture.workbox.util;

public class UserLoginDto {

	private String userId;
	private String scode;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getScode() {
		return scode;
	}

	public void setScode(String scode) {
		this.scode = scode;
	}

	@Override
	public String toString() {
		return "UserLoginDto [userId=" + userId + ", scode=" + scode + "]";
	}

}
