package life.weibo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ShouYe
{
	public String currentPage;
	
	public ShouYe(){
		
	}
	
	public void getPage(String url) throws InterruptedException{
		this.currentPage="";
		try
		{
			System.out.println(url);
			Document doc=Jsoup.connect(url)
					.timeout(3*1000).get();
			this.currentPage=doc.html();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Thread.sleep(3*1000);
		}		
	}
	
	public static void main(String[] args) throws Exception{
		ShouYe sy=new ShouYe();
		sy.getPage("http://weibo.com/u/2438084802?profile_ftype=1&is_all=1#_0");
		System.out.println(sy.currentPage);
	}
}
