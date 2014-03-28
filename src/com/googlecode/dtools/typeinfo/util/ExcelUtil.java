package com.googlecode.dtools.typeinfo.util;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
public class ExcelUtil {
	 public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet){  
	        copySheets(newSheet, sheet, true);  
	    }  
	    public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet, boolean copyStyle){  
	        int maxColumnNum = 0;  
	        Map<Integer, HSSFCellStyle> styleMap = (copyStyle)  
	                ? new HashMap<Integer, HSSFCellStyle>() : null;  
	  
	        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {  
	            HSSFRow srcRow = sheet.getRow(i);  
	            HSSFRow destRow = newSheet.createRow(i);  
	            if (srcRow != null) {  
	            	ExcelUtil.copyRow(sheet, newSheet, srcRow, destRow, styleMap);  
	                if (srcRow.getLastCellNum() > maxColumnNum) {  
	                    maxColumnNum = srcRow.getLastCellNum();  
	                }  
	            }  
	        }  
	        for (int i = 0; i <= maxColumnNum; i++) {  
	            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));  
	        }  
	    }  
	  
	    public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet, HSSFRow srcRow, HSSFRow destRow, Map<Integer, HSSFCellStyle> styleMap) {  
	        Set mergedRegions = new TreeSet();  
	        destRow.setHeight(srcRow.getHeight());  
	        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {  
	            HSSFCell oldCell = srcRow.getCell(j);  
	            HSSFCell newCell = destRow.getCell(j);  
	            if (oldCell != null) {  
	                if (newCell == null) {  
	                    newCell = destRow.createCell(j);  
	                }  
	                copyCell(oldCell, newCell, styleMap);  
	                Region mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), oldCell.getCellNum());  
	                if (mergedRegion != null) {  
//	                    Region newMergedRegion = new Region( destRow.getRowNum(), mergedRegion.getColumnFrom(),  
//	                            destRow.getRowNum() + mergedRegion.getRowTo() - mergedRegion.getRowFrom(), mergedRegion.getColumnTo() );  
	                    Region newMergedRegion = new Region(mergedRegion.getRowFrom(), mergedRegion.getColumnFrom(),  
	                            mergedRegion.getRowTo(), mergedRegion.getColumnTo());  
	                    if (isNewMergedRegion(newMergedRegion, mergedRegions)) {  
	                        mergedRegions.add(newMergedRegion);  
	                        destSheet.addMergedRegion(newMergedRegion);  
	                    }  
	                }  
	            }  
	        }  
	          
	    }  
	    public static void copyCell(HSSFCell oldCell, HSSFCell newCell, Map<Integer, HSSFCellStyle> styleMap) {  
	        if(styleMap != null) {  
	            if(oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()){  
	                newCell.setCellStyle(oldCell.getCellStyle());  
	            } else{  
	                int stHashCode = oldCell.getCellStyle().hashCode();  
	                HSSFCellStyle newCellStyle = styleMap.get(stHashCode);  
	                if(newCellStyle == null){  
	                    newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();  
	                    newCellStyle.cloneStyleFrom(oldCell.getCellStyle());  
	                    styleMap.put(stHashCode, newCellStyle);  
	                }  
	                newCell.setCellStyle(newCellStyle);  
	            }  
	        }  
	        switch(oldCell.getCellType()) {  
	            case HSSFCell.CELL_TYPE_STRING:  
	                newCell.setCellValue(oldCell.getStringCellValue());  
	                break;  
	            case HSSFCell.CELL_TYPE_NUMERIC:  
	                newCell.setCellValue(oldCell.getNumericCellValue());  
	                break;  
	            case HSSFCell.CELL_TYPE_BLANK:  
	                newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);  
	                break;  
	            case HSSFCell.CELL_TYPE_BOOLEAN:  
	                newCell.setCellValue(oldCell.getBooleanCellValue());  
	                break;  
	            case HSSFCell.CELL_TYPE_ERROR:  
	                newCell.setCellErrorValue(oldCell.getErrorCellValue());  
	                break;  
	            case HSSFCell.CELL_TYPE_FORMULA:  
	                newCell.setCellFormula(oldCell.getCellFormula());  
	                break;  
	            default:  
	                break;  
	        }  
	          
	    }  
	    public static Region getMergedRegion(HSSFSheet sheet, int rowNum, short cellNum) {  
	        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {  
	            Region merged = sheet.getMergedRegionAt(i);  
	            if (merged.contains(rowNum, cellNum)) {  
	                return merged;  
	            }  
	        }  
	        return null;  
	    }  
	  
	    private static boolean isNewMergedRegion(Region region, Collection mergedRegions) {  
	        return !mergedRegions.contains(region);  
	    }  
	    
	    public static void setCellValue(HSSFSheet sheet, int nRow, int nCol, String value)
	    {
	    	  Row row   = sheet.getRow(nRow);
	    	  if( row ==null) return;
	          Cell cell1 = row.getCell(nCol);
	          cell1.setCellValue(value);
	    }
	    public static void displaySheet(HSSFSheet sheet)
	    {
	    	 int nRow =0;
	         int nCol =0;
	         for (Row row : sheet) {
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
	         }
	    }
	}   
