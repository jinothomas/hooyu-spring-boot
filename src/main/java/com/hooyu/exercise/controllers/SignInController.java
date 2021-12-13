package com.hooyu.exercise.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hooyu.exercise.customers.dao.CustomerNotFoundException;
import com.hooyu.exercise.customers.domain.Customer;
import com.hooyu.exercise.dto.SignInRequest;
import com.hooyu.exercise.service.CustomerService;

@Controller
public class SignInController {
	
@Autowired	
private CustomerService customerService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String signin() {
    	
        return "signin";
    }
    
    @RequestMapping(value = "/signIn", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public String authenticate(SignInRequest signInRequest, Model model) {

		try {
			Customer customer = customerService.findCustomerByEmailAddress(signInRequest.getEmail());
			model.addAttribute("emailAddress", customer.getEmailAddress());
			model.addAttribute("customerType",customer.getCustomType().name());
			return "search";

		} catch (Exception exception) {
			model.addAttribute("error", exception.getLocalizedMessage());
			return "signin";

		}
	}
}
