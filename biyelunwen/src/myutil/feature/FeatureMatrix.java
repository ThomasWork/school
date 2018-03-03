package myutil.feature;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myutil.NumberUtil;
import myutil.fileprocess.FileUtil;

public class FeatureMatrix {

	public double[][] features;
	
	public static List<List<Double>> getFeatureColumn(double[][] matrix){
		List<List<Double>> columnList=new ArrayList<List<Double>>();
		int row=matrix.length;
		int column=matrix[0].length;
		for(int i=0; i<column; ++i){
			columnList.add(new ArrayList<Double>());
		}
		
		for(int i=0; i<column; ++i){//对于每一列
			for(int j=0; j<row; ++j){
				columnList.get(i).add(matrix[j][i]);
			}
		}
		return columnList;
	}
	
	public static double[][] getMatrix(String path, String split, boolean useFirstColumn){
		double [][] matrix=null;
		List<String> lines=FileUtil.getLinesFromFile(path);
		int column=lines.get(0).split(split).length;//这里应该考虑是否包含结尾？
		if(!useFirstColumn)//如果不使用第一列
			column--;
		matrix=new double[lines.size()][column];
		int start=1;//从第几列开始
		if(useFirstColumn)
			start=0;
		for(int i=0; i<lines.size(); ++i){
			String line=lines.get(i);
			String[] ss=line.split(split);
			for(int j=start, curCol=0; j<ss.length; ++j, ++curCol)
				matrix[i][curCol]=Double.parseDouble(ss[j]);
		}
		return matrix;
	}
	
	public static double[][] getMatrix(boolean[] used, double[][] source){
		int row=source.length;
		int column=0;
		for(int i=0; i<used.length; ++i){
			if(used[i]){
				column++;
			}
		}
		double[][] dest=new double[row][column];
		int curCol=0;
		for(int i=0; i<used.length;++i){
			if(used[i]){//如果需要这一列
				for(int j=0; j<row; ++j){
					dest[j][curCol]=source[j][i];
				}
				curCol++;
			}
		}
		return dest;
	}
	
	public static void test_getMatrix(){
		double [][] source={{1.0, 2.0, 3.0, 4.0}, {5.0, 6.0, 7.0, 8.0}, {9.0, 10.0, 11.0, 12.0}};
		boolean[] used={true, false, false, true};
		double[][] dest=getMatrix(used, source);
		showMatrix(dest);
	}
	
	public static void showMatrix(double[][] matrix){
		if(matrix.length<=0)
			return;
		int column=matrix[0].length;
		for(int i=0; i<matrix.length; ++i){			
			for(int j=0; j<column; ++j)
				System.out.print(matrix[i][j]+",\t");
			System.out.println();
		}
	}
	
	public static void writeMatrix(double[][] matrix, DecimalFormat df, String path){
		List<String> output=new ArrayList<String>();
		int column=matrix[0].length;
		for(int i=0; i<matrix.length; ++i){	
			StringBuilder sb=new StringBuilder(df.format(matrix[i][0]));
			for(int j=1; j<column; ++j)//这里从1开始
				sb.append(","+df.format(matrix[i][j]));
			output.add(sb.toString());
		}
		FileUtil.NewFile(path, output);
	}
	
	public static void test_writeMatrix(){
		String path=System.getProperty("user.dir")+"/src/myutil/feature/test_write_matrix.txt";
		double [][] source={{1.0, 2.0, 3.0, 4.0}, {5.0, 6.0, 7.0, 8.0}, {9.0, 10.0, 11.0, 12.0}};
		writeMatrix(source, NumberUtil.dfint, path);
	}
	
	public static class Feature{
		public String id;
		public double min;
		public double max;
		public double sectionLength;//步长
		
		public List<Double> values;
		
		public Feature(String idPar, List<Double> valuesPar){
			this.id=idPar;
			this.values=valuesPar;
		}
		
		//距离最小值的距离（最小为0），距离最大值的距离（最小值为0），分组数量
		public void setParameters(int minDis, int maxDis, int sectionNum){
			Collections.sort(this.values);//进行排序，有小到大
			this.min=this.values.get(minDis);
			int maxIndex=this.values.size()-1-maxDis;
			this.max=this.values.get(maxIndex);
			this.sectionLength=(this.max-this.min)/sectionNum;
		}
		
		@Override
		public String toString(){
			String temp="["+this.id+"]\n";
			temp+="max_value="+this.max+"\n";
			temp+="min_value="+this.min+"\n";
			temp+="section_len="+this.sectionLength+"\n";
			temp+="column_source=browse_feature\n";
			temp+="is_valid=1";
			return temp;
		}
	}
	
	public static void feature_test(){
		String path=System.getProperty("user.dir")+"/src/myutil/feature/matrix.txt";
	//	path="haha.txt";
	//	System.out.println(System.getProperty("java.class.path"));
	//	System.out.println();
	//	FileUtil.NewFile(path, new ArrayList<String>());
		double[][] matrix=getMatrix(path, ",", true);
		List<List<Double>> columns=getFeatureColumn(matrix);
		for(int i=0; i<columns.size(); ++i){
			List<Double> column=columns.get(i);
			Feature f=new Feature(i+"", column);
			f.setParameters(0, 0, matrix.length-1);
			System.out.println(f);
		}
	}
	
	public static void main(String[] args) {
	//	feature_test();
	//	test_getMatrix();
		test_writeMatrix();
	}

}
