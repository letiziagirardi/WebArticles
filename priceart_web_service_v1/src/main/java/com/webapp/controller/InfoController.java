package com.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.appconf.AppConfig;

@RestController
public class InfoController
{
	@Autowired
	private AppConfig configuration;

	/*
	 * This method provides a simple endpoint that can be used to retrieve the value of the "listino" configuration setting. 
	 */
	@RequestMapping("/info")
	public Map<String, String> getInfo()
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("listino", configuration.getListino());
		
		return map;
	}
}
