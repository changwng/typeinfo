//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.15 at 10:01:22 AM MSD 
//


package com.googlecode.dtools.typeinfo.beans;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.googlecode.dtools.typeinfo.beans package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DmType_QNAME = new QName("", "DmType");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.googlecode.dtools.typeinfo.beans
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DmTypeAttr.FkReferenceList }
     * 
     */
    public DmTypeAttr.FkReferenceList createDmTypeAttrFkReferenceList() {
        return new DmTypeAttr.FkReferenceList();
    }

    /**
     * Create an instance of {@link DmType }
     * 
     */
    public DmType createDmType() {
        return new DmType();
    }

    /**
     * Create an instance of {@link DmType.SubTypeList }
     * 
     */
    public DmType.SubTypeList createDmTypeSubTypeList() {
        return new DmType.SubTypeList();
    }

    /**
     * Create an instance of {@link DmTypeAttr.PossValuesList }
     * 
     */
    public DmTypeAttr.PossValuesList createDmTypeAttrPossValuesList() {
        return new DmTypeAttr.PossValuesList();
    }

    /**
     * Create an instance of {@link DmTypeAttr }
     * 
     */
    public DmTypeAttr createDmTypeAttr() {
        return new DmTypeAttr();
    }

    /**
     * Create an instance of {@link DmType.AttributeList }
     * 
     */
    public DmType.AttributeList createDmTypeAttributeList() {
        return new DmType.AttributeList();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DmType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DmType")
    public JAXBElement<DmType> createDmType(DmType value) {
        return new JAXBElement<DmType>(_DmType_QNAME, DmType.class, null, value);
    }

}
