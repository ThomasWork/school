package life.windowslog;

public class Event4647 extends EventLog
{
	public static String eventDescription="用户注销"+EventLog.getShutdownString();

	public Event4647(String[] content)
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
		
	}

}
