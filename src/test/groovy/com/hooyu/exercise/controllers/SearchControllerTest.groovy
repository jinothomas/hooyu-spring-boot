package com.hooyu.exercise.controllers

import spock.lang.Specification;

class SearchControllerTest extends Specification {

	SearchController controller
	
	def setup() {
		controller = new SearchController()
	}

	def "search"() {
		when:
			def page = controller.search()
	
		then:
			page == "search"
	}
}
