package algorithm.markov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import myutil.fileprocess.FileUtil;


/***
 * 马尔可夫链聚类，状态的下标必须从0开始
 * @author Administrator
 *
 */
public class Markov
{
	public int statusNumber;
	
	private List<StatusChain> chains;
	
	private StatusNode[] statusNodes;
	
	private int[][] statusTranFrequ;//状态转移频率
	public double[][] statusTranProb;//状态转移概率矩阵
	
	//注意，这里状态数量
	public Markov(int statusNumPar, List<StatusChain> chainsPar)
	{
		this.statusNumber=statusNumPar;
		this.chains=chainsPar;
		
		this.statusNodes=StatusNode.getNodes(StatusChain.indexToStatusMap);
		StatusChain.setStatusVisitFrequency(this.chains, this.statusNodes);
		
		this.statusTranFrequ=new int[this.statusNumber][this.statusNumber];
		this.statusTranProb=new double[this.statusNumber][this.statusNumber];
		
		for(int i=0;i<this.statusNumber;i++)
		{
			this.statusNodes[i].pointOutFrequency=0;//this.statusNumber;//应该等于这一行的和
			this.statusNodes[i].pointedFrequency=0;//this.statusNumber;//应该等于这一行的和
			
			for(int j=0;j<this.statusNumber;j++)
				this.statusTranFrequ[i][j]=0;//1;//为了防止0的情况发生，设置为1
		}
		
	//	this.setMarkov();
		this.setMarkovReverse();
	//	this.setMarkovOneAfterOther();
		Arrays.sort(this.statusNodes);//进行排序，这一步必须放在最后进行
	}
	
	private void setTranMatrix(){
		for(StatusChain sc: this.chains){
			int size=sc.indexChain.size();
			if(0==size)
				continue;
			int pre=sc.indexChain.get(0);
			for(int i=1; i<size; ++i){
				int next=sc.indexChain.get(i);
				this.statusNodes[pre].pointOutFrequency++;
				this.statusNodes[next].pointedFrequency++;
				this.statusTranFrequ[pre][next]++;//更新转移频率
				pre=next;
			}
		}
	}
	
	private void setMarkov()
	{	
		this.setTranMatrix();
		this.setStatusTransitionProb();
		for(StatusNode node: this.statusNodes){
			node.setPointOutRate(this.statusTranProb);
		}
	}
	
	private void setMarkovReverse(){
		this.setTranMatrix();
		this.setStatusTransitionProbReverse();
		for(StatusNode node: this.statusNodes){
			node.setPointedRate(this.statusTranProb);
		}
	}
	
	private void setMarkovOneAfterOther(){
		this.setTranMatrixWithOneAfterOther();
		this.setStatusTransitionProbOneAfterOther();
		for(StatusNode node: this.statusNodes){
			node.setPointOutRate(this.statusTranProb);
		}
	}
	
	//如果一个景点在另一个景点后面，在计数中+1
	private void setTranMatrixWithOneAfterOther(){
		System.out.println("查看景点的绝对游览先后关系");
		for(StatusChain sc: this.chains){
			int size=sc.indexChain.size();
			if(0==size)
				continue;
			for(int i=0; i<sc.indexChain.size()-1; ++i){//这里减1，保证不会访问最后一个
				int pre=sc.indexChain.get(i);//首先访问的
				for(int j=i+1; j<sc.indexChain.size(); ++j){
					int after=sc.indexChain.get(j);
					this.statusNodes[pre].pointOutFrequency++;
					this.statusNodes[after].pointedFrequency++;
					this.statusTranFrequ[pre][after]++;//更新转移频率
				}
			}
		}
		this.setStatusTransitionProb();
	}
	
	/***
	 * 根据频率设置初始状态和转移概率矩阵
	 */
	private void setStatusTransitionProb()
	{
		int number=this.statusNumber;
		for(int i=0;i<number;i++)
		{
			for(int j=0;j<number;j++)//更新状态转移概率矩阵
			{
				if(0==this.statusNodes[i].pointOutFrequency)
					this.statusTranProb[i][j]=1.0/number;
				else
					this.statusTranProb[i][j]=this.statusTranFrequ[i][j]*1.0/this.statusNodes[i].pointOutFrequency;
				
			//	this.statusTranProb[i][j]*=10;
			}
		}
	}
	
	private void setStatusTransitionProbReverse()
	{
		int number=this.statusNumber;
		for(int i=0;i<number;i++)
		{
		//	int sum=0;
			for(int j=0;j<number;j++)//更新状态转移概率矩阵
			{
				int temp=this.statusNodes[j].pointedFrequency;
				if(0==temp)
					this.statusTranProb[i][j]=1.0/number;
				else
					this.statusTranProb[i][j]=this.statusTranFrequ[i][j]*1.0/temp;
				
			//	this.statusTranProb[i][j]*=10;
			//	sum+=this.statusTranFrequ[i][j];
			}
		}		
	}
	
	private void setStatusTransitionProbOneAfterOther(){
		int number=this.statusNumber;
		for(int i=0;i<number;i++)
		{
		//	int sum=0;
			for(int j=i+1;j<number;j++)//这里可以设置+1，自己到自己为0
			{
				int temp=this.statusTranFrequ[i][j]+this.statusTranFrequ[j][i];
				if(0==temp){
					this.statusTranProb[i][j]=this.statusTranProb[j][i]=0;
				}
				else{
					this.statusTranProb[i][j]=this.statusTranFrequ[i][j]*1.0/temp;
					this.statusTranProb[j][i]=this.statusTranFrequ[j][i]*1.0/temp;
				}
				
			//	this.statusTranProb[i][j]*=10;
			//	sum+=this.statusTranFrequ[i][j];
			}
		}	
	}
	
	private void setStatusNodes(){
		for(StatusNode node: this.statusNodes){
			node.setPointOutRate(this.statusTranProb);
		}
	}
	
	public static List<Double> getNormalList(List<Double> source)
	{
		List<Double> result=new ArrayList<Double>();
		if(source.size()<=0)
			return result;
		double max=source.get(0), min=source.get(0);
		for(int i=0;i<source.size();i++)
		{
			double temp=source.get(i);
			if(temp>max)
				max=temp;
			if(temp<min)
				min=temp;
		}
		double dis=max-min;
		for(int i=0;i<source.size();i++)
		{
			double temp=source.get(i);
			result.add((temp-min)/dis);
		}
		return result;
	}
	
	private String getMatrixString(double[][] temp){
		int num=temp.length;
		int number=temp[0].length;
		String out="";
		for(int i=0;i<number;i++)
		{
			for(int j=0;j<number;j++)
			{
				out+=this.statusTranProb[i][j]+"\t";
			}
			out+="\n";
		}
		return out;
	}
	
	@Override
	public String toString()
	{
		String out="状态数量："+this.statusNumber+"\n";
		int number=this.statusNumber;
		
		out+="\n状态转移频率矩阵：\n";
		for(int i=0;i<number;i++)
		{
			for(int j=0;j<number;j++)
			{
				out+=this.statusTranFrequ[i][j]+"\t";
			}
			out+="\n";
		}
		out+="\n状态转移概率矩阵：\n";
		out+=this.getMatrixString(this.statusTranProb);
		
		StatusNode.showNodes(this.statusNodes, this.statusNodes.length);
		
		return out;
	}
	
	public static void main(String[] args){
		String dataFile="./src/algorithm/markov/data.txt";
		List<StatusChain> chains=StatusChain.getReadyChainFromFile(dataFile);
		for(StatusChain sc: chains){
			System.out.println(sc);
		}
		Markov mk=new Markov(StatusChain.statusNumber, chains);
		System.out.println(mk);
	}

}
