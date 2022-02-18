package it.unicam.cs.chaletsmart.serviziospiaggia;

public enum TipoPrenotazione {
	MEZZAGIORNATA(0.6f,"Mezza giornata"),
	INTERO(1f,"Intero");

	private float percentualeSulCosto;


	private String stringaAssociata;
	TipoPrenotazione(float percentualeSulCosto,String stringaAssociata)
	{
		this.percentualeSulCosto=percentualeSulCosto;
		this.stringaAssociata=stringaAssociata;
	}
	public float getPercentualeSulCosto() {
		return this.percentualeSulCosto;
	}

	/**
	 * 
	 * @param percentualeSulCosto
	 */
	public void setPercentualeSulCosto(float percentualeSulCosto) {
		this.percentualeSulCosto = percentualeSulCosto;
	}
	
	public String getStringaAssociata() {
		return stringaAssociata;
	}

}