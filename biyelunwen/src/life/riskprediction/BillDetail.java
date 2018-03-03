package life.riskprediction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;

public class BillDetail implements Comparable<BillDetail>
{
	public int userId;
	public long time;
	public int bankId;
	public double preBillAmount;//上期账单金额
	public double preRepayment;//上期还款金额
	public double creditLimit;//信用卡额度
	
	//6-10
	public double curBillLeft;//本期账单余额
	public double curBillLeastRepayment;//本期账单最低还款额
	public int consumeNum;//消费笔数
	public double curBillAmount;//本期账单金额
	public double adjustmentAmount;//调整金额
	
	//11-14
	public double circulationInterest;//循环利息
	public double availableBalance;//可用余额
	public double cashAdvanceLimit;//预借现金额度
	public String repaymentState;//还款状态
	

	public BillDetail(String line){
		String[] ss=line.split(",");
		this.userId=Integer.parseInt(ss[0]);
		this.time=Long.parseLong(ss[1]);
		this.bankId=Integer.parseInt(ss[2]);
		this.preBillAmount=Double.parseDouble(ss[3]);
		this.preRepayment=Double.parseDouble(ss[4]);
		this.creditLimit=Double.parseDouble(ss[5]);
		
		this.curBillLeft=Double.parseDouble(ss[6]);
		this.curBillLeastRepayment=Double.parseDouble(ss[7]);
		this.consumeNum=Integer.parseInt(ss[8]);
		this.curBillAmount=Double.parseDouble(ss[9]);
		this.adjustmentAmount=Double.parseDouble(ss[10]);
		
		this.circulationInterest=Double.parseDouble(ss[11]);
		this.availableBalance=Double.parseDouble(ss[12]);
		this.cashAdvanceLimit=Double.parseDouble(ss[13]);
		this.repaymentState=ss[14];
	}

	@Override
	public int compareTo(BillDetail o)
	{
		Integer tBankId=this.bankId;
		Integer oBankId=o.bankId;
		int temp= tBankId.compareTo(oBankId);
		if(temp!=0)
			return temp;
		Long tTime=this.time;
		Long oTime=o.time;
		return tTime.compareTo(oTime);
	}
	
	public String getLine(){
		StringBuilder sb=new StringBuilder();
		sb.append(this.userId+",");
		sb.append(this.time+",");
		sb.append(this.bankId+",");
		sb.append(this.preBillAmount+",");
		sb.append(this.preRepayment+",");
		sb.append(this.creditLimit+",");
		
		sb.append(this.curBillLeft+",");
		sb.append(this.curBillLeastRepayment+",");
		sb.append(this.consumeNum+",");
		sb.append(this.curBillAmount+",");		
		sb.append(this.adjustmentAmount+",");
		
		sb.append(this.circulationInterest+",");
		sb.append(this.availableBalance+",");
		sb.append(this.cashAdvanceLimit+",");
		sb.append(this.repaymentState);		
		
		return sb.toString();
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("用户ID:"+this.userId+",\t");
		sb.append("时间:"+this.time+",\t");
		sb.append("银行ID:"+this.bankId+",\t");
		sb.append("上期账单金额:"+this.preBillAmount+",\t");
		sb.append("上期还款金额:"+this.preRepayment+",\t");
		sb.append("信用卡额度:"+this.creditLimit+",\t");
		
		sb.append("本期账单余额:"+this.curBillLeft+",\t");
		sb.append("本期账单最低还款额:"+this.curBillLeastRepayment+",\t");
		sb.append("消费笔数:"+this.consumeNum+",\t");
		sb.append("本期账单金额:"+this.curBillAmount+",\t");		
		sb.append("调整金额:"+this.adjustmentAmount+",\t");
		
		sb.append("循环利息:"+this.circulationInterest+",\t");
		sb.append("可用余额:"+this.availableBalance+",\t");
		sb.append("预借现金额度:"+this.cashAdvanceLimit+",\t");
		sb.append("还款状态:"+this.repaymentState);		
		
		return sb.toString();
	}
	public static List<BillDetail> getBillDetails(){
		String path=MainConfigure.billDetailPath;
		List<String> lines=FileUtil.getLinesFromFile(path);
		List<BillDetail> details=new ArrayList<BillDetail>();
		for(String line: lines)
			details.add(new BillDetail(line));
		System.out.println("获得信用卡账单记录数量："+details.size());
		return details;
	}
	
	public static void countBillDetails(){
		List<BillDetail> details=BillDetail.getBillDetails();
		for(BillDetail detail: details){
		//	System.out.println(detail);
		}
		
		List<String> temp=new ArrayList<String>();
		List<Long> nums=new ArrayList<Long>();
		for(BillDetail detail: details){
			temp.add(detail.bankId+"");
			Long tl=detail.time;
			if(tl>0)
				nums.add(tl);
		}
		StringUtil.showPercentage(temp);
		NumberUtil.getMinMax(nums);
	}
	
	public static void main(String[] args)
	{
	//	countBillDetails();
	}
}
