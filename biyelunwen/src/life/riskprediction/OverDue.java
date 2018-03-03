package life.riskprediction;

import java.util.ArrayList;
import java.util.List;

import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;

public class OverDue
{
	public Integer userId;
	public byte label;
	
	public OverDue(String line){
		String[] ss=line.split(",");
		this.userId=Integer.parseInt(ss[0]);
		this.label=Byte.parseByte(ss[1]);
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("用户ID:"+this.userId+",\t");
		sb.append("标签:"+this.label);
		return sb.toString();
	}
	
	public static List<OverDue> getOverDues(){
		String path=MainConfigure.overDuePath;
		List<String> lines=FileUtil.getLinesFromFile(path);
		List<OverDue> dues=new ArrayList<OverDue>();
		for(String line: lines)
			dues.add(new OverDue(line));
		System.out.println("获得信用卡账单记录数量："+dues.size());
		return dues;
	}
	
	public static void countOverDues(){
		List<OverDue> dues=OverDue.getOverDues();
		for(OverDue due: dues){
		//	System.out.println(due);
		}
		
		List<String> temp=new ArrayList<String>();
		for(OverDue due: dues){
			temp.add(due.label+"");
		}
		StringUtil.showPercentage(temp);
	}
	
	public static void main(String[] args)
	{
		countOverDues();
	}
}
