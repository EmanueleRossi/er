package org.erossi.er;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.erossi.er.model.BackupItem;
import org.erossi.er.model.Catalog;
import org.erossi.er.observer.BackupStatusLogger;
import org.erossi.er.observer.RegistryCreator;

import io.reactivex.Observable;

public class ER {

  private Catalog catalog;

  public Catalog getCatalog() {
    return catalog;
  }

  private File registry;

  public File getRegistry() {
    return registry;
  }

  public static void main(String[] args) {
    try {
      ER er = new ER();
      er.loadJSONCatalog(new File(".\\build\\resources\\main\\catalog.json"));
      er.executeBackup();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ER() {
    catalog = new Catalog();
    registry = null;

  }

  public void loadJSONCatalog(File file) throws IOException, ERException {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      this.catalog = objectMapper.readValue(file, Catalog.class);
    } catch (Exception e) {
      throw new ERException("Catalog cannot be loaded from JSON file!", e);
    }
  }

  public void executeBackup() throws Exception {
    try {
      List<File> fileList = new ArrayList<File>();
      for (BackupItem bi : catalog.getItems()) {
        fileList.addAll((List<File>) FileUtils.listFiles(bi.getPath(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
      }

      Observable<File> files = Observable.fromIterable(fileList);
      files.subscribe(new BackupStatusLogger());
      files.subscribe(new RegistryCreator());

      for (int i = 0; i < fileList.size(); i++) {
        File f = fileList.get(i);
        Path fNonRootPath = f.toPath().getRoot().relativize(f.toPath());
        Path destinationCompletePath = Paths.get(catalog.getDestination().getAbsolutePath(), fNonRootPath.toString());
        File d = new File(destinationCompletePath.toUri());

        if (f.isFile()) {
          FileUtils.copyFile(f, d);
        } else {
          d.mkdirs();
        }
      }
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {

    } finally {
     /* if (registry != null) {

      }*/
    }

  }
}
