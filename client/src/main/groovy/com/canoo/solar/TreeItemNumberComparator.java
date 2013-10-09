package com.canoo.solar;

import javafx.scene.control.TreeItem;

import java.util.Comparator;

public class TreeItemNumberComparator implements Comparator<TreeItem> {
    @Override
    public int compare(TreeItem o1, TreeItem o2) {
        String firstCount = o1.getValue().toString();
        String secondCount = o2.getValue().toString();

        String theElement = firstCount.substring(firstCount.lastIndexOf('(')+1, firstCount.lastIndexOf(')'));
        String theElement2 =  secondCount.substring(secondCount.lastIndexOf('(')+1, secondCount.lastIndexOf(')'));

        Integer itemNumber1 = Integer.parseInt(theElement);
        Integer itemNumber2 = Integer.parseInt(theElement2);

        //ascending order
        return itemNumber2.compareTo(itemNumber1);

        //descending order
    }

}
