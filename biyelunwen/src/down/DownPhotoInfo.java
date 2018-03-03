package down;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entity.Photo;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;

public class DownPhotoInfo
{
	public static String base="https://api.flickr.com/services/rest/";
	public static String apiKey="822eb51ecd570ddfc1ebcd061b2c20f3";
	

	public static String method="flickr.photos.getInfo";
	
	
	public static void test1(String photoId){
		String url=base+"?method="+method+"&api_key="+apiKey+"&photo_id="+photoId;
		System.out.println(url);
		FileUtil.deleteFile("temp.txt");
		String temp=myutil.net.HttpHelper.testAndGetContent("temp.txt", url);
		System.out.println(temp);
	}
	
	//合并一个文件夹里面的id
	public static void mergePhotoIds(){
		List<String> id1= FileUtil.getFolderFilesLines("C:/Users/Admin/Desktop/jar/Flickr4Java-master/Flickr4Java/month");
		System.out.println(id1.size());
		List<String> id2=StringUtil.getUnique(id1);
		FileUtil.NewFile(Photo.pidPath, id2);
	}
	
	//使用一个线程下载数据
	public static void downPhotos(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.pidPath);
		System.out.println("需要下载"+ids.size());
		for(int i=0; i<ids.size(); ++i){
			String id=ids.get(i);
			System.out.println(i+","+id);
			String url=base+"?method="+method+"&api_key="+apiKey+"&photo_id="+id;
			String path=Photo.photoInfoDir+id+".txt";
			HttpHelper.testAndGetContent(path, url);
		}
	}
	
	public static void downPhotosMultiThreads(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.pidPath);
		System.out.println("需要下载"+ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				url=DownPhotoInfo.base+"?method="+DownPhotoInfo.method+"&api_key="+DownPhotoInfo.apiKey+"&photo_id="+url;
				String path=Photo.getPhotoInfoFilePath(url);
				HttpHelper.testAndGetContent(path, url);	
			}
		});
	}
	
	public static void main(String[] args){
		test1("31370068340");
	//	downPhotos();
	//	downPhotosMultiThreads();
	}

}
