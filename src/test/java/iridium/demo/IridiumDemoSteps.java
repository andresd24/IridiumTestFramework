package iridium.demo;

//import static org.hamcrest.CoreMatchers.containsString;
//import static org.junit.Assert.assertThat;
//import static org.junit.Assert.assertTrue;
//import org.w3c.dom.Document;
//import org.w3c.dom.NodeList;
//import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

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
//import cucumber.api.java.en.When;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class IridiumDemoSteps {
	
	public class VariationPair 
	{
	    private String templateString = "";
	    private String testData = "";

	    public VariationPair(String templateString, String testData)
	    {
	        this.templateString = templateString;
	        this.testData = testData;
	    }
	    public VariationPair() { }

	    public String getTemplateString() {
	        return templateString;
	    }

	    public String getTestData() {
	        return testData;
	    }

	    public void setTestVariation(String templateSting, String testData) {
	        this.templateString = templateSting;
	        this.testData = testData;
	    }
	}		

	private ArrayList<VariationPair> soapVariables = new ArrayList<VariationPair>();
	private ArrayList<String> allResponseLines = new ArrayList<String>();
	
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

	    String templateSoapFilePath = String.format("soap_files/test_templates/%1sTemplate.xml", soapMethodToTest);
        String soapFileTestVariationFilePath = String.format("soap_files/test_cases/%1s_%2s.xml", soapMethodToTest, flatTimeStamp);

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
        	System.out.println(line);
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
	
	
	@Given("^the method exists findServiceProviderProfile exists the Iridiums service$") 
	public void checkIfFindServiceProviderProfileExists() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException 
	{
/*		
		Process p = new ProcessBuilder("ext/curl", "-G", "http://192.168.0.218:8080/iws-current/iws-int?wsdl").start();
		
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
		*/
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

		fileName = String.format("@soap_files/test_cases/%1s", fileName);
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

	@Then("^result must contain the expected account number '(.*)' account name '(.*)'$")
	public void checkThatTheResultContainsTheAccountNumberAndName()
	{
		
		
	}
	
	
}
