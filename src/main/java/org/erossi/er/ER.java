package org.erossi.er;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.erossi.er.model.BackupItem;
import org.erossi.er.model.Catalog;
import org.erossi.er.observer.BackupExecutor;
import org.erossi.er.observer.RegistryCreator;
import org.erossi.er.util.FileTool;

import io.reactivex.Observable;

public class ER {

  private Catalog catalog;

  public Catalog getCatalog() { return catalog; }

  public static void main(String[] args) {
    try {
      ER main = new ER();      
      String gitHash = new String();
      try (JarInputStream jIS = new JarInputStream(main.getClass().getProtectionDomain().getCodeSource().getLocation().openStream())) {
        gitHash = jIS.getManifest().getMainAttributes().getValue("Git-Hash");
      } catch (Exception e) {
        gitHash = "...under development ;)";                
      }       
      System.out.format("### EasyRecovery ### - Version %s\n", gitHash);
      if (args[0].contains("help")) {
        System.out.format("\tUsage: java -jar <jarFileName> [Catalog File] [Registry File]\n"); 
        System.out.format("\t  ex.: java -jar <jarFileName> catalog.json registry.xlsx\n");          
      } else {      
        if (args[0].isEmpty() | args[1].isEmpty()) {
          System.out.format("\tWrong parameters specified... try \"help\" command for instructions.\n");
        } else {
          main.loadJSONCatalog(new File(args[0]));
          File registry = new File(args[1]);
          main.executeBackup(registry);
        }
      } 
    } catch (Exception e) {
      System.err.format("%s |%s|", "ERROR! :(", e.getMessage()); 
      e.printStackTrace();
    }
  }

  public ER() {
  }

  public void loadJSONCatalog(File file) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();      
      catalog = objectMapper.readValue(file, Catalog.class);
    } catch (IOException ioe) {
      System.err.format("%s |%s|", "ERROR! I/O Error loading JSON catalog :(", ioe.getMessage());      
    }           
  }

  public void executeBackup(File registry) {

    List<BackupItem> backupList = catalog.getItems();
    List<BackupItem> completeBackupList = new ArrayList<BackupItem>();
    FileTool fileTool = new FileTool(catalog);

    for (BackupItem bi : backupList) {
      if (bi.getOrigin().isFile()) {
        completeBackupList.add(new BackupItem(bi.getOrigin(), fileTool.getBackup(bi.getOrigin())));
      } else {
        FileUtils.listFiles(bi.getOrigin(), null, true).forEach(f -> completeBackupList.add(new BackupItem(f, fileTool.getBackup(f))));
      }
    }
    final List<BackupItem> completeBackupListFinal = completeBackupList;
    catalog.getExclusions().forEach(e -> completeBackupListFinal.removeIf(bi -> bi.getBackup().getName().contains(e)));
    backupList = backupList.stream().distinct().collect(Collectors.toList());

    Observable<BackupItem> obsBackupList = Observable.fromIterable(completeBackupListFinal);
    obsBackupList.subscribe(new BackupExecutor());        
    obsBackupList.subscribe(new RegistryCreator(registry)); 
  }
}
