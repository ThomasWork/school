package myutil.fileprocess;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 规则：如果文件已经存在，则判断文件是否更新，如果更新，则进行拷贝
 * 如果把文件夹拷贝过去，那么在目标文件夹存在而在源文件夹不存在的文件不会被删除
 * @author Admin
 *提醒功能：
 *有哪些文件没有被复制，输出未复制的文件的后缀名
 */

public class BackupFile
{
	public String configurePath;
	
	public static Map<String, List<String>> postfixes=new TreeMap<String, List<String>>();
	static{
		postfixes.put("txt", Arrays.asList(".txt"));
		postfixes.put("java", Arrays.asList(".java", ".jar", ".txt"));
		postfixes.put("c", Arrays.asList(".cpp", ".c", ".hpp"));
		postfixes.put("word", Arrays.asList(".doc", ".docx"));
		postfixes.put("excel", Arrays.asList("xlsx", ".xls"));
		postfixes.put("paper", Arrays.asList(".caj", ".pdf"));
		postfixes.put("visio", Arrays.asList(".vsd"));
		postfixes.put("img", Arrays.asList(".jpg", ".bmp", ".psd"));
		postfixes.put("executable", Arrays.asList(".exe", ".cmd"));
	}
	
	public static void compareTime(){
	//	System.out.println(FileUtil.getFileDateTime("C:/Users/Admin/Desktop/新建文本文档22.txt"));
	//	FileUtil.copyFolderNewFiles("C:/Users/Admin/Desktop/软科学", "C:/Users/Admin/Desktop/软科学2");
		FileUtil.copyFile("C:/Users/Admin/Desktop/新建文本文档22.txt", "C:/Users/Admin/Desktop/新建文本文档22/asdfasdf.txt");
	}
	
	public static void main(String[] args){
		compareTime();
	}
	
	//备份的基本单位，文件夹
	public class BackupEntity{
		String path;
		public List<String> postfixes;
		
		public BackupEntity(String pathPar){
			this.path = pathPar;
		}
		
		public BackupEntity(String pathPar, List<String> postfixesPar){
			this.path = pathPar;
			this.postfixes = postfixesPar;
		}
	}
}
