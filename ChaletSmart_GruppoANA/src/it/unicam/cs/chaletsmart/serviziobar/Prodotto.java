package it.unicam.cs.chaletsmart.serviziobar;

import java.util.Objects;

public class Prodotto  {

	private int ID;
	private String nome;
	private String descrizione;
	private boolean isDisponibile;
	private float costo;

	/**
	 * 
	 * @param id
	 * @param nome
	 * @param descrizione
	 */
	public Prodotto(int id, String nome, String descrizione,float costo) {
		this.ID=id;
		this.nome=nome;
		this.descrizione=descrizione;
		this.isDisponibile=true;
		this.costo=costo;
	}

	public int getID() {
		return this.ID;
	}

	/**
	 * 
	 * @param ID
	 */
	public void setID(int ID) {
		this.ID=ID;
	}

	public String getNome() {
		return this.nome;
	}

	/**
	 * 
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
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

	public boolean getIsDisponibile() {
		return this.isDisponibile;
	}

	/**
	 * 
	 * @param isDisponibile
	 */
	public void setIsDisponibile(boolean isDisponibile) {
		this.isDisponibile = isDisponibile;
	}

	public float getCosto() {
		return costo;
	}

	public void setCosto(float costo) {
		this.costo = costo;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Prodotto other = (Prodotto) obj;
		return ID == other.ID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ID);
	}

	@Override
	public String toString() {
		return "Prodotto [ID=" + ID + "\nnome=" + nome + "\ndescrizione=" + descrizione + "\nisDisponibile="
				+ isDisponibile + "\ncosto=" + costo + "]";
	}
	
	
}