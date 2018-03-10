package myutil.fileprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import entity.Photo;

public  class  FileUtil
{  
	
   //新建目录
   public static boolean NewFolder(String folderPath)//检测文件夹名合法？
   {
	   try
	   {
		   File newFolder=new File(folderPath);
		   if(!newFolder.exists())
		   {
			   newFolder.mkdir();
		   }
		   return true;
	   }
	   catch(Exception e)
	   {
		   System.out.println("新建---"+folderPath+"---文件夹出错");
		   e.printStackTrace();
		   return false;
	   }
	   finally
	   {
		   System.out.println("执行了finally");
	   }
   }
   
   public static void deleteFilesInFile(String path) {
	   List<String> lines = FileUtil.getLinesFromFile(path);
		for (int i = 0; i < lines.size(); i += 1) {
			FileUtil.deleteFile(lines.get(i));
		}
   }
   

   //新建文件，会覆盖以前的文件
   public static boolean NewFile(String filePathAndName, String fileContent)
   {
	   try
	   { 
		   FileOutputStream fos = new FileOutputStream(filePathAndName);
		   OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); 
		   osw.write(fileContent); 
		   osw.flush();
		   osw.close();
		   return true;
		} 
	   catch (Exception e)
		{
		   System.out.println("新建---"+filePathAndName+"---文件出错");
		   e.printStackTrace();
		   return false;
		}
   }
   
   public static Date getFileDateTime(String filePath){
	   File f = new File(filePath);
	   Calendar cal = Calendar.getInstance();
	   long time = f.lastModified();
	   cal.setTimeInMillis(time);
	//   System.out.println("修改时间： " + cal.getTime()); 
	   return cal.getTime();
   }
   
   public static List<String[]> ReadCSV(String csvFile, boolean hasHeader)
   {
	   List<String[]> csvList = new ArrayList<String[]>(); //用来保存数据
	   try 
	   {
            CsvReader reader = new CsvReader(csvFile, ',' ,Charset.forName("UTF-8"));    //一般用这编码读就可以了
            if(hasHeader) // 跳过表头
            	reader.readHeaders();
            while(reader.readRecord())
            	csvList.add(reader.getValues());
            reader.close();
	   }
	   catch(Exception e)
	   {
           System.out.println(e);
       }
	   return csvList;
   }
   
   public static void writeCsv(String csvFilePath, List<String[]> contents)
   {
       try
       {
            CsvWriter wr =new CsvWriter(csvFilePath, ',', Charset.forName("UTF-8"));
            for(int i=0;i<contents.size();i++)
            	wr.writeRecord(contents.get(i));
            wr.close();
        } 
       catch (IOException e)
       {
           e.printStackTrace();
        }
   }
   
   public static boolean checkExist(String filePath){
	   File file=new File(filePath);
	   return file.exists();
   }
   
   public static int getFolderFilesLinesCount(String folder)
   {
	   List<String> lines=new ArrayList<String>();
	   List<String> paths=FileUtil.getFolderFilesPath(folder);
	   for(String path: paths)
	   {
		   List<String> temp=FileUtil.getLinesFromFile(path);
		   lines.addAll(temp);
	   }
	   return lines.size();
   }
   
   public static List<String> getFolderFilesLines(String folder){
	   List<String> lines=new ArrayList<String>();
	   List<String> paths=FileUtil.getFolderFilesPath(folder);
	   for(String path: paths)
	   {
		   List<String> temp=FileUtil.getLinesFromFile(path);
		   lines.addAll(temp);
	   }
	   return lines;
   }
   
 //查看带有BOM的文件
   public static void CheckWithBom(String file) throws IOException
   {
		FileInputStream in = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		UnicodeReader ur=new UnicodeReader(in, "UTF-8");
		String line = br.readLine();
		int ii=10;
		while(line != null && ii>1)
		{
				ii--;
				System.out.println(line);
				byte[] allbytes = line.getBytes(); 
				for (int i=0; i < allbytes.length; i++)
				{
					    int tmp = allbytes[i];
					    String hexString = Integer.toHexString(tmp);
					    // 1个byte变成16进制的，只需要2位就可以表示了，取后面两位，去掉前面的符号填充
					    hexString = hexString.substring(hexString.length() -2);
					    System.out.print(hexString.toUpperCase());
					    System.out.print(" ");
				}
				line = br.readLine();
				System.out.println();
		   }
	}
   
   public static boolean NewFile(String filePathAndName, List<String> content)
   {
	   try
	   {
		   if(content.size()<=0)
			   return true;
		   File newFile=new File(filePathAndName);
	//	   FileUtil.NewFolder(newFile.getParent());
		   if(!newFile.exists())
			   newFile.createNewFile();
			BufferedWriter bw=new BufferedWriter(new FileWriter(newFile));
			bw.write(content.get(0));//写第一行
			for(int i=1;i<content.size();i++)
			{
				bw.write(System.getProperty("line.separator"));
				bw.write(content.get(i));
			}
			bw.close();
			return true;
		} 
	   catch (Exception e)
		{
		   System.out.println("新建---"+filePathAndName+"---文件出错");
		   e.printStackTrace();
		   return false;
		}	   
   }
   
   public static List<String> getLinesFromFile(String file)
   {
	   List<String>lines=new ArrayList<String>();
	   try
	   {
		   BufferedReader reader = new BufferedReader(new FileReader(file));
		   String line=null;
		   while((line=reader.readLine())!=null)
			   lines.add(line);
		   reader.close();
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return lines;
   }
   
   public static String readAllFromFile(String path){
	   
	   File file = new File(path);
       String txt=null;
       try
       {

           FileReader reader = new FileReader(file);
    	   int fileLen = (int)file.length();
    	   char[] chars = new char[fileLen];
    	   reader.read(chars);
    	   txt = String.valueOf(chars);
    	   reader.close();
       } catch (Exception e)
       {
    	   e.printStackTrace();
       }
       return txt;
   }
   
   public static HashSet<String> HashSetFromFile(String file) throws IOException
   {
	   HashSet<String>lines=new HashSet<String>();
	   BufferedReader reader = new BufferedReader(new FileReader(file));
	   String line=null;
	   while((line=reader.readLine())!=null)
		   lines.add(line);
	   reader.close();
	   return lines;
   }
   
   public static String GetFileExtension(String fileName)
   {
	   int index=fileName.lastIndexOf(".");
	   if(-1==index)
		   return null;
	   return fileName.substring(index);
   }
   
   /****************************获得文件夹中的文件*******************************/
   
   public static List<String> getFolderFilesPath(String path)
   {
	   List<String> paths=new ArrayList<String>();
	   File folder=new File(path);
	   if(!folder.isDirectory())
		   return paths;
	   String[] files=folder.list();
	   for(String file: files)
	   {
		   String temp=path+"/"+file;
		   paths.add(temp);
	   }
	   return paths;
   }
   
   public static List<String> getFolderFilesPath(String folderPath, List<String> postfixes){
	   List<String> paths=new ArrayList<String>();
	   File folder=new File(folderPath);
	   if(!folder.isDirectory())//如果是文件
		   return paths;
	   return paths;
   }
   
   public static boolean AppendText(String filePath, String content)
   {
	   try
	   {
		   BufferedWriter bw=new BufferedWriter(new FileWriter(filePath, true));
		   bw.newLine();
		   bw.write(content);
		   bw.close();
		   return true;
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
		   return false;
	   }
   }
   
   public static boolean deleteFile(String filePathAndName)
   {
	   try
	   {
		   File delFile=new File(filePathAndName);
		   delFile.delete();
		   System.out.println("删除文件---"+filePathAndName);
		   return true;
	   }
	   catch(Exception e)
	   {
		   System.out.println("删除---"+filePathAndName+"---文件出错");
		   e.printStackTrace();
		   return false;
	   }
   }
   
   public  void  delFolder(String  folderPath)  {  
       try  {  
           delAllFileInFolder(folderPath);  //删除完里面所有内容  
           String  filePath  =  folderPath;  
           filePath  =  filePath.toString();  
           java.io.File  myFilePath  =  new  java.io.File(filePath);  
           myFilePath.delete();  //删除空文件夹  
 
       }  
       catch  (Exception  e)  {  
           System.out.println("删除文件夹操作出错");  
           e.printStackTrace();  
 
       }  
 
   }
   
   public  void  delAllFileInFolder(String  path)  {  
       File  file  =  new  File(path);  
       if  (!file.exists())  {  
           return;  
       }  
       if  (!file.isDirectory())  {  
           return;  
       }  
       String[]  tempList  =  file.list();  
       File  temp  =  null;  
       for  (int  i  =  0;  i  <  tempList.length;  i++)  {  
           if  (path.endsWith(File.separator))  {  
               temp  =  new  File(path  +  tempList[i]);  
           }  
           else  {  
               temp  =  new  File(path  +  File.separator  +  tempList[i]);  
           }  
           if  (temp.isFile())  {  
               temp.delete();  
           }  
           if  (temp.isDirectory())  {  
               delAllFileInFolder(path+"/"+  tempList[i]);//先删除文件夹里面的文件  
               delFolder(path+"/"+  tempList[i]);//再删除空文件夹  
           }  
       }  
   }  
 
   /***********************************复制和移动文件夹**************************************/
   
   
   public static void copyFile(String oldPath, String newPath)
   {
	   File newFile = new File(newPath);
	//   newFile.getParentFile().mkdirs();
	   try
	   {
		   int byteRead=0;
		   File oldFile=new File(oldPath);
		   if(oldFile.exists())
		   {
			   InputStream inStream =new FileInputStream(oldPath);
			   FileOutputStream fOut=new FileOutputStream(newPath);
			   byte[] buffer=new byte[1024];
			   while((byteRead=inStream.read(buffer))!=-1)
			   {
				   fOut.write(buffer, 0, byteRead);
			   }
			   inStream.close();
			   fOut.close();
		   }
		   System.out.println("复制成功："+oldPath+"--->"+newPath);
	   }
	   catch(Exception e)
	   {
		   System.out.println("复制失败："+oldPath+"--->"+newPath);
		   e.printStackTrace();
	   }
   }
   
   public static void copyFileIfNew(String oldPath, String newPath){
	   File fold=new File(oldPath);
	   File fnew=new File(newPath);
	   if(fold.lastModified()>fnew.lastModified()){//如果文件不存在返回0
		   copyFile(oldPath, newPath);
	   }		   
   }
   
   public static void copyFolder(String  oldPath,  String  newPath){  
       try  {
           (new  File(newPath)).mkdirs();  //如果文件夹不存在  则建立新文件夹  
           File  a=new  File(oldPath);  
           String[]  file=a.list();  
           File  temp=null;  
           for  (int  i  =  0;  i  <  file.length;  i++)  {  
               if(oldPath.endsWith(File.separator)){  
                   temp=new  File(oldPath+file[i]);  
               }  
               else{  
                   temp=new  File(oldPath+File.separator+file[i]);  
               }  
 
               if(temp.isFile()){  
                   FileInputStream  input  =  new  FileInputStream(temp);  
                   FileOutputStream  output  =  new  FileOutputStream(newPath  +  "/"  + 
                           (temp.getName()).toString());  
                   byte[]  b  =  new  byte[1024  *  5];  
                   int  len;  
                   while  (  (len  =  input.read(b))  !=  -1)  {  
                       output.write(b,  0,  len);  
                   }  
                   output.flush();  
                   output.close();  
                   input.close();  
               }  
               if(temp.isDirectory()){//如果是子文件夹  
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);  
               }  
           }  
       }  
       catch  (Exception  e)  {  
           System.out.println("复制整个文件夹内容操作出错");  
           e.printStackTrace();  
 
       }  
 
   }

   //如果文件夹里面的文件比较新则拷贝
   public static void copyFolderNewFiles(String oldPath, String newPath){
       try{
    	   (new File(newPath)).mkdirs();  //如果文件夹不存在  则建立新文件夹  
           File sourceFile=new  File(oldPath);  
           String[] files=sourceFile.list();  
           File  temp=null;  
           for(int i=0; i<files.length; ++i){
        	   if(oldPath.endsWith(File.separator)){
        		   temp=new File(oldPath+files[i]);
        	   }
        	   else{
        		   temp=new File(oldPath+File.separator+files[i]);
        	   }
        	   System.out.println(temp.getPath());
        	   if(temp.isFile()){
        		   String newFilePath=newPath+"/"+(temp.getName()).toString();
        		   copyFileIfNew(temp.getPath(), newFilePath);
        	   }
        	   if(temp.isDirectory())
        		   copyFolderNewFiles(oldPath+"/"+files[i], newPath+"/"+files[i]);
           }
       }  
       catch  (Exception  e){  
           System.out.println("复制整个文件夹内容操作出错");  
           e.printStackTrace();
       }
   }
   
   public  void  moveFolder(String  oldPath,  String  newPath)  {
       copyFolder(oldPath,  newPath);  
       delFolder(oldPath);  
 
   } 
   
   
   
   
   public static void main(String[] args)
   {
	//   FileUtil file = new FileUtil();
	//   file.delAllFile("E:/1");
	//   String content=FileUtil.readAllFromFile("C:/Users/Admin/Desktop/新建文本文档.txt");
	 //  System.out.println(content);
	//   System.out.println(FileUtil.getFolderFilesLinesCount("E:/MyLearn/实习/做过的项目/CapabilityEvaluate"));
	 //  System.out.println(FileUtil.getFolderFilesLinesCount("E:/MyLearn/实习/做过的项目/OJ"));
	   System.out.println(FileUtil.getFolderFilesLinesCount("E:/MyLearn/实习/做过的项目/DataManagement"));
   }
}