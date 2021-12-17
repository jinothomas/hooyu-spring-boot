package com.hooyu.exercise.charging.service

import com.hooyu.exercise.customers.domain.Customer
import com.hooyu.exercise.customers.domain.CustomerType
import com.hooyu.exercise.service.CustomerService

import net.icdpublishing.exercise2.myapp.charging.ChargingException
import net.icdpublishing.exercise2.myapp.charging.dao.ChargingDao
import spock.lang.Specification;

class ChargingServiceImplTest extends Specification {

	private ChargingDao chargingDao;
	private CustomerService customerService;
	private ChargingServiceImpl chargingServiceImpl;
	
	private static final String EMAIL = 'john.doe@192.com';
	private static final String INVALID_EMAIL = 'Invalid@192.com';
	
	def setup() {
		chargingDao = Mock(ChargingDao.class);
		customerService = Mock(CustomerService.class);
		chargingServiceImpl = new ChargingServiceImpl(customerService);
	}

	def "user is charged for search"() {
		given: "return customer"
			customerService.findCustomerByEmailAddress(_)>> getExpectedCustomer();
			chargingDao.chargeForSearch(EMAIL, 2)>> void;
		when:
			chargingServiceImpl.charge(EMAIL, 2);
		then:
			void
	}
	
	def "user has low credit"() {
		given: "return customer"
			customerService.findCustomerByEmailAddress(_)>> getCustomerWithLowCredits();
		when:
			chargingServiceImpl.charge(EMAIL, 3);
		then:
			thrown(ChargingException.class)
	}
		
	def getExpectedCustomer() {
		Customer expectedCustomer = new Customer();
		expectedCustomer.setEmailAddress("john.doe@192.com");
		expectedCustomer.setForename("John");
		expectedCustomer.setSurname("Doe");
		expectedCustomer.setCustomType(CustomerType.PREMIUM);
		expectedCustomer.setTotalCredits(192);
		return expectedCustomer;
	}
	
	def getCustomerWithLowCredits() {
		Customer expectedCustomer = new Customer();
		expectedCustomer.setEmailAddress("john.doe@192.com");
		expectedCustomer.setForename("John");
		expectedCustomer.setSurname("Doe");
		expectedCustomer.setCustomType(CustomerType.PREMIUM);
		expectedCustomer.setTotalCredits(1);
		return expectedCustomer;
	}
}