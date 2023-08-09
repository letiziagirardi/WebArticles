package com.webapp.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.webapp.entities.DettListini;
import com.webapp.repository.PrezziRepository;

@Service
@Transactional
public class PrezziServiceImpl implements PrezziService
{
	@Autowired
	PrezziRepository prezziRepository;

	@Override
	@Cacheable(value = "chacheprezzo",key = "#CodArt",sync = true)
	public DettListini SelPrezzo(String CodArt, String Listino)
	{
		return prezziRepository.findByCodArtAndIdList(CodArt, Listino);
	}

	@Override
	@Caching(evict = { 
		@CacheEvict(cacheNames="chacheprezzo",key = "#dettListini.codArt")})
	public void InsPrezzo(DettListini dettListini) 
	{
		prezziRepository.save(dettListini);
	}

	@Override
	@Caching(evict = { 
		@CacheEvict(cacheNames="chacheprezzo",key = "#CodArt")})
	public void DelPrezzo(String CodArt, String IdList) 
	{
		prezziRepository.DelRowDettList(CodArt, IdList);
	}
}
