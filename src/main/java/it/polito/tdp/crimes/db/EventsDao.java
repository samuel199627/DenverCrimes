package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	//vogliamo i mesi distinti in cui si verificano i crimini ed estraggo mediante la funzione MONTH
	//e restituisce i mesi in numero
	public List<Integer> getMesi(){
		String sql = "SELECT DISTINCT Month(reported_date) as mese FROM events";
		List<Integer> mesi = new LinkedList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while (res.next()) {
				mesi.add(res.getInt("mese"));
			}
			conn.close();
			//ordino i mesi in maniera crescente da interi in quanto provata sul dataset in sequel pro
			//venivano restituiti abbastanza a caso i mesi
			Collections.sort(mesi);
			return mesi;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	//seleziono le categorie distinte esattamente come per i mesi
	//sono delle stringhe questi identificativi di categorie
	public List<String> getCategorie(){
		String sql = "SELECT DISTINCT offense_category_id as categoria FROM events";
		List<String> categorie = new LinkedList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while (res.next()) {
				categorie.add(res.getString("categoria"));
			}
			conn.close();
			return categorie;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Adiacenza> getAdiacenze(String categoria, Integer mese) {
		//vogliamo la coppia di reati in cui si sono verificati entrambi i reati e contare i quartieri distinti
		//la tabella e' una lista di crimini e noi cerchiamo una coppia di crimini diversi e quindi facciamo un join della
		//tabella con se stessa sul crimine diverso
		//filtriamo sulla categoria, filtriamo sul mese (su entrambe le tabelle di cui facciamo il join) e infine
		//filtriamo sul quartiere uguale tra i due reati.
		//Raggruppiamo tutto per contare sulla coppia.
		String sql = "select e1.offense_type_id as v1, e2.offense_type_id as v2, COUNT(DISTINCT(e1.neighborhood_id)) as peso " + 
				"from events e1, events e2 " + 
				"where e1.offense_category_id = ? " + 
				"	and e2.offense_category_id = ? " + 
				"	and Month(e1.reported_date) = ? " + 
				"	and Month(e2.reported_date) = ? " + 
				"	and e1.offense_type_id != e2.offense_type_id " + 
				"	and e1.neighborhood_id = e2.neighborhood_id " + 
				"group by e1.offense_type_id, e2.offense_type_id";
		List<Adiacenza> adiacenze = new LinkedList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setString(1, categoria);
			st.setString(2, categoria);
			st.setInt(3, mese);
			st.setInt(4, mese);
			
			ResultSet res = st.executeQuery() ;
			while (res.next()) {
				//QUANDO RINOMINO LE COLONNE NEL RISULTATO DELLA QUERY POSSO IMPORTARE FACILMENTE IL LORO NOME
				//USANDO IL NOME CAMBIATO E RINOMINATO
				adiacenze.add(new Adiacenza(res.getString("v1"), res.getString("v2"), res.getDouble("peso")));
			}
			conn.close();
			return adiacenze;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
	}

}
