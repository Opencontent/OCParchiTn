package it.opencontent.android.ocparchitn.db.entities;

public enum StruttureEnum {
	AREE("area", new Area()),
	GIOCHI("gioco", new Gioco()), 
	CONTROLLO("controllo",new Controllo()),
	INTERVENTO("intervento",new Intervento());

	public String tipo;
	public Struttura istanza;

	StruttureEnum(String tipo, Struttura istanza) {
		this.tipo = tipo;
		this.istanza = istanza;
	}

	@Override
	public String toString() {
		return this.name();
	}
}
