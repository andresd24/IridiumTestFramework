package tools.datagen;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.util.Random;

public class GenerateTestCaseVariations {

	private static String timestamp;
	private static String signature;
	private static String testFileName = "input_files/database_files/SP_ID.xlsx";
	
	private static String secretKey = "abcdefg";
	private static Mac macHash;
	
	private static ArrayList<Integer> listOfPossibleValues = new ArrayList<Integer>();
	
	private static void loadInitialVariations(int maxNumberOfValues)
	{
		 for (int i = 0; i < maxNumberOfValues; i++)
		 {
			 listOfPossibleValues.add(i);
		 }
	}
	
	private static int GetRandomVariationIndex() 
	{
	    Random rand = new Random();
	    int randomNum = rand.nextInt(listOfPossibleValues.size());
	    listOfPossibleValues.remove(randomNum);
	    return randomNum;
	}
	
	private static int GetNumberOfRowsFromExcel()
	{
		int rows = 0;
		try {
			FileInputStream input = new FileInputStream(new File(testFileName));
			Workbook workBook = WorkbookFactory.create(input);		
			Sheet sheet = workBook.getSheetAt(0);
			rows = sheet.getPhysicalNumberOfRows();
		    
		} catch(Exception ioe) {
		    ioe.printStackTrace();
		}
		
		return rows;
	}

	
	private static String GetAccountNameFromExcel(int rowIndex)
	{
		String accountName = "";
		try {
			FileInputStream input = new FileInputStream(new File(testFileName));
			Workbook workBook = WorkbookFactory.create(input);		
			Sheet sheet = workBook.getSheetAt(0);
			
		    Row row;
		    Cell cell;
		    DataFormatter formatter = new DataFormatter();		    
		    
		    row = sheet.getRow(rowIndex);
		    cell = row.getCell(0);
			accountName = formatter.formatCellValue(cell);
		    
		    
		} catch(Exception ioe) {
		    ioe.printStackTrace();
		}
		
		return accountName;
	}

	private static String GetAccountNumberFromExcel(int rowIndex)
	{
		String accountNumber = "";
		try {
			FileInputStream input = new FileInputStream(new File(testFileName));
			Workbook workBook = WorkbookFactory.create(input);		
			Sheet sheet = workBook.getSheetAt(0);
			
		    Row row;
		    Cell cell;
		    DataFormatter formatter = new DataFormatter();		    
		    
		    row = sheet.getRow(rowIndex);
		    cell = row.getCell(1);
		    accountNumber = formatter.formatCellValue(cell);
		    
		    
		} catch(Exception ioe) {
		    ioe.printStackTrace();
		}
		
		return accountNumber;
	}

	
	private static void GenerateIridiumVariationSignature(String soapMethod)
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		timestamp = sdf.format(now);
		
		try {
			macHash = Mac.getInstance("HmacSHA1");
			macHash.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA1"));
		} catch (Exception e) {
		     throw new RuntimeException(e);
		}
		String source = soapMethod + timestamp;
		
		byte[] result = macHash.doFinal( source.getBytes() );
		signature = new String(Base64.encodeBase64(result));
		
	
	}
	
	private static void AddTestCaseVariationToScenario(BufferedWriter jsonBufferedWriter, String webMethod, int index) throws IOException
	{
	
		timestamp = "";
		signature = "";
		String accountNumber = "";
		String accountName = "";
		
		System.out.println("");
	    System.out.println(String.format("Generating variations using database row %1s:", (index +1)));
		GenerateIridiumVariationSignature(webMethod);
		
		
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"IWSTESTSP0001\"},");
		jsonBufferedWriter.newLine();
		System.out.println("iwsUsername: IWSTESTSP0001");

		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"" + signature + "\"},");
		jsonBufferedWriter.newLine();
		System.out.println(String.format("signature: %1s", signature));
		
		accountNumber = GetAccountNumberFromExcel(index); 
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"" + accountNumber+ "\"},");
		jsonBufferedWriter.newLine();
		System.out.println(String.format("serviceProviderAccountNumber: %1s", accountNumber));
		
		
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"" + timestamp + "\"},");
		jsonBufferedWriter.newLine();
		System.out.println(String.format("timestamp: %1s", timestamp));
		
		accountName = GetAccountNameFromExcel(index);
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"" + accountName + "\"}");
		jsonBufferedWriter.newLine();
		System.out.println(String.format("accountName: %1s", accountName));
		
	}
	
	
	private static void GenerateExamplesTableHeader(BufferedWriter jsonBufferedWriter, String scenarioTitle) throws IOException
	{
		jsonBufferedWriter.write("[");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t {");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t \"title\": \"" + scenarioTitle +  "\",");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t \"testColumnTitles\":[");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t \"iwsUsername\",");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t \"signature\",");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t \"serviceProviderAccountNumber\",");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t \"timestamp\",");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t \"accountName\"");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t ]");
		jsonBufferedWriter.newLine();
	}
	
	private static void GenerateExampleTestRowVariation(BufferedWriter jsonBufferedWriter, String webMethod, int variationCount, int index) throws IOException
	{
		jsonBufferedWriter.write("\t\t {");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t \"testRow\": [");
		jsonBufferedWriter.newLine();

		AddTestCaseVariationToScenario(jsonBufferedWriter, webMethod, index);
		
		jsonBufferedWriter.write("\t\t\t ]");
		jsonBufferedWriter.newLine();

		if (index < variationCount - 1)
		{
			jsonBufferedWriter.write("\t\t },");
			jsonBufferedWriter.newLine();
		}
		else if (index == variationCount - 1)
		{
			jsonBufferedWriter.write("\t\t }");
		}					
		else 
		{
			jsonBufferedWriter.write("\t\t }");
		}					
		
		try {
		    Thread.sleep(1000);                 //1000 for different timestamps
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	private static void GenerateExampleTestVariationFooter(BufferedWriter jsonBufferedWriter) throws IOException
	{
    	jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t]");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t}");

		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("]");
	}
	
	
	public static String GenerateSequencedTestVariationsForScenario(String featureFileName, VariationScenario variationScenario, String executionFolder, int variationCount) throws IOException
	{
		String testJsonPath = "";
		System.out.println(String.format("Generating %1d sequenced test variations......", variationCount));
		System.out.println("");

		int maxVaritions = GetNumberOfRowsFromExcel();
		
		if (variationCount < maxVaritions)
		{
			testJsonPath = String.format("test_executions/%1s/%2s.json", executionFolder, featureFileName);
	        FileWriter jsonFileWriter = new FileWriter(testJsonPath, true);
	        BufferedWriter jsonBufferedWriter = new BufferedWriter(jsonFileWriter);
	    
	        GenerateExamplesTableHeader(jsonBufferedWriter, variationScenario.getScenarioTitle());
			
			jsonBufferedWriter.write("\t\t \"testRows\":[ ");
			jsonBufferedWriter.newLine();
	        
			// generate variations
			for (int i = 0; i < variationCount; i++)
			{
	        	/***************************************************************************************************/
	        	// TODO: We can't hard code to the first web method here.  We must find a way to logically select  //
	        	//       the web service related to the data generation step.  Here it's the only one:             //
	        	//		 findServiceProviderProfile because it's the only one created                        	   //
	        	/***************************************************************************************************/
				
				GenerateExampleTestRowVariation(jsonBufferedWriter, variationScenario.getListOfWebMethodsInScenario().get(0), variationCount, i); 
			}

			GenerateExampleTestVariationFooter(jsonBufferedWriter);
			jsonBufferedWriter.close();
			jsonFileWriter.close();
		}
		
		return testJsonPath;
	}

	public static String GenerateRandomTestVariationsForScenario(String featureFileName, VariationScenario variationScenario, String executionFolder, int variationCount) throws IOException
	{
		String testJsonPath = "";
		int maxVaritions = GetNumberOfRowsFromExcel();
		System.out.println("");
		System.out.println(String.format("Generating %1d random test variations......", variationCount));
		
		if (variationCount < maxVaritions)
		{
			loadInitialVariations(maxVaritions);

			testJsonPath = String.format("test_executions/%1s/%2s.json", executionFolder, featureFileName);
	        FileWriter jsonFileWriter = new FileWriter(testJsonPath, true);
	        BufferedWriter jsonBufferedWriter = new BufferedWriter(jsonFileWriter);
	    
	        GenerateExamplesTableHeader(jsonBufferedWriter, variationScenario.getScenarioTitle());
			
			jsonBufferedWriter.write("\t\t \"testRows\":[ ");
			jsonBufferedWriter.newLine();
	        
			// generate variations
			for (int i = 0; i < variationCount; i++)
			{
	        	/***************************************************************************************************/
	        	// TODO: We can't hard code to the first web method here.  We must find a way to logically select  //
	        	//       the web service related to the data generation step.  Here it's the only one:             //
	        	//		 findServiceProviderProfile because it's the only one created                        	   //
	        	/***************************************************************************************************/
				
				int randomIndex = GetRandomVariationIndex();
				GenerateExampleTestRowVariation(jsonBufferedWriter, variationScenario.getListOfWebMethodsInScenario().get(0), variationCount, randomIndex); 
			}

			GenerateExampleTestVariationFooter(jsonBufferedWriter);
			
			jsonBufferedWriter.close();
			jsonFileWriter.close();

		}
		
		return testJsonPath;
	}

}
