package down;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import entity.Photo;
import entity.User;
import myutil.fileprocess.FileUtil;
import myutil.multithreads.MyThread;
import myutil.multithreads.ProcessUrl;
import myutil.net.HttpHelper;

public class DownPhotoContent
{

	public static String method="flickr.photos.getSizes";
//https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&photo_id=14439128899&api_key=822eb51ecd570ddfc1ebcd061b2c20f3
	
	public static String getPhotoSizeUrl(String photoId){
		String url=DownPhotoInfo.base
				+"?method="+method
				+"&api_key="+DownPhotoInfo.apiKey
				+"&photo_id="+photoId;		
		return url;
	}
	
	public static String getPhotoSizeSavePath(String photoId){
		String path=Photo.photoSizeDir+photoId+".txt";
		return path;
	}
	
	public static String getPhotoContentSavePath(String url){
		String name=url.substring(url.lastIndexOf("/")+1);
		return Photo.photoContentDir + name;
	}
	
	public static String getUrl(String photoId) {
		String url=DownPhotoContent.getPhotoSizeUrl(photoId);
		//System.out.println(url);
		String path=DownPhotoContent.getPhotoSizeSavePath(photoId);
		FileUtil.deleteFile(path);
		String out=HttpHelper.testAndGetContent(path, url);
		//System.out.println(out);


			SAXReader saxReader = new SAXReader(); 
			Document document;
			try
			{
				document = saxReader.read(new File(path));
				Element rootElement = document.getRootElement();
				Element sizes = rootElement.element("sizes");
				List<Element> elems = sizes.elements();
				for (Element e: elems) {
					String label = e.attributeValue("label");
					//System.out.println(label);
					if (label.equals("Medium")) {
						return e.attributeValue("source");
					}
				}
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
			return "";
	}

	public static void downloadFile() {
		List<String> lines = FileUtil.getLinesFromFile("C:/Users/Admin/Desktop/temp.txt");
		for (String line: lines) {
			if (line.length() <= 0) {
				continue;
			}
			String[] ss = line.split(",");
			String url = getUrl(ss[0]);
			System.out.println(url);
			String path = Photo.photoContentDir + ss[1] + "_" + ss[0] + ".jpg";
			//HttpHelper.testAndGetContent(path, url);
			try
			{
				downloadContent(url, path);
			} catch (Exception e)
			{
				System.out.println(path);
				//e.printStackTrace();
			};
		}
	}
	
	//使用一个线程下载数据
	public static void downPhotoSize(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.workDir + "gugongphotoid.txt");
		System.out.println("需要下载"+ids.size());
		for(int i=0; i<ids.size(); ++i){
			String id=ids.get(i);
			System.out.println(i+","+id);
			String url=DownPhotoContent.getPhotoSizeUrl(id);
			String path=DownPhotoContent.getPhotoSizeSavePath(id);
			HttpHelper.testAndGetContent(path, url);
			
		}
	}
	
	public static void downPhotoContent(){
		List<String> lines= FileUtil.getLinesFromFile(Photo.workDir+"photo_sizeM.txt");
		System.out.println("需要下载"+lines.size());
		for(String line: lines){
			String[] ss=line.split(",");
			String path = DownPhotoContent.getPhotoContentSavePath(ss[1]);
			HttpHelper.downloadFromUrl(ss[1], path, 10);
		}
	}
	
	
	public static void downPhotoSizeMultiThreads(){
		List<String> ids= FileUtil.getLinesFromFile(Photo.workDir+"gugongphotoid.txt");
		System.out.println("需要下载"+ids.size());
		MyThread.processMultiStage(ids, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				url=DownPhotoContent.getPhotoSizeUrl(url);
				String path=DownPhotoContent.getPhotoSizeSavePath(url);
				HttpHelper.testAndGetContent(path, url);
			}			
		});
	}
	
	public static void downloadContent(String urlStr, String path) throws Exception {
		URL url = new URL(urlStr);  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();  
        //获取自己数组
        byte[] getData = readInputStream(inputStream);    

        File file = new File(path);    
        FileOutputStream fos = new FileOutputStream(file);     
        fos.write(getData); 
        if(fos!=null) {
            fos.close();  
        }
        if(inputStream!=null){
            inputStream.close();
        }
	}
        
        public static  byte[] readInputStream(InputStream inputStream) throws IOException {  
            byte[] buffer = new byte[1024];  
            int len = 0;  
            ByteArrayOutputStream bos = new ByteArrayOutputStream();  
            while((len = inputStream.read(buffer)) != -1) {  
                bos.write(buffer, 0, len);  
            }  
            bos.close();  
            return bos.toByteArray();  
        }
	
	public static void downPhotoContentMultiThreads(){
		List<String> lines= FileUtil.getLinesFromFile(Photo.workDir+"photo_sizeM.txt");
		System.out.println("需要下载"+lines.size());
		List<String> content=new ArrayList<String>();
		for(String line: lines){
			String[] ss=line.split(",");
			content.add(ss[1]);
		}		
		MyThread.processMultiStage(content, new ProcessUrl(){
			@Override
			public void ProcessUrl(String url)
			{
				String path=DownPhotoContent.getPhotoContentSavePath(url);
				HttpHelper.downloadFromUrl(url, path, 10);
			}
		});
	}
	
	public static void main(String[] args) throws Exception{
	//	downUsersInfo();
	//	downPhotoContent();
	//	downPhotoContentMultiThreads();
		downloadFile();
	}
}
