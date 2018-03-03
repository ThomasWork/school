package life.weibo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageParseFactory
{
	public static PageParse getPageParse(String filePath) throws Exception
	{
		
/*		File file=new File(filePath);
		String content=Jsoup.parse(file, "UTF-8").html();

		String blockRegex = "<script>FM.view\\(.*\\)";
		blockRegex="<title>.*的微博_微博</title>"; 
		Pattern pattern = Pattern.compile(blockRegex);
		Matcher matcher=pattern.matcher(content);
		if(matcher.find())
		{
			String temp=matcher.group();
			System.out.println(file.getName()+"---"+temp);
			FileUtil.NewFile("weibo"+"/"+file.getName(), content);
		}*/
		
	/*	BufferedReader br=new BufferedReader(new FileReader(file));
		String url=br.readLine();
		br.close();
		System.out.println(url+"-------");*/

		System.out.println(filePath);
		if(filePath.contains("follow"))
		{
			if(filePath.contains("relate=fans"))
				return new FanPageParse(filePath);
			else
				return new FollowPageParse(filePath);
		}
		else
			return new UserPageParse(filePath);
	/*	if(url.contains("s.weibo.com"))
			return new SearchPageParse(filePath);
		return new UserPageParse(filePath);*/
	}
	
	public static void groupTest()
	{
		String line="This order was placed for QT3000! OK?";
		String pattern ="(.*)(\\d)(.*)";
		Pattern r=Pattern.compile(pattern);
		Matcher m=r.matcher(line);
		System.out.println("group count: "+m.groupCount());
		if(m.find())
		{
			System.out.println(m.group(0));
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}
	}
	
	public static void Test()
	{
//		groupTest();
		final String REGEX="\\b[c-e]at\\b";
		final String INPUT="cat dat eat cattie dat";
		Pattern p=Pattern.compile(REGEX);
		Matcher m=p.matcher(INPUT);
		int count=0;
		System.out.println("Group count: "+m.groupCount());
		while(m.find())
		{
			String temp=m.group();
			System.out.println("temp: "+temp);
		}
		while(m.find())
		{
			count++;
			System.out.println("Match number "+count);
			System.out.println("start(): "+m.start());
			System.out.println("end(): "+m.end());
		}
	}
}
