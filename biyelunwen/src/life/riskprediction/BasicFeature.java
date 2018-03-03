package life.riskprediction;

import java.util.ArrayList;
import java.util.List;

import myutil.fileprocess.FileUtil;

public class BasicFeature
{
	public int id;
	public String sex;
	public String occupation;
	public String education;
	public String marriage;
	public String accountType;
	public Long loanTime;
	public byte overDue;
	
	public BasicFeature(String line){
		String[] ss=line.split(",");
		this.id=Integer.parseInt(ss[0]);
		this.sex=ss[1];
		this.occupation=ss[2];
		this.education=ss[3];
		this.marriage=ss[4];
		this.accountType=ss[5];
		this.loanTime=Long.parseLong(ss[6]);
		this.overDue=Byte.parseByte(ss[7]);
	}
	
	public BasicFeature(User u){
		UserInfo info=u.userInfo;
		this.id=info.id;
		this.sex=info.sex;
		this.occupation=info.occupation;
		this.education=info.education;
		this.marriage=info.marriage;
		this.accountType=info.accountType;
		this.loanTime=u.loanTime.time;
		this.overDue=u.overDue.label;
	}
	
	public String getLine(){
		StringBuilder sb=new StringBuilder(this.id+"");
		sb.append(","+this.sex);
		sb.append(","+this.occupation);
		sb.append(","+this.education);
		sb.append(","+this.marriage);
		sb.append(","+this.accountType);
		sb.append(","+this.loanTime);
		sb.append(","+this.overDue);
		return sb.toString();		
	}
	
	@Override
	public String toString(){
		String temp=this.getLine();
		return temp;
	}
	
	public static List<BasicFeature> getBasicFeature(){
		String path=MainConfigure.basicFeaturePath;
		List<String> lines=FileUtil.getLinesFromFile(path);
		List<BasicFeature> basics=new ArrayList<BasicFeature>();
		for(String line: lines)
			basics.add(new BasicFeature(line));
		System.out.println("获得基本特征数量："+basics.size());
		return basics;
	}
	
	
}
