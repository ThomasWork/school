package life.weibo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class FollowPageParse extends UserPageParse
{	
	public FollowPageParse(String filePath) throws IOException
	{
		super(filePath);
	}
	
	public void doSomething()
	{
		Document doc=Jsoup.parse(this.realContent);
//		System.out.println(doc.toString());
		Elements elements=doc.select("ul.follow_list a.S_txt1");
		for(Element element: elements)
		{
			String name=element.text();
		}
	}

}
