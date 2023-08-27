package com.webapp.service;

import com.webapp.entities.DettListini;
 
public interface PrezziService
{
	public DettListini SelPrezzo(String CodArt, String Listino);

	public void InsPrezzo(DettListini dettListini);

}
