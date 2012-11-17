package it.opencontent.android.ocparchitn.db.entities;

public enum StruttureEnum {
	GIOCHI("giochi", new Gioco()), AREE("aree", new Area());

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
