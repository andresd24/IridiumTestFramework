package tools.datagen;

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
