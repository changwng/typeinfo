package com.googlecode.dtools.typeinfo;


import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import com.googlecode.dtools.typeinfo.util.ExcelUtil;

/**
 * Created by IntelliJ IDEA.
 * User: A_Reshetnikov
 * Date: 07.06.11
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class TestExcel {
    public static void main(String[] args) throws Exception{
    	/*
    	  DmTypeDAO dao = (DmTypeDAO) SpringUtil.getSpringBean("dmTypeDAO");
          List<String> list = dao.getTypeNameList();
          System.out.println("list.size() = " + list.size());
         */
    	/*
        DmTypeDAO dao = (DmTypeDAO) SpringUtil.getSpringBean("dmTypeDAO");
        List<String> list = dao.getTypeNameList();
        System.out.println("list.size() = " + list.size());
        */
        //NPOIFSFileSystem fs = new NPOIFSFileSystem(new File("./workbook2_cw.xls"));
    	NPOIFSFileSystem fs = new NPOIFSFileSystem(new File("./xls/type_template.xls"));
//        HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot());
        HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), false);
        System.out.println("wb.size() = " + wb.getSheetName(0));
        System.out.println("wb.size() = " + wb.getSheet("COPYCODE").getSheetName()); 
        String repoName ="DAECM";
        String workingFilexls="type_"+repoName+".xls";
        FileOutputStream fileOut = new FileOutputStream("./xls/"+workingFilexls);
        wb.write(fileOut);
        fileOut.close();
        fs.close();
        
        
       fs = new NPOIFSFileSystem(new File("./xls/"+workingFilexls)); 
       wb = new HSSFWorkbook(fs.getRoot(), false);
       //System.out.println("wb.size() = " + wb.getSheetName(1));
       //HSSFSheet codeSheet  	=    wb.getSheet("COPYCODE");
       String sheetName ="dm_document";
       HSSFSheet codeSheetClone =    wb.cloneSheet(0);
       wb.setSheetName(wb.getSheetIndex(codeSheetClone),sheetName);
      /* 
       Row row1   = codeSheetClone.getRow(5);
       Cell cell1 = row1.getCell(2);
       cell1.setCellValue("dm_document");
       */
       String svalue ="dm_document2";
       ExcelUtil.setCellValue(codeSheetClone, 5,2,svalue);
       ExcelUtil.displaySheet(codeSheetClone);
      /* int nRow =0;
       int nCol =0;
       for (Row row : codeSheetClone) {
    	   nRow ++;
    	   nCol =0;
           for (Cell cell : row) {
        	   nCol ++;
               CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
               System.out.print(cellRef.formatAsString());
               System.out.print(" - ");
               System.out.print(" row:"+(nRow-1)+" cell:"+(nCol-1));
               System.out.print(" - ");

               switch (cell.getCellType()) {
                   case Cell.CELL_TYPE_STRING:
                       System.out.println(cell.getRichStringCellValue().getString());
                       break;
                   case Cell.CELL_TYPE_NUMERIC:
                       if (DateUtil.isCellDateFormatted(cell)) {
                           System.out.println(cell.getDateCellValue());
                       } else {
                           System.out.println(cell.getNumericCellValue());
                       }
                       break;
                   case Cell.CELL_TYPE_BOOLEAN:
                       System.out.println(cell.getBooleanCellValue());
                       break;
                   case Cell.CELL_TYPE_FORMULA:
                       System.out.println(cell.getCellFormula());
                       break;
                   default:
                       System.out.println();
               }
           }
       }*/
       
      
       /*
        *  int rowStart = Math.min(15, codeSheetClone.getFirstRowNum());
       int rowEnd = Math.max(1400, codeSheetClone.getLastRowNum());

        * for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
          Row r = codeSheetClone.getRow(rowNum);

          int lastColumn =r.getLastCellNum() ;  Math.max(r.getLastCellNum(), 1400);

          for (int cn = 0; cn < lastColumn; cn++) {
             Cell c = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
             if (c == null) {
                // The spreadsheet is empty in this cell
             } else {
                // Do something useful with the cell's contents
             }
          }
       }*/
       FileOutputStream fileOut2 = new FileOutputStream("./xls/"+workingFilexls);
       wb.write(fileOut2);
      
       fs.close();
    }
    








}
