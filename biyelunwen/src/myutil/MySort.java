package myutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MySort implements Comparable<MySort>
{
	public Integer key;
	public String label;
	public MySort(int keyPar, String labelPar){
		this.key=keyPar;
		this.label=labelPar;
	}
	
	public static void sortMap(Map<String, Integer> map, int showN){
		List<MySort> sorts=new ArrayList<MySort>();
		for(Entry<String, Integer> entry: map.entrySet()){
			MySort ms=new MySort(entry.getValue(), entry.getKey());
			sorts.add(ms);
		}
		Collections.sort(sorts);
		for(int i=0; i<showN && i<sorts.size(); ++i){
			MySort ms=sorts.get(i);
			System.out.println(i+1+"\t"+ms.label+"\t"+ms.key);
		}
	}

	@Override
	public int compareTo(MySort o)
	{
		return o.key.compareTo(this.key);//降序排序
	}

}
