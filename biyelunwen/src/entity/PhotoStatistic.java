package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import myutil.DateUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;
import sciencecluster.ClusterResult;
import sciencecluster.GeoBlock;
import sciencecluster.MyPoint;
import sciencecluster.ScienceCluster;
import trajectory.GeoTrajectory;
import trajectory.GeoTrajectoryCluster;
import trajectory.UserGeoTrajectory;
import draw.KmlFile;
import entity.filter.GeoFilter;
import entity.filter.PhotoFilter;
import entity.filter.TimeFilter;
import entity.filter.UserFilter;

public class PhotoStatistic
{
	//对北京按照月份进行统计
	public static void beijingMonthlyOld_12_31(){
		Map<String, List<Integer>> placesIndex=ClusterResult.getInitPlaceIndex();

		double clusterR=2.9;//用来设置局部密度的截断距离
		int clusterNum=10;//聚类的数量
		double rate=0.8;//平均密度比率
		MyPoint.mpw=new MyPoint.MyPointSelfWeight();//设置开启数据点的权重
		MyPoint.mpd=new MyPoint.MyPointCoordinateDistanceWithMax(100);//使用坐标距离
		
		
		for(int i=1; i<=12; ++i){
			TimeFilter.TimeType tt=new TimeFilter.MonthFilter(i);
			PhotoFilter pf=PhotoFilter.getFilter(tt);
			List<Photo> photos=PhotoFilter.getFilteredPhotos(pf);
			List<MyPoint> mps=Photo.getPoints(photos);
			List<GeoBlock> blocks=GeoBlock.getBlock(mps, 2000, 2000);
			List<MyPoint> cps=GeoBlock.getMyPoints(blocks);
			
			
			ScienceCluster sc=new ScienceCluster(cps);
			sc.initCluster(clusterR);
		//	sc.showLocalDensity();
			sc.cluster(clusterNum, clusterR, rate);
			
			List<MyPoint> used=new ArrayList<MyPoint>();
			for(MyPoint mp: cps){
				if(Integer.parseInt(mp.clusterId)>-1)
					used.add(mp);
			}
			System.out.println("聚类后还剩下："+used.size());
			KmlFile.writeClusterResult(DateUtil.getTodayMonthDay()+"_"+i+"_"+clusterR+"_"+clusterNum, used);
			List<ClusterResult> rs=ClusterResult.getClusters(sc.clusters);
			Map<String, Integer> index=ClusterResult.getPlacesIndex(rs);
			System.out.println(i+"月");
			ClusterResult.setPlacesIndex(placesIndex, index);//将当月的排名加入到列表中
			for(Map.Entry<String, List<Integer>> entry: placesIndex.entrySet()){
				List<Integer> temp=entry.getValue();
				if(temp.size()<i)
					temp.add(-1);
			}
		}
		List<String> content=new ArrayList<String>();
		List<String> names=new ArrayList<String>();
		Map<String, List<Double>> score=ClusterResult.getIndexScore(placesIndex);
		for(Map.Entry<String, List<Double>> entry: score.entrySet()){
			System.out.print(entry.getKey()+",");
			names.add(entry.getKey());
			List<Double> temp=entry.getValue();
			StringBuilder sb=new StringBuilder(temp.get(0)+"");
			for(int i=1; i<temp.size(); ++i)//这里从1开始
				sb.append(","+temp.get(i));
			content.add(sb.toString());
			System.out.print(sb);
			System.out.println();
		}
		FileUtil.NewFile("C:/Users/Admin/Desktop/图片地理位置/画图/12-轨迹-景点变化/data.txt", content);
		FileUtil.NewFile("C:/Users/Admin/Desktop/图片地理位置/画图/12-轨迹-景点变化/names.txt", names);
	}
	
	//对北京按照月份进行统计
	public static void beijingMonthly(){
		Map<String, List<Integer>> placesIndex=ClusterResult.getInitPlaceIndex();
		GeoFilter.Area area=GeoFilter.areaBeijing;		
		double maxHourDis=24;
	//	List<User> waiDiUsers=UserFilter.getTourists(2, maxHourDis);//获得外地游客
	//	List<Photo> userPhotos=UserFilter.getUsersPhotos(waiDiUsers);//获得他们拍摄的照片
		List<Photo> photos=GeoFilter.getAreaPhotos(area);
		
		for(int i=1; i<=12; ++i){
			TimeFilter.TimeType tt=new TimeFilter.MonthFilter(i);
			PhotoFilter pf=PhotoFilter.getFilter(tt);
			List<Photo> monthPhotos=PhotoFilter.filterPhotos(photos, pf);//获得按照月份过滤后的照片	
			List<User> users=User.getUsersWithPhotos(monthPhotos);//获得这些照片属于的用户
			ScienceCluster sc=GeoTrajectoryCluster.getUserScienceClusterResult(area, users);
			List<ClusterResult> crs=ClusterResult.getClusters(sc.clusters);
			ClusterResult.setClusterResultsLabel(crs, ClusterResult.places);			
			KmlFile.writeClusterResult(DateUtil.getTodayMonthDay()+"_"+i+"月聚类结果", sc.getUsedPoints());
			
			Map<String, Integer> index=ClusterResult.getPlacesIndex(crs);
			System.out.println(i+"月");
			ClusterResult.setPlacesIndex(placesIndex, index);//将当月的排名加入到列表中
			for(Map.Entry<String, List<Integer>> entry: placesIndex.entrySet()){
				List<Integer> temp=entry.getValue();
				if(temp.size()<i)
					temp.add(-1);
			}
		}
		List<String> content=new ArrayList<String>();
		List<String> names=new ArrayList<String>();
		Map<String, List<Double>> score=ClusterResult.getIndexScore(placesIndex);
		for(Map.Entry<String, List<Double>> entry: score.entrySet()){
			System.out.print(entry.getKey()+",");
			names.add(entry.getKey());
			List<Double> temp=entry.getValue();
			StringBuilder sb=new StringBuilder(temp.get(0)+"");
			for(int i=1; i<temp.size(); ++i)//这里从1开始
				sb.append(","+temp.get(i));
			content.add(sb.toString());
			System.out.print(sb);
			System.out.println();
		}
		FileUtil.NewFile("C:/Users/Admin/Desktop/图片地理位置/画图/12-轨迹-12个月景点变化/data.txt", content);
		FileUtil.NewFile("C:/Users/Admin/Desktop/图片地理位置/画图/12-轨迹-12个月景点变化/names.txt", names);
	}
	
	
	
	public static void getBeijing(){
		List<Photo> photos=Photo.getPhotos();
		GeoFilter.Area area=GeoFilter.areaGuGongNeiBu;
		photos=GeoFilter.getAreaPhotos(area);
		List<MyPoint> mps=Photo.getPoints(photos);
		List<GeoBlock> blocks=GeoBlock.getBlock(mps, 100, 100);
		List<MyPoint> cps=GeoBlock.getMyPoints(blocks);
		
		double clusterR=area.clusterR;//用来设置局部密度的截断距离
		int clusterNum=area.clusterNum;//聚类的数量
		double rate=0.5;//平均密度比率
		MyPoint.mpw=new MyPoint.MyPointSelfWeight();//设置开启数据点的权重
		MyPoint.mpd=new MyPoint.MyPointCoordinateDistance();//使用坐标距离
		
		ScienceCluster sc=new ScienceCluster(cps);
		sc.initCluster(clusterR);
	//	sc.showLocalDensity();
		sc.cluster(clusterNum, clusterR, rate);
		
		
		
		List<MyPoint> used=new ArrayList<MyPoint>();
		for(MyPoint mp: cps){
			if(Integer.parseInt(mp.clusterId)>-1)
				used.add(mp);
		}
		System.out.println("聚类后还剩下："+used.size());
		KmlFile.writeClusterResult(DateUtil.getTodayMonthDay()+"_"+clusterR+"_"+clusterNum, used);
	}

	//测试在故宫拍摄的照片的 时间关系
	public static void testGuGongTime(){
		List<Photo> photos=GeoFilter.getAreaPhotos(GeoFilter.areaGuGongNeiBu);
		for(Photo p: photos){
			if(p.getDateString(DateUtil.DateField.hour).equals("23")){
				System.out.println(p.id);
			}
		}
		Map<String, Integer> hours=Photo.getDateFields(photos, DateUtil.DateField.hour);
		for(Map.Entry<String, Integer> entry: hours.entrySet()){
			System.out.println(entry.getKey()+","+entry.getValue());
		}
	}
	
	public static void main(String[] args)
	{
	//	getBeijing();
	//	beijingMonthly();
		testGuGongTime();
		System.out.println(DateUtil.sdf.format(DateUtil.getDate(1463640063)));
	}

}
