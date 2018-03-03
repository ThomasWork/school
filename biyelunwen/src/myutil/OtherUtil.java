package myutil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OtherUtil
{
	public static String UrlToName(String url)
	{
		String result="";
		char[] rs={'/', '\\', '|', ':', '?', '*', '\"', '<', '>'};
		for(int i=0;i<url.length();i++)
		{
			char c=url.charAt(i);
			int j;
			for(j=0;j<rs.length;j++)
				if(rs[j]==c)
					c=' ';
			result+=c;
		}
		return result;
	}
	
	public static String ChangeString2Date(String dateStr)
	{
		if (dateStr.matches("\\d+"))
		{
			SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return formate.format(new Date(Long.parseLong(dateStr)));
		}

		String date = "";
		String[] tempDateStr = dateStr.trim().replaceAll("\\s+", "@")
				.replaceAll("(-|:)", "@").split("@");

		if (tempDateStr.length > 0)
		{
			String year = tempDateStr[0];
			if (year.length() == 2) 
			{
				year = "20" + year;
			}
			date = date + year + "-";
		}

		if (tempDateStr.length > 1) 
		{
			date = date
					+ (tempDateStr[1].length() > 1 ? tempDateStr[1] : "0"
							+ tempDateStr[1]) + "-";
		}

		if (tempDateStr.length > 2)
		{
			date = date
					+ (tempDateStr[2].length() > 1 ? tempDateStr[2] : "0"
							+ tempDateStr[2]) + " ";
		}

		if (tempDateStr.length > 3)
		{
			date = date
					+ (tempDateStr[3].length() > 1 ? tempDateStr[3] : "0"
							+ tempDateStr[3]) + ":";
		} 
		else 
		{
			date = date + "00:";
		}

		if (tempDateStr.length > 4) 
		{
			date = date
					+ (tempDateStr[4].length() > 1 ? tempDateStr[4] : "0"
							+ tempDateStr[4]) + ":";
		}
		else
		{
			date = date + "00:";
		}

		if (tempDateStr.length > 5)
		{
			date = date
					+ (tempDateStr[5].length() > 1 ? tempDateStr[5] : "0"
							+ tempDateStr[5]);
		} else 
		{
			date = date + "00";
		}

		return date;
	}
}
