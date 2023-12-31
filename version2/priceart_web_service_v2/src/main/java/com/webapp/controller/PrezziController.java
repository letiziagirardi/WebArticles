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

	/*
	 * The `getPriceCodArt` method is used to find and return the price of a specific article based on its unique code.
	 * It retrieves the pricing list identifier from a configuration source, fetches pricing details for the given article code and pricing list,
	 * and then returns the price if it's found.
	 * If the pricing details are not available, it logs a warning message and returns a default value of 0.
	 */
	@ApiOperation(
		value = "Search for the price of an item based on the price list entered in the properties",
		notes = "Method called by external service. Returns 0 if price not found",
		response = double.class, 
		produces = "application/json")
	@ApiResponses(value =
	{ @ApiResponse(code = 200, message = "Call Ok")})
	@RequestMapping(value = "/{codart}", method = RequestMethod.GET)
	// ------------------- SELECT PREZZO CODART ------------------------------------
	public double getPriceCodArt(@ApiParam("Code Article") @PathVariable("codart") String CodArt)  
	{
		double retVal = 0;

		String IdList = Config.getListino();
		
		logger.info("List: " + IdList);
		
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

	/*
	 * The `getListCodArt` method is used to fetch and provide pricing details for a particular article identified by its unique code.
	 * It communicates with a pricing service, handles potential errors, and returns the details as a JSON response entity.
	 * The method constructs an HTTP response entity that will contain the pricing details of the requested article.
	 * If the details are missing, it logs a warning and throws an exception with an appropriate error message.
	 */
	@ApiOperation(
				value = "Search for the price of an item based on the code article",
			 	response = DettListini.class,
			 	produces="application/json")
	@ApiResponses(value =
	{@ApiResponse(code = 200, message = "Price found")})
	
	// ------------------- SELECT DETTAGLIO LISTINO ------------------------------------
	@RequestMapping(value = "/cerca/codice/{codart}", method = RequestMethod.GET)
	public ResponseEntity<DettListini> getListCodArt(@PathVariable("codart") String CodArt)  
		throws NotFoundException
	{
		HttpHeaders headers = new HttpHeaders();
	
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		//Otteniamo il listino dal file di configurazione
		String IdList = Config.getListino();
		
		logger.info("Listo: " + IdList);
		
		DettListini dettListini =  prezziService.SelPrezzo(CodArt, IdList);
		
		if (dettListini == null)
		{
			String ErrMsg = String.format("Price list %s of code %s could not be found!", IdList, CodArt);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}
		
		return new ResponseEntity<DettListini>(dettListini, HttpStatus.OK);
			
	}

	/*
	 * The `createPrice` method handles the creation of a new price entry for an article. 
	 * It performs input validation, logs relevant information, and returns a response entity with information about the success of the operation.
	 */

	@ApiOperation(
				value = "Insert or modify new price for existing article",
			 	response = DettListini.class,
			 	produces="application/json")
	@ApiResponses(value =
	{@ApiResponse(code = 200, message = "Article added")})
	// ------------------- INSERT PREZZO LISTINO ------------------------------------
	@RequestMapping(value = "/inserisci", method = RequestMethod.POST)
	public ResponseEntity<DettListini> createModifyPrice(@Valid @RequestBody DettListini dettListini, BindingResult bindingResult,
			UriComponentsBuilder ucBuilder) 
			throws BindingException 
	{
		logger.info(String.format("We save the %s price of the %s item", dettListini.getPrezzo(),  dettListini.getCodArt()));
		
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(MsgErr);

			throw new BindingException(MsgErr);
		}
		 
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();
		
		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		prezziService.InsPrezzo(dettListini);
		
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Adding Price " + dettListini.getPrezzo() + " Done Successfullyo");

		return new ResponseEntity<DettListini>(headers, HttpStatus.CREATED);
	}

}
