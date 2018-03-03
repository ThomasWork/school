package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import draw.KmlFile;
import trajectory.GeoTrajectory;
import trajectory.UserGeoTrajectory;
import entity.filter.PhotoFilter;
import entity.filter.TimeFilter;
import entity.filter.UserFilter;
import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;

public class UserStatistic
{
	public static String statisticFolder="statistics/all/";
		
	//统计每个时间段上传了多少照片
	public static void countUserUploads()
	{
		List<User> users=User.getUsersWithPhotos();
		List<String> total=new ArrayList<String>();
		for(User u: users)
		{
		//	total.addAll(u.getDateString(DateUtil.DateField.year_month));
			total.addAll(u.getDateString(DateUtil.DateField.hour));
		}
		Map<String, Integer> dataMap=StringUtil.countFrequencyWithNumber(total);
		for(Entry<String, Integer> entry: dataMap.entrySet())
		{
			System.out.println(entry.getKey()+","+entry.getValue());
		}
	}
	
	//统计每个用户上传了多少照片
	public static void countEachUserUpload(){
		List<User> users=User.getUsersWithPhotos();
		List<Integer> count =new ArrayList<Integer>();
		for(User u: users)
		{
			count.add(u.photosList.size());
			System.out.println(u.photosList.size());
		}		
	}
	
	//统计每个用户的滞留时间
	public static void countEachUserStay(){
		List<User> users=User.getUsersWithPhotos();
		List<Double> nums=new ArrayList<Double>();
		for(User u: users)
		{
			nums.add(Photo.getMaxHourDis(u.photosList));
		}
		Double[] thresholds={24.0, 48.0, 72.0, 96.0, 120.0, 144.0};
		int[] count=NumberUtil.countFrequency(nums, thresholds);
		for(int i=0; i<count.length; ++i)
			System.out.println(count[i]);
		List<String> output=new ArrayList<String>();
		String temp1="<"+thresholds[0]/24;
		String temp2=count[0]+"";
		double sum=count[0];
		String temp3=sum/nums.size()+"";
		for(int i=1; i<count.length; ++i){
			if(i==count.length-1)
				temp1+=",>"+thresholds[i-1]/24;
			else
				temp1+=","+thresholds[i-1]/24+"-"+thresholds[i]/24;
			temp2+=","+count[i];
			sum+=count[i];
			temp3+=","+sum/nums.size();
		}
		output.add(temp1.replace(".0", ""));
		output.add(temp2);
		output.add(temp3);
		FileUtil.NewFile(WorkSituation.pythonDrawDir+"12-轨迹-滞留时间-柱状图/data.txt", output);
	//	System.out.println(output);
	}
	
	//统计世界上的其他人的
	public static void writeWorldParData(){
		Map<String, List<Integer>> yearMap=new HashMap<String, List<Integer>>();
		List<String> parts=Arrays.asList("北美洲",            "欧洲",               "中国",             "亚洲",                 "南美洲",              "非洲",               "大洋洲",              "南极洲");
		List<String> ps=Arrays.asList("120,30,161,207,72",   "330,30,245,231,196", "580,100,251,58,0", "460,120,221,202,222", "180,210,204,152,102", "340,160,243,210,97", "550,210,238,174,174", "360,330,211,237,250");
		Map<String, String> points=new HashMap<String, String>();
		for(int i=0; i<parts.size(); ++i)
			points.put(parts.get(i), ps.get(i));
		
		for(int i=0; i<parts.size(); ++i){
			yearMap.put(parts.get(i), new ArrayList<Integer>());
		}
		for(int year=2011; year<=2015; ++year){
			TimeFilter.TimeType tt=new TimeFilter.YearFilter(year);
			PhotoFilter pf=PhotoFilter.getFilter(tt);
			List<Photo> photos=PhotoFilter.getFilteredPhotos(pf);
			List<User> users=User.setPhotoUsersAndGetUsers(photos);
			Map<String, Integer> count=User.countUserData(users);
			for(int i=0; i<parts.size(); ++i){
				String part=parts.get(i);
				Integer p=count.get(part);
				if(null==p)
					p=0;
				yearMap.get(part).add(p);
			}
		}
		List<String> out=new ArrayList<String>();
		for(int i=0; i<parts.size(); ++i){
			String part=parts.get(i);
			StringBuilder sb=new StringBuilder(part+",");
			sb.append(points.get(part)+",");
			List<Integer> nums=yearMap.get(part);
			for(int j=0; j<nums.size(); ++j)
				sb.append(nums.get(j)+",");
			System.out.println(sb);
			out.add(sb.toString());
		}
		FileUtil.NewFile("E:/MyProject/VS2010/Network_16_10_17/DrawTest/bars.txt", out);
	}
	
	public static void countUserPhotoBehavior(){
		List<User> users=User.getUsersWithPhotos();
		for(User u: users){
			u.getUserPhotoTime();
		}
	}
	
	private static int countNoLocalNum(List<User> users){
		int total=0;
		for(User u: users){
			if(!u.isBeijing)
				total++;
		}
		return total;
	}
	
	private static String countNoLocalRate(Map<Integer, List<User>> numMap, int totalNum){
		String rateList="";
		double sum=0;
		for(Map.Entry<Integer, List<User>> entry: numMap.entrySet()){
			List<User> users=entry.getValue();
			sum+=countNoLocalNum(users);
			rateList+=sum/totalNum+",";
		}
		return rateList;
	}
	
	//测试分割时间对不同轨迹用户数量，以及总的轨迹数量的影响，过时
	public static void testSplitTimeEffect_OLD(){
		List<User> users=User.getUsersWithFromPlace();
	//	List<User> users=UserFilter.getLocalUsersWithSourceLocation();
		int totalNoLocalNum=countNoLocalNum(users);
		System.out.println("totalNoLocalNum"+totalNoLocalNum);
		List<String> userNumMatrix=new ArrayList<String>();
		List<String> traNumMatrix=new ArrayList<String>();
		List<String> rateMatrix=new ArrayList<String>();
		int [] hours={6, 12, 24, 48, 72};
	//	int [] hours={24};
		for(int hoursIndex=0; hoursIndex<hours.length; ++hoursIndex){
			Map<Integer, List<GeoTrajectory>> numMap=new TreeMap<Integer, List<GeoTrajectory>>();//保存轨迹数目为N的用户的所有轨迹
			Map<Integer, List<User>> idMap=new TreeMap<Integer, List<User>>();
			int maxTraNum=16;//最多只统计到10的轨迹
			Integer thres[]=new Integer[maxTraNum];
			for(int i=0; i<thres.length; ++i)
				thres[i]=i;
			for(int i=1; i<=maxTraNum; ++i){
				numMap.put(i, new ArrayList<GeoTrajectory>());
				idMap.put(i, new ArrayList<User>());
			}
			for(User u: users){
				List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectorysFromUser(u, hours[hoursIndex]);
				int size=tras.size();//该用户的轨迹数目
				if(size>=maxTraNum)
					size=maxTraNum;
				numMap.get(size).addAll(tras);
				idMap.get(size).add(u);
			}
			String tempUserNum="", tempTraNum="";
			for(int traNum=1; traNum<maxTraNum; ++traNum){//这里不统计尾巴的部分！！！！
				List<GeoTrajectory> tras=numMap.get(traNum);
			//	System.out.println(traNum+"\t"+idMap.get(traNum).size()+"\t"+tras.size()+"\t"+GeoTrajectory.countTrasPointsNum(tras));
				tempUserNum+=idMap.get(traNum).size()+",";
				tempTraNum+=tras.size()+",";
			}
			userNumMatrix.add(tempUserNum);
			traNumMatrix.add(tempTraNum);
			rateMatrix.add(countNoLocalRate(idMap, totalNoLocalNum));
		//	for(int j=0; j<count.length; ++j)
		//		System.out.println(count[j]);
		}
	//	FileUtil.NewFile(WorkSituation.pythonDrawDir+"12-轨迹-切割时间/data.txt", traNumMatrix);
	//	FileUtil.NewFile(WorkSituation.pythonDrawDir+"12-轨迹-切割时间/data.txt", userNumMatrix);
		FileUtil.NewFile(WorkSituation.pythonDrawDir+"12-轨迹-切割时间/data.txt", rateMatrix);
	}
	
	//测试分割时间对不同轨迹用户数量，以及总的轨迹数量的影响，过时
		public static void testSplitTimeEffect(){
		//	List<User> users=User.getUsersWithFromPlace();
		//	List<User> users=UserFilter.getLocalUsersWithSourceLocation();
			List<User> users=User.getUsersWithPhotos();
			int totalNoLocalNum=countNoLocalNum(users);
		//	System.out.println("totalNoLocalNum"+totalNoLocalNum);
			List<String> userNumMatrix=new ArrayList<String>();
			List<String> traNumMatrix=new ArrayList<String>();
			List<String> rateMatrix=new ArrayList<String>();
			int [] hours={96, 72, 48, 24, 12};
		//	int [] hours={24};
			for(int hoursIndex=0; hoursIndex<hours.length; ++hoursIndex){
				Map<Integer, List<GeoTrajectory>> numMap=new TreeMap<Integer, List<GeoTrajectory>>();//保存轨迹数目为N的用户的所有轨迹
				Map<Integer, List<User>> idMap=new TreeMap<Integer, List<User>>();
				int maxTraNum=12;//最多只统计到10的轨迹
				Integer thres[]=new Integer[maxTraNum];
				for(int i=0; i<thres.length; ++i)
					thres[i]=i;
				for(int i=1; i<=maxTraNum; ++i){
					numMap.put(i, new ArrayList<GeoTrajectory>());
					idMap.put(i, new ArrayList<User>());
				}
				for(User u: users){
					List<GeoTrajectory> tras=UserGeoTrajectory.getTrajectorysFromUser(u, hours[hoursIndex]);
					int size=tras.size();//该用户的轨迹数目
					if(size>=maxTraNum)
						size=maxTraNum;
					numMap.get(size).addAll(tras);
					idMap.get(size).add(u);
				}
				String tempUserNum="", tempTraNum="";
				for(int traNum=1; traNum<=maxTraNum; ++traNum){//这里不统计尾巴的部分！！！！
					List<GeoTrajectory> tras=numMap.get(traNum);
				//	System.out.println(traNum+"\t"+idMap.get(traNum).size()+"\t"+tras.size()+"\t"+GeoTrajectory.countTrasPointsNum(tras));
					tempUserNum+=idMap.get(traNum).size()+",";
					tempTraNum+=tras.size()+",";
				}
				userNumMatrix.add(tempUserNum);
				traNumMatrix.add(tempTraNum);
				rateMatrix.add(countNoLocalRate(idMap, totalNoLocalNum));
			//	for(int j=0; j<count.length; ++j)
			//		System.out.println(count[j]);
			}
		//	FileUtil.NewFile(WorkSituation.pythonDrawDir+"12-轨迹-切割时间/data.txt", traNumMatrix);
			FileUtil.NewFile(WorkSituation.pythonDrawDir+"12-轨迹-切割时间/data.txt", userNumMatrix);
		//	FileUtil.NewFile(WorkSituation.pythonDrawDir+"12-轨迹-切割时间/data.txt", rateMatrix);
		}
	
	public static void testUserFrom(){
		List<User> users=User.getUsersInfoList();
		int unknown=0, beijing=0;;
		for(User u: users){
			if(u.sourceLocation.equals("unknown"))
				unknown++;
			else
				System.out.println(u.sourceLocation);
			if(u.isBeijing)
				beijing++;
		}
		int knownPlace=users.size()-unknown;
		System.out.println("位置已知的用户数目："+knownPlace+"占比为："+knownPlace*1.0/users.size());
		System.out.println("北京用户数目："+beijing);
	}
	
	
	
	public static void main(String[] args)
	{
	//	countUserUploads();
//		countEachUserUpload();
	//	countEachUserStay();
	//	countUserPhotoBehavior();
	//	testBeijingAndWaiDiYouke();
	//	testSplitTimeEffect();
		testUserFrom();
	}

}
