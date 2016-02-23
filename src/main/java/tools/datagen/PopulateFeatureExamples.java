package tools.datagen;

import tools.datagen.GenerateTestCaseVariations;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class PopulateFeatureExamples {
	
	private static List<VariationScenario> variationScenarios = new ArrayList<VariationScenario>();

	private static String getTestExecutionId()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
		
	private static void ProcessExample(BufferedWriter processedFeatureFileTempBufferedWriter, JSONArray jsonArray, String inScenarioTitle) throws FileNotFoundException, IOException
    {
		processedFeatureFileTempBufferedWriter.write("\t Examples:");
        processedFeatureFileTempBufferedWriter.newLine();

		for (Object o : jsonArray)
    	{
		   JSONObject scenario = (JSONObject) o;
		   String scenarioTitle = (String) scenario.get("title");
		   
		   if (scenarioTitle.toLowerCase().equals(inScenarioTitle.toLowerCase()))
		   {
			   JSONArray testColumnTitles = (JSONArray) scenario.get("testColumnTitles");
			   
			   for (Object colTitle : testColumnTitles)
			   {
	               processedFeatureFileTempBufferedWriter.write(" | ");
	               processedFeatureFileTempBufferedWriter.write(colTitle.toString());
			   }
	           processedFeatureFileTempBufferedWriter.write(" | ");
	           processedFeatureFileTempBufferedWriter.newLine();
	           
	           JSONArray testRows = (JSONArray) scenario.get("testRows");
	           for (Object row : testRows)
			   {
	    		   JSONObject currentRow = (JSONObject) row;
	               JSONArray testCells = (JSONArray) currentRow.get("testRow");
			   
	               for (Object cell : testCells)
	    		   {
	            	   JSONObject currentCell = (JSONObject) cell;
	            	   String cellValue = (String) currentCell.get("testRowCell");
	            	   
	                   processedFeatureFileTempBufferedWriter.write(" | ");
	                   processedFeatureFileTempBufferedWriter.write(cellValue);
	    		   }
	               processedFeatureFileTempBufferedWriter.write(" | ");
	               processedFeatureFileTempBufferedWriter.newLine();
	
			   }
	    	}
	        processedFeatureFileTempBufferedWriter.newLine();
    	}     
    }
	

    // clean Examples table and replace them with [autodatagen] tag
    private static void PreProcessFile(String path) throws FileNotFoundException, IOException
    {
        // Read the file and display it line by line.
        String originalFeatureFilePath = path;
        boolean fileNeedsReplace = false;
        
        
        // make file backup to current execution
        String processedFeatureFileTempPath = path + ".autodatagen";

        File originalFeatureFile = new File(originalFeatureFilePath);
        File processedFeatureFileTemp = new File (processedFeatureFileTempPath);

        if(processedFeatureFileTemp.exists() && !processedFeatureFileTemp.isDirectory()) { 
        	processedFeatureFileTemp.delete();
        }
        
        FileInputStream originalFileInput = new FileInputStream(originalFeatureFile);
        BufferedReader originalFileInputBufferedReader = new BufferedReader(new InputStreamReader(originalFileInput));

        FileWriter processedFeatureFileTempWriter = new FileWriter(processedFeatureFileTempPath, true);
        BufferedWriter processedFeatureFileTempBufferedWriter = new BufferedWriter(processedFeatureFileTempWriter);
        
        String line = null;
        boolean newline_tag = false;
        boolean example_tag = false;
    
    	String currentScenarioTitle = "";
    	VariationScenario currentVariationScenario = new VariationScenario();
        while ((line = originalFileInputBufferedReader.readLine()) != null)
        {
            if (line.contains("Examples:") && newline_tag == false) {
                line = "\t[autodatagen]";
            	fileNeedsReplace = true;
            	example_tag = true;
                processedFeatureFileTempBufferedWriter.write(line);
                processedFeatureFileTempBufferedWriter.newLine();
                currentScenarioTitle = "";
            }
            else if (example_tag == true && newline_tag == false )
            {
        		if (line.trim().length() == 0)
        		{
        			newline_tag = true;
        		}
            }
            else if (example_tag == true && newline_tag == true)
            {
            	example_tag = false;
            	newline_tag = false;
                processedFeatureFileTempBufferedWriter.write(line);
                processedFeatureFileTempBufferedWriter.newLine();
            }
            else if (line.contains("Scenario Outline:"))
            {
            	int position = ("Scenario Outline: ").length() + 1;
            	currentScenarioTitle = line.substring(position).trim();
            	currentVariationScenario = new VariationScenario(currentScenarioTitle);
            	variationScenarios.add(currentVariationScenario);
            	processedFeatureFileTempBufferedWriter.write(line);
                processedFeatureFileTempBufferedWriter.newLine();
            }
            else if (line.contains("#web services:"))
            {
            	currentVariationScenario.ParseFeatureFileCommentLineIntoListOfScenarioWebMethods(line);
            	processedFeatureFileTempBufferedWriter.write(line);
                processedFeatureFileTempBufferedWriter.newLine();
            }
            else 
            {
            	processedFeatureFileTempBufferedWriter.write(line);
                processedFeatureFileTempBufferedWriter.newLine();
            }
        }
        
        originalFileInput.close();
        originalFileInputBufferedReader.close();
        processedFeatureFileTempWriter.flush();
        processedFeatureFileTempBufferedWriter.flush();
        processedFeatureFileTempWriter.close();
        processedFeatureFileTempBufferedWriter.close();
        

    	if (fileNeedsReplace)
    	{
	        try {
		        //repalce files (temp with feature)
		        Files.delete(originalFeatureFile.toPath());
		        Files.copy(processedFeatureFileTemp.toPath(), originalFeatureFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		        Files.delete(processedFeatureFileTemp.toPath());
		        
        	}
	        catch (Exception e)
	        {
	        	System.out.println("error pre-processing file!");
	        }   
    	}
    	else
    	{
	        Files.delete(processedFeatureFileTemp.toPath());
    	}
    	
    	return;
    }

    // Insert logic for processing found files here.
    private static void ProcessFile(String path, String jsonFile, String test_execution_id) throws FileNotFoundException, IOException
    {
        // Read the file and display it line by line.
        String originalFeatureFilePath = path;
        
        
        // make file backup to current execution
        String processedFeatureFileTempPath = path + ".temp";

        File originalFeatureFile = new File(originalFeatureFilePath);
        File processedFeatureFileTemp = new File (processedFeatureFileTempPath);

        if(processedFeatureFileTemp.exists() && !processedFeatureFileTemp.isDirectory()) { 
        	processedFeatureFileTemp.delete();
        }
        
        FileInputStream originalFileInput = new FileInputStream(originalFeatureFile);
        BufferedReader originalFileInputBufferedReader = new BufferedReader(new InputStreamReader(originalFileInput));

        FileWriter processedFeatureFileTempWriter = new FileWriter(processedFeatureFileTempPath, true);
        BufferedWriter processedFeatureFileTempBufferedWriter = new BufferedWriter(processedFeatureFileTempWriter);
        
    	System.out.println("");
        System.out.println(String.format("Processed file %1$s", originalFeatureFilePath));
        
        String scenarioTitle = "";

        String line = null;
        while ((line = originalFileInputBufferedReader.readLine()) != null)
        {
            if (!line.contains("[autodatagen]")) {
            	
                if (line.contains("Scenario Outline: ")) {
                    int position = ("Scenario Outline: ").length() + 1;
                    scenarioTitle = line.substring(position).trim();
                }
                processedFeatureFileTempBufferedWriter.write(line);
                processedFeatureFileTempBufferedWriter.newLine();
            }
            else
            {
            	
            	try 
            	{
	            	JSONParser parser = new JSONParser();
	            	JSONArray exampleTableArray = (JSONArray) parser.parse(new FileReader(jsonFile));
	            	ProcessExample(processedFeatureFileTempBufferedWriter, exampleTableArray, scenarioTitle);
            	}
            	catch (ParseException p)
            	{
            		System.out.println(p);
            	}
            	
            	scenarioTitle = "";
            }
        }
        
        originalFileInput.close();
        originalFileInputBufferedReader.close();
        processedFeatureFileTempWriter.flush();
        processedFeatureFileTempBufferedWriter.flush();
        processedFeatureFileTempWriter.close();
        processedFeatureFileTempBufferedWriter.close();
        

        try {
	        //repalce files (temp with feature)
	        Files.delete(originalFeatureFile.toPath());
	        Files.copy(processedFeatureFileTemp.toPath(), originalFeatureFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	        Files.delete(processedFeatureFileTemp.toPath());
	        
	        File test_execution_id_file = new File (String.format("test_executions/%1s/%2s", test_execution_id, originalFeatureFile.getName()));

	        // copy to test folder 
	        Files.copy(originalFeatureFile.toPath(), test_execution_id_file.toPath(),StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e)
        {
        	System.out.println("error processing file!" + e.toString());
        }   
        
        try 
        {
			Process p = new ProcessBuilder("ext/mvn.cmd", "test").start();
			try {
			    Thread.sleep(2000);                 //1000 for different timestamps
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
	        p.destroy();

        }
        catch (Exception e)
        {
        	System.out.println(e.toString());        	
        }
        
        return;
    }
    
    private static File[] GetFilesInFolderWithSpecificExtension(String folderPath, String extension){
    	File folder = new File(folderPath);

    	return folder.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(extension); }
    	} );
    }
        
    
    public static void main(String[] args) throws IOException, ParseException
    {
        try 
        {
	        String feature_files_location = "src/test/resources/iridium/demo/";
	        String test_json_file = "";
	        boolean folderCreated = false;
	        String currentFileName = ""; 	
	        
            String test_execution_id = getTestExecutionId();
            File[] featureFiles = GetFilesInFolderWithSpecificExtension(feature_files_location, ".feature");
	        folderCreated = new File(String.format("test_executions/%1s/soap_requests", test_execution_id)).mkdirs();
			if (folderCreated)
			{
				System.out.println(String.format("test execution folder %1s created", test_execution_id));
				System.out.println("");
			}
			else
			{
				System.out.println("failed to created execution folder");
				return;
			}
	        
        	File currentTestExecutionId = new File("test_executions/current_execution.txt");
        	if(!currentTestExecutionId.exists()) {
        		currentTestExecutionId.createNewFile();
        	} 
        	
        	FileWriter currentTestExecutionIdWritter = new FileWriter(currentTestExecutionId, false); // false to overwrite.
        	currentTestExecutionIdWritter.write(test_execution_id);
        	currentTestExecutionIdWritter.close();

	    	
	        for (int i = 0; i < featureFiles.length; i++)
	        {
	        	
	        	
	        	// get list of scenario titles
	        	PreProcessFile(featureFiles[i].getPath());
	
	        	currentFileName = featureFiles[i].getName();
	        	System.out.println(String.format("processing feature file \"%1s\"", currentFileName));
	        	
	        	
	        	/***************************************************************************************************/
	        	// TODO: We can't hard code the service name here.  All the data generation is hard coded as well  //
	        	//       the next step would be to create a generic data generation engine using the same logic:   //
	        	//		Input (DB/File/Algorithm) -> JSON Generation -> Feature file modification 	        	   //
	        	/***************************************************************************************************/
	        	
	        	// loop for each scenario
	        	for (int j = 0; j < variationScenarios.size(); j++)
	        	{
		        	/***************************************************************************************************/
		        	// TODO: We need to aggregate the creation of all scenario outline variations into a single JSON   //
		        	//       per feature file.  This might be hard but it's required.  Maybe this can be achieved by   //
		        	//		 simply appending to the created JSON in the previous loop instance so it's not so hard    //
		        	/***************************************************************************************************/
	        		
	        			        		
//	        		test_json_file = GenerateTestCaseVariations.GenerateSequencedTestVariationsForScenario(currentFileName, 
//	        				variationScenarios.get(j), test_execution_id, 5);
	        		
	        		test_json_file = GenerateTestCaseVariations.GenerateRandomTestVariationsForScenario(currentFileName, 
	        				variationScenarios.get(j), test_execution_id, 5);

//		        	test_json_file = GenerateTestCaseVariations.GenerateRandomTestVariations(currentFileName, "findServiceProviderProfile test scenario", 
//        			"findServiceProviderProfile", test_execution_id, 7);

	        	}
	        	
	        	if (test_json_file == "")
	        	{
	        		System.out.println("error generating test data exiting ...");
	        		return;
	        	}
	        	
	            ProcessFile(featureFiles[i].getPath(), test_json_file, test_execution_id);
	        }
        	System.out.println("");
	        System.out.println(String.format("Processed all feature files. Ready to execute tests!"));

        }
        catch (Exception e){
    		System.out.println(e.toString());
        }
        
        return;
        
    }
}
