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
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class GenerateTestCaseVariations {

	private static String timestamp;
	private static String signature;
	private static String testFileName = "test_files/SP_ID.xlsx";
	
	private static String secretKey = "abcdefg";
	private static Mac macHash;
	
	
	private static String getFlatTimeStamp()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
		
	private static int GetNumberOfColumnsFromExcel()
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

	
	private static void AddTestCaseVariationToScenario(BufferedWriter jsonBufferedWriter, VariationScenario scenario, String webMethod, int index) throws IOException
	{
	
		timestamp = "";
		signature = "";
		
		GenerateIridiumVariationSignature(webMethod);
		
		
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"IWSTESTSP0001\"},");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"" + signature + "\"},");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"" + GetAccountNumberFromExcel(index) + "\"},");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"" + timestamp + "\"},");
		jsonBufferedWriter.newLine();
		jsonBufferedWriter.write("\t\t\t\t{\"testRowCell\" : \"" + GetAccountNameFromExcel(index) + "\"}");
		jsonBufferedWriter.newLine();
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
		
		System.out.println(timestamp);
		System.out.println(signature);
		
	}
	
	public static String GenerateTestVariations(String featureFileName, String scenarioTitle, String webMethod, int variationCount) throws IOException
	{
		String testJsonPath = "";
		
		VariationScenario findServiceProfileScenario = new VariationScenario("findServiceProviderProfile test scenario");
		
		if (variationCount < GetNumberOfColumnsFromExcel())
		{
			testJsonPath = String.format("features_json/test_variations/%1s_%2s.json", featureFileName, getFlatTimeStamp());
	        FileWriter jsonFileWriter = new FileWriter(testJsonPath, true);
	        BufferedWriter jsonBufferedWriter = new BufferedWriter(jsonFileWriter);
	    
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
			jsonBufferedWriter.write("\t\t \"testRows\":[ ");
			jsonBufferedWriter.newLine();
	        
			
			for (int i = 0; i < variationCount; i++)
			{
				jsonBufferedWriter.write("\t\t {");
				jsonBufferedWriter.newLine();
				jsonBufferedWriter.write("\t\t\t \"testRow\": [");
				jsonBufferedWriter.newLine();


				AddTestCaseVariationToScenario(jsonBufferedWriter, findServiceProfileScenario, webMethod, i);
				
				jsonBufferedWriter.write("\t\t\t ]");
				jsonBufferedWriter.newLine();

				if (i < variationCount - 1)
				{
					jsonBufferedWriter.write("\t\t },");
					jsonBufferedWriter.newLine();
				}
				else if (i == variationCount - 1)
				{
					jsonBufferedWriter.write("\t\t }");
				}					
				
				try {
				    Thread.sleep(1000);                 //1000 for different timestamps
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				
			}
			jsonBufferedWriter.newLine();
			jsonBufferedWriter.write("\t\t]");
			jsonBufferedWriter.newLine();
			jsonBufferedWriter.write("\t\t}");

			jsonBufferedWriter.newLine();
			jsonBufferedWriter.write("]");

			
			jsonBufferedWriter.flush();
			jsonBufferedWriter.close();
		}
		
		return testJsonPath;
		
	}

	
}
