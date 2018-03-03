package database;

import java.sql.Connection;
import java.sql.PreparedStatement;

import myutil.CountTime;
/*

CREATE TABLE t_test (
id  int NOT NULL AUTO_INCREMENT,
age  int NOT NULL,
name  varchar(255) NULL,
PRIMARY KEY (id)
);
insert into t_test(age, name)
values(10, 100);
insert into t_test(age, name)
values(20, 200);

select * from t_test;

select count(*) from t_test;

#清空表数据，不留日志
truncate table t_test;

*/

public class BatchProcessTest extends CountTime
{
	public void test1(){
		try{
			String sql = "insert into t_test (age, name) values (?, ?)";
			Connection connection = DatabaseBasic.getDatabaseConnection();//注意连接字符串要加上后面的参数
			connection.setAutoCommit(false);////////////
			PreparedStatement ps = connection.prepareStatement(sql);
			final int batchSize = 2000;
			int count = 0;
			for (int i=1; i<=287654; ++i){
			    ps.setInt(1, i*10);
			    ps.setString(2, i+"00");
			    ps.addBatch();
			    count++;
			    if(count % batchSize == 0) {
			        ps.executeBatch();
			        connection.commit();
			        ps.clearBatch();
			        System.out.println(i);
			    }
			}
			ps.executeBatch(); // insert remaining records
			connection.commit();
			ps.close();
			connection.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		BatchProcessTest bpt=new BatchProcessTest();
		bpt.getTime();
	}

	@Override
	public void execute()
	{
		this.test1();
	}

}
