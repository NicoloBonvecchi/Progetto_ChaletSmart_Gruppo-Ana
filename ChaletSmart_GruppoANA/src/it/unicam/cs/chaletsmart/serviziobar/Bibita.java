package it.unicam.cs.chaletsmart.serviziobar;

public class Bibita extends Prodotto {



	private boolean isAlcolica;

	public Bibita(int id, String nome, String descrizione, float costo, boolean isAlcolica) {
		super(id, nome, descrizione, costo);
	}

	public boolean getIsAlcolica() {
		return this.isAlcolica;
	}

	/**
	 * 
	 * @param isAlcolica
	 */
	public void setIsAlcolica(boolean isAlcolica) {
		this.isAlcolica = isAlcolica;
	}

}