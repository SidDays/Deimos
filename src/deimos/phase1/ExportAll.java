package deimos.phase1;

public class ExportAll {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ExportBookmarks.retreiveBookmarksAsFile("export-bookmarks.txt");
		System.out.println("Bookmarks exported.");
		
		ExportCookies.retreiveCookiesAsFile("export-cookies.txt");
		System.out.println("Cookies exported.");
		
		ExportHistory.retreiveHistoryAsFile("export-history.txt");
		System.out.println("History exported.");

	}

}
