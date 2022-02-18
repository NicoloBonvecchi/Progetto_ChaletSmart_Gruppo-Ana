package it.unicam.cs.chaletsmart.persone;

import it.unicam.cs.chaletsmart.serviziospiaggia.*;

import java.util.Arrays;
import java.util.Date;

import it.unicam.cs.chaletsmart.serviziobar.*;

public class Cliente extends PersonaChaletSmart {

	public Cliente(String nome, String cognome, String dataNascita, String telefono) {
		super(nome, cognome, dataNascita, telefono);
	}

	/**
	 * 
	 * @param prenotazione
	 */
	public boolean prenotaSpiaggiaClienteSmart(TipoPrenotazione tipoPrenotazione, Ombrellone[] ombrelloniAssociati, int lettiniAssociati, Date giornoDellaPrenotazione, Spiaggia spiaggia) {
		Prenotazione prenotazione = new Prenotazione(
				this,
				tipoPrenotazione,
				ombrelloniAssociati,
				lettiniAssociati,
				giornoDellaPrenotazione);
		return spiaggia.aggiungiPrenotazione(prenotazione);
	}

	/**
	 * 
	 * @param attivita
	 */
	public boolean prenotaAttivitaClienteSmart(Attivita attivita) {
		return attivita.aggiungiPartecipante(this);
	}

	/**
	 * 
	 * @param ordinazione
	 */
	public Ordinazione creaOrdinazioneClienteSmart(Ombrellone ombrellone) {
		Ordinazione ordinazioneDaAggiungere=new Ordinazione(this, ombrellone);
		return ordinazioneDaAggiungere;
	}

	public boolean rimuoviPrenotazioneSpaggiaSpecificaClienteSmart(Prenotazione prenotazione, Spiaggia spiaggia)
	{
		return spiaggia.rimuoviPrenotazione(prenotazione);
	}
	
	public boolean rimuoviPrenotazioneAttivitaClienteSmart(Attivita attivita)
	{
		return attivita.rimuoviPartecipante(this);
	}
	public boolean rimuoviOrdinazioneClienteSmart(Ordinazione ordinazione, Spiaggia spiaggia)
	{
		return spiaggia.rimuoviOrdinazione(ordinazione);
	}

	@Override
	public String toString() {
		return "\nCliente [nome=" + getNome() + " ,cognome=" + getCognome() + " ,dataNascita=" + getDataN()
				+ " ,numeroTelefono=" + getTel() + " ,account=" + this.getAccount()
				+" ,listaNotifiche" + Arrays.toString(getListaNotificheOPromozioni())+"]";
	}	
}