package myutil;

public abstract class CountTime
{
	public abstract void execute();
	
	public void getTime(){
		long begin = System.currentTimeMillis();
		this.execute();
		long cost = System.currentTimeMillis() - begin;
		System.out.println("耗时\t" + cost + "\t毫秒");
	}
	
	public static void main(String[] args){
		
	}
}
