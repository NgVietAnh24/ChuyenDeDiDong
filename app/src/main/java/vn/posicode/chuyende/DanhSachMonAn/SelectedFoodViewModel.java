package vn.posicode.chuyende.DanhSachMonAn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SelectedFoodViewModel extends ViewModel {
    // MutableLiveData to hold the list of selected foods
    private final MutableLiveData<ArrayList<SelectedFood>> selectedFoodList;

    // Constructor
    public SelectedFoodViewModel() {
        selectedFoodList = new MutableLiveData<>(new ArrayList<>());
    }

    // LiveData getter
    public LiveData<ArrayList<SelectedFood>> getSelectedFoodList() {
        return selectedFoodList;
    }

    // Function to add a food item to the list
    public void addSelectedFood(SelectedFood selectedFood) {
        ArrayList<SelectedFood> currentList = selectedFoodList.getValue();
        if (currentList != null) {
            currentList.add(selectedFood);
            selectedFoodList.setValue(currentList); // Notify observers
        }
    }

    // Function to remove a food item from the list
    public void removeSelectedFood(SelectedFood selectedFood) {
        ArrayList<SelectedFood> currentList = selectedFoodList.getValue();
        if (currentList != null) {
            currentList.remove(selectedFood);
            selectedFoodList.setValue(currentList); // Notify observers
        }
    }

    // Function to set the selected food list
    public void setSelectedFoodList(ArrayList<SelectedFood> foodList) {
        selectedFoodList.setValue(foodList);
    }
}