package sciencecluster;

import java.util.ArrayList;
import java.util.List;

import myutil.fileprocess.FileUtil;

public class DistanceMatrix
{
	public int rowNum;
	public int columnNum;
	private double[][] distance=null;
	
	public static String sepaString=",";

	public DistanceMatrix(String disPath){
		List<String> lines=FileUtil.getLinesFromFile(disPath);
		String[] ss0=lines.get(0).split(DistanceMatrix.sepaString);
		this.rowNum=Integer.parseInt(ss0[0]);
		this.columnNum=Integer.parseInt(ss0[1]);
		
		this.distance=new double[this.rowNum][this.columnNum];
		
		for(int i=0; i<this.rowNum; ++i){
			String line=lines.get(i+1);//这里要加1
			String[] ss=line.split(DistanceMatrix.sepaString);
			for(int j=0; j<this.columnNum; ++j){
				this.distance[i][j]=Double.parseDouble(ss[j]);
			}
		}
		System.out.println("读取距离完毕！行数："+this.rowNum+"\t列数："+this.columnNum);
	}
	
	public DistanceMatrix(double[][] distancePar){
		this.distance=distancePar;
		this.rowNum=this.distance.length;
		this.columnNum=this.distance[0].length;
	}
	
	public double getDistance(int i, int j){
		return this.distance[i][j];
	}
	
	public void saveToFile(String path){
		String first=this.rowNum+DistanceMatrix.sepaString+this.columnNum;
		List<String> output=new ArrayList<String>();
		output.add(first);
		for(int i=0; i<this.rowNum; ++i){
			StringBuilder sb=new StringBuilder();
			for(int j=0; j<this.columnNum; ++j){
				sb.append(this.distance[i][j]+DistanceMatrix.sepaString);
			}
			output.add(sb.toString());
		}
		FileUtil.NewFile(path, output);
	}
	
	@Override
	public String toString(){
		String first=this.rowNum+DistanceMatrix.sepaString+this.columnNum;
		StringBuilder output=new StringBuilder(first+"\n");//非线程安全，速度更快
		for(int i=0; i<this.rowNum; ++i){
			StringBuilder sb=new StringBuilder();
			for(int j=0; j<this.columnNum; ++j){
				sb.append(this.distance[i][j]+DistanceMatrix.sepaString);
			}
			output.append(sb+"\n");
		}
		return output.toString();
	}
	
	public static void main(String[] args)
	{
		double[][] dist=new double[2][4];
		int total=0;
		for(int i=0; i<2; ++i){
			for(int j=0; j<4; ++j)
				dist[i][j]=total++;
		}
		DistanceMatrix dm=new DistanceMatrix(dist);
		System.out.println(dm);
		
		String path="haha.txt";
		dm.saveToFile(path);
		DistanceMatrix dmf=new DistanceMatrix(path);
		System.out.println(dmf);
	}

}
