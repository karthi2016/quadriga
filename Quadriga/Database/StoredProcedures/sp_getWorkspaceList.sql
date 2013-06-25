/*******************************************
Name          : sp_getWorkspaceList

Description   : Retrieves the workspace details.

Called By     : UI(DBConnectionProjectManager.java)

Create By     : Kiran Kumar Batna

Modified Date : 06/24/2013

********************************************/
DROP PROCEDURE IF EXISTS sp_getWorkspaceList;

DELIMITER $$
CREATE PROCEDURE sp_getWorkspaceList
(
   IN inprojectid   BIGINT,
   OUT errmsg       VARCHAR(255)
)
BEGIN
	  DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
       SET errmsg = "SQL exception has occurred";

	  -- valdiate the input parameters
      IF (inprojectid IS NULL)
       THEN SET errmsg = "Project id cannot be empty.";
      END IF;

      IF NOT EXISTS(SELECT 1 FROM vw_project
					  WHERE projectid = inprojectid)
        THEN SET errmsg = "Project id is invalid.";
      END IF;

      IF (errmsg IS NULL)
        THEN SET errmsg = "";
          SELECT vsws.workspacename,
                 vsws.description,
                 vsws.workspaceid,
                 vsws.workspaceowner
            FROM vw_workspace vsws
            JOIN vw_project_workspace vwprojws
              ON vsws.workspaceid = vwprojws.workspaceid
            WHERE vwprojws.projectid = inprojectid;
      END IF;
END $$
DELIMITER ;