package deimos.phase1;

/*
 * Much of this code was provided by
 * https://www.mkyong.com/java/java-find-location-using-ip-address/
 */

import org.ipify.Ipify;
import java.io.File;
import java.io.IOException;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;

class ServerLocation {

	private String countryCode;
	private String countryName;
	private String region;
	private String regionName;
	private String city;
	private String postalCode;
	private String latitude;
	private String longitude;

	@Override
	public String toString() {
		return city + " " + postalCode + ", " + regionName + " (" + region
				+ "), " + countryName + " (" + countryCode + ") " + latitude
				+ "," + longitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
}

public class IPLocationEstimator {

	// Get the location object for an IP using the default GeoLiteCity database
	public static ServerLocation getLocation(String ipAddress) {

		File file = new File(
				"libs/Maxmind/GeoLiteCity.dat");
		return getLocation(ipAddress, file);

	}
	
	// Get the location object for an IP using a provided database
	public static ServerLocation getLocation(String ipAddress, File file) {

		ServerLocation serverLocation = null;

		try {

			serverLocation = new ServerLocation();

			LookupService lookup = new LookupService(file,LookupService.GEOIP_MEMORY_CACHE);
			Location locationServices = lookup.getLocation(ipAddress);

			serverLocation.setCountryCode(locationServices.countryCode);
			serverLocation.setCountryName(locationServices.countryName);
			serverLocation.setRegion(locationServices.region);
			serverLocation.setRegionName(regionName.regionNameByCode(
					locationServices.countryCode, locationServices.region));
			serverLocation.setCity(locationServices.city);
			serverLocation.setPostalCode(locationServices.postalCode);
			serverLocation.setLatitude(String.valueOf(locationServices.latitude));
			serverLocation.setLongitude(String.valueOf(locationServices.longitude));

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return serverLocation;

	}
	
	// Get the location information for an IP passed as a parameter
	public static String getLocationString(String ipAddress)
	{

		ServerLocation location = IPLocationEstimator.getLocation(ipAddress);
		return location.toString();

	}
	
	// Get the location information of the given 
	public static String getLocationString()
	{
		try {

			String ipAddress = Ipify.getPublicIp();
			ServerLocation location = IPLocationEstimator.getLocation(ipAddress);
			return location.toString();

		}
		catch (IOException ioe) {
			System.out.println("Error while trying to get public IP.");
			ioe.printStackTrace();
			
			return "";
		}
		
	}

	public static void main(String[] args) {
		
		System.out.println(getLocationString());

	}
}