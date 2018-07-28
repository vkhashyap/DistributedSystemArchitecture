/**
 * The package contains the utilities such as Teacher and Student attributes along with classes that
 * performs I/O operations.
 */
package utility;

/**
 * The class provides attributes for the teacher.
 * 
 * @author KVM2
 */
public class Teacher extends Record {

	
	private String address;
	private String phone;
	private String specialization;
	private String location;

	public Teacher(String firstName, String lastName, String address, String phone, String specialization,
			String location) {

		super(firstName, lastName, "TR");
		this.address = address;
		this.phone = phone;
		this.specialization = specialization;
		this.location = location;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String toString() { 
	    return "Id: " + recordId + " First Name: " + firstName + " Last Name: " + lastName + " Address: " + address + " Phone: " + phone + " Specialization: " + specialization + " Location: " + location ;
	}
}