package down;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import myutil.DateUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import entity.Photo;
import entity.User;

public class DownPhotoExif
{
	public static String base = "https://api.flickr.com/services/rest/";
	public static String apiKey = "822eb51ecd570ddfc1ebcd061b2c20f3";
	public static String apiSecret = "7e033834372d0fff";

	public static String method="flickr.photos.getExif";
	
	public static String getExifUrl(String id) {
		String url = base+"?method=" + method + "&api_key=" + apiKey + "&photo_id=" + id;
		return url;
	}
	
	public static void test1(String photoId){
		String url = getExifUrl(photoId);
		System.out.println(url);
		FileUtil.deleteFile("temp.txt");
		String temp = myutil.net.HttpHelper.testAndGetContent("temp.txt", url);
		System.out.println(temp);
	}
	
	public static String getExifPath(String id) {
		return Photo.photoExifDir + id + ".txt";
	}
	
	//使用一个线程下载数据
	public static void downPhotosExif(List<String> ids) throws IOException{
		System.out.println("需要下载"+ids.size());
		for(int i = 0; i < ids.size(); i += 1){
			String id=ids.get(i);
			System.out.println(i+","+id);
			String url = getExifUrl(id);
			HttpHelper.downloadFromUrl(url, getExifPath(id));
//			HttpHelper.testAndGetContent(path, url);
		}
	}
	
	public static void downPhotosExifMultiThreads(List<String> ids) {
		System.out.println("需要下载" + ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				String path = getExifPath(url);
				url = getExifUrl(url);
				HttpHelper.testAndGetContent(path, url);
			}
		});
	}
	
	public static String getPhotoManufacturer(String id) {
		List<String> info = new ArrayList<String>();
		try{
			SAXReader saxReader = new SAXReader(); 
			Document document = saxReader.read(getExifPath(id));
			Element rootElement = document.getRootElement();
			Element photo = rootElement.element("photo");
			String camera = photo.attributeValue("camera");
			return camera;
			} catch (Exception e) {    
	            //e.printStackTrace();    
				//System.out.println(e.toString());
			}
		return "";
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
            //e.printStackTrace();    
        }
    }
	
	public static void downloadForeignPhotos() {
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedTourist);
		List<User> users = User.getUsersWithPhotos(photos);
		List<User> newus = User.parseUserTimeZone(users);
		photos.clear();
		for (User u: newus) {
			//u.updatePhotosTime();
			photos.addAll(u.photosList);
		}
		List<String> ids = new ArrayList<String>();
		List<String> camera = Arrays.asList("canon", "nikon", "sony", "pentax", "olympus", "panasonic", 
				"casio", "fuji", "leica", "kodak", "dslr", "hipstamatic", "sigma", "ricoh", "konica", "agfaphoto", "noritsu");
			//	"apple", "samsung", "huawei", "htc", "xiaomi", "nokia", "oppo", "lumia", "nubia", "google", "nexus", 
			//	"android", "meizu", "blackberry", "lg electronics", "oneplus");
		//List<String> phone = Arrays.asList("apple");
		List<String> needupdate = new ArrayList<String>();
		for (int i = 0; i < photos.size(); i += 1) {
			Photo p = photos.get(i);
			String temp = getPhotoManufacturer(p.id);
			String lower = temp.toLowerCase();
			int j = 0;
			for (j = 0; j < camera.size(); j += 1) {
				if (lower.contains(camera.get(j))) {
					needupdate.add(p.id);
					break;
				}
			}
			if (j == camera.size() && temp.length() > 0) {
				//System.out.println(temp);
			}
		}
		for (Photo p: photos) {
			ids.add(p.id);
			//System.out.println();
		}
		//downPhotosExifMultiThreads(ids);
		FileUtil.NewFile(Photo.cameraIDPath, needupdate);
	}

	public static void main(String[] args) throws IOException
	{
		List<String> ids= FileUtil.getLinesFromFile(Photo.pidPath);
		//downPhotosExifMultiThreads(ids);
		downloadForeignPhotos();
	}

}
