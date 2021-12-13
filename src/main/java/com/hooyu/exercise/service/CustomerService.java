package com.hooyu.exercise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hooyu.exercise.customers.dao.CustomerDao;
import com.hooyu.exercise.customers.domain.Customer;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerDao customerDao;
	
	public Customer findCustomerByEmailAddress(String userEmail) {
		
		return customerDao.findCustomerByEmailAddress(userEmail);
		
	}
	
	public boolean isCustomerPresent(String userEmail) {
		return customerDao.isCustomerPresent(userEmail);
	
	}
	
}