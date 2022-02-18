package it.unicam.cs.chaletsmart.persone;

import it.unicam.cs.chaletsmart.serviziospiaggia.*;

public class Gestore extends PersonaChaletSmart {


	private Spiaggia spiaggia;

	public Gestore(String nome, String cognome, String dataNascita, String telefono, Spiaggia spiaggia) {
		super(nome, cognome, dataNascita, telefono);
		this.spiaggia=spiaggia;
	}

	public Spiaggia getSpiaggia() {
		return this.spiaggia;
	}

	/**
	 * 
	 * @param spiaggia
	 */
	public void setSpiaggia(Spiaggia spiaggia) {
		this.spiaggia = spiaggia;
	}

	/**
	 * 
	 * @param nuovoRincaroStagione
	 */
	public void modificaRincaroStagione(int meseDiCuiModificareRincaro, float nuovoRincaroStagione) {
		for(RincaroStagione r : RincaroStagione.values())
		{
			if(r.getMese()==meseDiCuiModificareRincaro)
			{
				r.setRincaro(nuovoRincaroStagione);
			}
		}
	}

	/**
	 * 
	 * @param nuovoRincaroCollocazione
	 */
	public void modificaRincaroCollocazione(TipoCollocazione tipoCollocazioneDiCuiModificareRincaro, float nuovoRincaroCollocazione) {
		tipoCollocazioneDiCuiModificareRincaro.setRincaroCollocazione(nuovoRincaroCollocazione);
	}

	/**
	 * 
	 * @param nuovoOrarioApertura
	 */
	public void modificaOrarioApertura(String nuovoOrarioApertura) {
		spiaggia.setOrarioApertura(nuovoOrarioApertura);
	}

	/**
	 * 
	 * @param nuovoOrarioChiusura
	 */
	public void modificaOrarioChiusura(String nuovoOrarioChiusura) {
		spiaggia.setOrarioChiusura(nuovoOrarioChiusura);
	}

	/**
	 * 
	 * @param attivita
	 */
	public void aggiungiAttivitaAListaAttivita(Attivita attivita) {
		spiaggia.aggiungiAttivita(attivita);
	}

	/**
	 * 
	 * @param attivita
	 */
	public boolean rimuoviAttivitaDaListaAttivita(Attivita attivita) {
		return spiaggia.rimuoviAttivita(attivita);
	}

	/**
	 * 
	 * @param promozione
	 */
	public void aggiungiPromozioneAListaPromozioni(Promozione promozione) {
		spiaggia.aggiungiPromozione(promozione);
	}

	/**
	 * 
	 * @param promozione
	 */
	public boolean rimuoviPromozioneDaListaPromozioni(Promozione promozione) {
		return spiaggia.rimuoviPromozione(promozione);
	}

	/**
	 * 
	 * @param ombrellone
	 */
	public void aggiungiOmbrelloneAElencoOmbrelloni(Ombrellone ombrellone) {
		spiaggia.aggiungiOmbrellone(ombrellone);
	}

	/**
	 * 
	 * @param ombrellone
	 */
	public boolean rimuoviOmbrelloneDaElencoOmbrelloni(Ombrellone ombrellone) {
		return spiaggia.rimuoviOmbrellone(ombrellone);
	}
	
	public void modificaCapienzaMaxSpiaggia(int capienza)
	{
		this.spiaggia.setCapienzaMaxSpiaggia(capienza);
	}
	@Override
	public String toString() {
		return "\nGestore [nome=" + getNome() + ", cognome=" + getCognome() + ", dataNasciata="
				+ getDataN() + ", telefono" + getTel() + ", account" + getAccount() + "]";
	}

}