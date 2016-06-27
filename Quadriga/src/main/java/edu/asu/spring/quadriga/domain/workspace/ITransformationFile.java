package edu.asu.spring.quadriga.domain.workspace;

import edu.asu.spring.quadriga.domain.enums.ETextAccessibility;

public interface ITransformationFile {

    
    public abstract String getTitle();
    public abstract void setTitle(String titleName);
    
    public abstract String getUserName();
    public abstract void setUserName(String userName);
    
    public abstract String getDescription();
    public abstract void setDescription(String description);
    
    public abstract String getPatternTitle();
    public abstract void setPatternTitle(String patternTitle);
    
    public abstract String getPatternDescription();
    public abstract void setPatternDescription(String patternDescription);
    
    public abstract String getPatternFileName();
    public abstract void setPatternFileName(String patternFileName);
    
    public abstract String getMappingTitle();
    public abstract void setMappingTitle(String MappingTitle);
    
    public abstract String getMappingDescription();
    public abstract void setMappingDescription(String MappingDescription);
    
    public abstract String getMappingFileName();
    public abstract void setMappingFileName(String MappingFileName);
    
    public abstract String getMappingFileContent();
    public abstract void setMappingFileContent(String MappingFileName);
    
    public abstract String getPatternFileContent();
    public abstract void setPatternFileContent(String MappingFileName);
    

}

