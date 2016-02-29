package edu.asu.spring.quadriga.web.publicwebsite;

import java.security.Principal;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.spring.quadriga.domain.workbench.IProject;
import edu.asu.spring.quadriga.exceptions.QuadrigaStorageException;
import edu.asu.spring.quadriga.service.workbench.IRetrieveProjectManager;

@PropertySource(value = "classpath:/user.properties")
@Controller
public class PublicBlogController {
	@Autowired 
	private IRetrieveProjectManager projectManager;
	
	private IProject getProjectDetails(String name) throws QuadrigaStorageException{
		return projectManager.getProjectDetailsByUnixName(name);
	}
	
	/**
	 * This method gives the the projectblog
	 * @param projectUnixName	The project unix name
	 * @param model				Model
	 * @return view
	 * @throws QuadrigaStorageException
	 */
	@RequestMapping(value = "sites/{projectUnixName}/projectblog", method = RequestMethod.GET)
	public String projectblog(@PathVariable("projectUnixName") String projectUnixName,
									   Model model)
			throws QuadrigaStorageException {
		IProject project = getProjectDetails(projectUnixName);

		if (project == null) {
			return "forbidden";
		}
		// Creating Dummy Object
		List dummyList = new ArrayList ();
		for(int i=1; i<=10; i++)
		{
			HashMap <String, String> map = new HashMap<String, String>();
			map.put("title", "Title "+i);
			map.put("text", "Text "+i);
			map.put("date", "Date "+i);
			map.put("author", "Author "+i);

			dummyList.add(map);
		}
	    model.addAttribute("blockentrylist", dummyList);

		return "sites/projectblog";
	}
}