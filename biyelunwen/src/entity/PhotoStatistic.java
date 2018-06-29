package entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

import algorithm.markov.StatusChain;
import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.SortString;
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
		String pathS = Photo.statisticDir + "tags.txt";
		
	/*	for (int i = 0; i < beijing.size(); i += 1) {
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
		}*/
		//FileUtil.NewFile(pathS, tagsStr);
		tagsStr = FileUtil.getLinesFromFile(pathS);
		System.out.println("共有tags： " + tagsStr.size());
		Map<String, Integer> frequency = StringUtil.countFrequencyToLower(tagsStr);
		List<SortString> sss = StatusChain.sortCountMap(frequency);
		List<String> notneed = Arrays.asList("square", "iphoneography", "square format", "instagram app");
		for (int i = 0; i < 500; i += 1) {
			SortString ss = sss.get(i);
			if (notneed.contains(ss.content)) {
				continue;
			}
			int loop = (int)(ss.value + 0.1);
			for (int j = 0; j < loop; j += 1) {
				System.out.println(ss.content);
			}
		}
	}
	
	public static void countYearMonthFrequency() {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBasicInfoPath);
		Map<String, Integer> frequency = Photo.countDateFields(beijing, DateUtil.DateField.year_month);
		Map<String, Integer> users = countYearlyUserNumber(DateUtil.DateField.year_month);
		showMatrix(frequency, users);
	}
	
	//比较在一个星期的每一天中本地居民和外地游客的差异
	public static void comparePhotoTakenWeek() {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBeijingUser);
		Map<String, Integer> bf = Photo.countDateFields(beijing, DateUtil.DateField.dayOfWeek);
		List<Photo> waidi = Photo.getPhotos(Photo.photoSelectedTourist);
		Map<String, Integer> waif = Photo.countDateFields(waidi, DateUtil.DateField.dayOfWeek);
	}
	
	public static List<Photo> filterPhotos(List<Photo> photos) {
		System.out.println("pre: " + photos.size());
		List<User> users = User.getUsersWithPhotos(photos);
		photos.clear();
		for (User u: users) {
			if (u.getInvalidEntityBecauseBadGPS() == false) {
				photos.addAll(u.photosList);
			}
		}
		System.out.println("after: " + photos.size());
		return photos;
	}
	
	//比较在一天之内本地居民和外地游客拍摄照片的差别
	public static void comparePhotoTakenDay() {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBeijingUser);
		Map<String, Integer> bf = Photo.countDateFields(beijing, DateUtil.DateField.hour);
		List<Photo> waidi = Photo.getPhotos(Photo.photoSelectedTourist);
		waidi = filterPhotos(waidi);
		Map<String, Integer> waif = Photo.countDateFields(waidi, DateUtil.DateField.hour);
		List<User> users = User.getUsersWithPhotos(waidi);
		List<User> newus = User.parseUserTimeZone(users);
		waidi.clear();
		List<String> cameraids = FileUtil.getLinesFromFile(Photo.cameraIDPath);
		Set<String> sca = new HashSet<String>(cameraids);
		for (User u: newus) {
			u.updatePhotosTime(sca);
			waidi.addAll(u.photosList);
		}
		waif = Photo.countDateFields(waidi, DateUtil.DateField.hour);
		Map<String, Set<String>> bads = new HashMap<String, Set<String>>();
		for (Photo p: waidi) {
			if (p.getDateString(DateUtil.DateField.hour).equals("3")) {
				Set<String> bad = bads.get(p.userId);
				if (null == bad) {
					bad = new HashSet<String>();
					bads.put(p.userId, bad);
				}
				bad.add(p.id);
				//System.out.println(p.id + "," + "hour_4");
			}
		}
		for (Map.Entry<String, Set<String>> entry: bads.entrySet()) {
			System.out.println(entry.getKey() + "," + entry.getValue().size());
		}
	}
	
	public static void comparePhotoTakenHourOfDay() {
		List<Photo> beijing = Photo.getPhotos(Photo.photoSelectedBeijingUser);
		Map<String, Integer> bf = Photo.countDateFields(beijing, DateUtil.DateField.hour);
		List<Photo> waidi = Photo.getPhotos(Photo.photoSelectedNotBeijingUser);
		Map<String, Integer> waif = Photo.countDateFields(waidi, DateUtil.DateField.hour);
		showMatrix(bf, waif);
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
			if (count == 24) {
				//System.out.println(keys);
				System.out.println(values);
				System.out.println(valuesb);
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
		
		List<Photo> nobei = Photo.getPhotos(Photo.photoSelectedTourist);
		
		Photo.writePhotoListHeatFile(nobei, GeoFilter.areaNiaoChao, "tourist_niaochao");
		Photo.writePhotoListHeatFile(nobei, GeoFilter.areaMuTianYu, "tourist_mutianyu");
		Photo.writePhotoListHeatFile(nobei, GeoFilter.areaBaDaLing, "tourist_badaling");
		
		Photo.writePhotoListHeatFile(nobei, GeoFilter.areaBeijing, "beijing_not_user_photos");
		nobei = GeoFilter.getPhotosInArea(nobei, GeoFilter.areaBeijingNeibu);
		Photo.writePhotoListHeatFile(nobei, GeoFilter.areaBeijingNeibu, "beijing_not_user_photos_neibu");
		
		
	}
	
	//对所有的照片进行聚类，找到它们的
	public static void getClustersOfAllPhotos() {
		GeoFilter.Area area = GeoFilter.areaBeijing;	
		//List<Photo> photos = Photo.getPhotosOfBeijing().subList(0, 10000);
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedTourist);
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
		ClusterResult.getPlaceSortResult(crs, 50);
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
		compareBeijingAndNotBeijingPhotos();
		//beijingMonthly();
		//getClustersOfAllPhotos();
		//comparePhotoTakenWeek();
		//comparePhotoTakenDay();
		//getClustersOfAllPhotos();
		//comparePhotoTakenHourOfDay();
		//countPhotosTags();
		//compareBeijingAndNotBeijingPhotos();
	}

}
