package com.googlecode.dtools.typeinfo;


import com.googlecode.dtools.typeinfo.dao.DmTypeDAO;
import com.googlecode.dtools.typeinfo.util.SpringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 07.06.11
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) throws Exception{
        DmTypeDAO dao = (DmTypeDAO) SpringUtil.getSpringBean("dmTypeDAO");
        List<String> list = dao.getTypeNameList();
        System.out.println("list.size() = " + list.size());
//        TransformBean magicUtil = (TransformBean) SpringUtil.getSpringBean("magicUtil");
//    //   List l = dao.getRelationTypesNamesList();
//    //   System.out.println("l.size() = " + l.size());
//       DmType dmType = dao.getDmTypeInfo("kc_incoming_doc");
// //       DmType dmType = dao.getDmTypeInfo("kc_reserve");
//       // DmType dmType = null;
//        magicUtil.fillDomainOfAttributes(dmType);
//        magicUtil.fillCode(dmType);
//        System.out.println("dmType = " + dmType);
//
//
//
//        Writer writer = new OutputStreamWriter(System.out);
//        //FileWriter writer = new FileWriter("test.xml");
//        //System.out.
//
//     //   dmType.marshal(writer);try {
//
//    //    dmType.save(new java.io.File("test.xml"));
//
////        DmType testType = new DmType();
////        testType.setName("test");
//
//        JAXBElement element = new ObjectFactory().createDmType(dmType);
//
//        try {
//            // create a JAXBContext capable of handling classes generated into package
//            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("com.googlecode.dtools.typeinfo.beans");
//workbook2_cw.xls
//            // create a Marshaller and do marshal
//            javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
//
//            //marshaller.marshal(new JAXBElement(testType), MessageType.class, messageType );
//
//           marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//          // marshaller.setProperty(Marshaller.JAXB_ENCODING, "windows-1251");
//        marshaller.marshal(element, System.out);
//            marshaller.marshal(element, new java.io.FileOutputStream("text2.xml"));
//        } catch (javax.xml.bind.JAXBException je) {
//            je.printStackTrace();
//        }
////        } catch (java.io.FileNotFoundException io) {
////            io.printStackTrace();
////        }
//
////
////        try {
////            // create a JAXBContext capable of handling classes generated into package
////            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("com.googlecode.dtools.typeinfo.beans");
////            // create an Unmarshaller
////            javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
////            // unmarshal an instance document into a tree of Java content
////            // objects composed of classes from the package.
////            JAXBElement<DmType> unmarshalledObject = (JAXBElement<DmType>) unmarshaller.unmarshal(new java.io.FileInputStream("text2.xml"));
////            System.out.println("unmarshalledObject.getName() = " + unmarshalledObject.getName());
////            DmType dmType2 = unmarshalledObject.getValue();
////            System.out.println("dmType2.getName() = " + dmType2.getName());
////
////
////        } catch (javax.xml.bind.JAXBException je) {
////            je.printStackTrace();
////        } catch (java.io.IOException ioe) {
////            ioe.printStackTrace();
////        }

    }








}
