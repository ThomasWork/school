package life.riskprediction;

import java.util.ArrayList;
import java.util.List;

import myutil.fileprocess.FileUtil;

public class LoanTime
{
	public int userId;
	public long time;

	public LoanTime(String line){
		String[] ss=line.split(",");
		this.userId=Integer.parseInt(ss[0]);
		this.time=Long.parseLong(ss[1]);
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("用户ID:"+this.userId+",\t");
		sb.append("时间:"+this.time+",\t");
		
		return sb.toString();
	}
	
	public static List<LoanTime> getLoanTimes(){
		String path=MainConfigure.loanTimePath;
		List<String> lines=FileUtil.getLinesFromFile(path);
		List<LoanTime> times=new ArrayList<LoanTime>();
		for(String line: lines)
			times.add(new LoanTime(line));
		System.out.println("获得信用卡账单记录数量："+times.size());
		return times;
	}
	
	public static void countLoanTimes(){
		List<LoanTime> times=LoanTime.getLoanTimes();
		for(LoanTime time: times){
			System.out.println(time);
		}
	}
	
	public static void main(String[] args)
	{
		countLoanTimes();
	}

}
