package deimos.phase3;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;

import deimos.common.DeimosConfig;
import deimos.phase1.ExportIP;

/**
 * Uses Maxmind's GeoIP API to find a location mapping to a given public IP
 * address.
 * Much of this code was provided by
 * www.mkyong.com/java/java-find-location-using-ip-address/
 * 
 * @author mykong
 * @author Siddhesh Karekar
 *
 */

public class LocationEstimator {

	/**
	 * Get the location object for an IP using the default GeoLiteCity database
	 */
	public static ServerLocation estimateLocation(String ipAddress) {

		File file = new File(
				DeimosConfig.FILE_MAXMIND_GEOCITY);
		return estimateLocation(ipAddress, file);

	}
	
	/**
	 * Get the location object for an IP using a provided database
	 */
	public static ServerLocation estimateLocation(String ipAddress, File file) {

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
	
	/** 
	 * Get the location information for an IP passed as a parameter
	 */
	public static String estimateLocationAsString(String ipAddress)
	{

		ServerLocation location = LocationEstimator.estimateLocation(ipAddress);
		return location.toString();

	}
	
	/**
	 * Get the location information of the given
	 */
	public static String getLocationString()
	{
		try {
			String ipAddress = ExportIP.retrievePublicIP();
			ServerLocation location = LocationEstimator.estimateLocation(ipAddress);
			return location.toString();
			
		} catch (UnknownHostException e) {

			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		
		System.out.println(getLocationString());

	}
}