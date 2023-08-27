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

	@Value("${price.service.url}")
	private String priceServiceUrl;

	@Value("${promo.service.url}")
	private String promoServiceUrl;

	/*
	 * The getPriceArt method is responsible for retrieving the price of an article
	 * from an external service using a RESTful HTTP GET request.
	 * It returns a Double value representing the price of the article.
	 *  */
	private Double getPriceArt(String CodArt, String[] Credentials)
	{

		Double Prezzo ;
		try{
		String ServiceUri = priceServiceUrl + CodArt;

		logger.info("****** Asking price to service " + ServiceUri + " *******");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(Credentials[0], Credentials[1]));

		ResponseEntity<Double> responseEntity = restTemplate.getForEntity(ServiceUri, Double.class);

		Prezzo = responseEntity.getBody();

		logger.info("Price of Article " + CodArt + ": " + Prezzo);
		} catch(Throwable eThrowable){
				logger.error("Error in priceart: ", eThrowable);
				throw eThrowable;
			}

		return Prezzo;
	}

	/*
	 * The method getHttpCredentials extracts and decodes the basic authentication credentials
	 * from an Authorization header.
	 */
	// ------------------- Authorization ------------------------------------
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

	// ------------------- Search By BarCode Ean ------------------------------------ OK
    /*
	 * This method handles the HTTP GET request for retrieving article information based on a barcode.
    */
    @ApiOperation(
                value = "Search details about Article with code codArt",
                notes = "Returns article data in JSON format",
                response = Articoli.class,
                produces = "application/json")
    @ApiResponses(value =
    { @ApiResponse(code = 200, message = "Article  found"),
        @ApiResponse(code = 404, message = "Article not found")})
    @RequestMapping(value = "/cerca/codice/{codart}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Articoli> listArtByCodArt(@ApiParam("unique codart") @PathVariable("codart") String CodArt,
        HttpServletRequest httpRequest)
            throws NotFoundException
    {
		Articoli articolo = null;

		try{

			logger.info("****** Getting the best promo active on the article with codArt " + CodArt + " *******");

			String[] credentials = getHttpCredentials(httpRequest.getHeader("Authorization"));
			logger.info("credentials:" + credentials);

			articolo = articoliService.SelByCodArt(CodArt);

			if (articolo == null)
			{
				String ErrMsg = String.format("Article %s not found!", CodArt);

				logger.warn(ErrMsg);

				throw new NotFoundException(ErrMsg);
			}
			else
			{

				articolo.setPrezzo(this.getPriceArt(articolo.getCodArt(), credentials));
			}
		} catch(Throwable eThrowable){
				logger.error("Error in priceart: ", eThrowable);
				throw eThrowable;
			}

		return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);

    }

	// ------------------- Search By BarCode Ean ------------------------------------ OK
    /*
	 * This method handles the HTTP GET request for retrieving article information based on a barcode.
    */
    @ApiOperation(
                value = "Search details about Article with barcode EAN",
                notes = "Returns article data in JSON format",
                response = Articoli.class,
                produces = "application/json")
    @ApiResponses(value =
    { @ApiResponse(code = 200, message = "Article found"),
    @ApiResponse(code = 404, message = "Article not found")})

	@RequestMapping(value = "/cerca/ean/{barcode}", method = RequestMethod.GET, produces = "application/json")
		public ResponseEntity<Articoli> listArtByEan(@ApiParam("Barcode") @PathVariable("barcode") String Barcode,
			HttpServletRequest httpRequest)
				throws NotFoundException
		{
			logger.info("****** Getting Article with Barcode " + Barcode + " *******");

			String[] credentials = getHttpCredentials(httpRequest.getHeader("Authorization"));

			Articoli articolo;
			Barcode Ean = barcodeService.SelByBarcode(Barcode);

			if (Ean == null)
			{
				String ErrMsg = String.format("Barcode %s not found!", Barcode);

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


	// ------------------- Search By Description ------------------------------------
	/*
		* this code is an API endpoint that handles HTTP GET requests to retrieve a list of items from a database
		* based on their description. It retrieves all matching items in one request.
		* If no items are found for the provided description filter, it throws a NotFoundException.
		* Otherwise, it returns the list of items in JSON format as the response.
	*/
	@ApiOperation(
				value = "Search of article by description",
				notes = "Returns article data in JSON format",
				response = Articoli.class,
				produces = "application/json")
	@ApiResponses(value =
	{ @ApiResponse(code = 200, message = "Article found"),
	@ApiResponse(code = 404, message = "Article not found")})

	@RequestMapping(value = "/cerca/descrizione/{filter}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Articoli>> listArtByDesc(@PathVariable("filter") String Filter, HttpServletRequest httpRequest)
			throws NotFoundException
	{
		logger.info("****** Getting articles with description: " + Filter + " *******");

		String[] credentials = getHttpCredentials(httpRequest.getHeader("Authorization"));

		List<Articoli> articoli = articoliService.SelByDescrizione(Filter + "%");

		if (articoli == null)
		{
			String ErrMsg = String.format("No article with description %s found", Filter);

			logger.warn(ErrMsg);

			throw new NotFoundException(ErrMsg);
		}
		else
		{
			articoli.forEach(f -> f.setPrezzo(this.getPriceArt(f.getCodArt(),credentials)));
		}

		return new ResponseEntity<List<Articoli>>(articoli, HttpStatus.OK);
	}


	// ------------------- New Article ------------------------------------
    /*
	 * This method handles the HTTP POST request for creating a new article.
    */
    @ApiOperation(
                value = "Create new Article")
    @ApiResponses(value =
    { @ApiResponse(code = 200, message = "Article  insert"),
        @ApiResponse(code = 404, message = "Article not insert")})

		@RequestMapping(value = "/inserisci", method = RequestMethod.POST)
	public ResponseEntity<Articoli> createArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult,
			UriComponentsBuilder ucBuilder)
			throws BindingException, DuplicateException
	{
		logger.info("Insert article with codArt " + articolo.getCodArt());

		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());

			logger.warn(MsgErr);

			throw new BindingException(MsgErr);
		}

		Articoli checkArt =  articoliService.SelByCodArt(articolo.getCodArt());

		if (checkArt != null)
		{
			String MsgErr = String.format("Article %s found! Duplicate "
					+ "Impossible POST method", articolo.getCodArt());

			logger.warn(MsgErr);

			throw new DuplicateException(MsgErr);
		}

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		articoliService.InsArticolo(articolo);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Insert Article " + articolo.getCodArt() + " Done");

		return new ResponseEntity<Articoli>(headers, HttpStatus.CREATED);
	}

// ------------------- Change existing article ------------------------------------ OK
    /*
	 * This method handles the HTTP PUT request for modifying data of an existing article.
    */
    @ApiOperation(
                value = "Modify data of existing article")
    @ApiResponses(value =
    { @ApiResponse(code = 200, message = "Article modified"),
        @ApiResponse(code = 404, message = "Article not modified")})

		@RequestMapping(value = "/modifica", method = RequestMethod.PUT)
	public ResponseEntity<Articoli> updateArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult,
				UriComponentsBuilder ucBuilder) throws BindingException,NotFoundException
	{
		logger.info("Change article with code " + articolo.getCodArt());

		if (bindingResult.hasErrors())
		{
			String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());

			logger.warn(MsgErr);

			throw new BindingException(MsgErr);
		}

		Articoli checkArt =  articoliService.SelByCodArt(articolo.getCodArt());

		if (checkArt == null)
		{
			String MsgErr = String.format("Article %s not found! "
					+ "Impossible to use PUT method", articolo.getCodArt());

			logger.warn(MsgErr);

			throw new NotFoundException(MsgErr);
		}

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		articoliService.InsArticolo(articolo);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Change Article " + articolo.getCodArt() + " Done");

		return new ResponseEntity<Articoli>(headers, HttpStatus.CREATED);
	}
	// ------------------- Remove existing article ------------------------------------ OK
    /*
	 * This method handles the HTTP PUT request for removing an existing article.
    */
    @ApiOperation(
                value = "Remove an existing article")
    @ApiResponses(value =
    { @ApiResponse(code = 200, message = "Article removed"),
        @ApiResponse(code = 404, message = "Article not removed")})

		@RequestMapping(value = "/elimina/{codart}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteArt(@PathVariable("codart") String CodArt)
			throws  NotFoundException
	{
		logger.info("Remove article with codArt " + CodArt);

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		Articoli articolo = articoliService.SelByCodArt(CodArt);

		if (articolo == null)
		{
			String MsgErr = String.format("Article %s not found! ",CodArt);

			logger.warn(MsgErr);

			throw new NotFoundException(MsgErr);
		}

		articoliService.DelArticolo(articolo);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Remove article " + CodArt + " Done");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}

}
