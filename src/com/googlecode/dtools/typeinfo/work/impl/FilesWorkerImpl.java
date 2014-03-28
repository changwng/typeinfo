package com.googlecode.dtools.typeinfo.work.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.googlecode.dtools.typeinfo.beans.DmType;
import com.googlecode.dtools.typeinfo.beans.DmTypeAttr;
import com.googlecode.dtools.typeinfo.beans.ObjectFactory;
import com.googlecode.dtools.typeinfo.util.ExcelUtil;
import com.googlecode.dtools.typeinfo.work.FilesWorker;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 10.06.11
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public class FilesWorkerImpl implements FilesWorker {
	  private boolean labeldisplay;
	 
	public boolean isLabeldisplay() {
		return labeldisplay;
	}

	public void setLabeldisplay(boolean labeldisplay) {
		this.labeldisplay = labeldisplay;
	}

	public void setFileWithTypeNames(String fileWithTypeNames) {
        this.fileWithTypeNames = fileWithTypeNames;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        folderpathCheck(outputPath); 
         
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

    private String fileWithTypeNames;

    public String getOutputPath() {
        return outputPath;
    }

    private String outputPath;

    public ArrayList<String> readTypeNameList() {

        ArrayList<String> list = new ArrayList<String>();
        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fStream = new FileInputStream(this.fileWithTypeNames);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                System.out.println(strLine);
                if (!strLine.startsWith("#") && !strLine.trim().equals(""))
                    list.add(strLine);
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        return list;
    }

    public void saveXml(DmType dmType) {
        JAXBElement element = new ObjectFactory().createDmType(dmType);
        try {
            // create a JAXBContext capable of handling classes generated into package
            javax.xml.bind.JAXBContext jaxbContext
                    = javax.xml.bind.JAXBContext.newInstance("com.googlecode.dtools.typeinfo.beans");
            folderpathCheck( outputPath + dmType.getName() + ".xml");
            // create a Marshaller and do marshal
            javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // marshaller.setProperty(Marshaller.JAXB_ENCODING, "windows-1251");
           // marshaller.marshal(element, System.out);
            marshaller.marshal(element, new java.io.FileOutputStream(outputPath + dmType.getName() + ".xml"));
        } catch (javax.xml.bind.JAXBException je) {
            je.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
    public String saveDql(DmType dmType) {
    	int startPos = dmType.getStartPos();
        int attrCount = dmType.getAttrCount();
        String dqlfile = outputPath + dmType.getName() + ".dql";
    //	System.out.println("Create DQL " + dmType.getSuperName() + ":"+dmType.getStartPos());
    	StringBuilder sb = new StringBuilder();
    	String superType = dmType.getSuperName();
    	superType = (superType==null)?"":superType;
    	superType = superType.replaceAll(" ", "");
    	String labelText ="";
    	String customtype=dmType.getName();
    	//
    	sb.append("CREATE TYPE  "+dmType.getName()+" ( \r\n");
    	for (DmTypeAttr a : dmType.getAttributeList().getAttribute()){
    		 if (a.getPos() > startPos) { 
	        	//System.out.println(" " + a.getName() + " :"+ a.getPos() );
	            //System.out.println(" " + a.getName() + " :"+ a.getType() + " :"+ a.getLabel()+ " :"+ a.isRepeating());
    			 if(this.isLabeldisplay()){
	    			 labelText =" ( SET label_text='"+a.getLabel()+"', SET category_name='cate_"+customtype+"') ";
	    			 if("".equals(a.getLabel())){
	    				 labelText ="";
	    			 }
    			 }
    			if (a.isRepeating()){
    				sb.append("\t "+a.getName()+"      "+a.getType()+"      REPEATING "+labelText );
    			}else{
    				sb.append("\t "+a.getName()+"      "+a.getType() +" "+labelText);
    			}
	    			if(attrCount !=  a.getPos()){
	    				sb.append(", \r\n");
	    			}else {
	    				sb.append(" \r\n");
	    			}
    			}
        } 
    	if (!"".equals(superType)){
    		sb.append(") WITH SUPERTYPE "+superType+" PUBLISH;  \r\n ");
    	}else{
    		sb.append(") WITH SUPERTYPE  NULL; \r\n ");
    	}
    		
    	//System.out.println(sb.toString()); 
    	try {
			FileUtils.writeStringToFile(new File(dqlfile), sb.toString(), "EUC-KR");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			return sb.toString();
		}
    	 
    	
    }
    public void saveExcelTableInfo(DmType dmType , HSSFWorkbook wb ,  String dqlString ) {
    	int startPos = dmType.getStartPos();
    	 
        int attrCount = dmType.getAttrCount();
        int nTypeAttrCount = (attrCount-startPos)+1;
        
        String dqlfile = outputPath + dmType.getName() + ".dql";
    //	System.out.println("Create DQL " + dmType.getSuperName() + ":"+dmType.getStartPos());
    	//StringBuilder sb = new StringBuilder();
    	String superType = dmType.getSuperName();
    	superType = (superType==null)?"":superType;
    	superType = superType.replaceAll(" ", "");
    	String labelText ="";
    	String customtype=dmType.getName();
    	 
    	 String sheetName = customtype;
         HSSFSheet codeSheetClone  = null;
         String clone_name ="COPYCODE_20";
         int nMaxRow = 31;
         
         if (( 20<nTypeAttrCount )&&(nTypeAttrCount<=40 )) {
        	 clone_name ="COPYCODE_40";
        	 nMaxRow = 51;
         }else if (( 40<nTypeAttrCount )&&(nTypeAttrCount<=60 )) {
        	 clone_name ="COPYCODE_60";
        	 nMaxRow = 71;
         }else if (( 60<nTypeAttrCount )&&(nTypeAttrCount<=80 )) {
        	 clone_name ="COPYCODE_80";
        	 nMaxRow = 91;
         }else if (( 80<nTypeAttrCount )&&(nTypeAttrCount<=90 )) {
        	 clone_name ="COPYCODE_90";
        	 nMaxRow = 101;
         }
         codeSheetClone =  wb.cloneSheet(wb.getSheetIndex(clone_name));
          
         wb.setSheetName(wb.getSheetIndex(codeSheetClone),sheetName);
         String svalue =sheetName;
         ExcelUtil.setCellValue(codeSheetClone, 5,2,svalue);
    	//
         int nStartRow =9;
    	//sb.append("CREATE TYPE  "+dmType.getName()+" ( \r\n");
    	for (DmTypeAttr a : dmType.getAttributeList().getAttribute()){
    		 if (a.getPos() > startPos) { 
    			 nStartRow ++;
	        	 //System.out.println(" " + a.getName() + " :"+ a.getPos() + " :"+ nStartRow);
	             System.out.println(" " + a.getName() + " :"+ a.getType() + " :"+ a.getLabel()+ " :"+ a.isRepeating());
    			 //if(this.isLabeldisplay()){
	    			// labelText =" ( SET label_text='"+a.getLabel()+"', SET category_name='cate_"+customtype+"') ";
	    			 if("".equals(a.getLabel())){
	    				 labelText ="";
	    			 }
    			 //}
	    			 ExcelUtil.setCellValue(codeSheetClone, nStartRow,1, a.getLabel());
	    			 ExcelUtil.setCellValue(codeSheetClone, nStartRow,3, a.getName());
	    			 ExcelUtil.setCellValue(codeSheetClone, nStartRow,4, a.getType());
    			if (a.isRepeating()){
    				//sb.append("\t "+a.getName()+"      "+a.getType()+"      REPEATING "+labelText );
    				ExcelUtil.setCellValue(codeSheetClone, nStartRow,5, "Yes");
    			}else{
    				//sb.append("\t "+a.getName()+"      "+a.getType() +" "+labelText);
    			}
	    			 
    			}
        } 
    	if (!"".equals(superType)){
    		ExcelUtil.setCellValue(codeSheetClone, 7,2,superType);
    		//	sb.append(") WITH SUPERTYPE "+superType+" PUBLISH;  \r\n ");
    	}else{
    		ExcelUtil.setCellValue(codeSheetClone, 7,2,"NULL");
    		//  sb.append(") WITH SUPERTYPE  NULL; \r\n ");
    	}
    	// save Dql String
    	ExcelUtil.setCellValue(codeSheetClone, nMaxRow,2,dqlString);
    	
    }
    public void serialize(List<DmType> dmTypeList) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<DmType> deserialize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void write4Wiki(String fileName, String fileText) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeDDL(String fileText) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
