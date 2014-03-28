package com.googlecode.dtools.typeinfo.dao;

import com.documentum.fc.common.DfException;
import com.googlecode.dtools.typeinfo.beans.DmType;
import com.googlecode.dtools.typeinfo.beans.DmTypeAttr;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 07.06.11
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public interface DmTypeDAO {
    DmType getDmTypeInfo(String typeName);

    DmType getDmTypeBasicInfo(String typeName);

  //  void fillLastModifiedDate(DmType dmType);

    //List<DmTypeAttr> getAttrInfo(DmType dmType);

 //   List<DmTypeAttr> getAttrListInfo(String typeName);

//    DmTypeAttr getAttrInfoSingle(String typeName, int pos );

    //DmRelationType getDmRelationType(String relationName);

    List<String> getRelationTypesNamesList();

    Map<String, String> getTypeTagMap(List<String> typesList) throws DfException;


    List<String> getTypeNameList();

    boolean testConnection();

    String getDocbase();



}
