package algorithm.kruskal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sciencecluster.MyPoint;

public class Kruskal
{
	public static class TreeNode {
		public List<TreeNode> parent;
		public List<TreeNode> next;
		public MyPoint mp;
		public int tag;
		
		public TreeNode(MyPoint mp, int tagP) {
			this.mp = mp;
			this.next = new ArrayList<TreeNode>();
			this.parent = new ArrayList<TreeNode>();
			this.tag = tagP;
		}
		
		public void spreadTag(int newTag) {
			if (this.tag == newTag) {
				return;
			}
			this.tag = newTag;
			for (int i = 0; i < this.parent.size(); i += 1) {
				this.parent.get(i).spreadTag(newTag);
			}
			for (int i = 0; i < this.next.size(); i += 1) {
				this.next.get(i).spreadTag(newTag);
			}
		}
		
		public List<TreeNode> cutTree(double distance) {
			
		}
		
		public String getMPString(MyPoint mp) {
			return mp.userId + "," + mp.x + "," + mp.y;
		}
		
		@Override
		public String toString() {
			String temp = this.mp.userId + "\nparent:";
			for (int i = 0; i < this.parent.size(); i += 1) {
				temp += this.getMPString(this.parent.get(i).mp) + ",\t";
			}
			temp += "\nchild:\n";
			for (int i = 0; i < this.next.size(); i += 1) {
				temp += this.getMPString(this.next.get(i).mp) + ",\t";
			}
			return temp;
		}
	}
	
	private static class SortNode implements Comparable {
		public int x;
		public int y;
		public double distance;
		
		public SortNode(int xP, int yP, double dis) {
			this.x = xP;
			this.y = yP;
			this.distance = dis;
		}
		
		@Override
		public int compareTo(Object other) {
			SortNode sn = (SortNode)other;
			Double thisV = this.distance;
			Double otherV = sn.distance;
			return thisV.compareTo(otherV);
		}
		
		@Override
		public String toString() {
			return this.x + "," + this.y + "," + this.distance;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static TreeNode getTreeNode(List<MyPoint> mps) {
		if (mps.size() < 0) {
			return null;
		}
		int num = mps.size();
		List<SortNode> sns = new ArrayList<SortNode>();
		for (int i = 0; i < mps.size(); i += 1) {
			for (int j = i + 1; j < mps.size(); j += 1) {
				SortNode sn = new SortNode(i, j, mps.get(i).getDistance(mps.get(j)));
				sns.add(sn);
			}
		}
		Collections.sort(sns);
		System.out.println(sns);
		TreeNode [] visited = new TreeNode[num];
		for (int i = 0; i < visited.length; i += 1) {
			visited[i] = new TreeNode(mps.get(i), i);
		}
		int current = 0;
		for (int i = 0; i < num - 1; i += 1) {
			while(true) {
				SortNode sn = sns.get(current);
				if (visited[sn.x].tag != visited[sn.y].tag) {
					visited[sn.x].next.add(visited[sn.y]);
					visited[sn.y].parent.add(visited[sn.x]);
					visited[sn.y].spreadTag(visited[sn.x].tag);
					MyPoint mp1 = mps.get(sn.x);
					MyPoint mp2 = mps.get(sn.y);
					System.out.println("Current: x=" + mp1.userId + ",\ty=" + mp2.userId + ",\ttag=" + visited[sn.x].tag + ",\ttag=" + visited[sn.y].tag);
					break;
				}
				current += 1;
			}
		}
		for (int i = 0; i < num; i += 1) {
			System.out.println(visited[i]);
		}
		return visited[0];
	}
	
	public static void test1() {
		MyPoint.mpd = new MyPoint.MyPointCoordinateDistance();
		List<MyPoint> mps = new ArrayList<MyPoint>();
		mps.add(new MyPoint(0, 0, "0-0"));
		mps.add(new MyPoint(0, 1, "0-1"));
		mps.add(new MyPoint(0, 2, "0-2"));
		mps.add(new MyPoint(2, 2, "2-2"));
		mps.add(new MyPoint(10, 10, "10-10"));
		mps.add(new MyPoint(10, 11, "10-11"));
		mps.add(new MyPoint(10, 12, "10-12"));
		mps.add(new MyPoint(12, 12, "12-12"));
		TreeNode tn = getTreeNode(mps);
		System.out.println(tn);
	}
	
	public static void main(String[] args)
	{
		test1();
	}

}
