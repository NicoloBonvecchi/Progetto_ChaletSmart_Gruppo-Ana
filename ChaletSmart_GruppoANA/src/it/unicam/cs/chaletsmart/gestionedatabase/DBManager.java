package it.unicam.cs.chaletsmart.gestionedatabase;

import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import it.unicam.cs.chaletsmart.account.Account;
import it.unicam.cs.chaletsmart.account.TipoAccount;
import it.unicam.cs.chaletsmart.attrezzatura.Attrezzatura;
import it.unicam.cs.chaletsmart.attrezzatura.PrenotazioneAttrezzatura;
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
import it.unicam.cs.chaletsmart.serviziospiaggia.RincaroStagione;
import it.unicam.cs.chaletsmart.serviziospiaggia.Spiaggia;
import it.unicam.cs.chaletsmart.serviziospiaggia.TipoCollocazione;
import it.unicam.cs.chaletsmart.serviziospiaggia.TipoPrenotazione;

public class DBManager 
{
	private Connection conn = null;

	public DBManager(String url, String name, String password)
	{
		try 
		{
			Class.forName("org.postgresql.Driver");
		} 
		catch (ClassNotFoundException e)
		{
			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
		}	
		try 
		{
			conn = DriverManager.getConnection(url, name, password);
		}
		catch (Exception e) 
		{
			System.out.println("Problems in opening a connection to the DB");
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() 
	{
		try
		{
			return !conn.isClosed();
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public void caricaAccount(Account account,String password,PersonaChaletSmart persona)
	{
		String query;
		query = "insert into account (nomeutente,email,password,tipoaccount) values ('"+ 
		account.getNomeUtente()+"','"+account.getEmail()+"','"+password+"','"
		+account.getTipoAccount().getStringaAssociata()+"');";
		ResultSet resultSet;
		try
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch(Exception e)
		{
			System.out.println("caricaAccount 1 "+e.toString());
			return;
		}
		
		query = "Select ID from account WHERE email= '"+account.getEmail()+"';";
		
		try
		{
			Statement statement = conn.createStatement();
			resultSet =statement.executeQuery(query);
			int appId;
			if(resultSet.next())
			{
				appId=resultSet.getInt(1);
				query = "Update personachaletsmart Set accountid= "+appId+" WHERE nome = '"+persona.getNome()+"'and cognome = '"+
				persona.getCognome()+"'and datanascita = '"+persona.getDataN()+"'and telefono = '"+persona.getTel()+"';";
				statement.execute(query);
			}
		}
		catch (Exception e) 
		{
			System.out.println("caricaAccount 2 "+e.toString());
		}
		
	}
	public void caricaPersona(PersonaChaletSmart persona)
	{
		String query;
		try
		{
			query = "insert into personachaletsmart (nome,cognome,datanascita,telefono) values ('"+ 
					persona.getNome()+"','"+persona.getCognome()+"','"+persona.getDataN()+"','"
					+persona.getTel()+"');";
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("caricaPersona "+e.toString());
			return;
		}
		
	}

	public ArrayList<PersonaChaletSmart> inizializzaListaUtenti(Spiaggia spiaggia)
	{
		ArrayList<PersonaChaletSmart> risultato = new ArrayList<PersonaChaletSmart>();
		String queryPersone = "select * from personachaletsmart;";
		try
		{
			Statement statement = conn.createStatement();
			Statement statement2 = conn.createStatement();

			ResultSet resultPersona = statement.executeQuery(queryPersone);
			String queryAccount;
			while(resultPersona.next())
			{
				int appIdAccount = resultPersona.getInt(6);
				String appNome = resultPersona.getString(2);
				String appCognome = resultPersona.getString(3);
				String appDataNascita = resultPersona.getString(4);
				String appTelefono = resultPersona.getString(5);
				int appIdPersona = resultPersona.getInt(1);
				queryAccount = "Select * from account where id = "+appIdAccount +";";
				ResultSet resultAccount = statement2.executeQuery(queryAccount);
				Account appAccount = null;
				String tipoAccount=null;

				if(resultAccount.next())
				{
					appAccount = new Account(resultAccount.getString(2), resultAccount.getString(3), resultAccount.getString(4));
					tipoAccount = resultAccount.getString(5);
				}
				
				if(tipoAccount!=null)
				{
					switch(tipoAccount)
					{
						case "Cliente":
							risultato.add(new Cliente(appNome, appCognome, appDataNascita, appTelefono));
							appAccount.setTipoAccount(TipoAccount.ACCOUNTUTENTESTANDARD);
							risultato.get(risultato.size()-1).setAccount(appAccount);
							break;
						case "Addetto Prenotazioni":
							risultato.add(new AddettoPrenotazioni(appNome, appCognome, appDataNascita, appTelefono));
							appAccount.setTipoAccount(TipoAccount.ACCOUNTADDETTOPRENOTAZIONI);
							risultato.get(risultato.size()-1).setAccount(appAccount);
							break;
						case "Addetto Attivita":
							risultato.add(new AddettoAttivita(appNome, appCognome, appDataNascita, appTelefono));
							appAccount.setTipoAccount(TipoAccount.ACCOUNTADDETTOATTIVITA);
							risultato.get(risultato.size()-1).setAccount(appAccount);
							break;
						case "Addetto Bar Cassa":
							risultato.add(new AddettoBarCassa(appNome, appCognome, appDataNascita, appTelefono));
							appAccount.setTipoAccount(TipoAccount.ACCOUNTADDETTOBARCASSA);
							risultato.get(risultato.size()-1).setAccount(appAccount);
							break;
						case "Gestore":
							risultato.add(new Gestore(appNome, appCognome, appDataNascita, appTelefono,spiaggia));
							appAccount.setTipoAccount(TipoAccount.ACCOUNTAMMINISTRATORE);
							risultato.get(risultato.size()-1).setAccount(appAccount);
							break;
					}
				}
				else
					risultato.add(new Cliente(appNome, appCognome, appDataNascita, appTelefono));
				
				for(String s : this.inizializzaNotifica(appIdPersona))
					risultato.get(risultato.size()-1).aggiungiNotificaOPromozione(s);
			}
		}
		catch (Exception e) 
		{
			System.out.println("inizializzaListaUtenti "+e.toString());
		}
		
		return risultato;
	}
	
	public void cambiaDataInizio(String dataInizio)
	{
		String query = "select * from menu";
		int appId;
		try
		{
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.last();
			appId=resultSet.getInt(1);
			statement.execute("UPDATE menu SET datainizio= '"+dataInizio+"' WHERE id= "+appId+";");
		}
		catch(Exception e)
		{
			System.out.println("cambiaDataInizio "+e.toString());
			return;
		}
	}
	public void cambiaDataFine(String dataFine)
	{
		String query = "select * from menu";
		int appId;
		try
		{
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.last();
			appId=resultSet.getInt(1);
			statement.execute("UPDATE menu set datafine= '"+dataFine+"' WHERE id= "+appId+";");
		}
		catch(Exception e)
		{
			System.out.println("cambiaDataFine "+e.toString());
			return;
		}
	}
	
	public void caricaPiatto(Piatto piatto)
	{
		String query = "insert into piatto (id, nome, descrizione, isdisponibile, costo, ingredienti, allergeni, tipo) values ("+
		piatto.getID()+" ,'"+piatto.getNome()+"', '4"+piatto.getDescrizione()+"', "+piatto.getIsDisponibile() +
		", "+piatto.getCosto() +", '"+ piatto.getIngredienti()+"', '"+piatto.getAllergeni() +"', '"+piatto.getTipo()+"');";
		
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e)
		{
			System.out.println("caricaPiatto "+e.toString());
		}
	}
	public void caricaBibita(Bibita bibita)
	{
		String query = "insert into bibita (id, nome, descrizione, isdisponibile, costo, isalcolica) values ("+
		bibita.getID()+" ,'"+bibita.getNome()+"', '"+bibita.getDescrizione()+"', "+bibita.getIsDisponibile() +
		", "+bibita.getCosto() +", "+ bibita.getIsAlcolica()+");";
		
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e)
		{
			System.out.println("caricaBibita "+e.toString());
		}
	}
	public void caricamenu(Menu menu)
	{
		String query = "insert into menu (datainizio,datafine) values('"+menu.getDataInizio()+"','"+menu.getDataFine()+"');";
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e)
		{
			System.out.println("caricaMenu "+e.toString());
		}
	}
	
	public void caricaPiattoNelMenu(Piatto piatto)
	{
		String query = "select * from menu";
		int appId;
		try
		{
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.last();
			appId=resultSet.getInt(1);
			statement.execute("insert into piattonelmenu(piattoid, menuid) values("+ piatto.getID()+","+appId +");");
		}
		catch(Exception e)
		{
			System.out.println("caricaPiattoNelMenu "+e.toString());
			return;
		}
	}
	
	public void caricaBibitaNelMenu(Bibita bibita)
	{
		String query = "select * from menu;";
		int appId;
		try
		{
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.last();
			appId=resultSet.getInt(1);
			statement.execute("insert into bibitanelmenu(bibitaid, menuid) values("+ bibita.getID()+","+appId +");");
		}
		catch(Exception e)
		{
			System.out.println("caricaBibitaNelMenu "+e.toString());
			return;
		}
	}
	
	public Menu inizializzaMenu()
	{
		Menu ritorno = new Menu(null, null);
		String query = "Select * from menu";
		
		try 
		{
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.last();
			ritorno.setDataInizio(resultSet.getString(2));
			ritorno.setDataFine(resultSet.getString(3));
			
			for(Bibita b : this.inizializzaBibita(resultSet.getInt(1)))
			{
				ritorno.aggungiBibitaAdElencoBibite(b);
			}
			for(Piatto p : this.inizializzaPiatto(resultSet.getInt(1)))
			{
				ritorno.aggiungiPiattoAdElencoPiatti(p);
			}
		}
		catch (Exception e) {
			System.out.println("inizializzaMenu "+e.toString());
			return null;
		}
		return ritorno;
	}

	public ArrayList<Bibita> inizializzaBibita(int idMenu)
	{
		ArrayList<Bibita> ritorno = new ArrayList<Bibita>();
		String queryId = "Select bibitaid from bibitanelmenu where menuid = "+idMenu+";";
		try
		{
			Statement statement = conn.createStatement();
			ResultSet rsId = statement.executeQuery(queryId);
			
			while(rsId.next())
			{
				String queryBibita = "Select * from bibita where id = "+rsId.getInt(1)+";";
				Statement statementBibita = conn.createStatement();
				ResultSet rsBibita = statementBibita.executeQuery(queryBibita);
				
				if(rsBibita.next())
				{
					Bibita appbiBibita= new Bibita(rsBibita.getInt(1), rsBibita.getString(2), rsBibita.getString(3),rsBibita.getFloat(5) , rsBibita.getBoolean(6));
					appbiBibita.setIsDisponibile(rsBibita.getBoolean(4));
					ritorno.add(appbiBibita);
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println("inizializzaBibita"+e.toString());

		}
		
		return ritorno;
	}
	
	public ArrayList<Piatto> inizializzaPiatto(int idMenu)
	{
		ArrayList<Piatto> ritorno = new ArrayList<Piatto>();
		String queryId = "Select piattoid from piattonelmenu where menuid = "+idMenu+";";
		try
		{
			Statement statement = conn.createStatement();
			ResultSet rsId = statement.executeQuery(queryId);
			
			while(rsId.next())
			{
				String queryPiatto = "Select * from piatto where id = "+rsId.getInt(1)+";";
				Statement statementPiatto = conn.createStatement();
				ResultSet rsPiatto = statementPiatto.executeQuery(queryPiatto);
				
				if(rsPiatto.next())
				{
					Piatto appPiatto = new Piatto(rsPiatto.getInt(1), rsPiatto.getString(2), rsPiatto.getString(3), rsPiatto.getFloat(5), rsPiatto.getString(6), rsPiatto.getString(7), rsPiatto.getString(8));
					appPiatto.setIsDisponibile(rsPiatto.getBoolean(4));
					ritorno.add(appPiatto);
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println("inizializzaPiatti "+e.toString());
		}
		
		return ritorno;
	}
	
	public ArrayList<Prodotto> inizializzaProdotti()
	{
		ArrayList<Prodotto> ritorno = new ArrayList<>();
		String queryPiatto = "Select * from piatto;";
		
		try 
		{
			Statement statementPiatto = conn.createStatement();
			ResultSet rsPiatto = statementPiatto.executeQuery(queryPiatto);
			
			if(rsPiatto.next())
			{
				ritorno.add(new Piatto(rsPiatto.getInt(1), rsPiatto.getString(2), rsPiatto.getString(3), rsPiatto.getFloat(5), rsPiatto.getString(6), rsPiatto.getString(7), rsPiatto.getString(8)));
			}
		}
		catch (Exception e) 
		{
			System.out.println("inizializzaProdotti 1 "+e.toString());
		}
		String queryBibita = "Select * from bibita;";
		
		try 
		{
			Statement statementBibita = conn.createStatement();
			ResultSet rsBibita = statementBibita.executeQuery(queryBibita);
			
			if(rsBibita.next())
			{
				ritorno.add(new Bibita(rsBibita.getInt(1), rsBibita.getString(2), rsBibita.getString(3),rsBibita.getFloat(5) , rsBibita.getBoolean(6)));
			}
		}
		catch (Exception e) 
		{
			System.out.println("inizializzaProdotti 2 "+e.toString());
		}
		return ritorno;
	}
	
	public void rimuoviBibitaDaMenu(int idBibita)
	{
		try
		{
			Statement statement = conn.createStatement();
			statement.execute("delete from bibitanelmenu where bibitaid="+idBibita+";");
		}
		catch (Exception e) 
		{
			System.out.println("rimuoviBibitaDaMenu "+e.toString());
		}
	}
	public void rimuoviPiattoDaMenu(int idPiatto)
	{
		try
		{
			Statement statement = conn.createStatement();
			statement.execute("delete from piattonelmenu where piattoid="+idPiatto+";");
		}
		catch (Exception e) 
		{
			System.out.println("rimuoviPiattoDaMenu "+e.toString());
		}
	}
	
	public void caricaNotifiche(String notificaPromozione, PersonaChaletSmart persona)
	{
		String query = "INSERT INTO notificaepromozione (descrizionenotificaopromozione) values ('"+notificaPromozione+"');";
		try
		{
			Statement statementAggiungiNotifica = conn.createStatement();
			statementAggiungiNotifica.execute(query);
		}catch (Exception e) 
		{
			System.out.println("caricaNotifiche 1 "+e.toString());
		}
		
		try
		{
			int appIdNotifica;
			
			Statement statementIdNotifica = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSetIdNotifica = statementIdNotifica.executeQuery("Select id from notificaepromozione");
			resultSetIdNotifica.last();
			appIdNotifica=resultSetIdNotifica.getInt(1);
			int appIdPersona = this.idPersona(persona);
			Statement statementIdPersona = conn.createStatement();
			statementIdPersona.execute("Insert into notificaepromozioneassociata (notificaepromozioneid , personachaletsmartid) values("+
			appIdNotifica+" ,"+appIdPersona+");");
			
		}
		catch (Exception e) 
		{
			System.out.println("caricaNotifiche 2"+e.toString());
		}
		
	}
	
	public ArrayList<String> inizializzaNotifica(int idPersona)
	{
		ArrayList<String> ritorno = new ArrayList<>();
		String queryIdNotifica = "Select notificaepromozioneid from notificaepromozioneassociata where 	personachaletsmartid = "+idPersona;
		try
		{
			Statement statementIdNotifica = conn.createStatement();
			ResultSet resultIdNotifica = statementIdNotifica.executeQuery(queryIdNotifica);
			while(resultIdNotifica.next())
			{
				String queryNotifica = "Select descrizionenotificaopromozione from notificaepromozione where id ="+resultIdNotifica.getInt(1);
				Statement statementNotifica = conn.createStatement();
				ResultSet resultIotifica = statementNotifica.executeQuery(queryNotifica);
				if(resultIotifica.next())
					ritorno.add(resultIotifica.getString(1));
			}
		}
		catch (Exception e) 
		{
			System.out.println("inizializzaNotifica "+e.toString());
		}
		return ritorno;
	}
	
	public void caricaEnum()
	{
		try 
		{
			Statement statement = conn.createStatement();
			Statement statementRincaro= conn.createStatement();
			ResultSet resultRincaro = statementRincaro.executeQuery("Select * from rincarostagione");
			if(!resultRincaro.next())
			{
				for(RincaroStagione r: RincaroStagione.values())
				{
					statement.execute("Insert into rincarostagione (mese, rincarostagione) values ('"+r.getMese()+"',"+r.getRincaro()+");");
				}
			}
			
			Statement statementCollocazione= conn.createStatement();
			ResultSet resultCollocazione = statementCollocazione.executeQuery("Select * from tipocollocazione");
			if(!resultCollocazione.next())
			{
				for(TipoCollocazione t:TipoCollocazione.values())
				{
					statement.execute("Insert into tipocollocazione (stringaassociata, rincarocollocazione) values ('"+t.getStringaAssociata()+"',"+t.getRincaroCollocazione()+");");
				}
			}
			Statement statementPrenotazione= conn.createStatement();
			ResultSet resultPrenotazione = statementPrenotazione.executeQuery("Select * from tipoprenotazione");
			if(!resultPrenotazione.next())
			{
				for(TipoPrenotazione p:TipoPrenotazione.values())
				{
					statement.execute("Insert into tipoprenotazione (stringaassociata, percentualesulcosto) values ('"+p.getStringaAssociata()+"',"+p.getPercentualeSulCosto()+");");
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println("caricaEnum "+e.toString());
		}		
	}
	public void inizializzaEnum()
	{
		try
		{
			Statement statement = conn.createStatement();
			ResultSet resultset = statement.executeQuery("Select * from rincarostagione");
			RincaroStagione.AGOSTO.setRincaro(4);
			while(resultset.next())
			{
				for(RincaroStagione r: RincaroStagione.values())
				{
					if(resultset.getInt(2)==(r.getMese()))
						r.setRincaro(resultset.getFloat(3));
				}
			}
			statement=conn.createStatement();
			resultset = statement.executeQuery("Select * from tipocollocazione");
			while(resultset.next())
			{
				for(TipoCollocazione t: TipoCollocazione.values())
				{
					if(resultset.getString(2).equals(t.getStringaAssociata()))
						t.setRincaroCollocazione(resultset.getFloat(3));
				}
			}
			statement=conn.createStatement();
			resultset = statement.executeQuery("Select * from tipoprenotazione");
			while(resultset.next())
			{
				for(TipoPrenotazione p: TipoPrenotazione.values())
				{
					if(resultset.getString(2).equals(p.getStringaAssociata()))
						p.setPercentualeSulCosto(resultset.getFloat(3));
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("inizializzaEnum "+e.toString());
		}
	}
	
	public void modificaRincaroCollocazione(float rincaro, String collocazione)
	{
		try
		{
			Statement statemant = conn.createStatement();
			statemant.execute("update tipocollocazione set rincarocollocazione = "+rincaro+" where stringaassociata = '"+collocazione+"';");
		}
		catch (Exception e) 
		{
			System.out.println("modificaRincaroCollocazione "+e.toString());
		}
		
	}
	
	public void modificaRincaroStagione(float rincaro, int stagione)
	{
		try
		{
			Statement statemant = conn.createStatement();
			statemant.execute("update rincarostagione set rincarostagione = "+rincaro+" where mese = '"+stagione+"';");
		}
		catch (Exception e) 
		{
			System.out.println("modificaRincaroStagione "+e.toString());
		}
		
	}
	
	public void caricaAttrezzatura(Attrezzatura attrezzatura)
	{
		String query = "Insert into attrezzatura (id, quantitacomplessiva, descrizione) values ( "+attrezzatura.getIdTipoAttrezzatura()+
				", "+attrezzatura.getQuantitaComplessiva()+", '"+attrezzatura.getDescrizione()+"');";
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e)
		{
			System.out.println("caricaAttrezzatura "+e.toString());
		}
	}
	
	public ArrayList<Attrezzatura> inizializzaAttrezzatura()
	{
		ArrayList<Attrezzatura> ritorno = new ArrayList<Attrezzatura>();
		String query = "Select * from attrezzatura";
		try
		{
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while(resultSet.next())
			{
				ritorno.add(new Attrezzatura(resultSet.getInt(1), resultSet.getInt(2), resultSet.getString(3)));
			}
		}
		catch (Exception e)
		{
			System.out.println("inizializzaAttrezzatura "+e.toString());
		}
		return ritorno;
	}
	
	private int idPersona(PersonaChaletSmart persona)
	{
		String queryIdCliente ="Select id from personachaletsmart where nome = '"+persona.getNome()+"' and cognome = '"+persona.getCognome()+"' and datanascita = '"+
		persona.getDataN()+"' and telefono = '"+persona.getTel()+"'";
		int appIdPersona = -1;
		if(persona.getAccount()!=null)
		{
			String queryIdAccount ="Select id from account where email = '"+ persona.getAccount().getEmail()+"';";
			int appIdAccount=-1;
			try
			{
				Statement statementIdAccount = conn.createStatement();
				ResultSet resultAccount = statementIdAccount.executeQuery(queryIdAccount);
				if(resultAccount.next())
					appIdAccount=resultAccount.getInt(1);
				queryIdCliente+=" and accountid = "+appIdAccount;
			}
			catch (Exception e) 
			{
				System.out.println("idPersona 1 "+e.toString());
			}
		}
		try
		{
			Statement statementIdPersona = conn.createStatement();
			ResultSet resultPersona = statementIdPersona.executeQuery(queryIdCliente);
			if(resultPersona.next())
				appIdPersona=resultPersona.getInt(1);
		}
		catch (Exception e)
		{
			System.out.println("idPersona 2 "+e.toString());
		}
		return appIdPersona;
	}
	
	public void caricaPrenotazioneAttrezzatura(PrenotazioneAttrezzatura prenotazioneAttrezzatura)
	{
		int appIdAddetto = this.idPersona(prenotazioneAttrezzatura.getAddettoAttivita());
		String query = "Insert into prenotazioneattrezzatura (quantitaattrezzaturaprenotata, orainizioprenotazione, personachaletsmartid, attrezzaturaid)"+
		"values ("+ prenotazioneAttrezzatura.getQuantitaAttrezzaturaPrenotata()+", '"+prenotazioneAttrezzatura.getOraInizioPrenotazione()+"' ,"+
				appIdAddetto+", "+prenotazioneAttrezzatura.getIdTipoAttrezzatura()+");";
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("caricaPrenotazioneAttrezzatura "+e.toString());
		}
	}
	public void restituzioneAttrezzatura(PrenotazioneAttrezzatura prenotazioneAttrezzatura)
	{
		int appIdAddetto = this.idPersona(prenotazioneAttrezzatura.getAddettoAttivita());
		String query ="update prenotazioneattrezzatura set orarestituzione = '"+prenotazioneAttrezzatura.getOraRestituzione()+"' where "
				+ "quantitaattrezzaturaprenotata = "+prenotazioneAttrezzatura.getQuantitaAttrezzaturaPrenotata()+" and "
						+ "orainizioprenotazione = '"+prenotazioneAttrezzatura.getOraInizioPrenotazione()+"' and personachaletsmartid ="+
				appIdAddetto+" and attrezzaturaid ="+prenotazioneAttrezzatura.getIdTipoAttrezzatura();
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("restituzioneAttrezzatura "+e.toString());
		}
	}
	
	public ArrayList<PrenotazioneAttrezzatura> inizializzaPrenotazioniAttrezzatura()
	{
		ArrayList<PrenotazioneAttrezzatura> ritorno = new ArrayList<PrenotazioneAttrezzatura>();
		try
		{
			String queryPrenotazione = "Select * from prenotazioneAttrezzatura";
			Statement statementPrenotazione = conn.createStatement();
			ResultSet rsPrenotazione = statementPrenotazione.executeQuery(queryPrenotazione);
			while(rsPrenotazione.next())
			{
				ritorno.add(new PrenotazioneAttrezzatura(rsPrenotazione.getInt(5), rsPrenotazione.getInt(1), rsPrenotazione.getString(2),(AddettoAttivita) this.ottieniPersonaDaID(rsPrenotazione.getInt(4))));
				if(rsPrenotazione.getString(3)!=null)
					ritorno.get(ritorno.size()-1).setOraRestituzione(rsPrenotazione.getString(3));
			}

		}
		catch (Exception e)
		{
			System.out.println("inizializzaPrenotazioniAttrezzatura "+e.toString());
		}
		return ritorno;
	}
	
	private PersonaChaletSmart ottieniPersonaDaID(int id)
	{
		PersonaChaletSmart risultato = null;
		String queryPersone = "select * from personachaletsmart where id = "+id;
		try
		{
			Statement statement = conn.createStatement();
			Statement statement2 = conn.createStatement();

			ResultSet resultPersona = statement.executeQuery(queryPersone);
			String queryAccount;
			if(resultPersona.next())
			{
				int appIdAccount = resultPersona.getInt(6);
				String appNome = resultPersona.getString(2);
				String appCognome = resultPersona.getString(3);
				String appDataNascita = resultPersona.getString(4);
				String appTelefono = resultPersona.getString(5);
				queryAccount = "Select * from account where id = "+appIdAccount +";";
				ResultSet resultAccount = statement2.executeQuery(queryAccount);
				Account appAccount = null;
				String tipoAccount=null;

				if(resultAccount.next())
				{
					appAccount = new Account(resultAccount.getString(2), resultAccount.getString(3), resultAccount.getString(4));
					tipoAccount = resultAccount.getString(5);
				}
				
				if(tipoAccount!=null)
				{
					switch(tipoAccount)
					{
						case "Cliente":
							risultato= new Cliente(appNome, appCognome, appDataNascita, appTelefono);
							appAccount.setTipoAccount(TipoAccount.ACCOUNTUTENTESTANDARD);
							risultato.setAccount(appAccount);
							break;
						case "Addetto Prenotazioni":
							risultato =new AddettoPrenotazioni(appNome, appCognome, appDataNascita, appTelefono);
							appAccount.setTipoAccount(TipoAccount.ACCOUNTADDETTOPRENOTAZIONI);
							risultato.setAccount(appAccount);
							break;
						case "Addetto Attivita":
							risultato =new AddettoAttivita(appNome, appCognome, appDataNascita, appTelefono);
							appAccount.setTipoAccount(TipoAccount.ACCOUNTADDETTOATTIVITA);
							risultato.setAccount(appAccount);
							break;
						case "Addetto Bar Cassa":
							risultato = new AddettoBarCassa(appNome, appCognome, appDataNascita, appTelefono);
							appAccount.setTipoAccount(TipoAccount.ACCOUNTADDETTOBARCASSA);
							risultato.setAccount(appAccount);
							break;
					}
				}
				else
					risultato = new Cliente(appNome, appCognome, appDataNascita, appTelefono);
			}
		}
		catch (Exception e)
		{
			System.out.println("ottieniPersonaDaID "+e.toString());
		}
		
		return risultato;
	}
	
	public void caricaAttivita(Attivita attivita)
	{
		String query = "Insert into attivita (nomeidentificativo, capienzamax, orarioinizio, orariofine, datasvolgimento)"
				+ "values ('"+attivita.getNomeIdentificativo()+"', "+attivita.getCapienzaMax()+", '"+attivita.getOrarioInizio()+"', '"
				+attivita.getOrarioFine()+"', '"+attivita.getDataSvolgimento()+"');";
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("caricaAttivita "+e.toString());
		}
	}
	
	public void rimuoviAttivita(Attivita attivita)
	{
		String query = "delete from attivita where nomeidentificativo ='"+attivita.getNomeIdentificativo()+"' and capienzamax = "+attivita.getCapienzaMax()+
		" and orarioinizio = '"+attivita.getOrarioInizio()+"' and orariofine = '"+attivita.getOrarioFine()+"' and datasvolgimento = '"+attivita.getDataSvolgimento()+"';";
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("rimuoviAttivita "+e.toString());
		}
	}
	
	public ArrayList<Attivita> inizializzaListaAttivita()
	{
		ArrayList<Attivita> ritorno = new ArrayList<>();
		try
		{
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("Select * from attivita");
			while (rs.next())
			{
				ritorno.add(new Attivita(rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6)));
				
				Statement statmentPartecipanti = conn.createStatement();
				ResultSet rsPartecipanti = statmentPartecipanti.executeQuery("Select personachaletsmartid from prenotazioneattivita where attivitaid ="+rs.getInt(1));
				while(rsPartecipanti.next())
					ritorno.get(ritorno.size()-1).aggiungiPartecipante((Cliente)this.ottieniPersonaDaID(rsPartecipanti.getInt(1)));
			}
		}
		catch (Exception e) 
		{
			System.out.println("inizializzaListaAttivita "+e.toString());
		}
		return ritorno;
	}
	public void inizializzaPrenotazioniAttivita(Attivita attivita, PersonaChaletSmart persona)
	{
		String query = "Select * from attivita where nomeidentificativo ='"+attivita.getNomeIdentificativo()+"' and capienzamax = "+attivita.getCapienzaMax()+
				" and orarioinizio = '"+attivita.getOrarioInizio()+"' and orariofine = '"+attivita.getOrarioFine()+"' and datasvolgimento = '"+attivita.getDataSvolgimento()+"';";
			try 
			{
				Statement statement = conn.createStatement();
				ResultSet resultAttivita = statement.executeQuery(query);
				if(resultAttivita.next())
				{
					int appIdAttivita= resultAttivita.getInt(1);
					int appIdCliente = this.idPersona(persona);
					Statement statement2 = conn.createStatement();
					statement2.execute("Insert into prenotazioneattivita(personachaletsmartid,attivitaid) values("+appIdCliente+","+appIdAttivita+");");
				}
			}
			catch (Exception e) 
			{
				System.out.println("inizializzaPrenotazioneAttivita "+e.toString());
			}
	}
	public void rimuoviPrenotazioneAttivita(Attivita attivita, PersonaChaletSmart persona)
	{
		String query = "Select * from attivita where nomeidentificativo ='"+attivita.getNomeIdentificativo()+"' and capienzamax = "+attivita.getCapienzaMax()+
				" and orarioinizio = '"+attivita.getOrarioInizio()+"' and orariofine = '"+attivita.getOrarioFine()+"' and datasvolgimento = '"+attivita.getDataSvolgimento()+"';";
			try 
			{
				Statement statement = conn.createStatement();
				ResultSet resultAttivita = statement.executeQuery(query);
				if(resultAttivita.next())
				{
					int appIdAttivita= resultAttivita.getInt(1);
					int appIdCliente = this.idPersona(persona);
					Statement statement2 = conn.createStatement();
					statement2.execute("delete from prenotazioneattivita where personachaletsmartid = "+appIdCliente+" and attivitaid ="+appIdAttivita+";");
				}
			}
			catch (Exception e) 
			{
				System.out.print("rimuoviPrenotazione "+e.toString());
			}
	}
	
	public void caricaSpiaggia(Spiaggia spiaggia)
	{
		String query = "Insert into spiaggia (orarioapertura,orariochiusura,capienzamaxspiaggia,prezzolettino)"
				+ "values ('"+spiaggia.getOrarioApertura()+"','"+spiaggia.getOrarioChiusura()+"',"+spiaggia.getCapienzaMaxSpiaggia()
				+","+spiaggia.getPrezzoLettino()+");";
		
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("caricaSpiaggia "+e.toString());
		}
	}
	
	public void modificaOrarioAperturaSpiaggia(String orarioApertura)
	{
		try
		{
			Statement statementIdSpiaggia = conn.createStatement();
			ResultSet rsIdSpiaggia = statementIdSpiaggia.executeQuery("Select id from spiaggia");
			if(rsIdSpiaggia.next())
			{
				int appId = rsIdSpiaggia.getInt(1);
				
				Statement statementModifica = conn.createStatement();
				statementModifica.execute("update spiaggia set orarioapertura ='"+orarioApertura+"' where id = "+appId);
				
			}
		}
		catch (Exception e) 
		{
			System.out.println("modificaOrarioAperturaSpiaggia "+e.toString());
		}

	}
	public void modificaOrarioChiusuraSpiaggia(String orarioChiusura)
	{
		try
		{
			Statement statementIdSpiaggia = conn.createStatement();
			ResultSet rsIdSpiaggia = statementIdSpiaggia.executeQuery("Select id from spiaggia");
			if(rsIdSpiaggia.next())
			{
				int appId = rsIdSpiaggia.getInt(1);
				
				Statement statementModifica = conn.createStatement();
				statementModifica.execute("update spiaggia set orariochiusura ='"+orarioChiusura+"' where id = "+appId);
				
			}
		}
		catch (Exception e) 
		{
			System.out.println("modificaOrarioChiusuraSpiaggia "+e.toString());
		}

	}
	public void modificaCapienzaMaxSpiaggia(int capienzaMax)
	{
		try
		{
			Statement statementIdSpiaggia = conn.createStatement();
			ResultSet rsIdSpiaggia = statementIdSpiaggia.executeQuery("Select id from spiaggia");
			if(rsIdSpiaggia.next())
			{
				int appId = rsIdSpiaggia.getInt(1);
				
				Statement statementModifica = conn.createStatement();
				statementModifica.execute("update spiaggia set capienzamax ="+capienzaMax+" where id = "+appId);
				
			}
		}
		catch (Exception e) 
		{
			System.out.println("modificaCapienzaMaxSpiaggia "+e.toString());
		}
	}
	public void modificaPrezzoLettinoSpiaggia(float prezzoLettino)
	{
		try
		{
			Statement statementIdSpiaggia = conn.createStatement();
			ResultSet rsIdSpiaggia = statementIdSpiaggia.executeQuery("Select id from spiaggia");
			if(rsIdSpiaggia.next())
			{
				int appId = rsIdSpiaggia.getInt(1);
				
				Statement statementModifica = conn.createStatement();
				statementModifica.execute("update spiaggia set prezzolettino ="+prezzoLettino+" where id = "+appId);
				
			}
		}
		catch (Exception e) 
		{
			System.out.println("modificaPrezzoLettinoSpiaggia "+e.toString());
		}
	}
	public Spiaggia inizializzaSpiaggia()
	{
		Spiaggia ritorno = null;
		try 
		{
			Statement statementSpiaggia = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSpiaggia = statementSpiaggia.executeQuery("Select * from spiaggia");
			resultSpiaggia.last();
			ritorno = new Spiaggia(resultSpiaggia.getString(2), resultSpiaggia.getString(3),resultSpiaggia.getInt(4));
			ritorno.setPrezzoLettino(resultSpiaggia.getFloat(5));
			for(Ombrellone o: this.iniziallizaOmbrelloni())
				ritorno.aggiungiOmbrellone(o);
			for(Ordinazione o:this.inizializzaOrdinazione())
				ritorno.aggiungiOrdinazione(o);
			for(Prenotazione p : this.inizializzaPrenotazioni())
				ritorno.aggiungiPrenotazione(p);
		}
		catch (Exception e) 
		{
			System.out.println("inizializzaSpiaggia "+e.toString());
		}

		return ritorno;
	}
	
	public void caricaOmbrellone(Ombrellone ombrellone)
	{
		int appSpiaggiaID= -1;
		int appCollocazioneID=-1;
		try 
		{
			Statement statementSpiaggia = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSpiaggia = statementSpiaggia.executeQuery("Select id from spiaggia");
			resultSpiaggia.last();
			appSpiaggiaID=resultSpiaggia.getInt(1);
		}
		catch (Exception e)
		{
			System.out.println("caricaOmbrellone 1"+e.toString());
		}
		try 
		{
			Statement statementCollocazione = conn.createStatement();
			ResultSet resultCollocazione = statementCollocazione.executeQuery("Select id from tipocollocazione where stringaassociata ='"+ombrellone.getCollocazione().getStringaAssociata()+"';");
			if(resultCollocazione.next())
				appCollocazioneID = resultCollocazione.getInt(1);
		}
		catch (Exception e)
		{
			System.out.println("caricaOmbrellone 2"+e.toString());
		}
		
		
		String query = "Insert into ombrellone (id,prezzo,descrizione,qrcode,categoria,tipocollocazioneid,spiaggiaid) values("+ombrellone.getId()
		+","+ombrellone.getPrezzo()+",'"+ombrellone.getDescrizione()+"','"+ombrellone.getQRcode()+"',"+ombrellone.getCategoria()+","+appCollocazioneID+","+appSpiaggiaID+");";
		try 
		{
			Statement statementOmbrellone = conn.createStatement();
			statementOmbrellone.execute(query);
		} catch (Exception e) 
		{
			System.out.println("caricaOmbrellone 3"+e.toString());
		}
	}
	
	public void rimuoviOmbrellone(Ombrellone ombrellone)
	{
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute("delete from ombrellone where id = "+ombrellone.getId());
		}
		catch (Exception e)
		{
			System.out.println("rimuoviOmbrellone "+e.toString());
		}
	}
	public ArrayList<Ombrellone> iniziallizaOmbrelloni()
	{
		ArrayList<Ombrellone> ritorno = new ArrayList<>();
		String query = "Select * from ombrellone";
		try
		{
			Statement statement = conn.createStatement();
			ResultSet rs= statement.executeQuery(query);
			while(rs.next())
			{
				Statement statmentCollocazione = conn.createStatement();
				ResultSet rsCollocazione = statmentCollocazione.executeQuery("Select * from tipocollocazione where id = "+rs.getInt(7));
				TipoCollocazione appTipo = TipoCollocazione.AVANTI;
				if(rsCollocazione.next())
				{
					for(TipoCollocazione t:TipoCollocazione.values())
					{
						if(t.getStringaAssociata().equals(rsCollocazione.getString(2)))
							appTipo=t;
					}
				}
				ritorno.add(new Ombrellone(rs.getFloat(2), rs.getString(3), rs.getInt(1), appTipo, rs.getInt(5)));
			}
		} 
		catch (Exception e)
		{
			System.out.println("inizializzaOmbrelloni "+e.toString());
		}
		
		return ritorno;
	}
	
	public void caricaOrdinazione(Ordinazione ordinazione)
	{
		String query = "insert into ordinazione (costocomplessivo,oraordinazione,ombrelloneid,personachaletsmartid) values("+ordinazione.getCostoComplessivo()
				+",'"+ordinazione.getOraOrdinazione()+"',"+ordinazione.getOmbrelloneRelativo().getId()+","+this.idPersona(ordinazione.getClienteOrdinante())+");";
		try 
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
			Statement statementProdotti = conn.createStatement();
			String queryProdotto;
			int appIdOrdinazione=-1;
			String queryOrdinazione ="Select id from ordinazione where oraordinazione = '"+ordinazione.getOraOrdinazione()+"';";
			Statement statementIdOrdinazione = conn.createStatement();
			ResultSet resultIdOrdinazione = statementIdOrdinazione.executeQuery(queryOrdinazione);
			if(resultIdOrdinazione.next())
				appIdOrdinazione = resultIdOrdinazione.getInt(1);
			for(Prodotto p: ordinazione.getInsiemeProdotti())
			{
				if(p instanceof Piatto)
				{
					queryProdotto = "Insert into piattoordinato (ordinazioneid,piattoid) values("+appIdOrdinazione+","+p.getID()+");";
				}
				else
				{
					queryProdotto = "Insert into bibitaordinata (ordinazioneid,bibitaid) values("+appIdOrdinazione+","+p.getID()+");";
				}
				statementProdotti.execute(queryProdotto);
			}
		} 
		catch (Exception e) 
		{
			System.out.println("caricaOrdinazione "+e.toString());
		}
	}
	
	public ArrayList<Ordinazione> inizializzaOrdinazione()
	{
		ArrayList<Ordinazione> ritorno = new ArrayList<Ordinazione>();
		
		try
		{
			Statement statement = conn.createStatement();
			ResultSet rsOrdinazione = statement.executeQuery("Select * from ordinazione");
			while(rsOrdinazione.next())
			{
				Ombrellone appOmbrellone = this.trovaOmbrelloneDaID(rsOrdinazione.getInt(4));
				Ordinazione appOrdinazione =new Ordinazione((Cliente)this.ottieniPersonaDaID(rsOrdinazione.getInt(5)), appOmbrellone);
				appOrdinazione.setOraOrdinazione(rsOrdinazione.getTimestamp(3));
				
				Statement statementProdotti = conn.createStatement();
				int idProdotto=-1;
				ResultSet resultProdotti = statementProdotti.executeQuery("Select piattoid from piattoordinato where ordinazioneid ="+rsOrdinazione.getInt(1));
				while(resultProdotti.next())
				{
					idProdotto=resultProdotti.getInt(1);
					
					String queryPiatto = "Select * from piatto where id = "+idProdotto+";";
					statementProdotti = conn.createStatement();
					ResultSet rsPiatto = statementProdotti.executeQuery(queryPiatto);
					
					while(rsPiatto.next())
					{
						appOrdinazione.aggiungiProdottiAdElencoProdotti(new Piatto(rsPiatto.getInt(1), rsPiatto.getString(2), rsPiatto.getString(3), rsPiatto.getFloat(5), rsPiatto.getString(6), rsPiatto.getString(7), rsPiatto.getString(8)),1);
					}
				}
				
				resultProdotti = statementProdotti.executeQuery("Select piattoid from piattoordinato where ordinazioneid ="+rsOrdinazione.getInt(1));
				while(resultProdotti.next())
				{
					String querybibita = "Select * from bibita where id = "+idProdotto+";";
					Statement statementBibita = conn.createStatement();
					ResultSet rsBibita = statementBibita.executeQuery(querybibita);
					
					while(rsBibita.next())
					{
						appOrdinazione.aggiungiProdottiAdElencoProdotti(new Bibita(rsBibita.getInt(1), rsBibita.getString(2), rsBibita.getString(3),rsBibita.getFloat(5) , rsBibita.getBoolean(6)),1);
					}
				}
				
				
				ritorno.add(appOrdinazione);
			}
			
		}
		catch (Exception e)
		{
			System.out.println("inizializzaordinazioni :"+e.toString());
		}
		return ritorno;
	}
	
	private Ombrellone trovaOmbrelloneDaID(int id)
	{
		String queryOmbrellone = "Select * from ombrellone where id = "+id;
		try
		{
			Statement statementOmbrellone = conn.createStatement();
			ResultSet rs= statementOmbrellone.executeQuery(queryOmbrellone);
			if(rs.next())
			{
				Statement statmentCollocazione = conn.createStatement();
				ResultSet rsCollocazione = statmentCollocazione.executeQuery("Select * from tipocollocazione where id = "+rs.getInt(7));
				TipoCollocazione appTipo = TipoCollocazione.AVANTI;
				if(rsCollocazione.next())
				{
					for(TipoCollocazione t:TipoCollocazione.values())
					{
						if(t.getStringaAssociata().equals(rsCollocazione.getString(2)))
							appTipo=t;
					}
				}
				return new Ombrellone(rs.getFloat(2), rs.getString(3), rs.getInt(1), appTipo, rs.getInt(5));
			}
		}
		catch (Exception e) 
		{
			System.out.println("trovaOmbrelloniid :"+e.toString());
		}
		return null;
	}
	
	public void rimuoviOrdinazione(Ordinazione ordinazione)
	{
		String query = "delete from ordinazione where oraordinazione = '"+ordinazione.getOraOrdinazione()+"';";
		String queryIdOrdnazione = "Select id from ordinazione where oraordinazione ='"+ordinazione.getOraOrdinazione()+"';"; 
		int appIdOrdinazione = -1;
		try 
		{
			Statement statement = conn.createStatement();
			ResultSet resultOrdinazione = statement.executeQuery(queryIdOrdnazione);
			if(resultOrdinazione.next())
				appIdOrdinazione=resultOrdinazione.getInt(1);
			statement.execute("delete from piattoordinato where ordinazioneid ="+appIdOrdinazione);
			statement.execute("delete from bibitaordinata where ordinazioneid ="+appIdOrdinazione);
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("rimuoviOrdinazioni :"+e.toString());
		}
	}
	
	public void caricaPrenotazione(Prenotazione prenotazione)
	{
		try
		{
			Statement statement = conn.createStatement();
			int appIdTipoPrenotazione =-1;
			ResultSet rs = statement.executeQuery("Select id from tipoprenotazione where stringaassociata ='"+prenotazione.getTipoPrenotazione().getStringaAssociata()+"';");
			if(rs.next())
				appIdTipoPrenotazione = rs.getInt(1);
			
			
			String query ="Insert into prenotazione (giornodellaprenotazione,agiorniconsecutivi,lettiniassociati,personachaletsmartid,tipoprenotazioneid)"
					+ "values('"+prenotazione.getGiornoDellaPrenotazione()+"',"+prenotazione.isAGiorniConsecutivi()+","+prenotazione.getLettiniAssociati()
					+","+this.idPersona(prenotazione.getClientePrenotante())+","+appIdTipoPrenotazione+");";
			statement.execute(query);
			int appIdPrenotazione = -1;
			Statement statementIdPrenotazione =conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultIdPrenotazione = statementIdPrenotazione.executeQuery("Select id from prenotazione");
			resultIdPrenotazione.last();
			appIdPrenotazione=resultIdPrenotazione.getInt(1);
			for(Ombrellone o : prenotazione.getOmbrelloniAssociati())
			{
				statement.execute("insert into prenotazioneombrellone (prenotazioneid,ombrelloneid) values( "+appIdPrenotazione+","+o.getId()+");");
			}
		}
		catch (Exception e) 
		{
			System.out.println("Carica prenotazione "+e.toString());
		}

	}
	
	
	public ArrayList<Prenotazione> inizializzaPrenotazioni()
	{
		ArrayList<Prenotazione>ritorno = new ArrayList<Prenotazione>();
		ArrayList<Ombrellone> appOmbrelloni;
		try
		{
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("Select * from prenotazione");
			while(rs.next())
			{
				appOmbrelloni=new ArrayList<>();
				Statement statmentPrenotazione = conn.createStatement();
				ResultSet rsPrenotazione = statmentPrenotazione.executeQuery("Select * from tipoprenotazione where id = "+rs.getInt(6));
				TipoPrenotazione appTipo = TipoPrenotazione.INTERO;
				if(rsPrenotazione.next())
				{
					for(TipoPrenotazione t:TipoPrenotazione.values())
					{
						if(t.getStringaAssociata().equals(rsPrenotazione.getString(2)))
							appTipo=t;
					}
				}
				
				Statement statementOmbrelloni = conn.createStatement();
				ResultSet resultOmbrelloni = statementOmbrelloni.executeQuery("Select ombrelloneid from prenotazioneombrellone where prenotazioneid ="+rs.getInt(1));
				while (resultOmbrelloni.next())
				{
					appOmbrelloni.add(this.trovaOmbrelloneDaID(resultOmbrelloni.getInt(1)));
					
				}
				
				Ombrellone[] ombrelloniPrenotati = new Ombrellone[appOmbrelloni.size()];
				for(int i=0;i<appOmbrelloni.size();i++)
					ombrelloniPrenotati[i]=appOmbrelloni.get(i);
				Prenotazione appPrenotazione = new Prenotazione((Cliente)this.ottieniPersonaDaID(rs.getInt(5)),appTipo , ombrelloniPrenotati, rs.getInt(4), rs.getTimestamp(2));
				appPrenotazione.setAGiorniConsecutivi(rs.getBoolean(3));
				ritorno.add(appPrenotazione);
			}
		}
		catch (Exception e) 
		{
			System.out.print("Inizializza prenotazioni "+e.toString());
		}
		return ritorno;
	}
	
	public void rimuoviPrenotazioneSpiaggia(Prenotazione prenotazione)
	{
		try
		{
			int appIdPrenotazione;
			Statement statementId = conn.createStatement();
			ResultSet idPrenotazione = statementId.executeQuery("Select id from prenotazione where giornodellaprenotazione = '"+prenotazione.getGiornoDellaPrenotazione()+"';");
			if(idPrenotazione.next())
			{
				appIdPrenotazione=idPrenotazione.getInt(1);
				Statement statement = conn.createStatement();
				statement.execute("delete from prenotazioneombrellone where prenotazioneid = "+appIdPrenotazione);
				statement.execute("delete from prenotazione where id = "+appIdPrenotazione);
			}
		}
		catch (Exception e) 
		{
			System.out.println("rimuoviPrenotazioneSpiaggia "+e.toString());
		}
		
	}
	
	public void aggiornaGiorniConsecutivi(Prenotazione prenotazione, boolean giorniConsecutivi)
	{
		String query ="update prenotazione set agiorniconsecutivi ="+giorniConsecutivi+" where giornodellaprenotazione ='"+prenotazione.getGiornoDellaPrenotazione()+"';";
		try
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch(Exception e)
		{
			System.out.println("aggiornaGiorniConsecutivi "+e.toString());
		}
	}
	public void rimuoviPrenotazioneAttrezzatura(PrenotazioneAttrezzatura prenotazioneAttrezzatura)
	{
		int appIdAddetto = this.idPersona(prenotazioneAttrezzatura.getAddettoAttivita());
		String query ="delete from prenotazioneattrezzatura where "
				+ "quantitaattrezzaturaprenotata = "+prenotazioneAttrezzatura.getQuantitaAttrezzaturaPrenotata()+" and "
				+ "orainizioprenotazione = '"+prenotazioneAttrezzatura.getOraInizioPrenotazione()+"' and personachaletsmartid ="+
		appIdAddetto+" and attrezzaturaid ="+prenotazioneAttrezzatura.getIdTipoAttrezzatura();
		try
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch(Exception e)
		{
			System.out.println("rimuoviPrenotazioneAttrezzatura "+e.toString());
		}
	}
	
	public void modificaOrarioInizioAttivita(Attivita attivita,String orarioInizio)
	{
		String query ="update attivita set orarioinizio ='"+orarioInizio+"'where nomeidentificativo ='"+attivita.getNomeIdentificativo()+"' and capienzamax = "+attivita.getCapienzaMax()+
				" and orarioinizio = '"+attivita.getOrarioInizio()+"' and orariofine = '"+attivita.getOrarioFine()+"' and datasvolgimento = '"+attivita.getDataSvolgimento()+"';";
		try
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("modificaOrarioInizioAttivita "+e.toString());
		}
	}
	
	public void modificaOrarioFineAttivita(Attivita attivita,String orarioFine)
	{
		String query ="update attivita set orariofine ='"+orarioFine+"'where nomeidentificativo ='"+attivita.getNomeIdentificativo()+"' and capienzamax = "+attivita.getCapienzaMax()+
				" and orarioinizio = '"+attivita.getOrarioInizio()+"' and orariofine = '"+attivita.getOrarioFine()+"' and datasvolgimento = '"+attivita.getDataSvolgimento()+"';";
		try
		{
			Statement statement = conn.createStatement();
			statement.execute(query);
		}
		catch (Exception e) 
		{
			System.out.println("modificaOrarioFineAttivita "+e.toString());
		}
	}
}
