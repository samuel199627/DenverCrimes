package it.polito.tdp.crimes.model;

//Mi tiene traccia di tutte le adiacenze che abbiamo nel grafo: i due vertici piu' il peso tra di loro
//non ridefiniaimo il metodo equals ed hashcode in quanto non mettiamo nulla in mappe o cose strane.
//I vertici sono dei reati e nel grafo li vogliamo di una categoria 
public class Adiacenza {

	private String v1;
	private String v2;
	private Double peso;
	
	public Adiacenza(String v1, String v2, Double peso) {
		super();
		this.v1 = v1;
		this.v2 = v2;
		this.peso = peso;
	}

	public String getV1() {
		return v1;
	}

	public void setV1(String v1) {
		this.v1 = v1;
	}

	public String getV2() {
		return v2;
	}

	public void setV2(String v2) {
		this.v2 = v2;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}
	
	
	
}
