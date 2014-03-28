package com.googlecode.dtools.typeinfo.work;

import com.googlecode.dtools.typeinfo.beans.DmType;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 10.06.11
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public interface FilesWorker {

    void saveXml(DmType dmType);
    void setOutputPath(String outputPath);
    public String getOutputPath();
    public String saveDql(DmType dmType);
    public void saveExcelTableInfo(DmType dmType ,HSSFWorkbook wb,  String dqlString );
    
}
