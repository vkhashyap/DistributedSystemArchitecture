/**
 * The package contains the utilities such as Teacher and Student attributes along with classes that
 * performs I/O operations.
 */
package utility;

/**
 * The class provides attributes for the student.
 * 
 * @author KVM2
 */
public class Student extends Record {

	private String courseRegistered;
	private String status;
	private String statusDate;

	public Student(String firstName, String lastName, String courseRegistered, String status, String statusDate) {

		super(firstName, lastName, "ST");
		this.courseRegistered = courseRegistered;
		this.status = status;
		this.statusDate = statusDate;
	}

	public String getCourseRegistered() {
		return courseRegistered;
	}

	public void setCourseRegistered(String courseRegistered) {
		this.courseRegistered = courseRegistered;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	public String toString() {
		return "Id: " + recordId + " First Name: '" + firstName + " Last Name: " + lastName + " Course Registered: "
				+ courseRegistered + " Status: " + status + " Status Date: " + statusDate;
	}
}