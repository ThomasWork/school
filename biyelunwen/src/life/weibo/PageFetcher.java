package life.weibo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.fileprocess.FileUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;

public class PageFetcher
{
	public String currentPage=null;
	public String pageContent=null;	 
	private final String blockRegex = "<script>FM.view\\(.*\\)";
	private Pattern pattern = Pattern.compile(blockRegex); 
	
	public static String workFolder="src/life/weibo/conf/";
	public static final String cookiePath=workFolder+"cookie.txt";
	public static final String firefoxCookiePath=workFolder+"cookies.txt";
	public static final String alertPath=workFolder+"alert.txt";
	
	public Map<String, String>cookies=null;
	
	public int currentBlogCount;
	public int alertNum;
	
	public PageFetcher()
	{
		this.initFetcher();
	}
	
	public void initFetcher()
	{	
		this.loadCookies();
	//	this.setCookiesWithFirefox();
		this.setAlert();
	}
	
	public void loadCookies()
	{
		this.cookies=new HashMap<String, String>();
		String temp=FileUtil.getLinesFromFile(PageFetcher.cookiePath).get(0);
		String[] ss=temp.split("; ");
		for(String s: ss)
		{
			String s1=s.substring(0, s.indexOf("="));
			String s2=s.substring(s.indexOf("=")+1);
			this.cookies.put(s1,  s2);
		}
	//	System.out.println("设置cookie成功");
	}
	
	public void setAlert(){
		this.alertNum=Integer.parseInt(FileUtil.getLinesFromFile(PageFetcher.alertPath).get(0));
	}
	
	public void setCookiesWithFirefox(){
		try{
			List<String> needed= Arrays.asList("_s_tentry", "UOR", "Apache", "SINAGLOBAL", "ULV", "YF-Ugrow-G0");
			List<String> lines=FileUtil.getLinesFromFile(PageFetcher.firefoxCookiePath);
		//	System.out.println(lines.size());
			this.cookies=new HashMap<String, String>();
			for(String line: lines){
				String[] ss=line.split("\t");
				System.out.println(line);
				System.out.println(ss[5]+"\t"+ss[6]);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void GetPage(String url) throws InterruptedException
	{
		this.currentPage="";
		this.pageContent="";
		try
		{
	//		System.out.println(url);
	//		url="http://weibo.com/u/2810727020/haha.html";
			Document doc=Jsoup.connect(url)
					.cookies(this.cookies).timeout(5*1000).get();
			this.currentPage=doc.html();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Thread.sleep(3*1000);
		}		
	}
	
	public void SetPageContent()
	{
		if(null==this.currentPage)
			return;
		this.pageContent="";
		Matcher m = pattern.matcher(this.currentPage);  
		while(m.find())
		{
			  String jsonStr=m.group();
			  jsonStr=jsonStr.substring(jsonStr.indexOf("{"), jsonStr.lastIndexOf(")"));  
			  WeiboBlock block = JSON.parseObject(jsonStr, WeiboBlock.class);  // 解析json,转换为实体类 
			  String html=block.getHtml();
			  if(null==html)
				  continue;
			  this.pageContent+="\n"+html;
	      }
		if(this.pageContent.indexOf("行为有些异常")>0)
			System.out.println("很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾很遗憾");
	//	System.out.println(this.pageContent);
	}
	
	public void setCurrentBlogCount(){
		Document doc=Jsoup.parse(this.pageContent);
	//	System.out.println(doc.toString());
		Elements es=doc.select("div.WB_cardwrap table.tb_counter td.S_line1");// strong.W_f18
		for(Element e: es){
			String text=e.text();
			int num=Integer.parseInt(text.substring(0, text.length()-2));
		//	System.out.println(num);
			if(text.contains("微博"))
				this.currentBlogCount=num;
		}
	}
	
	public void GetPageUrls()
	{
		Document doc=Jsoup.parse(this.pageContent);
		Elements elements=doc.select("a");
		for(Element ele:elements)
		{
		}
	}
	
	public static void waitSeconds(int n){
		try
		{
			Thread.sleep(n*1000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void myLog(String message){
		String temp=DateUtil.sdf.format(new Date());
		System.out.println(temp+"\t"+message);
	}
	
	public static void testMain() throws Exception{
		try{
		final PageFetcher pf=new PageFetcher();
		pf.alertNum=88;
		int count=1;
		int waitSeconds=0;
		while(true){
			//	System.out.println("睡眠"+seconds);
			PageFetcher.waitSeconds(waitSeconds);
			waitSeconds=NumberUtil.getRandom(10, 60);
			pf.GetPage("http://weibo.com/u/3205779813?profile_ftype=1&is_all=1#_0");
		//	pf.GetPage("http://weibo.com/u/2438084802/home?wvr=5&lf=reg");
			pf.SetPageContent();
			pf.setCurrentBlogCount();
			count--;
			if(count==0){//到了显示周期
				PageFetcher.myLog("Cur："+pf.currentBlogCount+"\t："+pf.alertNum);
				count=10;
			}
			if(pf.alertNum!=pf.currentBlogCount){
				new Thread(new Runnable(){
					@Override
					public void run()
					{
						JOptionPane.showMessageDialog(null, "Cur: "+pf.currentBlogCount, "Alert", JOptionPane.ERROR_MESSAGE);
					}}).start();
				pf.alertNum=pf.currentBlogCount;
				PageFetcher.myLog("更新后："+pf.currentBlogCount+"\t："+pf.alertNum);
				waitSeconds=0;//如果发现更新，则停止一次睡眠
				count=1;//为了尽快展示，则令其为1
			}
		}
		}catch(Exception e){
			System.out.println(e);
			testMain();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		testMain();
	}
}
