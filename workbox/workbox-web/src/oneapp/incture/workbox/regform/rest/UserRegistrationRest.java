package oneapp.incture.workbox.regform.rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import oneapp.incture.workbox.regform.UserRegistrationDto;
import oneapp.incture.workbox.regform.UserRegistrationFacadeLocal;
import oneapp.incture.workbox.regform.UserResponse;

@Path("/userRegistration")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({ MediaType.APPLICATION_JSON })
public class UserRegistrationRest {
	
	@EJB
	UserRegistrationFacadeLocal userFacade;
	
	@POST
	@Path("/regUser")
	public UserResponse regUser(UserRegistrationDto dto){		
		return userFacade.regUser(dto);
	}

}
