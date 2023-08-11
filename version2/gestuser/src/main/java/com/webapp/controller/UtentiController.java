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
 
@RestController
@RequestMapping(value = "/utenti")
public class UtentiController
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	UtentiService utentiService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping(value = "/cerca/tutti", method = RequestMethod.GET)
	public List<Utenti> getAllUser()
	{
		LOG.info("Otteniamo tutti gli utenti");

		return utentiService.SelTutti();
	}
	
	@GetMapping(value = "/cerca/userid/{userId}")
	public Utenti getUtente(@PathVariable("userId") String UserId) 
	{
		LOG.info("Otteniamo l'utente " + UserId);
		
		Utenti retVal = utentiService.SelUser(UserId);
		
		return retVal;
	}
	
	@PostMapping(value = "/inserisci")
	public ResponseEntity<Utenti> addNewUser(@Valid @RequestBody Utenti utente, 
		BindingResult bindingResult)
	{
		LOG.info("Inserimento Nuovo Utente");

		if (bindingResult.hasErrors())
		{
			String MsgErr = "Errore Validazione Password";
			
			LOG.warn(MsgErr);

			//throw new BindingException(MsgErr);
		}
		
		String encodedPassword = passwordEncoder.encode(utente.getPassword());
		utente.setPassword(encodedPassword);
		utentiService.Save(utente);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(utente.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}

	// ------------------- ELIMINAZIONE UTENTE ------------------------------------
	@DeleteMapping(value = "/elimina/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") String UserId)
	{
		LOG.info("Eliminiamo l'utente con id " + UserId);

		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode responseNode = mapper.createObjectNode();

		Utenti utente = utentiService.SelUser(UserId);

		if (utente == null)
		{
			String MsgErr = String.format("Utente %s non presente in anagrafica! ",UserId);
			
			LOG.warn(MsgErr);
			
			//throw new NotFoundException(MsgErr);
		}

		utentiService.Delete(utente);

		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Eliminazione Utente " + UserId + " Eseguita Con Successo");

		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}
}
