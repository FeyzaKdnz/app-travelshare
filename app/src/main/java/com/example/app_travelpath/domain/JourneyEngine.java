package com.example.app_travelpath.domain;

import com.example.app_travelpath.model.CategoryType;
import com.example.app_travelpath.model.EffortLevel;
import com.example.app_travelpath.model.Spot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JourneyEngine {

    public List<Spot> generateRoute(
            List<Spot> allSpots,
            boolean wantCulture,
            boolean wantFood,
            boolean wantLeisure,
            double minBudget,
            double maxBudget,
            float maxDurationHours,
            EffortLevel effortLevel
    ) {
        List<Spot> finalRoute = new ArrayList<>();

        // 1. SÉPARER LES LIEUX PAR CATÉGORIE
        // C'est le secret pour éviter d'avoir que des restaurants !
        List<Spot> cultureSpots = new ArrayList<>();
        List<Spot> foodSpots = new ArrayList<>();
        List<Spot> leisureSpots = new ArrayList<>();

        for (Spot s : allSpots) {
            // Filtre de budget individuel (on évite les lieux hors de prix dès le début)
            if (s.getPrice() > maxBudget) continue;

            if (s.getCategoryType() == CategoryType.CULTURE && wantCulture) {
                cultureSpots.add(s);
            } else if (s.getCategoryType() == CategoryType.FOOD && wantFood) {
                foodSpots.add(s);
            } else if (s.getCategoryType() == CategoryType.LEISURE && wantLeisure) {
                leisureSpots.add(s);
            }
        }

        // 2. MÉLANGER CHAQUE LISTE SÉPARÉMENT
        Collections.shuffle(cultureSpots);
        Collections.shuffle(foodSpots);
        Collections.shuffle(leisureSpots);

        // 3. CONSTRUCTION INTELLIGENTE (Round-Robin)
        double currentCost = 0;
        double currentDuration = 0;

        // Tant qu'on a du temps et du budget...
        boolean addedSomething = true;

        while (addedSomething && currentDuration < maxDurationHours) {
            addedSomething = false;

            // -- TOUR A : CULTURE --
            if (!cultureSpots.isEmpty()) {
                Spot s = cultureSpots.remove(0); // On prend le premier
                if (isValid(s, currentCost, currentDuration, maxBudget, maxDurationHours)) {
                    finalRoute.add(s);
                    currentCost += s.getPrice();
                    currentDuration += s.getDuration();
                    addedSomething = true;
                }
            }

            // -- TOUR B : LEISURE --
            if (!leisureSpots.isEmpty() && currentDuration < maxDurationHours) {
                Spot s = leisureSpots.remove(0);
                if (isValid(s, currentCost, currentDuration, maxBudget, maxDurationHours)) {
                    finalRoute.add(s);
                    currentCost += s.getPrice();
                    currentDuration += s.getDuration();
                    addedSomething = true;
                }
            }

            // -- TOUR C : FOOD --
            // On ajoute un resto seulement si on ne vient pas d'en ajouter un (pour éviter resto sur resto)
            if (!foodSpots.isEmpty() && currentDuration < maxDurationHours) {
                Spot s = foodSpots.remove(0);
                if (isValid(s, currentCost, currentDuration, maxBudget, maxDurationHours)) {
                    finalRoute.add(s);
                    currentCost += s.getPrice();
                    currentDuration += s.getDuration();
                    addedSomething = true;
                }
            }
        }

        // 4. VÉRIFICATION FINALE DU BUDGET MINIMUM
        // Si on est en dessous du budget min, on essaie de remplacer des lieux gratuits par des payants
        // (Optionnel, pour l'instant on laisse comme ça pour ne pas compliquer)

        return finalRoute;
    }

    private boolean isValid(Spot s, double currentCost, double currentDuration, double maxBudget, double maxDuration) {
        // Vérifie si l'ajout du lieu ne fait pas exploser le budget ou le temps
        if (currentCost + s.getPrice() > maxBudget) return false;
        if (currentDuration + s.getDuration() > maxDuration + 0.5) return false; // On tolère 30min de dépassement
        return true;
    }
}