package edu.asu.spring.quadriga.web.workspace;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.asu.spring.quadriga.aspects.annotations.AccessPolicies;
import edu.asu.spring.quadriga.aspects.annotations.CheckedElementType;
import edu.asu.spring.quadriga.aspects.annotations.ElementAccessPolicy;
import edu.asu.spring.quadriga.domain.factory.impl.workspace.WorkspaceFormFactory;
import edu.asu.spring.quadriga.exceptions.QuadrigaAccessException;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.workspace.IArchiveWSManager;
import edu.asu.spring.quadriga.service.workspace.IListWSManager;
import edu.asu.spring.quadriga.validator.WorkspaceFormValidator;
import edu.asu.spring.quadriga.web.login.RoleNames;
import edu.asu.spring.quadriga.web.workspace.backing.ModifyWorkspace;
import edu.asu.spring.quadriga.web.workspace.backing.ModifyWorkspaceForm;
import edu.asu.spring.quadriga.web.workspace.backing.ModifyWorkspaceFormManager;

@Controller
public class ActivateWSController {
    @Autowired
    IArchiveWSManager archiveWSManager;
    
    @Autowired
    private IListWSManager listWsManager;

    @Autowired
    ModifyWorkspaceFormManager workspaceFormManager;

    @Autowired
    WorkspaceFormFactory workspaceFormFactory;

    @Autowired
    WorkspaceFormValidator validator;

    /**
     * Attach the custom validator to the Spring context
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {

        binder.setValidator(validator);
    }

    /**
     * This calls workspaceManger to deactivate the workspace passed using workspace id
     * 
     * @param principal User for workspace
     * @param projectid Identifier for project
     * @param workspaceid Identifier for workspace
     * @return Model and view object.
     * @throws QuadrigaStorageException and QuadrigaAccessException
     */
	@AccessPolicies({
			@ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 1, userRole = {
					RoleNames.ROLE_COLLABORATOR_ADMIN,
					RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN,
					RoleNames.ROLE_PROJ_COLLABORATOR_CONTRIBUTOR }),
			@ElementAccessPolicy(type = CheckedElementType.WORKSPACE, paramIndex = 0, userRole = {}) })
	@RequestMapping(value = "auth/workbench/{workspaceid}/deactivateworkspace", method = RequestMethod.GET)
	public ModelAndView deactivateSingleWorkspaceForm(
			@RequestParam("projectid") String projectid,@PathVariable("workspaceid") String workspaceid, Principal principal)
			throws QuadrigaStorageException, QuadrigaAccessException {

		ModelAndView model = new ModelAndView(
				"auth/workbench/workspace/deactivateworkspace");
		model.getModelMap().put("wsprojectid", projectid);
		model.getModelMap().put("success", 2);
		return model;
	}
	
	@AccessPolicies({
			@ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 1, userRole = {
					RoleNames.ROLE_COLLABORATOR_ADMIN,
					RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN,
					RoleNames.ROLE_PROJ_COLLABORATOR_CONTRIBUTOR }),
			@ElementAccessPolicy(type = CheckedElementType.WORKSPACE, paramIndex = 0, userRole = {}) })
	@RequestMapping(value = "auth/workbench/{workspaceid}/deactivateworkspace", method = RequestMethod.POST)
	public ModelAndView deactivateWorkspaceForm(
			@RequestParam("projectid") String projectid,
			@PathVariable("workspaceid") String workspaceid, Principal principal)
			throws QuadrigaStorageException, QuadrigaAccessException {

		ModelAndView model = new ModelAndView(
				"auth/workbench/workspace/deactivateworkspace");
		StringBuilder workspaceIdList = new StringBuilder();

		workspaceIdList.append(",");
		workspaceIdList.append(workspaceid);

		archiveWSManager.deactivateWorkspace(workspaceIdList.toString()
				.substring(1), principal.getName());

		model.getModelMap().put("wsprojectid", projectid);
		model.getModelMap().put("success", 1);
		return model;
	}

    /**
     * This calls workspaceManger to list the workspace associated with a
     * project to activate.
     * 
     * @param model
     * @param projectid
     * @return String - URL of the form
     * @throws QuadrigaStorageException
     * @author Kiran Kumar Batna
     */
    @AccessPolicies({
            @ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 1, userRole = {
                    RoleNames.ROLE_COLLABORATOR_ADMIN,
                    RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN,
                    RoleNames.ROLE_PROJ_COLLABORATOR_CONTRIBUTOR }),
            @ElementAccessPolicy(type = CheckedElementType.WORKSPACE, paramIndex = 0, userRole = {}) })
    @RequestMapping(value = "auth/workbench/{projectid}/activateworkspace", method = RequestMethod.GET)
    public ModelAndView activateWorkspaceForm(
            @PathVariable("projectid") String projectid, Principal principal)
            throws QuadrigaStorageException, QuadrigaAccessException {
        ModelAndView model;
        String userName;
        ModifyWorkspaceForm workspaceForm;
        List<ModifyWorkspace> activateWSList;

        userName = principal.getName();
        model = new ModelAndView("auth/workbench/workspace/activateworkspace");

        // retrieve the workspaces associated with the projects
        workspaceForm = workspaceFormFactory.createModifyWorkspaceForm();

        activateWSList = workspaceFormManager.getDeactivatedWorkspaceList(
                projectid, userName);
        workspaceForm.setWorkspaceList(activateWSList);

        model.getModelMap().put("workspaceform", workspaceForm);
        model.getModelMap().put("wsprojectid", projectid);
        model.getModelMap().put("success", 0);
        return model;
    }

    /**
     * This calls workspaceManager to activate the deactivated workspace.
     * 
     * @param projectid
     * @param req
     * @param model
     * @param principal
     * @return String - URL of the form
     * @throws QuadrigaStorageException
     * @author Kiran Kumar Batna
     * @throws QuadrigaAccessException
     */
    @AccessPolicies({
            @ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 3, userRole = {
                    RoleNames.ROLE_COLLABORATOR_ADMIN,
                    RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN,
                    RoleNames.ROLE_PROJ_COLLABORATOR_CONTRIBUTOR }),
            @ElementAccessPolicy(type = CheckedElementType.WORKSPACE, paramIndex = 0, userRole = {}) })
    @RequestMapping(value = "auth/workbench/{projectid}/activateworkspace", method = RequestMethod.POST)
    public ModelAndView activateWorkspace(
            @Validated @ModelAttribute("workspaceform") ModifyWorkspaceForm workspaceForm,
            BindingResult result, @PathVariable("projectid") String projectid,
            Principal principal) throws QuadrigaStorageException,
            QuadrigaAccessException {
        StringBuilder workspaceId;
        String userName;
        String wsInternalId;
        ModelAndView model;
        List<ModifyWorkspace> activateWSList;

        // fetch the user name
        userName = principal.getName();

        activateWSList = new ArrayList<ModifyWorkspace>();
        workspaceId = new StringBuilder();
        model = new ModelAndView("auth/workbench/workspace/activateworkspace");

        if (result.hasErrors()) {
            activateWSList = workspaceFormManager.getDeactivatedWorkspaceList(
                    projectid, userName);
            workspaceForm.setWorkspaceList(activateWSList);
            // frame the model objects
            model.getModelMap().put("success", 0);
            model.getModelMap().put("error", 1);
            model.getModelMap().put("workspaceform", workspaceForm);
            model.getModelMap().put("wsprojectid", projectid);
        } else {
            activateWSList = workspaceForm.getWorkspaceList();

            // loop through the selected workspace
            for (ModifyWorkspace workspace : activateWSList) {
                wsInternalId = workspace.getId();

                if (wsInternalId != null) {
                    workspaceId.append(",");
                    workspaceId.append(wsInternalId);
                }
            }

            archiveWSManager.activateWorkspace(workspaceId.toString()
                    .substring(1), userName);
            // frame the model objects
            model.getModelMap().put("success", 1);
            model.getModelMap().put("wsprojectid", projectid);
        }
        return model;

    }

    @RequestMapping(value = "auth/workbench/{projectid}/showinactiveworkspace", method = RequestMethod.GET)
    public String showInactiveWorkspaces(
            @PathVariable("projectid") String projectId, Principal principal,
            Model model) throws QuadrigaStorageException {
        List<ModifyWorkspace> deactivatedWSList = workspaceFormManager
                .getDeactivatedWorkspaceList(projectId, principal.getName());
        model.addAttribute("deactivatedWSList", deactivatedWSList);
        model.addAttribute("projectid", projectId);

        return "auth/workbench/workspace/showInactiveWorkspace";
    }

}
