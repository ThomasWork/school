package algorithm.kmeans;

public abstract class Feature implements Cloneable
{
	public abstract double getDistance(Feature f);
	
	public abstract double getValue();
	
	public abstract void setValue(Feature f);
	
	@Override
	public abstract Object clone() throws CloneNotSupportedException;
}
