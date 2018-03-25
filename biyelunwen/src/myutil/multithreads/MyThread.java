package myutil.multithreads;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import down.DownXML;
import myutil.DateUtil;
import myutil.NumberUtil;
import myutil.fileprocess.FileUtil;

public abstract class MyThread implements Runnable
{
	public static int showLoop = 100;//达到这个数目之后显示一下进度
	public static int stageNumber = 2000;//一阶段处理N个
	public static int threadNum = 3;
	public static String configPath="config/threads.txt";
	public static boolean showStageProgress=true;
	public static boolean showThreadProcess=true;
	private int id;
	private List<String> URIS;

	public MyThread(int idPar, List<String> urispar)
	{
		this.id=idPar;
		this.URIS=urispar;
	}

	public abstract void mainFunc(String uri);
	
	@Override
	public void run()
	{
		int total=this.URIS.size();
		System.out.println("任务"+this.id+"需要下载:"+total);
		for(int i=0; i<total; i++)
		{
			if(MyThread.showThreadProcess)
			{
				if(i % MyThread.showLoop == 0)
					System.out.println(DateUtil.getNowString() + ": 任务"+this.id+"完成:"+NumberUtil.df4.format(i*100.0/total)+"%");
			}
			this.mainFunc(this.URIS.get(i));
		}
	}
	

	public static void processMultiThread(List<String> urls, int threadNum, final ProcessUrl pu)
	{
		pu.testVariable="";
		List<List<String>> urlsList=new ArrayList<List<String>>();
		for(int i=0; i<threadNum; i++)
			urlsList.add(new ArrayList<String>());
		for(int i=0; i<urls.size(); i++)
		{
			int index = i % threadNum;
			urlsList.get(index).add(urls.get(i));
		}
		List<Thread> threads=new ArrayList<Thread>();
		for(int i = 0; i < threadNum; i++)
		{
		//	threads.add(new Thread(new DownXML(i, urlsList.get(i))));
			threads.add(new Thread(new MyThread(i, urlsList.get(i)){
				@Override
				public void mainFunc(String uri)
				{
					pu.ProcessUrl(uri);
				}
				
			}));
			threads.get(i).start();
		}
		for(int i=0; i<threadNum; i++)
		{
			try
			{
				threads.get(i).join();
			} 
			catch (InterruptedException e)
			{
				System.out.println("Join出错");
			}
		}
	}
	
	public static void createConfigFile()
	{
		List<String> content=new ArrayList<String>();
		content.add("0");
		content.add("1000");
		content.add("100");
		content.add("第一行为当前完成了多少");
		content.add("第二行为一个阶段处理多少");
		content.add("第三行为每个线程处理多少时显示进度");
		FileUtil.NewFile(MyThread.configPath, content);
	}
	
	public static void processMultiStage(List<String> urls, ProcessUrl pu)
	{		
		int finish = 0;//当前总共完成了多少
		System.out.println(DateUtil.getNowString() + ": 当前已经完成:" + finish + "；一组:"+MyThread.stageNumber+"；显示:"+showLoop);
		int totalNum = urls.size();
		while(finish < urls.size())
		{
			int next = finish + MyThread.stageNumber;
			if(MyThread.showStageProgress)
			{
				System.out.println(DateUtil.getNowString() + ": 总体进度:"+finish+"("+NumberUtil.df4.format(finish*100.0/totalNum)+"%)");
			}
			List<String> currentURLs=new ArrayList<String>();
			for(int i = finish; i < next && i < totalNum; i++)
			{
				currentURLs.add(urls.get(i));
			}
			processMultiThread(currentURLs, MyThread.threadNum, pu);
			finish=next;
		}
		System.out.println("所有任务结束");
	}
}
