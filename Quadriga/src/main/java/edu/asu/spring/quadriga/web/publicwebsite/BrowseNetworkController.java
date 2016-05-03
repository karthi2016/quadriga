package edu.asu.spring.quadriga.web.publicwebsite;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.spring.quadriga.aspects.annotations.CheckPublicAccess;
import edu.asu.spring.quadriga.aspects.annotations.InjectProject;
import edu.asu.spring.quadriga.domain.network.INetwork;
import edu.asu.spring.quadriga.domain.workbench.IProject;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.network.INetworkManager;

@Controller
public class BrowseNetworkController {

    @Autowired
    private INetworkManager networkmanager;

    
    /**
     * This method retrieves all the networks associated with the project based
     * on the project unix name
     * 
     * If the project contains networks, it displays all of the networks along
     * with the names of the workspaces that contain the networks. If no
     * networks have been created for that particular project, then an
     * appropriate error page is displayed.
     * 
     * @param unixName
     *            unix name that is given to the project at the time of its
     *            creation
     * @param model
     *            Model object to map values to view
     * @return returns a string to access the browse networks page of the
     *         project external website
     * @throws QuadrigaStorageException
     *             Database storage exception thrown
     */
    @CheckPublicAccess(projectIndex = 4)
    @RequestMapping(value = "sites/{ProjectUnixName}/browsenetworks", method = RequestMethod.GET)
    public String browseNetworks(
            @PathVariable("ProjectUnixName") String unixName,
            Model model,
            Principal principal,
            @InjectProject(unixNameParameter = "ProjectUnixName") IProject project)
            throws QuadrigaStorageException {
        String projectid = project.getProjectId();
        List<INetwork> Networks = networkmanager
                .getNetworksInProject(projectid);

        model.addAttribute("networks", Networks);
        model.addAttribute("project", project);
        return "sites/browseNetworks";
        
    }
}
