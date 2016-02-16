package iridium.demo;

import iridium.demo.GenerateJunitReport;
import org.junit.BeforeClass;

public abstract class AbstractTest {

	@BeforeClass
	public static void setUp(){
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	try {
					GenerateJunitReport.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
    }
}
