package com.example.app_travelpath.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app_travelpath.data.repository.SpotRepository;
import com.example.app_travelpath.model.Spot;

import java.util.List;

public class JourneyViewModel extends AndroidViewModel {

    private final SpotRepository repository;
    // Les LiveData sont des variables "observables" par l'interface
    private final MutableLiveData<List<Spot>> spots = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public JourneyViewModel(@NonNull Application application) {
        super(application);
        repository = new SpotRepository(application);
    }

    public LiveData<List<Spot>> getSpots() { return spots; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void searchCity(String city) {
        isLoading.setValue(true); // On affiche le chargement

        // Appel asynchrone au Repository
        repository.getSpotsByCity(city).thenAccept(resultList -> {
            // "postValue" permet de mettre à jour l'UI depuis un thread d'arrière-plan
            spots.postValue(resultList);
            isLoading.postValue(false);
        }).exceptionally(throwable -> {
            errorMessage.postValue("Erreur : " + throwable.getMessage());
            isLoading.postValue(false);
            return null;
        });
    }
}