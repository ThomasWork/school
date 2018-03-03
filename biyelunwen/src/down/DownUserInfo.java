package down;

import java.util.List;

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
		List<String> ids= FileUtil.getLinesFromFile(User.usersIdPath);
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
		List<String> ids= FileUtil.getLinesFromFile(User.usersIdPath);
		System.out.println("需要下载"+ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				url=DownUserInfo.getUserInfoUrl(url);
				String path=DownUserInfo.getUserInfoSavePath(url);
				HttpHelper.testAndGetContent(path, url);
			}
		});
	}
	
	public static void main(String[] args){
		test1();
	//	downUsersInfo();
	//	downUsersInfoMultiThreads();
	}

}
