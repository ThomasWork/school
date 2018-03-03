package myutil.fileprocess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public abstract class FileProcessLine
{
	public abstract void parseLine(String line);
	public void processList(){
		
	}
	
	public static void processFile(String path, FileProcessLine process){
		int count=1;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line=null;
			while((line=reader.readLine())!=null){
				process.parseLine(line);
				if(count%500000==0)
					System.out.println(count);
				count++;
			}
			reader.close();
		}catch(Exception e){
		   e.printStackTrace();
		}
		System.out.println("总共读取行数："+(count-1));
		process.processList();
	//	process.showResult();
	}
	
	public static void test(){
		List<String> content=new ArrayList<String>();
		content.add("1,2,3,4\n5,6,7,8");
		FileUtil.NewFile("test.txt", content);
		FileProcessLine.processFile("test.txt", new FileProcessLine(){
			public List<String> lines=new ArrayList<String>();
			@Override
			public void parseLine(String line)
			{
			//	System.out.println(line);
				lines.add(line);
				System.out.println(lines.size());
			}
			
			@Override
			public void processList(){
				System.out.println("总共有："+lines.size());
			}
		});
	}

	public static void main(String[] args)
	{
		test();
	}
}
