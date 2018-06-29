package algorithm;

import java.util.Arrays;
import java.util.List;

public class LCSS
{
	public static int getLCSLength(int[] p, int[] q){
		int[][] c=new int[p.length+1][q.length+1];//注意，这里需要+1
		for(int i=0; i<=q.length; ++i)
			c[0][i]=0;
		for(int j=0; j<=p.length; ++j)
			c[j][0]=0;
		for(int i=0; i<p.length; ++i){
			for(int j=0; j<q.length; ++j){
				if(p[i]==q[j]){
					c[i+1][j+1]=c[i][j]+1;
				}
				else{
					if(c[i][j + 1] >= c[i + 1][j])
						c[i + 1][j + 1] = c[i][j + 1];
					else
						 c[i + 1][j + 1] = c[i + 1][j];
				}
			}
		}
		return c[p.length][q.length];
	}
	
	public static int getLCSLength(List<Integer> p, List<Integer> q){
		int[] tp=getArray(p);
		int[] tq=getArray(q);
		return getLCSLength(tp, tq);
	}
	
	public static double getDistance(List<Integer> p, List<Integer> q){
		int lc=getLCSLength(p, q);
		int lp=p.size();
		int lq=q.size();
		if(lp<lq)
			lp=lq;
		double temp=1-lc*1.0/lp;
	//	System.out.println(p.toString()+"\t\t"+q.toString()+"\t\t"+lc+"\t"+temp);
		return temp;
	}
	
	public static int getMinDis(List<List<Integer>> paths) {
		double min = 999999;
		int index = -1;
		for (int i = 0; i < paths.size(); i += 1) {
			double sum = 0;
			for (int j = 0; j < paths.size(); j += 1) {
				sum += getDistance(paths.get(i), paths.get(j));
			}
			if (sum < min) {
				min = sum;
				index = i;
			}
		}
		return index;
	}
	
	public static int[] getArray(List<Integer> l){
		int[] temp=new int[l.size()];
		for(int i=0; i<l.size(); ++i)
			temp[i]=l.get(i);
		return temp;
	}
	
	public static void getLCSLength(){
		//int[] t1={1, 2, 3, 4};
		//int[] t2={2, 4, 5, 6};
		//int[] t3={4, 3, 2, 1, 5};
		List<Integer> t1=Arrays.asList(1, 2, 3, 4, 10);
		List<Integer> t2=Arrays.asList(2, 4, 5, 6);
		List<Integer> t3=Arrays.asList(4, 3, 2, 1, 5);
		System.out.println(getLCSLength(t1, t1));
		System.out.println(getLCSLength(t1, t2));
		System.out.println(getLCSLength(t1, t3));
		System.out.println(getLCSLength(t2, t3));
		System.out.println(getDistance(t1, t1));
		System.out.println(getDistance(t1, t2));
		System.out.println(getDistance(t2, t1));
	}
	
	

	public static void main(String args[]){
		getLCSLength();
	}
}
