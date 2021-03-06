
package gov.bnl.gums.userGroup;

import gov.bnl.gums.FQAN;
import gov.bnl.gums.GridUser;
import gov.bnl.gums.configuration.Configuration;
import gov.bnl.gums.db.UserGroupDB;

import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger; 
import org.apache.log4j.Level;

import org.glite.pap.generated.XACMLPolicyManagement;
import org.glite.pap.generated.XACMLPolicyManagementService;
import org.glite.pap.generated.XACMLPolicyManagementServiceLocator;
/*
import os.schema.policy._0._2.xacml.tc.names.oasis.RuleType;
import os.schema.policy._0._2.xacml.tc.names.oasis.EffectType;
import os.schema.policy._0._2.xacml.tc.names.oasis.PolicyType;
import os.schema.policy._0._2.xacml.tc.names.oasis.TargetType;
import os.schema.policy._0._2.xacml.tc.names.oasis.SubjectType;
import os.schema.policy._0._2.xacml.tc.names.oasis.SubjectsType;
import os.schema.policy._0._2.xacml.tc.names.oasis.ObligationsType;
import os.schema.policy._0._2.xacml.tc.names.oasis.SubjectMatchType;
import os.schema.policy._0._2.xacml.tc.names.oasis.AttributeValueType;
*/
import org.apache.axis.types.URI;
import org.apache.axis.message.MessageElement;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants;;
import org.glite.security.util.DN;
import org.glite.security.util.DNImpl;

import org.glite.voms.generated.VOMSCompatibility;
import org.glite.voms.generated.VOMSCompatibilityServiceLocator;

/** A group of users residing on an Argus or VOMS server. This class is able to 
 * import a list of users from Argus that are banned. It will store to a local
 * medium through the UserGroupDB interface. It also manages the caching from
 * the local database.
 * <p>
 * The code is mostly taken from VOMSUserGroup.java
 *
 * @author Brian Bockelman
 */
public class BannedUserGroup extends UserGroup {

    static public String getTypeStatic() {
        return "banned";
    }


    private Logger log = Logger.getLogger(BannedUserGroup.class);
    private String endpoint = "";
    private String sslCertfile = null;
    private String sslKey = null;
    private String sslKeyPasswd = null;
    private String sslCAFiles = null;
    private String persistenceFactory = null;

    public BannedUserGroup() {
    	super();
    }    


    public BannedUserGroup(Configuration configuration) {
        super(configuration);
    }


    public BannedUserGroup(Configuration configuration, String name) {
        super(configuration, name);
    }


    public UserGroup clone(Configuration configuration) {
    	BannedUserGroup userGroup = new BannedUserGroup(configuration, new String(getName()));
    	userGroup.setDescription(new String(getDescription()));
    	userGroup.setAccess(new String(getAccess()));
    	userGroup.setEndpoint(new String(getEndpoint()));
        if (sslCertfile != null) { userGroup.setSslCertfile(new String(sslCertfile)); }
        if (sslKey != null) { userGroup.setSslKey(new String(sslKey)); }
        if (sslKeyPasswd != null) { userGroup.setSslKeyPasswd(new String(sslKeyPasswd)); }
        if (sslCAFiles != null) { userGroup.setSslCAFiles(new String(sslCAFiles)); }
        userGroup.setPersistenceFactory(new String(getPersistenceFactory()));
    	return userGroup;
    }


    public void setSslCertfile(String cert) {
        sslCertfile = cert;
    }


    public void setSslKey(String key) {
        sslKey = key;
    }


    public void setSslKeyPasswd(String passwd) {
        sslKeyPasswd = passwd;
    }


    public void setSslCAFiles(String files) {
        sslCAFiles = files;
    }


    public void setPersistenceFactory(String factory) {
        persistenceFactory = factory;
    }


    public String getPersistenceFactory() {
        return persistenceFactory;
    }

    public String getEndpoint() {
        return endpoint;
    }


    public java.util.List getMemberList() {
        if (getUserDB()!=null) {
            return getUserDB().retrieveMembers();
        } else {
            return new ArrayList();
        }
    }


    @Override
    public String getType() {
        return "banned";
    }


    private XACMLPolicyManagement getXACMLPolicyManagement() {
        try {
            log.trace("Service Locator: url='" + getEndpoint() + "'");
            XACMLPolicyManagementServiceLocator locator = new XACMLPolicyManagementServiceLocator();
            URL argusUrl = new URL( getEndpoint() );
            log.info("Connecting to Argus at " + argusUrl);
            return locator.getXACMLPolicyManagementService(argusUrl);
        } catch (Throwable e) {
            log.error("Couldn't get Argus endpoint: ", e);
            throw new RuntimeException("Couldn't get Argus endpoint: " + e.getMessage(), e);
        }
    }


    private VOMSCompatibility getVOMSCompatibility() {
        try {
            log.trace("VOMS Service Locator: url='" + getEndpoint());
            VOMSCompatibilityServiceLocator locator = new VOMSCompatibilityServiceLocator();
            URL vomsUrl = new URL(getEndpoint());
            log.info("Connecting to VOMS Compatibility at " + vomsUrl);
            return locator.getVOMSCompatibility(vomsUrl);
        } catch (Throwable e) {
            log.error("Couldn't get VOMS Compatiblity interface at " + getEndpoint() + ": ", e);
            throw new RuntimeException("Couldn't get VOMS Compatibility interface at " + getEndpoint() + ": " + e.getMessage(), e);
        }
    }


    @Override
    public boolean isInGroup(GridUser user) {
        if (getUserDB()!=null) {
            return getUserDB().isMemberInGroup(new GridUser(user.getCertificateDN(), null));
        } else {
            return false;
        }
    }


    @Override
    public boolean isDNInGroup(GridUser user) {
        return isInGroup(user);
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }


    public String toString() {
        return "BannedUserGroup: endpoint='" + endpoint + "'";
    }

    
    public String toString(String bgColor) {
    	return "<td bgcolor=\""+bgColor+"\"><a href=\"userGroups.jsp?command=edit&name=" + getName() + "\">" + getName() + "</a></td><td bgcolor=\""+bgColor+"\">" + getType() + "</td><td bgcolor=\""+bgColor+"\">&nbsp;</td><td bgcolor=\""+bgColor+"\">&nbsp;</td><td bgcolor=\""+bgColor+"\">&nbsp;</td><td bgcolor=\""+bgColor+"\">&nbsp;</td>";
    }


    public String toXML() {
    	String retStr = "\t\t<bannedUserGroup\n"+
            "\t\t\tname='"+getName()+"'\n"+
            "\t\t\taccess='"+accessTypes[accessIndex]+"'\n" +
            "\t\t\tdescription='"+getDescription()+"'\n"+
            "\t\t\tendpoint='"+endpoint+"'\n"+
            "\t\t\tpersistenceFactory='"+getPersistenceFactory()+"'\n";
        if (sslKey != null && !sslKey.equals(""))
            retStr += "\t\t\tsslKey='"+sslKey+"'\n";
        if (sslCertfile != null && !sslCertfile.equals(""))
            retStr += "\t\t\tsslCertfile='"+sslCertfile+"'\n";
        if (sslKeyPasswd != null && !sslKeyPasswd.equals(""))
            retStr += "\t\t\tsslKeyPasswd='"+sslKeyPasswd+"'\n";
        if (sslCAFiles != null && !sslCAFiles.equals(""))
            retStr += "\t\t\tsslCAFiles='"+sslCAFiles+"'\n";

        if (retStr.charAt(retStr.length()-1)=='\n') {
            retStr = retStr.substring(0, retStr.length()-1);
        }
    	retStr += "/>\n\n";
    	return retStr;
    }

    public void updateMembers() {
        if (getUserDB()!=null) {
            getUserDB().loadUpdatedList(retrieveMembers());
        } else {
            throw new RuntimeException("Could not updateMembers for " + endpoint + "; getUserDB returned null");
        }
    }


    private UserGroupDB getUserDB() {
        return getConfiguration().getPersistenceFactory(persistenceFactory).retrieveUserGroupDB( getName() );
    }


    /**
    * Retrieves the list of members for this BannedUsersGroup
    */
    public List<GridUser> retrieveMembers() {
        if (getEndpoint().endsWith("/VOMSCompatibility")) {
            return retrieveMembersVOMS();
        } else {
            return retrieveMembersArgus();
        }
    }

    private List<GridUser> retrieveMembersArgus() {
        Properties p = System.getProperties();
        try {
            setProperties();
            log.debug("SSL properties read: " + 
            "sslCertfile='" + System.getProperty("sslCertfile") +
            "' sslKey='" + System.getProperty("sslKey") +
            "' sslKeyPasswd set:" + (System.getProperty("sslKeyPasswd")!=null) +
            " sslCAFiles='" + System.getProperty("sslCAFiles") + "'" ); 
            System.setProperty("axis.socketSecureFactory", "org.glite.security.trustmanager.axis.AXISSocketFactory");
                     
            XACMLPolicyManagement policyMgmt = getXACMLPolicyManagement();
            
            //PolicyType [] policies = policyMgmt.listPolicies("default").getPolicy();
            MessageElement [] elements = policyMgmt.listPolicies("default").get_any();

            System.setProperties(p);
            List<GridUser> entries = new ArrayList<GridUser>();

            if (elements.length == 0) { throw new RuntimeException("Argus failed to return a valid policy!"); }
            InputStream is = new ByteArrayInputStream(elements[0].toString().getBytes());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            //System.out.println(elements[0].toString());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource source = new InputSource(br);
            Document doc = builder.parse(source);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/Policy/Rule[@Effect='Deny']/Target/Subjects/Subject/SubjectMatch[@MatchId='urn:oasis:names:tc:xacml:1.0:function:x500Name-equal']/AttributeValue[@DataType='urn:oasis:names:tc:xacml:1.0:data-type:x500Name']/text()");
            NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Node node = nodes.item(idx);
                //System.out.println(node.getNodeValue());
                DN dn = new DNImpl(node.getNodeValue());
                dn = new DNImpl(dn.getRFCDNv2());
                //System.out.println(dn.getX500());
                GridUser gridUser = new GridUser(dn.getX500(), null);
                entries.add(gridUser);
            }
            return entries;
        } catch (Throwable e) {
        	String message = "Couldn't retrieve argus banned users: ";
            log.error(message, e);
            e.printStackTrace();
            throw new RuntimeException(message + e.getMessage());
        }
    }


    private List<GridUser> retrieveMembersVOMS() {
        Properties p = System.getProperties();
        try {
            setProperties();
            log.debug("SSL properties read: " +
            "sslCertfile='" + System.getProperty("sslCertfile") +
            "' sslKey='" + System.getProperty("sslKey") +
            "' sslKeyPasswd set:" + (System.getProperty("sslKeyPasswd")!=null) +
            " sslCAFiles='" + System.getProperty("sslCAFiles") + "'" );
            System.setProperty("axis.socketSecureFactory", "org.glite.security.trustmanager.axis.AXISSocketFactory");

            VOMSCompatibility vomscompat = getVOMSCompatibility();

            URL tempURL = new URL(getEndpoint());
            String container = "/";
            String [] path = tempURL.getPath().split("/");
            if (path.length >= 3) {
                container += path[path.length-3];
            }

            String[] users = vomscompat.getGridmapUsers(container);

            if (users.length > 0) {
                log.info("Retrieved " + users.length + " users.");
                log.info("First user is: '" + users[0] + "'");
                log.info("Last user is: '" + users[users.length - 1 ] + "'");
            } else {
                log.info("Retrieved no users.");
            }

            List<GridUser> entries = new ArrayList<GridUser>();
            for (int n=0; n < users.length; n++) {
                GridUser gridUser = new GridUser(users[n], null);
                entries.add(gridUser);
            }
            return entries;
        } catch (Throwable e) {
            String message = "Couldn't retrieve users: ";
            log.error(message, e);
            e.printStackTrace();
            throw new RuntimeException(message + e.getMessage(), e);
        }
    }


    public String getSslCertfile() {
        if (sslCertfile == null || sslCertfile.equals("")) { return "/etc/grid-security/http/httpcert.pem"; }
        return sslCertfile;
    }


    public String getSslKey() {
        if (sslKey == null || sslKey.equals("")) { return "/etc/grid-security/http/httpkey.pem"; }
        return sslKey;
    }


    public String getSslKeyPasswd() {
        if (sslKeyPasswd == null || sslKeyPasswd.equals("")) { return ""; }
        return sslKeyPasswd;
    }


    public String getSslCAFiles() {
        if (sslCAFiles == null || sslCAFiles.equals("")) { return "/etc/grid-security/certificates/*.0"; }
        return sslCAFiles;
    }


    private void setProperties() {
        log.debug( "SSL properties set: sslCertfile='" + getSslCertfile() + "' sslKey='" + getSslKey() + "' sslKeyPasswd set:" + (!getSslKeyPasswd().equals("")) + " sslCAFiles='" + getSslCAFiles() + "'" ); 
        if (!getSslCertfile().equals("")) {
            System.setProperty("sslCertfile", getSslCertfile());
        }
        if (!getSslKey().equals("")) {
            System.setProperty("sslKey", getSslKey());
        }
        if (!getSslKeyPasswd().equals("")) {
            System.setProperty("sslKeyPasswd", getSslKeyPasswd());
        }
        if (!getSslCAFiles().equals("")) {
            System.setProperty("sslCAFiles", getSslCAFiles());
        }
    }
}
