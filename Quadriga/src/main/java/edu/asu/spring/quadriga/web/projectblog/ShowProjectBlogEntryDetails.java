package edu.asu.spring.quadriga.web.projectblog;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.spring.quadriga.aspects.annotations.InjectProject;
import edu.asu.spring.quadriga.domain.impl.projectblog.ProjectBlogEntry;
import edu.asu.spring.quadriga.domain.projectblog.IProjectBlogEntry;
import edu.asu.spring.quadriga.domain.workbench.IProject;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.projectblog.IProjectBlogEntryManager;

/**
 * This controller shows the latest project blog entry
 * in a new page
 * 
 * @author Kavinya, Pawan
 *
 */
@Controller
public class ShowProjectBlogEntryDetails {
    @Autowired
    private IProjectBlogEntryManager projectBlogEntryManager;

    /**
     * This method displays the recent project blog
     * 
     * If the project has been set to 'accessible', then the public website page
     * is displayed. If the project does not exist then an error page is shown.
     * 
     * @param unixName
     *            unix name that is given to the project at the time of its
     *            creation
     * @param model
     *            Model object to map values to view
     * @return returns a string to access the external website main page
     * @throws QuadrigaStorageException
     *             Database storage exception thrown
     */
    @RequestMapping(value = "sites/{ProjectUnixName}/projectblogdetails", method = RequestMethod.POST)
    public String projectblogdetails(Model model,@ModelAttribute("latestProjectBlogEntry") ProjectBlogEntry latestProjectBlogEntry) throws QuadrigaStorageException {

        // Fetch blog entries for a project identified by project unix name
        model.addAttribute("latestProjectBlogEntry", latestProjectBlogEntry);
        //model.addAttribute("project", project);
        return "sites/projectblogdetails";
    }

}
