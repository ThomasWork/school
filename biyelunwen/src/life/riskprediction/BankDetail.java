package life.riskprediction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;

public class BankDetail implements Comparable<BankDetail>//银行流水记录
{
	public int userId;
	public long time;
	public String transactionType;
	public double amount;//交易金额
	public byte isSalary;

	public BankDetail(String line){
		String[] ss=line.split(",");
		this.userId=Integer.parseInt(ss[0]);
		this.time=Long.parseLong(ss[1]);
		this.transactionType=ss[2];
		this.amount=Double.parseDouble(ss[3]);
		this.isSalary=Byte.parseByte(ss[4]);
	}


	@Override
	public int compareTo(BankDetail o)
	{
		Long tTime=this.time;
		Long oTime=o.time;
		return tTime.compareTo(oTime);
	}
	
	public String getLine(){
		StringBuilder sb=new StringBuilder();
		sb.append(this.userId+",");
		sb.append(this.time+",");
		sb.append(this.transactionType+",");
		sb.append(this.amount+",");
		sb.append(this.isSalary+",");
		return sb.toString();
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("用户ID:"+this.userId+",\t");
		sb.append("时间:"+this.time+",\t");
		sb.append("交易类型:"+this.transactionType+",\t");
		sb.append("交易金额:"+this.amount+",\t");
		sb.append("是否工资："+this.isSalary+",\t");
		return sb.toString();
	}
	public static List<BankDetail> getBankDetails(){
		String path=MainConfigure.bankDetailPath;
		List<BankDetail> details=new ArrayList<BankDetail>();
		try{
			int count=1;
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line=null;
			while((line=reader.readLine())!=null){
				details.add(new BankDetail(line));
				if(count%500000==0)
					System.out.println(count);
				count++;
			}
			reader.close();
		}catch(Exception e){
		   e.printStackTrace();
		}
		System.out.println("获得银行流水记录数量："+details.size());
		return details;
	}
	
	public static void countBankDetails(){
		List<BankDetail> details=BankDetail.getBankDetails();
		for(BankDetail detail: details){
		//	System.out.println(detail);
		}
		
		List<String> temp=new ArrayList<String>();
		List<Long> nums=new ArrayList<Long>();
		for(BankDetail detail: details){
		//	temp.add(detail.isSalary);
			Long tl=detail.time;
			if(tl>0)
				nums.add(tl);
		}
		StringUtil.showPercentage(temp);
		NumberUtil.getMinMax(nums);
	}
	
	
	public static void main(String[] args)
	{
	//	countBankDetails();
	}

}
