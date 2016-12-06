package deimos.phase1;

public class ExportAll {

	public static void main(String[] args) {
		
		ExportBookmarks.retreiveBookmarksAsFile("export-bookmarks.txt");
		
		ExportCookies.retreiveCookiesAsFile("export-cookies.txt");
		
		ExportHistory.retreiveHistoryAsFile("export-history.txt");

	}

}
