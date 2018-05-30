package oneapp.incture.workbox.regform;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import oneapp.incture.workbox.poadapter.entity.BaseDo;

@Entity
@Table(name = "USER_REG")
public class UserRegistrationDo implements BaseDo, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", length = 150)
	private String id = UUID.randomUUID().toString().replaceAll("-", "");;

	@Column(name = "FIRST_NAME", length = 50)
	private String firstName;

	@Column(name = "LAST_NAME", length = 50)
	private String lastName;

	@Column(name = "EMAIL_ID", length = 70)
	private String emailId;

	@Column(name = "COMPANY_NAME", length = 70)
	private String companyName;

	@Column(name = "NO_OF_EMPLOYEES", length = 50)
	private String noOfEmployees;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyNmae) {
		this.companyName = companyNmae;
	}

	public String getNoOfEmployees() {
		return noOfEmployees;
	}

	public void setNoOfEmployees(String noOfEmployees) {
		this.noOfEmployees = noOfEmployees;
	}

	@Override
	public String toString() {
		return "UserRegDo [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId
				+ ", companyNmae=" + companyName + ", noOfEmployees=" + noOfEmployees + "]";
	}

	@Override
	public Object getPrimaryKey() {
		return id;
	}

}
