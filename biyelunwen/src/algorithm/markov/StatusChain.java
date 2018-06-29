package algorithm.markov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import algorithm.LCSS;
import entity.Photo;
import myutil.NumberUtil;
import myutil.SortString;
import myutil.SortValue;
import myutil.StringUtil;
import myutil.fileprocess.FileUtil;

public class StatusChain
{
	public String id;//状态转换链 有一个id
	public List<String> statusChain;//状态链，为了提高可用性，使用字符串表示状态之间的转换
	public List<Integer> indexChain;//状态下标之间的转换，下标从0开始
	public String initString;
	
	public static Map<String, Integer> statusMap;
	public static Map<Integer, String> indexToStatusMap;
	public static int statusNumber;//状态的数量
	
	public StatusChain(String line){
		this.initString = line;
		this.statusChain = new ArrayList<String>();
		String[] ss=line.split(",");
		this.id=ss[0];
		for(int i = 1; i<ss.length; ++i)
			statusChain.add(ss[i]);
	}
	
	public StatusChain(String idPar, List<String> status){
		this.id=idPar;
		this.statusChain=status;
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(this.id+":"+"\n");
		for(String s: this.statusChain){
			sb.append(s+"\t");
		}
		sb.append("\n");
		for(Integer in: this.indexChain){
			sb.append(in+"\t");
		}
		return sb.toString();
	}

	//从文件中读取多个状态链，用于下一步的处理
	private static List<StatusChain> getChainFromFile(String path){
		List<String> lines=FileUtil.getLinesFromFile(path);
		List<StatusChain> chains=new ArrayList<StatusChain>();
		for(String line: lines){
			StatusChain sc = new StatusChain(line);
			chains.add(sc);
		}
		return chains;
	}
	
	public static void setStatusMapAndStatusIndex(List<StatusChain> chains){
		Set<String> uni = new TreeSet<String>();//保证有序
		for(StatusChain sc: chains){
			uni.addAll(sc.statusChain);
		}
		List<String> arr=new ArrayList<String>(uni);
		Map<String, Integer> indexMap=new TreeMap<String, Integer>();
		StatusChain.indexToStatusMap = new TreeMap<Integer, String>();
		for(int i = 0; i<arr.size(); ++i){
			indexMap.put(arr.get(i), i);
			StatusChain.indexToStatusMap.put(i,  arr.get(i));
		}
		
		StatusChain.statusNumber = arr.size();//设置静态变量
		StatusChain.statusMap = indexMap;//设置静态变量
		
		for(StatusChain sc: chains){
			sc.setStatusIndex(indexMap);
		}
	}
	
	//首先从文件中加载状态链，然后将其转换为下标链
	public static List<StatusChain> getReadyChainFromFile(String path){
		List<StatusChain> chains = getChainFromFile(path);
		setStatusMapAndStatusIndex(chains);
		return chains;
	}
	
	//设置状态对应的下标
	public void setStatusIndex(Map<String, Integer> statusMap){
		this.indexChain=new ArrayList<Integer>();
		for(String status: this.statusChain){
			int index=statusMap.get(status);
			this.indexChain.add(index);
		}
	}
	
	public static int[] getVisitFrequency(List<StatusChain> chains){
		int[] fres=new int[StatusChain.statusNumber];
		for(int i=0; i<StatusChain.statusNumber; ++i){
			fres[i]=0;
		}
		for(StatusChain sc: chains){
			for(Integer index: sc.indexChain){
				fres[index]++;
			}
		}
		return fres;
	}
	
	public static void setStatusVisitFrequency(List<StatusChain> chains, StatusNode[] nodes){
		for(StatusChain sc: chains){
			for(Integer index: sc.indexChain){
				nodes[index].frequency++;
			}
		}
	}
	
	public static void test1(){
		String dataFile="./src/algorithm/markov/data.txt";
		List<StatusChain> chains = getReadyChainFromFile(dataFile);
		for(StatusChain sc: chains){
			System.out.println(sc);
		}
	}
	
	private static List<Integer> getSelectedOrder(Set<Integer> selected, List<Integer> source){
		List<Integer> qa=new ArrayList<Integer>();
		for(int i=0; i<source.size(); ++i){
			int temp=source.get(i);
			if(selected.contains(temp))
				qa.add(temp);
		}
		return qa;
	}
	
	/*
	//这里必须要保证没有重复的
	public static double getIndexArrayDistance(List<Integer> first, List<Integer> second){
		
		//第一步，获取都选中的元素
		Set<Integer> s1=new TreeSet<Integer>(first);
		Set<Integer> s2=new TreeSet<Integer>(second);
		
		Set<Integer> selected=new TreeSet<Integer>();//保存所有都被选中的元素
		Iterator<Integer> it=s1.iterator();
		while(it.hasNext()){
			Integer cur=it.next();
			if(s2.contains(cur))
				selected.add(cur);
		}	
		
		List<Integer> qa=getSelectedOrder(selected, first);
		List<Integer> qb=getSelectedOrder(selected, second);
	//	System.out.println(qa);
	//	System.out.println(qb);
		double temp=getKeyIndexDis(qa, qb);
	//	System.out.println("相同集合距离："+temp);
		double ftos=getDistance(first.size(), selected.size(), temp);
		double stof=getDistance(second.size(), selected.size(), temp);
	//	System.out.println(ftos+","+stof);
		return (ftos+stof)/2;
	}
	
	private static double getDistance(int size, int subsetSize, double subsetDis){
		double part1=1-subsetSize*1.0/size;//第一部分的差别
	//	System.out.println("part1:"+part1);
		double part2=(1-part1)*subsetDis;
	//	System.out.println("part2:"+part2);
		return part1+part2;
	}
	
	public static void testGetIndexArrayDistance(){
		List<Integer> a=Arrays.asList(0, 1, 6, 2, 4, 5);
		List<Integer> b=Arrays.asList(99, 5, 1, 2, 42, 4, 0);
		System.out.println(getIndexArrayDistance(a, b));
	}*/
	
	/*
	//得到两个全排列的对应元素的下标的差值之和
	private static double getKeyIndexDis(List<Integer> index1, List<Integer> index2){
		if(index1.size()<=1)//如果相同元素没有或者为1个，距离为0
			return 0;
		Map<Integer, Integer> inMap=new TreeMap<Integer, Integer>();
		for(int i=0; i<index1.size(); ++i)
			inMap.put(index1.get(i), i);
		for(Map.Entry<Integer, Integer> entry: inMap.entrySet()){
	//		System.out.println(entry.getKey()+","+entry.getValue());
		}
		int sum=0;
		for(int i=0; i<index2.size(); ++i){
			sum+=Math.abs(inMap.get(index2.get(i))-i);
		}
		
		int n=index1.size();
		double max;
		if(n%2==1)//是奇数
			max=(n*n-1)/2;
		else
			max=n*n/2;
	//	System.out.println(max+":"+n);
		return sum/max;
	}
	
	
	public static void testGetKeyIndexDis(){
		List<Integer> a=Arrays.asList(0, 1, 2, 3, 4, 5);
		List<Integer> b=Arrays.asList(5, 1, 2, 3, 4, 0);
		System.out.println(getKeyIndexDis(b, a));
	}*/
	
	
	/********************************                  毕业论文                 ************************************/
	
	//获取游览路径之间的距离，然后即可调用python代码进行层次聚类
	public static void getDistance() {
		List<StatusChain> chains = getReadyChainFromFile(Photo.userTravelPlaces);
		List<String> lines = new ArrayList<String>();
		List<Double> lengths = new ArrayList<Double>();
		double sum = 0;
		double sum2 = 0;
		for (StatusChain sc: chains) {
			if (sc.statusChain.size() <= 0) {
				continue;
			}
			//System.out.println(sc);
			lengths.add((double) sc.statusChain.size());
			sum += sc.statusChain.size();
 			if (sc.statusChain.size() > 1) {
 				lines.add(sc.initString);
 				sum2 += sc.statusChain.size();
 			}
		}
		System.out.println("all:" + chains.size() + ",\taverage length: " + sum / chains.size());
		System.out.println(StatusChain.statusNumber);
		System.out.println("left at least 2: " + lines.size() + ",\taverage length: " + sum2/lines.size());
		FileUtil.NewFile(Photo.userTravelPlacesLeast2, lines);
		lines.clear();
		chains = getReadyChainFromFile(Photo.userTravelPlacesLeast2);
		System.out.println("chan num: " + chains.size());
		for (int i = 0; i < chains.size(); i += 1) {
			String line = "";
			//System.out.println(i);
			StatusChain from = chains.get(i);
			for (int j = 0; j < chains.size(); j += 1) {
				StatusChain to = chains.get(j);
				line += LCSS.getDistance(from.indexChain, to.indexChain) + ",";
			}
			lines.add(line);
		}
		FileUtil.NewFile("G:/ASR/school/draw/层次聚类/data.txt", lines);
	}
	
	public static List<String> select(List<StatusChain> chains, List<String> clusterStr, List<SortString> sss, int n) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < n; i += 1) {
			String cluster = sss.get(i).content;
			for (int j = 0; j < clusterStr.size(); j += 1) {
				if (clusterStr.get(j).equals(cluster)) {
					System.out.println(chains.get(j));
				}
			}
		}
		return result;
	}
	
	public static StatusChain getCenter(List<StatusChain> chains) {
		List<List<Integer>> indexes = new ArrayList<List<Integer>>();
		for (int i = 0; i < chains.size(); i += 1) {
			indexes.add(chains.get(i).indexChain);
		}
		int index = LCSS.getMinDis(indexes);
		return chains.get(index);
	}
	
	@SuppressWarnings("unchecked")
	public static List<SortString> sortCountMap(Map<String, Integer> frequency) {
		List<SortString> sss = new ArrayList<SortString>();
		for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
			SortString ss = new SortString(entry.getKey(), entry.getValue());
			sss.add(ss);
		}
		Collections.sort(sss);
		return sss;
	}
	
	@SuppressWarnings("unchecked")
	public static void processClusterResult() {
		List<StatusChain> chains = getReadyChainFromFile(Photo.userTravelPlacesLeast2);
		//System.out.println(StatusChain.statusNumber);
		
		String path = "G:/ASR/school/draw/层次聚类/clusters.txt";
		List<String> lines = FileUtil.getLinesFromFile(path);
		if (lines.size() != chains.size()) {
			System.out.println("err lines not match: " + chains.size() + "," + lines.size());
		}
		Map<String, Integer> frequency = StringUtil.countFrequency(lines);
		List<SortString> sss = sortCountMap(frequency);
		for (int i = 0; i < sss.size();i += 1) {
			SortString ss = sss.get(i);
			//System.out.println(ss.content + ",\t" + ss.value);
		}
		System.out.println("total cluster count: " + frequency.size());
		
		for (int i = 0; i < 12; i += 1) {//显示排名前10的
			SortString ss = sss.get(i);
			String cluster = ss.content;//类簇编号
			System.out.println("cluster num: " + ss.content + ",\t points num" + ss.value + "----------------------------------------------");
			List<String> paths = new ArrayList<String>();
			List<StatusChain> cchains = new ArrayList<StatusChain>();
			for (int j = 0; j < lines.size(); j += 1) {
				if (lines.get(j).equals(cluster)) {
					cchains.add(chains.get(j));
					paths.add(StringUtil.listToString(chains.get(j).statusChain, "->"));
				}
			}
			Map<String, Integer> pathCount = StringUtil.countFrequency(paths);
			List<SortString> sss2 = sortCountMap(pathCount);
			for (int j = 0; j < sss2.size(); j += 1) {
			//	System.out.println(sss2.get(j).content + "," + sss2.get(j).value);
			}
			System.out.println(getCenter(cchains).statusChain);
		}
	}
	
	public static void countRate() {
		List<StatusChain> chains = getReadyChainFromFile(Photo.userTravelPlaces);
		Map<String, Integer> pre = new TreeMap<String, Integer>();
		Map<String, Integer> next = new TreeMap<String, Integer>();
		
		for (int i = 0; i < chains.size(); i += 1) {
			StatusChain sc = chains.get(i);
			for (int j = 0; j < sc.statusChain.size(); j += 1) {
				int tpre = j;
				int tnext = sc.statusChain.size() - 1 - tpre;
				String cur = sc.statusChain.get(j);
				Integer temp = pre.get(cur);
				if (null == temp) {
					pre.put(cur, tpre);
				} else {
					pre.put(cur, temp + tpre);
				}
				temp = next.get(cur);
				if (null == temp) {
					next.put(cur, tnext);
				} else {
					next.put(cur, temp + tnext);
				}
			}
		}
		for (Map.Entry<String, Integer> entry: pre.entrySet()) {
			int prec = entry.getValue();
			int nextc = next.get(entry.getKey());
			System.out.println(entry.getKey() + "," + prec + "," + nextc + "," + nextc*1.0/(prec + nextc));
		}
	}
	
	public static void main(String[] args)
	{
	//	test1();
	//	testGetKeyIndexDis();
	//	testGetIndexArrayDistance();
	//	getDistance();
		processClusterResult();
	//	countRate();
	}

}
