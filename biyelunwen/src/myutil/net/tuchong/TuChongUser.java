package myutil.net.tuchong;

import java.util.ArrayList;
import java.util.List;

import myutil.fileprocess.FileUtil;
import myutil.net.HttpHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TuChongUser
{
	public String id;
	public String shouYeUrl;
	public List<String> followersUrl;//关注这个人的人
	public List<String> followingUrl;//这个人关注的人
	public Document shouYe;
	
	public int followerNumber;//关注这个人的人
	public int followingNumber;//这个人关注的人
	
	public static String totalPageFolder="E:/netlength/tuchong/";
	public static String shouYeFolder=totalPageFolder+"shouye/";
	public static String followerFolder=totalPageFolder+"follower/";
	public static String followingFolder=totalPageFolder+"following/";
	
	public TuChongUser(String url){
		this.shouYeUrl=url;
		this.setId();
		this.getShouYe();
	}
	
	public void getShouYe(){
		String content=HttpHelper.testAndGetContent(shouYeFolder+this.id, this.shouYeUrl);
	//	System.out.println(content);
		this.shouYe=Jsoup.parse(content);
	//	System.out.println(this.shouYe);
	//	System.out.println(content);
		this.followerNumber=0;
		this.followingNumber=0;
		Element elem=this.shouYe.select("div.profile-meta").first();
		if(null==elem){
			FileUtil.deleteFile(shouYeFolder+this.id);
			return;
		}
	//	System.out.println(elem);
	//	String temp=elem.toString();
		for(Element e: elem.children()){
			String text=e.text();
			if(text.endsWith("关注"))
				this.followingNumber=Integer.parseInt(text.split(" ")[0]);
			if(text.endsWith("关注者"))
				this.followerNumber=Integer.parseInt(text.split(" ")[0]);
		}
	//	if(temp.contains("这是一个收藏夹")){
	//		System.out.println("这是一个收藏夹：");
	//	}
	//	System.out.println("关注者："+this.followerNumber+",关注："+this.followingNumber);
	}
	
	//设置关注他的人的URL
	public void setFollowerUrl(){
		this.followersUrl =  new ArrayList<String>();
		
		int pages=this.followerNumber/40;
		if(pages*40<this.followerNumber)
			pages++;
		if(pages>50)
			pages=50;
//		System.out.println(this.followerNumber+":"+pages);
		String baseUrl=this.shouYeUrl+"followers/?page=";
		for(int i=1; i<=pages; ++i){
			String url=baseUrl+i;
			String content = HttpHelper.testAndGetContent(TuChongUser.followerFolder+this.id+"_"+i, url);
		//	System.out.println(content);
			Document doc=Jsoup.parse(content);
			Elements elems=doc.select("h4.name");
			for(Element elem: elems){
				Element user=elem.child(0);
			//	System.out.println(user.attr("href"));
				this.followersUrl.add(user.attr("href"));
			}
		}
	}
	
	//设置他关注的人的URL
	public void setFollowingUrl(){
		this.followingUrl=new ArrayList<String>();
		int pages=this.followingNumber/40;
		if(pages*40<this.followingNumber)
			pages++;
		if(pages>50)
			pages=50;
	//	System.out.println(this.followingNumber+":"+pages);
		String baseUrl=this.shouYeUrl+"following/?page=";
		for(int i=1; i<=pages; ++i){
			String url=baseUrl+i;
			String content=HttpHelper.testAndGetContent(TuChongUser.followingFolder+this.id+"_"+i, url);
			Document doc=Jsoup.parse(content);
			Elements elems=doc.select("h4.name");
			for(Element elem: elems){
				Element user=elem.child(0);
				this.followingUrl.add(user.attr("href"));
		//		System.out.println(user.attr("href"));
		//		TuChongUser tcu=new TuChongUser(user.attr("href"));
			}
		}
	}
	
	//获得用户id
	public void setId(){
		this.id="";
		String url=this.shouYeUrl;
		if(url.startsWith("https://tuchong.com/"))
		{
			this.id = "houmian_"+url.substring("https://tuchong.com/".length(), url.lastIndexOf("/"));			
		}
		else if(url.startsWith("https://") && url.endsWith(".tuchong.com/"))
		{
			this.id = "qianmian_"+url.substring("https://".length(), url.lastIndexOf(".tuchong.com"));
		}
		else
		{
			System.out.println("无法识别id");
			System.exit(0);
		}
	//	System.out.println(url+"=========================="+this.id);
	}

	public static void main(String[] args)
	{
		String first="https://shelltown.tuchong.com/";
		first = "https://zyl-.tuchong.com/";
		TuChongUser tcu=new TuChongUser(first);
	//	tcu.setFollowerUrl();
	//	tcu.setFollowingUrl();
	}

}
