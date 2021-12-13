package com.hooyu.exercise.customers.dao;

import org.springframework.stereotype.Component;

import com.hooyu.exercise.customers.domain.Customer;

@Component
public interface CustomerDao {

	/**
	 * 
	 * @param email
	 * @return
	 * @throws CustomerNotFoundException
	 */
	Customer findCustomerByEmailAddress(String email) throws CustomerNotFoundException;

	boolean isCustomerPresent(String userEmail);
}
