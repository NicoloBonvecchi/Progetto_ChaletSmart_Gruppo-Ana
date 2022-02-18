package it.unicam.cs.chaletsmart.serviziospiaggia;

public enum RincaroStagione {
	
	MAGGIO(5,1f),
	GIUGNO(6,1f),
	LUGLIO(7,1.10f),
	AGOSTO(8,1.15f),
	SETTEMBRE(9,1f);
	
	private int mese;
	private float rincaro;
	
	RincaroStagione(int mese, float rincaro)
	{
		this.mese=mese;
		this.rincaro=rincaro;
	}

	public int getMese() {
		return mese;
	}

	public float getRincaro() {
		return rincaro;
	}

	public void setRincaro(float rincaro) {
		this.rincaro = rincaro;
	}
}
