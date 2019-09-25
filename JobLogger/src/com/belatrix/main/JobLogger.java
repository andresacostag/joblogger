package com.belatrix.main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;
	private static Map<String, String> dbParams;
	private static Logger logger;
	
	//Params
	private static String userName; 
	private static String password;
	private static String dbms;
	private static String serverName;
	private static String portNumber;
	private static String dbName;
	private static String schema;
	private static String logFileFolder;

	public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
			boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map<String, String> dbParamsMap) {
		logger = Logger.getLogger("MyLog");  
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		logToDatabase = logToDatabaseParam;		
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;	
		
		dbParams = dbParamsMap;
		
		buildParams();
	}
	
	public void buildParams() {
		
		if (dbParams != null) {
			userName = dbParams.get("userName") != null ? dbParams.get("userName") : ""; 
			password = dbParams.get("password") != null ? dbParams.get("password") : ""; 
			dbms = dbParams.get("dbms") != null ? dbParams.get("dbms") : ""; 
			serverName = dbParams.get("serverName") != null ? dbParams.get("serverName") : ""; 
			portNumber = dbParams.get("portNumber")!= null ? dbParams.get("portNumber") : ""; 
			dbName = dbParams.get("dbName") != null ? dbParams.get("dbName") : ""; 
			schema = dbParams.get("schema") != null ? dbParams.get("schema") : ""; 
			logFileFolder = dbParams.get("logFileFolder") != null ? dbParams.get("logFileFolder") : ""; 
		}
	}

	public static void logMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
		
		if (messageText == null || messageText.length() == 0) {
			
			return;
		} else {
			
			messageText = messageText.trim();
		}
		if (!logToConsole && !logToFile && !logToDatabase) {
			
			throw new Exception("Invalid configuration");
		}
		if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
			
			throw new Exception("Error or Warning or Message must be specified");
		}

		int t = 0;

		String l = "";		
		
		 SimpleDateFormat SDFormat  = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		
		if (message && logMessage) {
			
			l = l + SDFormat.format(new Date()) +" MESSAGE " + messageText;
			t = 1;
		}
		
		if (error && logError) {
			
			l = l + SDFormat.format(new Date()) +" ERROR " + messageText;
			t = 2;
		}

		if (warning && logWarning) {
			
			l = l + SDFormat.format(new Date()) +" WARNING " + messageText;
			t = 3;
		}

		if (!l.isEmpty()) {
		
			if(logToFile) {
				
				logToFile(dbParams, l);
			}
			
			if(logToConsole) {
				
				logToConsole(l);
			}
			
			if(logToDatabase) {
				
				logToDataBase(dbParams, l, t);
			}
		}
	}
	
	private static void logToConsole(String messageText) {
		
		ConsoleHandler ch = new ConsoleHandler();
		//logger.addHandler(ch);
		logger.log(Level.INFO, messageText);
	}
	
	private static void logToFile(Map<String, String> dbParams, String messageText) throws Exception{
			
			try {
				
			File logFile = new File(logFileFolder + "/logFile.txt");
			if (!logFile.exists()) {
				
				logFile.createNewFile();			
			}
			
			FileHandler fh = new FileHandler(logFileFolder + "/logFile.txt", true);
			
			logger.addHandler(fh);
			//logger.log(Level.INFO, messageText);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	}
	
	private static void logToDataBase(Map<String, String> dbParams, String messageText, int t) throws Exception{
		
		if (dbParams != null) {
		
			Connection connection = null;
			Statement stmt = null;
			Properties connectionProps = new Properties();
			connectionProps.put("user", userName);
			connectionProps.put("password", password);
			connectionProps.put("schema", schema);
	
			try {
				
				connection = DriverManager.getConnection("jdbc:" + dbms + "://" + serverName
						+ ":" + portNumber + "/" + dbName, connectionProps);
				stmt = connection.createStatement();
				stmt.execute("set search_path = 'joblogger'");
				stmt.executeUpdate("insert into log values('" + messageText + "', " + String.valueOf(t) + ")");
			} catch (SQLException e) {
				
				e.printStackTrace();
			} finally {
				
				if (stmt != null) {
					
					stmt.close();
				} 
				if (connection != null) {
					
					connection.close();
				}
			}
		} else {
			
			throw new Exception("Deben ser incluidas las propiedades");
		}		
	}
}

