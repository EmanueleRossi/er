import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.erossi.er.ER;
import org.erossi.er.model.Catalog;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ERTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();    

  @BeforeClass
  public static void executedBeforeEach() {
    try {
      FileUtils.cleanDirectory(new File(".\\build\\resources\\test\\destination"));                     
    } catch(Exception e) {
      e.printStackTrace();
    }
  }     
    
  @Test 
  public void testLoadJSONCatalog() throws Exception {
    ER er = new ER();
    er.loadJSONCatalog(new File(".\\build\\resources\\test\\catalog.json"));

    assertNotNull(er.getCatalog());
    assertTrue(er.getCatalog().getDestination().getAbsolutePath().equalsIgnoreCase("C:\\dev\\src\\er\\build\\resources\\test\\destination"));        
    assertEquals("Exclusions list size not matching!", 1, er.getCatalog().getExclusions().size());
    assertEquals("Items list size not matching!", 1, er.getCatalog().getItems().size());
  }    

  @Test 
  public void testExecuteBackup() throws Exception {
    ER er = new ER();
    er.loadJSONCatalog(new File(".\\build\\resources\\test\\catalog.json"));
    File aFile = Paths.get(er.getCatalog().getDestination().getAbsolutePath(), "ERTestRegistry.xlsx").toFile();
    System.out.println("aFile:" + aFile.getAbsolutePath());
    er.executeBackup(aFile);

    assertTrue(er.getCatalog().getDestination().list(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase("ERTestRegistry.xlsx");
        }
    }).length == 1);
  }  
}
