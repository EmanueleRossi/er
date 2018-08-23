
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
import org.erossi.er.util.HashTool;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegistryCreator implements Observer<File> {

  private Workbook wb;
  private Sheet sheet1;

  public RegistryCreator() {
  }

	@Override
	public void onSubscribe(Disposable d) {
    System.out.println("### ER - DISPOSABLE ###" + d.toString());
    wb = new XSSFWorkbook();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh_mm ss");    
    sheet1 = wb.createSheet(dateFormat.format(new Date()));        
  }

  @Override
  public void onNext(File f) {
      Row xlsxRow = sheet1.createRow(sheet1.getLastRowNum() + 1);
      xlsxRow.createCell(0).setCellValue(f.getAbsolutePath());
    try {      
      xlsxRow.createCell(1).setCellValue(HashTool.SHA512.checksumBase64(f));
    } catch (FileNotFoundException fnf) {
      fnf.printStackTrace();
    } catch (NoSuchAlgorithmException nsa) {
      nsa.printStackTrace();
    } catch (IOException io) {
      io.printStackTrace();
    }
  }  
  @Override 
  public void onComplete() {
    try {
      FileOutputStream fileOut = new FileOutputStream(".\\build\\resources\\test\\destination\\erRegistry.xlsx");
      wb.write(fileOut);
      fileOut.close();
    } catch (IOException io) {
      io.printStackTrace();
    }
  }
  
  @Override
  public void onError(Throwable e) {
    e.printStackTrace();
  }
}