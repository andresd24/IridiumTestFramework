package iridium.demo;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class GenerateJunitReport {

	private static final String sourceReportsFolder = "./target/cucumber";
	
	public static void execute() throws IOException, InterruptedException
	{
		String currentExecutionId = IridiumDemoSteps.getCurrentExecution();
		File desinationFolder = new File(String.format("test_executions/%1s/test_report", currentExecutionId));
		
		Thread.sleep(2000);
		FileUtils.copyDirectory(new File(sourceReportsFolder), desinationFolder, false);
		
		String pathToReport = String.format("%s1/%s2/index.html", System.getProperty("user.dir"), desinationFolder);
		
		//file:///C:/Users/adevivanco/workspace/IridiumTestFramework/test_executions/2016_02_16_10_42_02/test_reports/index.html
		
		String urlLinkToPath = String.format("file:///%s1",pathToReport.replace("\\", "/"));	
			
			
	    System.out.println(String.format("Test report link: %s1", urlLinkToPath));
	}
}
