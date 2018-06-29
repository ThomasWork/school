package myutil;

public class SortString implements Comparable
{
	public String content;
	public double value;
	
	public SortString(String contentPar, double valuePar)
	{
		this.content = contentPar;
		this.value=valuePar;
	}

	@Override
	public int compareTo(Object arg0)
	{
		SortString sv=(SortString) arg0;
		Double v0=this.value;
		Double v1=sv.value;
		return v1.compareTo(v0);//降序排序
//		return v0.compareTo(v1);
	}
}
