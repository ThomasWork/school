package sciencecluster;

import java.util.ArrayList;
import java.util.List;

import myutil.fileprocess.FileUtil;

public class ClusterLine
{
	public String name;
	public String description;
	public List<Cluster> clusters;
	
	public ClusterLine(String line)
	{
		String[] ss=line.split("\t");
		this.name=ss[0];
		this.description=ss[1];
		this.clusters=new ArrayList<Cluster>();
		for(int i=2; i<ss.length; ++i)
		{
			this.clusters.add(new Cluster(ss[i]));
		}
	}
	
	public void setClusterArea(double rate)//倍率
	{
		double max=-1;
		for(Cluster c: this.clusters)
		{
			if(c.count>max)
			{
				max=c.count;
			}
		}
		for(Cluster c: this.clusters)
		{
			c.circleArea=c.count*rate/max;
		}
	}
	
	public void showCount()
	{
		String temp="";
		for(Cluster c: this.clusters)
		{
			temp+=c.count+",";
		}
		temp=temp.substring(0, temp.lastIndexOf(","));
		System.out.println(temp);
	}
	
	public void showDensity()
	{
		String temp="";
		for(Cluster c: this.clusters)
		{
			temp+=c.density+",";
		}
		temp=temp.substring(0, temp.lastIndexOf(","));
		System.out.println(temp);
	}
	
	public void showArea()
	{
		this.setClusterArea(64.0);
		String temp="";
		for(Cluster c: this.clusters)
		{
			temp+=c.circleArea+",";
		}
		temp=temp.substring(0, temp.lastIndexOf(","));
		System.out.println(temp);
	}
	
	@Override
	public String toString()
	{
		String temp="";
		temp+=this.name+"("+this.description+")("+this.clusters.size()+")：";
		for(Cluster cluster: this.clusters)
		{
			temp+=cluster.toString()+"-->";
		}
		return temp;
	}

	public static void test1()
	{
		List<String> lines=FileUtil.getLinesFromFile("C:/Users/Admin/Desktop/图片地理位置/分类统计/data.txt");
		for(String line: lines)
		{
			ClusterLine cl=new ClusterLine(line);
		//	System.out.println(cl);
			cl.showArea();
		}
	}
	
	public static void main(String[] args)
	{
		test1();
	}
}

class Cluster
{
	public double density;//密度
	public double count;//数量
	public double circleArea;
	
	public Cluster(int countPar, double densityPar)
	{
		this.density=densityPar;
		this.count=countPar;
	}
	
	public Cluster(String line)
	{
	//	System.out.println(line);
		String[] ss=line.split(",");
		this.count=Integer.parseInt(ss[0]);
		this.density=Double.parseDouble(ss[1]);
	}
	
	@Override
	public String toString()
	{
		String temp="";
		temp+=this.count+":"+this.density;
		return temp;
	}
}