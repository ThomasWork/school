package life.weibo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSON;

public abstract class PageParse
{
	public String filePath;
	public String pageContent;
	public String realContent;
	
	public String userName;
	
	public PageParse(String filePath) throws IOException
	{
		this.filePath=filePath;
		File file=new File(this.filePath);
		this.pageContent=Jsoup.parse(file, "UTF-8").html();
	}
	
	public abstract Pattern GetMatchPattern();
	
	public void SetRealContent(Pattern pattern)
	{
		if(null==this.pageContent)
			return;
		this.realContent="";
		Matcher m = pattern.matcher(this.pageContent);  
		while(m.find())
		{
			  String jsonStr=m.group();
			  jsonStr=jsonStr.substring(jsonStr.indexOf("{"), jsonStr.lastIndexOf(")"));  
			  WeiboBlock block = JSON.parseObject(jsonStr, WeiboBlock.class);  // 解析json,转换为实体类 
			  String html=block.getHtml();
			  if(null==html)
				  continue;
			  this.realContent+="\n"+html;
	      }
	}
	
	public void doSomething()
	{
		
	}
}

