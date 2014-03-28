package com.googlecode.dtools.typeinfo.work.impl;

import com.googlecode.dtools.typeinfo.dao.DmTypeDAO;
import com.googlecode.dtools.typeinfo.work.ListWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 14.06.11
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class ListWorkerImpl implements ListWorker{

    public boolean isListFromDocbase() {
        return listFromDocbase;
    }

    public void setListFromDocbase(boolean listFromDocbase) {
        this.listFromDocbase = listFromDocbase;
    }

    public String getExcludePrefix() {
        return excludePrefix;
    }

    public void setExcludePrefix(String excludePrefix) {
        this.excludePrefix = excludePrefix;
    }

    private boolean listFromDocbase;
    private String excludePrefix;
    private DmTypeDAO dao;

    public void setDao(DmTypeDAO dao) {
        this.dao = dao;
    }

    public List<String> getListForProcessing() {
       List<String> list = null;
       System.out.println("ListWorkerImpl.getListForProcessing():");
        if (listFromDocbase){
            list = dao.getTypeNameList();
            List<String> filteredList = new ArrayList<String>();
            for (String s : list){
                if (!s.startsWith(excludePrefix)){
                    filteredList.add(s);
                }
            }
            return filteredList;
        }
        System.err.println("not implemented");
       return null;
    }
}
