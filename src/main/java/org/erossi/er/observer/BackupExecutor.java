
package org.erossi.er.observer;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.erossi.er.model.BackupItem;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BackupExecutor implements Observer<BackupItem> {

  public BackupExecutor() {
  }

  @Override 
  public void onComplete() {
    System.out.println("### ER - File Copy Operation Completed! ###");
  }
  
  @Override
  public void onError(Throwable e) {
    System.out.println("    ER - File Copy Operation ERROR! |" + e.getMessage() + "|");	
  }
  
  @Override
  public void onNext(BackupItem bi) {
    long fLength = bi.getOrigin().length();
    if (fLength != 0) fLength = fLength / 1024;
    System.out.println("    Backup item: |" + bi.getOrigin().getAbsolutePath() + "| of size |" + fLength + "| KB");
    if (bi.getOrigin().isFile()) {
      try {
        FileUtils.copyFile(bi.getOrigin(),  bi.getBackup());
      } catch (IOException ioe) {
        System.err.format("%s |%s|", "ERROR! Copying file :(", ioe.getMessage());
	    }
    } else {
      bi.getBackup().mkdirs(); 
    }   
  }

	@Override
	public void onSubscribe(Disposable d) {
    System.out.println("### ER - File Copy Operation Start! ###");
	}
}