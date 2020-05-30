/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Arco;
import it.polito.tdp.crimes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

/*
 	E' una simulazione d'esame.
 	Il dataset contiene crimini a Denver con un'unica tabella relativa ad un dato crimine.
 	Dobbiamo interrogare la base dati per analizzare la distribuzione delle forze di polizia con la
 	costruzione di un grafo (pesato, semplice non orientato).
 	
 	Non abbiamo utilizzato una identity Map in questo esercizio in quanto i vertici del grafo sono
 	delle stringhe e quindi i riferimenti sono sempre loro e non rischio di crearmi lo stesso
 	oggetto ma con due riferimenti differenti.
 	
 	Siccome dobbiamo stampare gli archi, ci creiamo una classe che li rappresenta per fare piu' veloce.
 	
 	Il secondo punto e' ricorsivo e dobbiamo calcolare un percorso, quindi dobbiamo ritornare un percorso
 	tra un vertice di partenza ed un vertice di arrivo. Vogliamo il cammino che contiene il numero massimo
 	di vertici.
 	
 	PROVANDO COSE DIVERSE DA QUELLE CHE HA MESSO LUI, CIOE' CHE HA SELEZIONATO MI DAVA QUALCHE ECCEZIONE, QUINDI
 	MAGARI QUI E LI' QUALCHE ERRORINO C'E' MA IL CONCETTO E' PRATICAMENTE QUELLO PRESENTE QUI.
 */

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxCategoria"
    private ComboBox<String> boxCategoria; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalisi"
    private Button btnAnalisi; // Value injected by FXMLLoader

    @FXML // fx:id="boxArco"
    private ComboBox<Arco> boxArco; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	Arco a = this.boxArco.getValue();
    	if(a == null) {
    		txtResult.appendText("Seleziona arco!");
    		return;
    	}
    	List<String> percorso = this.model.trovaPercorso(a.getV1(), a.getV2());
    	txtResult.appendText("Percorso migliore: \n\n");
    	for(String v : percorso) {
    		txtResult.appendText(v + "\n");
    	}
    }
    

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	
    	String categoria = boxCategoria.getValue();
    	if(categoria == null) {
    		txtResult.appendText("Seleziona categoria!");
    		return ;
    	}
    	
    	Integer mese = boxMese.getValue();
    	if(mese == null) {
    		txtResult.appendText("Seleziona mese!");
    		return ;
    	}
    	
    	this.model.creaGrafo(categoria, mese);
    	
    	//SICCOME DOBBIAMO STAMPARE GLI ARCHI, CI CREIAMO UNA CLASSE (simile ad adiacenza)
    	//che mi permette di fare una stampa molto facile
    	List<Arco> archi = this.model.getArchi();
    	//aggiungo gli archi al menu a tendina per poter fare il punto 2 dell'esercizio
    	boxArco.getItems().addAll(archi);
    	txtResult.appendText("ARCHI > PESO MEDIO: \n\n");
    	for(Arco a : archi) {
    		txtResult.appendText(a.toString() + "\n");
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxCategoria != null : "fx:id=\"boxCategoria\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAnalisi != null : "fx:id=\"btnAnalisi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxArco != null : "fx:id=\"boxArco\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	//AGGIUNGIAMO AI MENU A TENDINA  LE INFORMAZIONI CHE CI SERVONO
    	this.boxCategoria.getItems().addAll(this.model.getCategorie());
    	this.boxMese.getItems().addAll(this.model.getMesi());
    }
}
