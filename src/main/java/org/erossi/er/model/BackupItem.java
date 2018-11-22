package org.erossi.er.model;

import java.io.File;

public class BackupItem {

  private File origin;
  public File getOrigin() { return origin; }
  public void setOrigin(File origin) { this.origin = origin; }   
  
  private File backup;
  public File getBackup() { return backup; }
  public void setBackup(File backup) { this.backup = backup; }   

  public BackupItem() {
  }

  public BackupItem(File origin, File backup) { 
    this.origin = origin; 
    this.backup = backup;
  }  

  public String toString() {
    StringBuilder response = new StringBuilder();
    response.append(String.format("%s=|%s|\t", "origin", (this.getOrigin() == null) ? "{null}" : this.getOrigin().getAbsolutePath()));  
    response.append(String.format("%s=|%s|\t", "backup", (this.getBackup() == null) ? "{null}" : this.getBackup().getAbsolutePath()));   
    response.append("\n");
    return response.toString();
  }    

}
