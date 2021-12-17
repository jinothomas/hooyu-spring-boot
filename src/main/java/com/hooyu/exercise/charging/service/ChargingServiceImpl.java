package com.hooyu.exercise.charging.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.hooyu.exercise.customers.dao.CustomerNotFoundException;
import com.hooyu.exercise.customers.domain.Customer;
import com.hooyu.exercise.service.CustomerService;

import net.icdpublishing.exercise2.myapp.charging.ChargingException;
import net.icdpublishing.exercise2.myapp.charging.dao.ChargingDao;
import net.icdpublishing.exercise2.myapp.charging.dao.ImaginaryChargingDaoImpl;
import net.icdpublishing.exercise2.myapp.charging.services.ChargingService;


@Service
public class ChargingServiceImpl implements ChargingService {

	private ChargingDao chargingDao = new ImaginaryChargingDaoImpl();
	
	private CustomerService customerService;
	
	private static Log logger = LogFactory.getLog(ChargingServiceImpl.class);
	
	public ChargingServiceImpl(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	@Override
	public void charge(String userEmail, int numberOfCredits) throws ChargingException {
		logger.info("Inside charge method, email:" + userEmail);

		Customer customer = customerService.findCustomerByEmailAddress(userEmail);
		if (customer == null) {
			logger.error("Customer not found:" + userEmail);
			throw new CustomerNotFoundException("Customer not found");
		}
		/*
		 * A simple check is added as there is an Imaginary limit of 192 Credits for
		 * each user customer.getTotalCredits() < numberOfCredits ) will be false always
		 */
		if (customer.getTotalCredits() < numberOfCredits) {
			logger.error("Customer does not have enough credits", new ChargingException("Credit Balance is low"));
			throw new ChargingException("You Cannot perform the operation, Your credit balance is low");
		}
		/*
		 * * here as the method chargeForSearch is not returning any value, no
		 * subtraction to the actual credit is done
		 */
		chargingDao.chargeForSearch(userEmail, numberOfCredits);
		logger.info("Credits charged for search:" + numberOfCredits);

	}

}
