package org.erossi.er.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.erossi.er.model.Catalog;

public class FileTool {

  private Catalog catalog;

  public FileTool(Catalog catalog){
    this.catalog = catalog;
  }

  public File getBackup(File f) {
    Path fNonRootPath = f.getAbsoluteFile().toPath().getRoot().relativize(f.getAbsoluteFile().toPath());  
    Path destinationCompletePath = Paths.get(catalog.getDestination().getAbsolutePath(), fNonRootPath.toString());         
    return new File(destinationCompletePath.toUri());
  }

}