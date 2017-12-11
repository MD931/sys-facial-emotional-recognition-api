package com.surirobot.utils;

/*
 * Enumération pour les variables d'environnement 
 */
public enum EnvVar {

	APIKEY,
	API_ALGORITHMIA;

	private String value;

	EnvVar(){
		try{
			value = System.getenv(toString());
		} catch (Exception e){
			value = null;
		}
	}

	public String getValue(){ return value; }
}
