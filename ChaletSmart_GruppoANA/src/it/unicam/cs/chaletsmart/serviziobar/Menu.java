package it.unicam.cs.chaletsmart.serviziobar;

import java.util.ArrayList;

public class Menu{

	private String dataInizio;
	private String dataFine;
	private ArrayList<Bibita> elencoBibite;
	private ArrayList<Piatto> elencoPiatti;

	/**
	 * 
	 * @param dataInizio
	 * @param dataFine
	 */
	public Menu(String dataInizio, String dataFine) {
		this.dataInizio=dataInizio;
		this.dataFine=dataFine;
		this.elencoBibite=new ArrayList<Bibita>();
		this.elencoPiatti=new ArrayList<Piatto>();
	}

	public String getDataInizio() {
		return this.dataInizio;
	}

	/**
	 * 
	 * @param dataInizio
	 */
	public void setDataInizio(String dataInizio) {
		this.dataInizio = dataInizio;
	}

	public String getDataFine() {
		return this.dataFine;
	}

	/**
	 * 
	 * @param dataFine
	 */
	public void setDataFine(String dataFine) {
		this.dataFine = dataFine;
	}

	public ArrayList<Bibita> getElencoBibite() {
		return new ArrayList<>(this.elencoBibite);
	}

	/**
	 * 
	 * @param bibita
	 */
	public void aggungiBibitaAdElencoBibite(Bibita bibita) {
		this.elencoBibite.add(bibita);
	}

	/**
	 * 
	 * @param bibita
	 */
	public boolean rimuoviBibitaDaElencoBibite(Bibita bibita) {
		return elencoBibite.remove(bibita);
	}

	public ArrayList<Piatto> getElencoPiatti() {
		return new ArrayList<>(this.elencoPiatti);
	}

	/**
	 * 
	 * @param piatto
	 */
	public void aggiungiPiattoAdElencoPiatti(Piatto piatto) {
		this.elencoPiatti.add(piatto);
	}

	/**
	 * 
	 * @param piatto
	 */
	public boolean rimuoviPiattoDaElencoPiatti(Piatto piatto) {
		return elencoPiatti.remove(piatto);
	}

	@Override
	public String toString() {
		return "\nMenu [dataInizio=" + dataInizio + ", dataFine=" + dataFine + ", elencoBibite=" + elencoBibite
				+ ", elencoPiatti=" + elencoPiatti + "]";
	}

}