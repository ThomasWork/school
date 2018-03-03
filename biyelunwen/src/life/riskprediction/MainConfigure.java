package life.riskprediction;

public class MainConfigure
{
	public static String mainFolder="G:/个人征信/";
	public static String trainFolder=mainFolder+"train/";
	
	public static String userInfoPath=trainFolder+"user_info_train.txt";
	public static String bankDetailPath=trainFolder+"bank_detail_train.txt";
	public static String browseHistoryPath=trainFolder+"browse_history_train.txt";
	public static String billDetailPath=trainFolder+"bill_detail_train.txt";
	public static String loanTimePath=trainFolder+"loan_time_train.txt";
	public static String overDuePath=trainFolder+"overdue_train.txt";

	public static String testFolder=mainFolder+"test/";
	public static String userInfoPath_Test=testFolder+"user_info_test.txt";
	public static String browseHistoryPath_Test=testFolder+"browse_history_test.txt";
	public static String loanTimePath_Test=testFolder+"loan_time_test.txt";
	public static String overDuePath_Test=testFolder+"overdue_test.txt";
	
	
	public static String basicFeaturePath=trainFolder+"basic_feature_train.txt";
	
	public static String featureFolder=mainFolder+"feature/";
	
	public static String browseFeatureKeyPath=featureFolder+"浏览数据和浏览子行为编号.txt";
	public static String browseFeaturePath=featureFolder+"浏览数据特征.txt";
	public static String browseFeaturePath_Test=featureFolder+"浏览数据特征_Test.txt";
			
			
	public static void main(String[] args)
	{
		System.out.println("haha");
	}

}
