package it.unicam.cs.chaletsmart.account;

import java.util.Objects;

public class Account  {

	private String nomeUtente;
	private String email;
	private String password;
	private TipoAccount tipoAccount;

	/**
	 * 
	 * @param nomeUtente
	 * @param email
	 * @param password
	 */
	public Account(String nomeUtente, String email, String password) {
		this.nomeUtente=nomeUtente;
		this.email=email;
		this.password=password;
	}

	public String getNomeUtente() {
		return this.nomeUtente;
	}

	/**
	 * 
	 * @param nomeUtente
	 */
	public void setNomeUtente(String nomeUtente) {
		this.nomeUtente = nomeUtente;
	}

	public String getEmail() {
		return this.email;
	}

	/**
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 * @param password
	 */
	public Boolean controllaPassword(String password) {
		return this.password.equals(password);
	}

	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public TipoAccount getTipoAccount() {
		return this.tipoAccount;
	}

	/**
	 * 
	 * @param tipoAccount
	 */
	public void setTipoAccount(TipoAccount tipoAccount) {
		this.tipoAccount = tipoAccount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		return Objects.equals(email, other.email);
	}

	@Override
	public String toString() {
		return "\nAccount [nomeUtente=" + nomeUtente + ", email=" + email + ", tipoAccount="
				+ tipoAccount + "]";
	}

}