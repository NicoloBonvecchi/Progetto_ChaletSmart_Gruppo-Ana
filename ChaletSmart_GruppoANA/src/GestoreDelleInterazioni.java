import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import it.unicam.cs.chaletsmart.account.Account;
import it.unicam.cs.chaletsmart.account.TipoAccount;
import it.unicam.cs.chaletsmart.attrezzatura.Attrezzatura;
import it.unicam.cs.chaletsmart.attrezzatura.PrenotazioneAttrezzatura;
import it.unicam.cs.chaletsmart.gestionedatabase.DBManager;
import it.unicam.cs.chaletsmart.persone.AddettoAttivita;
import it.unicam.cs.chaletsmart.persone.AddettoBarCassa;
import it.unicam.cs.chaletsmart.persone.AddettoPrenotazioni;
import it.unicam.cs.chaletsmart.persone.Cliente;
import it.unicam.cs.chaletsmart.persone.Gestore;
import it.unicam.cs.chaletsmart.persone.PersonaChaletSmart;
import it.unicam.cs.chaletsmart.serviziobar.Bibita;
import it.unicam.cs.chaletsmart.serviziobar.Menu;
import it.unicam.cs.chaletsmart.serviziobar.Ordinazione;
import it.unicam.cs.chaletsmart.serviziobar.Piatto;
import it.unicam.cs.chaletsmart.serviziobar.Prodotto;
import it.unicam.cs.chaletsmart.serviziospiaggia.Attivita;
import it.unicam.cs.chaletsmart.serviziospiaggia.Ombrellone;
import it.unicam.cs.chaletsmart.serviziospiaggia.Prenotazione;
import it.unicam.cs.chaletsmart.serviziospiaggia.Promozione;
import it.unicam.cs.chaletsmart.serviziospiaggia.RincaroStagione;
import it.unicam.cs.chaletsmart.serviziospiaggia.Spiaggia;
import it.unicam.cs.chaletsmart.serviziospiaggia.TipoCollocazione;
import it.unicam.cs.chaletsmart.serviziospiaggia.TipoPrenotazione;

public class GestoreDelleInterazioni {

	private ArrayList<PersonaChaletSmart> listaUtenti;
	private ArrayList<Prodotto> prodottiCreati;
	private Spiaggia spiaggia;
	private DBManager dbManager;
	public void avvia()
	{		
		dbManager = new DBManager("jdbc:postgresql://localhost:5432/ChaletSmart", "postgres", "admin");
		if(dbManager.inizializzaSpiaggia()==null)
		{
			spiaggia = new Spiaggia("7:00", "18:00",100);
			dbManager.caricaSpiaggia(spiaggia);
		}
		else
			spiaggia = dbManager.inizializzaSpiaggia();
		
		if (dbManager.inizializzaMenu()==null)
			dbManager.caricamenu(spiaggia.getMenu());
		else
			spiaggia.setMenu(dbManager.inizializzaMenu());
		
		dbManager.caricaEnum();
		dbManager.inizializzaEnum();
		
		listaUtenti = new ArrayList<PersonaChaletSmart>();
		listaUtenti.addAll(dbManager.inizializzaListaUtenti(spiaggia));

		for(Attrezzatura a : dbManager.inizializzaAttrezzatura())
			this.spiaggia.aggiungiAttrezzatura(a);
		for(Attivita a:dbManager.inizializzaListaAttivita())
		{
			ArrayList<Cliente>appClienti = a.getPartecipanti();
			for(Cliente c : appClienti)
			{
				a.rimuoviPartecipante(c);
				a.aggiungiPartecipante((Cliente)this.listaUtenti.get(this.listaUtenti.indexOf(c)));
			}
			this.spiaggia.aggiungiAttivita(a);
			
		}

		prodottiCreati=new ArrayList<>();
		prodottiCreati.addAll(dbManager.inizializzaProdotti());
		
		for(PrenotazioneAttrezzatura p : dbManager.inizializzaPrenotazioniAttrezzatura())
		{
			AddettoAttivita appAddetto =(AddettoAttivita)listaUtenti.get(listaUtenti.indexOf(p.getAddettoAttivita()));
			appAddetto.aggiungiPrenotazioneAttrezzatura(p);
		}
		Account account=null;
		boolean esciDalCiclo=false;
		Scanner reader = new Scanner(System.in);
		int scelta=-2;
		int indice=0;
		do 
		{
			while (!esciDalCiclo) 
			{
				scelta=-2;
				System.out.println("[INFO] Benvenuti allo Chalet Smart");
				System.out.println("[INFO] Scegli il tipo di account con cui accedere:");			
				for (indice=0; indice<TipoAccount.values().length; indice++)
					System.out.println("[INFO] Scegli " + indice + " per accedere come " + TipoAccount.values()[indice].getStringaAssociata());
				
				System.out.println("[INFO] Scegli " + indice++ + " per creare un nuovo Account Cliente");
				System.out.println("[INFO] Scegli -1 per uscire");
				
				/**
				 * Controllo se viene inserito un intero
				 */
				if(reader.hasNextInt())
				{
					scelta = reader.nextInt();
				}
				else
					reader.nextLine();
				
				/**
				 * Se la scelta ricade in uno dei valori proposti (che vanno da 0 al 
				 * numero di account disponibili)
				 */
				if(scelta>=0 && scelta<TipoAccount.values().length)
				{
					account = login(TipoAccount.values()[scelta]);
					if(account==null)
						System.out.println("[INFO] Nome utente o password ERRATI");
					else
					{
						System.out.println("[INFO] Login effettuato correttamente in qualita' di : "+TipoAccount.values()[scelta].getStringaAssociata());
						break;
					}
				}
				
				/**
				 * Se la scelta e' quella di creare un nuovo Cliente e di dargli un account
				 */
				else if(scelta==TipoAccount.values().length)
				{
					Cliente clienteCreato= (Cliente) this.creaPersonaChaletSmart(TipoAccount.ACCOUNTUTENTESTANDARD);
					this.creaAccount(clienteCreato);
				}
				
				/**
				 * Se la scelta e' di uscire dal ciclo
				 */
				else if(scelta==-1)
					esciDalCiclo=true;
				else
					System.out.println("[INFO] Scelta non valida");
			}
			
			if(account!=null && account.getTipoAccount()!=null &&!esciDalCiclo)
			{
				switch (account.getTipoAccount()) {
				case ACCOUNTUTENTESTANDARD:
					this.accediFunzionalitaUtenteStandard(account);
					break;
				case ACCOUNTAMMINISTRATORE:
					this.accediFunzionalitaGestore(account);
					break;
				case ACCOUNTADDETTOATTIVITA:
					this.accediFunzionalitaAddettoAttivita(account);
				break;
				case ACCOUNTADDETTOBARCASSA:
					this.accediFunzionalitaAddettoBarCassa(account);
					break;
				case ACCOUNTADDETTOPRENOTAZIONI:
					this.accediFunzionalitaAddettoPrenotazioni(account);
					break;
				}
			}
		}while(!esciDalCiclo);
	}
	
	private Account login(TipoAccount tipoAccount)
	{
		String mail;
		String password;
		Scanner reader = new Scanner(System.in);
		
		//Credenziali utente
		System.out.println("[REQS] Inserire la mail");
		mail=reader.nextLine();
		System.out.println("[REQS] Inserire password");
		password=reader.nextLine();
		
		//Controllo account
		for(PersonaChaletSmart persona: this.listaUtenti)
		{
			Account account = persona.getAccount();
			if(account!=null && account.getTipoAccount()!=null &&
			   account.getTipoAccount().equals(tipoAccount)
			   && account.getEmail().compareTo(mail)==0)
					if(account.controllaPassword(password))
						return account;
		}
		return null;
	}
	
	private PersonaChaletSmart creaPersonaChaletSmart(TipoAccount tipoAccount) 
	{
		String nome;
		String cognome;
		String dataNascita;
		String telefono;
		Scanner reader = new Scanner(System.in);
		PersonaChaletSmart ritorno=null;
		//Credenziali utente
		System.out.println("[REQS] Inserire nome");
		nome=reader.nextLine();
		System.out.println("[REQS] Inserire cognome");
		cognome=reader.nextLine();
		System.out.println("[REQS] Inserire data di nascita");
		dataNascita=reader.nextLine();
		System.out.println("[REQS] Inserire numero di telefono");
		telefono=reader.nextLine();
		switch (tipoAccount) 
		{
			case ACCOUNTUTENTESTANDARD:
				ritorno = new Cliente(nome, cognome , dataNascita, telefono);
				break;
			case ACCOUNTADDETTOATTIVITA:
				ritorno = new AddettoAttivita(nome,cognome,dataNascita,telefono);
				break;
			case ACCOUNTADDETTOBARCASSA:
				ritorno = new AddettoBarCassa(nome,cognome,dataNascita,telefono);
			break;
			case ACCOUNTADDETTOPRENOTAZIONI:
				ritorno = new AddettoPrenotazioni(nome,cognome,dataNascita,telefono);
				break;
			case ACCOUNTAMMINISTRATORE:
				ritorno = new Gestore(nome,cognome,dataNascita,telefono,this.spiaggia);
				break;
		}
		if(!this.listaUtenti.contains(ritorno)&&ritorno!=null)
			this.listaUtenti.add(ritorno);
		
		dbManager.caricaPersona(ritorno);
		return ritorno;
	}
	
	private void creaAccount(PersonaChaletSmart persona)
	{
		String userName;
		String email;
		String password;
		Scanner reader = new Scanner(System.in);
		
		System.out.println("[REQS] Inserire username");
		userName=reader.nextLine();
		System.out.println("[REQS] Inserire email");
		email=reader.nextLine();
		System.out.println("[REQS] Inserire password");
		password=reader.nextLine();
		Account account = new Account(userName, email, password);
		for(PersonaChaletSmart p:this.listaUtenti)
			if(p.getAccount()!=null &&  p.getAccount().equals(account))
			{
				System.out.println("[INFO] Account GIA' ESISTENTE");
				return;
			}
		if(persona instanceof Cliente)
		{
			account.setTipoAccount(TipoAccount.ACCOUNTUTENTESTANDARD);
		}
		if(persona instanceof AddettoAttivita)
		{
			account.setTipoAccount(TipoAccount.ACCOUNTADDETTOATTIVITA);
		}
		if(persona instanceof AddettoBarCassa)
		{
			account.setTipoAccount(TipoAccount.ACCOUNTADDETTOBARCASSA);
		}
		if(persona instanceof AddettoPrenotazioni)
		{
			account.setTipoAccount(TipoAccount.ACCOUNTADDETTOPRENOTAZIONI);
		}
		if(persona instanceof Gestore)
		{
			account.setTipoAccount(TipoAccount.ACCOUNTAMMINISTRATORE);
		}
		persona.setAccount(account);
		dbManager.caricaAccount(account, password,persona);
	}
	private void accediFunzionalitaUtenteStandard(Account account)
	{
		PersonaChaletSmart persona;
		String scelta;
		Scanner reader = new Scanner(System.in);
		do 
		{
			System.out.println("[INFO] Scegli 0 per prenotare un ombrellone");
			System.out.println("[INFO] Scegli 1 per prenotare un'attivita'");
			System.out.println("[INFO] Scegli 2 per creare un'ordinazione");
			System.out.println("[INFO] Scegli 3 per rimuovere una prenotazione spiaggia");
			System.out.println("[INFO] Scegli 4 per rimuovere una prenotazione attivita'");
			System.out.println("[INFO] Scegli 5 per rimuovere un'ordinazione");
			System.out.println("[INFO] Scegli 6 per visualizzare le tue prenotazioni spiaggia");
			System.out.println("[INFO] Scegli 7 per visualizzare le attivita alla quale sei prenotato");
			System.out.println("[INFO] Scegli 8 per visualizzare le ordinazioni");
			System.out.println("[INFO] Scegli 9 per visualizzare le notifiche");
			System.out.println("[INFO] Scegli -1 per uscire dalla sezione utente standard");
			
			scelta = reader.nextLine();

			switch (scelta) {
			case "0":
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
					prenotaOmbrellone((Cliente)persona);
				
				break;
			case "1":
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
					this.prenotaAttivita((Cliente)persona);
				break;
			case "2":
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
					this.creaOrdinazione((Cliente)persona);
				break;
			case "3":
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
					this.rimuoviPrenotazioneSpiaggiaDelCliente((Cliente)persona);
				break;
				
			case "4":
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
					this.rimuoviPrenotazioneAttivitaDelCliente((Cliente)persona);
				break;
			case "5":
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
					this.rimuoviOrdinazioneDelCliente((Cliente)persona);
				break;
			case "6":
				ArrayList<String> prenotazioni = new ArrayList<String>();
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
				{
					System.out.println("[INFO] Le prenotazioni per la spiaggia sono :");
					prenotazioni.addAll(this.visualizzaPrenotazioniDelCliente((Cliente)persona));
					if(prenotazioni.size()>0)
					{
						for(String s : prenotazioni)
							System.out.println(s);
					}
					else
						System.out.println("[INFO] NON RISULTANO prenotazioni per la spiaggia");
				}
			break;
			case "7":
				ArrayList<String> attivita = new ArrayList<String>();
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
				{
					attivita.addAll(this.visualizzaPrenotazioneAttivitaDelCliente((Cliente)persona));
					System.out.println("[INFO] Le attivita prenotate sono :");
					if(attivita.size()>0)
					{
						for(String s : attivita)
							System.out.println(s);
					}
					else
						System.out.println("[INFO] NON CI SONO attivita' a cui sei prenotato");
				}
			break;
			case "8":
				ArrayList<String> ordinazioni = new ArrayList<String>();
				persona = trovaPersonadellAccount(account);
				if(persona instanceof Cliente)
				{
					ordinazioni.addAll(this.visualizzaOrdinazioniDelCliente((Cliente)persona));
					System.out.println("[INFO] Le Ordinazioni effettuate sono :");
					if(ordinazioni.size()>0)
					{
						for(String s:ordinazioni)
							System.out.println(s);
					}
					else
						System.out.println("[INFO] NON CI SONO ordinazioni effettuate");
				}
			break;
			case "9":
				System.out.println("[INFO] Le tue notifiche sono :");
				if(trovaPersonadellAccount(account)!=null)
					for(String s : trovaPersonadellAccount(account).getListaNotificheOPromozioni())
						System.out.println(s);
			break;
			default:
				break;
			}
		}while(scelta.compareTo("-1")!=0);
	}
	
	private void accediFunzionalitaAddettoAttivita(Account account)
	{
		String scelta;
		PersonaChaletSmart clienteScelto;
		PersonaChaletSmart appPersona;
		
		Scanner reader = new Scanner(System.in);
		do 
		{
			System.out.println("[INFO] Scegli 0 per creare un nuovo cliente");
			System.out.println("[INFO] Scegli 1 per prenotare un'attivita' per cliente");
			System.out.println("[INFO] Scegli 2 per rimuovere la prenotazione attivita' per cliente");
			System.out.println("[INFO] Scegli 3 per visualizzare le prenotazioni delle attivita' del cliente");
			System.out.println("[INFO] Scegli 4 per aggiungere una prenotazione attrezzatura");
			System.out.println("[INFO] Scegli 5 per rimuovere una prenotazione attrezzatura");
			System.out.println("[INFO] Scegli 6 per visualizzare tutte le tue prenotazioni attrezzatura");
			System.out.println("[INFO] Scegli 7 per restituire l'attrezzatura");
			System.out.println("[INFO] Scegli 8 per visualizzare le notifiche");
			System.out.println("[INFO] Scegli -1 per uscire dalla sezione Addetto Attivita'");
			scelta = reader.nextLine();
			
			switch (scelta) 
			{
			case "0":
				this.creaPersonaChaletSmart(TipoAccount.ACCOUNTUTENTESTANDARD);
				break;
				
			case "1":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					this.prenotaAttivita((Cliente)clienteScelto);
				}
				break;
			case "2":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					this.rimuoviPrenotazioneAttivitaDelCliente((Cliente)clienteScelto);
				}
				break;
			case "3":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					ArrayList<String> attivita = new ArrayList<String>();
					attivita.addAll(this.visualizzaPrenotazioneAttivitaDelCliente((Cliente)clienteScelto));
					System.out.println("[INFO] Le attivita prenotate dal cliente "
							+clienteScelto.getNome()+" "+clienteScelto.getCognome()+ "sono :");
					if(attivita.size()>0)
					{
						for(String s : attivita)
							System.out.println(s);
					}
					else
						System.out.println("[INFO] NON CI SONO attivita' a cui sei prenotato");
				}
				break;
			case "4":
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona instanceof AddettoAttivita)
					this.prenotaAttrezzatura((AddettoAttivita)appPersona);
				break;
			case "5":
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona instanceof AddettoAttivita)
					this.rimuoviPrenotazioneAttrezzatura((AddettoAttivita)appPersona);
				break;
			case "6":
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona instanceof AddettoAttivita)
				{
					AddettoAttivita appAddetto = (AddettoAttivita)appPersona;
					System.out.println("[INFO] Le attrezzature prenotate sono: ");
					if(appAddetto.visualizzaPrenotazioniAttrezzatura().length>0)
						for(String s : appAddetto.visualizzaPrenotazioniAttrezzatura())
							System.out.println(s);
					else
						System.out.println("[INFO] NON CI SONO prenotazioni attrezzatura");
				}
				break;
			case"7" : 
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona instanceof AddettoAttivita)
				{
					ArrayList<PrenotazioneAttrezzatura> appPrenotazioneAttrezzatura = new ArrayList<PrenotazioneAttrezzatura>();
					AddettoAttivita appAddetto = (AddettoAttivita)appPersona;
					for(PrenotazioneAttrezzatura p: appAddetto.getListaPrenotazioniAttrezzatura())
						if(p.getOraRestituzione()==null)
							appPrenotazioneAttrezzatura.add(p);
					if(appPrenotazioneAttrezzatura.size()>0)
					{
						System.out.println("[INFO] Prenotazioni disponibili : ");
						PrenotazioneAttrezzatura prenotazioneSelezionata = this.selezionaElemento(appPrenotazioneAttrezzatura);
						System.out.println("[REQS] Inserire l'ora di restituzione");
						if(appAddetto.restituisciAttrezzatura(prenotazioneSelezionata, reader.nextLine()))
						{
							dbManager.restituzioneAttrezzatura(prenotazioneSelezionata);
							System.out.println("[INFO] Restituzione attrezzatura eseguita CON SUCCESSO");
						}

					}
				}
				break;
			case "8" :
				System.out.println("[INFO] Le tue notifiche sono :");
				if(trovaPersonadellAccount(account)!=null)
					for(String s : trovaPersonadellAccount(account).getListaNotificheOPromozioni())
						System.out.println(s);
			break;
			}
		}while(scelta.compareTo("-1")!=0);
	}

	private void accediFunzionalitaAddettoBarCassa(Account account)
	{
		String scelta;
		PersonaChaletSmart clienteScelto;
		
		Scanner reader = new Scanner(System.in);
		do 
		{
			System.out.println("[INFO] Scegli 0 per creare un'ordinazione per cliente");
			System.out.println("[INFO] Scegli 1 per rimuovere un'ordinazione per cliente");
			System.out.println("[INFO] Scegli 2 per visualizzare le ordinazioni del cliente");
			System.out.println("[INFO] Scegli 3 per visualizzare le notifiche");
			System.out.println("[INFO] Scegli -1 per uscire dalla sezione Addetto Attivita'");
			scelta = reader.nextLine();
			
			switch (scelta) 
			{
			case "0":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					this.creaOrdinazione((Cliente)clienteScelto);
				}
				break;
				
			case "1":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					this.rimuoviOrdinazioneDelCliente((Cliente)clienteScelto);
				}
				break;
			case "2":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					ArrayList<String> ordinazioni = new ArrayList<String>();
					ordinazioni.addAll(this.visualizzaOrdinazioniDelCliente((Cliente)clienteScelto));
					System.out.println("[INFO] Le ordinazioni sono : ");
					if(ordinazioni.size()>0)
					{
						for(String s : ordinazioni)
							System.out.println(s);
					}
					else
						System.out.println("[INFO] NON CI SONO ordinazioni effettuate");
				}
			break;
			case "3":
				System.out.println("[INFO] Le tue notifiche sono :");
				if(trovaPersonadellAccount(account)!=null)
					for(String s : trovaPersonadellAccount(account).getListaNotificheOPromozioni())
						System.out.println(s);
			break;
			}
		}while(scelta.compareTo("-1")!=0);
	}

	private void accediFunzionalitaAddettoPrenotazioni(Account account)
	{
		String scelta;
		PersonaChaletSmart clienteScelto;
		Scanner reader = new Scanner(System.in);
		do 
		{
			System.out.println("[INFO] Scegli 0 per creare una prenotazione per cliente");
			System.out.println("[INFO] Scegli 1 per rimuovere una prenotazione per cliente");
			System.out.println("[INFO] Scegli 2 per visualizzare le prenotazioni del cliente");
			System.out.println("[INFO] Scegli 3 per visualizzare le notifiche");
			System.out.println("[INFO] Scegli -1 per uscire dalla sezione Addetto Attivita'");
			scelta = reader.nextLine();
			
			switch (scelta) 
			{
			case "0":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					this.prenotaOmbrellone((Cliente)clienteScelto);;
				}
				break;
				
			case "1":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					this.rimuoviPrenotazioneSpiaggiaDelCliente((Cliente)clienteScelto);
				}
				break;
			case "2":
				clienteScelto = this.selezionaCliente();
				if(clienteScelto!=null && clienteScelto instanceof Cliente)
				{
					ArrayList<String> prenotazioni = new ArrayList<String>();
					prenotazioni.addAll(this.visualizzaPrenotazioniDelCliente((Cliente)clienteScelto));
					System.out.println("[INFO] Le prenotazioni sono : ");
					if(prenotazioni.size()>0)
					{
						for(String s : prenotazioni)
							System.out.println(s);
					}
					else
						System.out.println("[INFO] NON CI SONO prenotazioni effettuate");
				}
				break;
			case "3":
				System.out.println("[INFO] Le tue notifiche sono :");
				if(trovaPersonadellAccount(account)!=null)
					for(String s : trovaPersonadellAccount(account).getListaNotificheOPromozioni())
						System.out.println(s);
				break;
			}
		}while(scelta.compareTo("-1")!=0);
	}

	
	private void accediFunzionalitaGestore(Account account)
	{
		String scelta;
		PersonaChaletSmart appPersona;
		boolean trovato = false;
		Scanner reader = new Scanner(System.in);
		do 
		{
			System.out.println("[INFO] Scegli 0 per creare un'account");
			System.out.println("[INFO] Scegli 1 per modificare il rincaro di una stagione");
			System.out.println("[INFO] Scegli 2 per modificare il rincaro di una collocazione");
			System.out.println("[INFO] Scegli 3 per modificare l'orario di apertura della spiaggia");
			System.out.println("[INFO] Scegli 4 per modificare l'orario di chiusura della spiaggia");
			System.out.println("[INFO] Scegli 5 per modificare la capienza massima della spiaggia");
			System.out.println("[INFO] Scegli 6 per creare un'attivita' ");
			System.out.println("[INFO] Scegli 7 per rimuovere un'attivita'");
			System.out.println("[INFO] Scegli 8 per creare un ombrellone ");
			System.out.println("[INFO] Scegli 9 per rimuovere un ombrellone");
			System.out.println("[INFO] Scegli 10 per creare una promozione");
			System.out.println("[INFO] Scegli 11 per rimuovere una promozione");
			System.out.println("[INFO] Scegli 12 per visualizzare la spiaggia");
			System.out.println("[INFO] Scegli 13 per modificare il menu'");
			System.out.println("[INFO] Scegli 14 per visualizzare tutti gli addetti");
			System.out.println("[INFO] Scegli 15 per visualizzare tutti i clienti");
			System.out.println("[INFO] Scegli 16 per visualizzare le notifiche");
			System.out.println("[INFO] Scegli 17 per notificare tutti i clienti in un giorno specificato");
			System.out.println("[INFO] Scegli 18 per modificare l'orario di inizio o fine di un'attivita'");
			System.out.println("[INFO] Scegli 19 per modificare il costo di un lettino");
			System.out.println("[INFO] Scegli 20 per aggiungere un'attrezzatura");
			System.out.println("[INFO] Scegli 21 per rimuovere un'attrezzatura");
			System.out.println("[INFO] Scegli -1 per uscire dalla sezione del Gestore");
			scelta = reader.nextLine();
			
			switch (scelta) 
			{
			case "0":
				System.out.println("[INFO] Scegli il tipo di account da creare:");			
				TipoAccount appTipoAccount = this.selezionaElemento(Arrays.asList(TipoAccount.values()));
				if(appTipoAccount!=null)
				{
					PersonaChaletSmart appPersonaDaCreare = this.creaPersonaChaletSmart(appTipoAccount);
					if(appTipoAccount != TipoAccount.ACCOUNTUTENTESTANDARD)
						this.creaAccount(appPersonaDaCreare);
				}
				else
					System.out.println("[INFO] Account selezionato non valido");
				break;
				
			case "1":
				ArrayList<RincaroStagione> appRincaro = new ArrayList<>(Arrays.asList(RincaroStagione.values()));
				System.out.println("[INFO] Le stagioni disponibili sono: ");
				RincaroStagione stagioneScelta = this.selezionaElemento(appRincaro);
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					System.out.println("[REQS] Inserisci il nuovo rincaro stagione:");
					if(reader.hasNextFloat())
					{
						float appNuovoRincaro = reader.nextFloat();
							if(appNuovoRincaro>0)
							{
								appGestore.modificaRincaroStagione(stagioneScelta.getMese(), appNuovoRincaro);
								dbManager.modificaRincaroStagione(appNuovoRincaro, stagioneScelta.getMese());
							}
							else
								System.out.println("[INFO] Rincaro stagione NON VALIDO");
					}
					else
						System.out.println("[INFO] Rincaro stagione NON VALIDO");
					reader.nextLine();
				}
				break;
			case "2":
				ArrayList<TipoCollocazione> appTipo = new ArrayList<>(Arrays.asList(TipoCollocazione.values()));
				System.out.println("[INFO] Le collocazioni disponibili sono: ");
				TipoCollocazione collocazioneScelta = this.selezionaElemento(appTipo);
				if(collocazioneScelta==null)
					break;
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					System.out.println("[REQS] Inserisci il nuovo rincaro collocazione :");
					if(reader.hasNextFloat())
					{
						float appNuovoRincaro = reader.nextFloat();
							if(appNuovoRincaro>0)
							{
								appGestore.modificaRincaroCollocazione(collocazioneScelta, appNuovoRincaro);
								dbManager.modificaRincaroCollocazione(appNuovoRincaro, collocazioneScelta.getStringaAssociata());
							}
							else
								System.out.println("[INFO] Rincaro collocazione NON VALIDO");
					}
					else
						System.out.println("[INFO] Rincaro collocazione NON VALIDO");
					reader.nextLine();
				}
				break;
			case "3":
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					System.out.println("[INFO] L'orario di apertura attuale e' :");
					System.out.println(appGestore.getSpiaggia().getOrarioApertura());
					System.out.println("[REQS] Inserisci il nuovo orario di apertura");
					String appNuovoOrario = reader.nextLine();
					appGestore.modificaOrarioApertura(appNuovoOrario);
					dbManager.modificaOrarioAperturaSpiaggia(appNuovoOrario);
				}
				break;
				
			case "4":
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					System.out.println("[INFO] L'orario di chiusura attuale e' :");
					System.out.println(appGestore.getSpiaggia().getOrarioChiusura());
					System.out.println("[REQS] Inserisci il nuovo orario di chiusura");
					String appNuovoOrario = reader.nextLine();
					appGestore.modificaOrarioChiusura(appNuovoOrario);
					dbManager.modificaOrarioChiusuraSpiaggia(appNuovoOrario);
				}
				break;
			case "5" :
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					int appCapienza=-1;
					Gestore appGestore = (Gestore)appPersona;
					System.out.println("[INFO] La capienza massima attuale e' : ");
					System.out.print(appGestore.getSpiaggia().getCapienzaMaxSpiaggia());
					System.out.println("[REQS] Inserisci la nuova capienza massima (SE NON VALIDA non verra' cambiata)");
					if(reader.hasNextInt())
					{
						appCapienza=reader.nextInt();
						if(appCapienza>0)
						{
							appGestore.modificaCapienzaMaxSpiaggia(appCapienza);
							dbManager.modificaCapienzaMaxSpiaggia(appCapienza);
						}
						else
							System.out.println("[INFO] Capienza inserita non valida");
					}
					reader.nextLine();
				}
				break;
			case "6" :
				int appCapienza=-1;
				String appNomeIdentificativo,appOrarioInizio,appOrarioFine,appDataSvolgimento;
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					System.out.println("[REQS] Inserisci il nome dell'attivita'");
					appNomeIdentificativo=reader.nextLine();
					do
					{
						System.out.println("[REQS] Inserisci il numero massimo dei partecipanti");
						if(reader.hasNextInt())
						{
							appCapienza=reader.nextInt();
						}
						reader.nextLine();
					}while(appCapienza<=0);
					System.out.println("[REQS] Inserisci l'orario di inizio dell'attivita'");
					appOrarioInizio=reader.nextLine();
					System.out.println("[REQS] Inserisci l'orario di fine dell'attivita'");
					appOrarioFine=reader.nextLine();
					System.out.println("[REQS] Inserisci la data dello svolgimento dell'attivita'");
					appDataSvolgimento=reader.nextLine();
					Attivita appAttivita = new Attivita(appNomeIdentificativo, appCapienza, appOrarioInizio, appOrarioFine, appDataSvolgimento);
					appGestore.aggiungiAttivitaAListaAttivita(appAttivita);
					dbManager.caricaAttivita(appAttivita);
					System.out.println("[INFO] Attivita CREATA CON SUCCESSO");
					for(PersonaChaletSmart p: this.listaUtenti)
					{
						if(p instanceof Cliente)
						{
							p.aggiungiNotificaOPromozione(appAttivita.toString());
							dbManager.caricaNotifiche(appAttivita.toString(), p);
						}
					}
				}
				break;
			case "7":
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					Attivita attivitaSelezionata = this.selezionaElemento(spiaggia.getListaAttivita());
					if(attivitaSelezionata!=null)
					{
						if(appGestore.rimuoviAttivitaDaListaAttivita(attivitaSelezionata))
						{
							dbManager.rimuoviAttivita(attivitaSelezionata);
							System.out.println("[INFO] Attivita' rimossa CON SUCCESSO");
							for(Cliente c : attivitaSelezionata.getPartecipanti() )
							{
								c.aggiungiNotificaOPromozione("RIMOSSA-"+attivitaSelezionata.toString());
								dbManager.caricaNotifiche("RIMOSSA-"+attivitaSelezionata.toString(), c);
							}
						}
					}
					else
						System.out.println("[INFO] Attivita' selezionata NON VALIDA");
							
				}
			break;
			case "8" :
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					float appPrezzo=-1;
					String appDescrizione=null;
					int appId=-1,appCategoria=-1;
					TipoCollocazione appCollocazione=TipoCollocazione.AVANTI;
					System.out.println("[REQS] Inserisci il prezzo da dare all'ombrellone");
					if(reader.hasNextFloat())
					{
						appPrezzo= reader.nextFloat();
					}
					reader.nextLine();
					if(appPrezzo <=0)
					{
						System.out.println("[INFO] Prezzo non valido");
						break;
					}
					System.out.println("[REQS] Inserisci la descrizione da dare all'ombrellone");
					appDescrizione=reader.nextLine();
					System.out.println("[REQS] Inserisci l'id da dare all'ombrellone");
					if(reader.hasNextInt())
					{
						appId=reader.nextInt();
					}
					reader.nextLine();
					if(appId<0)
					{
						System.out.println("[INFO] Id non valido");
						break;
					}
					appCollocazione=this.selezionaElemento(Arrays.asList(TipoCollocazione.values()));
					if(appCollocazione==null)
					{
						System.out.println("[INFO] Collocazione non valida");
						break;
					}
					System.out.println("[REQS] Inserisci la categoria dell'ombrellone ( 0-1-2 )");
					if(reader.hasNextInt())
					{
						appCategoria=reader.nextInt();
					}
					reader.nextLine();

					if(appCategoria<=0 && appCategoria>2)
					{
						System.out.println("[INFO] Categoria non valida");
						break;
					}
					Ombrellone o = new Ombrellone(appPrezzo, appDescrizione, appId, appCollocazione, appCategoria);
					if(!this.spiaggia.getElencoOmbrelloni().contains(o))
					{
						appGestore.aggiungiOmbrelloneAElencoOmbrelloni(o);
						dbManager.caricaOmbrellone(o);
					}
					else
						System.out.println("[INFO] Ombrellone con ID gia esistente");
				}
			break;
			case "9" :
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					Ombrellone ombrelloneSelezionato = this.selezionaElemento(spiaggia.getElencoOmbrelloni());
					if(ombrelloneSelezionato!=null)
					{
						if(appGestore.rimuoviOmbrelloneDaElencoOmbrelloni(ombrelloneSelezionato))
						{
							dbManager.rimuoviOmbrellone(ombrelloneSelezionato);
							System.out.println("[INFO] Ombrellone rimosso CON SUCCESSO");
						}
					}
					else
						System.out.println("[INFO] Ombrellone selezionato NON VALIDO");
				}
			break;
			case "10":
				String nomeIdentificativo,descrizione,noteSullaValidita;
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					System.out.println("[REQS] Inserisci il nome della promozione");
					nomeIdentificativo=reader.nextLine();
					System.out.println("[REQS] Inserisci la descrizione della promozione");
					descrizione=reader.nextLine();
					System.out.println("[REQS] Inserisci delle note sulla validita della promozione");
					noteSullaValidita=reader.nextLine();
					Promozione appPromozione = new Promozione(nomeIdentificativo, descrizione, noteSullaValidita);
					appGestore.aggiungiPromozioneAListaPromozioni(appPromozione);
					System.out.println("[INFO] Promozione creata CON SUCCESSO");
					for(PersonaChaletSmart p: this.listaUtenti)
					{
						if(p instanceof Cliente)
						{
							p.aggiungiNotificaOPromozione(appPromozione.toString());
							dbManager.caricaNotifiche(appPromozione.toString(), p);
						}
					}
				}
			break;
			case "11":
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					Promozione promozioneScelta = this.selezionaElemento(spiaggia.getListaPromozioni());
					if(promozioneScelta!=null)
					{
						if(appGestore.rimuoviPromozioneDaListaPromozioni(promozioneScelta))
							System.out.println("[INFO] Promozione rimossa CON SUCCESSO");
						for(PersonaChaletSmart p: this.listaUtenti)
						{
							if(p instanceof Cliente)
							{
								p.rimuoviNotificaOPromozione(promozioneScelta.toString());
								dbManager.caricaNotifiche(promozioneScelta.toString(), p);
							}
										
						}
					}
					else
						System.out.println("[INFO] Promozione selezionata NON VALIDA");
					
				}
			break;
			case "12":
				appPersona = this.trovaPersonadellAccount(account);
				if(appPersona != null && appPersona instanceof Gestore)
				{
					Gestore appGestore = (Gestore)appPersona;
					System.out.println(appGestore.getSpiaggia().toString());
				}
			break;
			case "13" :
				this.modificaMenu();
			break;
			case "14" :
				System.out.println("[INFO] Gli addetti registrati sono :");
				trovato = false;
				for(PersonaChaletSmart p: this.listaUtenti)
					if(!(p instanceof Cliente))
					{
						System.out.println(p.toString());
						trovato = true;
					}
				if(!trovato)
					System.out.println("[INFO] NON CI SONO addetti registrati");
			break;
			case "15" :
				System.out.println("[INFO] I clienti registrati sono :");
				trovato = false;
				for(PersonaChaletSmart p: this.listaUtenti)
					if(p instanceof Cliente)
					{
						System.out.println(p.toString());
						trovato=true;
					}
				if(!trovato)
					System.out.println("[INFO] NON CI SONO clienti registrati");
			break;
			case "16":
				System.out.println("[INFO] Le tue notifiche sono :");
				if(trovaPersonadellAccount(account)!=null)
					for(String s : trovaPersonadellAccount(account).getListaNotificheOPromozioni())
						System.out.println(s);
				break;
			case "17" :
				Date dataSelezionata = this.specificaData();
				System.out.println("[INFO] Inserisci la notifica che vuoi comunicare a tutti i clienti del giorno specificato");
				for(Prenotazione p : this.spiaggia.getListaPrenotazioni())
				{
					Date appDataPrenotazione =p.getGiornoDellaPrenotazione();
					if(dataSelezionata.getDay()==appDataPrenotazione.getDay()&&
							dataSelezionata.getMonth()==appDataPrenotazione.getMonth()&&
							dataSelezionata.getYear()==appDataPrenotazione.getYear())
					{
						String appNotifica = reader.nextLine();
						p.getClientePrenotante().aggiungiNotificaOPromozione(appNotifica);
						dbManager.caricaNotifiche(appNotifica, p.getClientePrenotante());
					}

				}
			break;
			case "18" :
				if(spiaggia.getListaAttivita().size()>0)
				{
					String appScelta;
					Attivita attivitaSelezionata = this.selezionaElemento(spiaggia.getListaAttivita());
					if(attivitaSelezionata!=null)
					{
						System.out.println("[REQS] Inserisci 1 per modificare l'orario di inizio");
						System.out.println("[REQS] Inserisci 2 per modificare l'orario di fine");
						appScelta=reader.nextLine();
						if(appScelta.equals("1"))
						{
							System.out.println("[REQS] Inserisci il nuovo orario di inizio");
							String appInizio = reader.nextLine();
							dbManager.modificaOrarioInizioAttivita(attivitaSelezionata, appInizio);
							attivitaSelezionata.setOrarioInizio(appInizio);
							for(PersonaChaletSmart p : attivitaSelezionata.getPartecipanti())
							{
								p.aggiungiNotificaOPromozione("Modifica orario inizio "+attivitaSelezionata.toString());
								dbManager.caricaNotifiche("Modifica orario inizio "+attivitaSelezionata.toString(), p);
							}
						}
						else
							if(appScelta.equals("2"))
							{
								System.out.println("[REQS] Inserisci il nuovo orario di fine");
								String appFine = reader.nextLine();
								dbManager.modificaOrarioFineAttivita(attivitaSelezionata, appFine);
								attivitaSelezionata.setOrarioFine(appFine);
								for(PersonaChaletSmart p : attivitaSelezionata.getPartecipanti())
								{
									p.aggiungiNotificaOPromozione("Modifica orario Fine "+attivitaSelezionata.toString());
									dbManager.caricaNotifiche("Modifica orario Fine "+attivitaSelezionata.toString(), p);
								}
							}
							else
							System.out.println("[INFO] Scelta non valida");
					}
					else
						System.out.println("[INFO] Non ci sono attivita' disponibili");
				}
			break;
			case "19" :
				float appPrezzoLettino;
				System.out.println("[INFO] L'attuale costo dei lettine e' : "+spiaggia.getPrezzoLettino());
				System.out.println("[REQS] Inserisci il nuovo prezzo dei lettini ");
				if(reader.hasNextFloat())
				{
					appPrezzoLettino=reader.nextFloat();
					if(appPrezzoLettino>0)
					{
						spiaggia.setPrezzoLettino(appPrezzoLettino);
						dbManager.modificaPrezzoLettinoSpiaggia(appPrezzoLettino);
						System.out.println("[INFO] Prezzo cambiato CORRETTAMENTE");
					}
				}
				reader.nextLine();
				break;
				
			case "20":
				int appId;
				int appQuantita;
				System.out.println("[RESQ] Inserisci l'id della nuova attrezzatura da aggiungere (deve essere maggiore di 0)");
				if(reader.hasNextInt())
				{
					appId = reader.nextInt();
					if(appId<=0)
					{
						System.out.println("[INFO] Id NON VALIDO");
						reader.nextLine();
						break;
					}
				}
				else
				{
					System.out.println("[INFO] Id NON VALIDO");
					reader.nextLine();
					break;
				}
				reader.nextLine();
				System.out.println("[RESQ] Inserisci la quantita'complessiva della nuova attrezzatura da aggiungere");
				if(reader.hasNextInt())
				{
					appQuantita = reader.nextInt();
					if(appQuantita<=0)
					{
						System.out.println("[INFO] Quantita' NON VALIDA");
						reader.nextLine();
						break;
					}
				}
				else
				{
					System.out.println("[INFO] Quantita' NON VALIDA");
					reader.nextLine();
					break;
				}
				reader.nextLine();
				System.out.println("[REQS] Inserire la descrizione della nuova attrezzatura da aggiungere");
				Attrezzatura a = new Attrezzatura(appId, appQuantita, reader.nextLine());
				if(this.spiaggia.aggiungiAttrezzatura(a))
				{
					dbManager.caricaAttrezzatura(a);
					System.out.println("[INFO] Attrezzatura aggiunta CON SUCCESSO");
				}
				else
					System.out.println("[INFO] ID gia' presente");
				break;
			case "21":
				Attrezzatura appAttrezzatura = this.selezionaElemento(Arrays.asList(this.spiaggia.getListaAttrezzatura()));
				if(appAttrezzatura!=null)
				{
					if(this.spiaggia.rimuoviAttrezzatura(appAttrezzatura))
					{
						System.out.println("[INFO] Attrezzatura rimossa CON SUCCESSO");
					}
				}
				break;
			}
		}while(!scelta.equals("-1"));
	}
	
	private PersonaChaletSmart trovaPersonadellAccount(Account account)
	{
		for(PersonaChaletSmart persona:this.listaUtenti)
			if(persona.getAccount()!=null && persona.getAccount().equals(account))
				return persona;
		return null;
	}
	private void prenotaOmbrellone(Cliente cliente)
	{
		int continuareAdAggiungereOmbrelloni=0;
		Date data = specificaData();
		Scanner reader = new Scanner(System.in);
		ArrayList<Ombrellone>ombrelloniTrovati = new ArrayList<Ombrellone>();
		ArrayList<Ombrellone>ombrelloniScelti = new ArrayList<Ombrellone>();
		boolean trovatoOccupato = false;
		/**
		 * Trovo tutti gli ombrelloni disponibili per la data specificata
		 */
		for(Ombrellone o:spiaggia.getElencoOmbrelloni())
		{
			trovatoOccupato = false;
			for(Prenotazione p: spiaggia.getListaPrenotazioni())
			{
				if(p.getGiornoDellaPrenotazione().getDay()==data.getDay() &&
				   p.getGiornoDellaPrenotazione().getMonth()==data.getMonth() &&
				   p.getGiornoDellaPrenotazione().getYear()==data.getYear())
					for(Ombrellone ombrellone:p.getOmbrelloniAssociati())
					{
						if(ombrellone.equals(o))
							trovatoOccupato =true;
					}
			}
			/**
			 * Se trovo un ombrellone non occupato, in quella data,
			 *  lo aggiungo all'elenco degli ombrelloni trovati
			 */
			if(!trovatoOccupato)
			{
				ombrelloniTrovati.add(o);
			}
		}
		if(ombrelloniTrovati.size()>0)
		{
			System.out.println("[INFO] Quale ombrellone vuoi prenotare");

			do 
			{
				System.out.println("[INFO] Ombrelloni ancora disponibili : ");
				if(ombrelloniTrovati.size()==0)
				{
					System.out.println("[INFO] NON CI SONO ombrelloni disponibili");
				}
				else
				{
				Ombrellone OmbrelloneSelezionato = this.selezionaElemento(ombrelloniTrovati);
					if(OmbrelloneSelezionato!=null)
					{
						ombrelloniScelti.add(OmbrelloneSelezionato);
					}
				}
				if(ombrelloniScelti.size()>0)
				{
					System.out.println("\n[INFO] Hai selezionato i seguenti ombrelloni : ");
					for(Ombrellone o : ombrelloniScelti)
					{
						System.out.println(o.toString() + "\n");
					}
					System.out.println("\n[REQS] Vuoi aggiungere altri ombrelloni?\n-1 : SI\n-QUALSIASI ALTRA COSA : NO");
					if(reader.hasNextInt())
					{
						continuareAdAggiungereOmbrelloni = reader.nextInt();
					}
					if(continuareAdAggiungereOmbrelloni<0 && continuareAdAggiungereOmbrelloni>2)
					{
						reader.nextLine();
				        System.out.println("[INFO] SCELTA NON VALIDA");
						continue;
					}
					
					ombrelloniTrovati.remove(ombrelloniScelti.get(ombrelloniScelti.size()-1));
				}
				else
					continuareAdAggiungereOmbrelloni = 2;
			}while(continuareAdAggiungereOmbrelloni!=2 && ombrelloniTrovati.size()>0);
			
			if(ombrelloniTrovati.size()==0 && continuareAdAggiungereOmbrelloni==1)
				System.out.println("[INFO] NON CI SONO PIU' OMBRELLONI DISPONIBILI");
			if(ombrelloniScelti.size()>0)
			{
				TipoPrenotazione tipoPrenotazione;
				System.out.println("[REQS] Scegli il tipo di prenotazione\n-1 : MEZZA GIORNTA\n-Digita qualsiasi altra cosa per GIORNATA INTERA");
				reader.nextLine();
				String appTipoPrenotazione = reader.nextLine();
				tipoPrenotazione = ((appTipoPrenotazione.equals("1")) ? TipoPrenotazione.MEZZAGIORNATA : TipoPrenotazione.INTERO);
				
				boolean veritas=false;
				int numeroLettini=0;
				do
				{
					System.out.println("[REQS] Inserisci numeri di lettini da prenotare");
					
					if(reader.hasNextInt())
					{
						numeroLettini=reader.nextInt();
						if(numeroLettini>=0)
							veritas=true;
					}
					else
						reader.nextLine();
					
					if(!veritas)
						System.out.println("[INFO] Numero di lettini NON CORRETTO");
				}while(!veritas);

				/**
				 * Controllo se il cliente ha gia prenotazioni consecutive
				 */
				boolean appConsecutive=false;
				for(Prenotazione p : spiaggia.getListaPrenotazioni())
				{
					if(p.getClientePrenotante().equals(cliente))
					{
						
						long diffInMillies = Math.abs(p.getGiornoDellaPrenotazione().getTime() - data.getTime());
					    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
						if(diff<=1)
							{
								p.setAGiorniConsecutivi(true);
								dbManager.aggiornaGiorniConsecutivi(p, true);
								appConsecutive=true;
							}
					}
				}
				Ombrellone[] ombrelloniSceltiDaPassareAPrenotazione = new Ombrellone[ombrelloniScelti.size()];
				for(int i=0;i<ombrelloniScelti.size();i++)
					ombrelloniSceltiDaPassareAPrenotazione[i]=ombrelloniScelti.get(i);
				Prenotazione appPrenotazione = new Prenotazione(cliente, tipoPrenotazione, ombrelloniSceltiDaPassareAPrenotazione, numeroLettini, data);
				cliente.prenotaSpiaggiaClienteSmart(tipoPrenotazione, ombrelloniSceltiDaPassareAPrenotazione, numeroLettini, data, spiaggia);	
				this.spiaggia.getListaPrenotazioni().get(this.spiaggia.getListaPrenotazioni().indexOf(appPrenotazione)).setAGiorniConsecutivi(appConsecutive);
				Prenotazione appPrenotazione1 = this.spiaggia.getListaPrenotazioni().get(this.spiaggia.getListaPrenotazioni().indexOf(appPrenotazione));
				dbManager.caricaPrenotazione(appPrenotazione1);
			}

			else
			{
				System.out.println("[INFO] Per effettuare una prenotazioni devi selezionare ALMENO un ombrellone");

			}
		}
		/**
		 * Altrimenti se per il giorno specificato non ci sono ombrelloni disponibili
		 */
		else
	        System.out.println("[INFO] Ci rincresce, la spiaggia, per il giorno specificato, risulta al completo");
			
		
	}
	
	private Date specificaData()
	{
		Date data = new Date();
		Scanner reader = new Scanner(System.in);
		int giorno;
		int mese;
		int anno;
		boolean meseTrovato;
		while(true)
		{
			meseTrovato=false;
			reader = new Scanner(System.in);
			System.out.println("[REQS] Per quale giorno vuoi prenotare");
			if(reader.hasNextInt())
			{
				giorno = reader.nextInt();
			}
			else
			{
				reader.nextLine();
		        System.out.println("[INFO] Giorno NON CORRETTO, riprovare\n");
				continue;
			}
			System.out.println("[REQS] Per quale mese vuoi prenotare");
			if(reader.hasNextInt())
			{
				mese = reader.nextInt();
				for(RincaroStagione r:RincaroStagione.values())
				{
					if(mese==r.getMese())
					{
						meseTrovato=true;
					}
				}
				
				if(!meseTrovato)
				{
			        System.out.println("[INFO] Spiaggia CHIUSA nel mese specificato\n");
					continue;
				}
			}
			else
			{
				reader.nextLine();
		        System.out.println("[INFO] Mese NON CORRETTO, riprovare\n");
				continue;
			}
			System.out.println("[REQS] Per quale anno vuoi prenotare");
			if(reader.hasNextInt())
			{
				anno = reader.nextInt();
			}
			else
			{
				reader.nextLine();
		        System.out.println("[INFO] Anno NON CORRETTO, riprovare\n");
				continue;
			}

			if(!convalidaData(new String(mese+"/"+giorno+"/"+anno)))
				continue;
			else 
			{
				mese--;
				data.setDate(giorno);
				data.setMonth(mese);
				data.setYear(anno-1900);
				Date dataOggi = new Date();
				if(data.before(dataOggi))
				{
					System.out.println("[INFO] La data della prenotazione DEVE ESSERE successiva a quella odierna");
					continue;
				}
				return data;

			}
		}
	}

   private  boolean convalidaData(String strData)
   {
		if (strData.trim().equals(""))
		    return true;
		else
		{
		    SimpleDateFormat sdfrmt = new SimpleDateFormat("MM/dd/yyyy");
		    sdfrmt.setLenient(false);
		    try
		    {
		        sdfrmt.parse(strData); 
		    }
		    catch (ParseException e)
		    {
		        System.out.println("[INFO] Formato data NON CORRETTO, riprovare\n");
		        return false;
		    }
		    return true;
		}
   }
	
   private ArrayList<String> visualizzaPrenotazioniDelCliente(Cliente cliente)
   {
	   ArrayList<String> ritorno = new ArrayList<String>();
	   for(Prenotazione p : spiaggia.getListaPrenotazioni())
	   {
		   if(p.getClientePrenotante().equals(cliente))
		   {
			   ritorno.add(p.toString() + "\nPrezzo = "+p.prezzoTotale(this.spiaggia));
		   }
	   }
		return ritorno;
   }
   
   private void prenotaAttivita(Cliente cliente)
   {
		System.out.println("[INFO] Le attivita' disponibili sono: ");
		if(spiaggia.getListaAttivita().size()==0)
		{
			System.out.println("[INFO] NON CI SONO attivita' disponibili");
			return;
		}
		Attivita attivitaDaPrenotare = this.selezionaElemento(spiaggia.getListaAttivita());
		if(attivitaDaPrenotare!=null)
		{
			if(cliente.prenotaAttivitaClienteSmart(attivitaDaPrenotare))
			{
				System.out.println("[INFO] Attivita prenotata CON SUCCESSO");
				dbManager.inizializzaPrenotazioniAttivita(attivitaDaPrenotare, cliente);
				return;
			}
		}
		System.out.println("[INFO] Attivita selezionata NON VALIDA");
   }
   
   private ArrayList<String> visualizzaPrenotazioneAttivitaDelCliente(Cliente cliente)
   {
	   ArrayList<String> ritorno = new ArrayList<>();
	   if(this.spiaggia.getListaAttivita().size()>0)
	   {
		   for(Attivita a : this.spiaggia.getListaAttivita())
		   {
			   for(Cliente c: a.getPartecipanti())
				   if(c.equals(cliente))
				   {
					   ritorno.add(a.toString());
				   }
		   }
	   }
	   return ritorno;
   }
   
   private void rimuoviPrenotazioneSpiaggiaDelCliente(Cliente cliente)
   {
	   
	   ArrayList<Prenotazione> prenotazioniDelCliente = new ArrayList<Prenotazione>();
	   for(Prenotazione p : spiaggia.getListaPrenotazioni())
		   if(p.getClientePrenotante().equals(cliente))
			   prenotazioniDelCliente.add(p);
	   System.out.println("[INFO] Le prenotazione spiaggia sono: ");
		if(this.visualizzaPrenotazioniDelCliente(cliente).size()==0)
		{
			System.out.println("[INFO] NON CI SONO prenotazioni spiaggia");
			return;
		}
		Prenotazione prenotazioneDaRimuovere = this.selezionaElemento(prenotazioniDelCliente);
		if(prenotazioneDaRimuovere!=null)
		{
			if(cliente.rimuoviPrenotazioneSpaggiaSpecificaClienteSmart(prenotazioneDaRimuovere, spiaggia))
			{
				dbManager.rimuoviPrenotazioneSpiaggia(prenotazioneDaRimuovere);
				this.controlloGiorniConsecutivi(cliente);
				System.out.println("[INFO] Prenotazione spiaggia rimossa CON SUCCESSO");
				return;
			}
		}
		System.out.println("[INFO] Prenotazione spiaggia selezionata NON VALIDA");
   }
   
   private void rimuoviPrenotazioneAttivitaDelCliente(Cliente cliente)
   {
	   ArrayList<Attivita> attivitaPrenotate = new ArrayList<Attivita>();
	   for(Attivita a : spiaggia.getListaAttivita())
		   if(a.getPartecipanti().contains(cliente))
			   attivitaPrenotate.add(a);
	   System.out.println("[INFO] Le attivita' prenotate sono: ");
		if(attivitaPrenotate.size()==0)
		{
			System.out.println("[INFO] NON CI SONO prenotazioni attivita'");
			return;
		}
		Attivita attivitaDaRimuovere = this.selezionaElemento(attivitaPrenotate);
		if(attivitaDaRimuovere!=null)
		{
			if(cliente.rimuoviPrenotazioneAttivitaClienteSmart(attivitaDaRimuovere))
			{
				dbManager.rimuoviPrenotazioneAttivita(attivitaDaRimuovere, cliente);
				System.out.println("[INFO] Prenotazione attivita' rimossa CON SUCCESSO");
				return;
			}
		}
		System.out.println("[INFO] Prenotazione attivita' selezionata NON VALIDA");
   }
   
   private void creaOrdinazione(Cliente cliente) 
   {
	   Ombrellone ombrelloneSelezionato = null;
	   ArrayList<Ombrellone> ombrelloniDelCliente = new ArrayList<Ombrellone>();
	   Scanner reader = new Scanner(System.in);
	   int numeroOmbrelloneSelezionato=-1;
	   for(Prenotazione p:spiaggia.getListaPrenotazioni())
	   {
		   if(p.getClientePrenotante().equals(cliente))
		   {
			   ombrelloniDelCliente.addAll(p.getOmbrelloniAssociati());
		   }
	   }
	   
	   if(ombrelloniDelCliente.size()==0)
	   {
		   System.out.println("[INFO] Per effettuare un ordinazione devi aver prenotato ALMENO un ombrellone");
		   return;
	   }
	   
	   if(ombrelloniDelCliente.size()>1)
	   {
		   for(int i=0;i<ombrelloniDelCliente.size();i++)
		   {
			   System.out.println(i+"---"+ombrelloniDelCliente.get(i).toString());
		   }
		   System.out.println("[INFO] Seleziona l'ombrellone per il quale crare l'ordinazione o QUALSIASI ALTRA COSA per annullare la selezione");
		   if(reader.hasNextInt())
		   {
			   numeroOmbrelloneSelezionato= reader.nextInt();
			   if(numeroOmbrelloneSelezionato>=0 && numeroOmbrelloneSelezionato<ombrelloniDelCliente.size())
			   {
				   ombrelloneSelezionato=ombrelloniDelCliente.get(numeroOmbrelloneSelezionato);
			   }
			   else
				   return;
		   }
		   else
		   {
			   reader.nextLine();
			   return;
		   }
	   }
	   else
	   {
		   ombrelloneSelezionato = ombrelloniDelCliente.get(0);
	   }
	   Ordinazione o = cliente.creaOrdinazioneClienteSmart(ombrelloneSelezionato);
	   do 
	   {
		   this.aggiungiProdottoAOrdinazione(o);
		   System.out.println("[REQS] Vuoi continuare ad aggiungere prodotti all'ordinazione\n1 : SI\nQUALSIASI ALTRA COSA per NO");
	   }while(reader.nextLine().equals("1"));
	   if(o.getInsiemeProdotti().size()>0)
	   {
		   System.out.println("[INFO] Ordinazione completata");
		   System.out.println(o.toString());
		   dbManager.caricaOrdinazione(o);
		   this.spiaggia.aggiungiOrdinazione(o); 
	   }
	
   }
   private void aggiungiProdottoAOrdinazione(Ordinazione ordinazione)
   {
	   Menu menu = spiaggia.getMenu();
	   Scanner reader = new Scanner(System.in);
	   int quantita=1;
	   String scelta;
	   ArrayList<Prodotto> prodottiDisponibili = new ArrayList<Prodotto>();
	   
	   if(menu!=null && (menu.getElencoBibite().size()>0 || menu.getElencoPiatti().size()>0))
	   {
		   System.out.println("[REQS] Digitare 1 : Per selezionare un PIATTO\n"
		   		+ "[REQS] Digitare 2 : Per seleziona una BIBITA\n"
		   		+ "[REQS] Digitare QUALSIASI ALTRA COSA per interrompere");
		   
		   scelta=reader.nextLine();
		   
		   switch(scelta) 
		   {
			   case "1":
					System.out.println("[INFO] I PIATTI disponibili sono: ");
					if(menu.getElencoPiatti().size()==0)
					{
						System.out.println("[INFO] NON CI SONO PIATTI disponibili");
						return;
					}
					else
						for(Piatto p:menu.getElencoPiatti())
							if(p.getIsDisponibile())
								prodottiDisponibili.add(p);

				   break;
			   case "2":
					System.out.println("[INFO] Le BIBITE disponibili sono: ");
					if(menu.getElencoBibite().size()==0)
					{
						System.out.println("[INFO] NON CI SONO BIBITE disponibili");
						return;
					}
					else
						for(Bibita b:menu.getElencoBibite())
							if(b.getIsDisponibile())
								prodottiDisponibili.add(b);
				   break;
			   default:
				   return;
		   }

			Prodotto prodottoDaAggiungere = this.selezionaElemento(prodottiDisponibili);
			if(prodottoDaAggiungere!=null)
			{
			   System.out.println("[REQS] Seleziona la quantita' desiderata o (in caso di errore si selezionera' un'unita' di quel prodotto) :");
			   if(reader.hasNextInt())
			   {
				   quantita=reader.nextInt();
			   }
			   else
			   {
				   quantita=1;
				   reader.nextLine();
			   }
			   if(ordinazione.aggiungiProdottiAdElencoProdotti(prodottoDaAggiungere,quantita))
			   {
					System.out.println("[INFO] Prodotto aggiunto CON SUCCESSO");
					return;
			  }
		  }
			System.out.println("[INFO] Prodotto selezionato NON VALIDO");
	   }
   }
   
   private ArrayList<String> visualizzaOrdinazioniDelCliente(Cliente cliente)
   {
	   ArrayList<String> ritorno = new ArrayList<>();
	   for(Ordinazione o: spiaggia.getListaOrdinazioni())
	   {
		   if(o.getClienteOrdinante().equals(cliente))
			   ritorno.add(o.toString());
	   }
	   return ritorno;
   }
   private void rimuoviOrdinazioneDelCliente(Cliente cliente)
   {
	   ArrayList<Ordinazione> ordinazioniDelCliente = new ArrayList<Ordinazione>();
	   for(Ordinazione o : spiaggia.getListaOrdinazioni())
		   if(o.getClienteOrdinante().equals(cliente))
			   ordinazioniDelCliente.add(o);
		System.out.println("[INFO] Le ordinazioni sono : ");
		if(ordinazioniDelCliente.size()==0)
		{
			System.out.println("[INFO] NON CI SONO ordinazioni selezionate");
			return;
		}
		Ordinazione ordinazioniDaRimuovere = this.selezionaElemento(ordinazioniDelCliente);
		if(ordinazioniDaRimuovere!=null)
		{
			if(cliente.rimuoviOrdinazioneClienteSmart(ordinazioniDaRimuovere, spiaggia))
			{
				System.out.println("[INFO] Ordinazione rimossa CON SUCCESSO");
				dbManager.rimuoviOrdinazione(ordinazioniDaRimuovere);
				return;
			}
		}
		System.out.println("[INFO] Ordinazione selezionata NON VALIDA");
   }
   
   private PersonaChaletSmart selezionaCliente()
   {
	   ArrayList<PersonaChaletSmart> appoggioClienti = new ArrayList<PersonaChaletSmart>();
	   System.out.println("[INFO] Clienti registrati:");
		for(PersonaChaletSmart p : this.listaUtenti)
			if(p instanceof Cliente)
				appoggioClienti.add(p);
		if(appoggioClienti.size()==0)
		{
			System.out.println("[INFO] NON CI SONO clienti registrati");
			return null;
		}
		PersonaChaletSmart clienteSelezionato = this.selezionaElemento(appoggioClienti);
		if(clienteSelezionato!=null)
		{
				return clienteSelezionato;
		}
		System.out.println("[INFO] Cliente selezionato NON VALIDO");
		return null;
   }
   
   private void prenotaAttrezzatura(AddettoAttivita addettoAttivita)
   {	   
	   Scanner reader = new Scanner(System.in);
	   int appRimanenti =0;
	   int quantita=1;
	   ArrayList<Attrezzatura> attrezzaturaDisponibile = new ArrayList<Attrezzatura>();
	   for(int i=0;i<spiaggia.getListaAttrezzatura().length;i++)
	   {
		   appRimanenti = this.attrezzaturaRimanente(spiaggia.getListaAttrezzatura()[i]);
		   if(appRimanenti>0)
		   {
			   attrezzaturaDisponibile.add(spiaggia.getListaAttrezzatura()[i]);
		   }
	   }
	   System.out.println("[INFO] Le attrezzature disponibili sono: ");
		if(attrezzaturaDisponibile.size()==0)
		{
			System.out.println("[INFO] NON CI SONO attrezzature disponibili");
			return;
		}
		Attrezzatura attrezzaturaDaAggiungere = this.selezionaElemento(attrezzaturaDisponibile);
		if(attrezzaturaDaAggiungere!=null)
		{
			 System.out.println("[REQS] Seleziona la quantita' (in caso di errore si selezionera' un'unita' di quell'attrezzatura):\n"+
			 "Quantita' rimanente: "+attrezzaturaRimanente(attrezzaturaDaAggiungere));
			   if(reader.hasNextInt())
			   {
				   quantita=reader.nextInt();
				   if(quantita<0 || quantita>attrezzaturaRimanente(attrezzaturaDaAggiungere))
					   quantita=1;
			   }
			   reader.nextLine();
			   System.out.println("[REQS] Seleziona l'orario per il quale vuoi prenotare l'attrezzatura");
			   String appOraInizio = reader.nextLine();
			   PrenotazioneAttrezzatura appPrenotazione = new PrenotazioneAttrezzatura(attrezzaturaDaAggiungere.getIdTipoAttrezzatura(),quantita,appOraInizio, addettoAttivita);
			   addettoAttivita.aggiungiPrenotazioneAttrezzatura(appPrenotazione);
			   System.out.println("[INFO] Attrezzatura prenotata CON SUCCESSO");
			   dbManager.caricaPrenotazioneAttrezzatura(appPrenotazione);
				
		}
		else
			System.out.println("[INFO] Prenotazione attrezzatura selezionata NON VALIDA");
   }
   private int attrezzaturaRimanente(Attrezzatura attrezzatura)
   {
	   int attrezzaturaPrenotata =0;
	   for(PersonaChaletSmart p : this.listaUtenti)
	   {
		   if(p instanceof AddettoAttivita)
		   {
			   AddettoAttivita addetto = (AddettoAttivita)p;
			   for(PrenotazioneAttrezzatura prenotazioni: addetto.getListaPrenotazioniAttrezzatura())
			   {
				   if(prenotazioni.getIdTipoAttrezzatura()==attrezzatura.getIdTipoAttrezzatura() && prenotazioni.getOraRestituzione()==null)
					   attrezzaturaPrenotata += prenotazioni.getQuantitaAttrezzaturaPrenotata();
			   }
		   }
	   }
	   return attrezzatura.getQuantitaComplessiva()-attrezzaturaPrenotata;
   }
   
   private void rimuoviPrenotazioneAttrezzatura(AddettoAttivita addettoAttivita)
   {		
		System.out.println("[INFO] Le attrezzature prenotate sono: ");
		if(addettoAttivita.visualizzaPrenotazioniAttrezzatura().length==0)
		{
			System.out.println("[INFO] NON CI SONO prenotazioni attrezzatura");
			return;
		}
		PrenotazioneAttrezzatura prenotazioneDaRimuovere = this.selezionaElemento(addettoAttivita.getListaPrenotazioniAttrezzatura());
		if(prenotazioneDaRimuovere!=null)
		{
			if(addettoAttivita.rimuoviPrenotazioneAttrezzatura(prenotazioneDaRimuovere))
			{
				System.out.println("[INFO] Prenotazione attrezzatura rimossa CON SUCCESSO");
				dbManager.rimuoviPrenotazioneAttrezzatura(prenotazioneDaRimuovere);
				return;
			}
		}
		System.out.println("[INFO] Prenotazione attrezzatura selezionata NON VALIDA");
   }
   
   private <E> E selezionaElemento(Collection<E> insiemeDiElementi)
   {
	   ArrayList<E> appElementi = new ArrayList<>();
	   Scanner reader = new Scanner(System.in);
	   int indiceScelto=-1;
	   Iterator<E> iteratore = insiemeDiElementi.iterator();
	   int indice=0;
	   while(iteratore.hasNext())
	   {
		   appElementi.add(iteratore.next());
		   System.out.println("\n" + indice+"---"+appElementi.get(indice).toString());
		   indice++;
	   }
	   System.out.println("[REQS] Seleziona il numero dell'elemento per selezionarlo o QUALSIASI ALTRA COSA per annullare la selezione ");
	   if(reader.hasNextInt())
	   {
		   indiceScelto=reader.nextInt();
		   if(indiceScelto>=0 && indiceScelto<appElementi.size())
		   {
			   return appElementi.get(indiceScelto);
		   }
	   }
	   reader.nextLine();
	   return null;
   }
   
   private void modificaMenu()
   {

		String scelta;
		
		Scanner reader = new Scanner(System.in);
		do 
		{
			System.out.println("[INFO] Scegli 0 per creare un nuovo prodotto");
			System.out.println("[INFO] Scegli 1 per rimuovere un prodotto dal menu'");
			System.out.println("[INFO] Scegli 2 per aggiungere un prodotto nel menu'");
			System.out.println("[INFO] Scegli 3 per impostare la disponibilita' dei prodotti");
			System.out.println("[INFO] Scegli 4 per stampare il menu' attuale");
			System.out.println("[INFO] Scegli 5 per modificare la data di inizio del menu'");
			System.out.println("[INFO] Scegli 6 per modificare la data di fine del menu'");

			System.out.println("[INFO] Scegli -1 per uscire dalla sezione di modifica del menu'");
			scelta = reader.nextLine();
			
			switch (scelta) 
			{
			case "0" :
				Prodotto appProdotto = this.creaProdotto();
				if(appProdotto!=null)
				{
					if(this.prodottiCreati.contains(appProdotto))
						System.out.println("[INFO] Prodotto gia' esistente (NON POSSONO ESISTERE ID UGUALI)");
					else
					{
						this.prodottiCreati.add(appProdotto);
						System.out.println("[INFO] Prodotto creato CON SUCCESSO");
					}
				}
				break;
			case "1" :
				String appScelta;
				System.out.println("[INFO] Scegli 0 per rimuovere un piatto\n[INFO] Scegli 1 per rimuovere una bibita");
				appScelta=reader.nextLine();
				if(appScelta.equals("0"))
				{
					ArrayList<Piatto>appPiatti = new ArrayList<Piatto>();
					for(Piatto p : spiaggia.getMenu().getElencoPiatti())
						appPiatti.add(p);
					Piatto piattoDaRimuovere = this.selezionaElemento(appPiatti);
					if(piattoDaRimuovere!=null)
					{
						spiaggia.getMenu().rimuoviPiattoDaElencoPiatti(piattoDaRimuovere);
						dbManager.rimuoviPiattoDaMenu(piattoDaRimuovere.getID());
						System.out.println("[INFO] Piatto rimosso CON SUCCESSO");
					}
					else
						System.out.println("[INFO] Piatto selezionato non valido");
				}
				if(appScelta.equals("1"))
				{
					ArrayList<Bibita>appBibite = new ArrayList<Bibita>();
					for(Bibita b : spiaggia.getMenu().getElencoBibite())
						appBibite.add(b);
					Bibita bibitaDaRimuovere = this.selezionaElemento(appBibite);
					if(bibitaDaRimuovere!=null)
					{
						spiaggia.getMenu().rimuoviBibitaDaElencoBibite(bibitaDaRimuovere);
						dbManager.rimuoviBibitaDaMenu(bibitaDaRimuovere.getID());
						System.out.println("[INFO] bibita rimossa CON SUCCESSO");

					}
					else
						System.out.println("[INFO] Bibita selezionata non valida");
				}

			break;
			case "2":
				System.out.println("[INFO] Scegli 1 per aggiungere un piatto\n[INFO] Scegli 2 per aggiungere una bibita\n[INFO] Scegli QUALSIASI ALTRA COSA per uscire");
				appScelta=reader.nextLine();
				if(appScelta.equals("1"))
				{
					ArrayList<Piatto>appPiatti = new ArrayList<Piatto>();
					for(Prodotto p : this.prodottiCreati)
						if(p instanceof Piatto)
							appPiatti.add((Piatto)p);
					if(appPiatti.size()>0)
					{
						Piatto piattoDaAggiungere = this.selezionaElemento(appPiatti);
						if(piattoDaAggiungere!=null)
						{
							spiaggia.getMenu().aggiungiPiattoAdElencoPiatti(piattoDaAggiungere);
							dbManager.caricaPiattoNelMenu(piattoDaAggiungere);
							System.out.println("[INFO] Piatto aggiunto CON SUCCESSO");
						}
						else
							System.out.println("[INFO] Piatto selezionato non valido");
					}
					else
						System.out.println("[INFO] Nessun piatto disponibile");
				}
				if(appScelta.equals("2"))
				{
					ArrayList<Bibita>appBibite = new ArrayList<Bibita>();
					for(Prodotto p : this.prodottiCreati)
						if(p instanceof Bibita)
							appBibite.add((Bibita)p);
					if(appBibite.size()>0)
					{
						Bibita bibitaDaAggiungere = this.selezionaElemento(appBibite);
						if(bibitaDaAggiungere!=null)
						{
							spiaggia.getMenu().aggungiBibitaAdElencoBibite(bibitaDaAggiungere);
							dbManager.caricaBibitaNelMenu(bibitaDaAggiungere);
							System.out.println("[INFO] Bibita aggiunta CON SUCCESSO");
						}
						else
							System.out.println("[INFO] Bibita selezionata non valida");
					}
					else
						System.out.println("[INFO] Nessuna bibita disponibile");
				}

			break;
			case "3" :
				System.out.println("[INFO] Scegli 1 per selezionare un piatto\n[INFO] Scegli 2 per selezionare una bibita\n[INFO] Scegli QUALSIASI ALTRA COSA per uscire");
				appScelta=reader.nextLine();
				if(appScelta.equals("1"))
				{
					if(spiaggia.getMenu().getElencoPiatti().size()>0)
					{
						Piatto piattoDaModificare = this.selezionaElemento(spiaggia.getMenu().getElencoPiatti());
						if(piattoDaModificare!=null)
						{
							
							System.out.println("[REQS] Scegli 1 per impostare il piatto come TERMINATO\n[REQS] Scegli QUALSIASI ALTRA COSA se il piatto e' disponibile");
							if(reader.nextLine().equals("1"))
								piattoDaModificare.setIsDisponibile(false);
							else
								piattoDaModificare.setIsDisponibile(true);
							System.out.println("[INFO] Disponibilita' modificata CON SUCCESSO");

						}
						else
							System.out.println("[INFO] Piatto selezionato non valido");
					}
					else
						System.out.println("[INFO] Nessun piatto disponibile");
				}
				if(appScelta.equals("2"))
				{
					if(this.spiaggia.getMenu().getElencoBibite().size()>0)
					{
						Bibita bibitaDaModificare = this.selezionaElemento(this.spiaggia.getMenu().getElencoBibite());
						if(bibitaDaModificare!=null)
						{
							System.out.println("[REQS] Scegli 1 per impostare la bibita come TERMINATA\n[REQS] Scegli QUALSIASI ALTRA COSA se la bibita e' disponibile");
							if(reader.nextLine().equals("1"))
								bibitaDaModificare.setIsDisponibile(false);
							else
								bibitaDaModificare.setIsDisponibile(true);
							System.out.println("[INFO] Disponibilita' modificata CON SUCCESSO");

						}
						else
							System.out.println("[INFO] Bibita selezionata non valida");
					}
					else
						System.out.println("[INFO] Nessuna bibita disponibile");
				}

			break;
			case "4" :
				System.out.println(this.spiaggia.getMenu().toString());
			break;
			case "5":
				System.out.println("[REQS] Inserisci la nuova data di inizio del menu'");
				String appData = reader.nextLine();
				spiaggia.getMenu().setDataInizio(appData);
				dbManager.cambiaDataInizio(appData);
			break;
			case "6":
				System.out.println("[REQS] Inserisci la nuova data di fine del menu'");
				String appDataFine = reader.nextLine();
				spiaggia.getMenu().setDataFine(appDataFine);
				dbManager.cambiaDataFine(appDataFine);
			break;
			}
		}while(scelta.compareTo("-1")!=0);
	}
   private Prodotto creaProdotto()
   {
	   String scelta;
	   int appId=-1;
	   float appCosto=-1;
	   String appNome,appDescrizione,appIngredienti,appAllergeni,AppTipo;
	   boolean appAlcolico = false;
	   Scanner reader = new Scanner(System.in);
	   
	   System.out.println("[INFO] Scegli 1 per creare un piatto");
	   System.out.println("[INFO] Scegli 2 per creare una bibita");
	   System.out.println("[INFO] QUALSIASI ALTRA COSA per uscire");
	   scelta = reader.nextLine();
	   
	   System.out.println("[REQS] Inserisci l'ID del prodotto");
	   if(reader.hasNextInt())
	   {
		   appId=reader.nextInt();
	   }
	   if(appId<0)
	   {
		   System.out.println("[INFO] ID non valido");
		   return null;
	   }
	   reader.nextLine();
	   
	   System.out.println("[REQS] Inserisci il nome del prodotto");
	   appNome = reader.nextLine();
	   
	   System.out.println("[REQS] Inserisci la descrizione del prodotto");
	   appDescrizione = reader.nextLine();
	   
	   System.out.println("[REQS] Inserisci il costo del prodotto");
	   if(reader.hasNextFloat())
	   {
		   appCosto=reader.nextFloat();
	   }
	   if(appCosto<0)
	   {
		   System.out.println("[INFO] Costo non valido");
		   return null;
	   }
	   reader.nextLine();

	   if(scelta.equals("1"))
	   {
		   System.out.println("[REQS] Inserisci gli ingredienti del prodotto");
		   appIngredienti = reader.nextLine();
		   
		   System.out.println("[REQS] Inserisci gli allergeni del prodotto");
		   appAllergeni = reader.nextLine();
		   
		   System.out.println("[REQS] Inserisci il tipo del prodotto");
		   AppTipo = reader.nextLine();
		   Piatto appPiatto = new Piatto(appId, appNome, appDescrizione, appCosto, appIngredienti, appAllergeni, AppTipo);
		   dbManager.caricaPiatto(appPiatto);
		   return appPiatto;
	   }
	   
	   if(scelta.equals("2"))
	   {
		   System.out.println("[REQS] Inserire 1 : se il prodotto e' alcolico o QUALSIASI ALTRA COSA se non lo e'");
		   if(reader.nextLine().equals("1"))
			   appAlcolico=true;
		   else
			   appAlcolico=false;
		   Bibita appBibita = new Bibita(appId, appNome, appDescrizione, appCosto, appAlcolico);
		   dbManager.caricaBibita(appBibita);
		   return appBibita;
	   }
	   return null;
   }
   
   private void controlloGiorniConsecutivi(Cliente cliente)
   {
	   ArrayList<Prenotazione> appPrenotazioneCliente = new ArrayList<Prenotazione>();
	   long diffInMillies=0;
	   long diff=0;
	   for(Prenotazione p : spiaggia.getListaPrenotazioni())
	   {
		   if(p.getClientePrenotante().equals(cliente))
		   {
			   appPrenotazioneCliente.add(p);
		   }
	   }
	   boolean appConsecutivi = false;
	   for(Prenotazione i : spiaggia.getListaPrenotazioni())
	   {
		   appConsecutivi=false;
		   for(Prenotazione j : spiaggia.getListaPrenotazioni())
		   {
			   if(!i.equals(j))
			   {
					diffInMillies = Math.abs(i.getGiornoDellaPrenotazione().getTime() - j.getGiornoDellaPrenotazione().getTime());
				    diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
				    if(diff<=1)
				    {
				    	appConsecutivi=true;
				    	break;
				    }
			   }
		   }
		   if(i.isAGiorniConsecutivi()!=appConsecutivi)
		   {
			   i.setAGiorniConsecutivi(appConsecutivi);
			   dbManager.aggiornaGiorniConsecutivi(i, appConsecutivi);
		   }
	   }
   }
}
