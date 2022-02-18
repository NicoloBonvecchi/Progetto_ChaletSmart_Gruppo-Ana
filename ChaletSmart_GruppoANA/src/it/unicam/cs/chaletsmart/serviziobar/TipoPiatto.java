package it.unicam.cs.chaletsmart.serviziobar;

public enum TipoPiatto {
	CONTORNO("CONTORNO"),
	PRIMOPIATTO("PRIMO PIATTO"),
	SECONDOPIATTO("SECONDO PIATTO"),
	DESSERT("DESSERT");


	private String StringaAssociata;

	TipoPiatto(String stringaAssociata)
	{
		this.StringaAssociata=stringaAssociata;
	}
	public String getStringaAssociata() {
		return this.StringaAssociata;
	}

	/**
	 * 
	 * @param StringaAssociata
	 */
	public void setStringaAssociata(String StringaAssociata) {
		this.StringaAssociata = StringaAssociata;
	}

}