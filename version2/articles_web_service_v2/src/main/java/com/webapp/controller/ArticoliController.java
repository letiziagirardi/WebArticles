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

		// ------------------- Search By Code ------------------------------------
		/*
		 * this code is an API endpoint that handles HTTP GET requests to retrieve a single item from a database
		 * based on its unique code.
		 * If the item with the provided code is found, it returns the item in JSON format as the response with
		 * status code 200 OK. If the item is not found, it throws a NotFoundException with an appropriate error message.
		 */
		@RequestMapping(value = "/cerca/codice/{codart}", method = RequestMethod.GET, produces = "application/json")
		public ResponseEntity<Articoli> listArtByCodArt(@PathVariable("codart") String CodArt)
				throws NotFoundException
		{
			logger.info("****** We get the article with code " + CodArt + " *******");

			Articoli articolo = articoliService.SelByCodArt(CodArt);

			if (articolo == null)
			{
				String ErrMsg = String.format("The article with code %s was not found!", CodArt);

				logger.warn(ErrMsg);

				throw new NotFoundException(ErrMsg);
			}

			return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);
		}

		// ------------------- Search By Description ------------------------------------
		/*
			* this code is an API endpoint that handles HTTP GET requests to retrieve a list of items from a database
			* based on their description. It retrieves all matching items in one request.
			* If no items are found for the provided description filter, it throws a NotFoundException.
			* Otherwise, it returns the list of items in JSON format as the response.
			*/
		@RequestMapping(value = "/cerca/descrizione/{filter}", method = RequestMethod.GET, produces = "application/json")
		public ResponseEntity<List<Articoli>> listArtByDesc(@PathVariable("filter") String Filter)
				throws NotFoundException
		{
			logger.info("****** We get the items with Description: " + Filter + " *******");

			List<Articoli> articoli = articoliService.SelByDescrizione(Filter + "%");

			if (articoli == null)
			{
				String ErrMsg = String.format("No item with description %s was found", Filter);

				logger.warn(ErrMsg);

				throw new NotFoundException(ErrMsg);

			}

			return new ResponseEntity<List<Articoli>>(articoli, HttpStatus.OK);
		}

		// ------------------- Create new Articles ------------------------------------
		/*
		 * this code is an API endpoint that handles the creation of new articles via 
		 * an HTTP POST request, ensuring validation, duplicate checks, database interaction, 
		 * and generating appropriate JSON responses.
		 */
		@RequestMapping(value = "/inserisci", method = RequestMethod.POST)
		public ResponseEntity<Articoli> createArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult,
				UriComponentsBuilder ucBuilder) 
				throws BindingException, DuplicateException
		{
			logger.info("Saving Article with code " + articolo.getCodArt());
			
			if (bindingResult.hasErrors())
			{
				String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
				
				logger.warn(MsgErr);

				throw new BindingException(MsgErr);
			}
			
			Articoli checkArt =  articoliService.SelByCodArt(articolo.getCodArt());

			if (checkArt != null)
			{
				String MsgErr = String.format("Article %s present in the registry! "
						+ "Cannot use POST method", articolo.getCodArt());
				
				logger.warn(MsgErr);
				
				throw new DuplicateException(MsgErr);
			}
			
			HttpHeaders headers = new HttpHeaders();
			ObjectMapper mapper = new ObjectMapper();
			
			headers.setContentType(MediaType.APPLICATION_JSON);

			ObjectNode responseNode = mapper.createObjectNode();

			articoliService.InsArticolo(articolo);
			
			responseNode.put("code", HttpStatus.OK.toString());
			responseNode.put("message", "Posting Article " + articolo.getCodArt() + " Executed Successfully");

			return new ResponseEntity<Articoli>(headers, HttpStatus.CREATED);
		}	


	// ------------------- MODIFICA ARTICOLO ------------------------------------
	@RequestMapping(value = "/modifica", method = RequestMethod.PUT)
	public ResponseEntity<Articoli> updateArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult,
				UriComponentsBuilder ucBuilder) throws BindingException,NotFoundException  
	{
		logger.info("Modifichiamo l'articolo con codice " + articolo.getCodArt());
		
		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			logger.warn(MsgErr);

			throw new BindingException(MsgErr);
		}
		
		Articoli checkArt =  articoliService.SelByCodArt(articolo.getCodArt());

		if (checkArt == null)
		{
			String MsgErr = String.format("Articolo %s non presente in anagrafica! "
					+ "Impossibile utilizzare il metodo PUT", articolo.getCodArt());
			
			logger.warn(MsgErr);
			
			throw new NotFoundException(MsgErr);
		}
		
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();
		
		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		articoliService.InsArticolo(articolo);
		
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Modifica Articolo " + articolo.getCodArt() + " Eseguita Con Successo");

		return new ResponseEntity<Articoli>(headers, HttpStatus.CREATED);
	}

}
