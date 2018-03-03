package algorithm.markov;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entity.Photo;
import myutil.NumberUtil;
import myutil.SortValue;

public class StatusNode implements Comparable<StatusNode>
{
	public int index;
	public String name;
	
	public int frequency;//状态出现的频率
	public int pointedFrequency;//状态被指向的频率
	public int pointOutFrequency;//状态向外指的频率
	public int[] topNProbIndex;
	
	public double[] pointOutRate;
	public double[] pointedRate;
	
	public StatusNode(int indexPar, String namePar){
		this.index=indexPar;
		this.name=namePar;
		this.frequency=0;
		this.pointOutFrequency=0;
		this.pointedFrequency=0;
	}
	
	public void setPointOutRate(double[][] tranProb){
		double[] pm=tranProb[this.index];
	//	System.out.println(pm[0]);
		this.pointOutRate=new double[pm.length];
		for(int i=0; i<pm.length; ++i)
			this.pointOutRate[i]=pm[i];
		
		this.topNProbIndex=SortValue.sortReturnIndex(this.pointOutRate);
	}
	
	public void setPointedRate(double[][] tranProb){
		this.pointedRate=new double[tranProb.length];//按列的方式处理
		for(int i=0; i<tranProb.length; ++i)
			this.pointedRate[i]=tranProb[i][this.index];
		this.topNProbIndex=SortValue.sortReturnIndex(this.pointedRate);
	}
	
	public void showTopNProb(int[] sortedIndex, double[] rate, int n){
		StringBuilder[] sbs =new StringBuilder[n];
		double sum=0;
		for(int i=0; i<n; ++i){
			int next=sortedIndex[i];
			double prob=rate[next];
			StringBuilder sb=new StringBuilder();
			if(0==i)
				sb.append(this.name+"（"+this.frequency+"）\t");
			else
				sb.append("\t");
			sum+=prob;
			sb.append(StatusChain.indexToStatusMap.get(next)+"\t"+NumberUtil.df.format(prob));
			sbs[i]=sb;
		}
		sbs[0].append("\t"+NumberUtil.df.format(sum/(n-1)));
		for(int i=0; i<n; ++i)
			System.out.println(sbs[i].toString());
	}

	@Override
	public int compareTo(StatusNode other)
	{
		Integer t1=this.frequency;
		Integer t2=other.frequency;
		return t2.compareTo(t1);//降序排序
	//	return t1.compareTo(t2);
	}
	
	@Override
	public String toString(){
		String temp="";
		temp=this.index+":"+this.name+"\t"+this.frequency+"\t"+this.pointOutFrequency+"\t"+this.pointedFrequency+"\tfre:";
		if(null !=this.pointOutRate){
			for(int i=0; i<this.pointOutRate.length; ++i)
				temp+=this.topNProbIndex[i]+",";
		}
		temp+="\n";
		if(null !=this.pointedRate){
			for(int i=0; i<this.pointedRate.length; ++i)
				temp+=this.topNProbIndex[i]+",";
		}
		return temp;
	}
	
	public static StatusNode[] getNodes(Map<Integer, String> map){
		StatusNode[] nodes=new StatusNode[map.size()];
		for(Map.Entry<Integer, String> entry: map.entrySet()){
			nodes[entry.getKey()]=new StatusNode(entry.getKey(), entry.getValue());
		}
		return nodes;
	}
	
	public static void showNodes(StatusNode[] statusNodes, int n){
		for(StatusNode node: statusNodes){
		//	System.out.println(node);
			node.showTopNProb(node.topNProbIndex, node.pointedRate, n);
		}
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
