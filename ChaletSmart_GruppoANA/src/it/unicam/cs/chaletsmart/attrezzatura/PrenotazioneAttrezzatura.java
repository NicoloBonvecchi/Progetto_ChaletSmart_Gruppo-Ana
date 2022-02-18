package it.unicam.cs.chaletsmart.attrezzatura;

import java.util.Objects;

import it.unicam.cs.chaletsmart.persone.*;

public class PrenotazioneAttrezzatura {

	private int idTipoAttrezzatura;
	private String oraInizioPrenotazione;
	/**
	 * L'ora della restituzione non sarà specificata quando si prenotano le attrezzature, bensì solo al momento della restituzione di quest'ultime.
	 */
	private String oraRestituzione;
	private AddettoAttivita addettoAttivita;
	private int quantitaAttrezzaturaPrenotata;

	/**
	 * 
	 * @param idTipoAttrezzatura
	 * @param quantitaAttrezzaturaPrenotata
	 * @param oraInizioPrenotazione
	 * @param addettoAttivita
	 */
	public PrenotazioneAttrezzatura(int idTipoAttrezzatura, int quantitaAttrezzaturaPrenotata, String oraInizioPrenotazione, AddettoAttivita addettoAttivita) {
		this.idTipoAttrezzatura=idTipoAttrezzatura;
		this.oraInizioPrenotazione=oraInizioPrenotazione;
		this.addettoAttivita=addettoAttivita;
		this.quantitaAttrezzaturaPrenotata=quantitaAttrezzaturaPrenotata;
	}

	

	public int getIdTipoAttrezzatura() {
		return this.idTipoAttrezzatura;
	}

	/**
	 * 
	 * @param idTipoAttrezzatura
	 */
	public void setIdTipoAttrezzatura(int idTipoAttrezzatura) {
		this.idTipoAttrezzatura = idTipoAttrezzatura;
	}

	public int getQuantitaAttrezzaturaPrenotata() {
		return this.quantitaAttrezzaturaPrenotata;
	}

	/**
	 * 
	 * @param quantitaAttrezzaturaPrenotata
	 */
	public void setQuantitaAttrezzaturaPrenotata(int quantitaAttrezzaturaPrenotata) {
		this.quantitaAttrezzaturaPrenotata = quantitaAttrezzaturaPrenotata;
	}

	public String getOraInizioPrenotazione() {
		return this.oraInizioPrenotazione;
	}

	/**
	 * 
	 * @param oraInizioPrenotazione
	 */
	public void setOraInizioPrenotazione(String oraInizioPrenotazione) {
		this.oraInizioPrenotazione = oraInizioPrenotazione;
	}

	public String getOraRestituzione() {
		return this.oraRestituzione;
	}

	/**
	 * 
	 * @param oraRestituzione
	 */
	public void setOraRestituzione(String oraRestituzione) {
		this.oraRestituzione = oraRestituzione;
	}

	public AddettoAttivita getAddettoAttivita() {
		return this.addettoAttivita;
	}

	/**
	 * 
	 * @param addettoAttivita
	 */
	public void setAddettoAttivita(AddettoAttivita addettoAttivita) {
		this.addettoAttivita = addettoAttivita;
	}

	@Override
	public int hashCode() {
		return Objects.hash(addettoAttivita, idTipoAttrezzatura, oraInizioPrenotazione, quantitaAttrezzaturaPrenotata);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrenotazioneAttrezzatura other = (PrenotazioneAttrezzatura) obj;
		return Objects.equals(addettoAttivita, other.addettoAttivita) && idTipoAttrezzatura == other.idTipoAttrezzatura
				&& Objects.equals(oraInizioPrenotazione, other.oraInizioPrenotazione)
				&& quantitaAttrezzaturaPrenotata == other.quantitaAttrezzaturaPrenotata;
	}



	@Override
	public String toString() {
		return "\nPrenotazioneAttrezzatura [idTipoAttrezzatura=" + idTipoAttrezzatura + ",oraInizioPrenotazione="
				+ oraInizioPrenotazione + ",oraRestituzione=" + oraRestituzione  + ",quantitaAttrezzaturaPrenotata=" + quantitaAttrezzaturaPrenotata +
				", addettoAttivita="+this.addettoAttivita.getNome()+","+addettoAttivita.getCognome()+"]";
	}
	
}