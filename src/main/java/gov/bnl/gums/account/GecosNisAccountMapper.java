/*
 * GecosNisAccountMapper.java
 *
 * Created on April 13, 2005, 4:21 PM
 */

package gov.bnl.gums.account;



import gov.bnl.gums.configuration.Configuration;

import java.util.Properties;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Matches the DN with the account information retrieved from a NIS server.
 *
 * @author Gabriele Carcassi
 */
public class GecosNisAccountMapper extends GecosAccountMapper {
    static private Log log = LogFactory.getLog(GecosNisAccountMapper.class);
    private String jndiNisUrl = "";
    
    public GecosNisAccountMapper() {
    	super();
    }
 
    public GecosNisAccountMapper(Configuration configuration) {
    	super(configuration);
    }
    
    public GecosNisAccountMapper(Configuration configuration, String name) {
    	super(configuration, name);
    }
    
    /**
     * Returns the URL used to describe the NIS server.
     * @return NIS url according to JNDI NIS driver.
     */
    public String getJndiNisUrl() {
        return jndiNisUrl;
    }
    
    /**
     * Changes the NIS server to use.
     * @param jndiNisUrl NIS url according to JNDI NIS driver.
     */
    public void setJndiNisUrl(String jndiNisUrl) {
        this.jndiNisUrl = jndiNisUrl;
    }
    
    /** Prepares the parameters for the JNDI connection.
     */
    private Properties retrieveJndiProperties() {
        Properties jndiProperties = new java.util.Properties();
        jndiProperties.put("java.naming.provider.url", jndiNisUrl);
        jndiProperties.put("java.naming.factory.initial","com.sun.jndi.nis.NISCtxFactory");
        return jndiProperties;
    }
    
    protected GecosMap createMap() {
        Properties jndiProperties = retrieveJndiProperties();
        int nTries = 5;
        Exception lastException = null;
        int i = 0;
        for (; i < nTries; i++) {
            GecosMap map = new GecosMap();
            log.debug("Attemp " + i + " to retrieve map for '" + jndiNisUrl + "'");
            try {
                DirContext jndiCtx = new InitialDirContext(jndiProperties);
                NamingEnumeration nisMap = jndiCtx.search("system/passwd.byname", "(cn=*)", null);
                log.trace("Server responded");
                while (nisMap.hasMore()) {
                    SearchResult res = (SearchResult) nisMap.next();
                    Attributes atts = res.getAttributes();
                    String username = (String) atts.get("cn").get();
                    Attribute gecosAtt = atts.get("gecos");
                    if (gecosAtt != null) {
                        String gecos = gecosAtt.get().toString();
                        map.addEntry(username, gecos);
                    } else {
                        log.trace("Found user '" + username + "' with no GECOS field");
                    }
                }
                jndiCtx.close();
                return map;
            } catch (javax.naming.NamingException ne) {
                log.warn("Error filling the maps for NIS "+jndiNisUrl, ne);
                lastException = ne;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.warn("Interrupted", e);
                }
            } catch (Exception e) {
                log.warn("Error filling the maps for NIS "+jndiNisUrl, e);
                lastException = e;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    log.warn("Interrupted", e);
                }
            }
        }
        if (i == nTries) {
            throw new RuntimeException("Couldn't retrieve NIS maps from " + jndiNisUrl, lastException);
        }
        return null;
    }

    protected String mapName() {
        return jndiNisUrl;
    }

    public String toString(String bgColor) {
    	return "<td bgcolor=\""+bgColor+"\">" + getName() + "</td><td bgcolor=\""+bgColor+"\">" + jndiNisUrl + "</td>";
    }

    public String toXML() {
    	return super.toXML() +
			"\t\t\tjndiNisUrl='"+jndiNisUrl+"'/>\n\n";
    }      
    
    public AccountMapper clone(Configuration configuration) {
    	GecosNisAccountMapper accountMapper = new GecosNisAccountMapper(configuration, getName());
    	accountMapper.setJndiNisUrl(jndiNisUrl);
    	return accountMapper;
    }
}
