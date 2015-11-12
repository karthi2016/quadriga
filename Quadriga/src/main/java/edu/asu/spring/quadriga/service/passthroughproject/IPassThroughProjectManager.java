package edu.asu.spring.quadriga.service.passthroughproject;

import java.io.IOException;
import java.security.Principal;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.asu.spring.quadriga.domain.IUser;
import edu.asu.spring.quadriga.exceptions.QuadrigaAccessException;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;

public interface IPassThroughProjectManager {

    String createWorkspaceForExternalProject(String externalWorkspaceId,String externalWorkspaceName, String projectId, IUser user)
            throws JAXBException, QuadrigaStorageException, QuadrigaAccessException;

    String addPassThroughProject(Principal principal, String projectName, String description, String externalProjectid,
            String externalUserId, String externalUserName, String client) throws QuadrigaStorageException;

    void getPassThroughProjectDTO();
    
    String getInternalProjectId(String externalProjectid, Principal principal) throws QuadrigaStorageException;

    String callQStore(String workspaceId, String xml, IUser user) throws ParserConfigurationException,
            SAXException, IOException, JAXBException, QuadrigaStorageException, QuadrigaAccessException;
    
}
