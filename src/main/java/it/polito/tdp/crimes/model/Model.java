package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	//vogliamo un grafo semplice, pesato e non orientato
	private SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	
	private List<String> best;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<Integer> getMesi(){
		return dao.getMesi();
	}
	
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	
	public void creaGrafo(String categoria, Integer mese) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//mi importo tutte le connessioni che ho
		List<Adiacenza> adiacenze = this.dao.getAdiacenze(categoria, mese);
		//le metto del grafo le mie connessioni
		for(Adiacenza a : adiacenze) {
			//aggiungo i vertici
			if(!this.grafo.containsVertex(a.getV1()))
				this.grafo.addVertex(a.getV1());
			if(!this.grafo.containsVertex(a.getV2()))
				this.grafo.addVertex(a.getV2());
			//creo l'arco solo se non c'e' gia' (non orientato basta inserirlo una volta sola)
			if(this.grafo.getEdge(a.getV1(), a.getV2()) == null) 
				Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
		}
		
		System.out.println(String.format("Grafo creato con %d vertici e %d archi", this.grafo.vertexSet().size(), this.grafo.edgeSet().size()));
	}
	
	//ci serve per fare piu' veloce a stamparci gli archi nel grafo e per evitare di fare le operazioni
	//dalla parte del controller
	public List<Arco> getArchi(){
		double pesoMedio = 0.0;
		//scorro gli archi per calcolarmi il peso medio
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pesoMedio += this.grafo.getEdgeWeight(e);
		}
		pesoMedio = pesoMedio/this.grafo.edgeSet().size();
		
		//scorro tutti gli archi per vedere se il peso e' oltre il peso medio
		List<Arco> archi = new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) > pesoMedio)
				archi.add(new Arco(this.grafo.getEdgeSource(e),this.grafo.getEdgeTarget(e),this.grafo.getEdgeWeight(e)));
		}
		//segue l'ordinamento che abbiamo messo con il compareTo nella classe di archi se non mettiamo
		//l'argomento in piu'
		Collections.sort(archi);
		return archi;
	}
	
	//riceviamo in argomento il punto di partenza e di arrivo del percorso che stiamo cercando.
	//impostiamo la ricorsione
	public List<String> trovaPercorso(String sorgente, String destinazione) {
		List<String> parziale = new ArrayList<>();
		this.best = new ArrayList<>();
		//aggiungiamo gia' il nodo di partenza
		parziale.add(sorgente);
		trovaRiscorsivo(destinazione,parziale, 0);
		return this.best;
	}

	//deve conoscere il nodo di destinazione per sapere quando interrompere la ricerca di vertici
	//il livello non serviva, ma se lo era creato e se lo e' portato dietro
	private void trovaRiscorsivo(String destinazione, List<String> parziale, int L) {

		//CASO TERMINALE? -> quando l'ultimo vertice inserito in parziale è uguale alla destinazione
		//e controllo se sono arrivato in una soluzione migliore
		if(parziale.get(parziale.size() - 1).equals(destinazione)) {
			//siccome noi vogliamo il percorso con il massimo numero di vertici mi basta controllare
			//la dimensione della lista di vertici che ho messo
			if(parziale.size() > this.best.size()) {
				this.best = new ArrayList<>(parziale);
			}
			return;
		}
		
		//scorro i vicini dell'ultimo vertice inserito in parziale
		//Graphs.neighborListOf mi da tutta la lista di vicini del vertice che passo come parametro
		//sul grafo che passo come primo parametro
		for(String vicino : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size() -1 ))) {
			//cammino aciclico -> controllo che il vertice non sia già in parziale perche' se lo fosse
			//mi andrei a creare un ciclo che e' quello che vogliamo evitare
			if(!parziale.contains(vicino)) {
				//provo ad aggiungere
				parziale.add(vicino);
				//continuo la ricorsione
				this.trovaRiscorsivo(destinazione, parziale, L+1);
				//faccio backtracking
				parziale.remove(parziale.size() -1);
			}
		}
	}
}
