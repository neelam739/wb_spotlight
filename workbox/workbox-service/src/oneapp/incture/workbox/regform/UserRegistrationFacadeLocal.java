package oneapp.incture.workbox.regform;

import javax.ejb.Local;

@Local
public interface UserRegistrationFacadeLocal {

	public UserResponse regUser(UserRegistrationDto dto);
}
