package com.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.entities.Articoli;
import com.webapp.repository.ArticoliRepository;
 

@Service
@Transactional(readOnly = true)
public class ArticoliServiceImpl implements ArticoliService
{
	@Autowired
	ArticoliRepository articoliRepository;

	@Override
	public Iterable<Articoli> SelTutti()
	{
		return articoliRepository.findAll();
	}

	@Override
	//@Cacheable("SelByDescrizione")
	public List<Articoli> SelByDescrizione(String descrizione, Pageable pageable)
	{
		return articoliRepository.findByDescrizioneLike(descrizione, pageable);
	}

	@Override
	@Cacheable(value = "articolicache", sync = true)
	public List<Articoli> SelByDescrizione(String descrizione)
	{
		return articoliRepository.findByDescrizioneLike(descrizione);
	}
	
	@Override
	@Cacheable(value = "articolo",key = "#codArt",sync = true)
	public Articoli SelByCodArt(String codArt)
	{
		return articoliRepository.findByCodArt(codArt);
	}

	@Override
	@Transactional
	@Caching(evict = { 
		@CacheEvict(cacheNames="articolicache", allEntries = true),
		@CacheEvict(cacheNames="articolo",key = "#articolo.codArt")})
	public void DelArticolo(Articoli articolo)
	{
		articoliRepository.delete(articolo);
	}

	@Override
	@Transactional
	@Caching(evict = { 
		@CacheEvict(cacheNames="articolicache", allEntries = true),
		@CacheEvict(cacheNames="articolo",key = "#articolo.codArt")})
	public void InsArticolo(Articoli articolo)
	{
		articoliRepository.save(articolo);
	}

}
