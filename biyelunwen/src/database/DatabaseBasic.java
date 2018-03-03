package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseBasic
{
	public static String url=null;
	public static String user=null;
	public static String password=null;
	public static String forNameString=null;
	public static String lastErrorString;
	public static Connection connection=null;
	public static Connection[] connections={null, null, null, null, null, null, null};
	static PreparedStatement ps=null;
	
	static{
		setMySQLConnectionParameters();
	}
	
	private static void setMySQLConnectionParameters(){
		DatabaseBasic.url="jdbc:mysql://127.0.0.1/"+"riskprediction"+"?rewriteBatchedStatements=true";//+"&useServerPrepStmts=false";//实际加上最后这个参数好像并没有显著提升
		DatabaseBasic.user="root";
		DatabaseBasic.password="123456";
		DatabaseBasic.forNameString="com.mysql.jdbc.Driver";
	}
	
	private static void setOracleConnectionParameters(){
		DatabaseBasic.url="jdbc:oracle:thin:@127.0.0.1:1521:orcl";
		DatabaseBasic.user="user1";
		DatabaseBasic.password="123456";
		DatabaseBasic.forNameString="oracle.jdbc.driver.OracleDriver";
	}
	
	public static Connection getDatabaseConnection()
	{
		if(null != DatabaseBasic.connection)
			return DatabaseBasic.connection;
		try
		{
			Class.forName(DatabaseBasic.forNameString);
			DatabaseBasic.connection = DriverManager.getConnection(DatabaseBasic.url, DatabaseBasic.user, DatabaseBasic.password);
			return DatabaseBasic.connection;
		}
		catch(Exception e)
		{
			DatabaseBasic.lastErrorString=e.toString();
			return null;
		}
	}
	
	public static Connection getDatabaseConnection(int index)
	{
		Connection tempC=DatabaseBasic.connections[index];
		if(null != tempC)
			return tempC;
		try
		{
			Class.forName(DatabaseBasic.forNameString);
			tempC = DriverManager.getConnection(DatabaseBasic.url, DatabaseBasic.user, DatabaseBasic.password);
			DatabaseBasic.connections[index]=tempC;
			return tempC;
		}
		catch(Exception e)
		{
			DatabaseBasic.lastErrorString=e.toString();
			return null;
		}
	}
}
