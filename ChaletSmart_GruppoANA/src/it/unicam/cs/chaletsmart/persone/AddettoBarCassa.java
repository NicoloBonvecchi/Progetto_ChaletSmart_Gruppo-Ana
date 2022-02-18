package it.unicam.cs.chaletsmart.persone;

import it.unicam.cs.chaletsmart.serviziobar.*;
import it.unicam.cs.chaletsmart.serviziospiaggia.Ombrellone;
import it.unicam.cs.chaletsmart.serviziospiaggia.Spiaggia;

public class AddettoBarCassa extends PersonaChaletSmart {

	public AddettoBarCassa(String nome, String cognome, String dataNascita, String telefono) {
		super(nome, cognome, dataNascita, telefono);
	}

	
	public Ordinazione creaOrdinazionePerCliente(Ombrellone ombrellone, Cliente cliente) {
		Ordinazione ordinazioneDaAggiungere=new Ordinazione(cliente, ombrellone);
		return ordinazioneDaAggiungere;
	}

	public boolean rimuoviOrdinazionePerCliente(Ordinazione ordinazione, Spiaggia spiaggia) {
		return spiaggia.rimuoviOrdinazione(ordinazione);
	}


	@Override
	public String toString() {
		return "\nAddettoBarCassa [nome=" + getNome() + ", cognome=" + getCognome() + ", dataNasciata="
				+ getDataN() + ", telefono" + getTel() + ", account" + getAccount() + "]";
	}
	
}