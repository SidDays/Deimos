package deimos.phase3;

import java.time.Year;

public class User
{
	private int userId;
	private String fName, lName, location, publicIP;
	private int yearOfBirth;
	private double input_row[];
	private String gender;

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getfName() {
		return fName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPublicIP() {
		return publicIP;
	}
	public void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}
	public int getYearOfBirth() {
		return yearOfBirth;
	}
	public void setYearOfBirth(int yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}
	public double[] getInput_row() {
		return input_row;
	}
	public void setInput_row(double[] input_row) {
		this.input_row = input_row;
	}

	@Override
	public String toString()
	{
		StringBuilder object = new StringBuilder();
		
		object.append(this.userId+" - ");
		
		boolean noFirstName = fName == null || fName.isEmpty();
		boolean noLastName = lName == null || lName.isEmpty();
		
		
		if(noFirstName & noLastName)
		{
			object.append("Unnamed user ");
		}
		else { 
			if(!noFirstName)
			{
				object.append(fName);
				if(!noLastName)
					object.append(" "+lName);
			}
			else {
				if(!noLastName)
					object.append(lName);
			}
		}

		
		object.append(", "+publicIP);

		return object.toString();
	}
	public int getAge() {
		int year = Year.now().getValue();
		return year - yearOfBirth;
	}
}
