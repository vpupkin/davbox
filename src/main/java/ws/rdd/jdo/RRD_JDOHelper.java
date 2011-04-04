package ws.rdd.jdo;

import java.util.Properties;

import javax.jdo.JDOHelper; 
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.listener.InstanceLifecycleListener;

public class RRD_JDOHelper implements InstanceLifecycleListener {
	private static final RRD_JDOHelper me = new RRD_JDOHelper();
	PersistenceManagerFactory PMF = null;
	
	private RRD_JDOHelper (){
		init();
	}
	public static boolean isGAE() {
		boolean retval = false;
		try{
			retval  = !(System.getProperty("com.google.appengine.runtime.version")==null);
		}catch(Throwable e){}
		return retval;
	}	
	void init(){
		try{
		 if (isGAE())
			 PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
		 else{
			//#3
			PMF = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		 }
		 Class[] classes = null;
		 InstanceLifecycleListener listener = this;
		 PMF.addInstanceLifecycleListener(listener , classes);
		}catch(Throwable e){
			e.printStackTrace();
			Properties properties = new Properties();
//MySQL
//			properties.setProperty("javax.jdo.PersistenceManagerFactoryClass",
//			                "org.datanucleus.jdo.JDOPersistenceManagerFactory");
//			properties.setProperty("javax.jdo.option.ConnectionDriverName","com.mysql.jdbc.Driver");
//			properties.setProperty("javax.jdo.option.ConnectionURL","jdbc:mysql://localhost/myDB");
//			properties.setProperty("javax.jdo.option.ConnectionUserName","login");
//			properties.setProperty("javax.jdo.option.ConnectionPassword","password");
			
//			Config Param 	
//			Config Value 	
//			Comment 
//
//			javax.jdo.option.ConnectionURL 	
//			jdbc:derby:;databaseName=../build/test/junit_metastore_db;create=true 	
//			derby database located at hive/trunk/build... 
//
//			javax.jdo.option.ConnectionDriverName 	
//			org.apache.derby.jdbc.EmbeddedDriver 	
//			Derby embeded JDBC driver class			
//			properties.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.jdo.JDOPersistenceManagerFactory");
//			properties.setProperty("javax.jdo.option.ConnectionDriverName","org.apache.derby.jdbc.EmbeddedDriver");
//			properties.setProperty("javax.jdo.option.ConnectionURL","jdbc:derby:;databaseName=junit_metastore_db;create=true");
//			properties.setProperty("javax.jdo.option.ConnectionUserName","login");
//			properties.setProperty("javax.jdo.option.ConnectionPassword","password");			
//			PMF = JDOHelper.getPersistenceManagerFactory(properties);


		}
	}
 
	
	public static RRD_JDOHelper getInstance(){
		return me;
	}

	public PersistenceManagerFactory getPMF() {
		if (PMF == null) init();
		return PMF;
	}
	@Override
	protected void finalize() throws Throwable {
		if(PMF != null){
			PMF.close();
			PMF = null;
		}
	}
	
	
 
}
