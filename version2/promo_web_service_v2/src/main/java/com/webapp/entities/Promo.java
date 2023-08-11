package com.webapp.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;
 
import lombok.Data;

@Entity
@Table(name = "PROMO")
@Data
public class Promo implements Serializable
{
	private static final long serialVersionUID = -5905631309290304849L;
	
	@Id
	@Column(name = "IDPROMO")
	@NotNull(message = "{NotNull.Promo.idPromo.Validation}")
	@Size(min = 1, max = 20, message = "{Size.Promo.idPromo.Validation}")
	private String idPromo;
	
	@Column(name = "ANNO")
	private int anno;
	
	@Column(name = "CODICE")
	@NotNull(message = "{NotNull.Promo.codice.Validation}")
	private String codice;
	
	@Column(name = "DESCRIZIONE")
	@Size(min = 6, max = 80, message = "{Size.Promo.descrizione.Validation}")
	private String descrizione;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,  mappedBy = "promo", orphanRemoval = true)
	@JsonManagedReference
	private List<DettPromo> dettPromo;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,  mappedBy = "promo", orphanRemoval = true)
	@JsonManagedReference
	private Set<DepRifPromo> depRifPromo = new HashSet<>();
	
}
