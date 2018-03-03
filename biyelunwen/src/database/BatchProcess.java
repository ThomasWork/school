package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import myutil.CountTime;

public class BatchProcess
{
	public static int batchSize=20000;
	
	public String sql;
	public static Connection conn;
	public PreparedStatement ps;
	
	public int curCount=0;
	
	public BatchProcess(String sqlPar){
		try{
		this.sql=sqlPar;
		this.curCount=0;
		this.conn=DatabaseBasic.getDatabaseConnection();
		this.conn.setAutoCommit(false);/////
		this.ps = this.conn.prepareStatement(this.sql);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void processEachLoop() throws SQLException{
		for (int i=1; i<=287654; ++i){
		    this.ps.setInt(1, i);
		    this.ps.setInt(2, i*10);
		    this.ps.setString(3, i+"00");
		    this.curCount++;
		    this.ps.addBatch();
		    this.showProgress();
		}
	}
	
	protected void showProgress() throws SQLException{
		if(this.curCount % BatchProcess.batchSize == 0) {
	        this.ps.executeBatch();
	        this.conn.commit();
	        this.ps.clearBatch();
	        System.out.println("processed:"+this.curCount);
	    }
	}
	
	public void batchProcess(){
		try{
			this.processEachLoop();
			this.ps.executeBatch(); // insert remaining records
			this.conn.commit();
			this.ps.close();
			this.conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		BatchProcess bp=new BatchProcess("insert into t_test (id, age, name) values (?, ?, ?)");
		bp.batchProcess();
	}

}
