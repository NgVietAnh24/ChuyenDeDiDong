//package vn.posicode.chuyende.DanhSachMonAn;
//
//import android.util.Log;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//
//public class SelectedFoodViewModel extends ViewModel {
//    // MutableLiveData to hold the list of selected foods
//    private final MutableLiveData<ArrayList<SelectedFood>> selectedFoodList;
//
//    // Constructor
//    public SelectedFoodViewModel() {
//        selectedFoodList = new MutableLiveData<>(new ArrayList<>());
//    }
//
//    // LiveData getter
//    public LiveData<ArrayList<SelectedFood>> getSelectedFoodList() {
//        return selectedFoodList;
//    }
//
//    // Function to add a food item to the list
//    public void addSelectedFood(SelectedFood selectedFood) {
//        ArrayList<SelectedFood> currentList = selectedFoodList.getValue();
//        if (currentList != null) {
//            currentList.add(selectedFood);
//            selectedFoodList.setValue(currentList); // Notify observers
//        }
//    }
//
//    // Function to remove a food item from the list
//    public void removeSelectedFood(SelectedFood selectedFood) {
//        ArrayList<SelectedFood > currentList = selectedFoodList.getValue();
//        if (currentList != null) {
//            currentList.remove(selectedFood);
//            selectedFoodList.setValue(currentList); // Notify observers
//        }
//    }
//
//    // Function to set the selected food list
//    public void setSelectedFoodList(ArrayList<SelectedFood> foodList) {
//        selectedFoodList.setValue(foodList);
//    }
//
//    // Function to check if a food item is already selected
//    public boolean isFoodSelected(SelectedFood selectedFood) {
//        ArrayList<SelectedFood> currentList = selectedFoodList.getValue();
//        return currentList != null && currentList.contains(selectedFood);
//    }
//
//    // Function to clear the selected food list
//    public void clearSelectedFoodList() {
//        selectedFoodList.setValue(new ArrayList<>()); // Notify observers with an empty list
//    }
//
//    // Function to update a selected food item
//    public void updateSelectedFood(SelectedFood updatedFood) {
//        ArrayList<SelectedFood> currentList = selectedFoodList.getValue();
//        if (currentList != null) {
//            for (int i = 0; i < currentList.size(); i++) {
//                SelectedFood food = currentList.get(i);
//                if (food.getId().equals(updatedFood.getId())) {
//                    currentList.set(i, updatedFood); // Update the food item
//                    selectedFoodList.setValue(currentList); // Notify observers
//                    break;
//                }
//            }
//        }
//    }
//
//    public void syncWithFirestore(FirebaseFirestore db, String tableId) {
//        ArrayList<SelectedFood> currentList = selectedFoodList.getValue();
//        if (currentList != null) {
//            db.collection("selected_foods")
//                    .whereEqualTo("ban_id", tableId)
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        // Xóa dữ liệu cũ
//                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                            doc.getReference().delete();
//                        }
//
//                        // Lưu dữ liệu mới
//                        for (SelectedFood selectedFood : currentList) {
//                            db.collection("selected_foods")
//                                    .add(selectedFood.toMap()) // Chuyển đổi sang Map trước khi lưu
//                                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi lưu dữ liệu: " + e.getMessage()));
//                        }
//                    })
//                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi đồng bộ: " + e.getMessage()));
//        }
//    }
//
//    // Function to get the current selected food list
//    public ArrayList<SelectedFood> getSelectedFoodListValue() {
//        return selectedFoodList.getValue();
//    }
//}