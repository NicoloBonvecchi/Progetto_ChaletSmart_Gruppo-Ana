package it.unicam.cs.chaletsmart.serviziospiaggia;

import java.util.ArrayList;
import java.util.Objects;

import it.unicam.cs.chaletsmart.persone.Cliente;

public class Attivita  {

	private String nomeIdentificativo;
	private int capienzaMax;
	private String orarioInizio;
	private String orarioFine;
	private String dataSvolgimento;
	private ArrayList<Cliente> partecipanti;

	/**
	 * 
	 * @param nomeIdentificativo
	 * @param capienzaMax
	 * @param orarioInizio
	 * @param orarioFine
	 * @param dataSvolgimento
	 */
	public Attivita(String nomeIdentificativo, int capienzaMax, String orarioInizio, String orarioFine, String dataSvolgimento) {
		this.nomeIdentificativo=nomeIdentificativo;
		this.capienzaMax=capienzaMax;
		this.orarioInizio=orarioInizio;
		this.orarioFine=orarioFine;
		this.dataSvolgimento=dataSvolgimento;
		this.partecipanti=new ArrayList<Cliente>();
	}

	public String getNomeIdentificativo() {
		return this.nomeIdentificativo;
	}

	/**
	 * 
	 * @param nomeIdentificativo
	 */
	public void setNomeIdentificativo(String nomeIdentificativo) {
		this.nomeIdentificativo = nomeIdentificativo;
	}

	public int getCapienzaMax() {
		return this.capienzaMax;
	}

	/**
	 * 
	 * @param capienzaMax
	 */
	public void setCapienzaMax(int capienzaMax) {
		this.capienzaMax = capienzaMax;
	}

	public String getOrarioInizio() {
		return this.orarioInizio;
	}

	/**
	 * 
	 * @param orarioInizio
	 */
	public void setOrarioInizio(String orarioInizio) {
		this.orarioInizio = orarioInizio;
	}

	public String getOrarioFine() {
		return this.orarioFine;
	}

	/**
	 * 
	 * @param orarioFine
	 */
	public void setOrarioFine(String orarioFine) {
		this.orarioFine = orarioFine;
	}

	public String getDataSvolgimento() {
		return this.dataSvolgimento;
	}

	/**
	 * 
	 * @param dataSvolgimento
	 */
	public void setDataSvolgimento(String dataSvolgimento) {
		this.dataSvolgimento = dataSvolgimento;
	}

	public ArrayList<Cliente> getPartecipanti() {
		return new ArrayList<>(this.partecipanti);
	}

	/**
	 * 
	 * @param partecipante
	 */
	public boolean aggiungiPartecipante(Cliente partecipante) {
		if(this.getPartecipanti().size()>=this.getCapienzaMax() || this.getPartecipanti().contains(partecipante))
			return false;
		this.partecipanti.add(partecipante);
		return true;
	}

	/**
	 * 
	 * @param partecipante
	 */
	public boolean rimuoviPartecipante(Cliente partecipante) {
		return this.partecipanti.remove(partecipante);
	}

	@Override
	public int hashCode() {
		return Objects.hash(capienzaMax, dataSvolgimento, nomeIdentificativo, orarioFine, orarioInizio);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attivita other = (Attivita) obj;
		return capienzaMax == other.capienzaMax && Objects.equals(dataSvolgimento, other.dataSvolgimento)
				&& Objects.equals(nomeIdentificativo, other.nomeIdentificativo)
				&& Objects.equals(orarioFine, other.orarioFine) && Objects.equals(orarioInizio, other.orarioInizio);
	}

	@Override
	public String toString() {
		return "\nAttivita [nomeIdentificativo=" + nomeIdentificativo + ", orarioInizio=" + orarioInizio + ", orarioFine="
				+ orarioFine + ", dataSvolgimento=" + dataSvolgimento + ", posti ancora disponibili="+(this.capienzaMax-this.partecipanti.size()) +"]";
	}
	
	

}