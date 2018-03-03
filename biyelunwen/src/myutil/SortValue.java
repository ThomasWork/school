package myutil;

import java.util.Arrays;
import java.util.Comparator;

public class SortValue implements Comparable
{
	public int index;
	public double value;
	
	public SortValue(int indexPar, double valuePar)
	{
		this.index=indexPar;
		this.value=valuePar;
	}

	@Override
	public int compareTo(Object arg0)
	{
		SortValue sv=(SortValue) arg0;
		Double v0=this.value;
		Double v1=sv.value;
		return v1.compareTo(v0);//降序排序
//		return v0.compareTo(v1);
	}
	
	
	public static int[] sortReturnIndex(double[] values)
	{
		SortValue[] svs=new SortValue[values.length];
		int[] index=new int[values.length];
		for(int i=0; i<values.length; ++i)
		{
			svs[i]=new SortValue(i, values[i]);
		}
		Arrays.sort(svs);
		for(int i=0; i<values.length; ++i)
		{
			index[i]=svs[i].index;
	//		values[i]=svs[i].value;
		}
		return index;
	}
	
	public static void test1()
	{
		double[] values={1, 45, 23, 14};
		int[] indexes=sortReturnIndex(values);
		for(int i=0; i<indexes.length; ++i)
			System.out.println(values[i]+":"+indexes[i]);
	}
	
	public static void main(String[] args)
	{
		test1();
	}
}
