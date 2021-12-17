package com.hooyu.exercise.signin.controller

import javax.servlet.http.HttpSession

import org.springframework.mock.web.MockHttpSession
import org.springframework.ui.Model

import com.hooyu.exercise.customers.dao.CustomerNotFoundException
import com.hooyu.exercise.customers.domain.Customer
import com.hooyu.exercise.customers.domain.CustomerType
import com.hooyu.exercise.service.CustomerService
import com.hooyu.exercise.signin.dto.SignInRequest

import spock.lang.Specification;

class SignInControllerTest extends Specification {

	private SignInController signInController;
	private customerService = Mock(CustomerService.class);
	HttpSession httpSession;
	SignInRequest signInRequest = Mock(SignInRequest.class);
	Model model;

	def setup() {
		signInController = new SignInController(customerService);
		model =  Mock(Model);
		signInRequest = new SignInRequest();
		httpSession = new MockHttpSession();
	}

	def "should return login page"() {
		when:
		def signIn = signInController.signin()
		then:
		signIn == "signin"
	}
	
	def "should return search page"() {
		given: "return customer"
			customerService.findCustomerByEmailAddress(_)>> getExpectedCustomer();
		when:
		def signIn = signInController.authenticate(signInRequest, httpSession, model);
		then:
			signIn == "search"
	}
	
	def "should return signIn page"() {
		given: "return CustomerNotFoundException"
		customerService.findCustomerByEmailAddress(_)>> {throw new CustomerNotFoundException()};
		
		when: "An Invalid email is provided"
		signInRequest.setEmail("InvalidUser@192.com");
		def signIn = signInController.authenticate(signInRequest, httpSession, model);
		then:
			signIn == "signin"
	}
	
	
	def "The user is signing Out"() {
		when: "An Invalid email is provided"
		httpSession.setAttribute("customer", getExpectedCustomer());
		def signIn = signInController.signOut(httpSession);
		then:
		signIn == "signin"
	}
	
	def getExpectedCustomer() {
		Customer expectedCustomer = new Customer();
		expectedCustomer.setEmailAddress("john.doe@192.com");
		expectedCustomer.setForename("John");
		expectedCustomer.setSurname("Doe");
		expectedCustomer.setCustomType(CustomerType.PREMIUM);
		return expectedCustomer;
	}
	
	}
