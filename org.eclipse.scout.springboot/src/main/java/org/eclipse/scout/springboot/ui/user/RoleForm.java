package org.eclipse.scout.springboot.ui.user;

import java.security.Permission;
import java.util.Collection;

import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.IPermissionService;
import org.eclipse.scout.springboot.entity.Role;
import org.eclipse.scout.springboot.entity.ToDoListModel;
import org.eclipse.scout.springboot.ui.AbstractDirtyFormHandler;
import org.eclipse.scout.springboot.ui.task.TaskForm.MainBox.CancelButton;
import org.eclipse.scout.springboot.ui.task.TaskForm.MainBox.OkButton;
import org.eclipse.scout.springboot.ui.user.RoleForm.MainBox.RoleBox;
import org.eclipse.scout.springboot.ui.user.RoleForm.MainBox.RoleBox.PermissionTableField;
import org.eclipse.scout.springboot.ui.user.RoleForm.MainBox.RoleBox.RoleNameField;

public class RoleForm extends AbstractForm {

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  protected int getConfiguredDisplayHint() {
    return IForm.DISPLAY_HINT_VIEW;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Role");
  }

  public void startModify() {
    startInternalExclusive(new ModifyHandler());
  }

  public void startNew() {
    startInternal(new NewHandler());
  }

  public CancelButton getCancelButton() {
    return getFieldByClass(CancelButton.class);
  }

  public RoleNameField getRoleNameField() {
    return getFieldByClass(RoleNameField.class);
  }

  public PermissionTableField getPermissionTableField() {
    return getFieldByClass(PermissionTableField.class);
  }

  public RoleBox getRoleBox() {
    return getFieldByClass(RoleBox.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  @Order(1000)
  public class MainBox extends AbstractGroupBox {

    @Order(1000)
    public class RoleBox extends AbstractGroupBox {

      @Order(1000)
      public class RoleNameField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("RoleName");
        }

        @Override
        protected boolean getConfiguredMandatory() {
          return true;
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 128;
        }
      }

      @Order(2000)
      public class PermissionTableField extends AbstractTableField<PermissionTableField.Table> {

        @Override
        protected boolean getConfiguredLabelVisible() {
          return false;
        }

        @Override
        protected int getConfiguredGridW() {
          return 2;
        }

        @Override
        protected int getConfiguredGridH() {
          return 6;
        }

        public class Table extends AbstractTable {

          public void setInitialRowContent(Collection<String> permissionIds) {
            for (Class<? extends Permission> permission : BEANS.get(IPermissionService.class).getAllPermissionClasses()) {
              String pId = permission.getName();
              String pName = TEXTS.getWithFallback(pId, pId);
              Boolean assigned = new Boolean(permissionIds.contains(pId));

              getTable().addRowByArray(new Object[]{pId, pName, assigned});
            }
          }

          public AssignedColumn getAssignedColumn() {
            return getColumnSet().getColumnByClass(AssignedColumn.class);
          }

          public NameColumn getNameColumn() {
            return getColumnSet().getColumnByClass(NameColumn.class);
          }

          public IdColumn getIdColumn() {
            return getColumnSet().getColumnByClass(IdColumn.class);
          }

          @Order(1000)
          public class IdColumn extends AbstractStringColumn {
            @Override
            protected boolean getConfiguredDisplayable() {
              return false;
            }

            @Override
            protected boolean getConfiguredPrimaryKey() {
              return true;
            }
          }

          @Order(2000)
          public class NameColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Name");
            }

            @Override
            protected int getConfiguredWidth() {
              return 100;
            }
          }

          @Order(3000)
          public class AssignedColumn extends AbstractBooleanColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Assigned");
            }

            @Override
            protected int getConfiguredWidth() {
              return 50;
            }

            @Override
            protected boolean getConfiguredEditable() {
              return true;
            }
          }
        }
      }
    }

    @Order(100000)
    public class OkButton extends AbstractOkButton {
    }

    @Order(101000)
    public class CancelButton extends AbstractCancelButton {
    }
  }

  public class ModifyHandler extends AbstractDirtyFormHandler {

    @Override
    protected void execLoad() {
      Role role = BEANS.get(ToDoListModel.class).getRole(getId());

      getRoleNameField().setValue(role.getName());
      getPermissionTableField().getTable().setInitialRowContent(role.getPermissions());
      setSubTitle(calculateSubTitle());

      setEnabledPermission(new UpdateUserPermission());
    }

    @Override
    protected void execStore() {
      Role role = BEANS.get(ToDoListModel.class).getRole(getId());

      role.setName(getRoleNameField().getValue());

      for (ITableRow row : getPermissionTableField().getTable().getRows()) {
        String permission = (String) row.getKeyValues().get(0);
        boolean assigned = (boolean) row.getCell(2).getValue();

        if (assigned) {
          role.addPermission(permission);
        }
        else {
          role.removePermission(permission);
        }
      }
//
//      user.setPicture(getPictureField().getByteArrayValue());
//      user.setFirstName(getFirstNameField().getValue());
//      user.setLastName(getLastNameField().getValue());
//      user.setPassword(getPasswordField().getValue());
//
//      if (getAdminField().getValue()) {
//        user.getRoles().add(ToDoListModel.ROLE_ADMIN);
//      }
//      else {
//        user.getRoles().remove(ToDoListModel.ROLE_ADMIN);
//      }
//
//      BEANS.get(UserPictureProviderService.class).addUserPicture(user.getName(), user.getPicture());
    }

    @Override
    protected void execDirtyStatusChanged(boolean dirty) {
      getForm().setSubTitle(calculateSubTitle());
    }

    @Override
    protected boolean getConfiguredOpenExclusive() {
      return true;
    }
  }

  public class NewHandler extends AbstractDirtyFormHandler {

    @Override
    protected void execLoad() {
      setEnabledPermission(new CreateUserPermission());
    }

    @Override
    protected void execStore() {
//      User user = BEANS.get(ToDoListModel.class).getUser(getUsernameField().getValue());
//
//      if (user != null) {
//        getUsernameField().setValue(null);
//        throw new VetoException(TEXTS.get("AccountAlreadyExists", user.getName()));
//      }
//
//      String userName = getUsernameField().getValue();
//      String firstName = getFirstNameField().getValue();
//      String lastName = getLastNameField().getValue();
//      String password = getPasswordField().getValue();
//
//      user = new User(userName, firstName, lastName, password);
//      user.setPicture(getPictureField().getByteArrayValue());
//
//      if (getAdminField().getValue()) {
//        user.getRoles().add(ToDoListModel.ROLE_ADMIN);
//      }
//      else {
//        user.getRoles().remove(ToDoListModel.ROLE_ADMIN);
//      }
//
//      BEANS.get(ToDoListModel.class).addUser(user);
    }

    @Override
    protected void execDirtyStatusChanged(boolean dirty) {
      getForm().setSubTitle(calculateSubTitle());
    }
  }

  private String calculateSubTitle() {
    return getRoleNameField().getValue();
  }
}
