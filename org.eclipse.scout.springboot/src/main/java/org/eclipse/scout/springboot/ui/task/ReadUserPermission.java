package org.eclipse.scout.springboot.ui.task;

import java.security.BasicPermission;

public class ReadUserPermission extends BasicPermission {

  private static final long serialVersionUID = 1L;

  public ReadUserPermission() {
    super(ReadUserPermission.class.getSimpleName());
  }
}