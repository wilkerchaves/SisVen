package utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

	private static SessionFactory factory = null;
	
	private static Configuration conf;
	
	private static SessionFactory buildSessionFactory() {
		try {
			conf = new Configuration();
			conf.configure("hibernate.cfg.xml");
			factory = conf.buildSessionFactory();
			return factory;
			
		}catch(Throwable e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		if(factory==null) {
			factory = buildSessionFactory();
			
		}
		return factory;
	}
}
