package entity;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import sciencecluster.GeoBlock;
import sciencecluster.MyPoint;
import trajectory.GeoTrajectory;
import trajectory.MyPointWithTime;
import trajectory.TimeTrajectory;
import trajectory.UserGeoTrajectory;
import down.DownUserPhotos;
import draw.KmlFile;
import entity.filter.GeoFilter;
import entity.filter.PhotoFilter;
import entity.filter.TimeFilter;
import entity.location.GetCountry;
import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;

//Ctrl+F11可以执行当前文件

public class User
{
	public String id;
	public int totalImg = -1;
	public List<Photo> photosList=new ArrayList<Photo>();
	public List<Photo> selected;
	
	public String sourceLocation;
	public String country;
	public String wordPart;
	public int timeZone;
	
	public boolean isBeijing;
	public MyPoint location;
	
	
	public static final int BeijingTimeZone=8;
	
	public User(String idPar)
	{
		this.id=idPar;
		this.sourceLocation="unknown";
	}
	
	public User(String[] info){
		this.id=info[0];
		this.sourceLocation=info[1];
		this.timeZone=Integer.parseInt(info[2]);
		this.country=GetCountry.getCountry.getCountry(this.sourceLocation);
		this.wordPart=GetCountry.getWordPart.getCountry(this.country);
		this.isBeijing=this.setIsBeijing(this.sourceLocation);
	}
	
	private boolean setIsBeijing(String content){
		content=StringUtil.stringToLowerCase(content);
		String[] mark={"北京", "beijing"};
		for(int i=0; i<mark.length; ++i){
			if(content.contains(mark[i]))
				return true;
		}
		return false;
	}
	
	//更新照片的时间
	private void updatePhotosTime() {
		if(this.timeZone<0)
			return;
		for(Photo p: this.photosList){
		//	if(this.timeZone != 8)
		//	System.out.println("时区："+this.timeZone+"时间："+DateUtil.sdf.format(p.dateTaken));
			
			int zoneDis = - (this.timeZone-User.BeijingTimeZone);//已知区时的时区减去待计算区时的时区，这里取了负数
			p.dateTaken=DateUtil.dateUpdate(p.dateTaken, DateUtil.DateField.hour, zoneDis);
	//		DateUtil.show(p.dateTaken);
		}
	}
	
	public void drawSpecificRoutes(GeoFilter.Area area) {
		this.photosList = GeoFilter.getPhotosInArea(this.photosList, area);
		this.drawRoutes();
	}
	
	public void drawRoutes() {
		Photo.sortPhotos(this.photosList);//进行排序
		List<MyPoint> mps = Photo.getPoints(this.photosList);
		KmlFile.writeMyPoint(this.id + "_" + mps.size(), mps);
	}
	
	public void getLocationByCluster() {
		List<Photo> ps = Photo.getPhotosWithGeo(this.photosList);
		if (this.photosList.size() > 50 && this.getDateDis() > 365 * 24) {
			this.location = Photo.clusterPhotosAndGetLargestGeo(ps);
		}
	}
	
	
	public double getDateDis() {
		Photo start = this.photosList.get(0);
		Photo last = this.photosList.get(this.photosList.size() - 1);
		return DateUtil.getDateDisHour(last.dateTaken, start.dateTaken);
	}
	
	public List<Double> getSequenceDateDis() {
		Photo.sortPhotos(this.photosList);
		List<Double> hours = new ArrayList<Double>();
		for (int i = 1; i < this.photosList.size(); i += 1) {
			Date pre = this.photosList.get(i - 1).dateTaken;
			Date cur = this.photosList.get(i).dateTaken;
			double time = DateUtil.getDateDisHour(cur, pre);
			if (time <= 0.00000001) {
				//System.out.println(this.photosList.get(i) + "," + this.photosList.get(i - 1));
			}
			hours.add(time);
		}
		return hours;
	}
	
	public List<String> getDateString(DateUtil.DateField df)
	{
		List<String> output=new ArrayList<String>();
		for(Photo img: this.photosList)
		{
			output.add(img.getDateString(df));
		}
		return output;
	}
	
	//首先获得用户的信息，然后把照片添加到用户的照片列表中
	public static List<User> getUsersWithPhotos(){
		List<Photo> photos=Photo.getPhotos(Photo.photoBasicInfoPath);
		return getUsersWithPhotos(photos);
	}
	
	public static List<User> getUsersWithPhotos(List<Photo> photos){
		List<User> users = User.setPhotoUsersAndGetUsers(photos);
		for(User u: users){
		//	u.updatePhotosTime();
		}
		System.out.println("使用带有照片信息的用户，数量为："+users.size());
		return users;
	}
	
	//设置照片的用户并且返回这些用户
	public static List<User> setPhotoUsersAndGetUsers(List<Photo> photos){
		List<User> users=new ArrayList<User>();
		Map<String, User> selected = new HashMap<String, User>();
		for(Photo p: photos) {
			User u = selected.get(p.userId);
			if(null==u){
				u = new User(p.userId);
				selected.put(p.userId, u);//这里作为选中的
			}
			p.user = u;
			u.photosList.add(p);
		}		
		for(Map.Entry<String, User> entry: selected.entrySet()){
			users.add(entry.getValue());
		}
		return users;
	}
	
	public static List<User> getUsersWithFromPlace(){
		List<User> users = User.getUsersWithPhotos();
		List<User> selected=new ArrayList<User>();
		for(int i=0; i<users.size(); ++i){
			User u=users.get(i);
		//	System.out.println(i+"\t"+u.id+"\t"+u.sourceLocation);
			if(!u.sourceLocation.equals("unknown"))//如果有位置信息
				selected.add(u);
		}
		System.out.println("使用带有位置信息的用户，数量为："+selected.size());
		return selected;
	}
	
	//获得时间经过矫正的照片
	private static List<Photo> getPhotosWithCorrectTimeOld(){
		List<Photo> photos=Photo.getPhotos(Photo.photoBasicInfoPath);
		List<User> users = User.setPhotoUsersAndGetUsers(photos);
		photos.clear();
		for(User u: users){
		//	u.updatePhotosTime();
		//	if(u.timeZone>=0)//如果可以矫正，则加入
		//		photos.addAll(u.photosList);
		}
		System.out.println("使用纠正过时间的数据："+photos.size());
		return photos;
	}
	
	/**************************************************************************************************************************
	 * 对用户列表进行的整体操作
	 */
	//画每个用户走过的路径
	public static void drawUsersRoutes(){
		List<User> users=User.getUsersWithPhotos();
		System.out.println(users.size());
		for(User u: users){
			if(u.photosList.size()>30)
			{
				u.drawSpecificRoutes(GeoFilter.areaBeijing);
			}
		}
	}
	//统计用户有多少照片
	public static int countUserPhotos(List<User> users){
		int sum=0;
		for(User u: users){
			sum+=u.photosList.size();
		}
		return sum;
	}
	//画用户在特定区域的路径
	public static void drawUsersSelected(){
		List<User> users=User.getUsersWithPhotos();
		System.out.println("用户数："+users.size());
		for(User u: users){
			u.drawSpecificRoutes(GeoFilter.areaBeijing);
		}
	}
	
	//统计游客的信息包含他们来自哪里，以及他们的照片数量有多少
	public static Map<String, Integer> countUserData(List<User> users){
		Map<String, Integer> count=new TreeMap<String, Integer>();
		
		for(User u: users){
			String part=u.wordPart;
			if(null==part)
				continue;
			Integer t=count.get(part);
			int increase=1;//u.photosList.size();
			if(null==t){
				count.put(part, increase);
			}else{
				count.put(part, t+increase);
			}
		}
	//	for(Map.Entry<String, Integer> entry: count.entrySet()){
	//		System.out.println(entry.getKey()+","+entry.getValue());
	//	}
		return count;
	}
	
	//将用户的游览的地理位置的栅格坐标输出出来
	public static void writeUserVisitBlockIndex()
	{
	//	List<Photo> photos=Photo.getPhotos();
		List<Photo> photos = Photo.getPhotosOfBeijing();
		List<User> users = User.setPhotoUsersAndGetUsers(photos);
		GeoBlock.setStaticParameter(Photo.getPoints(photos), 100, 100);//首先设置基本参数
	//	KmlFile.writeTrajectories(GeoTrajectory.getTrajectorysFromUser(users.get(14), 100000), "15号用户");
		List<String> output=new ArrayList<String>();
		for(User u: users){
			StringBuilder sb=new StringBuilder("");
			List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectorysFromUser(u, 48);
			if(tras.size()>3)//如果超过1条则跳过
				continue;
			List<String> blocks=GeoBlock.getBlockIndexesWithReadyParameters(Photo.getPoints(u.photosList));
			for(String block: blocks){
				sb.append(block+",");
			}
			output.add(sb.toString());
		}
		output.add("0,0");
		FileUtil.NewFile("indexPath.txt", output);
	}
	
	//将每个用户的多条轨迹分别列出来
	public static void writeUserEachTrajectoryVisitBlockIndex()
	{
	//	List<Photo> photos=Photo.getPhotos();
		List<Photo> photos = Photo.getPhotosOfBeijing();
		List<User> users = User.setPhotoUsersAndGetUsers(photos);
		GeoBlock.setStaticParameter(Photo.getPoints(photos), 100, 100);//首先设置基本参数
	//	KmlFile.writeTrajectories(GeoTrajectory.getTrajectorysFromUser(users.get(14), 100000), "15号用户");
		List<String> output=new ArrayList<String>();
		for(User u: users){
			List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectorysFromUser(u, 48);
			if(tras.size()<=3)//如果超过1条则跳过
				continue;
			output.add(tras.size()+"");//轨迹的数目
			for(GeoTrajectory tra: tras){
				StringBuilder sb=new StringBuilder("");
				List<String> blocks=GeoBlock.getBlockIndexesWithReadyParameters(MyPointWithTime.getMyPointList(tra.points));
				for(String block: blocks)
					sb.append(block+",");
				output.add(sb.toString());
			}
		}
		output.add("0");
		FileUtil.NewFile("indexPath.txt", output);
	}
	
	//写当地住户
	public static void writeLocalUserEachTrajectoryVisitBlockIndex(){
		int minTraNum=4;//最小的轨迹数目
		List<Photo> photos = Photo.getPhotosOfBeijing();
		List<User> users = User.setPhotoUsersAndGetUsers(photos);
		GeoBlock.setStaticParameter(Photo.getPoints(photos), 100, 100);//首先设置基本参数
	//	KmlFile.writeTrajectories(GeoTrajectory.getTrajectorysFromUser(users.get(14), 100000), "15号用户");
		List<String> output=new ArrayList<String>();
		for(User u: users){
			List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectorysFromUser(u, 48);
			if(tras.size()<minTraNum)//没有达到标准，则继续
				continue;
			tras=null;//UserGeoTrajectory.getTrajectorysFromPhotosListWithNextDay(u.id, u.photosList);
			output.add(tras.size()+"");//轨迹的数目
			for(GeoTrajectory tra: tras){
				StringBuilder sb=new StringBuilder("");
				List<String> blocks=GeoBlock.getBlockIndexesWithReadyParameters(MyPointWithTime.getMyPointList(tra.points));
				for(String block: blocks)
					sb.append(block+",");
				output.add(sb.toString());
			}
		}
		output.add("0");
		FileUtil.NewFile("indexPath.txt", output);
	}
	
	//获取用户每一天的拍照时间
	public Map<Integer, Map<String, Integer>> getUserPhotoTime(){
		Map<Integer, List<String>> tempMap=new TreeMap<Integer, List<String>>();
		for(Photo p: this.photosList){
			int day=DateUtil.getDateField(p.dateTaken, DateUtil.DateField.year_month_day);
			int hour=DateUtil.getDateField(p.dateTaken, DateUtil.DateField.hour);
			List<String> hours=tempMap.get(day);
			if(null==hours){
				hours=new ArrayList<String>();
				tempMap.put(day, hours);
			}
			hours.add(hour+"");
		}
		
		Map<Integer, Map<String, Integer>> result=new TreeMap<Integer, Map<String, Integer>>();//LinkedHashMap
		for(Map.Entry<Integer, List<String>> entry: tempMap.entrySet()){
		//	System.out.println(entry.getKey()+"\t"+entry.getValue());
			result.put(entry.getKey(), StringUtil.countFrequencyWithNumber(entry.getValue()));
		}
		if(result.size()!=2)//这里只是作为显示的时候方便
			return result;

		System.out.println(this.id+":"+result.size());
		for(Map.Entry<Integer, Map<String, Integer>> entry: result.entrySet()){
			System.out.println(entry.getKey()+"\t");
			System.out.println(entry.getValue());
		}
		return result;
	}
	
	/****************************                   毕业论文                 ***************************************/
	public static List<String> getUserIds() {
		List<String> ids = FileUtil.getLinesFromFile(Photo.userBeijingIDs);
		//ids.clear();
		//ids.add("101212512@N07");
		//return ids.subList(0, 2000);
		return ids;
	}
	
	public static List<User> getUsersWithHisAllPhoto2() {
		List<String> ids = getUserIds();
		List<User> us = new ArrayList<User>();
		for (int i = 0; i < ids.size(); i += 1) {
			if (i % 100 == 0) {
			System.out.println(i + ", " + ids.get(i));
			}
			String path = DownUserPhotos.getUserMergePhotosPath(ids.get(i));
			User u = new User(ids.get(i));
			u.photosList = Photo.getPhotos(path);
			us.add(u);
		}
		return us;
	}
	
	public static List<User> getUsersWithHisPhotoGeo() {
		List<String> ids = getUserIds();
		List<User> us = new ArrayList<User>();
		for (int i = 0; i < ids.size(); i += 1) {
			if (i % 100 == 0) {
			System.out.println(i + ", " + ids.get(i));
			}
			String path = DownUserPhotos.getUserGeoPhotoPath(ids.get(i));
			User u = new User(ids.get(i));
			u.photosList = Photo.getPhotos(path);
			us.add(u);
		}
		return us;
	}
	
	public static void writeUserLocations() {
		List<String> locations = new ArrayList<String>();
		List<User> users = getUsersWithHisPhotoGeo();
		for (User u: users) {
			u.getLocationByCluster();
			if (null != u.location) {
				locations.add(u.id + "," + u.location.x + ", " + u.location.y);
			}
		}
		FileUtil.NewFile(Photo.userLocations, locations);
	}
	
	public static void getBeijingUserPhotos() {
		Set<String> userIds = new HashSet<String>();
		
		List<String> lines = FileUtil.getLinesFromFile(Photo.userLocations);
		Set<String> beijingids = new HashSet<String>();
		for (String line: lines) {
			String[] ss = line.split(",");
			Photo p = new Photo(ss[0]);
			p.longitude = Double.parseDouble(ss[1]);
			p.latitude = Double.parseDouble(ss[2]);
			userIds.add(ss[0]);
			if (GeoFilter.isPhotoInArea(p, GeoFilter.areaBeijing)) {
				beijingids.add(ss[0]);
			}
		}
		System.out.println("all user ids: " + userIds.size());
		System.out.println("beijing user ids: " + beijingids.size());
		
		List<Photo> ps = Photo.getPhotosOfBeijing();
		List<Photo> bps = new ArrayList<Photo>();
		List<Photo> nbps = new ArrayList<Photo>();
		for (Photo p : ps) {
			if (beijingids.contains(p.userId)) {
				bps.add(p);
			} else {
				if (userIds.contains(p.userId)) {
					nbps.add(p);
				}
			}
		}
		Photo.savePhotos(bps, Photo.photoSelectedBeijingUser);
		Photo.savePhotos(nbps, Photo.photoSelectedNotBeijingUser);
	}
	
	
	public static void main(String[] args)
	{
		//getUserIds();
		//getUsersWithHisAllPhoto();
		getBeijingUserPhotos();
		//writeUserLocations();
	}

}
