package com.sample.cdi.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Set;


import javax.enterprise.inject.spi.InjectionPoint;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
	
/*****
 * Helper class to ease lookup of EJBs.
 * 
 * Lookup is based upon @ApplicationService annotation: when an EJB implementing
 * a given interface is searched, JNDI tree is recursively analyzed to retrieve
 * all EJBs JNDI names. A match occours when a JNDI name contains the FQN of the searched interface.
 * Among all matching EJBs, is returned:
 * - the implementation matching required version;
 * - default implementation if no version matching occours;
 * If no EJB JNDI name matches required interface, null is returned.
 */

public class LookupHelper {
	
	private static InitialContext ctx 	    = null;
	private static String JNDI_EJB_ROOT     = "java:app";
	private static LookupHelper INSTANCE    = null;
	private static ArrayList<CachedEntry> jndiCache = new ArrayList<CachedEntry>();
	
	
	private static Version	currentVersion  = Version.PRODUCTION;
	 
	
	/***
	 * Cache element to speed up searching JNDI tree. When a matching <interface, version> couple is found,
	 * its corresponding JNDI name is stored for subsequent searches. 
	 * 
	 */
	class CachedEntry {
		private String intfName = null;
		private Version version  = null;
		private String jndiName = null;
		
		CachedEntry(String intfName, Version version) {
			this.intfName = intfName;
			this.version  = version;
		}
		
		CachedEntry(String intfName, Version version, String jndiName) {
			this.intfName = intfName;
			this.version  = version;
			this.jndiName = jndiName;
		}
		
		String getJNDIName() {
			return jndiName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((intfName == null) ? 0 : intfName.hashCode());
			result = prime * result + ((version == null) ? 0 : version.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CachedEntry other = (CachedEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (intfName == null) {
				if (other.intfName != null)
					return false;
			} else if (!intfName.equals(other.intfName))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}
		private LookupHelper getOuterType() {
			return LookupHelper.this;
		}
	}
	
	private LookupHelper() { }

	public static void setCurrentVersion(Version version) {
		currentVersion = version;
	}
	
	/**
	 * Old fashioned factory..
	 */
	
	public static LookupHelper getInstance() {
		try {
			if(INSTANCE == null) {
				ctx = new InitialContext();
				INSTANCE = new LookupHelper();
			}
			return INSTANCE;
		}
		catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Used by ServiceLocator's producer methods to perform injection.
	 * This method calculates actual version to be used for look up using {@link InjectionPoint} injectionPoint
	 * parameter:
	 * - if the injected resource has a qualified version, that version is used.
	 * - if the injected resource has not a qualified version, then currentVersion is used.
	 */
	public <T> T lookup(Class<T> clz, InjectionPoint injectionPoint) {
    	try {
    		Version requiredVersion  = Version.CURRENT_CONFIG;
	    	Set<Annotation> qualifiers =  injectionPoint.getQualifiers();
    		if (qualifiers !=null && !qualifiers.isEmpty()) {
		    	for(Annotation ann : qualifiers) {
		    		if (ann instanceof ApplicationService) {
		    			requiredVersion = ((ApplicationService) ann).value();
		    		}
		    	}
	    	}
    		if(requiredVersion == Version.CURRENT_CONFIG) {
    			requiredVersion = currentVersion;
    		}
	    	return (T) lookup(clz, requiredVersion);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
		
    }
	    
	public <T> T lookup(Class<T> interfaceRequired) {
		return lookup(interfaceRequired,currentVersion);
	}
	     
    public <T> T lookup(Class<T> interfaceRequired,Version requiredVersion) {
    	System.out.println("Invoking custom producer ==> "+interfaceRequired.getCanonicalName()+", version : "+requiredVersion);
    	try {
            NamingEnumeration<NameClassPair> enumeration = ctx.list(JNDI_EJB_ROOT);
            while(enumeration.hasMoreElements()) {
                NameClassPair obj = enumeration.next();
                String subMatch= obj.getName();
                //Traversing all JNDI names and subtrees until a match is found.
                T matched = lookup(interfaceRequired, "java:app/"+subMatch, requiredVersion);
                if(matched != null) {
                    return matched;
                }
            }
            return null;
        }
        catch(Exception e) {
            return null;
        }
    }
	     
	     
	     
    @SuppressWarnings("unchecked")
    private <T> T lookup(Class<T> interfaceRequired,String jndiRoot, Version requiredVersion) {
        try {
            T requiredImplementation = null;
            T defaultImplementation  = null;
            String jndiName  		 = null;
            CachedEntry cache = new CachedEntry(interfaceRequired.getCanonicalName(), requiredVersion);
            int cachedIndex	  = jndiCache.indexOf(cache);
            if(cachedIndex >= 0) {
            	String cachedJndiName = jndiCache.get(cachedIndex).getJNDIName();
            	return (T) ctx.lookup(cachedJndiName);
            }
            NamingEnumeration<NameClassPair> enumeration = ctx.list(jndiRoot);
            while(enumeration.hasMoreElements()) {
                NameClassPair obj = enumeration.next();
                String objName    = obj.getName(); 
                
                if(objName.contains(interfaceRequired.getCanonicalName())) {
                   T implementation = (T) ctx.lookup(jndiRoot+"/"+objName);
                    Class<?> clz = Class.forName(obj.getClassName());
                    if(clz.isAnnotationPresent(ApplicationService.class)) {
                    	Version version = ((ApplicationService)clz.getAnnotation(ApplicationService.class)).value();
                        if(version == requiredVersion) {
                        	requiredImplementation = implementation;
                        	jndiName			   = jndiRoot+"/"+objName;
                        }
                        if(version == Version.BASE) {
                        	defaultImplementation = implementation;
                        	jndiName = jndiName == null ? jndiRoot+"/"+objName : jndiName;
                        }
                    }
                }
            }
            if(jndiName != null) {
            	System.out.println("Registering : JNDIName ["+jndiName+"] for endpoint ["+interfaceRequired.getCanonicalName()+"] version : "+requiredVersion);
            	CachedEntry newEntry = new CachedEntry(interfaceRequired.getCanonicalName(), requiredVersion, jndiName);
            	jndiCache.add(newEntry);
            }
            return requiredImplementation == null ? defaultImplementation : requiredImplementation;
        }
        catch(Exception e) {
            return null;
        }
    }
}
