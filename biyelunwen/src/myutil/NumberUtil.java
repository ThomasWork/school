package myutil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NumberUtil
{
	public static DecimalFormat df=new DecimalFormat("0.00");
	public static DecimalFormat dfint=new DecimalFormat("0");
	public static DecimalFormat df4=new DecimalFormat("0.0000"); 
	public static DecimalFormat df5=new DecimalFormat("0.00000"); 
	public static Random random=new Random();
	
	
	public static <T extends Comparable<? super T>> int[] countFrequency(List<T> numbers, T[] thresholds)
	{
		int [] count=new int [thresholds.length+1];
		for(T num: numbers)
		{
			int i=0;
			for(; i<thresholds.length; ++i)
			{
				if(num.compareTo(thresholds[i])<0)
					break;
			}
			count[i]++;
		}
		boolean show=true;
		if(show){
			for (int i = 0; i < numbers.size(); i += 1) {
				
			}
			System.out.println("total nums: " + numbers.size());
			df = new DecimalFormat("0");
			double aggregate = count[0]*100.0/numbers.size();
			System.out.println("<"+df.format(thresholds[0])+","+count[0]+"," + aggregate + "," + aggregate);
			for(int i = 0; i < thresholds.length - 1; ++i) {
				double rate = count[i+1]*100.0/numbers.size();
				aggregate += rate;
				System.out.println(df.format(thresholds[i])+"~"+df.format(thresholds[i+1])+","+count[i+1]+"," + rate + "," + aggregate);
			}
			double last = count[thresholds.length]*100.0/numbers.size();
			aggregate += last;
			System.out.println(">"+df.format(thresholds[thresholds.length-1])+","+count[thresholds.length] + "," + last + "," + aggregate);
		}
		return count;
	}
	
	public static <T extends Comparable<? super T>> void getMinMax(List<T> numbers)
	{
		T min=numbers.get(0);
		T max=numbers.get(0);
		for(int i=1; i<numbers.size(); ++i){
			T temp=numbers.get(i);
			if(temp.compareTo(min)<0)
				min=temp;
			if(temp.compareTo(max)>0)
				max=temp;
		}
		System.out.println(min + " " + max);
	}
	
	public static double getSum(List<Double> nums) {
		double sum = 0;
		for (Double num: nums) {
			sum += num;
		}
		return sum;
	}
	
	public static void testCountFrequency(){
		List<Integer> ints=new ArrayList<Integer>();
		ints.add(1);
		ints.add(3);
		ints.add(5);
		ints.add(2);
		ints.add(2);
		ints.add(1);
		ints.add(1);
		ints.add(3);
		Integer[] thres={0, 1, 2, 3};
		int[] fre=countFrequency(ints, thres);
	}
	
	public static void countFrequency(List<Double> values){
		Collections.sort(values);//首先进行排序
		int size=values.size();
		double min=values.get(0);
		double max=values.get(size-1);
		System.out.println("最小值："+min+"\t最大值："+max);
		int splitNum=10;
		double dis=(max-min)/splitNum;
		Double[] thres=new Double[splitNum];
		for(int i=0; i<splitNum; ++i){
			thres[i]=min+i*dis;
		}
		int[] fre=countFrequency(values, thres);
	}
	
	public static <T> void sortList(List<T> list, final String sortField, final String sortMode) 
	{  
	    if(list == null || list.size() < 2)
	    {  
	        return;  
	    }
	    Collections.sort(list, new Comparator<T>()
	    		{  
	        @Override  
	        public int compare(T o1, T o2) {  
	            try {  
	                Class clazz = o1.getClass();  
	                Field field = clazz.getDeclaredField(sortField); //获取成员变量  
	                field.setAccessible(true); //设置成可访问状态  
	                String typeName = field.getType().getName().toLowerCase(); //转换成小写  
	  
	                Object v1 = field.get(o1); //获取field的值  
	                Object v2 = field.get(o2); //获取field的值  
	  
	                boolean ASC_order = (sortMode == null || "ASC".equalsIgnoreCase(sortMode));  
	  
	                //判断字段数据类型，并比较大小  
	                if(typeName.endsWith("string")) {  
	                    String value1 = v1.toString();  
	                    String value2 = v2.toString();  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("short")) {  
	                    Short value1 = Short.parseShort(v1.toString());  
	                    Short value2 = Short.parseShort(v2.toString());  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("byte")) {  
	                    Byte value1 = Byte.parseByte(v1.toString());  
	                    Byte value2 = Byte.parseByte(v2.toString());  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("char")) {  
	                    Integer value1 = (int)(v1.toString().charAt(0));  
	                    Integer value2 = (int)(v2.toString().charAt(0));  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("int") || typeName.endsWith("integer")) {  
	                    Integer value1 = Integer.parseInt(v1.toString());  
	                    Integer value2 = Integer.parseInt(v2.toString());  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("long")) {  
	                    Long value1 = Long.parseLong(v1.toString());  
	                    Long value2 = Long.parseLong(v2.toString());  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("float")) {  
	                    Float value1 = Float.parseFloat(v1.toString());  
	                    Float value2 = Float.parseFloat(v2.toString());  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("double")) {  
	                    Double value1 = Double.parseDouble(v1.toString());  
	                    Double value2 = Double.parseDouble(v2.toString());  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("boolean")) {  
	                    Boolean value1 = Boolean.parseBoolean(v1.toString());  
	                    Boolean value2 = Boolean.parseBoolean(v2.toString());  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("date")) {  
	                    Date value1 = (Date)(v1);  
	                    Date value2 = (Date)(v2);  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else if(typeName.endsWith("timestamp")) {  
	                    Timestamp value1 = (Timestamp)(v1);  
	                    Timestamp value2 = (Timestamp)(v2);  
	                    return ASC_order ? value1.compareTo(value2) : value2.compareTo(value1);  
	                }  
	                else {  
	                    //调用对象的compareTo()方法比较大小  
	                    Method method = field.getType().getDeclaredMethod("compareTo", new Class[]{field.getType()});  
	                    method.setAccessible(true); //设置可访问权限  
	                    int result  = (Integer)method.invoke(v1, new Object[]{v2});  
	                    return ASC_order ? result : result*(-1);  
	                }  
	            }  
	            catch (Exception e) {  
	                String err = e.getLocalizedMessage();  
	                System.out.println(err);  
	                e.printStackTrace();  
	            }  
	  
	            return 0; //未知类型，无法比较大小  
	        }  
	    });  
	}  
	
	//得到数组里第n大的数
	@SuppressWarnings("unchecked")
	public static double getSortN(List<Double> source, int n)
	{
		Collections.sort(source, new Comparator()
		{
			@Override
			public int compare(Object arg0, Object arg1)
			{
				Double d1=(Double)arg0;
				Double d2=(Double)arg1;
				return d2.compareTo(d1);
			}			
		});
	//	Collections.sort(source);
		int nmax=20;
		if(nmax>0){
			System.out.println("输出前"+nmax+"大的数：");
			for(int i=0; i<source.size() && i<nmax; ++i)
			{
				System.out.println(source.get(i));
			}
		}
		
		return source.get(n-1);//注意，这里是n-1
	}
	
	public static void testSortN()
	{
		List<Double> ds=new ArrayList<Double>();
		ds.add(10.0);
		ds.add(12.1);
		ds.add(1.1);
		ds.add(-1.3);
		System.out.println(getSortN(ds, 3));		
	}
	
	//得到从0到n-1之间的随机数
	public static int getRandom(int n){
		return NumberUtil.random.nextInt(n);
	}
	
	public static int getRandom(int min, int max){
		int temp=NumberUtil.random.nextInt(max);
		while(temp<min)
			temp=NumberUtil.random.nextInt(max);
		return temp;
	}
	
	public static double min3(double a, double b, double c){
		double temp=a;
		if(b<temp)
			temp=b;//得到a和b中较小的那一个
		if(c<temp)
			temp=c;
		return temp;
	}
	
	public static boolean isZero(double v) {
		if (-0.000000001 < v && v < 0.00000000001) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args)
	{
	//	for(int i=0; i<100; ++i)
	//		System.out.println(getRandom(1));
	//	testCountFrequency();
		System.out.println(NumberUtil.dfint.format(3.9));
		
	}
}
