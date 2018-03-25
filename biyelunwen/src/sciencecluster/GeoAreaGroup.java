package sciencecluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import trajectory.GeoTrajectoryCluster;
import draw.KmlFile;
import entity.Photo;
import entity.filter.GeoFilter;
import entity.filter.PhotoFilter;
import entity.filter.TimeFilter;
import myutil.DateUtil;
import myutil.fileprocess.FileUtil;

public class GeoAreaGroup
{
	public String name;
	public List<GeoArea> gas;
	
	public GeoAreaGroup(String nameP, List<GeoArea> gasP) {
		this.name = nameP;
		this.gas = gasP;
	}
	
	public void addPhotos(List<Photo> ps) {
		Map<String, String> find = new HashMap<String, String>();
		for (Photo p: ps) {
			MyPoint mp = Photo.getPoint(p);
			for (GeoArea ga: this.gas) {
				if (ga.containPoint(mp)) {
					if (find.containsKey(p.id)) {
						System.out.println("multi add photos");
						System.out.println(find.get(p.id) + ",\t" + ga.toString());
						System.exit(1);
					}
					ga.userIds.add(mp.userId);
					find.put(p.id, ga.toString());
				}
			}
		}
	}
	
	public double getWeight(int n) {
		List<Double> nums = new ArrayList<Double>();
		for (GeoArea ga: this.gas) {
			nums.add((double) ga.userIds.size());
		}
		double sum = 0;
		Collections.sort(nums);
		for (int i = 0; i < n; i += 1) {
			int index = nums.size() - 1 - i;
			if (index < 0) {
				index  = 0;
			}
			sum += nums.get(index);
		}
		return sum;
	}
	
	public List<String> getLines() {
		List<String> lines = new ArrayList<String>();
		for (GeoArea ga: this.gas) {
			String temp = ga.name + "," + ga.minX + "," + ga.maxX + "," + ga.minY + "," + ga.maxY;
			lines.add(temp);
		}
		return lines;
	}
	
	public static List<GeoAreaGroup> getGeoAreaMap(String path) {
		Map<String, List<GeoArea>> mgas = new HashMap<String, List<GeoArea>>();
		List<GeoArea> gas = GeoArea.loadGeoAreas(path);
		for (GeoArea ga: gas) {
			List<GeoArea> temp = mgas.get(ga.name);
			if (null == temp) {
				temp =  new ArrayList<GeoArea>();
				mgas.put(ga.name, temp);
			}
			temp.add(ga);
		}
		Set<String> filtered = new HashSet<String>();
		filtered.add("没有匹配的景点");
		filtered.add("基督教堂");
		filtered.add("中央电视台");
		List<GeoAreaGroup> gap = new ArrayList<GeoAreaGroup>();
		for (Map.Entry<String, List<GeoArea>> entry: mgas.entrySet()) {
			if (filtered.contains(entry.getKey()) == false) {
				gap.add(new GeoAreaGroup(entry.getKey(), entry.getValue()));
			}
		}
		return gap;
	}
	
	public static void rewriteGaps(String path) {
		List<String> lines = new ArrayList<String>();
		List<GeoAreaGroup> gaps = GeoAreaGroup.getGeoAreaMap(path);
		for (GeoAreaGroup gap: gaps) {
			lines.addAll(gap.getLines());
		}
		FileUtil.NewFile(path, lines);
	}
	
	public static void mergeGaps() {

		Set<String> all = new HashSet<String>();
		List<String> beijing = FileUtil.getLinesFromFile(Photo.photoSelectedBeijingHotSpots);
		System.out.println(beijing.size());
		List<String> notbeijing = FileUtil.getLinesFromFile(Photo.photoSelectedNotBeijingHotSpots);
		System.out.println(notbeijing.size());
		all.addAll(beijing);
		all.addAll(notbeijing);
		List<String> saved = new ArrayList<String>(all);
		System.out.println(all.size());
		FileUtil.NewFile(Photo.photoSelectedAllHotSpots, saved);
		rewriteGaps(Photo.photoSelectedAllHotSpots);
	}
	
	
	/*******************************             毕业论文                       **********************************/
	//获取每个兴趣区的排名
	public static Map<String, Double> getGroupWeight(List<GeoAreaGroup> gaps, List<Photo> photos, int n) {
		Map<String, Double> sorted = new HashMap<String, Double>();
		for (GeoAreaGroup gap: gaps) {
			gap.addPhotos(photos);
			double sum = gap.getWeight(n);
			sorted.put(gap.name, sum);
		}
		return sorted;
	}
	
	//获取12个月中的排名结果
	public static void test12Months() {
		//注意下面两个路径应该保持一致
		
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedNotBeijingUser);

		//getGroupWeight(gaps, photos, 50);
		List<String> places = new ArrayList<String>(Arrays.asList("故宫", "天安门广场", "颐和园", "天坛公园",
				"后海", "雍和宫", "鸟巢", "十三陵", "圆明园",
				"慕田峪长城", "八达岭长城","金山岭长城","居庸关","国际机场","北京动物园",
				 "王府井","三里屯", "世贸天阶", "西单", "蓝色港湾", "798艺术区", "五道口"));
		for(int i = 0; i < places.size(); i += 1) {
			System.out.print("u\"" + places.get(i) + "\",");
		}
		System.out.println();
		List<String> lines = new ArrayList<String>();
		lines.add("月份,place,heat");
		for(int i = 1; i <= 12; ++ i) {
			TimeFilter.TimeType tt=new TimeFilter.MonthFilter(i);
			PhotoFilter pf = PhotoFilter.getFilter(tt);
			List<Photo> selected = PhotoFilter.filterPhotos(photos, pf);
			System.out.println(i + "月：>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			List<GeoAreaGroup> gaps = GeoAreaGroup.getGeoAreaMap(Photo.photoSelectedAllHotSpots);
			Map<String, Double> sum = getGroupWeight(gaps, selected, 30);
			for (int j = 0; j < places.size(); j += 1) {
				int weight = (int)(double)(sum.get(places.get(j)));
				lines.add(i + "," + places.get(j) + "," + weight);
			}
		}
		FileUtil.NewFile("G:/ASR/school/draw/本地居民12月热力图/data.csv", lines);
	}
	
	public static void main(String[] args)
	{
		test12Months();
		//rewriteGaps(Photo.photoSelectedNotBeijingHotSpots);
		//mergeGaps();
	}
	
	public static class GeoArea {

		public double minX;
		public double minY;
		public double maxX;
		public double maxY;
		
		public String name;
		public Set<String> userIds;
		
		public GeoArea(String line) {
			String[] ss = line.split(",");
			this.name = ss[0];
			this.minX = Double.parseDouble(ss[1]);
			this.maxX = Double.parseDouble(ss[2]);
			this.minY = Double.parseDouble(ss[3]);
			this.maxY = Double.parseDouble(ss[4]);
			this.userIds = new HashSet<String>();
		}
		
		boolean containPoint(MyPoint mp) {
			if (mp.x >= this.minX && mp.x < this.maxX && mp.y >= this.minY && mp.y < this.maxY) {
				return true;
			}
			return false;
		}
		
		@Override
		public String toString() {
			return this.name + "," + this.minX + "," + this.maxX + "," + this.minY + "," + this.maxY;
		}
		
		public static List<GeoArea> loadGeoAreas(String path) {
			List<String> lines = FileUtil.getLinesFromFile(path);
			List<GeoArea> gas = new ArrayList<GeoArea>();
			for (String line: lines) {
				gas.add(new GeoArea(line));
			}
			return gas;
		}
	}

}
