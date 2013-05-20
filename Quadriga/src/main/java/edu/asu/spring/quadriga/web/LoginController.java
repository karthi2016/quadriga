package edu.asu.spring.quadriga.web;

import java.security.Principal;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.spring.quadriga.domain.IUser;
import edu.asu.spring.quadriga.service.IUserManager;

@Controller
public class LoginController {

	@Autowired
	IUserManager userManager;
	IUser user;

	// PersistentTokenBasedRememberMeServices service;

	public LoginController() {

	}

	@RequestMapping(value = "auth/welcome", method = RequestMethod.GET)
	public String validUserHandle(ModelMap model, Principal principal,
			Authentication authentication) throws SQLException {

		// Get the LDAP-authenticated userid
		String sUserId = principal.getName();
		
		model.addAttribute("username", sUserId);
		// model.addAttribute("role",principal.);
		
		return "auth/loggedIn";

	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {

		return "login";

	}

	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {

		model.addAttribute("error", "true");
		return "login";

	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {

		return "login";

	}

}