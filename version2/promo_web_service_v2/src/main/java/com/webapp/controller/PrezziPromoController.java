package com.webapp.controller;

import java.util.List;
import java.util.OptionalDouble;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.entities.DettPromo;
import com.webapp.entities.Promo;
import com.webapp.service.DettPromoService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.webapp.exception.NotFoundException;

@RestController
@RequestMapping("/prezzo")
public class PrezziPromoController
{

	@Autowired
	private DettPromoService dettPromoService;

	/*
	 * The 'getPricePromoCodArt' method calculates and retrieves the minimum promotional price for a specific product
	 * based on its code. It does this by fetching detailed promotional information, filtering the relevant data,
	 * and calculating the minimum price.
	 */

	private static final Logger logger = LoggerFactory.getLogger(PrezziPromoController.class);

// ------------------- Search By CodArt ------------------------------------
	@ApiOperation(
		value = "Ricerca l'articolo per codice CodArt",
		notes = "Restituisce i dati dell'articolo in formato JSON",
		response = Promo.class,
		produces = "application/json")
	@ApiResponses(value =
	{ @ApiResponse(code = 200, message = "Articolo Trovato"),
		@ApiResponse(code = 404, message = "Articolo Non Trovato")})
	@RequestMapping(value = "/promo/codice/{codart}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<OptionalDouble> listArtByCodArt(@PathVariable("codart") String CodArt,
		HttpServletRequest httpRequest)
			throws NotFoundException
	{

		logger.info("****** Otteniamo l'articolo con codice " + CodArt + " *******");

		OptionalDouble Prezzo = null;

		List<DettPromo> dettPromo = dettPromoService.SelDettPromoByCode(CodArt);

		if (dettPromo != null)
		{
			Prezzo = dettPromo.stream()
					.filter(v -> v.getCodfid() == null)
					.mapToDouble(v -> Double.parseDouble(v.getOggetto().replace(",", "."))).min();

			logger.info("Prezzo Promo Articolo: " + Prezzo);

		}else{
			Prezzo = null;
		}
		return new ResponseEntity<OptionalDouble>(Prezzo, HttpStatus.OK);
		}
	}
