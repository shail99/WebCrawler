import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FocusedCrawler {
	
	
	private static final int polite = 1000;
	private static final int MAX_PAGES_LIMIT = 1000;
	private static final int MAX_DEPTH = 5;
	private static final String SEED_PAGE ="https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher";
	private LinkedHashMap<String,Integer> pagesVisited = new LinkedHashMap<String, Integer>();
	private ArrayList<WebPage> pagesToVisit = new ArrayList<WebPage>();
	private LinkedHashMap<String, Integer> pagesRelevantVisited = new LinkedHashMap<String, Integer>();
	private Document doc;
	private LinkedList<String> links = new LinkedList<String>();
	private long currTime,lastTime;

	public LinkedHashMap<String, Integer> getPagesVisited() {
		return pagesVisited;
	}

	public void setPagesVisited(LinkedHashMap<String, Integer> pagesVisited) {
		this.pagesVisited = pagesVisited;
	}

	public ArrayList<WebPage> getPagesToVisit() {
		return pagesToVisit;
	}

	public void setPagesToVisit(ArrayList<WebPage> pagesToVisit) {
		this.pagesToVisit = pagesToVisit;
	}

	public LinkedHashMap<String, Integer> getPagesRelevantVisited() {
		return pagesRelevantVisited;
	}

	public void setPagesRelevantVisited(LinkedHashMap<String, Integer> pagesRelevantVisited) {
		this.pagesRelevantVisited = pagesRelevantVisited;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public LinkedList<String> getLinks() {
		return links;
	}

	public void setLinks(LinkedList<String> links) {
		this.links = links;
	}

	public String getNextUrl() {
		
		String nxtUrl = null;
		int nxtDepth;
		do{
			WebPage pg = this.pagesToVisit.remove(0);
			nxtUrl = pg.getUrl();
			nxtDepth = pg.getDepth();
			
		}while(this.pagesVisited.containsKey(nxtUrl) | !validNextUrl(nxtUrl));
		
		this.pagesVisited.put(nxtUrl,nxtDepth);
		return nxtUrl;
	}

	private boolean validNextUrl(String nxtUrl) {
		if(nxtUrl.contains("#") | nxtUrl.indexOf(":", 6) > 5)
		{
			return false;
		}
		else
		{
			if(nxtUrl.substring((nxtUrl.indexOf(":") + 1)).startsWith("//en.wikipedia.org/wiki/")
					& !nxtUrl.substring((nxtUrl.indexOf(":") + 1)).startsWith("//en.wikipedia.org/wiki/Main_Page"))
			{
				return true;
			}
			else 
				return false;
		}
	}
	
	public int getLastKeyValue() {
		  int out = 0;
		  for (String key : this.pagesVisited.keySet()) {
		    out = this.pagesVisited.get(key);
		  }
		  return out;
		}

	public void crawler(String seed, String keyphrase)
	{
		int i = 1;
		while(this.pagesRelevantVisited.size() < MAX_PAGES_LIMIT 
				&& getLastKeyValue() <= MAX_DEPTH)
		{
			String currUrl = null;
			if(this.pagesToVisit.isEmpty())
			{
				currUrl = seed;
				this.pagesVisited.put(currUrl, 1);
				this.pagesRelevantVisited.put(currUrl, 1);
				System.out.println("Relevant page visited 1: "+currUrl+ " depth : 1");
			}
			else
			{
				currUrl = this.getNextUrl();
				
			}
			
			this.crawl(currUrl);
			
			if(this.searchForKeyPhrase(keyphrase))
			{
				LinkedList<String> listoflinks = new LinkedList<String>();
				listoflinks = (LinkedList<String>) this.getLinks();
				for(String link : listoflinks)
				{
					WebPage webpage = new WebPage();
					webpage.setUrl(link);
					webpage.setDepth(this.pagesVisited.get(currUrl) + 1);
					this.pagesToVisit.add(webpage);
				}
				
				if(currUrl != seed){
					i++;
					this.pagesRelevantVisited.put(currUrl,this.pagesVisited.get(currUrl));
					System.out.println("Relevant page visited " + i + ": " +currUrl+ " with depth : "+ this.pagesVisited.get(currUrl));
				}
			}
			this.links.clear();
		}
	}

	public boolean crawl(String urlToCrawl) {
		
		try {
			
			try {
				if(urlToCrawl.equals(SEED_PAGE))
				{
					lastTime = new Date().getTime(); 
					Thread.sleep(polite);
				}
				else
				{
					currTime = new Date().getTime();
					if(currTime - lastTime > polite)
					{
						Thread.sleep(polite);
					}
					else
					{
						Thread.sleep(polite - (currTime - lastTime));
					}	
					lastTime = currTime;
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Connection connection = Jsoup.connect(urlToCrawl);
			doc = connection.get();
			
			if(connection.response().statusCode() == 200)
			{
				//System.out.println("**Visiting web page**"+urlToCrawl);
			}
			if(!connection.response().contentType().contains("text/html"))
			{
				System.out.println("**Failure**");
				return false;
			}
				
			org.jsoup.select.Elements links = doc.select("a[href]");
			for (Element e : links)
			{
				this.links.add(e.absUrl("href"));
			
			}
			return true;
			
		} catch (IOException e) {
			return false;
		}
	
	}
	
	public boolean searchForKeyPhrase(String keyphrase)
	{
		
		return this.doc.body().text().toLowerCase().contains(keyphrase.toLowerCase());
	}

}
