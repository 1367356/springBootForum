package com.qingwenwei.event;

import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@SuppressWarnings("serial")
public class OnRegistrationCompleteEvent extends ApplicationEvent {

	private final String username;
	private final String appUrl;
	private final Locale locale;
	
	public OnRegistrationCompleteEvent(String username, String appUrl, Locale locale) {
		super(username);
		this.username = username;
		this.appUrl = appUrl;
		this.locale = locale;
	}

	public String getUsername() {
		return username;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public Locale getLocale() {
		return locale;
	}

}
