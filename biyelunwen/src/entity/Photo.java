package entity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import entity.filter.GeoFilter;
import entity.filter.TimeFilter;
import entity.filter.TimeFilter.TimeType;
import sciencecluster.MyPoint;
import trajectory.MyPointWithTime;
import myutil.DateUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;


public class Photo implements Comparable<Photo>
{
	public static String workDir = "G:/ASR/school/data/biye/";
	public static String photoIDsDir = workDir + "photo-id-files/";
	public static String photoTempDir = workDir + "photo-temp-files/";
	public static String photoInfoDir = workDir + "photo-info-files/";
	public static String photoFavoriteDir = workDir + "favorite/";
	public static String photoSizeDir = workDir + "size/";
	public static String photoContentDir = workDir + "content/";
	public static String userInfoDir = workDir + "userinfo/";
	public static String userPhotosDir = workDir + "user-photos-files/";
	public static String clusterDir = workDir + "cluster/";
	
	
	public static String pidUidPath = workDir + "pid_uid.txt";
	public static String pidPath = workDir + "photo-ids.txt";
	public static String photoIdImgFile = workDir + "pid_img.txt";
	public static String photoIdTagsFile = workDir + "pid_tags.txt";
	public static String photoIdImgTagsFile = workDir + "pid_img_tags.txt";
	public static String photoBasicInfoPath = workDir + "photo_info.txt";
	public static String photoSelectedBasicInfoPath = workDir + "photo_selected_info.txt";
	public static String beijingUserIDs = workDir + "user_beijing_ids.txt";
	
	public static String allUserPhotosID = workDir + "user_photo_ids.txt";
	
	public String id;
	public String name;
	public String description;
	
	public double longitude;
	public double latitude;
	
	public int favoriteNum;//表示favorite这张照片的用户
	
	public Date dateTaken;
	public String userId;
	public User user;

	public Photo(String idPar){
		this.id = idPar;
	}
	
	@Override
	public int compareTo(Photo p)
	{
		return this.dateTaken.compareTo(p.dateTaken);
	}
	
	public String getDateString(DateUtil.DateField df){
		return DateUtil.getDateField(this.dateTaken, df)+"";
	}
	
	public static Map<String, Integer> countDateFields(List<Photo> photos, DateUtil.DateField df){
		List<String> fs=new ArrayList<String>();
		for(Photo p: photos){
			fs.add(p.getDateString(df));
		}
		return StringUtil.countFrequencyWithNumber(fs);
	}
	
	@Override
	public String toString() {
		return this.id + "," + this.userId + "," + this.latitude + "," + this.longitude + "," + DateUtil.getDateString(this.dateTaken);
	}
	
	
	/********************************************************************************************
	 * 下面的函数为针对照片列表进行的操作
	 */
	//对照片进行排序
	public static void sortPhotos(List<Photo> photos){
		Collections.sort(photos);
		for(int i=0; i<photos.size(); ++i){
			photos.get(i).id=i+"";
		}
	}	
	
	//获得一个照片列表中拍摄时间相隔最大的两张照片，该函数会将列表排序
	public static double getMaxDayDis(List<Photo> photos){
		Collections.sort(photos);
		for(Photo p: photos){
	//		System.out.println(DateUtil.sdf.format(p.dateTaken));
		}
		int size=photos.size();
		return DateUtil.getDateDisDay(photos.get(size-1).dateTaken, photos.get(0).dateTaken);
	}
	//获得一个照片列表中拍摄时间相隔最大的两张照片，该函数会将列表排序
	public static double getMaxHourDis(List<Photo> photos){
		Collections.sort(photos);
		for(Photo p: photos){
	//		System.out.println(DateUtil.sdf.format(p.dateTaken));
		}
		int size=photos.size();
		if(0==size)
			return 0;
		return DateUtil.getDateDisHour(photos.get(size-1).dateTaken, photos.get(0).dateTaken);
	}
	
	/****************************************************************************************
	 * 针对单张照片进行的操作
	 */
	//得到保存照片信息的路径
	public static String getPhotoInfoFilePath(String id){
		String path=Photo.photoInfoDir + id + ".txt";
		return path;
	}
	
	/********************************************************************************************
	 * 获得照片列表的操作
	 */
	//从文件中读取照片信息，返回一个列表
	public static List<Photo> getPhotos(String path){
		String splitString=",";
		List<Photo> photos = new ArrayList<Photo>();
		List<String> lines = FileUtil.getLinesFromFile(path);
		System.out.println(lines.size());
		for(int i = 0; i < lines.size(); ++i){
			String line=lines.get(i);
			String[] ss=line.split(splitString);
			Photo p=new Photo(ss[0]);
			p.userId=ss[1];
			p.latitude=Double.parseDouble(ss[2]);
			p.longitude=Double.parseDouble(ss[3]);
			p.dateTaken = DateUtil.getDate(ss[4]);
	//		p.favoriteNum=Integer.parseInt(ss[5]);
			photos.add(p);
		}
		System.out.println("使用读取所有照片函数，照片数量为：" + photos.size());
		return photos;
	}
	
	public static List<Photo> getPhotosOfBeijing() {
		List<Photo> photos = getPhotos(Photo.photoBasicInfoPath);
		photos = GeoFilter.getPhotosInArea(photos, GeoFilter.areaBeijing);
		TimeType tf = new TimeFilter.StartEndFilter(DateUtil.getDate("2008-01-01 00:00:00"), DateUtil.getDate("2018-01-01 00:00:00"));
		photos = TimeFilter.filterPhoto(photos, new TimeFilter(tf));
		return photos;
	}
	
	public static void savePhotos(List<Photo> photos, String path) {
		List<String> content = new ArrayList<String>();
		for (Photo p : photos) {
			content.add(p.toString());
		}
		FileUtil.NewFile(path, content);
	}
	

	//得到照片拍摄时间的特定的域
	public static void showDateField(){
		List<Photo> photos=Photo.getPhotos(Photo.photoBasicInfoPath);
		Map<String, Integer> dateC = Photo.countDateFields(photos, DateUtil.DateField.month);
		for(Map.Entry<String, Integer> entry: dateC.entrySet()){
			System.out.println(entry.getKey()+","+entry.getValue());
		}
	}
	
	//根据照片的id合并照片信息
	public static void mergePhotoInfo(){
		List<String> big=FileUtil.getLinesFromFile(Photo.photoBasicInfoPath);
		List<String> small=FileUtil.getLinesFromFile(Photo.workDir + "pid_favorite.txt");
		List<String> all=StringUtil.mergeLine(big, small, ",");
		FileUtil.NewFile(Photo.workDir+"fall.txt", all);
	}
	
	//将照片的图片链接和标签合并
	public static void mergePhotoImgWithTag(){
		List<String> imgs=FileUtil.getLinesFromFile(Photo.photoIdImgFile);
		List<String> tags=FileUtil.getLinesFromFile(Photo.photoIdTagsFile);
		List<String> out=StringUtil.mergeLine(imgs, tags, ",");
		FileUtil.NewFile(Photo.photoIdImgTagsFile, out);
	}
	
	public static MyPoint getPoint(Photo p){
		MyPoint mp=new MyPoint();
		mp.userId=p.userId;
		mp.x=p.longitude;
		mp.y=p.latitude;
		return mp;
	}
	
	public static List<MyPoint> getPoints(List<Photo> photos){
		List<MyPoint> mps=new ArrayList<MyPoint>();
		for(Photo p: photos){
			mps.add(Photo.getPoint(p));
		}
		return mps;
	}
	
	public static List<MyPointWithTime> getPointsWithTime(List<Photo> photos){
		List<MyPointWithTime> mps=new ArrayList<MyPointWithTime>();
		for(Photo p: photos){
			MyPoint mp=new MyPoint();
			mp.userId=p.userId;
			mp.x=p.longitude;
			mp.y=p.latitude;
			mps.add(new MyPointWithTime(p.dateTaken, mp));
		}
		return mps;
	}
	
	public static void main(String[] args){
	//	showDateField();
		mergePhotoInfo();
	//	mergePhotoImgWithTag();
	}
}
