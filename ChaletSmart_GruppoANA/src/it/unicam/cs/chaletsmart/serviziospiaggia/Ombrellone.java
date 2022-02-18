package it.unicam.cs.chaletsmart.serviziospiaggia;

import java.util.Objects;

public class Ombrellone {

	private float prezzo;
	private String descrizione;
	private int id;
	private String QRcode;
	private TipoCollocazione collocazione;
	/**
	 * Le categorie corrispondono a:
	 * -0 = fascia bassa
	 * -1 = fascia media
	 * -2 = fascia alta
	 */
	private int categoria;

	/**
	 * 
	 * @param prezzo
	 * @param descrizione
	 * @param id
	 * @param collocazione
	 * @param categoria
	 */
	
	public Ombrellone(float prezzo, String descrizione, int id, TipoCollocazione collocazione, int categoria) {
		this.prezzo=prezzo;
		this.descrizione=descrizione;
		this.id=id;
		this.collocazione=collocazione;
		this.categoria=categoria;
		this.QRcode=Integer.toString(Integer.hashCode(id));
		
	}

	public float getPrezzo() {
		return this.prezzo;
	}

	/**
	 * 
	 * @param prezzo
	 */
	public void setPrezzo(float prezzo) {
		this.prezzo = prezzo;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	/**
	 * 
	 * @param descr
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public int getId() {
		return this.id;
	}

	public TipoCollocazione getCollocazione() {
		return this.collocazione;
	}

	/**
	 * 
	 * @param collocazione
	 */
	public void setCollocazione(TipoCollocazione collocazione) {
		this.collocazione = collocazione;
	}

	public int getCategoria() {
		return this.categoria;
	}

	/**
	 * 
	 * @param categoria
	 */
	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public String getQRcode() {
		return QRcode;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Ombrellone [prezzo=" + prezzo + ", descrizione=" + descrizione + ", id=" + id +
				 ", collocazione=" + collocazione.getStringaAssociata() + ", categoria=" + categoria + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ombrellone other = (Ombrellone) obj;
		return id == other.id;
	}

}