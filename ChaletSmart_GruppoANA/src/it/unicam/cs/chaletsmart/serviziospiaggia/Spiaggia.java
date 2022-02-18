package it.unicam.cs.chaletsmart.serviziospiaggia;

import java.util.ArrayList;
import java.util.Date;

import it.unicam.cs.chaletsmart.attrezzatura.Attrezzatura;
import it.unicam.cs.chaletsmart.persone.*;
import it.unicam.cs.chaletsmart.serviziobar.Menu;
import it.unicam.cs.chaletsmart.serviziobar.Ordinazione;

public class Spiaggia  {

	private ArrayList<Prenotazione> listaPrenotazioni;
	private String orarioApertura;
	private String orarioChiusura;
	private ArrayList<Attivita> listaAttivita;
	private ArrayList<Promozione> listaPromozioni;
	private ArrayList<Ombrellone> elencoOmbrelloni;
	private ArrayList<Ordinazione> listaOrdinazioni;
	private int capienzaMaxSpiaggia;
	private float rincaroGiorniConsecutivi;
	private Menu menu;
	private ArrayList<Attrezzatura> listaAttrezzatura;
	private float prezzoLettino;

	public Spiaggia(String orarioApertura, String orarioChiusura,int capienzaMaxSpiaggia) {
		this.orarioApertura=orarioApertura;
		this.orarioChiusura=orarioChiusura;
		this.capienzaMaxSpiaggia=capienzaMaxSpiaggia;
		this.listaPrenotazioni=new ArrayList<Prenotazione>();
		this.listaAttivita=new ArrayList<Attivita>();
		this.listaPromozioni=new ArrayList<Promozione>();
		this.elencoOmbrelloni=new ArrayList<Ombrellone>();
		this.listaOrdinazioni=new ArrayList<Ordinazione>();
		this.rincaroGiorniConsecutivi=0.7f;
		this.menu=new Menu("Orario inizio di default", "Orario fine di default");
		this.listaAttrezzatura= new ArrayList<Attrezzatura>();
		this.prezzoLettino = 8;
	}

	public ArrayList<Prenotazione> getListaPrenotazioni() {
		return new ArrayList<>(this.listaPrenotazioni);
	}

	/**
	 * 
	 * @param prenotazione
	 */
	public boolean aggiungiPrenotazione(Prenotazione prenotazione) {
		
		int ombrelloniPrenotati= this.getNumeroTotaleOmbrelloniPrenotatiPerData(prenotazione.getGiornoDellaPrenotazione())
				+prenotazione.getOmbrelloniAssociati().size();
		if(ombrelloniPrenotati>this.getCapienzaMaxSpiaggia() || listaPrenotazioni.contains(prenotazione))
			return false;
		this.listaPrenotazioni.add(prenotazione);
		return true;
	}

	/**
	 * 
	 * @param prenotazione
	 */
	public boolean rimuoviPrenotazione(Prenotazione prenotazione) {
		return listaPrenotazioni.remove(prenotazione);
	}



	public String getOrarioApertura() {
		return this.orarioApertura;
	}

	/**
	 * 
	 * @param orarioApertura
	 */
	public void setOrarioApertura(String orarioApertura) {
		this.orarioApertura = orarioApertura;
	}

	public String getOrarioChiusura() {
		return this.orarioChiusura;
	}

	/**
	 * 
	 * @param orarioChiusura
	 */
	public void setOrarioChiusura(String orarioChiusura) {
		this.orarioChiusura = orarioChiusura;
	}

	public ArrayList<Attivita> getListaAttivita() {
		return new ArrayList<>(this.listaAttivita);
	}

	/**
	 * 
	 * @param attivita
	 */
	public void aggiungiAttivita(Attivita attivita) {
		this.listaAttivita.add(attivita);
	}

	/**
	 * 
	 * @param attivita
	 */
	public boolean rimuoviAttivita(Attivita attivita) {
		return listaAttivita.remove(attivita);
	}

	public ArrayList<Promozione> getListaPromozioni() {
		return new ArrayList<>(this.listaPromozioni);
	}

	/**
	 * 
	 * @param promozione
	 */
	public void aggiungiPromozione(Promozione promozione) {
		this.listaPromozioni.add(promozione);
	}
	
	/**
	 * 
	 * @param ombrellone
	 */
	public void aggiungiOmbrellone(Ombrellone ombrellone) {
		if(!elencoOmbrelloni.contains(ombrellone))
			this.elencoOmbrelloni.add(ombrellone);
	}	
	
	
	/**
	 * 
	 * @param ombrellone
	 */
	public boolean rimuoviOmbrellone(Ombrellone ombrellone) {
		return elencoOmbrelloni.remove(ombrellone);
	}

	public ArrayList<Ombrellone> getElencoOmbrelloni() {
		return new ArrayList<>(this.elencoOmbrelloni);
	}

	/**
	 * 
	 * @param promozione
	 */
	public boolean rimuoviPromozione(Promozione promozione) {
		return listaPromozioni.remove(promozione);
	}

	/**
	 * 
	 * @param cliente
	 * @param attivita
	 */
	public boolean aggiungiPrenotazioneAttivita(Cliente cliente, Attivita attivita) {
		if(attivita.getCapienzaMax()<=attivita.getPartecipanti().size())
			return false;
		return attivita.aggiungiPartecipante(cliente);
	}

	public boolean rimuoviPrenotazioneAttivita(Cliente cliente, Attivita attivita) {
		return attivita.rimuoviPartecipante(cliente);
	}
	
	public int getNumeroTotaleOmbrelloniPrenotatiPerData(Date dataSpecifica)
	{
		int contatore=0;
		for(Prenotazione p:listaPrenotazioni)
			if(p.getGiornoDellaPrenotazione().getMonth()==dataSpecifica.getMonth()&&
				p.getGiornoDellaPrenotazione().getDay()==dataSpecifica.getDay()&&
				p.getGiornoDellaPrenotazione().getYear() == dataSpecifica.getYear())
					contatore+=p.getOmbrelloniAssociati().size();
		return contatore;
	}
	

	public int getCapienzaMaxSpiaggia() {
		return capienzaMaxSpiaggia;
	}

	public void setCapienzaMaxSpiaggia(int capienzaMaxSpiaggia) {
		this.capienzaMaxSpiaggia = capienzaMaxSpiaggia;
	}
	public float getRincaroMedioPrenotazioneDelCliente(Cliente cliente)
	{
		float contatore=0;
		float rincaro=0;
		for(Prenotazione p: listaPrenotazioni)
		{
			if(p.getClientePrenotante().equals(cliente))
			{
				for(RincaroStagione r:RincaroStagione.values())
				{
					if(r.getMese()==p.getGiornoDellaPrenotazione().getMonth())
					{
						rincaro+=r.getRincaro();
						rincaro+=p.getTipoPrenotazione().getPercentualeSulCosto();
						contatore+=2;
						if(p.isAGiorniConsecutivi())
						{
							rincaro+=rincaroGiorniConsecutivi;
							contatore++;
						}
					}
				}
			}
		}

		return rincaro/contatore;
	}
	
	public ArrayList<Ordinazione> getListaOrdinazioni() {
		return new ArrayList<>(this.listaOrdinazioni);
	}

	public void aggiungiOrdinazione(Ordinazione ordinazione) {
		this.listaOrdinazioni.add(ordinazione);
	}
	
	public boolean rimuoviOrdinazione(Ordinazione ordinazione)
	{
		return this.listaOrdinazioni.remove(ordinazione);
	}

	public float getRincaroGiorniConsecutivi() {
		return rincaroGiorniConsecutivi;
	}

	public void setRincaroGiorniConsecutivi(float rincaroGiorniConsecutivi) {
		this.rincaroGiorniConsecutivi = rincaroGiorniConsecutivi;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public Attrezzatura[] getListaAttrezzatura() {
		Attrezzatura[] ritorno = new Attrezzatura[this.listaAttrezzatura.size()];
		for(int i=0;i<this.listaAttrezzatura.size();i++)
			ritorno[i]=this.listaAttrezzatura.get(i);
		return ritorno;
	}

	public void setListaAttrezzatura(ArrayList<Attrezzatura> listaAttrezzatura) {
		this.listaAttrezzatura = listaAttrezzatura;
	}

	@Override
	public String toString() {
		return "\nSpiaggia [listaPrenotazioni=" + listaPrenotazioni + ", orarioApertura=" + orarioApertura
				+ ", orarioChiusura=" + orarioChiusura + ", listaAttivita=" + listaAttivita + ", listaPromozioni="
				+ listaPromozioni + ", elencoOmbrelloni=" + elencoOmbrelloni + ", listaOrdinazioni=" + listaOrdinazioni
				+ ", capienzaMaxSpiaggia=" + capienzaMaxSpiaggia + ", rincaroGiorniConsecutivi="
				+ rincaroGiorniConsecutivi + ", menu=" + menu + ", listaAttrezzatura=" + listaAttrezzatura + "]";
	}

	public float getPrezzoLettino() {
		return prezzoLettino;
	}

	public void setPrezzoLettino(float prezzoLettino) {
		this.prezzoLettino = prezzoLettino;
	}
	
	public boolean aggiungiAttrezzatura(Attrezzatura attrezzatura)
	{
		if(this.listaAttrezzatura.contains(attrezzatura))
			return false;
		listaAttrezzatura.add(attrezzatura);
		return true;
	}
	
	public boolean rimuoviAttrezzatura(Attrezzatura attrezzatura)
	{
		return this.listaAttrezzatura.remove(attrezzatura);
	}
}