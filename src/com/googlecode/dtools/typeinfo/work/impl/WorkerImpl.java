package com.googlecode.dtools.typeinfo.work.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.ArrayUtil;

import com.googlecode.dtools.typeinfo.beans.DmType;
import com.googlecode.dtools.typeinfo.dao.DmTypeDAO;
import com.googlecode.dtools.typeinfo.util.ExcelUtil;
import com.googlecode.dtools.typeinfo.work.FilesWorker;
import com.googlecode.dtools.typeinfo.work.ListWorker;
import com.googlecode.dtools.typeinfo.work.TransformBean;
import com.googlecode.dtools.typeinfo.work.Worker;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 10.06.11
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class WorkerImpl implements Worker, createExcelWorkbook{

	  private String exceltemplate;
	  
    public String isExceltemplate() {
		return exceltemplate;
	}

	public void setExceltemplate(String exceltemplate) {
		this.exceltemplate = exceltemplate;
	}
    public FilesWorker getFilesWorker() {
        return filesWorker;
    }

    private FilesWorker filesWorker;

    public DmTypeDAO getDmTypeDAO() {
        return dmTypeDAO;
    }

    public DmTypeDAO getDmTypeCreateDAO() {
		return dmTypeCreateDAO;
	}

	public void setDmTypeCreateDAO(DmTypeDAO dmTypeCreateDAO) {
		this.dmTypeCreateDAO = dmTypeCreateDAO;
	}

	private DmTypeDAO dmTypeDAO;
    private DmTypeDAO dmTypeCreateDAO;
     private ListWorker listWorker;

    public void setTransformBean(TransformBean transformBean) {
        this.transformBean = transformBean;
    }

    private TransformBean transformBean;


    public void work() { 
        List<String> list = listWorker.getListForProcessing();
        System.out.println("list.size() = " + list.size());
        processList(list);

    }
    private void outputPathCheck()
    {
    	  String wokingfilepath = this.filesWorker.getOutputPath();
	       folderpathCheck(wokingfilepath);
    }
    public DmType processSingle(String dmTypeName, HSSFWorkbook wb) {
        DmType dmType = dmTypeDAO.getDmTypeInfo(dmTypeName);
        transformBean.transform(dmType);
        filesWorker.saveXml(dmType);
        System.out.println("Saved XML " + dmType.getName() + ".xml");
        String dqlString =  filesWorker.saveDql(dmType); 
        System.out.println("Saved DQL " + dmType.getName() + ".dql"); 
        
        filesWorker.saveExcelTableInfo(dmType, wb, dqlString); 
        System.out.println("Saved Excel " + dmType.getName() + ".xls"); 
        
     
        //Create domain for dql create type
        //DQL 생성
      //  processDqlCreate(dmType);
        return dmType;
    }

	
	// Excel 템플릿 작업 진행
    public void processList(List<String> list) {
    	 System.out.println("processList  start===");
    	 outputPathCheck();
    	// 
    	 HSSFWorkbook wb= createExcelWorkBook(); 
    	 String sheetName ="";
    	 DmType dmType = null;
    	 int nRowStart=2;
    	 int i =0;
        for (String s: list){
         //   if (typeListFromDocbase && !s.startsWith("dm"))    //todo exclude list
        	/*if(s.startsWith("dm_sysobject") )
        	{*/
        		dmType =  processSingle(s , wb);
	            
	         // Type List link document Start
        		nRowStart ++;
        		i++;
        		setTypeListSheet(wb, dmType, nRowStart, i, s);
	            // Type List link document End
	            
	            closeWorkbook(wb);
        	//}
           // break;
        }
        System.out.println("processList  end===");
        //closeWorkbook(wb);
    }
 // Type List link document
	private void setTypeListSheet(HSSFWorkbook wb, DmType dmType,
			int nRowStart, int i, String s) {
		String sheetName;
		sheetName = s;
		HSSFSheet typeSheet =    wb.getSheet("TYPELIST"); //(wb.getSheetIndex("TYPELIST"));
		Row row  		    = typeSheet.getRow(nRowStart);
		if(null == row ){
			row = typeSheet.createRow((short)nRowStart);
			row.createCell(0).setCellValue("");
			row.createCell(1).setCellValue("");
			row.createCell(2).setCellValue("");
			row.createCell(3).setCellValue("");
			row.createCell(4).setCellValue("");
		}
		ExcelUtil.setCellValue(typeSheet, nRowStart,0,String.valueOf(i));
		Cell cell= ExcelUtil.setCellValue(typeSheet, nRowStart,2,sheetName);
		HSSFHyperlink paramHyperlink = new HSSFHyperlink(HSSFHyperlink.LINK_DOCUMENT);
		paramHyperlink.setAddress("'"+sheetName+"'!A1");
		cell.setHyperlink(paramHyperlink);
		
		ExcelUtil.setCellValue(typeSheet, nRowStart,1,dmType.getSuperName());
		//System.out.println("dmType.getNote():"+dmType.getNote());
		ExcelUtil.setCellValue(typeSheet, nRowStart,3,dmType.getNote());
		if( null != dmType.getSubTypeList()){
			ExcelUtil.setCellValue(typeSheet, nRowStart,4,this.getStringByList((List<String>)dmType.getSubTypeList().getSubType()) , true);
		}
	}
	private String getStringByList(List<String> list)
	{
		String retVal="";
		int nSize = list.size();
		int i=0;
		for (String subType : list ){
			i++;
			if (i < nSize){
				retVal += subType+"\n";
			}else{
				retVal += subType;
			}
				
		}
		return retVal; 
	}
    private void closeWorkbook(HSSFWorkbook wb)  {
    	try {
    		
    		 String repoName = this.dmTypeDAO.getDocbase(); //"DAECM";
	         String workingFilexls="type_"+repoName+".xls";
	         String wokingfilepath = this.filesWorker.getOutputPath();
	         System.out.println("Save Xls Table Info===:"+wokingfilepath+workingFilexls);
	         FileOutputStream fileOut = new FileOutputStream(wokingfilepath+workingFilexls); 
	         wb.write(fileOut);
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
	private HSSFWorkbook createExcelWorkBook()  {
		 NPOIFSFileSystem fs=null;
		 HSSFWorkbook wb=null ;
		try {
			fs = new NPOIFSFileSystem(new File(this.exceltemplate));
		         wb = new HSSFWorkbook(fs.getRoot(), false);
		         String repoName = this.dmTypeDAO.getDocbase(); //"DAECM";
		         String workingFilexls="type_"+repoName+".xls";
		         String wokingfilepath = this.filesWorker.getOutputPath();
		         folderpathCheck(wokingfilepath+workingFilexls);
		         FileOutputStream fileOut = new FileOutputStream(wokingfilepath+workingFilexls);
		         wb.write(fileOut);
		         fileOut.close();
		         fs.close();
		         fs = new NPOIFSFileSystem(new File(wokingfilepath+workingFilexls)); 
		         wb = new HSSFWorkbook(fs.getRoot(), false); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			 return wb;
		}
	}
	private void folderpathCheck(String outputPath) {
		File file = new File(outputPath);
		if (file.isDirectory()) {
		     file.mkdirs();
		} else {
		    File dir = file.getParentFile();
		    // do not check if this worked
			// , as it may also return false, when all neccessary dirs are present
		    dir.mkdirs();
		}
	}
    public void setOutputPath(String path) {
       //todo
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setFilesWorker(FilesWorker filesWorker) {
        this.filesWorker = filesWorker;
    }

    public void setDmTypeDAO(DmTypeDAO dmTypeDAO) {
        this.dmTypeDAO = dmTypeDAO;
    }



    public void setListWorker(ListWorker listWorker) {
        this.listWorker = listWorker;
    }
}
