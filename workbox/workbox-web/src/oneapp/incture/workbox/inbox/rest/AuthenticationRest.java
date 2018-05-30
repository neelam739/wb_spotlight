package oneapp.incture.workbox.inbox.rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import oneapp.incture.workbox.pmc.services.CollaborationFacadeLocal;
import oneapp.incture.workbox.poadapter.dto.LoginResponseMessage;
import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.RestResponse;
import oneapp.incture.workbox.util.RestUtil;
import oneapp.incture.workbox.util.ServicesUtil;
import oneapp.incture.workbox.util.UserLoginDto;

@Path("/auth")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class AuthenticationRest {
	
	@EJB
	CollaborationFacadeLocal collaboration;
	
	@POST
	@Path("/login")
	public LoginResponseMessage loginUser(UserLoginDto userLoginDto) {
		LoginResponseMessage responseMessage = new LoginResponseMessage();
		String basicString = null;
		responseMessage.setMessage("Unknown Problem");
		responseMessage.setStatus("FAILURE");
		responseMessage.setStatusCode("1");
		String loginVerifyUrl = "https://aiiha1kww.accounts.ondemand.com/service/users/password";
		RestResponse restResponse = RestUtil.callRest(null, null, loginVerifyUrl, PMCConstant.HTTP_METHOD_POST, null, userLoginDto.getUserId(), userLoginDto.getScode());
		if(restResponse.getResponseCode().equals(PMCConstant.HTTP_STATUS_SUCCESS)) {
			responseMessage.setMessage(PMCConstant.HTTP_STATUS_USER_AUTHORIZED);
			basicString = collaboration.getBasicString(userLoginDto.getUserId());
			if(!ServicesUtil.isEmpty(basicString)) {
				responseMessage.setBasicString(basicString);
			}
			responseMessage.setStatus("SUCCESS");
			responseMessage.setStatusCode("0");
		} else if(restResponse.getResponseCode().equals(PMCConstant.HTTP_STATUS_CODE_UN_AUTHORIZED)) {
			responseMessage.setMessage(PMCConstant.HTTP_STATUS_USER_UN_AUTHORIZED);
			responseMessage.setStatus("SUCCESS");
			responseMessage.setStatusCode("0");
		}
		return responseMessage;
	}
	
	public static void main(String[] args) {
		UserLoginDto userLoginDto = new UserLoginDto();
		userLoginDto.setUserId("P000036");
		userLoginDto.setScode("SW5jdHVyZTEyMw==");
		System.out.println(new AuthenticationRest().loginUser(userLoginDto));
	}
}
