package com.dzf.zxkj.platform.util.taxrpt;

import java.io.File;

public class TaxReportPath {

	public static String taxReportPath = "";
	  static
	  {
		  taxReportPath ="";
	    if (!taxReportPath.endsWith(File.separator))
	    	taxReportPath += File.separator;
	    taxReportPath += "taxreport" + File.separator;
	    
	    File fSpreadFile = new File(taxReportPath + "spreadfile");
	    if (fSpreadFile.exists() == false)
	    {
	    	fSpreadFile.mkdir();
	    }
	    File fPdfFile = new File(taxReportPath + "pdffile");
	    if (fPdfFile.exists() == false)
	    {
	    	fPdfFile.mkdir();
	    }
	    File fQCDataFile = new File(taxReportPath + "qcdata");
	    if (fQCDataFile.exists() == false)
	    {
	    	fQCDataFile.mkdir();
	    }
	  }
	public TaxReportPath() {
		// TODO Auto-generated constructor stub
	}

}
