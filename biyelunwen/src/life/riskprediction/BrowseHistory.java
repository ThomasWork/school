package life.riskprediction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import database.BatchProcess;
import database.DatabaseResult;
import database.DatabaseTest;
import life.riskprediction.timecorrection.TestBrowseHistory;
import life.riskprediction.timecorrection.TimeCorrection;
import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.StringUtil;
import myutil.fileprocess.FileProcessLine;
import myutil.fileprocess.FileUtil;

public class BrowseHistory implements Comparable<BrowseHistory>
{
	public int userId;
	public long time;
	public String browseType;
	public String subBrowseNum;
	
	public static final Map<String, Integer> selectedBrowseTypeMap=TestBrowseHistory.getAllBrowseTypeFromFile();
	
	public BrowseHistory(String line){
		String[] ss=line.split(",");
		this.userId=Integer.parseInt(ss[0]);
		this.time=Long.parseLong(ss[1]);
		this.browseType=ss[2];
		this.subBrowseNum=ss[3];
	}

	@Override
	public int compareTo(BrowseHistory o)
	{
		Long tTime=this.time;
		Long oTime=o.time;
		return tTime.compareTo(oTime);
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("用户ID:"+this.userId+",\t");
		sb.append("时间:"+this.time+",\t");
		sb.append("浏览数据:"+this.browseType+",\t");
		sb.append("浏览子行为编号:"+this.subBrowseNum);
		return sb.toString();
	}
	public static List<BrowseHistory> getBrowseHistorys(){
		String path=MainConfigure.browseHistoryPath;
		List<BrowseHistory> historys=new ArrayList<BrowseHistory>();
		try{
			int count=1;
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line=null;
			while((line=reader.readLine())!=null){
				historys.add(new BrowseHistory(line));
				if(count%500000==0)
					System.out.println(count);
				count++;
			}
			reader.close();
		}catch(Exception e){
		   e.printStackTrace();
		}
		System.out.println("获得用户浏览行为数量："+historys.size());
		return historys;
	}
	
	public static void countBrowseHistorys(){
		List<BrowseHistory> historys=BrowseHistory.getBrowseHistorys();
		for(BrowseHistory history: historys){
		//	System.out.println(history);
		}
		
		List<String> temp=new ArrayList<String>();
		List<Long> nums=new ArrayList<Long>();
		for(BrowseHistory history: historys){
			temp.add(history.browseType+","+history.subBrowseNum);
			Long tl=history.time;
			if(tl>0)
				nums.add(tl);
		}
		StringUtil.showPercentage(temp);
		NumberUtil.getMinMax(nums);
	}
	
	public static class DatabaseProcess extends BatchProcess{

		private static String tableName="browse_history";
		
		public DatabaseProcess(String sqlPar)
		{
			super(sqlPar);
		}
		
		public static List<BrowseHistory> getBrowseHistory(int userId)
		{
			List<BrowseHistory> bhs=new ArrayList<BrowseHistory>();
			String sql = "select * from "+tableName+" where user_id="+userId;//SQL语句  
		//	System.out.println(sql);
	        try {
				DatabaseResult result=new DatabaseResult(sql);
				ResultSet rs=result.results;
	            while (rs.next()) {
	                String uid = rs.getString("user_id");//下标从1开始
	                String info=rs.getString("info");
	                bhs.add(new BrowseHistory(uid+","+info));
	          //      System.out.println(info);
	            }//显示数据 
	            result.closeAll();
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }
	    //    for(BrowseHistory bh: bhs){
	     //   	System.out.println(bh);
	     //   }
	        return bhs;
		}
		
		@Override
		public void processEachLoop() throws SQLException{
			String path=MainConfigure.browseHistoryPath;
			try{
				BufferedReader reader = new BufferedReader(new FileReader(path));
				String line=null;
				while((line=reader.readLine())!=null){
					this.curCount++;
					int index=line.indexOf(",");
					int userId=Integer.parseInt(line.substring(0, index));
					String info=line.substring(index+1);
				//	System.out.println(line+"\n"+userId+"\t"+info);
					this.ps.setInt(1, userId);
					this.ps.setString(2, info);
					this.ps.addBatch();
					this.showProgress();
				//	if(this.curCount>11)
				//		break;
				}
				reader.close();
			}catch(Exception e){
			   e.printStackTrace();
			}
			System.out.println("总共处理了:"+this.curCount);
		}		
	}
	
	public static void main(String[] args)
	{
	//	countBrowseHistorys();
	//	getTime();
		BrowseHistory.DatabaseProcess bhd=new BrowseHistory.DatabaseProcess("insert into "+BrowseHistory.DatabaseProcess.tableName+"(user_id, info) values(?, ?)");
		bhd.batchProcess();
	}

}
