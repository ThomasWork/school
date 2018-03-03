package life.onlinetest;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SplitStudents
{
	public static void test1() throws IOException{
		Document doc=Jsoup.parse(new File("C:/Users/Admin/Desktop/all.html"), "UTF-8");
		Elements elems=doc.select("tr[align=center]");
	//	System.out.println(doc.toString());
		System.out.println(elems.size());
		for(int i=1; i<elems.size(); ++i){
			Element cur=elems.get(i);
		//	System.out.println(cur);
			Elements children=cur.select("td");
			String id=children.get(0).select("input").first().attr("value");
			String name=children.get(2).text();
			String solved=children.get(3).select("a").text();
		//	System.out.println(name);
			String url=getUrl(id);
			String temp=id+"\t"+name+"\t"+solved+"\t"+url;
			for(int j=5; j<=10; ++j){
				temp+="\t"+children.get(j).text();
			}
			System.out.println(temp);
		}
	}
	
	public static String getUrl(String id){
		String url="http://222.29.196.187/JudgeOnline/status?user_id="+id+"&contest_id=1005";
		return url;
	}
	
	public static String getId(Elements elems){
		Element id=elems.get(0);
		String ids=id.attr("value");
		return ids;
	}

	public static void main(String[] args) throws IOException
	{
		test1();
	}

}
