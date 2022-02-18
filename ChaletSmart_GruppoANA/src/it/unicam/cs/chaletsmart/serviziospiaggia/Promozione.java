package it.unicam.cs.chaletsmart.serviziospiaggia;

public class Promozione {

	private String nomeIdentificativo;
	private String descrizione;
	private String noteSullaValidita;

	/**
	 * 
	 * @param nomeIdentificativo
	 * @param descrizione
	 * @param noteSullaValidita
	 */
	public Promozione(String nomeIdentificativo, String descrizione, String noteSullaValidita) {
		this.nomeIdentificativo=nomeIdentificativo;
		this.descrizione=descrizione;
		this.noteSullaValidita=noteSullaValidita;
	}

	public String getNomeIdentificativo() {
		return this.nomeIdentificativo;
	}

	/**
	 * 
	 * @param nomeIdentificativo
	 */
	public void setNomeIdentificativo(String nomeIdentificativo) {
		this.nomeIdentificativo = nomeIdentificativo;
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

	public String getNoteSullaValidita() {
		return this.noteSullaValidita;
	}

	/**
	 * 
	 * @param noteSullaValidita
	 */
	public void setNoteSullaValidita(String noteSullaValidita) {
		this.noteSullaValidita = noteSullaValidita;
	}

	@Override
	public String toString() {
		return "\nPromozione [nomeIdentificativo=" + nomeIdentificativo + ", descrizione=" + descrizione
				+ ", noteSullaValidita=" + noteSullaValidita + "]";
	}

}