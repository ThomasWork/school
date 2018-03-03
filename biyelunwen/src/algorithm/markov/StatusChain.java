package algorithm.markov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import myutil.fileprocess.FileUtil;

public class StatusChain
{
	public String id;//状态转换链 有一个id
	public List<String> statusChain;//状态链，为了提高可用性，使用字符串表示状态之间的转换
	public List<Integer> indexChain;//状态下标之间的转换，下标从0开始
	
	public static Map<String, Integer> statusMap;
	public static Map<Integer, String> indexToStatusMap;
	public static int statusNumber;//状态的数量
	
	public StatusChain(String line){
		this.statusChain = new ArrayList<String>();
		String[] ss=line.split(",");
		this.id=ss[0];
		for(int i=1; i<ss.length; ++i)
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
			StatusChain sc=new StatusChain(line);
			chains.add(sc);
		}
		return chains;
	}
	
	public static void setStatusMapAndStatusIndex(List<StatusChain> chains){
		Set<String> uni=new TreeSet<String>();//保证有序
		for(StatusChain sc: chains){
			uni.addAll(sc.statusChain);
		}
		List<String> arr=new ArrayList<String>(uni);
		Map<String, Integer> indexMap=new TreeMap<String, Integer>();
		StatusChain.indexToStatusMap=new TreeMap<Integer, String>();
		for(int i=0; i<arr.size(); ++i){
			indexMap.put(arr.get(i), i);
			StatusChain.indexToStatusMap.put(i,  arr.get(i));
		}
		
		StatusChain.statusNumber=arr.size();//设置静态变量
		StatusChain.statusMap=indexMap;//设置静态变量
		
		for(StatusChain sc: chains){
			sc.setStatusIndex(indexMap);
		}
	}
	
	//首先从文件中加载状态链，然后将其转换为下标链
	public static List<StatusChain> getReadyChainFromFile(String path){
		List<StatusChain> chains=getChainFromFile(path);
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
		List<StatusChain> chains=getReadyChainFromFile(dataFile);
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
	
	public static void main(String[] args)
	{
		test1();
	//	testGetKeyIndexDis();
	//	testGetIndexArrayDistance();
	}

}
