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
import com.webapp.entities.Promo;
import com.webapp.exception.BindingException;
import com.webapp.exception.NotFoundException;
import com.webapp.service.PromoService;

@RestController
@RequestMapping("/promo")
public class PromoController
{
	private static final Logger logger = LoggerFactory.getLogger(PromoController.class);

	@Autowired
	private PromoService promoService;	

	@Autowired
	private ResourceBundleMessageSource errMessage;
		
	@RequestMapping(value = "/cerca/tutti", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Promo>> listAllPromo()
			throws NotFoundException
	{           
		logger.info("****** Otteniamo tutte le promozioni *******");

		List<Promo> promo = promoService.SelTutti();

		if (promo.isEmpty())
		{
			String ErrMsg = String.format("Non è stata trovata alcuna promozione!");
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}

		logger.info("Numero dei record: " + promo.size());
		
		return new ResponseEntity<List<Promo>>(promo, HttpStatus.OK);
	}

}
