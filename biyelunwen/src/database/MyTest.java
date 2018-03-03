package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyTest
{
	public String content="";
	public MyTest(ResultSet rs, int n){
		this.content="";
		for(int i=1; i<=n; ++i){
			try
			{
				this.content+=rs.getString(i)+"\t";
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void myTest() throws SQLException{
		List<String> ss=new ArrayList<String>();
		String cmd="select * from bill_detail";
		DatabaseResult result=new DatabaseResult(cmd);
		int total=0;
		ResultSet rs=result.results;
		while(rs.next())
		{
			ss.add(new MyTest(rs, 3).content);
			total++;
			if(total%100==0)
				System.out.println(total);
		}
		result.closeAll();
		for(String s: ss){
			System.out.println(s);
		}
	}

	public static void main(String[] args) throws SQLException
	{
		myTest();
	}
}
