package com.googlecode.semrs.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * @author Roger Marin
 *
 * Clase Utilitaria utilizada para exportar
 * las tablas o grids vistos desde el front end
 * a formato xls a travez de Apache POI.
 */


public class ExcelExporter {
	
	/**
	 * Metodo que se encarga de exportar una lista de objetos
	 * de modo tabular, recibe una lista de objetos(mapas) a exportar,
	 * una seria de encabezados para cada elemento y un nombre de hoja.
	 * Utiliza las clases del Projecto Apache POI. 
	 * Retorna un Objeto HSSFWorkbook que representa un libro de excel.
	 * @param objects
	 * @param headers
	 * @param sheetName
	 * @return
	 */
	
	public static HSSFWorkbook export(ArrayList objects, ArrayList headers, String sheetName){
				

		short rownum;	
		// create a new file
		// create a new workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		// create a new sheet
		HSSFSheet s = wb.createSheet();
		// declare a row object reference
		HSSFRow r = null;
		// declare a cell object reference
		HSSFCell c = null;
		// create 3 cell styles
		HSSFCellStyle cs = wb.createCellStyle();
		HSSFCellStyle cs2 = wb.createCellStyle();
		HSSFCellStyle cs3 = wb.createCellStyle();
		HSSFDataFormat df = wb.createDataFormat();
		// create 2 fonts objects
		HSSFFont f = wb.createFont();
		HSSFFont f2 = wb.createFont();

		//set font 1 to 12 point type
		f.setFontHeightInPoints((short) 12);
		//make it blue
		f.setColor( (short)0xc );
		// make it bold
		//arial is the default font
		f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		//set font 2 to 10 point type
		f2.setFontHeightInPoints((short) 10);
		//make it red
		f2.setColor( (short)HSSFFont.COLOR_NORMAL );
		//make it bold
		//f2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		//f2.setStrikeout( true );

		//set cell stlye
		cs.setFont(f);
		//set the cell format 
		cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));

		//set a thin border
		//cs2.setBorderBottom(cs2.BORDER_THIN);
		//fill w fg fill color
		//cs2.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		//set the cell format to text see HSSFDataFormat for a full list
		

		// set the font
		cs2.setFont(f2);
		cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));

		// in case of compressed Unicode
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		 wb.setSheetName(0, sheetName+"_"+format.format(new Date()), HSSFWorkbook.ENCODING_COMPRESSED_UNICODE );
		
		  r = s.createRow(0);
		  for (short cellnum = (short) 0; cellnum<headers.size(); cellnum++)
		    {

		        String cellValue;

		        // create a string cell (see why += 2 in the
		        c = r.createCell((short) (cellnum));
		        
		            // set this cell to the first cell style we defined
		            c.setCellStyle(cs);
		            // set the cell's string value to "Test"
		            c.setEncoding( HSSFCell.ENCODING_COMPRESSED_UNICODE );
		            c.setCellValue((String) headers.get(cellnum));
   
		        // make this column a bit wider
		        s.setColumnWidth((short) (cellnum), (short) ((50 * 8) / ((double) 1 / 20)));
		         
		    }
		  
		  rownum = (short) 0;
		  for(Object map : objects){
			  r = s.createRow(rownum+1);
			  Iterator items = ((TreeMap)map).entrySet().iterator();
			  for (short cellnum = (short) 0; items.hasNext(); cellnum ++)
			    {
			        // create a numeric cell
			        c = r.createCell(cellnum);
			      
			        String cellValue;

			        // create a string cell (see why += 2 in the
			        c = r.createCell((short) (cellnum));

			            c.setCellStyle(cs2);
			            c.setEncoding( HSSFCell.ENCODING_COMPRESSED_UNICODE );
			            
			            Map.Entry pairs = (Map.Entry)items.next();
			            c.setCellValue( (String) pairs.getValue());
			   
			      
			        // make this column a bit wider
			        s.setColumnWidth((short) (cellnum + 1), (short) ((50 * 8) / ((double) 1 / 20)));
			    }
			  rownum++;
			  
		  }
		  
		 
		
		//draw a thick black border on the row at the bottom using BLANKS
		// advance 2 rows
		//rownum++;
		//rownum++;
		return wb;
	
		        
	}

}
