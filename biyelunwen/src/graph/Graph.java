package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import myutil.NumberUtil;
import myutil.fileprocess.FileUtil;

public class Graph
{
	public List<GraphNode> nodes=new ArrayList<GraphNode>();
	
	public Map<String, Integer> idToIndex=new TreeMap<String, Integer>();
	
	public int edgeNumber;
	
	String seperator;
	public boolean isDirected;
	
	public Set<String> maxComponent=new HashSet<String>();//最大连通分量
	
	public Graph(String filePath, String seperator, String isDirected, boolean withWeight)
	{
		this.seperator=seperator;
		
		if(isDirected.equals("directed"))
			this.isDirected=true;
		else if(isDirected.equals("notdirected"))
			this.isDirected=false;
		else{
			System.out.println("参数错误");
			System.exit(0);	
		}
		
		
		List<String> edges=FileUtil.getLinesFromFile(filePath);
		for(int i=0;i<edges.size();i++)
		{
			if(i%10000==0 && i>0)
				System.out.println(i);
			String edge=edges.get(i);
			String[] ss=edge.split(seperator);
		//	System.out.println(edge+","+ss.length);
			if(ss[0].equals(ss[1]))
				System.out.println("指向自己："+edge);
			int fromIndex = this.testNewNode(ss[0]);
			int toIndex = this.testNewNode(ss[1]);
			double weight=0;
			if(withWeight)
				weight=Double.parseDouble(ss[2]);
			GraphNode from=this.nodes.get(fromIndex);
			GraphNode to=this.nodes.get(toIndex);
			from.addPointOut(new GraphEdge(toIndex, weight));
			to.addPointIn(new GraphEdge(fromIndex, weight));//被指向的节点也添加边
			if(! this.isDirected)//如果是无向图
			{
				from.addPointIn(new GraphEdge(toIndex, weight));
				to.addPointOut(new GraphEdge(fromIndex, weight));
			}
		}
		this.setParameter();
	}
	
	
	private void setParameter()
	{
		this.edgeNumber=0;
		for(int i=0;i<this.nodes.size();i++)
		{
			GraphNode gn=this.nodes.get(i);
			this.edgeNumber+=gn.getPointOut().size();
		}
	//	if(! this.isDirected)//如果是无向图
	//		this.edgeNumber/=2;
		System.out.println("设置参数完毕！");
		System.out.println("节点数目："+this.nodes.size());
		if(this.isDirected)
			System.out.println("有向图");
		else
			System.out.println("无向图");
		System.out.println("边的数目为(无向图算作双向边)："+this.edgeNumber);
	}
	
	public void getConnectedComponents()
	{
		Queue<Integer> queue=new LinkedList<Integer>();
		
		int size=this.nodes.size();
		int [] visited=new int[size];
		for(int i=0;i<size;i++)
			visited[i]=-1;
		
		int type=0;//连通分支的标记
		for(int i=0;i<size;i++)
		{
		//	System.out.println(queue.size());
			if(-1 == visited[i])
			{
				visited[i]=type;//设置属类
				queue.offer(i);
				while(!queue.isEmpty())
				{
					int first=queue.poll();
					GraphNode gn=this.nodes.get(first);
					for(GraphEdge nei: gn.getPointOut())//
					{
						if(-1 == visited[nei.index])//如果没有访问过,加入到队列中
						{
							visited[nei.index]=type;//必须在加入之前就设定，不然可能会多次加入
							queue.offer(nei.index);
						}
					}
					for(GraphEdge pin: gn.getPointIn())
					{
						if(-1 == visited[pin.index])//如果没有访问过,加入到队列中
						{
							visited[pin.index]=type;//必须在加入之前就设定，不然可能会多次加入
							queue.offer(pin.index);
						}
					}
				}
				type++;
			}
		}
		List<List<String>> components=new ArrayList<List<String>>();
		for(int i=0;i<type;i++)
		{
			List<String> component=new ArrayList<String>();
			components.add(component);
		}
		for(int i=0;i<size;i++)
		{
			List<String> component = components.get(visited[i]);//得到那一类
			component.add(this.nodes.get(i).id);//得到下标
		}
		
		List<Integer> componentsSize=new ArrayList<Integer>();		
		for(int i=0; i<components.size(); ++i)
		{
			componentsSize.add(components.get(i).size());
		//	System.out.println(components.get(i));
		}
		Collections.sort(componentsSize);
		System.out.println("连通分量大小："+componentsSize);
		
		int max=-1, maxIndex=-1;
		for(int i=0;i<components.size();i++)
		{
			if(components.get(i).size()>max)
			{
				max=components.get(i).size();
				maxIndex=i;
			}
		}
		List<String> maxComponent=components.get(maxIndex);
		this.maxComponent.clear();
		this.maxComponent.addAll(maxComponent);
		System.out.println("最大连通分量数量："+this.maxComponent.size());
		
	}
	
	public void writeEdgesInMaxComponent()
	{
		List<String> out=new ArrayList<String>();
		int notIn=0;
		for(int i=0;i<this.nodes.size();i++)
		{
			GraphNode gn=this.nodes.get(i);
			if(this.maxComponent.contains(gn.id))//如果一个节点在连通分量里面，那么它的邻接节点也在
			{
				for(GraphEdge edge : gn.getPointOut())
				{
					String temp=gn.id+this.seperator+this.nodes.get(edge.index).id+this.seperator+edge.weight;
			//		System.out.println(temp);
					out.add(temp);
				}
			}
			else
				notIn+=gn.getPointOut().size();
		}
		FileUtil.NewFile("maxComponent.txt", out);
		System.out.println("不在连通分量里面的边数目为："+notIn);
	}
	
	private int testNewNode(String id)
	{
		if(!this.idToIndex.containsKey(id))//如果还没有包含该id
		{
			int currentIndex=this.nodes.size();
			GraphNode gn=new GraphNode(id, currentIndex);
			this.nodes.add(gn);
			this.idToIndex.put(id, currentIndex);
			return currentIndex;
		}
		else//如果已经包含了该节点
		{
			int index=this.idToIndex.get(id);
			return index;
		}
	}
	
	public void generateTrueIdFile()
	{
		List<String>out=new ArrayList<String>();
		List<String> lines=FileUtil.getLinesFromFile("zhiliang.txt");
		for(String line: lines)
		{
			String[] ss=line.split("\t");
			Integer index=this.idToIndex.get(ss[0]);
			if(null != index)
			out.add(index+","+ss[1]);
		}
		FileUtil.NewFile("index_threshold.txt",  out);
	}
	
	/*
	文件格式有严格要求
	（1）第1行为节点数量和边的数量
	（2）第二行开始为节点下标以及边的数量N，以后N行为该节点指向的边的下标
	（3）节点的下标从0开始，依次列出
	（4）即使节点没有指向外面的边，也需要说明边数为0
	*/
	public void generateCleanNetFile(String path, String seperator){
		List<String> lines=new ArrayList<String>();
		int edge=this.edgeNumber;
		String directedStr="directed";
		if(!this.isDirected)
			directedStr="notdirected";
		lines.add(this.nodes.size()+seperator+edge+seperator+directedStr);
		for(int i=0; i<this.nodes.size(); ++i){
			List<GraphEdge> out=this.nodes.get(i).getPointOut();
			lines.add(i+seperator+out.size());
			for(int j=0; j<out.size(); ++j)
				lines.add(out.get(j).index+"");
		}
		FileUtil.NewFile(path, lines);
	}
	
	@Override
	public String toString()
	{
		String temp="";
		temp+="节点数："+this.nodes.size()+"\n";
		temp+="边数："+this.edgeNumber+"\n";
		temp+="节点和下标的映射："+"\n";
		for(Map.Entry<String, Integer> entry : this.idToIndex.entrySet())
		{
			temp+=entry.getKey()+":"+entry.getValue()+"\n";
		}
		temp+="\nedges:\n";
		for(int i=0;i<this.nodes.size();i++)
		{
			GraphNode gn=this.nodes.get(i);
			temp+=gn.id+":"+gn.getString(false)+"\n";
		}
		return temp;
	}
	
	/**
	 * 针对c++不能处理大于10位的问题，提出这个解决方案，囧，先凑合着用
	 */
	public void getIdAndStringFile()
	{
		List<String>out=new ArrayList<String>();
		for(int i=0;i<this.nodes.size();i++)
		{
			int indexFrom=i;
			GraphNode gn=this.nodes.get(i);
			String idFrom=gn.id;
			for(GraphEdge edge: gn.getPointOut())
			{
				int indexTo=edge.index;
				String idTo=this.nodes.get(indexTo).id;
				double weight=edge.weight;
				out.add(indexFrom+" "+indexTo+" "+String.format("%#.6f", weight)+" "+idFrom+" "+idTo);
			//	System.out.println();
			}
		}
		FileUtil.NewFile("indexWeightId.txt",  out);
	}
	
	public void generateNodesPair(int pairNum, String savePath){
		List<String> temp=Graph.getNodePair(this.nodes.size(), pairNum);
		FileUtil.NewFile(savePath, temp);
	}
	
	public static List<String> getNodePair(int nodeNum, int pairNum){
		System.out.println("需要生成："+pairNum);
		Set<String> pairsSet=new HashSet<String>();
		while(true){
			int from=NumberUtil.getRandom(nodeNum);//0 到 nodeNum-1
			int to=NumberUtil.getRandom(nodeNum);
			pairsSet.add(from+","+to);
			if(pairsSet.size()==pairNum)
				break;
			if(pairsSet.size()%100000==0)
				System.out.println("已经生成："+pairsSet.size()+"----"+pairsSet.size()*1.0/pairNum);
		}
		List<String> pairsArray=new ArrayList<String>(pairsSet);
	//	for(int i=0; i<pairNum; ++i)
	//		System.out.println(pairsArray.get(i));
		return pairsArray;
	}
	
	public static void generateCleanNet(){
		//	Graph g=new Graph("C:/Users/Admin/Desktop/netlength/node_to_node.txt", " ", true, false);
		//	System.out.println(g);
			String totalDir="E:/netlength/";
			String currentDir="facebook/";//无向图
			currentDir="ca-GrQc/";//虽然说是无向图，但是边文件中已经把边作为有向边了，下载的文件中有说明，但是被我给删掉了，"\t"
			currentDir="CA-HepTh/";//虽然说是无向图，但是边文件中已经把边作为有向边了，下载的文件中有说明，但是被我给删掉了，"\t"
			currentDir="p2p-Gnutella08/";//有向图，\t分割
			currentDir="p2p-Gnutella09/";//有向图，\t分割
			currentDir="wiki-Vote/";//有向图,\t分割
			currentDir="未知网络/";//有向图，左关注右，空格分隔
		//	currentDir="email-Enron/";//无向图，\t分割
		//	currentDir="test1/";
			currentDir="com-DBLP/";//无向图，\t分割
			currentDir="com-Amazon/";//无向图,\t分割
			currentDir="teachers/arvix/";//有向图，\t分割
			currentDir="teachers/facebook/";//无向图，空格分割
			currentDir="teachers/renren/";//无向图，\t分割
			currentDir="teachers/sina/";//微博，有向图，\t分割
			currentDir="teachers/twitter/";//有向图，\t分割
			Graph g=new Graph(totalDir+currentDir+"node_to_node.txt", "\t", "directed", false);
		//	System.out.println(g);
			g.getConnectedComponents();
		//	g.generateCleanNetFile(totalDir+currentDir+"clean_nd.txt", " ");
		//	g.generateNodesPair(40000000, totalDir+currentDir+"node_pair.txt");
	}
	
	public static void main(String[] args)
	{
		generateCleanNet();
		
	}
	
}
