package com.hooyu.exercise.search.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.hooyu.exercise.SearchRequest;
import com.hooyu.exercise.customers.dao.CustomerNotFoundException;
import com.hooyu.exercise.customers.dao.UnauthorizedUserException;
import com.hooyu.exercise.customers.domain.Customer;
import com.hooyu.exercise.customers.domain.CustomerType;
import com.hooyu.exercise.search.dao.RecordNotFoundException;
import com.hooyu.exercise.search.dto.SearchInputRequest;
import com.hooyu.exercise.search.dto.SearchResultsResponse;
import com.hooyu.exercise.service.CustomerService;
import com.hooyu.exercise.shared.dao.ErrorResponse;

import net.icdpublishing.exercise2.myapp.charging.services.ChargingService;
import net.icdpublishing.exercise2.myapp.charging.services.ImaginaryChargingService;
import net.icdpublishing.exercise2.searchengine.domain.Record;
import net.icdpublishing.exercise2.searchengine.domain.SourceType;
import net.icdpublishing.exercise2.searchengine.loader.DataLoader;
import net.icdpublishing.exercise2.searchengine.requests.SimpleSurnameAndPostcodeQuery;
import net.icdpublishing.exercise2.searchengine.services.DummyRetrievalServiceImpl;
import net.icdpublishing.exercise2.searchengine.services.SearchEngineRetrievalService;

@Controller
public class SearchController {

	@Autowired
	private CustomerService customerService;

	private SearchEngineRetrievalService retrievalService = new DummyRetrievalServiceImpl(new DataLoader());

	private ChargingService chargingService = new ImaginaryChargingService();

	private static final String EMPTY_STRING = "";

	private static final String SEARCH = "search";

	private static final String CUSTOMER = "customer";

	private static final String SURNAME = "surname";

	private static final String POSTCODE = "postcode";

	private static final String EMAIL = "email";
	
	private static Log logger = LogFactory.getLog(SearchController.class);

	@RequestMapping("/search")
	public String search(HttpSession httpSession) throws Exception {

		// Checks whether the user is authenticated to do the action
		validateUser(httpSession);
		return SEARCH;
	}

	/*
	 * The method is the mapping for search functionality for HTML Page. Response is
	 * modified to be more suitable for UI manipulation.
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String searchUser(SearchInputRequest searchInputs, Model model, HttpSession session)
			throws UnauthorizedUserException {
		logger.info("Reached searchUser Method");

		// Checks whether the user is authenticated to do the action
		Customer customer = validateUser(session);

		SimpleSurnameAndPostcodeQuery query = new SimpleSurnameAndPostcodeQuery(searchInputs.getSurname(),
				searchInputs.getPostcode());

		logger.info("Customer record found: " + customer);
		SearchRequest searchRequest = new SearchRequest(query, customer);
		Collection<Record> records = handleRequest(searchRequest);

		if (!CollectionUtils.isEmpty(records)) {
			model.addAttribute("records", mapSearchResults(records));
			logger.info("Records are fetched successfully");
		} else {
			model.addAttribute("error", new RecordNotFoundException("Record not Found"));
			logger.error("No Valid Records found for the given inputs:",
					new RecordNotFoundException("Record not Found"));
		}
		model.addAttribute("emailAddress", customer.getEmailAddress());
		model.addAttribute("customerType", customer.getCustomType().name());
		logger.info("Returns Search Page");
		return SEARCH;
	}

	/*
	 * The method is the mapping for search functionality for JSON Response. Actual
	 * Response from search engine is returned.
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ResponseEntity<?> fetchSearchResults(@RequestHeader(CUSTOMER) String email,
			@RequestHeader(SURNAME) String surname, @RequestHeader(POSTCODE) String postcode)
			throws CustomerNotFoundException {
		logger.info("Reached fetchSearchResults Method");
		SimpleSurnameAndPostcodeQuery query = new SimpleSurnameAndPostcodeQuery(surname, postcode);

		Customer customer = customerService.findCustomerByEmailAddress(email);
		logger.info("Customer record found: " + customer);
		SearchRequest searchRequest = new SearchRequest(query, customer);
		Collection<Record> searchResponse = handleRequest(searchRequest);

		if (CollectionUtils.isEmpty(searchResponse)) {
			logger.error("No Valid Records found for the given inputs:",
					new RecordNotFoundException("Record not Found"));
			throw new RecordNotFoundException("Record not Found");
		} else {
			return new ResponseEntity<>(searchResponse, HttpStatus.OK);
		}

	}

	/*
	 * The method is to filter BT records only for Non-paying customers and also to
	 * exclude BT from charging
	 */
	public Collection<Record> handleRequest(SearchRequest request) {
		Collection<Record> resultSet = getResults(request.getQuery());
		Collection<Record> bTResultSet = resultSet.stream()
				.filter(result -> result.getSourceTypes().size() == 1
						&& result.getSourceTypes().stream().findFirst().get().equals(SourceType.BT))
				.collect(Collectors.toList());
		if (request.getCustomer().getCustomType().equals(CustomerType.NON_PAYING)) {
			resultSet = bTResultSet;
		}
		if (request.getCustomer().getCustomType().equals(CustomerType.PREMIUM)) {

			int numberOfCredits = resultSet.size() - bTResultSet.size();

			chargingService.charge(request.getCustomer().getEmailAddress(), numberOfCredits);
		}

		return resultSet;
	}

	/*
	 * The method is to map the results to SearchResultsResponse to send to the
	 * Front-end
	 **/
	private Set<SearchResultsResponse> mapSearchResults(Collection<Record> resultSet) {
		Set<SearchResultsResponse> searchResultsList = new HashSet<>();
		resultSet.forEach((record) -> {
			SearchResultsResponse result = new SearchResultsResponse();
			if (record.getPerson() != null && record.getPerson().getAddress() != null) {

				// Setting name
				StringBuilder name = new StringBuilder();
				name.append(record.getPerson().getForename()).append(" ");
				name.append(record.getPerson().getMiddlename()).append(" ");
				name.append(record.getPerson().getSurname());
				result.setName(name.toString());

				// Setting address
				StringBuilder address = new StringBuilder();
				address.append(record.getPerson().getAddress().getBuildnumber()).append(", ");
				address.append(record.getPerson().getAddress().getStreet()).append(", ");
				address.append(record.getPerson().getAddress().getPostcode()).append(", ");
				address.append(record.getPerson().getAddress().getTown());
				result.setAddress(address.toString());

				// Setting Telephone
				String telephone = record.getPerson().getTelephone() != null ? record.getPerson().getTelephone()
						: EMPTY_STRING;
				result.setTelephone(telephone);

				// Setting Source types
				StringBuilder sourceTypes = new StringBuilder();
				if (!CollectionUtils.isEmpty(record.getSourceTypes())) {
					record.getSourceTypes().forEach((type) -> {
						if (sourceTypes.length() > 0) {
							sourceTypes.append(", ").append(type);
						} else {
							sourceTypes.append(type);
						}
					});
				}
				result.setSourceTypes(sourceTypes.toString());

			}

			searchResultsList.add(result);
		});
		return searchResultsList;
	}

	/*
	 * This method is to call the search service
	 */
	private Collection<Record> getResults(SimpleSurnameAndPostcodeQuery query) {
		return retrievalService.search(query);
	}

	/*
	 * The method recordNotFoundExceptionHandler is for handling the
	 * RecordNotFoundException
	 */
	@ExceptionHandler(RecordNotFoundException.class)
	private ResponseEntity<ErrorResponse> recordNotFoundExceptionHandler(RecordNotFoundException exception,
			WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add("Email:" + request.getHeader(EMAIL));
		details.add("Postcode:" + request.getHeader(POSTCODE));
		details.add("Surname:" + request.getHeader(SURNAME));
		ErrorResponse errorResponse = new ErrorResponse(exception.getLocalizedMessage(), details);
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
	}

	/*
	 * The method compares the session that is coming from the UI and saved in the
	 * back-end. Returns the customer data if the session is valid
	 * 
	 */
	private Customer validateUser(HttpSession httpSession) throws UnauthorizedUserException {

		Customer customer = (Customer) httpSession.getAttribute(CUSTOMER);
		if (customer == null) {
			throw new UnauthorizedUserException(
					"The user is unauthorized to perform the operation, Sign in to continue");
		}
		return customer;

	}
}