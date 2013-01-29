package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;

public enum AvailableFragment {

	//Livelli di autenticazione: 0 none - 15 visualizzazione, 32 modifica
	
	RILEVAZIONE_AREA	("rilevazione_area","Rilevazione Area",RilevazioneAreaFragment.class,Constants.PERMESSO_VISUALIZZA,Constants.PERMESSO_MODIFICA),
	RILEVAZIONE_GIOCO	("rilevazione","Rilevazione Gioco",RilevazioneGiocoFragment.class,Constants.PERMESSO_VISUALIZZA,Constants.PERMESSO_VISUALIZZA),
	CONTROLLO   		("controllo","Controllo",ControlloFragment.class,Constants.PERMESSO_MODIFICA,Constants.PERMESSO_MODIFICA),
	SPOSTAMENTO 		("spostamento","Spostamento",SpostamentoFragment.class,Constants.PERMESSO_MODIFICA,Constants.PERMESSO_MODIFICA);
	
	public final String label;
	public final String title;
	public final Class<ICustomFragment> specificClass;
	public final int minimumComuneAutLevel;
	public final int minimumCooperativaAutLevel;
	
	/**
	 * 
	 * @param l etichetta del fragment, solo per uso interno
	 * @param t titolo del fragment, mostrato nell'interfaccia
	 * @param c classe da passare al costruttore dei fragments
	 * @param comuneLevel livello minimo necessario per la visualizzazione da parte del comune
	 * @param coopLevel livello minimo necessario per la visualizzazione da parte della cooperativa
	 */
	AvailableFragment(String l,String t,Class c,int comuneLevel, int coopLevel) {
		this.label = l;
		this.title = t;
		this.specificClass = c;
		this.minimumComuneAutLevel = comuneLevel;
		this.minimumCooperativaAutLevel = coopLevel;
	}
}
