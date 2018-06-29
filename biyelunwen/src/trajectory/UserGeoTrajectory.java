package trajectory;

import java.util.ArrayList;
import java.util.List;

import draw.KmlFile;
import myutil.DateUtil;
import sciencecluster.MyPoint;
import entity.Photo;
import entity.User;
import entity.filter.GeoFilter;
import entity.filter.UserFilter;

public class UserGeoTrajectory
{
	//从照片列表中获得轨迹，如果隔天则认为是两个轨迹
	public static List<GeoTrajectory> getTrajectorysFromPhotosListWithNextDay_OLD(String traId, List<Photo> photos){
		List<GeoTrajectory> tras=new ArrayList<GeoTrajectory>();
		Photo.sortPhotos(photos);//首先对所有的距离进行排序
		List<Photo> temp=new ArrayList<Photo>();//保存轨迹
		String start, finish;
		for(int i=0; i<photos.size(); ++i){
			Photo cur=photos.get(i);
			if(temp.size()==0){
				temp.add(cur);
				continue;
			}
			Photo pre=temp.get(temp.size()-1);//获取前一个
			int preDate=DateUtil.getDateField(pre.dateTaken, DateUtil.DateField.year_month_day);
			int curDate=DateUtil.getDateField(cur.dateTaken, DateUtil.DateField.year_month_day);
			if(preDate==curDate)//日期相等
				temp.add(cur);
			else{//表示隔天，一个轨迹已经终结
		//		System.out.println(cur.longitude);
				List<MyPointWithTime> mpts=Photo.getPointsWithTime(temp);
				start=DateUtil.sdfDay.format(temp.get(0).dateTaken);
				tras.add(new GeoTrajectory(traId+"_"+start, mpts));
				temp.clear();//清空轨迹
				temp.add(cur);
			}
		}
		if(temp.size()>0){//表示还有尾巴没有处理
			start=DateUtil.sdfDay.format(temp.get(0).dateTaken);
			tras.add(GeoTrajectory.getTrajectoryWithPointsNoTime(traId+"_"+start, Photo.getPoints(temp)));
		}
		return tras;
	}
	
	
	//从照片列表中获得照片分段列表
	private static List<GeoTrajectoryPhoto> getPhotosListFromPhotosList(String traId, List<Photo> photos, double maxHourDis){
		List<GeoTrajectoryPhoto> tras=new ArrayList<GeoTrajectoryPhoto>();
		Photo.sortPhotos(photos);//首先对所有的距离进行排序
		List<Photo> temp=new ArrayList<Photo>();//保存轨迹
		String start, finish;
		for(int i=0; i<photos.size(); ++i){
			Photo cur=photos.get(i);
			if(temp.size()==0){
				temp.add(cur);
				continue;
			}
			Photo pre=temp.get(temp.size()-1);//获取前一个
			double hourDis=DateUtil.getDateDisHour(cur.dateTaken, pre.dateTaken);
			if(hourDis<0)
				System.out.println("时间差为："+hourDis);
			if(hourDis<=maxHourDis)//在最大时间差范围之内
				temp.add(cur);
			else{//表示时间差过大，一个轨迹已经终结
		//		System.out.println(cur.longitude);
				start=DateUtil.sdfDHM.format(temp.get(0).dateTaken);
				finish=DateUtil.sdfDHM.format(temp.get(temp.size()-1).dateTaken);
				tras.add(new GeoTrajectoryPhoto(traId+"_"+start+"_"+finish, temp));
				temp=new ArrayList<Photo>();//这里必须要这样
				temp.add(cur);
			}
		}
		if(temp.size()>0){//表示还有尾巴没有处理
			start=DateUtil.sdfDHM.format(temp.get(0).dateTaken);
			finish=DateUtil.sdfDHM.format(temp.get(temp.size()-1).dateTaken);
			tras.add(new GeoTrajectoryPhoto(traId+"_"+start+"_"+finish, temp));
		}
		return tras;
	}
	
	public static List<GeoTrajectory> getTrasFromPhotoList(List<GeoTrajectoryPhoto> tps){
		List<GeoTrajectory> tras=new ArrayList<GeoTrajectory>();
		for(GeoTrajectoryPhoto tp: tps){
			tras.add(tp.getGeoTrajectory());
		}
		return tras;
	}
	
	public static void testGetTrajectorysFromPhotosList(){
		List<Photo> photos=new ArrayList<Photo>();
		Photo [] ps=new Photo[6];
		for(int i=0; i<ps.length; ++i){
			ps[i]=new Photo("");
			ps[i].longitude=i;
		}
		
		ps[0].dateTaken=DateUtil.getDate("2012-10-1 10:00:00", DateUtil.notSafeSdf);
		ps[1].dateTaken=DateUtil.getDate("2012-10-7 10:00:00", DateUtil.notSafeSdf);
		ps[2].dateTaken=DateUtil.getDate("2012-10-1 10:00:00", DateUtil.notSafeSdf);
		ps[3].dateTaken=DateUtil.getDate("2012-10-2 10:00:00", DateUtil.notSafeSdf);
		ps[4].dateTaken=DateUtil.getDate("2012-10-2 10:00:00", DateUtil.notSafeSdf);
		ps[5].dateTaken=DateUtil.getDate("2012-10-3 11:00:00", DateUtil.notSafeSdf);
		for(int i=0; i<ps.length; ++i){
			photos.add(ps[i]);
		}
		List<GeoTrajectoryPhoto> tps=getPhotosListFromPhotosList("用户", photos, 80);
	//	List<GeoTrajectory> tras=getTrajectorysFromPhotosListWithNextDay("用户", photos);
		List<GeoTrajectory> tras=GeoTrajectoryPhoto.getGeoTrajectories(tps);
		for(GeoTrajectory tra: tras){
			System.out.println(tra);
		}
	}
	
	//从用户的拍摄记录中获得轨迹
	public static List<GeoTrajectory> getTrajectorysFromUser(User u, double maxHourDis){
		List<GeoTrajectoryPhoto> tps=getPhotosListFromPhotosList(u.id, u.photosList, maxHourDis);
		List<GeoTrajectory> tras=getTrasFromPhotoList(tps);
	//	System.out.println("userID:"+u.id);
		for(GeoTrajectory tra: tras){
	//		System.out.println(tra);
		}
		return tras;
	}
	
	//从用户列表获得轨迹列表
	public static List<GeoTrajectory> getTrajectoryFromUsers(List<User> users, double maxHourDis){
		List<GeoTrajectory> tras=new ArrayList<GeoTrajectory>();
		for(User u: users){
			tras.addAll(UserGeoTrajectory.getTrajectorysFromUser(u, maxHourDis));
		}
		System.out.println("从"+users.size()+"名用户中获得"+tras.size()+"个轨迹");
		return tras;
	}
	
	public static List<GeoTrajectory> getTourTrajectory(List<User> users, double maxHourDis, double dayDis){
		List<GeoTrajectory> tours=new ArrayList<GeoTrajectory>();
		for(User u: users){
			List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectorysFromUser(u, maxHourDis);//获取该用户的轨迹
			List<GeoTrajectory> selected=GeoTrajectory.getSingleToursWithDay(tras, dayDis);
		//	System.out.println("本来轨迹数目："+tras.size()+"旅游轨迹数目："+selected.size());
			tours.addAll(selected);
		}
		System.out.println("获得旅游路径："+tours.size());
		return tours;
	}
	
	public static List<GeoTrajectory> getNotTourTrajectory(List<User> users, double maxHourDis, double dayDis){
		List<GeoTrajectory> tours=new ArrayList<GeoTrajectory>();
		for(User u: users){
			List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectorysFromUser(u, maxHourDis);//获取该用户的轨迹
			tours.addAll(GeoTrajectory.getNotSingleToursWithDay(tras, dayDis));
		}
		return tours;
	}
	
	public static String getTrajectorySavePath(GeoFilter.Area curArea){
		String traPath=Photo.clusterDir+curArea.name+"_tra.txt";
		return traPath;		
	}
	
	//选取用户，然后保存轨迹，然后读取轨迹，然后计算距离
	public static List<GeoTrajectory> getTrajectoryAndSetDistance(GeoFilter.Area curArea, double maxHourDis){
		String traPath=getTrajectorySavePath(curArea);
		System.out.println("轨迹保存路径："+traPath);
		List<GeoTrajectory> tras=null;
		List<User> users=UserFilter.selectUsers(curArea);
		tras=UserGeoTrajectory.getTrajectoryFromUsers(users, maxHourDis);
		System.out.println("过滤前的轨迹数目："+tras.size());
	//	tras=filterTrajectory(tras);
		System.out.println("过滤后的轨迹数目："+tras.size());
		GeoTrajectory.writeTrajectoryToFile(tras, traPath);//将轨迹写到文件中
		KmlFile.writeTrajectories(tras, curArea.name+"_tra");
		return tras;
	}
	
	//对北京本地人的轨迹中的点进行聚类，观察它们是否和游客经常去的景点一样
	public static void testTourTrajectory(){
		List<User> users=User.getUsersWithPhotos();
		List<GeoTrajectory> tras=UserGeoTrajectory.getTourTrajectory(users, 72, 180);
		System.out.println("长途旅游轨迹数量："+tras.size()+"\t节点数量："+GeoTrajectory.countTrasPointsNum(tras));
		KmlFile.writeTrajectories(tras, "长途旅游轨迹");
	//	getScienceClusterResult(GeoFilter.areaBeijing, tras);
	}
	
	public static void testNotTourTrajectory(){
		List<User> users=User.getUsersWithPhotos();
		List<GeoTrajectory> tras=UserGeoTrajectory.getNotTourTrajectory(users, 72, 180);
		System.out.println("非旅游轨迹数量："+tras.size()+"\t节点数量："+GeoTrajectory.countTrasPointsNum(tras));
		KmlFile.writeTrajectories(tras, "非旅游轨迹");
	//	getScienceClusterResult(GeoFilter.areaBeijing, tras);
	}
	
	public static void main(String[] args){
		//	getTrajectoryAndSetDistance(GeoFilter.areaGuGonNeiBu, 2);//故宫内部，时间差为2个小时
		//	getTrajectoryAndSetDistance(GeoFilter.areaYiHeYuan, 2);//故宫内部，时间差为2个小时
		// 	getTrajectoryAndSetDistance(GeoFilter.areaBeijing, 24);//故宫内部，时间差为2个小时
		//testSpecificUsersTras();
		testGetTrajectorysFromPhotosList();
	}
}
