package Rezervare_bilet;

public class Hotel {

	String nume;
	int numarCamere;
	int numarPersoaneInCamera;
	int numarStele;
	int pretCamera;
	int sales;

	public Hotel(String nume, int numarCamere, int numarPersoaneInCamera, int numarStele, int pret) {
		this.nume = nume;
		this.numarCamere = numarCamere;
		this.numarPersoaneInCamera = numarPersoaneInCamera;
		this.numarStele = numarStele;
		this.pretCamera = pret;
	}

	public String getNume() {
		return nume;
	}

	public int getNumarCamere() {
		return numarCamere;
	}

	public int getNumarPersoaneInCamera() {
		return numarPersoaneInCamera;
	}

	public int getNumarStele() {
		return numarStele;
	}

	public int getPret() {
		return pretCamera;
	}

	public int getSales() {
		return sales;
	}

	public void buyRoom() {
		numarCamere--;
		sales++;
	}

	public void releaseRoom() {
		numarCamere++;
	}

	public void incrementRefusals() {
		// TODO Auto-generated method stub
	}
}
