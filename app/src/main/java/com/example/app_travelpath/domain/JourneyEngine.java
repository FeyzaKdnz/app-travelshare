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
        List<Spot> cultureSpots = new ArrayList<>();
        List<Spot> foodSpots = new ArrayList<>();
        List<Spot> leisureSpots = new ArrayList<>();

        for (Spot s : allSpots) {
            if (s.getPrice() > maxBudget) continue;

            if (s.getCategoryType() == CategoryType.CULTURE && wantCulture) {
                cultureSpots.add(s);
            } else if (s.getCategoryType() == CategoryType.FOOD && wantFood) {
                foodSpots.add(s);
            } else if (s.getCategoryType() == CategoryType.LEISURE && wantLeisure) {
                leisureSpots.add(s);
            }
        }

        Collections.shuffle(cultureSpots);
        Collections.shuffle(foodSpots);
        Collections.shuffle(leisureSpots);

        double currentCost = 0;
        double currentDuration = 0;
        boolean addedSomething = true;

        while (addedSomething && currentDuration < maxDurationHours) {
            addedSomething = false;

            if (!cultureSpots.isEmpty()) {
                Spot s = cultureSpots.remove(0);
                if (isValid(s, currentCost, currentDuration, maxBudget, maxDurationHours)) {
                    finalRoute.add(s);
                    currentCost += s.getPrice();
                    currentDuration += s.getDuration();
                    addedSomething = true;
                }
            }

            if (!leisureSpots.isEmpty() && currentDuration < maxDurationHours) {
                Spot s = leisureSpots.remove(0);
                if (isValid(s, currentCost, currentDuration, maxBudget, maxDurationHours)) {
                    finalRoute.add(s);
                    currentCost += s.getPrice();
                    currentDuration += s.getDuration();
                    addedSomething = true;
                }
            }

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

        return finalRoute;
    }

    private boolean isValid(Spot s, double currentCost, double currentDuration, double maxBudget, double maxDuration) {
        if (currentCost + s.getPrice() > maxBudget) return false;
        if (currentDuration + s.getDuration() > maxDuration + 0.5) return false;
        return true;
    }
}