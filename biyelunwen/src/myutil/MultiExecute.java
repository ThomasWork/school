/**
 * 这个接口的作用是重复执行指定的函数
 */
package myutil;

/**
 * @author Admin
 *
 */
public abstract class MultiExecute
{
	public static Object getResult(MultiExecute me, int times){
		if(times<=0)//不再尝试
			return null;
		try{			
			return me.realFunction();
		}catch(Exception e){
			System.out.println(e.toString());
			return getResult(me, times-1);
		}
	}
	
	abstract Object realFunction();
}
