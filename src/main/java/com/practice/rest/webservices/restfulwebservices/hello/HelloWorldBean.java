package com.practice.rest.webservices.restfulwebservices.hello;

public class HelloWorldBean {
	private final String message;

	public HelloWorldBean(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "HelloWorldBean [message=" + message + "]";
	}

}
