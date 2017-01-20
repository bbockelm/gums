/*
 * VOMSUserGroupManager.java
 *
 * Created on May 25, 2004, 10:20 AM
 */

package gov.bnl.gums.userGroup;

import gov.bnl.gums.FQAN;
import gov.bnl.gums.GridUser;
import gov.bnl.gums.configuration.Configuration;
import gov.bnl.gums.db.UserGroupDB;

import java.net.URL;

import org.apache.log4j.Logger; 
import org.apache.log4j.Level;

/** A group of users residing on a VOMS vo database. This class is able to 
 * import a list of users from a VOMS server. It will store to a local
 * medium through the UserGroupDB interface. It also manages the caching from
 * the local database.
 * <p>
 * The authentication is done through the proxy, or a certificate/key/password
 * combination. The parameters are to be set externally as system properties.
 * The proxy can be set through "gridProxyFile" property. Other properties
 * are "sslCertfile", "sslKey", "sslKeyPasswd" and "sslCAFiles". More documentation
 * can be found in the documentation of the edg trustmanager  
 *
 * @todo Should refactor with LDAPGroup, and provide a PersistanceCachedGroup
 * since they both share local site buffering functionality
 * @author Gabriele Carcassi, Jay Packard
 */
public class VOMSUserGroup extends UserGroup {
	static private final boolean defaultAcceptProxyWithoutFQAN = true;
	static private final String defaultMatchFQAN = "ignore";
	static private String[] matchFQANTypes = {"exact","vorole","role","vogroup","vo","ignore"};
	
    static public String getTypeStatic() {
		return "voms";
	}
    
    static public List getMatchFQANTypes() {
		ArrayList retList = new ArrayList();
		for(int i=0; i<matchFQANTypes.length; i++)
			retList.add(matchFQANTypes[i]);
		return retList;
	}

    private Logger log = Logger.getLogger(VOMSUserGroup.class);
    private String voGroup = "";
    private String role = "";
    private String fqan = null;
    private String matchFQAN = defaultMatchFQAN;

	private boolean acceptProxyWithoutFQAN = defaultAcceptProxyWithoutFQAN;
    
    public VOMSUserGroup() {
    	super();
    }    
 
    public VOMSUserGroup(Configuration configuration) {
        super(configuration);
    }
    
    public VOMSUserGroup(Configuration configuration, String name) {
        super(configuration, name);
    }
    
    public UserGroup clone(Configuration configuration) {
    	VOMSUserGroup userGroup = new VOMSUserGroup(configuration, new String(getName()));
    	userGroup.setDescription(new String(getDescription()));
    	userGroup.setAccess(new String(getAccess()));
    	userGroup.setVomsServer(new String(getVomsServer()));
    	userGroup.setRole(new String(getRole()));
    	userGroup.setVoGroup(new String(getVoGroup()));
    	userGroup.setMatchFQAN(new String(getMatchFQAN()));
    	userGroup.setRemainderUrl(new String(getRemainderUrl()));
    	userGroup.setAcceptProxyWithoutFQAN(acceptProxyWithoutFQAN);
    	return userGroup;
    }
    
    /**
     * The scheme according to which the FQAN will be matched.
     * <p>
     * Possible values are:
     * <ul>
     *   <li>exact (default) - role, group, and vo have to match. </li>
     *   <li>vorole - role and vo have to match.</li>
     *   <li>role - role has to match.</li>
     *   <li>group, vogroup - group and vo have to match.</li>
     *   <li>vo - vo has to match.</li>
     *   <li>ignore - no matching.</li>
     * </ul>
     * @return matching type as String.
     */
    public String getMatchFQAN() {
   		return matchFQAN;
    }
    
    public List<GridUser> getMemberList() {
        return new ArrayList<GridUser>();
    }
    
    public String getRemainderUrl() {
    	return remainderUrl;
    }
    
    public String getType() {
		return "voms";
	}
    
    public String getUrl() {
		if (getVoObject()!=null)
			return getVoObject().getBaseUrl() + remainderUrl;
		else
			return "";
    }
    
    public String getVomsServer() {
    	return vomsServer;
    }
    
    /**
     * Returns the VO group.
     * @return The group in the VOMS (i.e. /atlas/usatlas)
     */
    public String getVoGroup() {
        return this.voGroup;
    }
    
    
    /**
     * Changes the role.
     * @return The role name in the VOMS server (i.e. myrole), or "" for no role
     */
    public String getRole() {
        return this.role;
    }
    
    /**
     * True if non-VOMS will be accepted. If true, all non-VOMS proxies with a matchin
     * DN will be matched. VOMS proxies won't be affected by the use of this property.
     * @return True if group will accept non-VOMS proxies
     */
    public boolean isAcceptProxyWithoutFQAN() {
        return false;
    }
    
    /**
     * Convenience function for "ignore".equals(getmatchFQAN())
     * @return False if FQAN is used during the match
     */
   public boolean isIgnoreFQAN() {
        return "ignore".equals(matchFQAN);
    }

    @Override
    public boolean isInGroup(GridUser user) {
        if (fqan == null) {
            log.error("FQAN missing for VOMS User Group '" + getName() +
                      "' (this indicates a missing 'voGroup' attribute in gums.config).");
            return false;
        }

    	if (user.getVoFQAN() == null) {
            // We no longer allow VOMS-based lookups without a VOMS proxy.
	    return false;
    	}
        if (!user.getVerified()) {
            log.error("User's FQAN " + user.getVoFQAN() + " was not verified by remote callout; denying access.");
            return false;
        }

        // We now know we don't have user.getVoFQAN()==null

        // If we have vorole match, entire fqan has to be the same
        if ("exact".equals(getMatchFQAN())) {
            if (!user.getVoFQAN().toString().equals(fqan))
                return false;
        }
        
        // If we have a vo-role match, vo and role has to be the same
        if ("vorole".equals(getMatchFQAN()) && user.getVoFQAN().getVo()!=null && user.getVoFQAN().getRole()!=null) {
        	FQAN theFQAN = new FQAN(fqan);
            if (!user.getVoFQAN().getVo().equals(theFQAN.getVo()) && !user.getVoFQAN().getRole().equals(theFQAN.getRole()))
                return false;
        }
        
        // If we have a role match, role has to be the same
        if ("role".equals(getMatchFQAN()) && user.getVoFQAN().getRole()!=null) {
        	FQAN theFQAN = new FQAN(fqan);
            if (!user.getVoFQAN().getRole().equals(theFQAN.getRole()))
                return false;
        }
        
        // If we match the group, we make sure the VO starts with the group
        if ("group".equals(getMatchFQAN()) || "vogroup".equals(getMatchFQAN())) {
            if (!user.getVoFQAN().toString().startsWith(voGroup))
                return false;
        }

        // If we match the vo, we check the vo is the same
        if ("vo".equals(getMatchFQAN()) && user.getVoFQAN().getVo()!=null) {
            FQAN theFQAN = new FQAN(fqan);
            if (!user.getVoFQAN().getVo().equals(theFQAN.getVo()))
                return false;
        }

    }

    @Override
    public boolean isDNInGroup(GridUser user) {
        return false;
    }

    /**
     * Changes the way non-VOMS proxies are handled.
     * @param acceptProxyWithoutFQAN True if group will accept non-VOMS proxies
     */
    public void setAcceptProxyWithoutFQAN(boolean acceptProxyWithoutFQAN) {
        this.acceptProxyWithoutFQAN = acceptProxyWithoutFQAN;
    }

    /**
     * Changes the scheme according to which the FQAN will be matched. See
     * getMatchFQAN for more details.
     * @param matchFQAN One of the following:  "exact, "vorole, "role", "vogroup", "vo", "ignore". (also "group" for backwards compat.)
     */
    public void setMatchFQAN(String matchFQAN) {
    	boolean found = false;
    	if (matchFQAN.equals("group"))
    		matchFQAN = "vogroup";
    	if (matchFQAN.equals(""))
    		matchFQAN = "exact";
    	for (int i=0; i<matchFQANTypes.length; i++)
    		if (matchFQANTypes[i].equalsIgnoreCase(matchFQAN)) found = true;
    	if (!found)
    		throw new RuntimeException("Invalid match FQAN string: "+matchFQAN);
        this.matchFQAN = matchFQAN;
    }
    
    public void setRemainderUrl(String remainderUrl) {
    	this.remainderUrl = remainderUrl;
    }

    /**
     * Set name of VOMS Server
     * @param vo
     */
    public void setVomsServer(String vomsServer) {
    	this.vomsServer = vomsServer;
    }
    
    /**
     * Changes the VO group.
     * @param voGroup The group in the VOMS (i.e. /atlas/usatlas)
     */
    public void setVoGroup(String voGroup) {
        this.voGroup = voGroup;
        prepareFQAN();
    }
    
    /**
     * Changes the role.
     * @param role The role in the VOMS (i.e.production)
     */
    public void setRole(String role) {
        this.role = role;
        prepareFQAN();
    }

    public String toString() {
        return "VOMSGroup: voGroup='" + getVoGroup() + "' - role='" + getRole() + "'";
    }
    
    public String toString(String bgColor) {
    	return "<td bgcolor=\""+bgColor+"\"><a href=\"userGroups.jsp?command=edit&name=" + getName() + "\">" + getName() + "</a></td><td bgcolor=\""+bgColor+"\">" + getType() + "</td><td bgcolor=\""+bgColor+"\">" + matchFQAN + "</td><td bgcolor=\""+bgColor+"\">" + acceptProxyWithoutFQAN + "</td><td bgcolor=\""+bgColor+"\">" + voGroup + "&nbsp;</td><td bgcolor=\""+bgColor+"\">" + role + "&nbsp;</td>";
    }

    public String toXML() {
    	String retStr = "\t\t<vomsUserGroup\n"+
		"\t\t\tname='"+getName()+"'\n"+
		"\t\t\taccess='"+accessTypes[accessIndex]+"'\n" +
		"\t\t\tdescription='"+getDescription()+"'\n"+
        "\t\t\tvomsServer='"+vomsServer+"'\n";
    	if (!voGroup.equals(""))
        	retStr += "\t\t\tvoGroup='"+voGroup+"'\n";
    	if (!role.equals(""))
    		retStr += "\t\t\trole='"+role+"'\n";
    	if (retStr.charAt(retStr.length()-1)=='\n')
    		retStr = retStr.substring(0, retStr.length()-1);
    	retStr += "/>\n\n";
    	return retStr;
    }

    private void prepareFQAN() {
        if (!voGroup.equals("")) {
            if (!role.equals("") && !voGroup.equals(""))
            	fqan = voGroup + "/Role=" + role;
            else if (!voGroup.equals(""))
                fqan = voGroup;
            else
            	fqan = null;
        }
    }
}
