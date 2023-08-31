package com.webapp.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webapp.model.Utenti;

import com.webapp.service.UtentiService;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/utenti")
public class UtentiController
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	UtentiService utentiService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

  @ApiOperation(
				value = "Get all Users",
				response = Utenti.class,
				produces = "application/json")
	@ApiResponses(value =
	{ @ApiResponse(code = 200, message = "Search succeded"),
	@ApiResponse(code = 404, message = "Search not succeded")})
	@RequestMapping(value = "/cerca/tutti", method = RequestMethod.GET)
	public List<Utenti> getAllUser()
	{
		LOG.info("Getting all users!");

		return utentiService.SelTutti();
	}

	@ApiOperation("Get a User by userId")
	@ApiResponses(value =
		{ @ApiResponse(code = 200, message = "Search succeded"),
		@ApiResponse(code = 404, message = "Search not succeded")})
	@GetMapping(value = "/cerca/userid/{userId}")
	public Utenti getUtente(@PathVariable("userId") String UserId)
	{
		LOG.info("Getting user with userId " + UserId);

		Utenti retVal = utentiService.SelUser(UserId);

		return retVal;
	}


  @ApiOperation(
				value = "Insert a new user")
	@ApiResponses(value =
	{ @ApiResponse(code = 200, message = "Insert succeded"),
	@ApiResponse(code = 404, message = "Insert not succeded")})
  @PostMapping(value = "/inserisci")
	public ResponseEntity<Utenti> addNewUser(@Valid @RequestBody Utenti utente,
		BindingResult bindingResult)
	{
		LOG.info("Insert new User");

		if (bindingResult.hasErrors())
		{
			String MsgErr = "Error in Password validation";

			LOG.warn(MsgErr);
		}

		String encodedPassword = passwordEncoder.encode(utente.getPassword());
		utente.setPassword(encodedPassword);
		utentiService.Save(utente);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(utente.getId()).toUri();

		return ResponseEntity.created(location).build();
	}

  @ApiOperation(
				value = "Deleting user with ID userId")
	@ApiResponses(value =
	{ @ApiResponse(code = 200, message = "User Found"),
	@ApiResponse(code = 404, message = "User not Found")})
	@DeleteMapping(value = "/elimina/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") String UserId)
	{
		LOG.info("Deleting User with UserId " + UserId);

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		Utenti utente = utentiService.SelUser(UserId);

		if (utente == null)
		{
			String MsgErr = String.format("User %s not present! ",UserId);

			LOG.warn(MsgErr);

		}

		utentiService.Delete(utente);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Delete User " + UserId + " Done");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}
}
