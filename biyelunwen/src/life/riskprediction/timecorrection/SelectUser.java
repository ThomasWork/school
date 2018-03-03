package life.riskprediction.timecorrection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import life.riskprediction.BankDetail;
import life.riskprediction.BasicFeature;
import life.riskprediction.BillDetail;
import life.riskprediction.BrowseHistory;
import life.riskprediction.LoanTime;
import life.riskprediction.MainConfigure;
import life.riskprediction.OverDue;
import life.riskprediction.User;
import life.riskprediction.UserInfo;
import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.fileprocess.FileProcessLine;
import myutil.fileprocess.FileUtil;

public class SelectUser
{
	public static List<UserInfo> selectUsersInfo(int n){
		List<UserInfo> users=UserInfo.getUsersInfo();
		List<UserInfo> selected=new ArrayList<UserInfo>();
		int number=users.size()/n;
		for(UserInfo info: users){
			int temp=NumberUtil.getRandom(number);
			if(0==temp)
				selected.add(info);
		}
		System.out.println("随机获得用户信息数量："+selected.size());
		return selected;
	}
	
	public static List<UserInfo> selectUsersInfo(Set<Integer> ids){
		List<UserInfo> users=UserInfo.getUsersInfo();
		List<UserInfo> selected=new ArrayList<UserInfo>();
		for(UserInfo info: users){
			if(ids.contains(info.id))
				selected.add(info);
		}
		System.out.println("指定用户ID获得用户信息数量："+selected.size());
		return selected;
	}
	
	public static Set<Integer> getUserIds(List<User> users){
		Set<Integer> ids=new HashSet<Integer>();
		for(User u: users){
			ids.add(u.userInfo.id);
		}
		return ids;
	}
	
	public static Map<Integer, List<BankDetail>> setUsersBankDetails(final List<User> users){
		final Set<Integer> userIds=getUserIds(users);
		final Map<Integer, List<BankDetail>> map=new TreeMap<Integer, List<BankDetail>>();
		FileProcessLine.processFile(MainConfigure.bankDetailPath, new FileProcessLine(){
			@Override
			public void parseLine(String line)
			{
				BankDetail detail=new BankDetail(line);
				if(!userIds.contains(detail.userId))
					return;
				List<BankDetail> details=map.get(detail.userId);
				if(null==details){
					details=new ArrayList<BankDetail>();
					map.put(detail.userId, details);
				}
				details.add(detail);
			}
		});
		for(User u: users){
			u.setBankDetails(map.get(u.userInfo.id));
		}
		return map;
	}
	
	public static Map<Integer, List<BillDetail>> setUsersBillDetails(final List<User> users){
		final Set<Integer> userIds=getUserIds(users);
		final Map<Integer, List<BillDetail>> map=new TreeMap<Integer, List<BillDetail>>();
		FileProcessLine.processFile(MainConfigure.billDetailPath, new FileProcessLine(){
			@Override
			public void parseLine(String line)
			{
				BillDetail detail=new BillDetail(line);
				if(!userIds.contains(detail.userId))
					return;
				List<BillDetail> details=map.get(detail.userId);
				if(null==details){
					details=new ArrayList<BillDetail>();
					map.put(detail.userId, details);
				}
				details.add(detail);
			}
		});
		for(User u: users){
			u.setBillDetails(map.get(u.userInfo.id));
		}
		return map;
	}
	
	public static Map<Integer, List<BrowseHistory>> setUsersBrowseHistorys(final List<User> users, String path){
		final Set<Integer> userIds=getUserIds(users);
		final Map<Integer, List<BrowseHistory>> map=new TreeMap<Integer, List<BrowseHistory>>();
		FileProcessLine.processFile(path, new FileProcessLine(){
			@Override
			public void parseLine(String line)
			{
				BrowseHistory history=new BrowseHistory(line);
				if(!userIds.contains(history.userId))
					return;
				List<BrowseHistory> historys=map.get(history.userId);
				if(null==historys){
					historys=new ArrayList<BrowseHistory>();
					map.put(history.userId, historys);
				}
				historys.add(history);
			}
		});
		for(User u: users){
			u.setBrowseHistory(map.get(u.userInfo.id));
		}
		return map;
	}
	
	public static void setUsersBrowseHistorysFromDatabase(final List<User> users){
		for(int i=0; i<users.size(); ++i){
			User u=users.get(i);
			if(i%30==0)
				System.out.println("设置浏览记录数量："+i);
			u.setBrowseHistory(BrowseHistory.DatabaseProcess.getBrowseHistory(u.userInfo.id));
		}
	//	System.gc();
	}
	
	public static Map<Integer, LoanTime> setUsersLoanTime(final List<User> users){
		return setUsersLoanTime(users, MainConfigure.loanTimePath);
	}
	
	public static Map<Integer, LoanTime> setUsersLoanTime(final List<User> users, String path){
		final Set<Integer> userIds=getUserIds(users);
		final Map<Integer, LoanTime> map=new TreeMap<Integer, LoanTime>();
		FileProcessLine.processFile(path, new FileProcessLine(){
			@Override
			public void parseLine(String line)
			{
				LoanTime lt=new LoanTime(line);
				if(!userIds.contains(lt.userId))
					return;
				map.put(lt.userId, lt);
			}
		});
		for(User u: users){
			u.loanTime=map.get(u.userInfo.id);
		}
		return map;
	}
	
	public static Map<Integer, OverDue> setUsersOverDue(final List<User> users){
		return setUsersOverDue(users, MainConfigure.overDuePath);
	}
	
	public static Map<Integer, OverDue> setUsersOverDue(final List<User> users, String path){
		final Set<Integer> userIds=getUserIds(users);
		final Map<Integer, OverDue> map=new TreeMap<Integer, OverDue>();
		FileProcessLine.processFile(path, new FileProcessLine(){
			@Override
			public void parseLine(String line)
			{
				OverDue od=new OverDue(line);
				if(!userIds.contains(od.userId))
					return;
				map.put(od.userId, od);
			}
		});
		for(User u: users){
			u.overDue=map.get(u.userInfo.id);
		}
		return map;
	}
	
	public static List<User> selectUsers(int n){
		List<UserInfo> infos=selectUsersInfo(n);		
		List<User> users=new ArrayList<User>();
		for(UserInfo info: infos){
			User u=new User(info);
			users.add(u);
		}
	//	SelectUser.setUsersBankDetails(users);
	//	SelectUser.setUsersBillDetails(users);
	//	SelectUser.setUsersLoanTime(users);
	//	SelectUser.setUsersOverDue(users);
		SelectUser.setUsersBrowseHistorysFromDatabase(users);
		return users;
	}
	
	public static void getBasicFeature(){
		List<User> users=selectUsers(100);
		List<BasicFeature> basics=new ArrayList<BasicFeature>();
		for(User u: users){
			basics.add(new BasicFeature(u));
		}
	//	List<BasicFeature> 
	//	basics=BasicFeature.getBasicFeature();
		List<String> content=new ArrayList<String>();
		for(BasicFeature bf: basics){
			content.add(bf.getLine());
		}
		FileUtil.NewFile(MainConfigure.basicFeaturePath, content);
	}
	
	public static void testSelectUser(){
		List<User> users=selectUsers(100);
		for(User u: users){
			if(u.bankDetails.size()>0 && u.billDetails.size()>0){
				u.showUser();
				System.out.println("\n\n\n");
			}
		}
	}
	
	public static void getUsers(){
		List<Integer> idas=Arrays.asList(37114);//(37114, 48758, 45154, 10819, 30013, 40678);
		Set<Integer> ids=new TreeSet<Integer>(idas);
		List<UserInfo> infos=selectUsersInfo(ids);		
		List<User> users=new ArrayList<User>();
		for(UserInfo info: infos){
			User u=new User(info);
			users.add(u);
		}
	//	SelectUser.setUsersBankDetails(users);
	//	SelectUser.setUsersBillDetails(users);
		SelectUser.setUsersBrowseHistorysFromDatabase(users);
		for(User u: users){
			u.showUser();
		}
	}
	
	public static void main(String[] args){
	//	testSelectUser();
		selectUsers(200);
	//	getUsers();
	//	getBasicFeature();
	}
}
