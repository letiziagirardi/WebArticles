package com.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.entities.Barcode;
import com.webapp.repository.BarcodeRepository;

@Service
@Transactional(readOnly = true)
public class BarcodeServiceImpl implements BarcodeService
{

	@Autowired
	BarcodeRepository barcodeRepository;
	
	@Override
	public Barcode SelByBarcode(String Barcode)
	{
		return barcodeRepository.findByBarcode(Barcode);
	}

}
