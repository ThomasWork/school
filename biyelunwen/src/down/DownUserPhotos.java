package down;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import myutil.DateUtil;
import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;
import entity.Photo;
import entity.User;

public class DownUserPhotos
{
	public static String method="flickr.people.getPhotos";
	public static int MAX_PAGE = 500;
//https://api.flickr.com/services/rest/?user_id=21646077%40N00&method=flickr.people.getInfo&api_key=822eb51ecd570ddfc1ebcd061b2c20f3

	public static String getUserPhotosUrl(String userId){
		String url = DownPhotoInfo.base
				+ "?method=" + method
				+ "&api_key=" + DownPhotoInfo.apiKey
				+ "&per_page=" + MAX_PAGE
				+ "&extras=" + "date_taken,geo"
				+ "&user_id=" + userId;		
		return url;
	}
	
	public static String getUserPhotosUrl(String userId, int page){
		return getUserPhotosUrl(userId) + "&page=" + page;
	}
	
	public static String getUserPhotosSavePath(String userId, int page){
		String path=Photo.userPhotosDir + userId + "_" + page +".txt";
		return path;
	}
	
	public static String getUserMergePhotosPath(String id) {
		return Photo.userMergePhotosDir + id + ".txt";
	}
	
	public static String getUserGeoPhotoPath(String id) {
		return Photo.userMergePhotosGeo + id + ".txt";
	}
	
	public static String downUserPhotos(String id, int page) {
		String url = getUserPhotosUrl(id, page);
		String path = getUserPhotosSavePath(id, page);
		String content = HttpHelper.testAndGetContent(path, url);
		if (getTotalPages(content) < 0) {
			System.out.println(path);
		}
		return content;
	}
	
	public static int getTotalPages(String content) {
		try {
	            SAXReader saxReader = new SAXReader();
	            Document document = saxReader.read(new ByteArrayInputStream(content.getBytes("UTF-8")));//必须指定文件的绝对路径  
	            Element rootElement = document.getRootElement();
	            Element photos = rootElement.element("photos");
	            String pageStr = photos.attributeValue("pages");
	            int page = Integer.parseInt(pageStr);
	            return page;
	        } catch (Exception e) {    
	            System.out.println(e.toString());
	        }
		return -1;
	}
	
	public static void downUserPhotos(String id) {
		String content = downUserPhotos(id, 1);
		int pages = getTotalPages(content);
		if (pages <= -1) {
			System.out.println("get page error: " + id);
		}
		for (int i = 2; i <= pages; i += 1) {
			downUserPhotos(id, i);
		}
	}
	
	public static void mergeUserPhotos(String id) {
		List<entity.Photo> ps = new ArrayList<entity.Photo>();
		String first = downUserPhotos(id, 1);
		int pages = getTotalPages(first);
		for (int i = 1; i <= pages; i += 1) {
			String content2 = downUserPhotos(id, i);
			try {
	            SAXReader saxReader = new SAXReader();
	            Document document = saxReader.read(new ByteArrayInputStream(content2.getBytes("UTF-8")));//必须指定文件的绝对路径  
	            Element rootElement = document.getRootElement();
	            Element photos = rootElement.element("photos");
	            List<Element> pts = photos.elements("photo");
	            for (Element el : pts) {
	            	Photo p = new Photo(el.attributeValue("id"));
	            	p.dateTaken = DateUtil.getDate(el.attributeValue("datetaken"));
	            	p.latitude = Double.parseDouble(el.attributeValue("latitude"));
	            	p.longitude = Double.parseDouble(el.attributeValue("longitude"));
	            	p.userId = id;
	            	ps.add(p);
	            }
	        } catch (Exception e) {
	            System.out.println(id + "\n" + e.toString());
	        }
		}
		Collections.sort(ps);
		Photo.savePhotos(ps, getUserMergePhotosPath(id));
		//System.out.println(id + "," + ps.size());
	}
	
	public static void getUserPhotosWithGeo(String id) {
		String path = DownUserPhotos.getUserMergePhotosPath(id);
		User u = new User(id);
		u.photosList = Photo.getPhotos(path);
		u.photosList = Photo.getPhotosWithGeo(u.photosList);
		Photo.savePhotos(u.photosList, getUserGeoPhotoPath(id));
	}
	
	public static void test1() {
		String userId = "141026916@N08";
		//downUserPhotos(userId);
		mergeUserPhotos(userId);
	}
	
	//使用一个线程下载数据
	public static void downUsersPhotos() {
		List<String> ids= FileUtil.getLinesFromFile(Photo.userBeijingIDs);
		System.out.println("需要下载"+ids.size());
		for(int i=0; i<ids.size(); ++i) {
			String id=ids.get(i);
			System.out.println(i+","+id);
			String url = DownUserPhotos.getUserPhotosUrl(id);
			String path = DownUserPhotos.getUserPhotosSavePath(id, 1);
			HttpHelper.testAndGetContent(path, url);
		}
	}
	
	
	public static void downUsersPhotosMultiThreads(){
		List<String> ids = FileUtil.getLinesFromFile(Photo.userBeijingIDs);
		System.out.println("需要下载" + ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				//downUserPhotos(url);
				//mergeUserPhotos(url);
				getUserPhotosWithGeo(url);
			}
		});
	}

	public static void main(String[] args)
	{
		//downUsersPhotosMultiThreads();
		//test1();
		FileUtil.deleteFilesInFile();
		//mergeUserPhotos("8192694@N05");
	}

}
