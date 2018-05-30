package oneapp.incture.workbox.regform;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import oneapp.incture.workbox.poadapter.dao.BaseDao;
import oneapp.incture.workbox.util.ExecutionFault;
import oneapp.incture.workbox.util.InvalidInputFault;
import oneapp.incture.workbox.util.NoResultFault;
import oneapp.incture.workbox.util.ServicesUtil;

public class UserRegistrationDao extends BaseDao<UserRegistrationDo, UserRegistrationDto> {

	public UserRegistrationDao(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected UserRegistrationDo importDto(UserRegistrationDto fromDto) throws InvalidInputFault, ExecutionFault, NoResultFault {
		UserRegistrationDo entity = null;
		if (!ServicesUtil.isEmpty(fromDto)) {
			entity = new UserRegistrationDo();

			entity.setFirstName(ServicesUtil.isEmpty(fromDto.getFirstName()) ? null : fromDto.getFirstName());

			entity.setLastName(ServicesUtil.isEmpty(fromDto.getLastName()) ? null : fromDto.getLastName());

			entity.setEmailId(ServicesUtil.isEmpty(fromDto.getEmailId()) ? null : fromDto.getEmailId());

			entity.setCompanyName(ServicesUtil.isEmpty(fromDto.getCompanyName()) ? null : fromDto.getCompanyName());

			entity.setNoOfEmployees(
					ServicesUtil.isEmpty(fromDto.getNoOfEmployees()) ? null : fromDto.getNoOfEmployees());

		}

		return entity;
	}

	@Override
	protected UserRegistrationDto exportDto(UserRegistrationDo entity) {

		UserRegistrationDto udto = null;
		if (!ServicesUtil.isEmpty(entity)) {
			udto = new UserRegistrationDto();

			udto.setFirstName(ServicesUtil.isEmpty(entity.getFirstName()) ? null : entity.getFirstName());

			udto.setLastName(ServicesUtil.isEmpty(entity.getLastName()) ? null : entity.getLastName());

			udto.setEmailId(ServicesUtil.isEmpty(entity.getEmailId()) ? null : entity.getEmailId());

			udto.setCompanyName(ServicesUtil.isEmpty(entity.getCompanyName()) ? null : entity.getCompanyName());

			udto.setNoOfEmployees(ServicesUtil.isEmpty(entity.getNoOfEmployees()) ? null : entity.getNoOfEmployees());

		}

		return udto;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
	public UserResponse regUser(UserRegistrationDto dto) {
		UserResponse userRespone = null;



		if(!ServicesUtil.isEmpty(dto)&&!ServicesUtil.isEmpty(dto.getFirstName())&&!ServicesUtil.isEmpty(dto.getLastName())&&!ServicesUtil.isEmpty(dto.getEmailId())&&!ServicesUtil.isEmpty(dto.getCompanyName())&&!ServicesUtil.isEmpty(dto.getNoOfEmployees()))
		{
			userRespone = new UserResponse();
			userRespone.setMessage("user not saved");
			userRespone.setStatus("false");
			userRespone.setStatusCode("0");

			try {
				this.create(dto);
				userRespone.setMessage("user succefully saved");
				userRespone.setStatus("true");
				userRespone.setStatusCode("1");
			} catch (ExecutionFault e) {
				e.printStackTrace();
			} catch (InvalidInputFault e) {
				e.printStackTrace();
			} catch (NoResultFault e) {
				e.printStackTrace();
			}
		}else{
			userRespone = new UserResponse();
			userRespone.setStatus("false");
			userRespone.setStatusCode("0");
			userRespone.setMessage("dto contain empty data");
		}

		return userRespone;
	}
}
