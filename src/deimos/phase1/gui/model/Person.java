package deimos.phase1.gui.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;

/**
 * Currently unused.
 * A class detailing all possible properties for a Person
 * intended to be used with the helper GUI. 
 * @author Siddhesh Karekar
 */
public class Person {
	
	private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty gender;
    private final IntegerProperty yearOfBirth;
    private final BooleanProperty tosAgree;
    
    private final StringProperty browser;
    private final StringProperty publicIP;
    
    private final IntegerProperty progressCookies;
    private final IntegerProperty progressHistory;
    private final IntegerProperty progressFavorites;
    private final IntegerProperty progressPublicIP;
    
    public Person() {
    	this(null, null, null, 0, false);
    }

	public Person(String firstName, String lastName, String gender, int yearOfBirth,
    		boolean tosAgree) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.tosAgree = new SimpleBooleanProperty(tosAgree);
        this.yearOfBirth = new SimpleIntegerProperty(yearOfBirth);
        
        this.browser = new SimpleStringProperty();
        this.gender = new SimpleStringProperty();
        this.publicIP = new SimpleStringProperty();

        // Some initial dummy data, just for convenient testing.
        progressCookies = new SimpleIntegerProperty(0);
        progressHistory = new SimpleIntegerProperty(0);
        progressFavorites = new SimpleIntegerProperty(0);
        progressPublicIP = new SimpleIntegerProperty(0);

    }
	
    public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}
	
	public StringProperty firstNameProperty() {
        return firstName;
    }

	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}
	
	public StringProperty lastNameProperty() {
        return lastName;
    }

	public String getGender() {
		return gender.get();
	}

	public void setGender(String gender) {
		this.gender.set(gender);
	}
	
	public StringProperty genderProperty() {
		return gender;
	}

	public int getYearOfBirth() {
		return yearOfBirth.get();
	}

	public void setYearOfBirth(int yearOfBirth) {
		this.yearOfBirth.set(yearOfBirth);;
	}
	
	public IntegerProperty yearOfBirthProperty() {
		return yearOfBirth;
	}

	public boolean getTosAgree() {
		return tosAgree.get();
	}

	public void setTosAgree(boolean tosAgree) {
		this.tosAgree.set(tosAgree);;
	}
	
	public BooleanProperty tosAgreeProperty() {
		return tosAgree;
	}

	public String getBrowser() {
		return browser.get();
	}

	public void setBrowser(String browser) {
		this.browser.set(browser);;
	}
	
	public StringProperty browserProperty() {
		return browser;
	}

	public String getPublicIP() {
		return publicIP.get();
	}

	public void setPublicIP(String publicIP) {
		this.publicIP.set(publicIP);;
	}
	
	public StringProperty publicIPProperty() {
		return publicIP;
	}

	public int getProgressCookies() {
		return progressCookies.get();
	}

	public void setProgressCookies(int progressCookies) {
		this.progressCookies.set(progressCookies);
	}
	
	public IntegerProperty progressCookiesProperty() {
		return progressCookies;
	}

	public int getProgressHistory() {
		return progressHistory.get();
	}

	public void setProgressHistory(int progressHistory) {
		this.progressHistory.set(progressHistory);;
	}

	public IntegerProperty progressHistoryProperty() {
		return progressHistory;
	}
	
	public int getProgressFavorites() {
		return progressFavorites.get();
	}

	public void setProgressFavorites(int progressFavorites) {
		this.progressFavorites.set(progressFavorites);;
	}
	
	public IntegerProperty progressFavoritesProperty() {
		return progressFavorites;
	}

	public int getProgressPublicIP() {
		return progressPublicIP.get();
	}

	public void setProgressPublicIP(int progressPublicIP) {
		this.progressPublicIP.set(progressPublicIP);;
	}
	
	public IntegerProperty progressPublicIPProperty() {
		return progressPublicIP;
	}


}
