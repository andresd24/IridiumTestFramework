package iridium.demo;

import tools.datagen.VariationPair;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.net.MalformedURLException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class IridiumDemoSteps {
	
	private ArrayList<VariationPair> soapVariables = new ArrayList<VariationPair>();
	private ArrayList<String> allResponseLines = new ArrayList<String>();
	
	private static String getCurrentExecution() throws IOException
	{
        File currentExecutionFile = new File("test_executions/current_execution.txt");
        
        FileInputStream currentExecutionReader = new FileInputStream(currentExecutionFile);
        BufferedReader currentExecutionBufferedReader = new BufferedReader(new InputStreamReader(currentExecutionReader));

        String current_execution_id = "";
        String line = null;
        while ((line = currentExecutionBufferedReader.readLine()) != null)
        {
        	current_execution_id = line.trim();
        }
        currentExecutionBufferedReader.close();
        
        return current_execution_id;
        
	}
	
	private static String getFlatTimeStamp()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
	private static String CreateSoapXMLFileVariation(String soapMethodToTest, ArrayList<VariationPair> soapVariables) throws FileNotFoundException, IOException
    {
		String flatTimeStamp = getFlatTimeStamp();
		String currentExecutionId = getCurrentExecution();
		
	    String templateSoapFilePath = String.format("input_files/soap_templates/%1sTemplate.xml", soapMethodToTest);
        String soapFileTestVariationFilePath = String.format("test_executions/%1s/soap_requests/%2s_%3s.xml", currentExecutionId, soapMethodToTest, flatTimeStamp);

        File templateSoapFile = new File(templateSoapFilePath);
        File soapFileTestVariationFile = new File (soapFileTestVariationFilePath);

        if(soapFileTestVariationFile.exists() && !soapFileTestVariationFile.isDirectory()) { 
        	soapFileTestVariationFile.delete();
        }
        
        FileInputStream templateSoapFileInput = new FileInputStream(templateSoapFile);
        BufferedReader templateFileInputBufferedReader = new BufferedReader(new InputStreamReader(templateSoapFileInput));

        FileWriter soapFileTestVariationFileWriter = new FileWriter(soapFileTestVariationFilePath, true);
        BufferedWriter soapFileTestVariationBufferedWriter = new BufferedWriter(soapFileTestVariationFileWriter);
        

        String line = null;
        while ((line = templateFileInputBufferedReader.readLine()) != null)
        {
        	for (int i = 0 ; i < soapVariables.size(); i++)
        	{
        		String currentTemplateString = soapVariables.get(i).getTemplateString(); 
        		
                if (line.contains(currentTemplateString)) 
                {
                	String currentTestData = soapVariables.get(i).getTestData(); 
            		line = line.replace(currentTemplateString, currentTestData);
                	break;
                }
        	}
            soapFileTestVariationBufferedWriter.write(line);
            if (!line.contains("</soap:Envelope>"))
            {
                soapFileTestVariationBufferedWriter.newLine();
            }
        }
        	
        templateSoapFileInput.close();
        templateFileInputBufferedReader.close();
        soapFileTestVariationBufferedWriter.flush();
        soapFileTestVariationFileWriter.flush();
        soapFileTestVariationFileWriter.close();
        
        return soapFileTestVariationFile.getName();
    }
	
	
	@Given("^that the Iridiums service is up and running$") 
	public void checkIfFindServiceProviderProfileExists() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException 
	{
		
		String urlStr = "http://192.168.0.218:8080/iws-current/iws-int?wsdl";
		URL url = null;
		boolean assertionIsThrown = false;
		try {
			  url = new URL(urlStr);
			  URLConnection urlConnection = url.openConnection();
			  //urlConnection.wait();
			 // urlConnection.connect();
		} catch (MalformedURLException ex) {
				assertionIsThrown = true;   
				System.out.println("bad URL");
		} catch (IOException ex) {
				assertionIsThrown = true;   
				System.out.println("Failed opening connection. Perhaps WS is not up?");
		}
		assertFalse(assertionIsThrown);
		return;
	}
	
	
	@And("^a request is sent to findServiceProviderProfile with iwsUsername '(.*)', signature '(.*)', serviceProviderAccountNumber '(.*)' and timestamp '(.*)'$")
	public void sendRequestToFindServiceProviderProfile(String iwsUsername, String signature, String serviceProviderAccountNumber, String timeStamp) throws InterruptedException, IOException 
	{
		
		VariationPair iwsUsernamePair = new VariationPair();
		iwsUsernamePair.setTestVariation("{iwsUsername}", iwsUsername);
		soapVariables.add(iwsUsernamePair);
		
		VariationPair signaturePair = new VariationPair();
		signaturePair.setTestVariation("{signature}", signature);
		soapVariables.add(signaturePair);
				
		VariationPair serviceProviderAccountNumberPair = new VariationPair();
		serviceProviderAccountNumberPair.setTestVariation("{serviceProviderAccountNumber}", serviceProviderAccountNumber);
		soapVariables.add(serviceProviderAccountNumberPair);
				
		VariationPair timeStampPair = new VariationPair();
		timeStampPair.setTestVariation("{timestamp}", timeStamp);
		soapVariables.add(timeStampPair);
		
		String fileName = CreateSoapXMLFileVariation("findServiceProviderProfile", soapVariables);

		fileName = String.format("@test_executions/%1s/soap_requests/%2s", getCurrentExecution(), fileName);
		System.out.println(fileName);
        
		Process p = new ProcessBuilder("ext/curl", "-X", "POST", "--header", "Content-Type: application/soap+xml;charset=UTF-8", 
				"--header", "SOAPAction:irid:findServiceProviderProfile", "--data", fileName,  "http://192.168.0.218:8080/iws-current/iws-int").start();

		InputStream inputStream = p.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
        String newline = "";
        while ((newline = bufferedReader.readLine()) != null)
        {
        	System.out.println(newline);
        	allResponseLines.add(newline);
        }
		p.destroy();
	}

	@Then("^the result must contain the expected account number '(.*)' and account name '(.*)'$")
	public void checkThatTheResultContainsTheAccountNumberAndName(String accountNumber, String accountName)
	{
        boolean passedThroughLoop = false;
        for (int i = 0; i < allResponseLines.size(); i++)
        {
        	assertThat(allResponseLines.get(i), containsString(String.format("<accountNumber>%1$s</accountNumber><accountName>%2$s</accountName>", accountNumber, accountName)));
        	passedThroughLoop = true;
        }
        assertTrue(passedThroughLoop);
	}
	

}
