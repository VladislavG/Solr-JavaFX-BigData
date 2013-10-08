package com.canoo.solar;

import javafx.scene.control.TreeItem;

import java.util.Comparator;

public class TreeItemNumberComparator implements Comparator<TreeItem> {
    @Override
    public int compare(TreeItem o1, TreeItem o2) {
        String[] elements = o1.getValue().toString().split(" \\(");
        String element1 = elements[1];
        String[] elements2 = element1.split("\\)");
        String theElement = elements2[0];

        String[] elements3 = o2.getValue().toString().split(" \\(");
        String element2 = elements3[1];
        String[] elements4 = element2.split("\\)");
        String theElement2 = elements4[0];

        String itemNumber1 = theElement;
        String itemNumber2 = theElement2;

        //ascending order
        return theElement2.compareTo(theElement);

        //descending order
    }

}
