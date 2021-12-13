package com.hooyu.exercise.controllers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hooyu.exercise.SearchRequest;
import com.hooyu.exercise.customers.domain.Customer;
import com.hooyu.exercise.customers.domain.CustomerType;
import com.hooyu.exercise.dto.SearchInputs;
import com.hooyu.exercise.dto.SearchResults;
import com.hooyu.exercise.service.CustomerService;

import net.icdpublishing.exercise2.myapp.charging.dao.ImaginaryChargingDaoImpl;
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
	
	private static String EMPTY_STRING ="";

	@RequestMapping("/search")
	public String search() {
		return "search";
	}
	
	/*
	 * The method is the mapping for search functionality. Returns results as a HTML page.
	 * */
	@RequestMapping(value = "/search", method = RequestMethod.POST,  consumes = "application/x-www-form-urlencoded")
	public String searchUser(SearchInputs searchInputs, Model model) throws Exception {
		SimpleSurnameAndPostcodeQuery query = new SimpleSurnameAndPostcodeQuery(searchInputs.getSurname(),
				searchInputs.getPostcode());
		Customer customer = customerService.findCustomerByEmailAddress(searchInputs.getEmail());
		SearchRequest searchRequest = new SearchRequest(query, customer);
		Collection<Record> records = handleRequest(searchRequest);
		if (!CollectionUtils.isEmpty(records)) {
			model.addAttribute("records", mapSearchResults(records));
		} else {
			model.addAttribute("error", "No Record Available");
		} 
		model.addAttribute("emailAddress", customer.getEmailAddress());
		model.addAttribute("customerType",customer.getCustomType().name());
		return "search";
	}
	
	
	/*
	 * The method is to filter BT records only for Non-paying customers and also to exclude BT from charging
	 * */
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
	 * The method is to map the results to SearchResults to send to the Front-end
	 * **/
	private Set<SearchResults> mapSearchResults(Collection<Record> resultSet) {
		Set<SearchResults> searchResultsList = new HashSet<>();
		resultSet.forEach((record)->{
			SearchResults result = new SearchResults();
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
				String telephone = record.getPerson().getTelephone() != null ? record.getPerson().getTelephone(): EMPTY_STRING;
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
	 * */
	private Collection<Record> getResults(SimpleSurnameAndPostcodeQuery query) {
		return retrievalService.search(query);
	}
}