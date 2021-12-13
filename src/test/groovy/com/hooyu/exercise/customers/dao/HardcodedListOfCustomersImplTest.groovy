package com.hooyu.exercise.customers.dao

import com.hooyu.exercise.customers.domain.Customer
import com.hooyu.exercise.customers.domain.CustomerType
import spock.lang.Specification;

class HardcodedListOfCustomersImplTest extends Specification {

	private HardcodedListOfCustomersImpl customerImpl;
	
	def setup() {
		customerImpl = new HardcodedListOfCustomersImpl();
	}

	def "should return expected customer"() {
		when:
			def customer = customerImpl.findCustomerByEmailAddress("john.doe@192.com");

		then:
			customer.equals(getExpectedCustomer())
	}

	def "should throw CustomerNotFoundException"() {
		when:
			customerImpl.findCustomerByEmailAddress("invalid_customer@192.com")

		then:
			thrown(CustomerNotFoundException.class)
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