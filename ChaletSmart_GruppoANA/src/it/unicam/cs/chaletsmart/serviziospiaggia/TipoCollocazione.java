package it.unicam.cs.chaletsmart.serviziospiaggia;

public enum TipoCollocazione {
	AVANTI(1.15f,"Avanti"),
	INMEZZO(1.05f,"In mezzo"),
	DIETRO(1f,"Dietro");
	private float rincaroCollocazione;
	private String stringaAssociata;
	
	
	TipoCollocazione(float rincaroCollocazione,String stringaAssociata)
	{
		this.rincaroCollocazione=rincaroCollocazione;
		this.stringaAssociata=stringaAssociata;
	}
	
	
	public float getRincaroCollocazione() {
		return rincaroCollocazione;
	}

	public void setRincaroCollocazione(float rincaroCollocazione) {
		this.rincaroCollocazione = rincaroCollocazione;
	}
	
	
	public String getStringaAssociata() {
		return stringaAssociata;
	}

	public void setStringaAssociata(String stringaAssociata) {
		this.stringaAssociata = stringaAssociata;
	}


}
