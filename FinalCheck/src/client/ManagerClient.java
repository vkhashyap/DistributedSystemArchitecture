/**
 * The package contains the all the clients which have an access to the server
 */
package client;

import java.util.Scanner;

import java.lang.StringBuilder;

import logger.LogManager;
import server.MTLServer3;
import server.ServerFrontEnd;
import CenterServerModule.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

/**
 * The class represents the manager client. Depending upon the region to which
 * manager belongs, the data access is provided to the manager to perform the
 * required operations after successful validation.
 * 
 * @author KVM2
 */
public class ManagerClient extends Thread {

	public static String MTL_SERVER_1 = "MONTREAL1";
	public static String MTL_SERVER_2 = "MONTREAL2";
	public static String MTL_SERVER_3 = "MONTREAL3";
	public static String LVL_SERVER_1 = "LAVAL1";
	public static String LVL_SERVER_2 = "LAVAL2";
	public static String LVL_SERVER_3 = "LAVAL3";
	public static String DDO_SERVER_1 = "DOLLARD1";
	public static String DDO_SERVER_2 = "DOLLARD2";
	public static String DDO_SERVER_3 = "DOLLARD3";
	public static String LVL_SERVER_NAME_PRIMARY = "LAVAL";
	public static String DDO_SERVER_NAME_PRIMARY = "DDO";
	public String serverName;
	private String firstName;
	private String lastName;
	private String address;
	private String phone;
	private String specialization;
	private String location;
	private String status;
	private String statusDate;
	private String courseRegistered;
	private String recordId;
	private String fieldName;
	private String newValue;
	public static LogManager clientLogger = null;
	private static String managerId;
	private String targetServerName;
	public static int testOption;
	public static int increment = 0;
	public static ICenterServer serverImpl;
	public static ServerFrontEnd objServerFrontEnd;
	public static ManagerClient objManagerClient;

	public static String fetchServer(String serverName) {

		serverName = serverName.toLowerCase();
		String loc = null;
		if (serverName.equals("mtl")) {
			loc = "Montreal";
		} else if (serverName.equals("lvl")) {
			loc = "Laval";
		} else if (serverName.equals("ddo")) {
			loc = "Dollard";
		} else {
			System.out.println("Incorrect server name");
			clientLogger.mLogger.info("Incorrect server name");
			System.exit(0);
		}
		return loc;

	}

	/**
	 * Validates the manager Id
	 * 
	 * @param managerId
	 *            Manager Login ID
	 * @return True/False whether the validation was successfully or not
	 */
	public static boolean validateManager(String managerId) {

		try {
			if (managerId.length() != 7) {
				return false;
			}

			for (int i = 3; i < managerId.length(); i++) {
				if (!Character.isDigit(managerId.charAt(i))) {
					return false;
				}
			}

			String center = managerId.substring(0, 3);

			if (center.equalsIgnoreCase("MTL") || center.equalsIgnoreCase("LVL") || center.equalsIgnoreCase("DDO")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean validatePhoneNumber(String phoneNumber) {
		try {
			if (phoneNumber.length() != 10) {
				System.out.println("Phone number can't be less than 10 digits");
				return false;
			}
			if (!phoneNumber.matches("^[0-9]*$")) {
				System.out.println("Phone number can't have any characters");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean validateSpecialization(String specializationType) {
		try {
			specializationType = specializationType.toLowerCase();
			if (specializationType.equals("french") || specializationType.equals("maths")
					|| specializationType.equals("science")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Specialization should be from maths, french or science only");
		return false;
	}

	/**
	 * Prompts the user for Teacher attributes.
	 * 
	 * @param scan
	 *            Simple text scanner for user input
	 */
	public void fetchTRecordDetails(Scanner scan) {

		try {
			System.out.println("Enter First Name:");
			firstName = scan.nextLine();

			do {
				System.out.println("Enter Last name(Shouldnt be empty)");
				lastName = scan.nextLine();
			} while (lastName.length() < 1);

			System.out.println("Enter address");
			address = scan.nextLine();
			System.out.println("Enter Phone Number");
			phone = scan.nextLine();
			while (!validatePhoneNumber(phone)) {
				System.out.println("Enter Phone Number");
				phone = scan.nextLine();
			}
			System.out.println("Enter Specialization");
			specialization = scan.nextLine();
			while (!validateSpecialization(specialization)) {
				System.out.println("Enter Specialization");
				specialization = scan.nextLine();
			}
			System.out.println(managerId);
			location = managerId.substring(0, 3);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createTRecord() {

		try {
			clientLogger.mLogger.info("Sending request to create Teacher Record with First Name: " + firstName
					+ " Last name: " + lastName + " Address: " + address + " Phone number: " + phone
					+ " Specialization: " + specialization + " location: " + location + '\n');
			if (objServerFrontEnd.createTRecord(firstName, lastName, address, phone, specialization, location,
					managerId)) {
				System.out.println("Request to create Teacher record completed successfully");
				clientLogger.mLogger.info("Request to create Teacher record completed successfully" + '\n');
			} else {
				System.out.println(
						"Request to create Teacher record failed from the server due to some validation errors");
				ManagerClient.clientLogger.mLogger.info(
						"Request to create Teacher record failed from the server due to some validation errors" + '\n');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Prompts the user for Student attributes.
	 * 
	 * @param scan
	 *            Simple text scanner for user input
	 */
	public void fetchSRecordDetails(Scanner scan) {

		try {
			System.out.println("Enter First Name:");
			firstName = scan.nextLine();

			do {
				System.out.println("Enter Last name(Shouldnt be empty)");
				lastName = scan.nextLine();
			} while (lastName.length() < 1);

			courseRegistered = courseMenu(scan, new StringBuilder());
			status = statusMenu(scan);
			System.out.println("Enter Status Date (dd/mm/yyyy)");
			statusDate = scan.nextLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createSRecord() {
		try {
			clientLogger.mLogger.info("Sending request to create Student Record with First Name: " + firstName
					+ " Last name: " + lastName + " Course: " + courseRegistered + " Status: " + status
					+ " Status Date: " + statusDate + '\n');
			if (objServerFrontEnd.createSRecord(firstName, lastName, courseRegistered, status, statusDate, managerId))
				System.out.println("Request to create Student record completed successfully");
			else {
				System.out.println(
						"Request to create Student record failed from the server due to some validation errors");
				clientLogger.mLogger.info(
						"Request to create Student record failed from the server due to some validation errors" + '\n');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Prompts the user for Student status.
	 * 
	 * @param scan
	 *            Simple text scanner for user input
	 * @return Status of the student
	 */
	public String statusMenu(Scanner scan) {

		String status = "";
		try {
			System.out.println("Enter Status\n" + "1. Active\n" + "2. Inactive\n");

			String option = scan.nextLine();

			switch (option) {
			case "1":
				status = "active";
				break;

			case "2":
				status = "inactive";
				break;

			default:
				System.out.println("Invalid option. Try again");
				statusMenu(scan);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

	/**
	 * Prompts the user for courses.
	 * 
	 * @param scan
	 *            Simple text scanner for user input
	 * @return Registered Courses
	 */
	public String courseMenu(Scanner scan, StringBuilder sbCourseList) {

		try {
			System.out.println("Select Course\n" + "1. Maths\n" + "2. French\n" + "3. Science\n" + "4. Exit");

			String option = scan.nextLine();
			do {
				switch (option) {
				case "1":
					if (!sbCourseList.toString().contains("Maths")) {
						sbCourseList.append("Maths,");
					}
					break;

				case "2":
					if (!sbCourseList.toString().contains("French")) {
						sbCourseList.append("French,");
					}
					break;

				case "3":
					if (!sbCourseList.toString().contains("Science")) {
						sbCourseList.append("Science,");
					}
					break;

				case "4":
					if (sbCourseList.toString().isEmpty()) {
						System.out.println("Select atleast one course");
					} else {
						return sbCourseList.toString();
					}
					break;

				default:
					System.out.println("Invalid option. Try again");
					break;
				}
				System.out
						.println("Select another Course\n" + "1. Maths\n" + "2. French\n" + "3. Science\n" + "4. Exit");
				option = scan.nextLine();
			} while ((Integer.parseInt(option) < 4 && Integer.parseInt(option) > 0)
					|| sbCourseList.toString().isEmpty());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sbCourseList.toString().substring(0, sbCourseList.toString().length() - 1);
	}

	public String fetchEditRecordDetails(Scanner scan) {

		try {
			System.out.println("Enter the Record Id");
			recordId = scan.nextLine();

			// Field validation for Student

			if ((recordId.length() > 2) && (recordId.substring(0, 2).equals("ST"))
					&& (recordId.toString().matches(".*\\d+.*"))) {
				do {
					System.out.println("Enter the field name which is 'courseRegistered', 'status' and 'statusDate'");
					fieldName = scan.nextLine();
				} while ((!fieldName.equals("status")) && (!fieldName.equals("statusDate"))
						&& (!fieldName.equals("courseRegistered")));
			}

			// Field validation for Teacher

			else if ((recordId.length() > 2) && (recordId.substring(0, 2).equals("TR"))
					&& (recordId.toString().matches(".*\\d+.*"))) {
				do {
					System.out.println("Enter the field name which is 'address', 'phone' and 'location'");
					fieldName = scan.nextLine();
				} while ((!fieldName.equals("address")) && (!fieldName.equals("phone"))
						&& (!fieldName.equals("location")));
			}

			/*
			 * Error cases 1.If id is not starting with "TR"/"SR" 2.If id contains only
			 * alphabets
			 */
			else {
				System.out.println("Please enter a valid TR/SR record");
				fetchEditRecordDetails(scan);

			}
			System.out.println("Enter the new value");
			newValue = scan.nextLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "return";
	}

	public void editRecord() {
		try {
			clientLogger.mLogger.info("Sending request to edit Record with ID:" + recordId + '\n');
			String response = objServerFrontEnd.editRecord(recordId, fieldName, newValue, managerId);
			if (response.equals("The given record id doesn't exist")
					|| response.equals("The given field name is invalid for student record")
					|| response.equals("The given field name is invalid for teacher record")) {
				System.out.println("Request to edit Record failed from the server, returned response is: " + response);
				clientLogger.mLogger.info(
						"Request to edit Record failed from the server, returned response is: " + response + '\n');
			} else {
				System.out.println("Request to edit Record completed successfully");
				clientLogger.mLogger.info("Request to edit Record completed successfully" + '\n');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void recordsCount() {
		try {
			clientLogger.mLogger.info("Sending request to get the number of records in each server" + '\n');
			String response = objServerFrontEnd.getRecordCounts(managerId);
			System.out.println("Total number of records are\n" + response + '\n');
			clientLogger.mLogger.info("Total number of records are " + response + '\n');
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fetchTransferRecordDetails(Scanner scan) {

		try {

			System.out.println("Enter the recordID (NOTE: server name should not be same as the current server)");
			recordId = scan.nextLine();

			// Field validation for Student

			if ((recordId.length() > 2) && (recordId.substring(0, 2).equals("ST"))
					&& (recordId.toString().matches(".*\\d+.*"))) {
				do {
					System.out.println("Enter a valid target server name (mtl, ddo or lvl)");
					targetServerName = scan.nextLine();
				} while (!targetServerName.equalsIgnoreCase("mtl") && !targetServerName.equalsIgnoreCase("ddo")
						&& !targetServerName.equalsIgnoreCase("lvl"));
			}

			// Field validation for Teacher

			else if ((recordId.length() > 2) && (recordId.substring(0, 2).equals("TR"))
					&& (recordId.toString().matches(".*\\d+.*"))) {
				do {
					System.out.println("Enter a valid target server name (mtl, ddo or lvl)");
					targetServerName = scan.nextLine();
				} while (!targetServerName.equalsIgnoreCase("mtl") && !targetServerName.equalsIgnoreCase("ddo")
						&& !targetServerName.equalsIgnoreCase("lvl"));
			}

			/*
			 * Error cases 1.If id is not starting with "TR"/"SR" 2.If id contains only
			 * alphabets
			 */
			else {
				System.out.println("Please enter a valid TR/SR record");
				fetchTransferRecordDetails(scan);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transferRecord() {
		try {
			clientLogger.mLogger.info("Transfering the record " + recordId + " from " + managerId.substring(0, 3)
					+ " to " + targetServerName + '\n');

			if (objServerFrontEnd.transferRecord(managerId, recordId, targetServerName)) {
				System.out.println("Request to transfer record completed successfully");
				clientLogger.mLogger.info("Request to transfer record completed successfully" + '\n');
			} else {
				System.out.println("Request to transfer record failed");
				clientLogger.mLogger.info("Request to transfer record failed" + '\n');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * System Main menu Select the following options: 1>Create Teacher Record
	 * 2>Create Student Record 3>Edit Record 4>Get record count 5>Exit
	 * 
	 * @param scan
	 *            Simple text scanner for user input
	 * @param menu
	 */
	public void mainMenu(Scanner scan, StringBuffer menu) {

		try {
			System.out.println(menu);

			String option = scan.nextLine();

			do {
				switch (option) {
				case "1":
					fetchTRecordDetails(scan);
					createTRecord();
					break;

				case "2":
					fetchSRecordDetails(scan);
					createSRecord();
					break;

				case "3":
					fetchEditRecordDetails(scan);
					editRecord();
					break;

				case "4":
					recordsCount();
					break;

				case "5":
					do {
						fetchTransferRecordDetails(scan);
					} while (targetServerName.equalsIgnoreCase(managerId.substring(0, 3)));
					transferRecord();
					break;
				case "6":
					objServerFrontEnd.killThreads();
					break;

				case "7":
					clientLogger.mLogger.info("Logged Out" + '\n');
					System.out.println("Good Bye");
					System.exit(0);
					break;

				default:
					clientLogger.mLogger.info("Client entered Invalid Option for main menu: " + option + '\n');
					System.out.println("Invalid option. Try again");
					break;
				}
				mainMenu(scan, menu);
			} while (option != "5");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Main Method.
	 * 
	 * @param args
	 *            (No arguments are needed to launch)
	 */
	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);
		objManagerClient = new ManagerClient();

		try {
			StringBuffer menu = new StringBuffer(
					"Select the following options:\n" + "1> Create Teacher Record\n" + "2> Create Student Record\n"
							+ "3> Edit Record\n" + "4> Get record count\n" + "5> Transfer record\n"+ "6> kill MTL primary\n" + "7> Exit\n");

			System.out.println("Enter the Manager Id");
			managerId = scan.nextLine();
			if (validateManager(managerId)) {
				clientLogger = new LogManager(managerId);
				String serName = managerId.substring(0, 3);
				String location = fetchServer(serName);
				objServerFrontEnd = new ServerFrontEnd(args, location);
				objManagerClient.mainMenu(scan, menu);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}

	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public void run() {

	}
}