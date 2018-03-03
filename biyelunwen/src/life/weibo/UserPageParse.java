package life.weibo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class UserPageParse extends PageParse
{
	
	public static HashSet<String> distinctNames=new HashSet<String>();
	
	public String userName="----------------------------------------";
	public int userId;
	
	public UserPageParse(String filePath) throws IOException
	{
		super(filePath);
		Document doc=Jsoup.parse(this.pageContent);
//		System.out.println(this.pageContent);
		Element element=doc.select("title").first();
//		System.out.println(element.toString());
		String temp=element.text();
	/*	this.userName=temp.substring(0, temp.lastIndexOf("的微博_微博"));
		this.userId=WeiboUser.allUsers.get(this.userName);
		System.out.println("微博用户："+this.userName+" "+this.userId);
		UserPageParse.distinctNames.add(this.userName);*/
	}

	@Override
	public Pattern GetMatchPattern()
	{
		String blockRegex = "<script>FM.view\\(.*\\)";
		Pattern pattern = Pattern.compile(blockRegex);
		return pattern;
	}
}
