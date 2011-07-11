package org.eclipse.remail.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.remail.Activator;
import org.eclipse.remail.Mail;
import org.eclipse.remail.couchdb.helper.CouchDBMethodName;
import org.eclipse.remail.couchdb.helper.HttpGetView;
import org.eclipse.remail.couchdb.util.CouchDBView;
import org.eclipse.remail.preferences.PreferenceConstants;

import com.fourspaces.couchdb.Session;

/**
 * This is a "static" class that stores which classes have been already searched
 * 
 * @author Lorenzo Baracchi <lorenzo.baracchi@usi.ch>
 * 
 */
public class CacheCouchDB {

	// big test
	private static String dbname = "big-test";
	// Uncomment for use the small test
	// private String dbname="small-db";

	private static Set<String> classSearched = new HashSet<String>();

	/**
	 * Tells if the given class has already been searched
	 * 
	 * @param classname
	 *            the name of the class
	 * @return true if it has been searched, false otherwise
	 */
	public static boolean containsClass(final String classname) {
		boolean is=classSearched.contains(classname);
		if(is)
			return classSearched.contains(classname);
		else{
			is=checkCouchDBcache(classname);
			if(is)
				addClass(classname);
		}
		return is;
	}

	/**
	 * Add the given class to the list of class searched
	 * 
	 * @param classname
	 *            the name of the class
	 */
	public static void addClass(final String classname) {
		classSearched.add(classname);
	}
	
	public static boolean checkCouchDBcache (final String classname) {
				
		//check the method used
		String methodUsed="";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String method = store.getString(PreferenceConstants.P_METHOD);
		if (method.contains("searchSensitive"))
			methodUsed=CouchDBMethodName.CASE_SENSITIVE.getName();
		else if (method.contains("searchInsensitive"))
			methodUsed=CouchDBMethodName.CASE_INSENSITIVE.getName();
		else if (method.contains("searchStrict"))
			methodUsed=CouchDBMethodName.SEARCH_STRICT.getName();
		else if (method.contains("searchLoose"))
			methodUsed=CouchDBMethodName.SEARCH_LOSE.getName();
		else if (method.contains("searchDict"))
			methodUsed=CouchDBMethodName.SEARCH_DICT.getName();
		else if (method.contains("searchCamel"))
			methodUsed=CouchDBMethodName.SEARCH_CAMEL.getName();
		
		//check if exist the query
		String uri=CouchDBView.server+dbname+"/_design/"+methodUsed+"-"+classname;
		HttpGetView hgv = new HttpGetView(uri);
		String response = hgv.sendRequest();
//		System.out.println(response);
		if(response.equals("{\"error\":\"not_found\",\"reason\":\"missing\"}"))
			return false;
		else 
			return true;
	}
}
