package it.unicam.cs.chaletsmart.persone;

import it.unicam.cs.chaletsmart.serviziospiaggia.*;

public class AddettoPrenotazioni extends PersonaChaletSmart {

	public AddettoPrenotazioni(String nome, String cognome, String dataNascita, String telefono) {
		super(nome, cognome, dataNascita, telefono);
	}


	public boolean prenotaSpiaggiaPerCliente(Prenotazione prenotazione, Spiaggia spiaggia) {
		return spiaggia.aggiungiPrenotazione(prenotazione);
	}
	
	public boolean rimuoviPrenotazioneSpiaggiaPerCliente(Prenotazione prenotazione, Spiaggia spiaggia)
	{
		return spiaggia.rimuoviPrenotazione(prenotazione);
	}


	@Override
	public String toString() {
		return "\nAddettoPrenotazioni [nome=" + getNome() + ", cognome=" + getCognome() + ", dataNasciata="
				+ getDataN() + ", telefono" + getTel() + ", account" + getAccount() + "]";
	}
	
}