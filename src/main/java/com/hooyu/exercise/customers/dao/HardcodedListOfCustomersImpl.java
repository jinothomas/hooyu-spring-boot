package com.hooyu.exercise.customers.dao;

import com.hooyu.exercise.customers.domain.Customer;
import com.hooyu.exercise.customers.domain.CustomerType;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class HardcodedListOfCustomersImpl implements CustomerDao {

	private static Map<String,Customer> customers = new HashMap<>();
	
	public HardcodedListOfCustomersImpl() {
		customers.put("john.doe@192.com", createDummyCustomer("john.doe@192.com", "John", "Doe", CustomerType.PREMIUM));
		customers.put("sally.smith@192.com", createDummyCustomer("sally.smith@192.com", "Sally", "Smith", CustomerType.PREMIUM));
		customers.put("harry.lang@192.com", createDummyCustomer("harry.lang@192.com", "Harry", "Lang", CustomerType.NON_PAYING));
	}
	
	public Customer findCustomerByEmailAddress(String email) throws CustomerNotFoundException {
		Customer customer = customers.get(email);
		if(customer == null) {
			throw new CustomerNotFoundException("Invalid customer");
		}	
		return customer;
	}
	
	private Customer createDummyCustomer(String email, String forename, String surname, CustomerType type) {
		Customer c = new Customer();
		c.setEmailAddress(email);
		c.setForename(forename);
		c.setSurname(surname);
		c.setCustomType(type);
		return c;
	}

	@Override
	public boolean isCustomerPresent(String userEmail) {
		return customers.containsKey(userEmail);
	}
}