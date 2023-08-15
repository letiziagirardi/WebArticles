package com.webapp.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


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

	/*
	 * this method is responsible for fetching the price of an article from an external service
	 * using the provided URL, article code, and authentication credentials.
	 * It uses Spring's RestTemplate for making the HTTP request and extracting the price from
	 * the response. The method returns the fetched price as a Double.
	 */
	@Value("${price.service.url}")
	private String priceServiceUrl;

	private Double getPriceArt(String CodArt, String[] Credentials)
	{

		String ServiceUri = priceServiceUrl + CodArt;

		logger.info("****** Richiesto prezzo al servizio " + ServiceUri + " *******");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(Credentials[0], Credentials[1]));

		ResponseEntity<Double> responseEntity = restTemplate.getForEntity(ServiceUri, Double.class);

		Double Prezzo = responseEntity.getBody();

		logger.info("Prezzo Articolo " + CodArt + ": " + Prezzo);

		return Prezzo;
	}

	private String[] getHttpCredentials(String authorization)
	{
		String[] values = null;

		if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
			// Authorization: Basic base64credentials
			String base64Credentials = authorization.substring("Basic".length()).trim();
			byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
			String credentials = new String(credDecoded, StandardCharsets.UTF_8);
			// credentials = username:password
			values = credentials.split(":", 2);
		}

		return values;
	}

		// ------------------- Search By BarCode Ean ------------------------------------
		/*
		 * This method defines a REST API endpoint that receives a barcode, retrieves information about an article
		 * using that barcode, calculates its price based on the provided credentials, and returns the article information
		 * along with its calculated price as an HTTP response.
		 */
		@ApiOperation(
			      value = "Ricerca l'articolo per codice a barre",
			      notes = "Restituisce i dati dell'articolo in formato JSON",
			      response = Articoli.class,
			      produces = "application/json")
		@ApiResponses(value =
		{ @ApiResponse(code = 200, message = "Articolo Trovato"),
		  @ApiResponse(code = 404, message = "Articolo Non Trovato")})
		@RequestMapping(value = "/cerca/ean/{barcode}", method = RequestMethod.GET, produces = "application/json")
		public ResponseEntity<Articoli> listArtByEan(@ApiParam("Barcode univoco dell'articolo") @PathVariable("barcode") String Barcode,
			HttpServletRequest httpRequest)
				throws NotFoundException
		{
			logger.info("****** Otteniamo l'articolo con barcode " + Barcode + " *******");

			String[] credentials = getHttpCredentials(httpRequest.getHeader("Authorization"));

			Articoli articolo;
			Barcode Ean = barcodeService.SelByBarcode(Barcode);

			if (Ean == null)
			{
				String ErrMsg = String.format("Il barcode %s non è stato trovato!", Barcode);

				logger.warn(ErrMsg);

				throw new NotFoundException(ErrMsg);
				//return new ResponseEntity<Articoli>(HttpStatus.NOT_FOUND);
			}
			else
			{
				articolo = Ean.getArticolo();

				articolo.setPrezzo(this.getPriceArt(articolo.getCodArt(), credentials));
			}

			return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);
		}


		// ------------------- Search By CodArt ------------------------------------
		@ApiOperation(
			      value = "Ricerca l'articolo per codice CodArt",
			      notes = "Restituisce i dati dell'articolo in formato JSON",
			      response = Articoli.class,
			      produces = "application/json")
		@ApiResponses(value =
		{ @ApiResponse(code = 200, message = "Articolo Trovato"),
		  @ApiResponse(code = 404, message = "Articolo Non Trovato")})
		@RequestMapping(value = "/cerca/codice/{codart}", method = RequestMethod.GET, produces = "application/json")
		public ResponseEntity<Articoli> listArtByCodArt(@PathVariable("codart") String CodArt,
			HttpServletRequest httpRequest)
				throws NotFoundException
		{
			logger.info("****** Otteniamo l'articolo con codice " + CodArt + " *******");
			
			String[] credentials = getHttpCredentials(httpRequest.getHeader("Authorization"));

			Articoli articolo = articoliService.SelByCodArt(CodArt);

			if (articolo == null)
			{
				String ErrMsg = String.format("L'articolo con codice %s non è stato trovato!", CodArt);
				
				logger.warn(ErrMsg);
				
				throw new NotFoundException(ErrMsg);
			}
			else
			{

				articolo.setPrezzo(this.getPriceArt(articolo.getCodArt(), credentials));
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
		@ApiOperation(
			      value = "Ricerca l'articolo per descrizione",
			      notes = "Restituisce i dati dell'articolo in formato JSON",
			      response = Articoli.class,
			      produces = "application/json")
		@ApiResponses(value =
		{ @ApiResponse(code = 200, message = "Articolo Trovato"),
		  @ApiResponse(code = 404, message = "Articolo Non Trovato")})
		@RequestMapping(value = "/cerca/descrizione/{filter}", method = RequestMethod.GET, produces = "application/json")
		public ResponseEntity<List<Articoli>> listArtByDesc(@PathVariable("filter") String Filter,
			HttpServletRequest httpRequest)
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
		@ApiOperation(
			      value = "Ricerca l'articolo per descrizione",
			      notes = "Restituisce i dati dell'articolo in formato JSON",
			      response = Articoli.class,
			      produces = "application/json")
		@ApiResponses(value =
		{ @ApiResponse(code = 200, message = "Articolo Trovato"),
		  @ApiResponse(code = 404, message = "Articolo Non Trovato")})
		@RequestMapping(value = "/inserisci", method = RequestMethod.POST)
		public ResponseEntity<Articoli> createArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult,
			UriComponentsBuilder ucBuilder, HttpServletRequest httpRequest)
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
				String MsgErr = String.format("Articolo %s presente in anagrafica! "
						+ "Impossibile utilizzare il metodo POST", articolo.getCodArt());
				
				logger.warn(MsgErr);
				
				throw new DuplicateException(MsgErr);
			}
			
			HttpHeaders headers = new HttpHeaders();
			ObjectMapper mapper = new ObjectMapper();
			
			headers.setContentType(MediaType.APPLICATION_JSON);

			ObjectNode responseNode = mapper.createObjectNode();

			articoliService.InsArticolo(articolo);
			
			responseNode.put("code", HttpStatus.OK.toString());
			responseNode.put("message", "Inserimento Articolo " + articolo.getCodArt() + " Eseguita Con Successo");

			return new ResponseEntity<Articoli>(headers, HttpStatus.CREATED);
			}


			
}
