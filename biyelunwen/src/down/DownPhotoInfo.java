package down;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entity.Photo;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.dom4j.Document;  
import org.dom4j.DocumentHelper;  
import org.dom4j.Element;  
import org.dom4j.io.SAXReader;  

public class DownPhotoInfo
{
	public static String base = "https://api.flickr.com/services/rest/";
	public static String apiKey = "822eb51ecd570ddfc1ebcd061b2c20f3";
	public static String apiSecret = "7e033834372d0fff";

	public static String method="flickr.photos.getInfo";
	
	
	public static void test1(String photoId){
		String url=base+"?method="+method+"&api_key="+apiKey+"&photo_id="+photoId;
		System.out.println(url);
		FileUtil.deleteFile("temp.txt");
		String temp=myutil.net.HttpHelper.testAndGetContent("temp.txt", url);
		System.out.println(temp);
	}
	
	//使用一个线程下载数据
	public static void downPhotos(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.pidPath);
		System.out.println("需要下载"+ids.size());
		for(int i = 0; i < ids.size(); i += 1){
			String id=ids.get(i);
			System.out.println(i+","+id);
			String url = base+"?method=" + method + "&api_key=" + apiKey + "&photo_id=" + id;
			String path = Photo.photoInfoDir + id+".txt";
			HttpHelper.testAndGetContent(path, url);
		}
	}
	
	public static void downPhotosMultiThreads() {
		List<String> ids= FileUtil.getLinesFromFile(Photo.pidPath);
		System.out.println("需要下载" + ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				String path=Photo.getPhotoInfoFilePath(url);
				url = DownPhotoInfo.base+"?method=" + DownPhotoInfo.method+"&api_key=" + DownPhotoInfo.apiKey + "&photo_id="+url;
				HttpHelper.testAndGetContent(path, url);
			}
		});
	}
	
	public static String getPhotoContent(String path) {
		List<String> info = new ArrayList<String>();
		try{
			SAXReader saxReader = new SAXReader(); 
			Document document = saxReader.read(new File(path));
			Element rootElement = document.getRootElement();
			Element photo = rootElement.element("photo");
			info.add(photo.attributeValue("id"));
			Element user = photo.element("owner");
			info.add(user.attributeValue("nsid"));
			Element location = photo.element("location");
			info.add(location.attributeValue("latitude"));
			info.add(location.attributeValue("longitude"));
			Element dates = photo.element("dates");
			info.add(dates.attributeValue("taken"));
			} catch (Exception e) {    
	            //e.printStackTrace();    
				System.out.println(path);
			}
		return StringUtil.listToString(info, ",");
	}
	
	public static void getAllUserId() {
		List<String> ids= FileUtil.getLinesFromFile(Photo.pidPath);
		Set<String> uids = new HashSet<String>();
		List<String> infos = new ArrayList<String>();
		for (int i = 0; i < ids.size(); i += 1) {
			String path=Photo.getPhotoInfoFilePath(ids.get(i));
			infos.add(getPhotoContent(path));
		}
		FileUtil.NewFile(Photo.photoBasicInfoPath, infos);
		List<String> uids2 = new ArrayList<String>(uids);
		FileUtil.NewFile(Photo.beijingUserIDs, uids2);
	}
	
	public static void test1() {
		try{  
         //   InputStream inputStream = new FileInputStream(new File("D:/project/dynamicWeb/src/resource/module01.xml"));  
            SAXReader saxReader = new SAXReader();  
            Document document = saxReader.read(new File("G:/ASR/school/data/biye/test.txt"));//必须指定文件的绝对路径  
            Element rootElement = document.getRootElement();
            Element photo = rootElement.element("photo");
            Element element = photo.element("owner");
            System.out.println(element.attributeValue("nsid"));
            element = photo.element("dates");
            System.out.println(element.attributeValue("taken"));
            Element location = photo.element("location");
            System.out.println(location.attributeValue("longitude"));
        } catch (Exception e) {    
            e.printStackTrace();    
        }
    }
	
	public static void main(String[] args){
	//	test1("31370068340");
	//	downPhotos();
	//	downPhotosMultiThreads();
		getAllUserId();
		
		
	//	test1();
	//	System.out.println(getPhotoContent("G:/ASR/school/data/biye/test.txt"));
	}

}
