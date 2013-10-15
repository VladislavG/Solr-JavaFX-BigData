package com.canoo.solar;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableView;

public class TableViews {
    public static TableViewInfo getTableViewInfo(TableView tableView) {
        TableViewSkin tableViewSkin = (TableViewSkin) tableView.getSkin();
        ObservableList<Node> children = tableViewSkin.getChildren();


        VirtualFlow virtualFlow = (VirtualFlow) children.get(1);
        TableHeaderRow tableHeaderRow = (TableHeaderRow) children.get(0);




        return new TableViewInfo(virtualFlow, tableHeaderRow);


    }



    /**
     * Provides access to internal controls of a {@code TableView}. Can be created with {@link #getTableViewInfo}
     */
    public static class TableViewInfo {
        private final VirtualFlow virtualFlow;
        private final TableHeaderRow tableHeaderRow;


        private TableViewInfo(final VirtualFlow virtualFlow, final TableHeaderRow tableHeaderRow) {
            this.virtualFlow = virtualFlow;
            this.tableHeaderRow = tableHeaderRow;
        }


        public VirtualFlow getVirtualFlow() {
            return virtualFlow;
        }


        public TableHeaderRow getTableHeaderRow() {
            return tableHeaderRow;
        }
    }
}
