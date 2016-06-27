package edu.asu.spring.quadriga.domain.impl.workspace;

import edu.asu.spring.quadriga.domain.workspace.ITransformationFile;

public class TransformationFile implements ITransformationFile{

    private String title;
    private String userName;
    private String description;
    private String patternTitle;
    private String patternDescription;
    private String patternFileName;
    private String mappingTitle;
    private String mappingDescription;
    private String mappingFileName;
    private String patternFileContent;
    private String mappingFileContent;
    
    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public void setTitle(String titleName) {
        
        this.title = titleName;
    }
    @Override
    public String getUserName() {
        
        return userName;
    }
    @Override
    public void setUserName(String userName) {
        
        this.userName = userName;
        
    }
    @Override
    public String getDescription() {
        
        return description;
    }
    @Override
    public void setDescription(String description) {
        
        this.description = description;
    }
    @Override
    public String getPatternTitle() {
        
        return patternTitle;
    }
    @Override
    public void setPatternTitle(String patternTitle) {
        
        this.patternTitle = patternTitle;
    }
    @Override
    public String getPatternDescription() {
        
        return patternDescription;
    }
    @Override
    public void setPatternDescription(String patternDescription) {
        
        this.patternDescription = patternDescription;
        
    }
    @Override
    public String getPatternFileName() {
        
        return patternFileName;
    }
    @Override
    public void setPatternFileName(String patternFileName) {
        
        this.patternFileName = patternFileName;
    }
    @Override
    public String getMappingTitle() {
        
        return mappingTitle;
    }
    @Override
    public void setMappingTitle(String MappingTitle) {
        
        this.mappingTitle = MappingTitle;
    }
    @Override
    public String getMappingDescription() {
        
        return mappingDescription;
    }
    @Override
    public void setMappingDescription(String MappingDescription) {
        
        this.mappingDescription = MappingDescription;
    }
    @Override
    public String getMappingFileName() {
        
        return mappingFileName;
    }
    
    @Override
    public void setMappingFileName(String MappingFileName) {
        
        this.mappingFileName = MappingFileName;
    }
    
    @Override
    public String getMappingFileContent() {
        return mappingFileContent;
    }
    
    @Override
    public void setMappingFileContent(String mappingFileContent) {
        this.mappingFileContent = mappingFileContent;
    }
    
    @Override
    public String getPatternFileContent() {
        return patternFileContent;
    }
    
    @Override
    public void setPatternFileContent(String patternFileContent) {
        this.patternFileContent = patternFileContent;
    }
   
}
