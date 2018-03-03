package life.riskprediction.timecorrection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import life.riskprediction.BrowseHistory;
import life.riskprediction.MainConfigure;
import life.riskprediction.User;
import life.riskprediction.UserInfo;
import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.feature.FeatureMatrix;
import myutil.feature.FeatureMatrix.Feature;
import myutil.fileprocess.FileUtil;

public class TestBrowseHistory
{
	//统计浏览每种数据的次数
	public static Map<String, Integer> countEachBrowse(List<BrowseHistory> historys){
		Map<String, Integer> count=new TreeMap<String, Integer>();
		List<String> temp=new ArrayList<String>();
		for(BrowseHistory history: historys){
			temp.add(history.browseType+","+history.subBrowseNum);
		}
		count=StringUtil.countFrequency(temp);
		return count;
	}
	
	public static int[] countEachBrowse(List<BrowseHistory> historys, Map<String, Integer> indexMap){
		int[] temp=new int[indexMap.size()];
		for(int i=0; i<temp.length; ++i){
			temp[i]=0;//初始化每一种为0
		}
		for(BrowseHistory bh: historys){
			Integer index=indexMap.get(bh.browseType+","+bh.subBrowseNum);
			if(null != index)
				temp[index]++;//否则，什么都不做
		}
		return temp;
	}
	
	//得到所有的数据以及子编号的组合
	public static Map<String, Integer> getAllBrowseTypeFromUsers(List<User> users){
		Set<String> keySet=new TreeSet<String>();
		for(User u: users){
		//	keySet.addAll(u.beforeBrowseCount.keySet());
		//	keySet.addAll(u.afterBrowseCount.keySet());
		}
		List<String> keysArray=new ArrayList<String>(keySet);
		System.out.println("所有的浏览类型："+keysArray.size());
		return StringUtil.getIndexMap(keysArray);
	}
	
	public static Map<String, Integer> getAllBrowseTypeFromFile(){
		String path=MainConfigure.browseFeatureKeyPath;
		List<String> lines=FileUtil.getLinesFromFile(path);
		Map<String, Integer> keyIndex=StringUtil.getIndexMap(lines);
		System.out.println("选中类型："+keyIndex.size());
		return keyIndex;
	}
	
	
	private static int[] fillMapKeys(List<String> keys, Map<String, Integer> map){
		int[] array=new int[keys.size()];
		for(int i=0; i<keys.size(); ++i){
			String key=keys.get(i);
			Integer temp=map.get(key);
			if(null==temp)
				array[i]=0;
			else
				array[i]=temp;
		}
		return array;
	}
	
	public static void setUserCompleteKeys(List<User> users){
	/*	List<String> allKeys=new ArrayList<String>(getAllBrowseType(users));
		for(User u: users){
			u.beforeBrowseArray=fillMapKeys(allKeys, u.beforeBrowseCount);
			u.afterBrowseArray=fillMapKeys(allKeys, u.afterBrowseCount);
		}*/
	}
	
	public static void getUsersBrowseFeature(){
		List<UserInfo> infos=UserInfo.getUsersInfo();
	//	infos=infos.subList(7,  80);
		List<User> users=new ArrayList<User>();
		for(UserInfo info: infos){
			User u=new User(info);
			users.add(u);
		}
		SelectUser.setUsersLoanTime(users);//设置贷款时间
		SelectUser.setUsersOverDue(users);//设置还款状态
	//	SelectUser.setUsersBankDetails(users);
	//	SelectUser.setUsersBillDetails(users);
		SelectUser.setUsersBrowseHistorysFromDatabase(users);//注意这里会把它清空
	//	TestBrowseHistory.setUserCompleteKeys(users);
		List<String> content=new ArrayList<String>();
		for(int i=0; i<users.size(); ++i){
			User u=users.get(i);
			content.add(u.showBrowseCount());
		}
		FileUtil.NewFile(MainConfigure.browseFeaturePath, content);
	}
	
	//获得用户的测试数据的特征
	public static void getUsersTestBrowseFeature(){
		List<UserInfo> infos=UserInfo.getUsersInfo(MainConfigure.userInfoPath_Test);
		//	infos=infos.subList(7,  80);
			List<User> users=new ArrayList<User>();
			for(UserInfo info: infos){
				User u=new User(info);
				users.add(u);
			}
			SelectUser.setUsersLoanTime(users, MainConfigure.loanTimePath_Test);//设置贷款时间
		//	SelectUser.setUsersOverDue(users, MainConfigure.overDuePath_Test);//设置还款状态
		//	SelectUser.setUsersBankDetails(users);
		//	SelectUser.setUsersBillDetails(users);
			SelectUser.setUsersBrowseHistorys(users, MainConfigure.browseHistoryPath_Test);//注意这里会把它清空
		//	TestBrowseHistory.setUserCompleteKeys(users);
			List<String> content=new ArrayList<String>();
			for(int i=0; i<users.size(); ++i){
				User u=users.get(i);
				content.add(u.showBrowseCount());
			}
			String savePath=MainConfigure.browseFeaturePath_Test;
			FileUtil.NewFile(savePath, content);
			double[][] matrix=FeatureMatrix.getMatrix(savePath, ",", true);
			boolean[] used=getBrowseFeatureDescription();//得到需要使用的列
			FeatureMatrix.writeMatrix(FeatureMatrix.getMatrix(used, matrix), NumberUtil.dfint, savePath+"new.txt");
	}
	
	public static List<String> getFeatureNames(){
		List<String> lines=FileUtil.getLinesFromFile(MainConfigure.browseFeatureKeyPath);
		List<String> names=new ArrayList<String>();
		for(String line: lines){
			String[] ss=line.split(",");
			names.add("before_30_"+ss[0]+"_"+ss[1]);
		}
		for(String line: lines){
			String[] ss=line.split(",");
			names.add("after_30_"+ss[0]+"_"+ss[1]);
		}
		return names;
	}
	
	public static boolean[] getBrowseFeatureDescription(){
		
		List<String> names=getFeatureNames();
		
		String path=MainConfigure.browseFeaturePath;
		double[][] matrix=FeatureMatrix.getMatrix(path, ",", true);
		List<List<Double>> columns=FeatureMatrix.getFeatureColumn(matrix);
		boolean[] used=new boolean[columns.size()];
		for(int i=0; i<used.length; ++i)//第一列肯定是需要使用的
			used[i]=true;
		Feature f0=new Feature("user_id", columns.get(0));
		f0.setParameters(0, 0, matrix.length-1);
		System.out.println(f0);
		for(int i=1; i<columns.size(); ++i){
			List<Double> column=columns.get(i);
			Feature f=new Feature(names.get(i-1), column);
			f.setParameters(1000, 1000, 5);
			if(f.max<=0.001 && f.min<=0.001)
				used[i]=false;
			else
				System.out.println(f);
		}
	//	FeatureMatrix.writeMatrix(FeatureMatrix.getMatrix(used, matrix), NumberUtil.dfint, MainConfigure.browseFeaturePath+"new.txt");
		return used;
	}
	
	public static void getUsersBrowseFeature_test(){
		List<UserInfo> infos=UserInfo.getUsersInfo();
		infos=infos.subList(7,  8);
		List<User> users=new ArrayList<User>();
		for(UserInfo info: infos){
			User u=new User(info);
			users.add(u);
		}
		SelectUser.setUsersLoanTime(users);//设置贷款时间
		SelectUser.setUsersOverDue(users);//设置还款状态
		User u0=users.get(0);
		long msb=TimeCorrection.monthSecBigger;
		u0.loanTime.time=msb+100;
		List<BrowseHistory> bhs=new ArrayList<BrowseHistory>();
		bhs.add(new BrowseHistory("1,0,118,1"));//时间为0排除，正确
		bhs.add(new BrowseHistory("1,99,118,1"));//时间不在范围内排除，正确
		bhs.add(new BrowseHistory("1,110,118,1"));
		bhs.add(new BrowseHistory("1,120,118,1"));
		bhs.add(new BrowseHistory("1,"+(msb+100+msb-10)+",118,1"));
		bhs.add(new BrowseHistory("1,"+(msb+100+msb+20)+",118,1"));
		u0.setBrowseHistory(bhs);
	//	TestBrowseHistory.setUserCompleteKeys(users);
		for(int i=0; i<users.size(); ++i){
			User u=users.get(i);
			u.showBrowseCount();
		}		
	}
	
	public static void main(String[] args){
		//getUsersBrowseFeature();
		//getBrowseFeatureDescription();
		getUsersTestBrowseFeature();
	}
}
