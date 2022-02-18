package it.unicam.cs.chaletsmart.serviziospiaggia;

import java.util.Date;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;

import it.unicam.cs.chaletsmart.persone.Cliente;

public class Prenotazione {

	private Cliente clientePrenotante;
	private TipoPrenotazione tipoPrenotazione;
	private ArrayList<Ombrellone> ombrelloniAssociati;
	private int lettiniAssociati;
	private Date giornoDellaPrenotazione;
	private boolean aGiorniConsecutivi;




	/**
	 * 
	 * @param cliente
	 * @param tipoPrenotazione
	 * @param ombrelloniAssociati
	 * @param lettiniAssociati
	 * @param giornoDellaPrenotazione
	 */
	public Prenotazione(Cliente cliente, TipoPrenotazione tipoPrenotazione, Ombrellone[] ombrelloniAssociati, int lettiniAssociati, Date giornoDellaPrenotazione) {
		this.clientePrenotante=cliente;
		this.tipoPrenotazione=tipoPrenotazione;
		this.lettiniAssociati=lettiniAssociati;
		this.giornoDellaPrenotazione=giornoDellaPrenotazione;
		this.ombrelloniAssociati=new ArrayList<Ombrellone>(Arrays.asList(ombrelloniAssociati));
		this.aGiorniConsecutivi=false;
	}

	
	


	public Cliente getClientePrenotante() {
		return this.clientePrenotante;
	}

	public TipoPrenotazione getTipoPrenotazione() {
		return this.tipoPrenotazione;
	}

	/**
	 * 
	 * @param tipoPrenotazione
	 */
	public void setTipoPrenotazione(TipoPrenotazione tipoPrenotazione) {
		this.tipoPrenotazione = tipoPrenotazione;
	}

	public ArrayList<Ombrellone> getOmbrelloniAssociati() {
		return new ArrayList<>(ombrelloniAssociati);
	}

	/**
	 * 
	 * @param ombrellone
	 */
	public void aggiungiOmbrellone(Ombrellone ombrellone) {
		this.ombrelloniAssociati.add(ombrellone);
	}

	/**
	 * 
	 * @param ombrellone
	 */
	public boolean rimuoviOmbrellone(Ombrellone ombrellone) {
		return this.ombrelloniAssociati.remove(ombrellone);
	}

	public int getLettiniAssociati() {
		return this.lettiniAssociati;
	}

	/**
	 * 
	 * @param lettiniAssociati
	 */
	public void setLettiniAssociati(int lettiniAssociati) {
		this.lettiniAssociati = lettiniAssociati;
	}

	private float getCostoComplessivoOmbrelloni() {
		float appCosto=0;
		for(Ombrellone o: this.ombrelloniAssociati)
			appCosto+=o.getPrezzo()*o.getCollocazione().getRincaroCollocazione();
		return appCosto;
	}
	
	public boolean isAGiorniConsecutivi() {
		return aGiorniConsecutivi;
	}

	public void setAGiorniConsecutivi(boolean aGiorniConsecutivi) {
		this.aGiorniConsecutivi = aGiorniConsecutivi;
	}

	public Date getGiornoDellaPrenotazione() {
		return this.giornoDellaPrenotazione;
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(clientePrenotante, giornoDellaPrenotazione, ombrelloniAssociati);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Prenotazione other = (Prenotazione) obj;
		return Objects.equals(clientePrenotante, other.clientePrenotante)
				&& Objects.equals(giornoDellaPrenotazione, other.giornoDellaPrenotazione)
				&& Objects.equals(ombrelloniAssociati, other.ombrelloniAssociati);
	}





	@Override
	public String toString() {
		String prenotazione= "\nPrenotazione [tipoPrenotazione=" + tipoPrenotazione + ", ombrelloniAssociati=";
		for(Ombrellone o : ombrelloniAssociati)
			prenotazione+=o.toString()+"\n";
		prenotazione+= "lettiniAssociati=" + lettiniAssociati + ", giornoDellaPrenotazione=" + giornoDellaPrenotazione +", aGiorniConsecutivi="+aGiorniConsecutivi+"]";
		return prenotazione;
	}

	public float prezzoTotale(Spiaggia spiaggia)
	{
		return (this.getCostoComplessivoOmbrelloni()+this.lettiniAssociati*spiaggia.getPrezzoLettino())*spiaggia.getRincaroMedioPrenotazioneDelCliente(this.getClientePrenotante());
	}
	
	
}