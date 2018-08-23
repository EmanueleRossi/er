
package org.erossi.er.observer;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BackupStatusLogger implements Observer<File> {
  @Override 
  public void onComplete() {
    System.out.println("### ER - Backup Operation Completed! ###");
  }
  
  @Override
  public void onError(Throwable e) {
    System.out.println("    ER - Backup Operation ERROR! |" + e.getMessage() + "|");	
  }
  
  @Override
  public void onNext(File f) {
    long fLength = f.length();
    if (fLength != 0) fLength = fLength / 1024;
    System.out.println("    Backup item: |" + f.getAbsolutePath() + "| of size |" + fLength + "| KB");
  }

	@Override
	public void onSubscribe(Disposable d) {
    System.out.println("### ER - Backup Operation Start! ###");
	}
}