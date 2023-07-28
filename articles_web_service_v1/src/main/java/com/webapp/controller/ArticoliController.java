package com.webapp.controller;

import java.util.List;

import javax.validation.Valid;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webapp.entities.Articoli;
import com.webapp.entities.Barcode;
import com.webapp.exception.BindingException;
import com.webapp.exception.DuplicateException;
import com.webapp.exception.NotFoundException;
import com.webapp.service.ArticoliService;
import com.webapp.service.BarcodeService;

@RestController
@RequestMapping("/articoli")
public class ArticoliController
{
	private static final Logger logger = LoggerFactory.getLogger(ArticoliController.class);

	@Autowired
	private BarcodeService barcodeService;

	@Autowired
	private ArticoliService articoliService;

	@Autowired
	private ResourceBundleMessageSource errMessage;

	// ------------------- Search By EAN ------------------------------------
	/*
	 * this code is an API endpoint that handles HTTP GET requests to retrieve a single item from a database
	 * based on its unique barcode (EAN). If a barcode record with the provided barcode is found,
	 * it retrieves the associated item and returns it in JSON format as the response with status code 200 OK.
	 * If the barcode is not found in the database, it throws a NotFoundException with an appropriate error message.
	 */
	@RequestMapping(value = "/cerca/ean/{barcode}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Articoli> listArtByEan(@PathVariable("barcode") String Barcode)
		throws NotFoundException
	{
		logger.info("****** Let's retrieve the article using the barcode number " + Barcode + " *******");

		Articoli articolo;
		Barcode Ean = barcodeService.SelByBarcode(Barcode);

		if (Ean == null)
		{
			String ErrMsg = String.format("The barcode %s was not found!", Barcode);

			logger.warn(ErrMsg);

			throw new NotFoundException(ErrMsg);
		}
		else
			articolo = Ean.getArticolo();

		return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);
	}

}
