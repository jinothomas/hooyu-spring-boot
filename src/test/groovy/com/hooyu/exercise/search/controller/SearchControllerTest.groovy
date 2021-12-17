package com.hooyu.exercise.search.controller

import javax.servlet.http.HttpSession

import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpSession
import org.springframework.ui.Model

import com.hooyu.exercise.customers.dao.CustomerNotFoundException
import com.hooyu.exercise.customers.dao.UnauthorizedUserException
import com.hooyu.exercise.customers.domain.Customer;
import com.hooyu.exercise.customers.domain.CustomerType;
import com.hooyu.exercise.search.dao.RecordNotFoundException
import com.hooyu.exercise.search.dto.SearchInputRequest
import com.hooyu.exercise.service.CustomerService;

import net.icdpublishing.exercise2.myapp.charging.dao.ChargingDao
import net.icdpublishing.exercise2.myapp.charging.dao.ImaginaryChargingDaoImpl
import net.icdpublishing.exercise2.myapp.charging.services.ChargingService
import net.icdpublishing.exercise2.searchengine.services.SearchEngineRetrievalService
import spock.lang.Specification;

class SearchControllerTest extends Specification {

	Model model;
	SearchController controller;
	SearchEngineRetrievalService retrievalService;
	ChargingService chargingService = Mock(ChargingService.class);;
	HttpSession httpSession;
	ChargingDao chargingDao;
	SearchInputRequest searchInputRequest;
	private customerService = Mock(CustomerService.class);

	private static final String PAYING_EMAIL = "john.doe@192.com";
	private static final String NON_PAYING_EMAIL = "harry.lang@192.com";
	private static final String INVALID_EMAIL = "invalid.lang@192.com";

	def setup() {
		controller = new SearchController(customerService,chargingService);
		searchInputRequest = new SearchInputRequest();
		model =  Mock(Model);
		chargingService = Mock(ChargingService.class);
		retrievalService = Mock(SearchEngineRetrievalService.class);
		chargingDao = new ImaginaryChargingDaoImpl();
		httpSession = new MockHttpSession();
	}

	def "search page is expected"() {
		when:
		httpSession.setAttribute("customer", getPremiumCustomer())
		def page = controller.search(httpSession);

		then:
		page == "search"
	}


	def "unauthorized user exception is expected"() {
		when:
		def page = controller.search(httpSession);

		then:
		thrown(UnauthorizedUserException.class)
	}

	def "searchRecords with NON PRMIUM User"() {
		given: "chargingservice.charge returns void"
		chargingService.charge(NON_PAYING_EMAIL,12) >> void
		when:
		httpSession.setAttribute("customer", getNonPayingCustomer())
		searchInputRequest.setSurname("Smith");
		searchInputRequest.setPostcode("sw6 2bq");
		def page = controller.searchUser(searchInputRequest,model,httpSession);

		then:
		page == "search"
	}

	def "searchRecords with PREMIUM User "() {
		given: "chargingservice.charge returns void"
		chargingService.charge(PAYING_EMAIL,12) >> void
		when:
		searchInputRequest = new SearchInputRequest();
		httpSession.setAttribute("customer", getPremiumCustomer())
		searchInputRequest.setSurname("Smit");
		searchInputRequest.setPostcode("sw6 2bq");
		def page = controller.searchUser(searchInputRequest,model,httpSession);

		then:
		page == "search"
	}

	def "fetchSearchResults"() {
		given: "return customer"
		customerService.findCustomerByEmailAddress(_)>> getPremiumCustomer();
		when:
		def responseEntity = controller.fetchSearchResults(PAYING_EMAIL, "Smith", "sw6 2bq");

		then:
		responseEntity.statusCode == HttpStatus.OK
	}


	def "No search results are returned"() {
		given: "return customer"
		customerService.findCustomerByEmailAddress(_)>> getPremiumCustomer();
		when:
		def responseEntity = controller.fetchSearchResults(PAYING_EMAIL, "Smit", "sw6 2b");

		then:
		thrown(RecordNotFoundException.class)
	}
	
	def "CustomerNotFoundException is returned"() {
		given: "return exception"
		customerService.findCustomerByEmailAddress(_)>> null;
		when:
		def responseEntity = controller.fetchSearchResults(INVALID_EMAIL, "Smit", "sw6 2b");

		then:
		thrown(CustomerNotFoundException.class)
	}

	def getPremiumCustomer() {
		Customer expectedCustomer = new Customer();
		expectedCustomer.setEmailAddress(PAYING_EMAIL);
		expectedCustomer.setForename("John");
		expectedCustomer.setSurname("Doe");
		expectedCustomer.setCustomType(CustomerType.PREMIUM);
		return expectedCustomer;
	}

	def getNonPayingCustomer() {
		Customer expectedCustomer = new Customer();
		expectedCustomer.setEmailAddress(NON_PAYING_EMAIL);
		expectedCustomer.setForename("Harry");
		expectedCustomer.setSurname("Lang");
		expectedCustomer.setCustomType(CustomerType.NON_PAYING);
		return expectedCustomer;
	}
}
