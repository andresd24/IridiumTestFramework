package tools.datagen;

import java.util.ArrayList;
import tools.datagen.VariationPair;

public class VariationScenario {

    private String scenarioTitle = "";
    private ArrayList<VariationPair> testVariations = new ArrayList<VariationPair>();
    private int testVariationCount = 0;

    
    public VariationScenario(String scenarioTitle, ArrayList<VariationPair> testVariations)
    {
        this.scenarioTitle = scenarioTitle;
        this.testVariations = testVariations;
    }

    public VariationScenario(String scenarioTitle) { 
        this.scenarioTitle = scenarioTitle;
    }

    public void setScenarioTitle(String scenarioTitle)
    {
    	this.scenarioTitle = scenarioTitle;
    }

    public void addTestVariationToScenario(ArrayList<VariationPair> testVariation)
    {
    	this.testVariations.addAll(testVariationCount, testVariation);
    	testVariationCount++;
    }
    
    public VariationScenario() { }
    
    public ArrayList<VariationPair> getVariationPairs()
    {
    	return testVariations;      	
    }
    
    public ArrayList<String> getColumnNameTitles()
    {
    	ArrayList<String> columnNameTitles = new ArrayList<String>();
    	
    	for (int i = 0 ; i < testVariations.size(); i++)
    	{
    		String currentTemplateString = testVariations.get(i).getTemplateString(); 
    		String columnNameTitle = currentTemplateString.replaceAll("\\{", "");
    		columnNameTitle = columnNameTitle.replaceAll("\\}", "");
    		
    		columnNameTitles.add(columnNameTitle);
    	}
    	return columnNameTitles;
    }

    public ArrayList<String> getCellValues()
    {
    	ArrayList<String> cellValues = new ArrayList<String>();
    	
    	for (int i = 0 ; i < testVariations.size(); i++)
    	{
    		String currentCellValue = testVariations.get(i).getTestData(); 
    		
    		cellValues.add(currentCellValue);
    	}
    	return cellValues;
    }


    public String getScenarioTitle()
    {
    	return scenarioTitle;
    }

    
}
