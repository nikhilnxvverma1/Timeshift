package com.nikhil.logging;

public class Logger{

	//TODO what about logging level
	public static void log(String message){
		//TODO make actual logging utlity
		System.out.println(message);
	}
	
	public static void log(boolean message){
		log(message+"");
	}

	public static void log(Exception e){
		e.printStackTrace();
	}
}