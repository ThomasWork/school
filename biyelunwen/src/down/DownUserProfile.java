package down;

import java.util.List;

import myutil.fileprocess.FileUtil;
import myutil.net.HttpHelper;
import entity.Photo;
import entity.User;

public class DownUserProfile
{
	public static String method="flickr.profile.getProfile";
	public static String getUserProfileUrl(String userId){
		String url=DownPhotoInfo.base
				+"?method="+method
				+"&api_key="+DownPhotoInfo.apiKey
				+"&user_id="+userId;		
		return url;
	}
	
	public static String getUserProfileSavePath(String userId){
		String path=Photo.userProfileDir+userId+".txt";
		return path;
	}
	
	public static void downloadProfile() throws Exception {
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedNotBeijingUser);
		List<User> users = User.getUsersWithPhotos(photos);
		for (int i = 0; i < users.size(); i += 1) {
			String url = getUserProfileUrl(users.get(i).id);
			String path = getUserProfileSavePath(users.get(i).id);
			HttpHelper.testAndGetContent(path, url);
		}
	}
	
	public static void main(String[] args) throws Exception {
		downloadProfile();
	}
}
