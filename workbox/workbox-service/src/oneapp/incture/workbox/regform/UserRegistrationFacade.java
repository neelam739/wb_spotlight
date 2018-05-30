package oneapp.incture.workbox.regform;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import oneapp.incture.workbox.pmc.services.EntityManagerProviderLocal;

/**
 * Session Bean implementation class ConfigurationFacade
 */
@Stateless
public class UserRegistrationFacade implements UserRegistrationFacadeLocal {

	@EJB
	EntityManagerProviderLocal em;

	public UserResponse regUser(UserRegistrationDto dto) {

		return new UserRegistrationDao(em.getEntityManager()).regUser(dto);

	}

}
