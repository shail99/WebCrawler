import java.util.Date;

public class FocusedCrawlerDemo {
	private static final String SEED = "https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher";
	private static final String KEYPHRASE = "concordance";
	private static final String NOKEYPHRASE = "";
	public static void main(String[] args) {
		System.out.println("Start Time : " + new Date().toString());
		
		FocusedCrawler fc = new FocusedCrawler();
		fc.crawler(SEED, KEYPHRASE);
		
		System.out.println("Pages Visited : "+fc.getPagesVisited().size());
		
		System.out.println("End Time : " + new Date ().toString());
		
	}

}
