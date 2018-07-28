package logger;

import java.util.logging.*;

public class LogManager {
	public Logger mLogger;
	public FileHandler fileManager;
	
	public LogManager(String name) {
		try {
			mLogger = Logger.getLogger(LogManager.class.getName());
			fileManager = new FileHandler(name + ".log", true);
			mLogger.addHandler(fileManager);	
			mLogger.setUseParentHandlers(false);
			SimpleFormatter format = new SimpleFormatter();
			fileManager.setFormatter(format);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
