package it.opencontent.android.ocparchitn.services;

import java.util.HashMap;

public interface IRemoteConnection {

	public void sendRequest(Object data);

	public void getRemoteResponse(String methodName, HashMap<String, Object> data,boolean finish,String forcedMapIndex);

}
