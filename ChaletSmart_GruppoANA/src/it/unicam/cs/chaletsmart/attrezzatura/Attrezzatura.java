package it.unicam.cs.chaletsmart.attrezzatura;

import java.util.Objects;

public class Attrezzatura {

	
	private int idTipoAttrezzatura;
	/**
	 * Rappresenta la quantita' complessiva di un dato tipo di attrezzatura
	 */
	private int quantitaComplessiva;
	private String descrizione;

	/**
	 * 
	 * @param idTipoAttrezzatura
	 * @param quantita
	 * @param descrizione
	 */
	public Attrezzatura(int idTipoAttrezzatura, int quantitaComplessiva, String descrizione) {
		this.idTipoAttrezzatura=idTipoAttrezzatura;
		this.quantitaComplessiva=quantitaComplessiva;
		this.descrizione=descrizione;
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

	public int getQuantitaComplessiva() {
		return this.quantitaComplessiva;
	}

	/**
	 * 
	 * @param quantita
	 */
	public void setQuantita(int quantitaComplessiva) {
		this.quantitaComplessiva = quantitaComplessiva;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	/**
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idTipoAttrezzatura);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attrezzatura other = (Attrezzatura) obj;
		return idTipoAttrezzatura == other.idTipoAttrezzatura;
	}

	@Override
	public String toString() {
		return "\nAttrezzatura [idTipoAttrezzatura=" + idTipoAttrezzatura + ", quantitaComplessiva=" + quantitaComplessiva
				+ ", descrizione=" + descrizione + "]";
	}

}