package com.hooyu.exercise.signin.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.hooyu.exercise.customers.dao.CustomerNotFoundException;
import com.hooyu.exercise.customers.domain.Customer;
import com.hooyu.exercise.signin.dto.SignInRequest;
import com.hooyu.exercise.service.CustomerService;
import com.hooyu.exercise.shared.dao.ErrorResponse;

@Controller
public class SignInController {
	
@Autowired	
private CustomerService customerService;

	private static String SIGN_IN ="signin";
	
	private static String SEARCH ="search";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String signin() {
        return SIGN_IN;
    }
    
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
	public String authenticate(SignInRequest signInRequest, Model model) {

		try {
			Customer customer = customerService.findCustomerByEmailAddress(signInRequest.getEmail());
			model.addAttribute("emailAddress", customer.getEmailAddress());
			model.addAttribute("customerType",customer.getCustomType().name());
			return SEARCH;

		} catch (CustomerNotFoundException exception) {
			model.addAttribute("error", exception.getLocalizedMessage());
			return SIGN_IN;

		}
	}
    
}
