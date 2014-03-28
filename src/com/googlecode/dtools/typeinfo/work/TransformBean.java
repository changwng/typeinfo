package com.googlecode.dtools.typeinfo.work;

import com.googlecode.dtools.typeinfo.beans.DmType;
import com.googlecode.dtools.typeinfo.beans.DmTypeAttr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * This class named magic util,
 * because it converts documentum magic constants to human readable form
 * User: A_Reshetnikov
 */
public class TransformBean {

    public void transform(DmType dmType){
        fillCode(dmType);
        fillDomainOfAttributes(dmType);
        if (fillDateOfCollectInfo)
        fillDateTimeOfCollectInfo(dmType);
    }

    public void setMapCodes(Map<Integer, String> mapCodes) {
        this.mapCodes = mapCodes;
    }

    private Map<Integer,String> mapCodes;
    private boolean cleanRedundant;
    private boolean fillDateOfCollectInfo;

    public void setFillDateOfCollectInfo(boolean fillDateOfCollectInfo) {
        this.fillDateOfCollectInfo = fillDateOfCollectInfo;
    }

    public void setCleanRedundant(boolean cleanRedundant) {
        this.cleanRedundant = cleanRedundant;
    }


     public void fillCode(DmType dmType){
        for (DmTypeAttr a : dmType.getAttributeList().getAttribute()){
            fillCode(a);
        }
    }

    private void fillCode(DmTypeAttr attr){
       String res;
       if (attr.getTypeCode()==2){
    	  res = mapCodes.get(attr.getTypeCode())+ "("+attr.getLength()+")";
        //  res = "string("+attr.getLength()+")";
       }
       else {
           res = mapCodes.get(attr.getTypeCode());
       }
       attr.setType(res);
        if (cleanRedundant){
            attr.setTypeCode(null);
            attr.setLength(null);
        }

    }

    public static void fillDomainOfAttributes(DmType dmType){
        for (DmTypeAttr a : dmType.getAttributeList().getAttribute()){
            fillDomain(a, dmType.getStartPos());
        }
    }

    private static void fillDomain(DmTypeAttr dmTypeAttr, int startPos) {
        if (dmTypeAttr.getName() == null || dmTypeAttr.getPos() == 0 ) {
            System.err.println("cannot fill domain for " + dmTypeAttr.getName());

            return;
        }

        if (dmTypeAttr.getName().startsWith("r_")) {
            dmTypeAttr.setDomain("System");
        } else if (dmTypeAttr.getName().startsWith("a_")) {
            dmTypeAttr.setDomain("Application");
        } else if (dmTypeAttr.getName().startsWith("i_")) {
            dmTypeAttr.setDomain("Internal");
        } else if (dmTypeAttr.getPos() > startPos) {
            dmTypeAttr.setDomain("Custom");
        } else {
            dmTypeAttr.setDomain("General");
        }
    }      

    public static void fillDateTimeOfCollectInfo(DmType dmType){
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String s = formatter.format(date);
        dmType.setDateOfCollectInfo(s);
    }
}
