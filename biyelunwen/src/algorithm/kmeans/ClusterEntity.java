package algorithm.kmeans;

import java.util.ArrayList;
import java.util.List;

public class ClusterEntity implements Cloneable
{
	public int id;
	public int featureNumber;
	public List<Feature>features;
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		List<Feature> fs=new ArrayList<Feature>();
		for(int i=0;i<this.featureNumber;i++)
			fs.add((Feature) this.features.get(i).clone());
		ClusterEntity ce=new ClusterEntity(this.id, fs);
		return ce;
	}
	
	public ClusterEntity(int id, List<Feature>features)
	{
		this.id=id;
		this.features=features;
		this.featureNumber=features.size();
	}
	
	public double getDistance(ClusterEntity ce)
	{
		double sum=0;
		for(int i=0;i<this.features.size();i++)
		{
			double temp=this.features.get(i).getDistance(ce.features.get(i));
			sum+=temp*temp;
		}
		return sum;
	}
	
	public double getFeatureValue(int index)
	{
		return this.features.get(index).getValue();
	}
	
	public void show()
	{
		String temp="";
		for(int i=0;i<this.featureNumber;i++)
			temp+=","+this.features.get(i).getValue();
		System.out.println("id:"+this.id+temp);
	}
	
	
	
}
