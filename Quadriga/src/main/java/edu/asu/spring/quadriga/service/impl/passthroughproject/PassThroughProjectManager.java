package edu.asu.spring.quadriga.service.impl.passthroughproject;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import edu.asu.spring.quadriga.accesschecks.IProjectSecurityChecker;
import edu.asu.spring.quadriga.dao.workbench.passthroughproject.IPassThroughProjectDAO;
import edu.asu.spring.quadriga.dao.workspace.IWorkspaceDAO;
import edu.asu.spring.quadriga.domain.IUser;
import edu.asu.spring.quadriga.domain.enums.EProjectAccessibility;
import edu.asu.spring.quadriga.dto.PassThroughProjectDTO;
import edu.asu.spring.quadriga.dto.QuadrigaUserDTO;
import edu.asu.spring.quadriga.exceptions.QuadrigaAccessException;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.IUserManager;
import edu.asu.spring.quadriga.service.impl.BaseManager;
import edu.asu.spring.quadriga.service.network.INetworkManager;
import edu.asu.spring.quadriga.service.passthroughproject.IPassThroughProjectDocumentReader;
import edu.asu.spring.quadriga.service.passthroughproject.IPassThroughProjectManager;
import edu.asu.spring.quadriga.service.workspace.mapper.IExternalWSManager;
import edu.asu.spring.quadriga.web.login.RoleNames;

@Service
public class PassThroughProjectManager extends BaseManager implements IPassThroughProjectManager {

    @Autowired
    private IWorkspaceDAO workspaceDao;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private INetworkManager networkManager;

    @Autowired
    private IExternalWSManager externalWSManager;

    @Autowired
    private IPassThroughProjectDAO projectDao;

    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IProjectSecurityChecker projectSecurityChecker;
    
    
    @Resource(name = "projectconstants")
    private Properties messages;
    
    @Autowired
    private IPassThroughProjectDocumentReader passThroughProjectDocumentReader;

    @Override
    @Transactional
    public String addPassThroughProject(Principal principal, String projectName, String description,
            String externalProjectid, String externalUserId, String externalUserName, String client)
                    throws QuadrigaStorageException {

        String projectId = messages.getProperty("project_internalid.name") + generateUniqueID();

        IUser user = userManager.getUser(principal.getName());

        Query query = sessionFactory.getCurrentSession().getNamedQuery("QuadrigaUserDTO.findByUsername");
        query.setParameter("username", principal.getName());

        List<QuadrigaUserDTO> quadrigaUsers = query.list();

        PassThroughProjectDTO projectDTO = new PassThroughProjectDTO();
        // internal project details
        projectDTO.setProjectid(projectId);
        projectDTO.setProjectname(projectName);
        projectDTO.setDescription(description);
        // Since we are not passing unix name in REST request, we are assigning
        // the unix name as projet name
        projectDTO.setUnixname(projectName);
        projectDTO.setProjectid(projectId);
        projectDTO.setProjectowner(quadrigaUsers.get(0));
        projectDTO.setCreatedby(user.getUserName());
        projectDTO.setCreateddate(new Date());
        projectDTO.setUpdatedby(user.getUserName());
        projectDTO.setUpdateddate(new Date());
        projectDTO.setAccessibility(EProjectAccessibility.PUBLIC.name());

        // external project details
        projectDTO.setExternalProjectid(externalProjectid);
        projectDTO.setExternalUserId(externalUserId);
        projectDTO.setExternalUserName(externalUserName);
        projectDTO.setClient(client);

        projectDao.saveNewDTO(projectDTO);

        return projectId;

    }

    @Override
    @Transactional
    public String getInternalProjectId(String externalProjectid,
            Principal principal) throws QuadrigaStorageException {

        Query query = sessionFactory.getCurrentSession().getNamedQuery(
                "PassThroughProjectDTO.findByExternalProjectid");
        query.setParameter("externalProjectid", externalProjectid);

        List<PassThroughProjectDTO> projectDTOs = query.list();

        for (PassThroughProjectDTO projectDTO : projectDTOs) {
            boolean isOwner = projectSecurityChecker.isProjectOwner(
                    principal.getName(), projectDTO.getProjectid());
            if (isOwner) {
                return projectDTO.getProjectid();
            }
            boolean isCollaborator = projectSecurityChecker.isCollaborator(
                    principal.getName(),
                    RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN,
                    projectDTO.getProjectid());
            if (isCollaborator) {
                return projectDTO.getProjectid();
            }
        }
        return null;
    }

    @Override
    public void getPassThroughProjectDTO() {
        // TODO Auto-generated method stub

    }

    @Override
    public String callQStore(String workspaceId, String xml, IUser user) throws ParserConfigurationException,
            SAXException, IOException, JAXBException, QuadrigaStorageException, QuadrigaAccessException {
    	String annotatedText = passThroughProjectDocumentReader.getAnnotateData(xml);
        String networkName = "VogenWeb_Details";
        String responseFromQStore = networkManager.storeXMLQStore(annotatedText);
        String networkId = networkManager.storeNetworkDetails(responseFromQStore, user, networkName, workspaceId,
                INetworkManager.NEWNETWORK, "", INetworkManager.VERSION_ZERO);
        return networkId;
    }

    @Override
    @Transactional
    public String createWorkspaceForExternalProject(String externalWorkspaceId, String externalWorkspaceName,
            String projectId, IUser user) throws JAXBException, QuadrigaStorageException, QuadrigaAccessException {

        boolean isExternalWorkspaceExists = externalWSManager.isExternalWorkspaceExists(externalWorkspaceId);
        String workspaceId = null;
        if (!isExternalWorkspaceExists) {
            // External workspace does not exists so insert the values into
            // externalWorkspace table
            // Create a new externalWorkspaceId and InternalWorkspaceId and then
            // call storeNetworkDetails
            workspaceId = workspaceDao.generateUniqueID();
            externalWSManager.createExternalWorkspace(externalWorkspaceId, externalWorkspaceName, workspaceId,
                    projectId, user);
        } else {
            // Get the workspace Id related to the external workspace Id
            workspaceId = externalWSManager.getInternalWorkspaceId(externalWorkspaceId);
        }

        // After creating the values save the values by calling store network
        // details. If already available. Call network details for updation

        return workspaceId;

    }

}
