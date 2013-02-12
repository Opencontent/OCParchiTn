package it.opencontent.android.ocparchitn.fragments;


public enum AvailableFragment {

	
	RILEVAZIONE_AREA	("rilevazione_area","Area",RilevazioneAreaFragment.class,true,true),
	RILEVAZIONE_GIOCO	("rilevazione","Gioco",RilevazioneGiocoFragment.class,true,true),
	CONTROLLO   		("controllo","Controllo",ControlloFragment.class,true,true),
	INTERVENTO   		("intervento","Intervento",InterventoFragment.class,true,true),
	SPOSTAMENTO 		("spostamento","Sposta",SpostamentoFragment.class,true,false),
	SYNC 				("sincronizzazione","Sync",SyncFragment.class,true,true);
	
	public final String label;
	public final String title;
	public final Class<ICustomFragment> specificClass;
	public final boolean accessoPermessoAlComune;
	public final boolean accessoPermessoAllaCooperativa;
	
	AvailableFragment(String l,String t,Class c,boolean comune, boolean cooperativa) {
		this.label = l;
		this.title = t;
		this.specificClass = c;
		this.accessoPermessoAlComune = comune;
		this.accessoPermessoAllaCooperativa = cooperativa;
	}
}
