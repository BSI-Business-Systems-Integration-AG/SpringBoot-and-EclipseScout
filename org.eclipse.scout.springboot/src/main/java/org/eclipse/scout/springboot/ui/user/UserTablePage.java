package org.eclipse.scout.springboot.ui.user;

import java.util.Collection;
import java.util.Set;

import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.springboot.entity.ToDoListModel;
import org.eclipse.scout.springboot.entity.User;
import org.eclipse.scout.springboot.ui.user.UserTablePage.Table;

public class UserTablePage extends AbstractPageWithTable<Table> {

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("UserTablePage");
  }

  @Override
  protected boolean getConfiguredLeaf() {
    return true;
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    Collection<User> users = BEANS.get(ToDoListModel.class).getUsers();
    Table table = getTable();

    table.deleteAllRows();

    if (users == null || users.size() == 0) {
      return;
    }

    for (User user : users) {
      ITableRow row = table.createRow();

      table.getUserNameColumn().setValue(row, user.name);
      table.getFirstNameColumn().setValue(row, user.firstName);
      table.getLastNameColumn().setValue(row, user.lastName);
      table.getAdminColumn().setValue(row, user.roles.contains(ToDoListModel.ROLE_ADMIN));

      table.addRow(row);
    }
  }

  public class Table extends AbstractTable {

    @Override
    protected void execRowAction(ITableRow row) {
      getMenuByClass(EditMenu.class).execAction();
    }

    @Order(1000)
    public class NewMenu extends AbstractMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("New");
      }

      @Override
      protected String getConfiguredIconId() {
        // get unicode from http://fontawesome.io/icon/magic/
        return "font:awesomeIcons \uf0d0";

      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        UserForm form = new UserForm();
        form.addFormListener(new UserFormListener());
        form.startNew();
      }
    }

    @Order(2000)
    public class EditMenu extends AbstractMenu {

      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Edit");
      }

      @Override
      protected String getConfiguredIconId() {
        // get unicode from http://fontawesome.io/icon/pencil/
        return "font:awesomeIcons \uf040";
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        String userName = (String) getSelectedRow().getKeyValues().get(0);

        UserForm form = new UserForm();
        form.addFormListener(new UserFormListener());
        form.getUsernameField().setValue(userName);
        form.startModify();
      }
    }

    private class UserFormListener implements FormListener {

      @Override
      public void formChanged(FormEvent e) {
        // reload page to reflect new/changed data after saving any changes
        if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
          reloadPage();
        }
      }
    }

    public FirstNameColumn getFirstNameColumn() {
      return getColumnSet().getColumnByClass(FirstNameColumn.class);
    }

    public AdminColumn getAdminColumn() {
      return getColumnSet().getColumnByClass(AdminColumn.class);
    }

    public UserNameColumn getUserNameColumn() {
      return getColumnSet().getColumnByClass(UserNameColumn.class);
    }

    public LastNameColumn getLastNameColumn() {
      return getColumnSet().getColumnByClass(LastNameColumn.class);
    }

    @Order(1000)
    public class UserNameColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("UserName");
      }

      @Override
      protected boolean getConfiguredPrimaryKey() {
        return true;
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(2000)
    public class FirstNameColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("FirstName");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(3000)
    public class LastNameColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("LastName");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(4000)
    public class AdminColumn extends AbstractBooleanColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Admin");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }
  }
}