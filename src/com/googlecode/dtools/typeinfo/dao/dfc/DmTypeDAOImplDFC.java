package com.googlecode.dtools.typeinfo.dao.dfc;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 07.06.11
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */


import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.googlecode.dtools.typeinfo.beans.DmType;
import com.googlecode.dtools.typeinfo.beans.DmTypeAttr;
import com.googlecode.dtools.typeinfo.dao.DmTypeDAO;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 19.11.2010
 * Time: 12:35:26
 * To change this template use File | Settings | File Templates.
 */
public class DmTypeDAOImplDFC implements DmTypeDAO {


    private String userName;
    private String userPassword;

    public String getDocbase() {
        return docbase;
    }

    private String docbase;
    private IDfSession session = null;
    private int possibleValuesCount;
    private boolean collectCountInfo;
  
    private String labelNlsKey = "en";


    private IDfSession getSession() {
        IDfSession session = null;
        IDfClientX clientX = new DfClientX();
        IDfSessionManager sMgr = null;
        IDfLoginInfo loginInfo = clientX.getLoginInfo();
        loginInfo.setUser(userName);
        loginInfo.setPassword(userPassword);

        try {
            IDfClient client = clientX.getLocalClient();
            sMgr = client.newSessionManager();
            //bind the Session Manager to the login info
            sMgr.setIdentity(docbase, loginInfo);
            session = sMgr.getSession(docbase);
        } catch (DfException e) {
            e.printStackTrace();
        }
        return session;
    }

    public DmType getDmTypeBasicInfo(String typeName) {

        DmType dmType = new DmType();
        //     DmType dmType = new DmTypeImpl(null);
        //  DmType dmType = DmType.Factory.newInstance();
        try {
            if (session == null)
                session = getSession();
            IDfQuery q = new DfQuery();
            String dqlcolumn = "SELECT name, super_name, attr_count, start_pos from dm_type where name='" + typeName + "'";
           
            q.setDQL(dqlcolumn);
            IDfCollection col = q.execute(session, DfQuery.DF_READ_QUERY);
            while (col.next()) {
                dmType.setName(typeName);
                dmType.setAttrCount(col.getInt("attr_count"));
                dmType.setStartPos(col.getInt("start_pos"));
                dmType.setSuperName(col.getString("super_name"));
            }
            if(col !=null)
            {col.close();}
            // Count
            if (collectCountInfo) {
                String dql = "select count(*) from " + typeName;
                q.setDQL(dql);
                col = q.execute(session, DfQuery.DF_READ_QUERY);
                while (col.next()) {
                    dmType.setCount(col.getLong("count(*)"));
                }
                if(col !=null)
                {col.close();}
            }

            // direct subtypes
            q.setDQL("SELECT name from dm_type where super_name='" + typeName + "'");
            col = q.execute(session, DfQuery.DF_READ_QUERY);
            List<String> list = new ArrayList<String>();
            while (col.next()) {
                list.add(col.getString("name"));
            }
            if(col !=null)
            {col.close();}
            DmType.SubTypeList subTypeList = new DmType.SubTypeList();
            subTypeList.getSubType().addAll(list);
            dmType.setSubTypeList(subTypeList);
            
         // direct subtypes
            // "' and nls_key = '" + labelNlsKey + "'" +
            String dql ="SELECT nls_key, label_text from dmi_dd_type_info where type_name='" + typeName + "'";
            dql +=" and nls_key = '" + labelNlsKey + "'";
           // q.setDQL("SELECT nls_key, lable_text from dmi_dd_type_info where type_name='" + typeName + "'");
            q.setDQL(dql);
            col = q.execute(session, DfQuery.DF_READ_QUERY);
            String type_label=typeName;
            while (col.next()) {
            	type_label = col.getString("label_text");
            }
            if(col !=null)
            {col.close();}
            //System.out.println("type_label:"+type_label);
            dmType.setNote(type_label);
            
        } catch (DfException e) {
            e.printStackTrace();
        }


        return dmType;
    }

    private void fillLastModifiedDate(DmType dmType) {

        if (dmType == null || dmType.getName() == null)
            return;

       // dmType.setNote("");

        try {
            if (session == null)
                session = getSession();
            IDfQuery q = new DfQuery();
            q.setDQL(" select  max(r_modify_date) as v  from " + dmType.getName());
            // System.out.println("q.getDQL() = " + q.getDQL());
            IDfCollection col = q.execute(session, DfQuery.DF_READ_QUERY);
            while (col.next()) {
                dmType.setLastModifiedDate(col.getString("v"));

            }
            if(col !=null)
            {col.close();}
        } catch (DfException e) {
            System.out.println("OOps, " + dmType.getName() + " is not sysobject");
            //  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public DmType getDmTypeInfo(String typeName) {
        DmType dmType = getDmTypeBasicInfo(typeName);

        if (collectCountInfo) fillLastModifiedDate(dmType);

        List<DmTypeAttr> list = getAttrListInfo(typeName);
        DmType.AttributeList attributeList = new DmType.AttributeList();
        attributeList.getAttribute().addAll(list);
        dmType.setAttributeList(attributeList);

        return dmType;
    }

    public List<DmTypeAttr> getAttrListInfo(String typeName) {

        //IDfAttr iDfAttr;
        List<DmTypeAttr> list = new ArrayList<DmTypeAttr>();

        try {
            if (session == null)
                session = getSession();
            IDfQuery q = new DfQuery();
            String s_dql ="SELECT i_position, attr_name, attr_type, attr_repeating, attr_length" +
            " \nFROM dm_type t" +
            " where name='" + typeName + "'" +
            " \n order by i_position desc";
            System.out.println("s_dql:"+s_dql);
            q.setDQL(s_dql);
            IDfCollection col = q.execute(session, DfQuery.DF_READ_QUERY);
            while (col.next()) {
                DmTypeAttr dmTypeAttr = new DmTypeAttr();
                //  DmTypeAttr dmTypeAttr = DmTypeAttr.Factory.newInstance();
                dmTypeAttr.setName(col.getString("attr_name"));
                dmTypeAttr.setPos(-1 * col.getInt("i_position"));
                dmTypeAttr.setTypeCode(col.getInt("attr_type"));
                dmTypeAttr.setLength(col.getInt("attr_length"));
                dmTypeAttr.setRepeating(col.getBoolean("attr_repeating"));
                list.add(dmTypeAttr);

            }
            if(col!=null)
            {
            	col.close();
            	System.out.println("========typeName:"+typeName);
            }
            
            //  
            for (DmTypeAttr attr : list) {
                //  if (attr.getName().startsWith("ka"))
                fillAdditionalInfo(typeName, attr);
            }

        } catch (DfException e) {
            e.printStackTrace();
        }


        return list;
    }
/*
    public DmRelationType getDmRelationType(String relationName) {

        DmRelationType r = new DmRelationType();
        IDfQuery q = new DfQuery();
        q.setDQL("SELECT t.parent_type, t.child_type," +
                " t.description, t.integrity_kind, " +
                "t.direction_kind, t.permanent_link, t.copy_child, t.security_type" +
                " from dm_relation_type t where t.relation_name='" + relationName + "'");

        if (session == null)
            session = getSession();
        try {
            IDfCollection col = q.execute(session, DfQuery.DF_READ_QUERY);
            while (col.next()) {
                r.setName(relationName);
                r.setChildType(col.getString("child_type"));
                r.setParentType(col.getString("parent_type"));
                r.setDescription(col.getString("description"));
                r.setDirection(col.getInt("direction_kind"));
                r.setIntegrity(col.getInt("integrity_kind"));
                r.setPermanentLink(col.getBoolean("permanent_link"));
                r.setSecurityType(col.getString("security_type"));
            }


            q.setDQL("select count(*) as cnt from dm_relation where relation_name='" + relationName + "'");
            col = q.execute(session, DfQuery.DF_READ_QUERY);
            while (col.next()) {
                r.setCount(col.getLong("cnt"));
            }

            fillAdditionalInfoAboutParentsAndChildren(r, true);
            fillAdditionalInfoAboutParentsAndChildren(r, false);
            fillAttributePossibleValues(r, "description");
            fillAttributePossibleValues(r, "effective_date");
            fillAttributePossibleValues(r, "expiration_date");
            fillAttributePossibleValues(r, "order_no");
            fillAttributeCount(r, "description");
            fillAttributeCount(r, "effective_date");
            fillAttributeCount(r, "expiration_date");
            fillAttributeCount(r, "order_no");


        } catch (DfException e) {
            e.printStackTrace();
        }


        return r;

    }    */
    /*
  private void fillAttributePossibleValues(DmRelationType r, String attrName) throws DfException {
      if (session == null)
          session = getSession();

      List<String> list = new ArrayList<String>();
      String dql =
              "select distinct " + attrName + "  from dm_relation where relation_name='" + r.getName() + "'  ENABLE (RETURN_TOP" + possibleValuesCount + " )";
      IDfCollection col;
      IDfQuery q = new DfQuery();
      q.setDQL(dql);
      col = q.execute(session, DfQuery.DF_READ_QUERY);
      while (col.next()) {
          list.add(col.getString(attrName));
      }
      if (attrName.equals("description"))
          r.setDescriptionList(list);
      if (attrName.equals("effective_date"))
          r.setEffectiveDateList(list);
      if (attrName.equals("expiration_date"))
          r.setExpirationDateList(list);
      if (attrName.equals("order_no"))
          r.setOrderNoList(list);

  }  */

/*

    private void fillAttributeCount(DmRelationType r, String attrName) throws DfException {
        if (session == null)
            session = getSession();

        Long cnt = 0L;
        String dql =
                "select count(distinct " + attrName + ") as cnt  from dm_relation where relation_name='" + r.getName() + "'  ENABLE (RETURN_TOP" + possibleValuesCount + " )";
        IDfCollection col;
        IDfQuery q = new DfQuery();
        q.setDQL(dql);
        col = q.execute(session, DfQuery.DF_READ_QUERY);
        while (col.next()) {
            cnt = col.getLong("cnt");
        }
        if (attrName.equals("description"))
            r.setDescriptionCount(cnt);
        if (attrName.equals("effective_date"))
            r.setEffectiveDateCount(cnt);
        if (attrName.equals("expiration_date"))
            r.setExpirationDateCount(cnt);
        if (attrName.equals("order_no"))
            r.setOrderNoCount(cnt);

    }

*/
/*

    private void fillAdditionalInfoAboutParentsAndChildren(DmRelationType dmRelationType, boolean parent) throws DfException {


        if (session == null)
            session = getSession();

        List<String> list = new ArrayList<String>();

        String dql =
                "select distinct  r_object_type \n" +
                        "from dm_sysobject \n" +
                        "where r_object_id in (\n" +
                        "SELECT " + (parent ? "parent_id" : "child_id") + " \n" +
                        "from dm_relation r where r.relation_name = '" +
                        dmRelationType.getName()
                        + "' \n"
                        // + "where "+ attr.getName()+" \n" +
                        //  "is not null"
                        + ")";
        IDfCollection col;
        IDfQuery q = new DfQuery();
        q.setDQL(dql);
        col = q.execute(session, DfQuery.DF_READ_QUERY);
        while (col.next()) {
            list.add(col.getString("r_object_type"));
        }
        if (parent)
            dmRelationType.setParents(list);
        else
            dmRelationType.setChildren(list);

    }
*/

    public List<String> getRelationTypesNamesList() {
        List<String> list = new ArrayList<String>();
        IDfQuery q = new DfQuery();
        q.setDQL("select relation_name from dm_relation_type where relation_name like 'k%'");
        if (session == null)
            session = getSession();

        try {


            IDfCollection col = q.execute(session, DfQuery.DF_READ_QUERY);
            while (col.next()) {
                list.add(col.getString("relation_name"));
            }
            if(col !=null)
            {col.close();}

        } catch (DfException e) {
            e.printStackTrace();
        }

        return list;

    }

    public Map<String, String> getTypeTagMap(List<String> typesList) throws DfException {
        if (session == null)
            session = getSession();
        String tag = null;

        Map<String, String> map = new HashMap<String, String>();


        for (String typeName : typesList) {
        		
        	if(typeName.startsWith("dmi_0")) { continue;};
            String dql = "select distinct substr(r_object_id,0,2) as tag_ from " + typeName;
            IDfQuery q = new DfQuery();
            q.setDQL(dql);
            IDfCollection col = q.execute(session, DfQuery.DF_READ_QUERY);
            if (col.next()) {
                tag = col.getString("tag_");
            }
            if( col !=null){col.close();}
           /* 두개이상의 테그가 있으면 에러 발생
            * while (col.next()) {
                System.err.println("more then 2 tag of object type!" + typeName);
            }*/
            //  if (!tag.equals("00"))
            map.put(typeName, tag);

        }
        return map;
    }

    public List<String> getTypeNameList() {
        List<String> list = new ArrayList<String>();
        IDfQuery q = new DfQuery();
        q.setDQL("select name from dm_type order by name asc");
        if (session == null)
            session = getSession();
        try {
            IDfCollection col = q.execute(session, DfQuery.DF_READ_QUERY);
            while (col.next()) {
                list.add(col.getString("name"));
            }
            if(col !=null)
            {col.close();}

        } catch (DfException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean testConnection() {
        if (session == null)
            session = getSession();
        IDfQuery q = new DfQuery();
        q.setDQL("select 1 from dm_location");
        try {
            q.execute(session, DfQuery.READ_QUERY);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private void fillAdditionalInfo(String typeName, DmTypeAttr attr) throws DfException {

        if (session == null)
            session = getSession();
        if (attr.getName().equals("user_password")) return;
        if (attr.getName().equals("i_contents")) return;
        if (attr.getName().equals("i_other_contents")) return;
        
        
        
        IDfQuery q = new DfQuery();
        IDfCollection col=null;
        if (collectCountInfo) {
        	try{
            q.setDQL("SELECT count(distinct " + attr.getName() + ") as cnt from " + typeName);
            col = q.execute(session, DfQuery.DF_READ_QUERY);
            while (col.next()) {
                attr.setDistinctCount(col.getLong("cnt"));

            }
        	}catch(Exception ex){
        		attr.setDistinctCount(1L);
        	}
        	finally{
	            if(col !=null)
	            {col.close();}
        	}
        }
       
        // PossValuesList
        if (possibleValuesCount > 0) {
            if (collectCountInfo && attr.getDistinctCount() > 0) {
                fillPossibleValuesList(typeName, attr);
                fillFKReference(typeName, attr);
            }
        }

        //Label
        String s_dql ="SELECT label_text " +
        "FROM dmi_dd_attr_info " +
        "WHERE type_name='" + typeName +
        "' and nls_key = '" + labelNlsKey + "'" +
        "and attr_name='" + attr.getName() + "'";
        //System.out.println("s_dql:"+s_dql);
        q.setDQL(s_dql);
        col = q.execute(session, DfQuery.DF_READ_QUERY);
        while (col.next()) {
            attr.setLabel(col.getString("label_text"));
        }
        if(col !=null)
        {col.close();}

    }


    //todo

    private void fillFKReference(String typeName, DmTypeAttr attr) throws DfException {
        // Description
        if (attr.getTypeCode() == 3 ||
                (attr.getTypeCode() == 2 && attr.getLength() == 32)) {


            List<String> list = getFKasSysobjectChilds(typeName, attr);
            if (list.size() == 0)
                list = getFKbyTagRecognition(typeName, attr);

            if (list != null) {
                DmTypeAttr.FkReferenceList fkList = new DmTypeAttr.FkReferenceList();
                fkList.getFkReference().addAll(list);
                attr.setFkReferenceList(fkList);
            }

        }

    }

    private List<String> getFKbyTagRecognition(String typeName, DmTypeAttr attr) throws DfException {
        if (session == null)
            session = getSession();

        if (attr.getName().equals("ka_oms_id")) return null;

        List<String> tagList = new ArrayList<String>();
        List<String> list = new ArrayList<String>();
        String dql =

                "SELECT distinct substr(" + attr.getName() + ",0,2)" +
                        " as tag_ \n" +
                        "from " + typeName + " \n";
        IDfCollection col;
        IDfQuery q = new DfQuery();
        q.setDQL(dql);
        col = q.execute(session, DfQuery.DF_READ_QUERY);
        while (col.next()) {
            tagList.add(col.getString("tag_"));
        }
        if(col !=null)
        {col.close();}
        for (String tag : tagList) {
            if (tag != null && !tag.equals("00"))
                list.addAll(DfcMap.getTypesNamesByTag(tag));
        }
        return list;
    }

    private List<String> getFKasSysobjectChilds(String typeName, DmTypeAttr attr) throws DfException {
        if (session == null)
            session = getSession();

        List<String> list = new ArrayList<String>();

        String dql =
                "select distinct r_object_type \n" +
                        "from dm_sysobject \n" +
                        "where r_object_id in (\n" +
                        "SELECT " + attr.getName() + " \n" +
                        "from " + typeName + " \n"
                        // + "where "+ attr.getName()+" \n" +
                        //  "is not null"
                        + ")";
        IDfCollection col;
        IDfQuery q = new DfQuery();
        q.setDQL(dql);
        col = q.execute(session, DfQuery.DF_READ_QUERY);
        while (col.next()) {
            list.add(col.getString("r_object_type"));
        }
        if(col !=null)
        {col.close();}
        return list;
    }


    private void fillPossibleValuesList(String typeName, DmTypeAttr attr) throws DfException {
        if (session == null)
            session = getSession();
        IDfQuery q = new DfQuery();
        IDfCollection col;
        q.setDQL("SELECT distinct " + attr.getName()
                + " from " + typeName
                + " ENABLE (RETURN_TOP " + possibleValuesCount + " )");
        col = q.execute(session, DfQuery.DF_READ_QUERY);
        List<String> list = new ArrayList<String>();
        while (col.next()) {
            list.add(col.getString(attr.getName()));
        }
        if(col !=null)
        {col.close();}
        DmTypeAttr.PossValuesList possValuesList = new DmTypeAttr.PossValuesList();
        possValuesList.getPossValue().addAll(list);
        attr.setPossValuesList(possValuesList);
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

    public void setPossibleValuesCount(int possibleValuesCount) {
        this.possibleValuesCount = possibleValuesCount;
    }

    public void setCollectCountInfo(boolean collectCountInfo) {
        this.collectCountInfo = collectCountInfo;
    }

    public void setLabelNlsKey(String labelNlsKey) {
        this.labelNlsKey = labelNlsKey;
    }

	
    public List<Map<String, String>> getTypeTagList(List<String> typesList) throws DfException {
        if (session == null)
            session = getSession();
        String tag = null;

        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, String>> lst = new ArrayList<Map<String, String>>();

        for (String typeName : typesList) {
        	map = new HashMap<String, String>();
        	if(typeName.startsWith("dmi_0")) { continue;};
            String dql = "select distinct substr(r_object_id,0,2) as tag_ from " + typeName;
            IDfQuery q = new DfQuery();
            q.setDQL(dql);
            IDfCollection col = q.execute(session, DfQuery.DF_READ_QUERY);
            if (col.next()) {
                tag = col.getString("tag_");
            }
            if( col !=null){col.close();}
           /* 두개이상의 테그가 있으면 에러 발생
            * while (col.next()) {
                System.err.println("more then 2 tag of object type!" + typeName);
            }*/
            //  if (!tag.equals("00"))
            map.put(typeName, tag);
            lst.add(map);
        }
        return lst;
    }
}
