package down;

import java.util.List;

import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;
import entity.Photo;

public class DownPhotoFavorite
{
	public static String method="flickr.photos.getFavorites";
	
	public static String getPhotoFavoriteUrl(String photoId){
		String url=DownPhotoInfo.base
				+"?method="+method
				+"&api_key="+DownPhotoInfo.apiKey
				+"&photo_id="+photoId
				+"&per_page=50"
				+"&page=1";
		
		return url;
	}
	
	public static String getFavoriteSavePath(String photoId, int page){
		String path=Photo.photoFavoriteDir+photoId+"_"+page+".txt";
		return path;
	}
	
	public static void test1(){
		
		String photoId="5622324726";
		String url=DownPhotoFavorite.getPhotoFavoriteUrl(photoId);
		System.out.println(url);
		String path=DownPhotoFavorite.getFavoriteSavePath(photoId, 1);
		FileUtil.deleteFile(path);
		String out=HttpHelper.testAndGetContent(path, url);
		System.out.println(out);
	}
     
	
	//使用一个线程下载数据
	public static void downPhotosFavorite(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.pidPath);
		System.out.println("需要下载"+ids.size());
		for(int i=0; i<ids.size(); ++i){
			String id=ids.get(i);
			System.out.println(i+","+id);
			String url=DownPhotoFavorite.getPhotoFavoriteUrl(id);
			String path=DownPhotoFavorite.getFavoriteSavePath(id, 1);
			HttpHelper.testAndGetContent(path, url);
		}
	}
	
	
	
	public static void downPhotosFavoriteMultiThreads(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.pidPath);
		System.out.println("需要下载"+ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){

			@Override
			public void ProcessUrl(String url)
			{
				url=DownPhotoFavorite.getPhotoFavoriteUrl(url);
				String path=DownPhotoFavorite.getFavoriteSavePath(url, 1);
				HttpHelper.testAndGetContent(path, url);		
			}
			
		});
	}
	
	public static void main(String[] args){
	//	test1();
	//	downPhotosFavorite();
		downPhotosFavoriteMultiThreads();
	}

}