package com.hooyu.exercise.signin.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hooyu.exercise.customers.dao.CustomerNotFoundException;
import com.hooyu.exercise.customers.domain.Customer;
import com.hooyu.exercise.service.CustomerService;
import com.hooyu.exercise.signin.dto.SignInRequest;

@Controller
public class SignInController {

	@Autowired
	private CustomerService customerService;

	private static String SIGN_IN = "signin";

	private static String SEARCH = "search";

	/*
	 * The signin method is to return the Sign In page on load
	 * 
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String signin() {
		logger.info("Sign In page returned");
		return SIGN_IN;
	}

	private static Log logger = LogFactory.getLog(SignInController.class);

	/*
	 * The authenticate method is to Authenticate the user and redirect to Search
	 * page.
	 * 
	 */
	@RequestMapping(value = "/signIn", method = RequestMethod.POST)
	public String authenticate(SignInRequest signInRequest, HttpSession session, Model model) {

		try {

			Customer customer = customerService.findCustomerByEmailAddress(signInRequest.getEmail());
			session.setAttribute("customer", customer);
		
			model.addAttribute("emailAddress", customer.getEmailAddress());
			model.addAttribute("customerType", customer.getCustomType().name());
			logger.info("Valid User found, redirecting to Search page");
			return SEARCH;

		} catch (CustomerNotFoundException exception) {
			logger.error("An error occured: " + exception.getLocalizedMessage());
			model.addAttribute("error", exception.getLocalizedMessage());
			return SIGN_IN;

		}
	}
	
	@RequestMapping(value = "/signOut", method = RequestMethod.GET)
	public String signOut(HttpSession session) {
		session.removeAttribute("customer");
		logger.info("Sign out completed, Sign page returned");
		return SIGN_IN;
	}
}
