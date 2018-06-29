package trajectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.helper.StringUtil;

import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.fileprocess.FileUtil;
import sciencecluster.MyPoint;
import entity.GeoAreaGroup;
import entity.Photo;
import entity.User;

public class GeoTrajectoryPhoto
{
	public String id = "";
	public List<Photo> photos;
	public User user;

	public GeoTrajectoryPhoto(String idPar, User u, List<Photo> ps)
	{
		this.id = idPar;
		this.photos = ps;
		this.user = u;
	}

	// 从照片列表中获得照片分段列表
	private static List<GeoTrajectoryPhoto> getPhotosListFromPhotosList(User user, double maxHourDis)
	{
		List<GeoTrajectoryPhoto> tras = new ArrayList<GeoTrajectoryPhoto>();
		Photo.sortPhotos(user.photosList);// 首先对所有的距离进行排序
		List<Photo> temp = new ArrayList<Photo>();// 保存轨迹
		String start, finish;
		int total = 0;
		for (int i = 0; i < user.photosList.size(); ++i)
		{
			Photo cur = user.photosList.get(i);
			if (temp.size() == 0)
			{
				temp.add(cur);
				continue;
			}
			Photo pre = temp.get(temp.size() - 1);// 获取前一个
			double hourDis = DateUtil.getDateDisHour(cur.dateTaken,
					pre.dateTaken);
			if (hourDis < 0)
				System.out.println("时间差为：" + hourDis);
			if (hourDis <= maxHourDis) {// 在最大时间差范围之内 
				temp.add(cur);
			} else
			{// 表示时间差过大，一个轨迹已经终结
				// System.out.println(cur.longitude);
				start = DateUtil.sdfDHM.format(temp.get(0).dateTaken);
				finish = DateUtil.sdfDHM.format(temp.get(temp.size() - 1).dateTaken);
				tras.add(new GeoTrajectoryPhoto(start + "_"
						+ finish, pre.user, temp));
				temp = new ArrayList<Photo>();// 这里必须要这样，不能清空
				temp.add(cur);
			}
		}
		if (temp.size() > 0)
		{// 表示还有尾巴没有处理
			start = DateUtil.sdfDHM.format(temp.get(0).dateTaken);
			finish = DateUtil.sdfDHM
					.format(temp.get(temp.size() - 1).dateTaken);
			tras.add(new GeoTrajectoryPhoto(start + "_" + finish, temp.get(0).user, temp));
		}
		return tras;
	}

	//对北京用户按照星期进行切割
	private static List<GeoTrajectoryPhoto> getPhotosListFromPhotosListSplitWeek(User user)
	{
		List<GeoTrajectoryPhoto> tras = new ArrayList<GeoTrajectoryPhoto>();
		Photo.sortPhotos(user.photosList);// 首先对所有的距离进行排序
		List<Photo> temp = new ArrayList<Photo>();// 保存轨迹
		Date init = DateUtil.getDate("2009-12-28 00:00:00");
		Map<Integer, List<Photo>> photos = new TreeMap<Integer, List<Photo>>();
		for (int i = 0; i < user.photosList.size(); ++i)
		{
			Photo p = user.photosList.get(i);
			int dis = (int) (DateUtil.getDateDisWeek(p.dateTaken, init));
			List<Photo> ps = photos.get(dis);
			if (null == ps) {
				ps = new ArrayList<Photo>();
				photos.put(dis, ps);
			}
			ps.add(p);
		}
		for (Map.Entry<Integer, List<Photo>> entry : photos.entrySet()) {
			List<Photo> ps = entry.getValue();
			Photo.sortPhotos(ps);
			String start = DateUtil.sdfDHM.format(ps.get(0).dateTaken);
			String end = DateUtil.sdfDHM.format(ps.get(ps.size() - 1).dateTaken);
			tras.add(new GeoTrajectoryPhoto(start + "_" + end, user, ps));
		}
		return tras;
	}
	
	// 在这里更新了数据点的用户id，因为不想改变照片的用户id
	public GeoTrajectory getGeoTrajectory()
	{
		List<MyPointWithTime> mpts = new ArrayList<MyPointWithTime>();
		for (Photo p : this.photos)
		{
			MyPointWithTime mpt = new MyPointWithTime(p);
			mpt.point.userId = this.id;
			mpts.add(mpt);
		}
		return new GeoTrajectory(this.id, mpts);
	}

	public static List<GeoTrajectory> getGeoTrajectories(
			List<GeoTrajectoryPhoto> tps)
	{
		List<GeoTrajectory> tras = new ArrayList<GeoTrajectory>();
		for (GeoTrajectoryPhoto tp : tps)
		{
			tras.add(tp.getGeoTrajectory());
		}
		return tras;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(this.id + "\n");
		for (int i = 0; i < this.photos.size(); ++i)
		{
			Photo mp = this.photos.get(i);
			sb.append("," + mp.userId + "," + mp.longitude + "," + mp.latitude);
		}
		return sb.toString();
	}

	/******************************            毕业论文                   *******************************************/
	public static void countFrequency(Map<String, List<String>> paths) {
		List<String> all = new ArrayList<String>();
		for (Map.Entry<String, List<String>> entry: paths.entrySet()) {
			all.addAll(entry.getValue());
		}
		System.out.println(paths.size());
		System.out.println(all.size());
		Map<String, Integer> counts = myutil.StringUtil.countFrequency(all);
	}
	
	public static List<String> selectString(List<String> source, List<String> selected) {
		List<String> ret = new ArrayList<String>();
		for(String ele: source) {
			if (selected.contains(ele)) {
				ret.add(ele);
			}
		}
		return ret;
	}
	
	public static void testCutTime() {
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedNotBeijingUser);
		List<User> users = User.getUsersWithPhotos(photos);
		List<Double> cuts = Arrays.asList(72.0);
		for (int i = 0; i < cuts.size();i += 1) {
			double cut = cuts.get(i);
			List<Integer> counts = new ArrayList<Integer>();
			for (int j = 0; j < users.size(); j += 1) {
				User u = users.get(j);
				List<GeoTrajectoryPhoto> gtps = getPhotosListFromPhotosList(u, cut);
				if (gtps.size() > 3) {
					counts.add(gtps.size());
					u.drawAllPoints();
				}
			}
			System.out.println(counts.size());
			//System.out.println(counts);
		}
	}
	
	public static void splitEight() {
		List<GeoAreaGroup> gaps = GeoAreaGroup.getGeoAreaMap(Photo.photoSelectedAllHotSpots);
		List<Photo> photos = Photo.getPhotos(Photo.photoSelectedNotBeijingUser);
		List<User> users = User.getUsersWithPhotos(photos);
		List<String> lines = new ArrayList<String>();
		List<GeoTrajectoryPhoto> splits = new ArrayList<GeoTrajectoryPhoto>();
		for (User u: users) {
			splits.addAll(getPhotosListFromPhotosList(u, 8));
		}
		int visitCount = 0;
		for (GeoTrajectoryPhoto ps: splits) {
			Photo.sortPhotos(ps.photos);
			List<String> places = new ArrayList<String>();
			List<String> tags = new ArrayList<String>();
			for (Photo p: ps.photos) {
				for (GeoAreaGroup gap: gaps) {
					if (gap.contains(p)) {
						String place = gap.name;
						if (places.size() <= 0) {
							places.add(place);
							tags.add(place + DateUtil.sdfDay.format(p.dateTaken));
							break;
						}
						if (places.get(places.size() - 1).equals(place) == false) {
								places.add(place);
								tags.add(place + p.id + " " + DateUtil.sdfDHM.format(p.dateTaken));
						}
						break;
					}
				}
			}
			String list = StringUtil.join(places, ",");
			if (list.length() > 0) {
				//System.out.println(list);
				visitCount += places.size();
				lines.add(list);
			}
		}
		System.out.println("path num: " + lines.size() + "," + visitCount * 1.0 / lines.size());
		FileUtil.NewFile(Photo.userTravelPlaces, lines);
	}
	
	public static void getAllTrace(String path, List<String> selected) {
		List<GeoAreaGroup> gaps = GeoAreaGroup.getGeoAreaMap(Photo.photoSelectedAllHotSpots);
		List<Photo> photos = Photo.getPhotos(path);
		List<User> users = User.getUsersWithPhotos(photos);
		List<GeoTrajectoryPhoto> splits = new ArrayList<GeoTrajectoryPhoto>();
		for (User u: users) {
			//splits.addAll(getPhotosListFromPhotosListSplitWeek(u));
			if (u.getInvalidEntityBecauseBadGPS() == false) {
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
				for (GeoAreaGroup gap: gaps) {
					if (gap.contains(p)) {
						String place = gap.name;
						if (places.size() <= 0) {
							places.add(place);
							break;
						}
						Date preDate = ps.photos.get(i - 1).dateTaken;
						double dis = DateUtil.getDateDisMinute(p.dateTaken, preDate);
						//System.out.println(dis);
						String prePlace = places.get(places.size() - 1);
						if (prePlace.equals(place) == false) {
							if (dis < 1) {
								System.out.println(p.userId + ",\t" + ps.photos.get(i-1) + "(" + prePlace + ")" + ",\t" + p + "(" + place + ")");
							} else {
								places.add(place);
							}
						} else if(dis > 480) {//最多8小时
							places.add(place);
						}
						break;
					}
				}
			}
			places = selectString(places, selected);
			if (places.size() > 0) {
				visits.put(ps.user.id + "_" + ps.id, places);
			}
		}
		countFrequency(visits);
		List<String> lines = new ArrayList<String>();
		for (Map.Entry<String, List<String>> entry: visits.entrySet()) {
			lines.add(entry.getKey() + "," + StringUtil.join(entry.getValue(), ","));
		}
		FileUtil.NewFile(Photo.userTravelPlaces, lines);
	}
	
	//对北京本地用户按照星期进行切割
	public static void splitBeijing() {
		getAllTrace(Photo.photoSelectedBeijingUser, Arrays.asList("798艺术区", "三里屯", "世贸天阶", "后海",
					"国际机场", "天安门广场", "故宫", "雍和宫", "颐和园", "鸟巢"));
	}
	
	public static void getAllTourist() {
		getAllTrace(Photo.photoSelectedTourist, Arrays.asList("故宫", "天安门广场", "天坛公园", "后海", "颐和园", "鸟巢", "王府井", "雍和宫", "国际机场", "慕田峪长城"));
	}
	
	public static void main(String[] args)
	{
		// test1();
		// getKeyTrajectory(GeoFilter.areaBeijing);
		// testGetTrajectorysFromPhotosList();//对函数进行测试
		//splitEight();
		//splitBeijing();
		getAllTourist();
		//testCutTime();
		//splitBeijing();
	}
}
