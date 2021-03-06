package edu.asu.spring.quadriga.aspects;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.spring.quadriga.accesschecks.IWSSecurityChecker;
import edu.asu.spring.quadriga.domain.IQuadrigaRole;
import edu.asu.spring.quadriga.domain.workspace.IWorkspace;
import edu.asu.spring.quadriga.domain.workspace.IWorkspaceCollaborator;
import edu.asu.spring.quadriga.exceptions.IllegalObjectException;
import edu.asu.spring.quadriga.exceptions.QuadrigaAccessException;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.workspace.IWorkspaceManager;

/**
 * Service level Implementation of {@link IAuthorization} for {@link IWorkspace}
 * for REST APIs. This class specifically works on authorization check of user
 * for {@link IWorkspace} access.
 * 
 * @author Kiran kumar
 *
 */
@Service("workspaceRestAuthorization")
public class WorkspaceRestAuthorization implements IAuthorization {
    @Autowired
    private IWorkspaceManager wsManager;

    @Autowired
    private IWSSecurityChecker wsSecurityManager;

    @Override
    public boolean chkAuthorization(String userName, Object workspaceObj, String[] userRoles)
            throws QuadrigaStorageException, QuadrigaAccessException {

        IWorkspace workspace;

        if (workspaceObj instanceof String) {
            String workspaceId = (String) workspaceObj;
            workspace = wsManager.getWorkspaceDetails(workspaceId, userName);
        } else {
            try {
                workspace = (IWorkspace) workspaceObj;
            } catch (ClassCastException cce) {
                throw new IllegalObjectException(cce);
            }
        }

        if (workspace == null) {
            throw new QuadrigaAccessException();
        }
        // check if the logged in user is workspace owner
        if (workspace.getOwner() != null) {

            if (userName.equals(workspace.getOwner().getUserName())) {
                return true;
            } else {
                if (userRoles.length > 0) {
                    List<String> roles = Arrays.asList(userRoles);
                    List<IWorkspaceCollaborator> workspaceCollaboratorList = workspace.getWorkspaceCollaborators();
                    List<IQuadrigaRole> collaboratorRoles = null;
                    if (workspaceCollaboratorList != null) {
                        for (IWorkspaceCollaborator workspaceCollaborator : workspaceCollaboratorList) {
                            // check if he is a collaborator to the project
                            String collaboratorName = null;
                            if (workspaceCollaborator.getCollaborator() != null) {
                                collaboratorName = workspaceCollaborator.getCollaborator().getUserObj().getUserName();
                            }
                            if (collaboratorName != null) {
                                if (userName.equals(collaboratorName)) {
                                    if (workspaceCollaborator.getCollaborator() != null) {
                                        collaboratorRoles = workspaceCollaborator.getCollaborator()
                                                .getCollaboratorRoles();
                                    }
                                    if (collaboratorRoles != null) {
                                        for (IQuadrigaRole collabRole : collaboratorRoles) {
                                            String collaboratorRoleId = collabRole.getId();
                                            if (roles.contains(collaboratorRoleId)) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean chkAuthorizationByRole(String userName, String[] userRoles)
            throws QuadrigaStorageException, QuadrigaAccessException {

        // fetch the details of the project
        if (wsSecurityManager.checkIsWorkspaceAssociated(userName)) {
            return true;
        } else { // check the user roles if he is not a project owner
            if (userRoles.length > 0) {
                List<String> roles = Arrays.asList(userRoles);
                // check if the user associated with the role has any projects
                for (String role : roles) {
                    if (wsSecurityManager.chkIsCollaboratorWorkspaceAssociated(userName, role)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    @Override
    public boolean chkAuthorization(String userName, List<String> accessObjectId, String[] userRoles)
            throws QuadrigaStorageException, QuadrigaAccessException {
        // TODO Auto-generated method stub
        return false;
    }
}
