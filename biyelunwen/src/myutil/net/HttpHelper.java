package myutil.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import myutil.fileprocess.FileUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;


public class HttpHelper
{	
	public static int sleepTime=1000;//如果获取失败睡眠时间
	public static void downloadFromUrl(String urlString,String saveFile) throws IOException
	{
		URL url=new URL(urlString);
		HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection(); 

		//设置User-Agent 
	//	httpConnection.setRequestProperty("User-Agent","NetFox"); 
	//	int endByteIndex=100*1024-1;
		//设置断点续传的开始位置 
	//	httpConnection.setRequestProperty("RANGE","bytes=0-"+endByteIndex);
		//获得输入流 
	//	System.out.println(httpConnection.getHeaderFields().toString());
		InputStream input = httpConnection.getInputStream(); 
        FileOutputStream fs = new FileOutputStream(saveFile);  
        
        int byteread = 0;

        byte[] buffer = new byte[1024];
        while ((byteread = input.read(buffer)) != -1) 
        {  
            fs.write(buffer, 0, byteread);
        }
        fs.close();
    }
	
	public static void downloadFromUrl(String urlString,String saveFile, int count){		
		try{
			downloadFromUrl(urlString, saveFile);
		}catch(Exception e){//如果出现问题
			HttpHelper.processException(urlString, e);
			System.out.println("哈哈哈");
			if(count>0)
			{
				try
				{
					Thread.sleep(HttpHelper.sleepTime);
				} catch (InterruptedException e2)
				{
					e2.printStackTrace();
				}
				System.out.println("再次尝试获取数据:"+count);
				downloadFromUrl(urlString, saveFile, count-1);
			}
		}
	}
	
	/*
	public static String getContentWithJsoup(String url, int count)
	{
		String result=null;
		try{
			Document doc=Jsoup.connect(url)
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0") //设置User-Agent
					.timeout(15000)  //设置连接超时时间
					.parser(null)//设置使用XML解析
					.get();
		
			result=doc.toString();
			System.out.println(result);
		} 
		catch (IOException e) {
			HttpHelper.processException(url, e);
		}
		try{
			Thread.sleep(500);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		if(null==result && count>0)
		{
			System.out.println("再次尝试获取数据:"+count);
			result=getContentWithJsoup(url, count-1);
		}
		return result;
	}*/
	public static void processException(String url, Exception e)
	{
		System.out.println("获取失败:"+url);
		//e.printStackTrace();
		System.out.println(e.getLocalizedMessage());
	}
	
	//在这个网址可以下载很多jar文件。https://hc.apache.org/downloads.cgi
	public static String getContentWithClient(String url) throws Exception{
		 CloseableHttpClient httpclient = HttpClients.createDefault();
         HttpGet httpget = new HttpGet(url);
         HttpResponse response = httpclient.execute(httpget);
         HttpEntity entity = response.getEntity();
         String out = EntityUtils.toString(entity, "UTF-8");
         httpclient.close();
         return out;
	}
	
	public static String getContentWithClient(String url, int count){
		String content=null;
		
		try{
			content=getContentWithClient(url);
		}catch(Exception e){
			HttpHelper.processException(url, e);
		}
		if(null==content && count>0)
		{
			try
			{
				Thread.sleep(HttpHelper.sleepTime);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			System.out.println("再次尝试获取数据:"+count);
			content=getContentWithClient(url, count-1);
		}
		return content;
	}
	
	public static String testAndGetContent(String savePath, String url){
		File file=new File(savePath);
		if(file.isFile())
			return FileUtil.readAllFromFile(savePath).trim();
		String result = HttpHelper.getContentWithClient(url, 10);
		if(null != result)
			FileUtil.NewFile(savePath, result);
		return result;
	}
}
