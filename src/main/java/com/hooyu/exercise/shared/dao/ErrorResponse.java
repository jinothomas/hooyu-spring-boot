package com.hooyu.exercise.shared.dao;

import java.util.List;

public class ErrorResponse {
	private String message;
	private List<String> details;

	public ErrorResponse(String message, List<String> errorDetails) {
		super();
		this.setMessage(message);
		this.setDetails(errorDetails);
	}

	public List<String> getErrorDetails() {
		return details;
	}

	public void setDetails(List<String> errorDetails) {
		this.details = errorDetails;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
