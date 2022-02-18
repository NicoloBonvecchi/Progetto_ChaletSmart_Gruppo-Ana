package it.unicam.cs.chaletsmart.serviziobar;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import it.unicam.cs.chaletsmart.persone.Cliente;
import it.unicam.cs.chaletsmart.serviziospiaggia.*;

public class Ordinazione{

	private Cliente clienteOrdinante;
	private ArrayList<Prodotto> insiemeProdotti;
	private float costoComplessivo;
	private Ombrellone ombrelloneRelativo;
	private Date oraOrdinazione;

	
	/**
	 * 
	 * @param cliente
	 * @param ombrellone
	 */
	public Ordinazione(Cliente cliente, Ombrellone ombrellone) {
		this.clienteOrdinante=cliente;
		this.ombrelloneRelativo=ombrellone;
		this.costoComplessivo=0;
		this.insiemeProdotti=new ArrayList<Prodotto>();
		this.oraOrdinazione=new Date(); 
		//Quando viene effettuata un'ordinazione, viene registrata la data e l'ora attuali 
		//(queste ultime corrispondono a quelle del sistema) 
	}


	public Cliente getClienteOrdinante() {
		return this.clienteOrdinante;
	}

	/**
	 * 
	 * @param clienteOrdinante
	 */
	public void setClienteOrdinante(Cliente clienteOrdinante) {
		this.clienteOrdinante = clienteOrdinante;
	}

	public float getCostoComplessivo() {
		float appCosto=0;
		for(Prodotto p : this.insiemeProdotti)
			appCosto+=p.getCosto();
		this.costoComplessivo=appCosto;
		return this.costoComplessivo;
	}

	/**
	 * 
	 * @param costoComplessivo
	 */
	public void setCostoComplessivo(float costoComplessivo) {
		this.costoComplessivo = costoComplessivo;
	}

	public Ombrellone getOmbrelloneRelativo() {
		return this.ombrelloneRelativo;
	}

	/**
	 * 
	 * @param ombrelloneRelativo
	 */
	public void setOmbrelloneRelativo(Ombrellone ombrelloneRelativo) {
		this.ombrelloneRelativo = ombrelloneRelativo;
	}

	/**
	 * 
	 * @param prodotto
	 * @param quantita
	 */
	public boolean aggiungiProdottiAdElencoProdotti(Prodotto prodotto, int quantita) {
		for(int i=0;i<quantita;i++)
		{
			if(!prodotto.getIsDisponibile())
				return false;
			insiemeProdotti.add(prodotto);
		}
		return true;
	}


	public boolean rimuoviProdottiDaElencoProdottiPerTipo(Prodotto prodotto)
	{
		boolean trovato = false;
		for(Prodotto p : insiemeProdotti)
		{
			insiemeProdotti.remove(p);
			trovato=true;
		}
		return trovato;
	}

	public Date getOraOrdinazione() {
		return oraOrdinazione;
	}


	@Override
	public int hashCode() {
		return Objects.hash(clienteOrdinante, insiemeProdotti, ombrelloneRelativo, oraOrdinazione);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ordinazione other = (Ordinazione) obj;
		return Objects.equals(clienteOrdinante, other.clienteOrdinante)
				&& Objects.equals(insiemeProdotti, other.insiemeProdotti)
				&& Objects.equals(ombrelloneRelativo, other.ombrelloneRelativo)
				&& Objects.equals(oraOrdinazione, other.oraOrdinazione);
	}


	@Override
	public String toString() {
		return "\nOrdinazione [clienteOrdinante=" + clienteOrdinante.getNome()+", "+clienteOrdinante.getCognome()+", telefono= "+clienteOrdinante.getTel() + "\ninsiemeProdotti=" + insiemeProdotti.toString()
				+ ", costoComplessivo=" + this.getCostoComplessivo() + ", ombrelloneRelativo=" + ombrelloneRelativo
				+ ", oraOrdinazione=" + oraOrdinazione + "]";
	}


	public ArrayList<Prodotto> getInsiemeProdotti() {
		return insiemeProdotti;
	}


	public void setOraOrdinazione(Date oraOrdinazione) {
		this.oraOrdinazione = oraOrdinazione;
	}	
	
}