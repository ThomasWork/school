package life.windowslog;

public class Event4648 extends EventLog
{
	public static String eventDescription="以其他用户的权限运行程序";

	public Event4648(String[] content)
	{
		super(content);
	}

	@Override
	public String getMessage()
	{
		String temp=this.getBasicString()+eventDescription;
		return temp;
	}

	@Override
	public void setSelfContent()
	{
		// TODO Auto-generated method stub

	}

}
