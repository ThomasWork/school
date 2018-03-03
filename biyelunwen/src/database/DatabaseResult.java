package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseResult
{
	public String cmdString;
	public PreparedStatement ps=null;
	public ResultSet results=null;
	
	public DatabaseResult(String cmd) throws SQLException
	{
		this.cmdString=cmd;
		this.setResults();
	}
	
	public DatabaseResult(String cmd, int index)
	{
		this.cmdString=cmd;
		this.setResults(index);
	}
	
	private void setResults()
	{
		Connection con=DatabaseBasic.getDatabaseConnection();
		if(null ==con)
			return;
		try
		{
			this.ps=con.prepareStatement(this.cmdString);
	        this.results = this.ps.executeQuery();//执行语句，得到结果集  
		}
		catch(Exception e)
		{
			DatabaseBasic.lastErrorString=e.toString();
		}
	}
	
	private void setResults(int index)
	{
		Connection con=DatabaseBasic.getDatabaseConnection(index);
		if(null ==con)
			return;
		try
		{
			this.ps=con.prepareStatement(this.cmdString);
	        this.results = this.ps.executeQuery();//执行语句，得到结果集  
		}
		catch(Exception e)
		{
			DatabaseBasic.lastErrorString=e.toString();
		}
	}
	
	public void closeAll() throws SQLException
	{
		if(null!=this.results)
			this.results.close();
		if(null!=this.ps)
			this.ps.close();
	}
}
