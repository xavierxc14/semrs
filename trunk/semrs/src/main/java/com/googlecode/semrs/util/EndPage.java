package com.googlecode.semrs.util;



import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Paragraph;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;


public class EndPage extends PdfPageEventHelper {
    
	  public PdfPTable table;
	
	  public BaseFont helv;
	  
	  public PdfTemplate tpl;



    /**
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    public void onEndPage(PdfWriter writer, Document document) {
        try {
        	
        	  PdfContentByte cb = writer.getDirectContent();
			cb.saveState();
			table = new PdfPTable(2);
			tpl = writer.getDirectContent().createTemplate(100, 100);
			tpl.setBoundingBox(new Rectangle(-20, -20, 100, 100));
			// initialization of the font
			helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);

			table.setTotalWidth(document.right() - document.left());
			table.writeSelectedRows(0, -1, document.left(), document
					.getPageSize().getHeight() - 50, cb);
			// compose the footer
			String text = "Página " + writer.getPageNumber() + " de ";
			float textSize = helv.getWidthPoint(text, 12);
			float textBase = document.bottom() - 10;
			cb.beginText();
			cb.setFontAndSize(helv, 12);
			// if ((writer.getPageNumber() & 1) == 1) {
			float adjust = helv.getWidthPoint("0", 12);
			cb.setTextMatrix(document.right() - textSize - adjust, textBase);
			cb.showText(text);
			cb.endText();
			cb.addTemplate(tpl, document.right() - adjust, textBase);

			cb.saveState();
			cb.restoreState();



        	
       
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    
    /**
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onCloseDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     */
    public void onCloseDocument(PdfWriter writer, Document document) {
       tpl.beginText();
       tpl.setFontAndSize(helv, 12);
       tpl.setTextMatrix(0, 0);
       tpl.showText("" + (writer.getPageNumber() - 1));
       tpl.endText();
    }


}
