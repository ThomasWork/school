package shortpath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import myutil.fileprocess.FileUtil;

public class Path
{
	public int fromIndex;
	public int toIndex;
	public int firstLength;
	public int secondLength;
	
	static final int PATH_UNREACH=-99999999;
	static final int PATH_SELF=-9000;//自己到自己
	
	public enum PathType{
		selfToSelf,
		unreach,
		reachToUnreach,
		reachToReach;
	}	
	
	public Path(){
		
	}
	
	public Path(String line){
		String [] ss=line.split(",");
		this.fromIndex=Integer.parseInt(ss[0]);
		this.toIndex=Integer.parseInt(ss[1]);
		this.firstLength=Integer.parseInt(ss[2]);
		this.secondLength=Integer.parseInt(ss[3]);
	}
	
	public static List<Path> loadPath(String path){
		List<String> lines=FileUtil.getLinesFromFile(path);
		System.out.println("LINES:"+lines.size());
		List<Path> paths=new ArrayList<Path>();
		for(int i=0; i<lines.size(); ++i){
			if(i%10000==0)
				System.out.println(i);
			String line=lines.get(i);
			String [] ss=line.split(",");
			Path p=new Path();
			p.fromIndex=Integer.parseInt(ss[0]);
			p.toIndex=Integer.parseInt(ss[1]);
			p.firstLength=Integer.parseInt(ss[2]);
			p.secondLength=Integer.parseInt(ss[3]);
			paths.add(p);
		}
		return paths;
	}
	
	private static PathType getPathType(Path p){
		if(p.fromIndex==p.toIndex)
			return PathType.selfToSelf;
		//后面的都不是自己指向自己的
		if(p.firstLength==Path.PATH_UNREACH)
			return PathType.unreach;
		//后面的都是第一次可达的
		if(p.secondLength==Path.PATH_UNREACH)
			return PathType.reachToUnreach;
		//后面都是两次可达的
		return PathType.reachToReach;
	}
	
	public static Map<PathType, List<Path>> getTypeMap(List<Path> paths){
		Map<PathType, List<Path>> map= new HashMap<PathType, List<Path>>();
		for(PathType pt: PathType.values()){
			map.put(pt, new ArrayList<Path>());
		}
		for(Path p: paths){
			PathType pt=Path.getPathType(p);
			map.get(pt).add(p);
		}
		return map;
	}
	
	public static void getAverageReachToReach(List<Path> paths){
		double firstSum=0, secondSum=0;
		for(Path p: paths){
			firstSum+=p.firstLength;
			secondSum+=p.secondLength;
		}
		int size=paths.size();
		double firstAve=firstSum/size;
		double secondAve=secondSum/size;
		System.out.println("firstAverage:"+firstAve);
		System.out.println("secondAverage:"+secondAve);
	}
	
	public static void getAverageReachToReachWithFile(String path, int start){
	   try {
		   BufferedReader reader = new BufferedReader(new FileReader(path));
		   String line=null;
		   int count=0;
		   int number=0;
		   double firstSum=0, secondSum=0;
		   while((line=reader.readLine())!=null)
		   {
			   count++;
			   if(count<start){
				   System.out.println(line);
				   continue;
			   }
			   if(count%1000000==0)
				   System.out.println(count);
			   Path p=new Path(line);
			   if(PathType.reachToReach==Path.getPathType(p)){
				   number++;
				   firstSum+=p.firstLength;
				   secondSum+=p.secondLength;
			   }
		   }
		   reader.close();
		   double firstAve=firstSum/number;
		   double secondAve=secondSum/number;
		   System.out.println("number:"+number);
		   System.out.println("firstAverage:"+firstAve);
		   System.out.println("secondAverage:"+secondAve);
	   } catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	}
	
	//统计第一路径可达的分布
	public static void getFirstReachLenCountWithFile(String path, int start){
		   try {
			   Map<Integer, Integer> firstLenCount=new TreeMap<Integer, Integer>();
			   Map<Integer, Integer> secondLenCount=new TreeMap<Integer, Integer>();
			   Map<Integer, Integer> reachToReachFirstLenCount=new TreeMap<Integer, Integer>();
			   BufferedReader reader = new BufferedReader(new FileReader(path));
			   String line=null;
			   int count=0;
			   double firstSum=0, secondSum=0, reachToReachFirstSum=0;
			   int firstCount=0, secondCount=0, reachToReachFirstCount=0, unreachCount=0;
			   while((line=reader.readLine())!=null)
			   {
				   count++;
				   if(count<start){
					   System.out.println(line);
					   continue;
				   }
				   if(count%1000000==0)
					   System.out.println(count);
				   Path p=new Path(line);
				   PathType pt=Path.getPathType(p);
				   if(PathType.reachToReach==pt || PathType.reachToUnreach==pt){//第一路径可达
					   firstSum+=p.firstLength;
					   firstCount++;
					   Integer c = firstLenCount.get(p.firstLength);
					   if(null==c)
						   firstLenCount.put(p.firstLength, 1);
					   else
						   firstLenCount.put(p.firstLength, c+1);
				   }
				   if(PathType.unreach==pt){//不可达
					   unreachCount++;
				   }
				   if(PathType.reachToReach==pt){//第二也可达
					   secondSum+=p.secondLength;
					   reachToReachFirstSum+=p.firstLength;
					   secondCount++;
					   reachToReachFirstCount++;
					   Integer c = secondLenCount.get(p.secondLength);
					   if(null == c)
						   secondLenCount.put(p.secondLength, 1);
					   else
						   secondLenCount.put(p.secondLength, c+1);
					   c=reachToReachFirstLenCount.get(p.firstLength);
					   if(null == c)
						   reachToReachFirstLenCount.put(p.firstLength, 1);
					   else
						   reachToReachFirstLenCount.put(p.firstLength, c+1);
				   }
			   }
			   for(Map.Entry<Integer, Integer> entry: firstLenCount.entrySet()){
				   System.out.println(entry.getKey()+","+entry.getValue());
			   }
			   System.out.println("first:"+firstCount+":"+firstSum/firstCount);
			   System.out.println("unreach:"+unreachCount);
			   System.out.println("第二路径：");
			   for(Map.Entry<Integer, Integer> entry: secondLenCount.entrySet()){
				   System.out.println(entry.getKey()+","+entry.getValue());				   
			   }
			   System.out.println("second:"+secondCount+":"+secondSum/secondCount);
			   System.out.println("第二路径可达，第一路径：");
			   
			   for(Map.Entry<Integer, Integer> entry: reachToReachFirstLenCount.entrySet()){
				   System.out.println(entry.getKey()+","+entry.getValue());				   
			   }
			   System.out.println("reachToReachFirst:"+reachToReachFirstCount+":"+reachToReachFirstSum/reachToReachFirstCount);
			   reader.close();
		   } catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		}
	
	public static void main(String[] args){
		String path;
	//	path="E:/netlength/facebook/out - 副本.txt";Path.getFirstReachLenCountWithFile(path, 1);
	//	path="E:/netlength/ca-GrQc/out - 副本.txt";Path.getFirstReachLenCountWithFile(path, 1);
	//	path="E:/netlength/CA-HepTh/out - 副本.txt";Path.getFirstReachLenCountWithFile(path, 7);
	//	path="E:/netlength/p2p-Gnutella08/out - 副本.txt";Path.getFirstReachLenCountWithFile(path, 4);
	//	path="E:/netlength/wiki-Vote/out - 副本.txt";Path.getFirstReachLenCountWithFile(path, 12);
	//	path="E:/netlength/p2p-Gnutella09/out.txt"; Path.getFirstReachLenCountWithFile(path, 4);
	//	path="E:/netlength/email-Enron/out.txt"; Path.getFirstReachLenCountWithFile(path, 39);
	//	path="E:/netlength/未知网络/out.txt"; Path.getFirstReachLenCountWithFile(path, 43);
	//	path="E:/netlength/com-DBLP/out - 副本.txt"; Path.getFirstReachLenCountWithFile(path, 1);
	//	path="E:/netlength/com-Amazon/out - 副本.txt"; Path.getFirstReachLenCountWithFile(path, 1);
	//	path="E:/netlength/teachers/arvix/out.txt"; Path.getFirstReachLenCountWithFile(path, 4);
	//	path="E:/netlength/teachers/facebook/out.txt"; Path.getFirstReachLenCountWithFile(path, 19);
	//	path="E:/netlength/teachers/renren/out.txt"; Path.getFirstReachLenCountWithFile(path, 4);
	//	path="E:/netlength/teachers/sina/out.txt"; Path.getFirstReachLenCountWithFile(path, 6);
		path="E:/netlength/teachers/twitter/out.txt"; Path.getFirstReachLenCountWithFile(path, 12);
		
		
	}
	
}
