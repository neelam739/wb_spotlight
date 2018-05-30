package oneapp.incture.workbox.dbproviders;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DatabasePropertyProvider {
	
	public static Map<String, Object> properties = null;
	
	/* Property Keys */
	final static String KEY_CACHE_SHARED_DEFAULT = "eclipselink.cache.shared.default";
	final static String KEY_JDBC_URL = "javax.persistence.jdbc.url";
	final static String KEY_JDBC_USER = "javax.persistence.jdbc.user";
	final static String KEY_JDBC_PASSWORD = "javax.persistence.jdbc.password";
	final static String KEY_JDBC_DRIVER = "javax.persistence.jdbc.driver";
	final static String KEY_PROVIDER = "provider";
	
	/* Property Values */
	
	/* Hana-DB Values */
	public final static String HANA_JDBC_DRIVER = "com.sap.db.jdbc.Driver";
//	public final static String HANA_JDBC_URL = "jdbc:sap://localhost:30015/SPOTLIGHT_USER";
	public final static String HANA_JDBC_URL = "jdbc:sap://vadbi4t.od.sap.biz:30015/SPOTLIGHT_USER";
	public final static String HANA_JDBC_USER = "SPOTLIGHT_USER";
	public final static String HANA_JDBC_PASSWORD = "Incture1234567891013";
	public final static Class<?> HANA_JDBC_PROVIDER_CLASS = org.eclipse.persistence.jpa.PersistenceProvider.class;
	public final static String HANA_CACHE_SHARED_DEFAULT = "false";
	
	/* Oracle DB Values */
	public final static String ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	public final static String ORACLE_JDBC_URL = "jdbc:oracle:thin:@34.213.118.108:1521:xe";
	public final static String ORACLE_JDBC_USER = "SPOTLIGHT";
	public final static String ORACLE_JDBC_PASSWORD = "SPOTLIGHT";
	public final static Class<?> ORACLE_JDBC_PROVIDER_CLASS = org.eclipse.persistence.jpa.PersistenceProvider.class;
	public final static String ORACLE_CACHE_SHARED_DEFAULT = "false";
	
	
	public static Map<String, Object> getConnectionProperties(String DB) {
		DBPropertiesDto propDto = null;
		if(DB.toLowerCase().contains("hana")) {
			propDto = DatabasePropertyProvider.getHanaProperties();
		} else if(DB.toLowerCase().contains("oracle")) {
			propDto = DatabasePropertyProvider.getOracleProperties();
		}
		properties = new HashMap<String, Object>();
		properties.put(KEY_PROVIDER, propDto.getProvider());
		properties.put(KEY_CACHE_SHARED_DEFAULT, propDto.getCacheSharedDefault());
		properties.put(KEY_JDBC_URL, propDto.getJdbcUrl());
		properties.put(KEY_JDBC_DRIVER, propDto.getJdbcDriver());
		properties.put(KEY_JDBC_USER, propDto.getUserId());
		properties.put(KEY_JDBC_PASSWORD, propDto.getPassword());
		Iterator<Map.Entry<String, Object>> entries = properties.entrySet().iterator();
		while(entries.hasNext()){
			Map.Entry<String, Object> entry = entries.next();
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
		return properties;
	}
	
	private static DBPropertiesDto getHanaProperties(){
		DBPropertiesDto propDto = new DBPropertiesDto();
		propDto.setProvider(DatabasePropertyProvider.HANA_JDBC_PROVIDER_CLASS);
		propDto.setCacheSharedDefault(DatabasePropertyProvider.HANA_CACHE_SHARED_DEFAULT);
		propDto.setJdbcDriver(DatabasePropertyProvider.HANA_JDBC_DRIVER);
		propDto.setJdbcUrl(DatabasePropertyProvider.HANA_JDBC_URL);
		propDto.setUserId(DatabasePropertyProvider.HANA_JDBC_USER);
		propDto.setPassword(DatabasePropertyProvider.HANA_JDBC_PASSWORD);
		return propDto;
	}
	
	private static DBPropertiesDto getOracleProperties(){
		DBPropertiesDto propDto = new DBPropertiesDto();
		propDto.setProvider(DatabasePropertyProvider.ORACLE_JDBC_PROVIDER_CLASS);
		propDto.setCacheSharedDefault(DatabasePropertyProvider.ORACLE_CACHE_SHARED_DEFAULT);
		propDto.setJdbcDriver(DatabasePropertyProvider.ORACLE_JDBC_DRIVER);
		propDto.setJdbcUrl(DatabasePropertyProvider.ORACLE_JDBC_URL);
		propDto.setUserId(DatabasePropertyProvider.ORACLE_JDBC_USER);
		propDto.setPassword(DatabasePropertyProvider.ORACLE_JDBC_PASSWORD);
		return propDto;
	}
}
