package tools.datagen;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class VariationScenario {

    private String scenarioTitle = "";
    private List<String> webMethods = new ArrayList<String>();
    
    public void ParseFeatureFileCommentLineIntoListOfScenarioWebMethods(String rawCommentLine)
    {
    	int position = rawCommentLine.indexOf("#web services:") +  ("#web services:").length() + 1;
    	String m_rawCommentLine = rawCommentLine.substring(position).trim();
    	this.webMethods  = Arrays.asList(m_rawCommentLine.split("\\s*,\\s*"));
    }
    
    public VariationScenario(String scenarioTitle, String featureFileCommentLine)
    {
        this.scenarioTitle = scenarioTitle;
    }

    public VariationScenario(String scenarioTitle) { 
        this.scenarioTitle = scenarioTitle;
    }

    public void setScenarioTitle(String scenarioTitle)
    {
    	this.scenarioTitle = scenarioTitle;
    }

    public List<String> getListOfWebMethodsInScenario()
    {
    	return this.webMethods;
    }
    
    public VariationScenario() { }
    

    public String getScenarioTitle()
    {
    	return scenarioTitle;
    }

    
}
