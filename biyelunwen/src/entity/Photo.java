package entity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import draw.KmlFile;
import entity.filter.GeoFilter;
import entity.filter.TimeFilter;
import entity.filter.TimeFilter.TimeType;
import sciencecluster.GeoBlock;
import sciencecluster.MyPoint;
import sciencecluster.ScienceCluster;
import trajectory.MyPointWithTime;
import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;


public class Photo implements Comparable<Photo>
{
	public static String workDir = "G:/ASR/school/data/biye/";
	
	
	public static String statisticDir = workDir + "statistic/";
	public static String userPhotoFromInfoStatistic = statisticDir + "user_photo_from_info_count.txt";
	public static String userPhotoAllStatistic = statisticDir + "user_all_count.txt";
	public static String userPhotoGeoStatistic = statisticDir + "user_geo_all.txt";
	
	public static String photoIDsDir = workDir + "photo-id-files/";
	public static String photoInfoDir = workDir + "photo-info-files/";
	public static String userPhotosDir = workDir + "user-photos-files/";
	public static String userMergePhotosDir = workDir + "user-photos-merge-files/";
	public static String userMergePhotosGeo = workDir + "user-photos-merge-geo/";
	public static String userInfoDir = workDir + "user-info-files/";
	
	
	public static String photoFavoriteDir = workDir + "favorite/";
	public static String photoSizeDir = workDir + "size/";
	public static String photoContentDir = workDir + "content/";
	public static String clusterDir = workDir + "cluster/";
	
	public static String pidPath = workDir + "photo_ids.txt";
	public static String photoSelectedBasicInfoPath = workDir + "photo_selected_info.txt";
	public static String photoSelectedBeijingUser = workDir + "photo_selected_beijing_user.txt";
	public static String photoSelectedNotBeijingUser = workDir + "photo_selected_not_beijing_user.txt";
	
	public static String photoSelectedBeijingHotSpots = workDir + "photo_selected_beijing_hot_spots";
	public static String photoSelectedNotBeijingHotSpots = workDir + "photo_selected_not_beijing_hot_splots";
	public static String photoSelectedAllHotSpots = workDir + "photo_selected_all_hot_spots.txt";
	
	
	public static String userBeijingIDs = workDir + "user_beijing_ids.txt";
	public static String userLocations = workDir + "user_locations.txt";
	
	public static String pidUidPath = workDir + "pid_uid.txt";
	public static String photoIdImgFile = workDir + "pid_img.txt";
	public static String photoIdTagsFile = workDir + "pid_tags.txt";
	public static String photoIdImgTagsFile = workDir + "pid_img_tags.txt";
	public static String photoBasicInfoPath = workDir + "photo_info.txt";
	
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
		return DateUtil.getDateField(this.dateTaken, df) + "";
	}
	
	public static Map<String, Integer> countDateFields(List<Photo> photos, DateUtil.DateField df){
		List<String> fs = new ArrayList<String>();
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
			//photos.get(i).id=i+"";
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
		MyPoint mp = new MyPoint();
		mp.userId = p.userId;
		mp.x = p.longitude;
		mp.y = p.latitude;
		mp.label = DateUtil.notSafeSdf.format(p.dateTaken);
		return mp;
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
	
	/****************************************              毕业论文               ***************************/
	//从文件中读取照片信息，返回一个列表
	public static List<Photo> getPhotos(String path){
		String splitString=",";
		List<Photo> photos = new ArrayList<Photo>();
		List<String> lines = FileUtil.getLinesFromFile(path);
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
		//System.out.println("使用读取所有照片函数，照片数量为：" + photos.size());
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
	
	public static List<String> getUniqueUsers(List<Photo> photos) {
		Set<String> users = new HashSet<String>();
		for(Photo p : photos) {
			users.add(p.userId);
		}
		return new ArrayList<String>(users);
	}
	
	public static List<Photo> getPhotosWithGeo(List<Photo> photos) {
		List<Photo> ps = new ArrayList<Photo>();
		for (Photo p: photos) {
			if (NumberUtil.isZero(p.latitude) && NumberUtil.isZero(p.longitude)) {
				continue;
			}
			ps.add(p);
		}
		return ps;
	}

	public static List<MyPoint> getPoints(List<Photo> photos){
		List<MyPoint> mps=new ArrayList<MyPoint>();
		for(Photo p: photos){
			mps.add(Photo.getPoint(p));
		}
		return mps;
	}
	
	//使用该方法对单个用户的照片列表进行聚类，得到他的居住地
	public static MyPoint clusterPhotosAndGetLargestGeo(List<Photo> photos) {
		GeoFilter.Area area = GeoFilter.areaWorld;
		List<MyPoint> mps = Photo.getPoints(photos);
		GeoBlock.setStaticParameter(area);
		List<GeoBlock> blocks = GeoBlock.getBlockWithReadyParameter(mps);
		List<MyPoint> cps = GeoBlock.getMyPoints(blocks);
		
		double clusterR = area.clusterR;//用来设置局部密度的截断距离
		int clusterNum = area.clusterNum;//聚类的数量
		double rate = 0.5;//平均密度比率
		MyPoint.mpw = new MyPoint.MyPointSelfWeight();//设置开启数据点的权重
		MyPoint.mpd = new MyPoint.MyPointCoordinateDistance();//使用坐标距离
		
		ScienceCluster sc=new ScienceCluster(cps);
		sc.initCluster(clusterR);
		//sc.showLocalDensity();
		sc.cluster(clusterNum, clusterR, rate);

		List<MyPoint> used = new ArrayList<MyPoint>();
		for(MyPoint mp: cps){
			if(Integer.parseInt(mp.clusterId)>-1)
				used.add(mp);
		}
		List<MyPoint> first = sc.clusters.get(1);
		double x = 0, y = 0, count = 0;
		for (MyPoint mp : first) {
			String[] ss = mp.label.split(",");
			x += Double.parseDouble(ss[0]) * mp.getPointWeight();
			y += Double.parseDouble(ss[1]) * mp.getPointWeight();
			count += mp.getPointWeight();
		}
		x /= count;
		y /= count;
		System.out.println("聚类中心：" + x + "," +y);
		return new MyPoint(x, y);
		//System.out.println("聚类后还剩下："+used.size());
		//KmlFile.writeClusterResult(DateUtil.getTodayMonthDay()+"_" + clusterR + "_" + clusterNum, used);
	}

	public static List<Photo> selectOne(List<Photo> photos, int num) {
		List<Photo> ps = new ArrayList<Photo>();
		for (Photo p: photos) {
			int rand = NumberUtil.getRandom(num);
			if (rand == num - 1) {
				ps.add(p);
			}
		}
		return ps;
	}

	public static void writePhotoListHeatFile(List<Photo> photos, GeoFilter.Area area, String name) {
		List<MyPoint> mps = Photo.getPoints(photos);
		GeoBlock.setStaticParameter(area);
		List<GeoBlock> blocks = GeoBlock.getBlockWithReadyParameter(mps);
		List<String> lines = GeoBlock.getPointToBlockWeight(blocks);
		FileUtil.NewFile(KmlFile.saveFolder + name + ".geoblocks", lines);
		
		List<Photo> selected = Photo.selectOne(photos, 100);
		List<MyPoint> smps = Photo.getPoints(selected);
		KmlFile.writeMyPoint(name, smps);
	}
	 
	
	public static void main(String[] args){
	//	showDateField();
		mergePhotoInfo();
	//	mergePhotoImgWithTag();
	}
}
