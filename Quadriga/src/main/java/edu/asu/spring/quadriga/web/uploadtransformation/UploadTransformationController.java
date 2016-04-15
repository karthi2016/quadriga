package edu.asu.spring.quadriga.web.uploadtransformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.asu.spring.quadriga.service.uploadtransformation.IUploadTransformationManager;

/**
 * 
 * @author Jaya Venkat
 * This is a controller which takes request from uploadTransfomation.jsp to upload tranformation files
 */
@Controller
public class UploadTransformationController {

	@Autowired
	private IUploadTransformationManager uploadTnfmManager;
	
	@RequestMapping(value="auth/uploadTransformation",method=RequestMethod.POST)
	public String uploadTransformationFiles(@ModelAttribute("UploadTransformationBackingBean") UploadTransformationBackingBean formBean, ModelMap map, 
			@RequestParam("file") MultipartFile[] file,RedirectAttributes redirectAttributes) throws IOException{

		String mappingTitle = formBean.getMappingTitle();
		String mappingDescription=""+formBean.getMappingDescription();				
		System.out.println("Mapping File Title is: "+mappingTitle);
		System.out.println("Mapping File Description is: "+mappingDescription);
		
		String transformTitle= formBean.getTransformTitle();		
		String transformDescription = formBean.getTransformDescription();	    
		System.out.println("Transfomation File Title is: "+transformTitle);
		System.out.println("Tranformation File Description is: "+transformDescription);
		uploadTnfmManager.saveMetaData(mappingTitle, mappingDescription, transformTitle, transformDescription);
				
		map.addAttribute("success", 1);
		return "auth/uploadTransformation";
	}
}
