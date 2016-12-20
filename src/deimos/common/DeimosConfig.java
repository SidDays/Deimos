package deimos.common;

public class DeimosConfig {
	
	public static final String DIR_OUTPUT = "output";
	public static final String DIR_CHROME_WIN = System.getenv("LOCALAPPDATA") +
			"/Google/Chrome/User Data/Default";
	
	public static final String FILE_OUTPUT_BOOKMARKS = "export-bookmarks.txt";
	public static final String FILE_OUTPUT_COOKIES = "export-cookies.txt";
	public static final String FILE_OUTPUT_HISTORY = "export-history.txt";
	public static final String FILE_OUTPUT_PUBLICIP = "export-publicIP.txt";
	public static final String FILE_OUTPUT_USERINFO = "export-userInfo.txt";
	public static final String[] FILES_OUTPUT_ALL = {
			
			FILE_OUTPUT_BOOKMARKS,
			FILE_OUTPUT_COOKIES,
			FILE_OUTPUT_HISTORY,
			FILE_OUTPUT_PUBLICIP,
			FILE_OUTPUT_USERINFO
	};
	public static final String FILE_OUTPUT_ALL_ZIP = "export-all.zip";
	
	public static final String DELIM = "|";
}
