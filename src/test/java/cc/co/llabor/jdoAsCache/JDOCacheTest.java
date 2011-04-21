package cc.co.llabor.jdoAsCache;

import junit.framework.TestCase;

/** 
 * <b>Description:TODO</b>
 * @author      gennady<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  Feb 24, 2011::11:27:05 PM<br> 
 */
public class JDOCacheTest extends TestCase {
	public static boolean isGAE() {
		return !(System.getProperty("com.google.appengine.runtime.version")==null);
	}	
	public void test1st(){
		if(isGAE())return;
		net.sf.jsr107cache.Cache c = new JDOCache ("Jo!");
		c.put("1", "1");
		Object o1 = c.get("1");
		assertEquals(o1, "1");
	}

	public void testDEL(){
		if(isGAE())return;
		net.sf.jsr107cache.Cache c = new JDOCache ("Jo!");
		c.put("1", "1");
		Object o1 = c.get("1");
		assertEquals(o1, "1");
		Object o2 = c.remove("1");
		//assertEquals(o2, o1);
		Object o3 = c.get("1");
		assertEquals(null,o3);
	}	
	
}


 