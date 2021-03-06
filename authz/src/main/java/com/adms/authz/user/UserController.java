package com.adms.authz.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.adms.authz.user.repo.User;
import com.adms.authz.user.repo.UserRepository;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	@RequestMapping(value = "/api/user/current", method = RequestMethod.GET)
	public User getCurrent() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof UserAuthentication) {
			return ((UserAuthentication) authentication).getDetails();
		}
		return new User(); //anonymous user support
	}

	@RequestMapping(value = "/admin/api/user/{user}/grant/role/{role}", method = RequestMethod.POST)
	public ResponseEntity<String> grantRole(@PathVariable User user, @PathVariable UserRole role) {
		if (user == null) {
			return new ResponseEntity<>("invalid user id", HttpStatus.UNPROCESSABLE_ENTITY);
		}

		user.grantRole(role);
		userRepository.saveAndFlush(user);
		return new ResponseEntity<>("role granted", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/user/{user}/revoke/role/{role}", method = RequestMethod.POST)
	public ResponseEntity<String> revokeRole(@PathVariable User user, @PathVariable UserRole role) {
		if (user == null) {
			return new ResponseEntity<>("invalid user id", HttpStatus.UNPROCESSABLE_ENTITY);
		}

		user.revokeRole(role);
		userRepository.saveAndFlush(user);
		return new ResponseEntity<>("role revoked", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/api/user", method = RequestMethod.GET)
	public List<User> list() {
		return userRepository.findAll();
	}
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView registerPage(ModelAndView mav) {
		mav.setViewName("register");
		User user = new User();
		mav.addObject("user", user);
		return mav;
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView registerUser(ModelAndView mav,@ModelAttribute("user") User user) {
		//userRepository.saveAndFlush(user);
		mav.setViewName("login");
		mav.addObject("user", user);
		return mav;
	}

}
