package com.webapp.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.webapp.entities.Barcode;

public interface BarcodeRepository extends JpaRepository<Barcode, Integer>
{
	Barcode findByBarcode(String Barcode);
}
