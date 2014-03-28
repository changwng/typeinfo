package com.googlecode.dtools.typeinfo.work;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.googlecode.dtools.typeinfo.beans.DmType;
import com.googlecode.dtools.typeinfo.dao.DmTypeDAO;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 10.06.11
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
public interface Worker {
    void work();
    DmType processSingle(String dmTypeName, HSSFWorkbook wb);
    void processList(List<String> list);
    FilesWorker getFilesWorker();
    DmTypeDAO getDmTypeDAO();
}
