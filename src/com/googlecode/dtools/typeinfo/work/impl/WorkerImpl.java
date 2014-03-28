package com.googlecode.dtools.typeinfo.work.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;

import com.googlecode.dtools.typeinfo.beans.DmType;
import com.googlecode.dtools.typeinfo.beans.DmTypeAttr;
import com.googlecode.dtools.typeinfo.dao.DmTypeDAO;
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
        System.out.println("Saved Excel " + dmType.getName() + ".dql"); 
        //Create domain for dql create type
        //DQL 생성
      //  processDqlCreate(dmType);
        return dmType;
    }

	private void processDqlCreate(DmType dmType) {
		System.out.println("Create DQL " + dmType.getName() + ".dql");
        int startPos = dmType.getStartPos();
        int attrCount = dmType.getAttrCount();
        String dqlfile =  dmType.getName() + ".dql";
    //	System.out.println("Create DQL " + dmType.getSuperName() + ":"+dmType.getStartPos());
    	StringBuilder sb = new StringBuilder();
    	sb.append("CREATE TYPE  "+dmType.getName()+" ( \r\n");
    	for (DmTypeAttr a : dmType.getAttributeList().getAttribute()){
    		 if (a.getPos() > startPos) { 
	        	//System.out.println(" " + a.getName() + " :"+ a.getPos() );
	            //System.out.println(" " + a.getName() + " :"+ a.getType() + " :"+ a.getLabel()+ " :"+ a.isRepeating());
    			if (a.isRepeating()){
    				sb.append("\t "+a.getName()+"      "+a.getType()+"      REPEATING " );
    			}else{
    				sb.append("\t "+a.getName()+"      "+a.getType() +"");
    			}
	    			if(attrCount !=  a.getPos()){
	    				sb.append(", \r\n");
	    			}else {
	    				sb.append(" \r\n");
	    			}
    			}
        } 
    	if("".equals(dmType.getSuperName())){
    		sb.append(") WITH SUPERTYPE NULL PUBLISH;  \r\n ");
    	}else{
    		sb.append(") WITH SUPERTYPE "+dmType.getSuperName()+" PUBLISH;  \r\n ");
    	}
    	System.out.println(sb.toString());
    	//FileUtils.writeStringToFile(file, sb.toString(), "UTF-8");
    	 
	}
	// Excel 템플릿 작업 진행
    public void processList(List<String> list) {
    	 System.out.println("processList  start===");
    	 outputPathCheck();
    	// 
    	 HSSFWorkbook wb= createExcelWorkBook(); 
        for (String s: list){
         //   if (typeListFromDocbase && !s.startsWith("dm"))    //todo exclude list
        	/*if(s.startsWith("dm_sysobject") )
        	{*/
	            processSingle(s , wb);
	            closeWorkbook(wb);
        	//}
           // break;
        }
        System.out.println("processList  end===");
        //closeWorkbook(wb);
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
