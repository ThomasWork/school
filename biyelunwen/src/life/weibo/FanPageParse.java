package life.weibo;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class FanPageParse extends UserPageParse
{
	public FanPageParse(String filePath) throws IOException
	{
		super(filePath);
	}
	
	public void doSomething()
	{
		Document doc=Jsoup.parse(this.realContent);
	//	System.out.println(doc.toString());
		Elements elements=doc.select("ul.follow_list a.S_txt1");
		for(Element element: elements)
		{
		}
	}
}
