/**
 * ACLEntry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.glite.security.voms;

public class ACLEntry  implements java.io.Serializable {
    private java.lang.String adminCA;

    private java.lang.String adminDN;

    private boolean allow;

    private java.lang.String operationName;

    public ACLEntry() {
    }

    public ACLEntry(
           java.lang.String adminCA,
           java.lang.String adminDN,
           boolean allow,
           java.lang.String operationName) {
           this.adminCA = adminCA;
           this.adminDN = adminDN;
           this.allow = allow;
           this.operationName = operationName;
    }


    /**
     * Gets the adminCA value for this ACLEntry.
     * 
     * @return adminCA
     */
    public java.lang.String getAdminCA() {
        return adminCA;
    }


    /**
     * Sets the adminCA value for this ACLEntry.
     * 
     * @param adminCA
     */
    public void setAdminCA(java.lang.String adminCA) {
        this.adminCA = adminCA;
    }


    /**
     * Gets the adminDN value for this ACLEntry.
     * 
     * @return adminDN
     */
    public java.lang.String getAdminDN() {
        return adminDN;
    }


    /**
     * Sets the adminDN value for this ACLEntry.
     * 
     * @param adminDN
     */
    public void setAdminDN(java.lang.String adminDN) {
        this.adminDN = adminDN;
    }


    /**
     * Gets the allow value for this ACLEntry.
     * 
     * @return allow
     */
    public boolean isAllow() {
        return allow;
    }


    /**
     * Sets the allow value for this ACLEntry.
     * 
     * @param allow
     */
    public void setAllow(boolean allow) {
        this.allow = allow;
    }


    /**
     * Gets the operationName value for this ACLEntry.
     * 
     * @return operationName
     */
    public java.lang.String getOperationName() {
        return operationName;
    }


    /**
     * Sets the operationName value for this ACLEntry.
     * 
     * @param operationName
     */
    public void setOperationName(java.lang.String operationName) {
        this.operationName = operationName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ACLEntry)) return false;
        ACLEntry other = (ACLEntry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.adminCA==null && other.getAdminCA()==null) || 
             (this.adminCA!=null &&
              this.adminCA.equals(other.getAdminCA()))) &&
            ((this.adminDN==null && other.getAdminDN()==null) || 
             (this.adminDN!=null &&
              this.adminDN.equals(other.getAdminDN()))) &&
            this.allow == other.isAllow() &&
            ((this.operationName==null && other.getOperationName()==null) || 
             (this.operationName!=null &&
              this.operationName.equals(other.getOperationName())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAdminCA() != null) {
            _hashCode += getAdminCA().hashCode();
        }
        if (getAdminDN() != null) {
            _hashCode += getAdminDN().hashCode();
        }
        _hashCode += (isAllow() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getOperationName() != null) {
            _hashCode += getOperationName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ACLEntry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://glite.org/wsdl/services/org.glite.security.voms", "ACLEntry"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adminCA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "adminCA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adminDN");
        elemField.setXmlName(new javax.xml.namespace.QName("", "adminDN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allow");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allow"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operationName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "operationName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
