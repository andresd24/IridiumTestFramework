package tools.datagen;

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
import java.io.FileNotFoundException;
import java.io.FileReader;

public class PopulateFeatureExamples {
	
		
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

    // Insert logic for processing found files here.
    private static void ProcessFile(String path) throws FileNotFoundException, IOException
    {
        // Read the file and display it line by line.
        String originalFeatureFilePath = path;
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
        
        String featureFileName = originalFeatureFile.getName();
        		
        System.out.println(featureFileName); 		
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
	            	JSONArray exampleTableArray = (JSONArray) parser.parse(new FileReader(String.format("features_json/%1$s.json", featureFileName)));
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
        }
        catch (Exception e)
        {
        	
        }   
        
        try 
        {
			Process p = new ProcessBuilder("ext/mvn.cmd", "test").start();
			p.waitFor();
        }
        catch (Exception e)
        {
        	System.out.println(e.toString());        	
        }
    }
    
    public static void ProcessDirectory(final File folder) throws IOException, ParseException {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                	ProcessDirectory(fileEntry);
                } else {
                    System.out.println(String.format("Processing feature file %1s", fileEntry.getName()));
                	ProcessFile(fileEntry.getName());
           }
       }
    }

    private static File[] GetFilesInFolderWithSpecificExtension(String folderPath, String extension){
    	File folder = new File(folderPath);

    	return folder.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(extension); }
    	} );
    }
        
        
    public static void PopulateExampleTags() throws IOException, ParseException
    {
        String feature_files_location = "src/test/resources/iridium/demo/";

        File[] featureFiles = GetFilesInFolderWithSpecificExtension(feature_files_location, ".feature");

        for (int i = 0; i < featureFiles.length; i++)
        {
            ProcessFile(featureFiles[i].getPath());
        }
    }

    
    public static void main(String[] args) throws IOException, ParseException
    {
        String feature_files_location = "src/test/resources/iridium/demo/";

        File[] featureFiles = GetFilesInFolderWithSpecificExtension(feature_files_location, ".feature");

        for (int i = 0; i < featureFiles.length; i++)
        {
            ProcessFile(featureFiles[i].getPath());
        }
    }
}
