package it.unicam.cs.chaletsmart.persone;

import java.util.ArrayList;
import java.util.Objects;
import it.unicam.cs.chaletsmart.account.Account;

public class PersonaChaletSmart {


	private String nome;
	private String cognome;
	private String dataNascita;
	private String telefono;
	private Account account;
	private ArrayList<String> listaNotifichePromozioni;

	/**
	 * 
	 * @param nome
	 * @param cognome
	 * @param dataN
	 * @param tel
	 */
	public PersonaChaletSmart(String nome, String cognome, String dataNascita, String telefono) {
		this.nome=nome;
		this.cognome=cognome;
		this.dataNascita=dataNascita;
		this.telefono=telefono;
		this.listaNotifichePromozioni= new ArrayList<>();
		this.account=null;
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

	public String getCognome() {
		return this.cognome;
	}

	/**
	 * 
	 * @param cognome
	 */
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getDataN() {
		return this.dataNascita;
	}

	/**
	 * 
	 * @param dataN
	 */
	public void setDataN(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getTel() {
		return this.telefono;
	}

	/**
	 * 
	 * @param tel
	 */
	public void setTel(String telefono) {
		this.telefono = telefono;
	}

	public Account getAccount() {
		return this.account;
	}

	/**
	 * 
	 * @param account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public String[] getListaNotificheOPromozioni() {
		String[] ritorno = new String[this.listaNotifichePromozioni.size()];
		for(int i=0;i<listaNotifichePromozioni.size();i++)
			ritorno[i]=listaNotifichePromozioni.get(i);
		return ritorno;
	}

	public void aggiungiNotificaOPromozione(String notifica) {
		this.listaNotifichePromozioni.add(notifica);
	}
	
	public boolean rimuoviNotificaOPromozione(String stringa)
	{
		return this.listaNotifichePromozioni.remove(stringa);
	}

	@Override
	public String toString() {
		return "\nPersonaChaletSmart [nome=" + nome + ", cognome=" + cognome + ", dataNascita=" + dataNascita
				+ ", telefono=" + telefono + ", account=" + account + ", listaNotifichePromozioni="
				+ listaNotifichePromozioni + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(account, cognome, dataNascita, nome, telefono);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonaChaletSmart other = (PersonaChaletSmart) obj;
		return Objects.equals(account, other.account) && Objects.equals(cognome, other.cognome)
				&& Objects.equals(dataNascita, other.dataNascita) && Objects.equals(nome, other.nome)
				&& Objects.equals(telefono, other.telefono);
	}
	

}