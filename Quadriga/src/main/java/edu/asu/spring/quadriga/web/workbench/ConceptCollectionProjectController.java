package edu.asu.spring.quadriga.web.workbench;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.spring.quadriga.aspects.annotations.AccessPolicies;
import edu.asu.spring.quadriga.aspects.annotations.CheckedElementType;
import edu.asu.spring.quadriga.aspects.annotations.ElementAccessPolicy;
import edu.asu.spring.quadriga.domain.conceptcollection.IConceptCollection;
import edu.asu.spring.quadriga.domain.workbench.IProject;
import edu.asu.spring.quadriga.domain.workbench.IProjectConceptCollection;
import edu.asu.spring.quadriga.exceptions.QuadrigaAccessException;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.conceptcollection.IConceptCollectionManager;
import edu.asu.spring.quadriga.service.workbench.IProjectConceptCollectionManager;
import edu.asu.spring.quadriga.service.workbench.IRetrieveProjectManager;
import edu.asu.spring.quadriga.web.login.RoleNames;

@Controller
public class ConceptCollectionProjectController {

    @Autowired
    IConceptCollectionManager conceptCollectionManager;

    @Autowired
    IRetrieveProjectManager projectManager;

    @Autowired
    private IProjectConceptCollectionManager projectConceptCollectionManager;

    @RequestMapping(value = "auth/workbench/{projectid}/conceptcollections", method = RequestMethod.GET)
    public String listProjectConceptCollection(@PathVariable("projectid") String projectid, Model model,
            Principal principal) throws QuadrigaStorageException {
        String userId = principal.getName();
        List<IProjectConceptCollection> projectConceptCollectionList = projectConceptCollectionManager
                .listProjectConceptCollection(projectid, userId);
        model.addAttribute("projectConceptCollectionList", projectConceptCollectionList);
        IProject project = projectManager.getProjectDetails(projectid);
        model.addAttribute("project", project);
        model.addAttribute("projectid", projectid);
        return "auth/workbench/project/conceptcollections";
    }

    // @AccessPolicies({ @ElementAccessPolicy(type =
    // CheckedElementType.PROJECT,paramIndex = 1, userRole =
    // {RoleNames.ROLE_COLLABORATOR_ADMIN,RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN}
    // )})
    // @RequestMapping(value =
    // "auth/workbench/{projectid}/addconceptcollection", method =
    // RequestMethod.GET)
    // public String addProjectConceptCollection(
    // @PathVariable("projectid") String projectid, Model model)
    // throws QuadrigaAccessException
    //
    // {
    // try {
    // UserDetails user = (UserDetails) SecurityContextHolder.getContext()
    // .getAuthentication().getPrincipal();
    // String userId = user.getUsername();
    //
    // List<IProjectConceptCollection> conceptCollectionList = null;
    // //TODO: getCollectionsOwnedbyUser() needs to be changed according to
    // mapper
    // try {
    // conceptCollectionList =
    // conceptCollectionManager.getCollectionsOwnedbyUser(userId);
    // } catch (QuadrigaStorageException e) {
    // throw new QuadrigaStorageException();
    // }
    // if (conceptCollectionList == null) {
    // logger.info("conceptCollectionList list is empty");
    // }
    // //TODO: iterator needs to be changed
    // Iterator<IProjectConceptCollection> I = conceptCollectionList.iterator();
    // while(I.hasNext()){
    // IConceptCollection con = I.next();
    // logger.info(" "+con.getConceptCollectionName());
    // }
    // model.addAttribute("conceptCollectionList", conceptCollectionList);
    // IProject project = projectManager.getProjectDetails(projectid);
    // model.addAttribute("project", project);
    // model.addAttribute("projectid", projectid);
    // model.addAttribute("userId", userId);
    // } catch (Exception e) {
    // logger.error(" ----",e);
    // }
    // return "auth/workbench/project/addconceptcollections";
    // }
    @AccessPolicies({ @ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 1, userRole = {
            RoleNames.ROLE_COLLABORATOR_ADMIN, RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN }) })
    @RequestMapping(value = "auth/workbench/{projectid}/addconceptcollection", method = RequestMethod.GET)
    public String addProjectConceptCollection(@PathVariable("projectid") String projectid, Model model,
            Principal principal) throws QuadrigaAccessException, QuadrigaStorageException {
        String userId = principal.getName();
        List<IConceptCollection> conceptCollectionList = conceptCollectionManager.getCollectionsOwnedbyUser(userId);
        model.addAttribute("conceptCollectionList", conceptCollectionList);
        IProject project = projectManager.getProjectDetails(projectid);
        model.addAttribute("project", project);
        model.addAttribute("projectid", projectid);
        return "auth/workbench/project/addconceptcollections";
    }

    @AccessPolicies({ @ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 2, userRole = {
            RoleNames.ROLE_COLLABORATOR_ADMIN, RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN }) })
    @RequestMapping(value = "auth/workbench/{projectid}/addconceptcollection", method = RequestMethod.POST)
    public String addProjectConceptCollection(HttpServletRequest req, @PathVariable("projectid") String projectid,
            Model model, Principal principal, RedirectAttributes attr)
                    throws QuadrigaStorageException, QuadrigaAccessException {
        String userId = principal.getName();
        String[] values = req.getParameterValues("selected");
        if (values == null) {
            attr.addFlashAttribute("show_error_alert", true);
            attr.addFlashAttribute("error_alert_msg", "Please select a Concept Collection.");
            return "redirect:/auth/workbench/" + projectid + "/addconceptcollection";
        }
        for (int i = 0; i < values.length; i++) {
            projectConceptCollectionManager.addProjectConceptCollection(projectid, values[i], userId);
        }
        attr.addFlashAttribute("show_success_alert", true);
        attr.addFlashAttribute("success_alert_msg", "Concept Collection added to workspace successfully.");
        return "redirect:/auth/workbench/" + projectid + "/addconceptcollection";
    }

    @AccessPolicies({ @ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 1, userRole = {
            RoleNames.ROLE_COLLABORATOR_ADMIN, RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN }) })
    @RequestMapping(value = "auth/workbench/{projectid}/deleteconceptcollections", method = RequestMethod.GET)
    public String deleteProjectConceptCollection(@PathVariable("projectid") String projectid, Model model,
            Principal principal) throws QuadrigaStorageException, QuadrigaAccessException {
        String userId = principal.getName();
        List<IProjectConceptCollection> projectConceptCollectionList = projectConceptCollectionManager
                .listProjectConceptCollection(projectid, userId);

        model.addAttribute("projectConceptCollectionList", projectConceptCollectionList);
        IProject project = projectManager.getProjectDetails(projectid);
        model.addAttribute("project", project);
        model.addAttribute("projectid", projectid);
        return "auth/workbench/project/deleteconceptcollections";
    }

    @AccessPolicies({ @ElementAccessPolicy(type = CheckedElementType.PROJECT, paramIndex = 2, userRole = {
            RoleNames.ROLE_COLLABORATOR_ADMIN, RoleNames.ROLE_PROJ_COLLABORATOR_ADMIN }) })
    @RequestMapping(value = "auth/workbench/{projectid}/deleteconceptcollections", method = RequestMethod.POST)
    public String deleteProjectConceptCollection(HttpServletRequest req, @PathVariable("projectid") String projectid,
            Model model, Principal principal, RedirectAttributes attr)
                    throws QuadrigaStorageException, QuadrigaAccessException {
        String userId = principal.getName();
        String[] values = req.getParameterValues("selected");
        if (values == null) {
            attr.addFlashAttribute("show_error_alert", true);
            attr.addFlashAttribute("error_alert_msg", "Please select a Concept Collection.");
            return "redirect:/auth/workbench/" + projectid + "/deleteconceptcollections";
        }
        for (int i = 0; i < values.length; i++) {
            projectConceptCollectionManager.deleteProjectConceptCollection(projectid, userId, values[i]);
        }

        attr.addFlashAttribute("show_success_alert", true);
        attr.addFlashAttribute("success_alert_msg", "Concept Collection deleted from workspace successfully.");
        return "redirect:/auth/workbench/" + projectid + "/deleteconceptcollections";
    }
}
