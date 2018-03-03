package life.windowslog;

public class Event4902 extends EventLog
{
	public static String eventDescription="已创建每用户审核策略表（在系统启动时会有此事件，可忽略）";

	public Event4902(String[] content)
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
