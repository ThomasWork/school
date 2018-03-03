package diary;

import org.apache.log4j.Logger;

public class Log4JTest
{
	private static Logger logger = Logger.getLogger(Log4JTest.class); 
	public static void main(String[] args)
	{ 
		logger.debug("This is debug message.");
		logger.info("This is info message.");
		logger.error("This is error message."); 
	}
}
