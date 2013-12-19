package com.canoo.solar

import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.VBox
import org.opendolphin.core.PresentationModel

import static com.canoo.solar.Constants.FilterConstants.FACET
import static com.canoo.solar.Constants.FilterConstants.ORDER

/**
 * Created by vladislav on 16.12.13.
 */
class UpdateActions {
    static void updateTree(LinkedList data, TreeView tree, List idsList, List countList, Integer dataIndex, String property) {

        tree.getSelectionModel().clearSelection()
        idsList.clear()
        idsList.addAll(data.get(dataIndex).get(Constants.FilterConstants.IDS))
        countList.clear()
        countList.addAll(data.get(dataIndex).get(Constants.FilterConstants.NUM_COUNT))
        def size = data.get(0).get(Constants.FilterConstants.SIZE)
        Map<Integer,Integer> map = new LinkedHashMap<Integer,Integer>();
        List<TreeItem<String>> iteratedList = new ArrayList<TreeItem<String>>()

        for (int i=0; i<idsList.size(); i++) {
            map.put(idsList.get(i), countList.get(i));
        }

        tree.getRoot().getChildren().each {
            String name = it.getValue().toString().substring(0,it.getValue().toString().lastIndexOf(' ('))

            def newCount = map.get(name)
            if (newCount==null)newCount=0;
            final TreeItem<String> checkBoxTreeItem =
                new TreeItem<String>(name + " (" + newCount + ")");
            iteratedList.add(checkBoxTreeItem);
        }

        tree.getRoot().setValue("$property ($size)")
        Collections.sort(iteratedList, new TreeItemNumberComparator())
        tree.getRoot().getChildren().clear()
        tree.getRoot().getChildren().addAll(iteratedList)

    }

    public static void clearPmsAndPowerPlants(){
        List<PresentationModel> pmsToRemove = new ArrayList<PresentationModel>()
        Application.clientDolphin.getModelStore().findAllPresentationModelsByType(Constants.FilterConstants.POWERPLANT).each {
            pmsToRemove.add(it)
        }
        Application.clientDolphin.deleteAllPresentationModelsOfType(Constants.FilterConstants.POWERPLANT)
        pmsToRemove.each {
            Application.getFakeList().removePlant(Integer.parseInt(it.getId()))
        }

    }

    public static void refreshTable(){
        Application.clientDolphin.data Constants.CMD.GET, { data ->
            def size = data.get(0).get(Constants.FilterConstants.SIZE)
            PowerPlantList newFakeList = new PowerPlantList((Integer)size, new OurConsumer<Integer>(){
                @Override
                void accept(Integer rowIndex) {
                    Application.loadPresentationModel(rowIndex)
                }
            });
            javafx.collections.ObservableList<PowerPlant> newItems = FakeCollections.newObservableList(newFakeList);
            Application.table.setItems(newItems)
            Application.totalCount.setText(newItems.size() + "/1377475")
            Application.treesGrid.setDisable(false)
            Application.table.getSelectionModel().clearSelection()
            if (Application.clientDolphin[Constants.FilterConstants.STATE][Constants.FilterConstants.HOLD].getValue()==false){

                def value = Application.clientDolphin[Constants.FilterConstants.STATE][Constants.FilterConstants.CHANGE_FROM].getValue()
                if (value==-1 || value==-2){
                    UpdateActions.updateTree(data,Application.treeTypes, Application.observableListTypes, Application.observableListTypesCount, 1, "Plant Types")
                    UpdateActions.updateTree(data, Application.treeZip, Application.observableListZips, Application.observableListZipsCount, 3, "Zip-Codes")
                    UpdateActions.updateTree(data,Application.treeCities,Application.observableListCities,Application.observableListCitiesCount,2,"Cities")
                }else if(value==0){
                    VBox originBox = Application.facetBox.getChildren().get(value)
                    originBox.getChildren().each {

                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                        if (selectedIdx == -1) {
                            treeView.getSelectionModel().select(treeView.getRoot());
                            treeView.getSelectionModel().clearSelection()
                        }else{

                            treeView.getSelectionModel().clearSelection()
                            treeView.getSelectionModel().select(selectedIdx)
                        }
                    }
                }
                else {

                    VBox originBox = Application.facetBox.getChildren().get(value-1)
                    originBox.getChildren().each {

                        TreeView treeView = it.getChildren().get(0).getChildren().get(1)
                        Integer selectedIdx = treeView.getSelectionModel().getSelectedIndex()
                        if (selectedIdx == -1) {
                            treeView.getSelectionModel().select(treeView.getRoot());
                            treeView.getSelectionModel().clearSelection()
                        }else{

                            treeView.getSelectionModel().clearSelection()
                            treeView.getSelectionModel().select(selectedIdx)
                        }
                    }
                }
                Application.clientDolphin[Constants.FilterConstants.STATE][Constants.FilterConstants.CHANGE_FROM].setValue(-2)
            }
            Application.clientDolphin[Constants.FilterConstants.STATE][Constants.FilterConstants.HOLD].setValue(false)
        }


    }

    public static void facetAddRemove(String propertyName, TextField autoFillTextBox, String addRemove){

                if (addRemove.equals("add")) {
                    List valuesList = new ArrayList()
                    int c = 1

                    Application.clientDolphin.findAllPresentationModelsByType(FACET).each {
                        def orderValue = it.findAttributeByPropertyName(ORDER).getValue()
                        if (orderValue > 0 && !valuesList.contains(orderValue)){
                            valuesList.add(orderValue)
                            c++
                        }
                    }


                    Application.clientDolphin.findPresentationModelById(propertyName)[ORDER].setValue(c)
                    autoFillTextBox.setVisible(false)
                }

                else {
                    boolean emptyVbox = true
                    def orderValue = Application.clientDolphin.findPresentationModelById(propertyName)[ORDER].getValue()
                    Application.clientDolphin.findAllPresentationModelsByType(FACET).each {
                        if (it.getId().equals(propertyName))return;
                        if (it[ORDER].getValue().equals(orderValue)){
                            emptyVbox = false
                        }
                    }
                    Application.clientDolphin.findAllPresentationModelsByType(FACET).each {
                        def facetOrder = it.findAttributeByPropertyName(ORDER)
                        if (emptyVbox){
                            if (facetOrder.getValue() > orderValue){
                                facetOrder.setValue(facetOrder.getValue() - 1)
                            }
                        }
                    }

                    Application.clientDolphin.findPresentationModelById(propertyName)[ORDER].setValue(0)
                    autoFillTextBox.setVisible(true)
                }
    }
}
