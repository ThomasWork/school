package life.riskprediction;

import java.util.ArrayList;
import java.util.List;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;

public class UserInfo
{
	public int id;
	public String sex;
	public String occupation;
	public String education;
	public String marriage;
	public String accountType;
	
	public UserInfo(String line){
		String[] ss=line.split(",");
		this.id=Integer.parseInt(ss[0]);
		this.sex=ss[1];
		this.occupation=ss[2];
		this.education=ss[3];
		this.marriage=ss[4];
		this.accountType=ss[5];
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("ID："+this.id+"\t");
		sb.append("性别:"+this.sex+"\t");
		sb.append("职业:"+this.occupation+"\t");
		sb.append("教育:"+this.education+"\t");
		sb.append("婚姻:"+this.marriage+"\t");
		sb.append("账户类型:"+this.accountType);
		return sb.toString();
	}
	
	public static List<UserInfo> getUsersInfo(){
		String path=MainConfigure.userInfoPath;
		return getUsersInfo(path);
	}
	
	public static List<UserInfo> getUsersInfo(String path){
		System.out.println(path);
		List<String> lines=FileUtil.getLinesFromFile(path);
		List<UserInfo> infos=new ArrayList<UserInfo>();
		for(String line: lines)
			infos.add(new UserInfo(line));
		System.out.println("获得用户信息数量："+infos.size());
		return infos;
		
	}
	
	public static void countUsersInfo(){
		List<UserInfo> infos=UserInfo.getUsersInfo();
		for(UserInfo info: infos){
		//	System.out.println(info);
		}
		
		List<String> temp=new ArrayList<String>();
		for(UserInfo info: infos){
			temp.add(info.accountType);
		}
		StringUtil.showPercentage(temp);
	}

	public static void main(String[] args)
	{
		countUsersInfo();
	}

}
