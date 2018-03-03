package life.windowslog;

public class Event5056 extends EventLog
{
	public static String eventDescription="已执行加密自检";

	public Event5056(String[] content)
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
