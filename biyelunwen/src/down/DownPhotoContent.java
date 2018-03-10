package down;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entity.Photo;
import entity.User;
import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;

public class DownPhotoContent
{

	public static String method="flickr.photos.getSizes";
//https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&photo_id=14439128899&api_key=822eb51ecd570ddfc1ebcd061b2c20f3
	
	public static String getPhotoSizeUrl(String photoId){
		String url=DownPhotoInfo.base
				+"?method="+method
				+"&api_key="+DownPhotoInfo.apiKey
				+"&photo_id="+photoId;		
		return url;
	}
	
	public static String getPhotoSizeSavePath(String photoId){
		String path=Photo.photoSizeDir+photoId+".txt";
		return path;
	}
	
	public static String getPhotoContentSavePath(String url){
		String name=url.substring(url.lastIndexOf("/")+1);
		return Photo.photoContentDir+name;
	}
	
	public static void test1(){
		String photoId="14439128856";
		String url=DownPhotoContent.getPhotoSizeUrl(photoId);
		System.out.println(url);
		String path=DownPhotoContent.getPhotoSizeSavePath(photoId);
		FileUtil.deleteFile(path);
		String out=HttpHelper.testAndGetContent(path, url);
		System.out.println(out);
	}
	
	//使用一个线程下载数据
	public static void downPhotoSize(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.workDir+"gugongphotoid.txt");
		System.out.println("需要下载"+ids.size());
		for(int i=0; i<ids.size(); ++i){
			String id=ids.get(i);
			System.out.println(i+","+id);
			String url=DownPhotoContent.getPhotoSizeUrl(id);
			String path=DownPhotoContent.getPhotoSizeSavePath(id);
			HttpHelper.testAndGetContent(path, url);
		}
	}
	
	public static void downPhotoContent(){
		List<String> lines= FileUtil.getLinesFromFile(Photo.workDir+"photo_sizeM.txt");
		System.out.println("需要下载"+lines.size());
		for(String line: lines){
			String[] ss=line.split(",");
			String path=DownPhotoContent.getPhotoContentSavePath(ss[1]);
			HttpHelper.downloadFromUrl(ss[1], path, 10);
		}
	}
	
	
	public static void downPhotoSizeMultiThreads(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.workDir+"gugongphotoid.txt");
		System.out.println("需要下载"+ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				url=DownPhotoContent.getPhotoSizeUrl(url);
				String path=DownPhotoContent.getPhotoSizeSavePath(url);
				HttpHelper.testAndGetContent(path, url);
			}			
		});
	}
	
	public static void downPhotoContentMultiThreads(){
		List<String> lines= FileUtil.getLinesFromFile(Photo.workDir+"photo_sizeM.txt");
		System.out.println("需要下载"+lines.size());
		List<String> content=new ArrayList<String>();
		for(String line: lines){
			String[] ss=line.split(",");
			content.add(ss[1]);
		}		
		MyThread.processMultiStage(content, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				String path=DownPhotoContent.getPhotoContentSavePath(url);
				HttpHelper.downloadFromUrl(url, path, 10);
			}
		});
	}
	
	public static void main(String[] args){
	//	test1();
	//	downUsersInfo();
		downPhotoContent();
	//	downPhotoContentMultiThreads();
	}
}
