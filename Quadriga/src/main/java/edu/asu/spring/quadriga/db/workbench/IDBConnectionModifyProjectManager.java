package edu.asu.spring.quadriga.db.workbench;

import javax.sql.DataSource;

import edu.asu.spring.quadriga.domain.IProject;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;

public interface IDBConnectionModifyProjectManager {

	public abstract String deleteProjectRequest(String projectIdList)
			throws QuadrigaStorageException;

	public abstract String updateProjectRequest(IProject project, String userName)
			throws QuadrigaStorageException;

	public abstract String addProjectRequest(IProject project)
			throws QuadrigaStorageException;

	public abstract void setDataSource(DataSource dataSource);

	public abstract void setupTestEnvironment(String sQuery)
			throws QuadrigaStorageException;

}
