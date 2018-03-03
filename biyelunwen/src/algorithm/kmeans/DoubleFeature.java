package algorithm.kmeans;

public class DoubleFeature extends Feature
{
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		DoubleFeature df=new DoubleFeature(this.value);
		return df;
	}

	double value;
	
	public DoubleFeature(double value)
	{
		this.value=value;
	}
	
	@Override
	public double getDistance(Feature f)
	{
		return this.value-f.getValue();
	}
	
	@Override
	public double getValue()
	{
		return this.value;
	}

	@Override
	public void setValue(Feature f)
	{
		this.value=f.getValue();		
	}
	
}
