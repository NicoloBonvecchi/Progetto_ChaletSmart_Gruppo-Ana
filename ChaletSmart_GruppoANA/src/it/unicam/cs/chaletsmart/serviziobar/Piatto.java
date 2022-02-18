package it.unicam.cs.chaletsmart.serviziobar;

public class Piatto extends Prodotto {


	private String ingredienti;
	private String allergeni;
	private String tipo;

	
	public Piatto(int id, String nome, String descrizione, float costo, String ingredienti, String allergeni, String tipo) {
		super(id, nome, descrizione, costo);
		this.ingredienti=ingredienti;
		this.allergeni=allergeni;
		this.tipo=tipo;
	}

	public String getIngredienti() {
		return this.ingredienti;
	}

	/**
	 * 
	 * @param ingredienti
	 */
	public void setIngredienti(String ingredienti) {
		this.ingredienti = ingredienti;
	}

	public String getAllergeni() {
		return this.allergeni;
	}

	/**
	 * 
	 * @param allergeni
	 */
	public void setAllergeni(String allergeni) {
		this.allergeni = allergeni;
	}

	public String getTipo() {
		return this.tipo;
	}

	/**
	 * 
	 * @param tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}