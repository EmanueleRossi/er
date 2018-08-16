package org.erossi.er;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.erossi.er.model.BackupItem;
import org.erossi.er.model.Catalog;
import org.erossi.er.util.HashTool;

public class ER {

    private static final Logger logger = Logger.getLogger(ER.class.getName());

    private Catalog catalog;
    public Catalog getCatalog() { return catalog; }

    private File registry;
    public File getRegistry() { return registry; }
    private Workbook wb;

    public static void main(String[] args) {
        try {
            ER er = new ER();
            er.loadJSONCatalog(new File(".\\build\\resources\\main\\catalog.json"));
            er.initBackupRegistry("erRegistry.xlsx");
            er.executeBackup();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ER() {
        catalog = new Catalog();        
        registry = null;
        wb = new XSSFWorkbook();
    }

    public void loadJSONCatalog(File file) throws IOException, ERException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.catalog = objectMapper.readValue(file, Catalog.class);
        } catch (Exception e) {            
            throw new ERException("Catalog cannot be loaded from JSON file!", e);
        }
    }

    public void initBackupRegistry(String fileName) throws Exception {
        if (catalog.getDestination() != null) {
            registry = new File(Paths.get(catalog.getDestination().getAbsolutePath(), fileName).toUri());
        } else {
            registry = new File(fileName);
        }

        if (registry.exists()) {
            wb = WorkbookFactory.create(registry);
        } else {
            wb = new XSSFWorkbook();
        }
    }
 
    public void executeBackup() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh_mm ss");
        Sheet sheet1 = wb.createSheet(dateFormat.format(new Date()));        

        List<File> fileList = new ArrayList<File>();
        for(BackupItem bi : catalog.getItems()) {
            fileList.addAll((List<File>) FileUtils.listFiles(bi.getPath(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
        }

        for (int i = 0; i < fileList.size(); i++) {
            File f = fileList.get(i);
            Path fNonRootPath = f.toPath().getRoot().relativize(f.toPath());            
            logger.log(Level.FINE, "fNonRootPath.toString() =|" + fNonRootPath.toString() + "|");
            Path destinationCompletePath = Paths.get(catalog.getDestination().getAbsolutePath(), fNonRootPath.toString());
            logger.log(Level.FINE, "destinationCompletePath.toString() =|" + destinationCompletePath.toString() + "|");
            File d = new File(destinationCompletePath.toUri());
            logger.log(Level.FINE, "d.toString() =|" + d.toString() + "|");

            Row xlsxRow = sheet1.createRow(i);
            xlsxRow.createCell(0).setCellValue(f.getAbsolutePath());
            xlsxRow.createCell(1).setCellValue(HashTool.SHA512.checksumBase64(f));

            if (f.isFile()) {
                FileUtils.copyFile(f, d);
            } else {
                d.mkdirs();    
            }

            xlsxRow.createCell(2).setCellValue(d.getAbsolutePath());
            xlsxRow.createCell(3).setCellValue(HashTool.SHA512.checksumBase64(d));
        }

        if (registry != null) {
            FileOutputStream fileOut = new FileOutputStream(registry);
            wb.write(fileOut);
            fileOut.close();    
        }      
    }
}
