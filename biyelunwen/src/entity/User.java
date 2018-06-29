package entity;
import java.io.File;
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

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sciencecluster.GeoBlock;
import sciencecluster.MyPoint;
import trajectory.GeoTrajectory;
import trajectory.GeoTrajectoryPhoto;
import trajectory.MyPointWithTime;
import trajectory.TimeTrajectory;
import trajectory.UserGeoTrajectory;
import down.DownUserInfo;
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
import myutil.net.HttpHelper;

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
	public void updatePhotosTime() {
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
	
	public void updatePhotosTime(Set<String> need) {
		//if(this.timeZone<0)
		//	return;
		for(Photo p: this.photosList){
			if(this.timeZone == 8) {
				continue;
			}
			System.out.println("时区："+this.timeZone+"时间："+DateUtil.notSafeSdf.format(p.dateTaken));
			if (need.contains(p.id)) {
				int zoneDis = - (this.timeZone-User.BeijingTimeZone);//已知区时的时区减去待计算区时的时区，这里取了负数
				p.dateTaken = DateUtil.dateUpdate(p.dateTaken, DateUtil.DateField.hour, zoneDis);
				DateUtil.show(p.dateTaken);
			}
			
		}
	}
	
	public void drawSpecificRoutes(GeoFilter.Area area) {
		this.photosList = GeoFilter.getPhotosInArea(this.photosList, area);
		this.drawRoutes();
	}
	
	public void drawRoutes() {
		Photo.sortPhotos(this.photosList);//进行排序
		List<MyPoint> mps = Photo.getPoints(this.photosList);
		KmlFile.writeKmlPath(this.id + "_" + mps.size(), mps);
	}
	
	public void drawPoints() {
		List<MyPoint> mps = Photo.getPoints(this.photosList);
		KmlFile.writeMyPoint(this.id + "_" + mps.size(), mps);
	}
	
	public void drawAllPoints() {
		List<Photo> photos = Photo.getPhotos(Photo.userMergePhotosGeo + this.id + ".txt");
		List<MyPoint> mps = Photo.getPoints(photos);
		KmlFile.writeMyPoint(this.id + "_" + mps.size() + "_" + this.photosList.size(), mps);
	}
	
	public void getLocationByCluster() {
		List<Photo> ps = Photo.getPhotosWithGeo(this.photosList);
		if (this.photosList.size() > 50 && this.getDateDis() > 365 * 24) {
			this.location = Photo.clusterPhotosAndGetLargestGeo(ps);
		}
	}
	
	//如果相邻拍摄的照片时间相差比较小，但是位置相差比较大，则认为是错误的照片
	public void getInvalidEntity() {
		Photo.sortPhotos(this.photosList);
		double total = this.photosList.size() - 1;
		int count = 0;
		for (int i = 1; i < this.photosList.size(); i += 1) {
			Photo pre = this.photosList.get(i - 1);
			Photo cur = this.photosList.get(i);
			double dis = DateUtil.getDateDisSecond(cur.dateTaken, pre.dateTaken);
			double geodis = MyPoint.MyPointGPSDistance.getDistance(cur.longitude, cur.latitude, pre.longitude, pre.latitude);
			if (geodis > 1000 && dis < 60) {
				count += 1;
			}
		}
		double rate = 0;
		if (total > 0) {
			rate = count / total;
		}
		if (rate > 0) {
			System.out.println(this.id + ",\t" + this.photosList.size() + ",\t" + count + ",\t" + rate);
		}
	}
	
	//部分用户对不同的照片使用相同的坐标！！！
	public boolean getInvalidEntityBecauseBadGPS() {
		Photo.sortPhotos(this.photosList);
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		Map<String, List<Date>> mapd = new HashMap<String, List<Date>>();
		int baddate = 0;
		for (int i = 0; i < this.photosList.size(); i += 1) {
			Photo p = this.photosList.get(i);
			String temp = p.getGPSString() ;//+ "," + DateUtil.sdfDay.format(p.dateTaken);
			
			Set<String> cur = map.get(temp);
			List<Date> curd = mapd.get(temp);
			if (null == cur) {
				cur = new HashSet<String>();
				map.put(temp, cur);
			}
			if (null == curd) {
				curd = new ArrayList<Date>();
				mapd.put(temp, curd);
			}
			if (curd.size() > 0) {
				Date last = curd.get(curd.size() - 1);
				if (DateUtil.getDateDisHour(p.dateTaken, last) > 10 ) {
					baddate += 1;
					cur.add("bad:" + DateUtil.sdfDHM.format(p.dateTaken));
				}
			} else {
				curd.add(p.dateTaken);
			}
			cur.add(DateUtil.sdfDHM.format(p.dateTaken));
		}
		int count = map.size();
		double total = 0;
		total= baddate * 1.0 / this.photosList.size();
		if (total > 0.2) {
			System.out.println(this.id + ",\t" + this.photosList.size() + ",\t" + count + ",\t" + total);
			for (Map.Entry<String, Set<String>> entry: map.entrySet()) {
				System.out.println(entry.getKey() + "," + entry.getValue());
			}
			return true;
		}
		return false;
	}
	
	
	public double getDateDis() {
		Photo.sortPhotos(this.photosList);
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
	
	public static List<User> parseUserTimeZone(List<User> users) {
		List<User> newus = new ArrayList<User>();
		for (User u: users) {
			String id = u.id;
			String url = DownUserInfo.getUserInfoUrl(id);
			//System.out.println(url);
			String path = DownUserInfo.getUserInfoSavePath(id);
			String out = HttpHelper.testAndGetContent(path, url);
			
			SAXReader saxReader = new SAXReader(); 
			Document document;
			try
			{
				document = saxReader.read(new File(path));
				Element rootElement = document.getRootElement();
				Element person = rootElement.element("person");
				Element zone = person.element("timezone");
				if (null == zone) {
					continue;
				}
				String offset = zone.attributeValue("offset");
				u.timeZone = Integer.parseInt(offset.substring(0, 3));
				//System.out.println(offset + "," + u.timeZone);
				newus.add(u);
			} catch (Exception e1)
			{
				System.out.println(e1.toString());
			}
		}
		return newus;
	}
	
	public static void showVisitPlaces(String path, String id) {
		List<GeoAreaGroup> gaps = GeoAreaGroup.getGeoAreaMap(Photo.photoSelectedAllHotSpots);
		List<Photo> photos = Photo.getPhotos(path);
		List<User> users = User.getUsersWithPhotos(photos);
		List<GeoTrajectoryPhoto> splits = new ArrayList<GeoTrajectoryPhoto>();
		for (User u: users) {
			//splits.addAll(getPhotosListFromPhotosListSplitWeek(u));
			if (u.id.equals(id)) {
				splits.add(new GeoTrajectoryPhoto(u.id, u, u.photosList));
			}
		}
		Map<String, List<String>> visits = new TreeMap<String, List<String>>();
		for (GeoTrajectoryPhoto ps: splits) {
			Photo.sortPhotos(ps.photos);
			List<String> places = new ArrayList<String>();
			List<String> tags = new ArrayList<String>();
			for (int i = 0; i < ps.photos.size(); i += 1) {
				Photo p = ps.photos.get(i);
				//System.out.println(p.id + "," + "3_gugong_" + id);
				for (GeoAreaGroup gap: gaps) {
					if (gap.contains(p)) {
						String place = gap.name;
						System.out.println(p + ",\t" + DateUtil.sdfDHM.format(p.dateTaken) + ":" +place);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public String toString() {
		String temp = "";//this.id + "\n";
		Photo.sortPhotos(this.photosList);
		for (Photo p: this.photosList) {
			temp += p.toString() + "\n";
		}
		return temp;
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
	
	//计算所有用户的居住地
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
	
	public static String getDesc(int a, int b) {
		return a + "," + b + "," + b * 1.0 / a;
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
		

		System.out.println("all user ids: " + getDesc(userIds.size(),ps.size()));
		System.out.println("beijing user ids: " + getDesc(beijingids.size(), bps.size()));
		System.out.println("not beijing:" + getDesc((userIds.size() - beijingids.size()), nbps.size()));
		
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedNotBeijingUser);
		List<User> users = User.getUsersWithPhotos(photos);
		List<Photo> tourists = new ArrayList<Photo>();
		List<User> tus = new ArrayList<User>();
		for (User u: users) {
			if (u.getDateDis() < 168) {
				tourists.addAll(u.photosList);
				tus.add(u);
			}
		}
		System.out.println("tourist: " + getDesc(tus.size(), tourists.size()));
		Photo.savePhotos(tourists, Photo.photoSelectedTourist);
	}
	
	public static void getInvalidUsersBecauseBadGPS() {
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedTourist);
		List<User> users = User.getUsersWithPhotos(photos);
		int count = 0;
		for (User u: users) {
			if( u.getInvalidEntityBecauseBadGPS()) {
				count += 1;
			}
		}
		System.out.println("bad user count: " + count);
	}
	
	
	public static void main(String[] args)
	{
		//getUserIds();
		//getUsersWithHisAllPhoto();
		//getBeijingUserPhotos();
		//writeUserLocations();
		//showVisitPlaces(Photo.photoSelectedTourist, "146885863@N02");
		getInvalidUsersBecauseBadGPS();
	}

}
