package oneapp.incture.workbox.util;

import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserProvider;

public class UserManagementUtil {

	public static oneapp.incture.workbox.util.User getLoggedInUser() {
		try {
			UserProvider provider = UserManagementAccessor.getUserProvider();
			User user = provider.getCurrentUser();
			oneapp.incture.workbox.util.User usr = new oneapp.incture.workbox.util.User();
			if(!ServicesUtil.isEmpty(user.getName())) {
				usr.setName(user.getName().toUpperCase());
//				usr.setName("P1942566911");
			}
//			UserManagementUtil.getUserGroups();
			return usr;
		} catch(PersistenceException e){
			System.err.println("Exception : "+e.getMessage());
			return null;
		}
	} 
	
	public static void getUserGroups() {
		try {
			UserProvider provider = UserManagementAccessor.getUserProvider();
			User user = provider.getUser("tharun");
			System.err.println(user.getGroups());
//			return usr;
		} catch(PersistenceException e){
			System.err.println("Exception : "+e.getMessage());
//			return null;
		}
	}
}
