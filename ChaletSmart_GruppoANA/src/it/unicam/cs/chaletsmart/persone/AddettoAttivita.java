package it.unicam.cs.chaletsmart.persone;

import java.util.ArrayList;
import java.util.Arrays;

import it.unicam.cs.chaletsmart.attrezzatura.*;
import it.unicam.cs.chaletsmart.serviziospiaggia.*;

public class AddettoAttivita extends PersonaChaletSmart {
	
	private ArrayList<PrenotazioneAttrezzatura> listaPrenotazioniAttrezzatura;

	public AddettoAttivita(String nome, String cognome, String dataNascita, String telefono) {
		super(nome, cognome, dataNascita, telefono);
		listaPrenotazioniAttrezzatura=new ArrayList<PrenotazioneAttrezzatura>();
	}


	public ArrayList<PrenotazioneAttrezzatura> getListaPrenotazioniAttrezzatura() {
		return new ArrayList<>(this.listaPrenotazioniAttrezzatura);
	}

	/**
	 * 
	 * @param listaPrenotazioniAttrezzatura
	 */
	public void setListaPrenotazioniAttrezzatura(PrenotazioneAttrezzatura[] listaPrenotazioniAttrezzatura) {
		this.listaPrenotazioniAttrezzatura.clear();
		this.listaPrenotazioniAttrezzatura.addAll(Arrays.asList(listaPrenotazioniAttrezzatura));
	}

	/**
	 * 
	 * @param attrezzatura
	 * @param oraInizioPrenotazione
	 */
	public void aggiungiPrenotazioneAttrezzatura(PrenotazioneAttrezzatura prenotazioneAttrezzatura) {
		this.listaPrenotazioniAttrezzatura.add(prenotazioneAttrezzatura);
	}

	/**
	 * 
	 * @param attrezzatura
	 */
	public boolean rimuoviPrenotazioneAttrezzatura(PrenotazioneAttrezzatura prenotazioneAttrezzatura) {
		return listaPrenotazioniAttrezzatura.remove(prenotazioneAttrezzatura);
	}


	/**
	 * 
	 * @param cliente
	 * @param attivita
	 */
	public boolean prenotaAttivitaPerCliente(Cliente cliente, Attivita attivita) {
		if(attivita.getPartecipanti().size()>=attivita.getCapienzaMax())
			return false;
		attivita.aggiungiPartecipante(cliente);
		return true;
	}
	
	public boolean rimuoviPrenotazioneAttivitaPerCliente(Cliente cliente, Attivita attivita)
	{
		return attivita.rimuoviPartecipante(cliente);
	}


   public String[] visualizzaPrenotazioniAttrezzatura()
   {
	   String[] prenotazione = new String[this.getListaPrenotazioniAttrezzatura().size()];
	   for(int i = 0;i<this.getListaPrenotazioniAttrezzatura().size();i++)
	   {
		   prenotazione[i] = this.getListaPrenotazioniAttrezzatura().get(i).toString();
	   }
	   return prenotazione;
   }
   
   public boolean restituisciAttrezzatura(PrenotazioneAttrezzatura prenotazioneAttrezzatura,String oraRestituzione)
   {
	   if(this.listaPrenotazioniAttrezzatura.contains(prenotazioneAttrezzatura) && prenotazioneAttrezzatura.getOraRestituzione()==null)
	   {
		   prenotazioneAttrezzatura.setOraRestituzione(oraRestituzione);
		   return true;
	   }
	   else return false;
   }


@Override
public String toString() {
	return "\nAddettoAttivita [listaPrenotazioniAttrezzatura=" + listaPrenotazioniAttrezzatura + "nome=" + getNome() + ", cognome=" + getCognome() + ", dataNasciata="
			+ getDataN() + ", telefono" + getTel() + ", account" + getAccount() + "]";
}

	   
}