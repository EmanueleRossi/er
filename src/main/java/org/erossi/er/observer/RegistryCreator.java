
package org.erossi.er.observer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.erossi.er.model.BackupItem;
import org.erossi.er.util.HashTool;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegistryCreator implements Observer<BackupItem> {

  private Workbook wb;
  private Sheet sheet1;
  private FileOutputStream fileOut;

  public RegistryCreator(File registry) {        
    try {
      this.fileOut = new FileOutputStream(registry);
    } catch (IOException ioe) {
      System.err.format("%s |%s|", "ERROR! I/O Error :(", ioe.getMessage());             
    }    
  }

	@Override
	public void onSubscribe(Disposable d) {
    System.out.println("### ER - Registry Creation Operation Start! ###");    
    wb = new XSSFWorkbook();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh_mm ss");    
    sheet1 = wb.createSheet(dateFormat.format(new Date()));        
  }

  @Override
  public void onNext(BackupItem bi) {
    Row xlsxRow = sheet1.createRow(sheet1.getLastRowNum() + 1);
    xlsxRow.createCell(0).setCellValue(bi.getOrigin().getAbsolutePath());
    xlsxRow.createCell(2).setCellValue(bi.getBackup().getAbsolutePath());
    xlsxRow.createCell(4).setCellFormula(String.format("%s%d%s%d%s", "IF(B", xlsxRow.getRowNum() + 1, "=D", xlsxRow.getRowNum() + 1, ",TRUE,FALSE)"));
    try {     
      xlsxRow.createCell(1).setCellValue(HashTool.SHA512.checksumBase64(bi.getOrigin().getAbsoluteFile()));
      xlsxRow.createCell(3).setCellValue(HashTool.SHA512.checksumBase64(bi.getBackup().getAbsoluteFile()));      
    } catch (FileNotFoundException fnf) {
      System.err.format("%s |%s|", "ERROR! File Not Found :(", bi.getOrigin().getAbsolutePath());                  
    } catch (NoSuchAlgorithmException nsa) {
      System.err.format("%s |%s|", "ERROR! Mo Such Algorithm :(", nsa.getMessage());        
    } catch (IOException ioe) {
      System.err.format("%s |%s|", "ERROR! I/O Error :(", ioe.getMessage());             
    }
  }  
  
  @Override 
  public void onComplete() {
    try {  
      wb.write(fileOut);
      fileOut.close();
      System.out.println("### ER - Registry Creation Operation End! ###");         
    } catch (IOException ioe) {
      System.err.format("%s |%s|", "ERROR! I/O Error :(", ioe.getMessage());             
    }
  }
  
  @Override
  public void onError(Throwable e) {
    System.err.format("%s |%s|", "ERROR! :(", e.getMessage()); 
  }
}