package oneapp.incture.workbox.pmc.rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import oneapp.incture.workbox.pmc.dto.MailRequestDto;
import oneapp.incture.workbox.pmc.services.NotificationFacadeLocal;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;

@Path("/notification")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class NotificationRest {

	@EJB
	private NotificationFacadeLocal notification;

	@POST
	@Path("/sendMail")
	public ResponseMessage sendMail(MailRequestDto requestDto) {
		return notification.sendNotification(requestDto);
	}

}
