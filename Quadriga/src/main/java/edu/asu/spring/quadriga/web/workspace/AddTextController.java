package edu.asu.spring.quadriga.web.workspace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.asu.spring.quadriga.aspects.annotations.AccessPolicies;
import edu.asu.spring.quadriga.aspects.annotations.CheckedElementType;
import edu.asu.spring.quadriga.aspects.annotations.ElementAccessPolicy;
import edu.asu.spring.quadriga.domain.factory.workspace.ITextFileFactory;
import edu.asu.spring.quadriga.domain.impl.workspace.TextFile;
import edu.asu.spring.quadriga.exceptions.FileStorageException;
import edu.asu.spring.quadriga.exceptions.QuadrigaAccessException;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.textfile.ITextFileManager;
import edu.asu.spring.quadriga.validator.AddTextValidator;
import edu.asu.spring.quadriga.web.login.RoleNames;

@Controller
public class AddTextController {

    @Autowired
    private ITextFileFactory textFileFactory;

    @Autowired
    private ITextFileManager tfManager;

    @Autowired
    private AddTextValidator txtValidator;

    /**
     * @param binder
     *            Generic binder object to bind the validator to the domain
     *            object.
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(txtValidator);
    }

    private List<String> accList = new ArrayList<>(Arrays.asList("Select", "Private", "Public"));
    /**
     * Method to generate a view to generate a view to add new text file.
     * 
     * @param workspaceid
     *            Path variable to retrieve the active workspace id
     * @param projid
     *            Path variable to retrieve the active project id
     * @return Returns the text file addition module
     * @throws QuadrigaStorageException
     * @throws QuadrigaAccessException
     */
    @AccessPolicies({
            @ElementAccessPolicy(type = CheckedElementType.WORKSPACE, paramIndex = 1, userRole = {
                    RoleNames.ROLE_WORKSPACE_COLLABORATOR_ADMIN }),
            @ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 2, userRole = {
                    RoleNames.ROLE_COLLABORATOR_ADMIN, RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN,
                    RoleNames.ROLE_PROJ_COLLABORATOR_CONTRIBUTOR }) })
    @RequestMapping(value = "/auth/workbench/workspace/{projectid}/{workspaceid}/addtext", method = RequestMethod.GET)
    public ModelAndView addTextFileForm(@PathVariable("workspaceid") String workspaceid,
            @PathVariable("projectid") String projid) throws QuadrigaStorageException, QuadrigaAccessException {

        
        ModelAndView model = new ModelAndView("auth/workbench/workspace/addtext");
        model.getModelMap().put("textfile", textFileFactory.createTextFileObject());
        model.getModelMap().put("workspaceId", workspaceid);
        model.getModelMap().put("myProjectId", projid);
        model.getModelMap().put("success", "0");
        model.getModelMap().put("accessibilityList",accList);
        return model;
    }

    /**
     * @param resp
     *            Generic HTTP Response Object
     * @param txtFile
     *            Web backing domain object to store the Text Details
     * @param result
     *            Generic BingingResults object to hold validation errors
     * @param workspaceid
     *            Path Variable to retrieve the associated Workspace id
     * @return Returns the workspace details page if success else returns the
     *         add text page.
     * @throws QuadrigaStorageException
     * @throws QuadrigaAccessException
     * @throws IOException
     */
    @AccessPolicies({
            @ElementAccessPolicy(type = CheckedElementType.WORKSPACE, paramIndex = 4, userRole = {
                    RoleNames.ROLE_WORKSPACE_COLLABORATOR_ADMIN, RoleNames.ROLE_WORKSPACE_COLLABORATOR_EDITOR }),
            @ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 5, userRole = {
                    RoleNames.ROLE_COLLABORATOR_ADMIN, RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN,
                    RoleNames.ROLE_PROJ_COLLABORATOR_CONTRIBUTOR }) })
    @RequestMapping(value = "/auth/workbench/workspace/{projectid}/{workspaceid}/addtext", method = RequestMethod.POST)
    public ModelAndView saveTextFileForm(HttpServletResponse resp,
            @Validated @ModelAttribute("textfile") TextFile txtFile, BindingResult result,
            @PathVariable("workspaceid") String workspaceid, @PathVariable("projectid") String projid,
            RedirectAttributes redirectAttributes)
                    throws QuadrigaAccessException, QuadrigaStorageException {

        
        ModelAndView model = new ModelAndView();
        model.getModelMap().put("workspaceId", workspaceid);
        model.getModelMap().put("myProjectId", projid);
        if (result.hasErrors()) {
            model.setViewName("auth/workbench/workspace/addtext");
            model.getModelMap().put("textfile", txtFile);
            model.getModelMap().put("accessibilityList", accList);
        } else {
            model = new ModelAndView("redirect:/auth/workbench/workspace/workspacedetails/" + workspaceid);
            txtFile.setWorkspaceId(workspaceid);
            txtFile.setProjectId(projid);
            try {
                if (tfManager.saveTextFile(txtFile)) {
                    redirectAttributes.addFlashAttribute("show_success_alert", true);
                    redirectAttributes.addFlashAttribute("success_alert_msg", "The text file is successfully saved");
                } else {
                    redirectAttributes.addFlashAttribute("show_error_alert", true);
                    redirectAttributes.addFlashAttribute("error_alert_msg", "Error while saving the text file.");
                }
            } catch (FileStorageException fse) {
                redirectAttributes.addFlashAttribute("show_error_alert", true);
                redirectAttributes.addFlashAttribute("error_alert_msg", "The file could not be saved. If the problem persists, please contact an administrator.");
                
            }
        }
        return model;
    }

}
