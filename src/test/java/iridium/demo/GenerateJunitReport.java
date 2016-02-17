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
		
	    System.out.println(String.format("Test report link: %1s", desinationFolder));
	}
}
