package com.webapp.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webapp.appconf.AppConfig;
import com.webapp.entities.DettListini;
import com.webapp.exception.BindingException;
import com.webapp.exception.NotFoundException;
import com.webapp.service.PrezziService;

@RestController
@RequestMapping("/prezzi")
public class PrezziController
{
	private static final Logger logger = LoggerFactory.getLogger(PrezziController.class);

	@Autowired
	private PrezziService prezziService;
	 
	@Autowired
	private AppConfig Config;
	
	@Autowired
	private ResourceBundleMessageSource errMessage;

	@ApiOperation(
		    value = "Search for the price of an item based on the price list entered in the properties",
			notes = "Method called by external service. Returns 0 if price not found",
			response = double.class,
			produces="application/json")
	@ApiResponses(value =
	{ @ApiResponse(code = 200, message = "Calling Ok")})

	
	@RequestMapping(value = "/{codart}", method = RequestMethod.GET)

	/*
	 * The `getPriceCodArt` method is used to find and return the price of a specific article based on its unique code. 
	 * It retrieves the pricing list identifier from a configuration source, fetches pricing details for the given article code and pricing list, 
	 * and then returns the price if it's found.
	 * If the pricing details are not available, it logs a warning message and returns a default value of 0.
	 */
	public double getPriceCodArt(@ApiParam("Code Article") @PathVariable("codart") String CodArt)  
	{
		double retVal = 0;

		String IdList = Config.getListino();
		
		logger.info("Reference Price List: " + IdList);
		
		DettListini prezzo =  prezziService.SelPrezzo(CodArt, IdList);
		
		if (prezzo != null)
		{
			logger.info("Price Article: " + prezzo.getPrezzo());
		
			retVal = prezzo.getPrezzo();
		}
		else
		{
			logger.warn("Price Item Absent!!");
		}

		return retVal;
	}
}
