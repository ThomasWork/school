package down;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import entity.Photo;
import entity.User;
import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;

public class DownUserInfo
{
	public static String method="flickr.people.getInfo";
//https://api.flickr.com/services/rest/?user_id=21646077%40N00&method=flickr.people.getInfo&api_key=822eb51ecd570ddfc1ebcd061b2c20f3
	
	public static String getUserInfoUrl(String userId){
		String url=DownPhotoInfo.base
				+"?method="+method
				+"&api_key="+DownPhotoInfo.apiKey
				+"&user_id="+userId;		
		return url;
	}
	
	public static String getUserInfoSavePath(String userId){
		String path=Photo.userInfoDir+userId+".txt";
		return path;
	}
	
	public static void test1(){
		String userId="141026916@N08";
		String url=DownUserInfo.getUserInfoUrl(userId);
		System.out.println(url);
		String path=DownUserInfo.getUserInfoSavePath(userId);
		FileUtil.deleteFile(path);
		String out=HttpHelper.testAndGetContent(path, url);
		System.out.println(out);
	}
     
	
	//使用一个线程下载数据
	public static void downUsersInfo(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.userBeijingIDs);
		System.out.println("需要下载"+ids.size());
		for(int i=0; i<ids.size(); ++i){
			String id=ids.get(i);
			System.out.println(i+","+id);
			String url=DownUserInfo.getUserInfoUrl(id);
			String path=DownUserInfo.getUserInfoSavePath(id);
			HttpHelper.testAndGetContent(path, url);
		}
	}
	
	
	public static void downUsersInfoMultiThreads(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.userBeijingIDs);
		System.out.println("需要下载"+ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				String path=DownUserInfo.getUserInfoSavePath(url);
				url=DownUserInfo.getUserInfoUrl(url);
				HttpHelper.testAndGetContent(path, url);
			}
		});
	}
	
	public static int getUserPhotoNum(String content) {
		try {
		//	System.out.println(content);
	            SAXReader saxReader = new SAXReader();
	            Document document = saxReader.read(new ByteArrayInputStream(content.getBytes("UTF-8")));//必须指定文件的绝对路径  
	            Element rootElement = document.getRootElement();
	            Element person = rootElement.element("person");
	           // System.out.println(person);
	            Element photos = person.element("photos");
	            Element countE = photos.element("count");
	            String countStr = countE.getText();
	           // System.out.println(countStr);
	            int count = Integer.parseInt(countStr);
	            return count;
	        } catch (Exception e) {    
	            System.out.println(e.toString());
	        }
		return -1;
	}
	
	public static void writeUserPhotoNum() {
		List<String> ids = FileUtil.getLinesFromFile(Photo.userBeijingIDs);
		//ids = ids.subList(0, 2);
		List<String> nums = new ArrayList<String>();
		for(String id : ids) {
			String path = DownUserInfo.getUserInfoSavePath(id);
			String content = FileUtil.readAllFromFile(path).trim();
			int page = getUserPhotoNum(content);
			if (-1 == page) {
				System.out.println(path);
			} else {
				nums.add("" + page);
			}
		}
		FileUtil.NewFile(Photo.userPhotoFromInfoStatistic, nums);
	}
	
	public static void main(String[] args){
		//test1();
	//	downUsersInfo();
		//downUsersInfoMultiThreads();
		writeUserPhotoNum();
	}

}
