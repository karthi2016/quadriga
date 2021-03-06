package edu.asu.spring.quadriga.mapper.workspace;

import java.util.List;

import edu.asu.spring.quadriga.domain.proxy.WorkSpaceProxy;
import edu.asu.spring.quadriga.domain.workbench.IProject;
import edu.asu.spring.quadriga.domain.workbench.impl.Project;
import edu.asu.spring.quadriga.domain.workspace.IWorkspace;
import edu.asu.spring.quadriga.domain.workspace.impl.Workspace;
import edu.asu.spring.quadriga.dto.ProjectWorkspaceDTO;
import edu.asu.spring.quadriga.dto.WorkspaceDTO;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;

/**
 * This interface has methods to implement the mapping of DTO object to Domain
 * objects for the service layer for Workspace. These methods need to map
 * {@link WorkspaceDTO} to {@link WorkSpaceProxy} object.
 * 
 * @author Lohith Dwaraka, Julia Damerow
 *
 */
public interface IWorkspaceShallowMapper extends IBaseWorkspaceMapper {

    /**
     * This class should get a {@link IWorkspace} of domain class type
     * {@link WorkSpaceProxy} for a {@link IWorkspace} ID.
     * 
     * @param workspaceDTO
     *            {@link WorkspaceDTO} object
     * @return Returns {@link IWorkspace} object of domain class typ
     *         {@link WorkSpaceProxy}
     * @throws QuadrigaStorageException
     *             Throws the storage exception when the method has issues to
     *             access the database
     */
    IWorkspace mapWorkspaceDTO(WorkspaceDTO workspaceDTO) throws QuadrigaStorageException;

    /**
     * Given a list of {@link ProjectWorkspaceDTO}s, this method create a list of {@link IProjectWorkspace}s
     * that connect {@link Project}s with {@link Workspace}s. The {@link Workspace}s that are
     * referenced in the {@link IProjectWorkspace}s are {@link WorkSpaceProxy}s.
     * 
     * @param project The project that the {@link IProjectWorkspace}s link to.
     * @param projectWorkspaceDTOList the list of {@link ProjectWorkspaceDTO}s that should be turned
     * into {@link IProjectWorkspace}s.
     * @return
     * @throws QuadrigaStorageException
     */
    List<IWorkspace> getProjectWorkspaceList(IProject project, List<ProjectWorkspaceDTO> projectWorkspaceDTOList)
            throws QuadrigaStorageException;

}