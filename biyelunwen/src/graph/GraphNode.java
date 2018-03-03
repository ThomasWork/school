package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphNode
{
	public String id;
	public int nodeIndex;
	private List<GraphEdge> pointOut=new ArrayList<GraphEdge>();//保存向外指的边
	private Set<Integer> pointOutSet=new HashSet<Integer>();
	private List<GraphEdge> pointIn=new ArrayList<GraphEdge>();//指向节点本身的边
	private Set<Integer> pointInSet=new HashSet<Integer>();
	
	public GraphNode(String id, int index)
	{
		this.id=id;
		this.nodeIndex=index;
	}
	
	public void addPointOut(GraphEdge ge){
		if(this.pointOutSet.contains(ge.index)){
		//	System.out.println("id:"+this.id+" out contains index:"+ge.index);
			return;
		}
		this.pointOutSet.add(ge.index);
		this.pointOut.add(ge);
	}
	
	public void addPointIn(GraphEdge ge){
		if(this.pointInSet.contains(ge.index)){
		//	System.out.println("id:"+this.id+" in contains index:"+ge.index);
			return;
		}
		this.pointInSet.add(ge.index);
		this.pointIn.add(ge);
	}
	
	public List<GraphEdge> getPointOut(){
		return this.pointOut;
	}
	
	public List<GraphEdge> getPointIn(){
		return this.pointIn;
	}
	
	public String getString(boolean withWeight){
		String temp="";
		for(GraphEdge edge: this.pointOut)
		{
			temp+=edge.index;
			if(withWeight)
				temp+=":"+edge.weight;
			temp+=", ";
		}
		return temp;
	}
	
	@Override
	public String toString()
	{
		return this.getString(true);
	}
}
