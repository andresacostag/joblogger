package com.belatrix.main;

import java.util.HashMap;

public class Application {

	public static void main(String[] args) {

		HashMap<String, String> map = new HashMap<>();
		map.put("userName", "postgres");
		map.put("password", "adminpostgres");
		map.put("dbms", "postgresql");
		map.put("serverName", "localhost");
		map.put("portNumber", "5432");
		map.put("dbName", "pruebas");
		map.put("schema", "pruebas");
		map.put("logFileFolder", "C:/Users/aacosta/Documents/Desarrollo");
		
		JobLogger jobLogger = new JobLogger(true, true, true,
				false, false, true, map);
		try {
			jobLogger.logMessage("Mensaje de prueba", false, false, true);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
