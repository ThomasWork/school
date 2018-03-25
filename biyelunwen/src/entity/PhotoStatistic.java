package entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import myutil.DateUtil;
import myutil.NumberUtil;
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
	/*
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
	}*/
	
	
	
	
	
/*	public static void getBeijing(){
		List<Photo> photos=Photo.getPhotos(Photo.photoBasicInfoPath);
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
		Map<String, Integer> hours=Photo.countDateFields(photos, DateUtil.DateField.hour);
		for(Map.Entry<String, Integer> entry: hours.entrySet()){
			System.out.println(entry.getKey()+","+entry.getValue());
		}
	}*/
	
	/*************************     毕业论文             ***********************************/
	public static void getTotalPhotos() {
		List<Photo> beijing = Photo.getPhotosOfBeijing();
		System.out.println("北京共有图片：" + beijing.size());
		Photo.savePhotos(beijing, Photo.photoSelectedBasicInfoPath);
	}
	
	public static void countFrequency() {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBasicInfoPath);
		Photo.countDateFields(beijing, DateUtil.DateField.year);
	}
	
	public static void countUserNumber() {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBasicInfoPath);
		List<String> users = Photo.getUniqueUsers(beijing);
		System.out.println("total users: " + users.size());
		FileUtil.NewFile(Photo.userBeijingIDs, users);
	}
	
	public static Map<String, Integer> countYearlyUserNumber(DateUtil.DateField df) {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBasicInfoPath);
		Map<String, Set<String>> userYear = new HashMap<String, Set<String>>();
		for (Photo p: beijing) {
			String year = p.getDateString(df);
			if (null == userYear.get(year)) {
				userYear.put(year, new HashSet<String>());
			}
			userYear.get(year).add(p.userId);
		}
		List<String> years = new ArrayList<String>();
		for (Map.Entry<String, Set<String>> entry : userYear.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i += 1) {
				years.add(entry.getKey());
			}
		}
		return StringUtil.countFrequencyWithNumber(years);
	}
	
	//统计照片的词频信息
	public static void countPhotosTags() {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBasicInfoPath);
		List<String> tagsStr = new ArrayList<String>();
		String path = Photo.statisticDir + "tags.txt";
		
		/*for (int i = 0; i < beijing.size(); i += 1) {
			if (i % 1000 == 0) {
				System.out.println(i);
			}
			Photo p = beijing.get(i);
			String path = Photo.photoInfoDir + p.id + ".txt";
			try{
				SAXReader saxReader = new SAXReader(); 
				Document document = saxReader.read(new File(path));
				Element rootElement = document.getRootElement();
				Element photo = rootElement.element("photo");
				Element tags = photo.element("tags");
				List<Element> tags2 = tags.elements();
				for (Element tag: tags2) {
					if (tag.attributeValue("machine_tag").equals("0")) {
						tagsStr.add(tag.attributeValue("raw"));
					}
				}
				} catch (Exception e) {    
		            //e.printStackTrace();    
					System.out.println(path);
				}
		}
		FileUtil.NewFile(path, tagsStr);*/
		tagsStr = FileUtil.getLinesFromFile(path);
		System.out.println("共有tags： " + tagsStr.size());
		Map<String, Integer> frequency = StringUtil.countFrequencyToLower(tagsStr);
	}
	
	public static void countYearMonthFrequency() {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBasicInfoPath);
		Map<String, Integer> frequency = Photo.countDateFields(beijing, DateUtil.DateField.year_month);
		Map<String, Integer> users = countYearlyUserNumber(DateUtil.DateField.year_month);
		showMatrix(frequency, users);
	}
	
	//生成python画图需要的格式
	public static void showMatrix(Map<String, Integer> ma, Map<String, Integer> mb) {
		String keys = "";
		String values = "";
		String valuesb = "";
		int count = 0;
		for(Map.Entry<String, Integer> entry: ma.entrySet())
		{
			count += 1;
			keys = keys + entry.getKey() + ",";
			values = values + entry.getValue() + ",";
			valuesb = valuesb + mb.get(entry.getKey()) + ",";
			if (count == 12) {
				//System.out.println(keys);
				System.out.println(valuesb);
				System.out.println(values);
				count = 0;
				keys = "";
				values = "";
				valuesb = "";
			}
		}
	}
	
	//比较北京用户和非北京用户拍摄的照片的分布区别
	public static void compareBeijingAndNotBeijingPhotos() {
		List<Photo> beijings = Photo.getPhotos(Photo.photoSelectedBeijingUser);
		Photo.writePhotoListHeatFile(beijings, GeoFilter.areaBeijing, "beijing_user_photos");
		beijings = GeoFilter.getPhotosInArea(beijings, GeoFilter.areaBeijingNeibu);
		Photo.writePhotoListHeatFile(beijings, GeoFilter.areaBeijingNeibu, "beijing_user_photos_neibu");
		
		List<Photo> nobei = Photo.getPhotos(Photo.photoSelectedNotBeijingUser);
		Photo.writePhotoListHeatFile(nobei, GeoFilter.areaBeijing, "beijing_not_user_photos");
		nobei = GeoFilter.getPhotosInArea(nobei, GeoFilter.areaBeijingNeibu);
		Photo.writePhotoListHeatFile(nobei, GeoFilter.areaBeijingNeibu, "beijing_not_user_photos_neibu");
	}
	
	//对所有的照片进行聚类，找到它们的
	public static void getClustersOfAllPhotos() {
		GeoFilter.Area area = GeoFilter.areaBeijing;	
		//List<Photo> photos = Photo.getPhotosOfBeijing().subList(0, 10000);
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedBeijingUser);
		List<MyPoint> mps = Photo.getPoints(photos);
		ScienceCluster sc = GeoTrajectoryCluster.ScienceCluster(area, mps);
		
		List<ClusterResult> crs = ClusterResult.getClusters(sc.clusters);
		/*for (int i = 0; i < crs.size(); i += 1) {
			System.out.println(crs.get(i).points.get(0));
		}*/
		//ClusterResult.setClusterResultsLabel(crs, ClusterResult.places);
		KmlFile.writeClusterResult(DateUtil.getTodayMonthDay() + "notbeijing聚类结果", sc.getUsedPoints());
		/*for(ClusterResult cr: crs) {
			for (MyPoint mp : cr.points) {
				System.out.println(cr.label + "," + mp.getBox());
			}
		}*/
		ClusterResult.getPlaceSortResult(crs, 40);
		List<String> lines = new ArrayList<String>();
		Map<String, List<ClusterResult>> index = ClusterResult.getLabeledClusterResult(crs);
		for (Map.Entry<String, List<ClusterResult>> entry : index.entrySet()) {
			for (ClusterResult cr : entry.getValue()) {
				for (int i = 0; i < cr.points.size(); i += 1) {
					lines.add(entry.getKey() + "," + cr.points.get(i).getBox());
				}
			}
		}
		FileUtil.NewFile(Photo.photoSelectedBeijingHotSpots, lines);
	}
	
	public static void main(String[] args)
	{
		//getTotalPhotos();
		//countFrequency();
		//countYearlyUserNumber();
		//countYearMonthFrequency();
		//countUserNumber();
		//compareBeijingAndNotBeijingPhotos();
		//beijingMonthly();
		getClustersOfAllPhotos();
		//countPhotosTags();
		//compareBeijingAndNotBeijingPhotos();
	}

}
