package it.opencontent.android.ocparchitn.db.entities;

public enum StruttureEnum {
	GIOCHI("gioco", new Gioco()), 
	AREE("area", new Area()),
	CONTROLLO("controllo",new Controllo());

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
