package it.unicam.cs.chaletsmart.account;

public enum TipoAccount {
	ACCOUNTUTENTESTANDARD("Cliente"),
	ACCOUNTADDETTOPRENOTAZIONI("Addetto Prenotazioni"),
	ACCOUNTADDETTOATTIVITA("Addetto Attivita"),
	ACCOUNTADDETTOBARCASSA("Addetto Bar Cassa"),
	ACCOUNTAMMINISTRATORE("Gestore");

	private String StringaAssociata;
	TipoAccount(String StringaAssociata)
	{
		this.StringaAssociata=StringaAssociata;
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