package life.riskprediction.tongji;

import java.util.ArrayList;
import java.util.List;

import life.riskprediction.User;
import life.riskprediction.UserInfo;
import life.riskprediction.timecorrection.SelectUser;
import life.riskprediction.timecorrection.TestBrowseHistory;

public class TestUser {
	
	public static void test1(){
		List<User> users=User.getUsers();
		SelectUser.setUsersOverDue(users);//设置还款状态
		SelectUser.setUsersBankDetails(users);
		SelectUser.setUsersBillDetails(users);
		SelectUser.setUsersBrowseHistorysFromDatabase(users);//注意这里会把它清空
	//	TestBrowseHistory.setUserCompleteKeys(users);
		for(int i=0; i<users.size(); ++i){
			User u=users.get(i);
			u.show3RecordCount();
		//	u.showBrowseCount();
		}
	}

	public static void main(String[] args) {
		test1();
	}

}
