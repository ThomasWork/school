package myutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import myutil.fileprocess.FileUtil;


public class StringUtil
{
	public static Map<String, Integer> countFrequency(String filePath)
	{
		List<String> lines=FileUtil.getLinesFromFile(filePath);
		return countFrequency(lines);
	}
	
	public static Map<String, Integer> countSplitFrequency(List<String> lines, String split)
	{
		Map<String, Integer> frequency=new HashMap<String, Integer>();
		for(String line1 : lines)
		{
			String[] ss=line1.split(split);
			Integer count=frequency.get(ss[0]);
			if(null==count)//如果之前还没有数据
				frequency.put(ss[0], 1);
			else
				frequency.put(ss[0], count+1);
		}
		for(Map.Entry<String, Integer> entry: frequency.entrySet())
		{
			System.out.println(entry.getKey()+","+entry.getValue());
		}
		return frequency;
	}
	
	public static Map<String, Integer> countFrequency(List<String> lines)
	{
		Map<String, Integer> frequency=new TreeMap<String, Integer>();
		for(String line : lines)
		{
			Integer count=frequency.get(line);
			if(null==count)//如果之前还没有数据
				frequency.put(line, 1);
			else
				frequency.put(line, count+1);
		}
	/*	for(Map.Entry<String, Integer> entry: frequency.entrySet())
		{
			System.out.println(entry.getKey()+","+entry.getValue());
		//	System.out.println(entry.getValue());
		}*/
		return frequency;
	}
	
	public static Map<String, Integer> getIndexMap(List<String> lines){
		Map<String, Integer> map=new TreeMap<String, Integer>();
		for(int i=0; i<lines.size(); ++i){
			map.put(lines.get(i), i);
		}
		for(String line: lines){
		//	System.out.println("key:"+line+"\t"+map.get(line));
		}
		return map;
	}
	
	public static void showPercentage(List<String> lines){
		int total=lines.size();
		Map<String, Integer> count=StringUtil.countFrequency(lines);
		for(Entry<String, Integer> entry: count.entrySet()){
			System.out.println(entry.getKey()+" "+entry.getValue()+" "+NumberUtil.df.format(entry.getValue()*100.0/total)+"%");
		}
	}
	
	//获得有序的统计map，但是比较时是根据数值大小比较的
	public static Map<String, Integer> countFrequencyWithNumber(List<String> lines){

		Map<String, Integer> frequency = new TreeMap<String, Integer>(
				new Comparator<String>(){
            public int compare(String o1, String o2) {
            	Double t1=Double.parseDouble(o1);
            	Double t2=Double.parseDouble(o2);
            	return t1.compareTo(t2);
            }     
			});
		for(String line : lines)
		{
			Integer count=frequency.get(line);
			if(null==count)//如果之前还没有数据
				frequency.put(line, 1);
			else
				frequency.put(line, count+1);
		}
	/*	for(Map.Entry<String, Integer> entry: frequency.entrySet())
		{
			System.out.println(entry.getKey()+","+entry.getValue());
		}*/
		return frequency;
	}
	
	public static List<String> filterString(String path)
	{
		List<String> lines=FileUtil.getLinesFromFile(path);
		System.out.println("原来有:"+lines.size());
		List<String> used=new ArrayList<String>();
		for(String line: lines)
		{
			if(line.startsWith("G:/DownImg/bin/img"))
				used.add(line);
		}
		System.out.println("过滤后:"+used.size());
		Set<String> uni=new HashSet<String>(used);
		System.out.println("去重后:"+uni.size());
		return new ArrayList<String>(uni);
	}

	public static List<String> getUnique(List<String> source)
	{
		Set<String> out=new HashSet<String>(source);
		return new ArrayList<String>(out);
	}
	
	public static List<String> repalceContent(List<String> input, String old, String ne){
		List<String> output=new ArrayList<String>();
		for(String ins: input){
			String temp=ins.replaceAll(old, ne);
			output.add(temp);
		}
		return output;
	}
	
	private static Map<String, String> getKeyStringMap(List<String> lines, String seperator){
		Map<String, String> out= new HashMap<String, String>();
		for(String line: lines){
			String key=null, value=null;
		//	System.out.println(line);
			if(-1 == line.indexOf(seperator)){//没有分隔符，即一开始只有一个id
				key=line;
				value="";
			} else{
				key=line.substring(0, line.indexOf(seperator));
				value=line.substring(line.indexOf(seperator)+1);
			}
		//	System.out.println(key+"----"+value);
			out.put(key, value);
		}
		return out;
	}
	
	//合并两个列表，该列表的第一项为键值，deleteSingle指示是否删除没有对应的元素
	public static List<String> mergeLine(List<String> big, List<String> small, String seperator){
		System.out.println("big size:"+big.size());
		System.out.println("small size:"+small.size());
		Map<String, String> bigMap=StringUtil.getKeyStringMap(big, seperator);
		Map<String, String> smallMap=StringUtil.getKeyStringMap(small, seperator);
		List<String> out=new ArrayList<String>();
		for(Map.Entry<String, String> entry: bigMap.entrySet()){
			String key=entry.getKey();
			String value=entry.getValue();
			String smallValue=smallMap.get(key);
		//	System.out.println(smallValue);
			if(null != smallValue)
				out.add(key+seperator+value+seperator+smallValue);
		}
		System.out.println("out size:"+out.size());
		
		return out;
	}
	public static void testMergeLine(){
		List<String> big=Arrays.asList("12, 12", "15, 25", "17, 23");
		List<String> small=Arrays.asList("26, df", "12,qw", "17, we");
		List<String> out=mergeLine(big, small, ",");
		for(String line: out)
			System.out.println(line);
	}
	
	//这里把字符串中的英文字母转换为小写格式
	public static String stringToLowerCase(String str){  
	    StringBuffer sb = new StringBuffer();  
	    if(str!=null){  
	        for(int i=0;i<str.length();i++){  
	            char c = str.charAt(i);  
	       //     System.out.println(c);
	            if(c>='A' && c<='Z'){
	            	sb.append(Character.toLowerCase(c));
	            }
	            else
	            	sb.append(c);
	        }
	    }
	    return sb.toString();  
	}
	
	public static String listToString(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(separator);
		}
		return sb.toString();
	}

	
	public static void main(String[] args){
	//	testMergeLine();
		 String test = "123,345,";
         String[] ss = test.split(",");
         System.out.println(ss.length);
		
	}

}
