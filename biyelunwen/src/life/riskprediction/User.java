package life.riskprediction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import life.riskprediction.timecorrection.TestBrowseHistory;
import life.riskprediction.timecorrection.TimeCorrection;

public class User
{
	public UserInfo userInfo;
	public List<BankDetail> bankDetails=new ArrayList<BankDetail>();
	public List<BillDetail> billDetails=new ArrayList<BillDetail>();
	public List<BrowseHistory> browseHistorys=new ArrayList<BrowseHistory>();
	public LoanTime loanTime=null;
	public OverDue overDue=null;
	
//	public Map<String, Integer> beforeBrowseCount=null;
//	public Map<String, Integer> afterBrowseCount=null;
	public int[] beforeBrowseArray=null;
	public int[] afterBrowseArray=null;
	
	public RecordCount bankDetailCount;
	public RecordCount billDetailCount;
	public RecordCount browseCount;
	
	public User(UserInfo infoPar){
		this.userInfo=infoPar;
	}
	
	public void setBankDetails(List<BankDetail> details){
		if(null==details)
			details=new ArrayList<BankDetail>();
		this.bankDetails=details;
		Collections.sort(this.bankDetails);
		this.setBankDetailsParameter();
	}
	
	public void setBankDetailsParameter(){
		this.bankDetailCount=new RecordCount();
		this.bankDetailCount.total=this.bankDetails.size();
		for(BankDetail bd: this.bankDetails){
			if(bd.time==0)
				this.bankDetailCount.specific++;
		}
		this.bankDetails.clear();
	}
	
	public void setBillDetails(List<BillDetail> details){
		if(null==details)
			details=new ArrayList<BillDetail>();
		this.billDetails=details;
		Collections.sort(this.billDetails);
		this.setBillDetailsParameter();
	}
	
	public void setBillDetailsParameter(){
		this.billDetailCount=new RecordCount();
		this.billDetailCount.total=this.billDetails.size();
		for(BillDetail bd: this.billDetails){
			if(bd.time==0)
				this.billDetailCount.specific++;
		}
		this.billDetails.clear();
	}
	
	public void setBrowseHistory(List<BrowseHistory> historys){
		if(null==historys)
			historys=new ArrayList<BrowseHistory>();
		this.browseHistorys=historys;
		Collections.sort(this.browseHistorys);
		this.setBrowseParameters();
	}
	
	public void setBrowseParameters(){
		
		this.setEachTypeBrowseCount();
		this.browseHistorys.clear();
	}
	
	private void setBrowseCount(){
		this.browseCount=new RecordCount();
		this.browseCount.total=this.browseHistorys.size();
		for(BrowseHistory bh: this.browseHistorys){
			if(bh.time==0)
				this.browseCount.specific++;
			}
	}
	
	private void setEachTypeBrowseCount(){
		long loanTime=this.loanTime.time;//借款时间
		List<BrowseHistory> before=new ArrayList<BrowseHistory>();
		List<BrowseHistory> after=new ArrayList<BrowseHistory>();
		for(BrowseHistory bh: this.browseHistorys){
			if(0==bh.time)//情况未知
				continue;
			if(bh.time<=loanTime){//在借款之前浏览
				if(loanTime-bh.time<=TimeCorrection.monthSecBigger)//时间差在一个月之内
					before.add(bh);
			}else{
				if(bh.time-loanTime<=TimeCorrection.monthSecBigger)
					after.add(bh);
			}
		}
	//	System.out.println("uid:"+this.userInfo.id);
	//	this.showBrowseHistory();
	//	System.out.println(before.size()+","+after.size());
		this.beforeBrowseArray=TestBrowseHistory.countEachBrowse(before, BrowseHistory.selectedBrowseTypeMap);
		this.afterBrowseArray=TestBrowseHistory.countEachBrowse(after, BrowseHistory.selectedBrowseTypeMap);
//		this.beforeBrowseCount=TestBrowseHistory.countEachBrowse(before);
//		this.afterBrowseCount=TestBrowseHistory.countEachBrowse(after);
	}
	
	private void showBrowseHistory(){
		for(BrowseHistory bh: this.browseHistorys){
			System.out.println(bh.time+" "+bh.browseType+","+bh.subBrowseNum);
		}
	}
	
	private void showBillDetails(){
		for(BillDetail bd: this.billDetails){
			System.out.println(bd.getLine());
		}
	}
	
	private void showBankDetails(){
		for(BankDetail bd: this.bankDetails){
			System.out.println(bd.getLine());
		}
	}
	
	public void showUser(){
		StringBuilder sb=new StringBuilder("ID："+this.userInfo.id+"\n");
		sb.append("银行记录："+this.bankDetails.size());
		sb.append("信用卡记录："+this.billDetails.size());
		System.out.println(sb.toString());
		this.showBillDetails();
	//	this.showBankDetails();
	}
	
	public String showBrowseCount(){
		StringBuilder sb=new StringBuilder(this.userInfo.id+"");
	//	sb.append("before:\n");
	/*	for(Map.Entry<String, Integer> entry: this.beforeBrowseCount.entrySet()){
			sb.append(entry.getKey()+":"+entry.getValue()+"\t");			
		}*/
		for(int i=0; i<this.beforeBrowseArray.length; ++i){
			sb.append(","+this.beforeBrowseArray[i]);
		}
	//	sb.append("\nafter:\n");
	/*	for(Map.Entry<String, Integer> entry: this.afterBrowseCount.entrySet()){
			sb.append(entry.getKey()+":"+entry.getValue()+"\t");	
		}*/
		for(int i=0; i<this.afterBrowseArray.length; ++i){
			sb.append(","+this.afterBrowseArray[i]);
		}
	//	System.out.println(sb.toString());
		return sb.toString();
	}
	public void showBrowseCount_test(){
	}
	
	public void show3RecordCount(){
		StringBuilder sb=new StringBuilder(""+this.userInfo.id);
		sb.append(","+this.bankDetailCount+","+this.billDetailCount+","+this.browseCount);
		sb.append(","+this.loanTime.time+","+this.overDue.label);
		System.out.println(sb);
	}
	
	private class RecordCount{
		public int total, specific;
		
		public RecordCount(){
			this.total=0;
			this.specific=0;
		}
		
		@Override
		public String toString(){
			return this.total+","+this.specific;
		}
	}
	
	public static List<User> getUsers(){
		List<UserInfo> infos=UserInfo.getUsersInfo();
		//	infos=infos.subList(0,  15);
			List<User> users=new ArrayList<User>();
			for(UserInfo info: infos){
				User u=new User(info);
				users.add(u);
			}
		return users;
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
