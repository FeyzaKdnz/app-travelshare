package com.example.app_travelpath.domain;

import com.example.app_travelpath.model.CategoryType;
import com.example.app_travelpath.model.EffortLevel;
import com.example.app_travelpath.model.Spot;

import java.util.ArrayList;
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

        List<Spot> poolCulture = new ArrayList<>();
        List<Spot> poolFood = new ArrayList<>();
        List<Spot> poolLeisure = new ArrayList<>();

        for (Spot s : allSpots) {
            if (s.getPrice() > maxBudget) continue;

            if (s.getCategoryType() == CategoryType.CULTURE && wantCulture) {
                poolCulture.add(s);
            } else if (s.getCategoryType() == CategoryType.FOOD && wantFood) {
                poolFood.add(s);
            } else if (s.getCategoryType() == CategoryType.LEISURE && wantLeisure) {
                poolLeisure.add(s);
            }
        }

        if (poolCulture.isEmpty() && poolFood.isEmpty() && poolLeisure.isEmpty()) {
            return finalRoute;
        }

        Spot currentSpot = null;
        if (!poolCulture.isEmpty()) {
            currentSpot = poolCulture.remove(0);
        } else if (!poolLeisure.isEmpty()) {
            currentSpot = poolLeisure.remove(0);
        } else if (!poolFood.isEmpty()) {
            currentSpot = poolFood.remove(0);
        }

        if (currentSpot == null) return finalRoute;

        double currentCost = currentSpot.getPrice();
        double currentDuration = currentSpot.getDuration();
        finalRoute.add(currentSpot);

        boolean searchContinues = true;
        while (searchContinues && currentDuration < maxDurationHours) {
            searchContinues = false;

            List<List<Spot>> candidatePools = new ArrayList<>();
            if (currentSpot.getCategoryType() != CategoryType.CULTURE && !poolCulture.isEmpty()) candidatePools.add(poolCulture);
            if (currentSpot.getCategoryType() != CategoryType.LEISURE && !poolLeisure.isEmpty()) candidatePools.add(poolLeisure);
            if (currentSpot.getCategoryType() != CategoryType.FOOD && !poolFood.isEmpty()) candidatePools.add(poolFood);
            if (candidatePools.isEmpty()) {
                if (!poolCulture.isEmpty()) candidatePools.add(poolCulture);
                if (!poolLeisure.isEmpty()) candidatePools.add(poolLeisure);
                if (!poolFood.isEmpty()) candidatePools.add(poolFood);
            }

            if (candidatePools.isEmpty()) break;

            Spot bestNextSpot = null;
            double minDistance = Double.MAX_VALUE;
            int bestPoolIndex = -1;

            for (int i = 0; i < candidatePools.size(); i++) {
                List<Spot> pool = candidatePools.get(i);
                for (Spot candidate : pool) {
                    double dist = calculateDistance(currentSpot, candidate);
                    if (dist < minDistance) {
                        if (isValid(candidate, currentCost, currentDuration, maxBudget, maxDurationHours)) {
                            minDistance = dist;
                            bestNextSpot = candidate;
                            bestPoolIndex = i;
                        }
                    }
                }
            }

            if (bestNextSpot != null) {
                finalRoute.add(bestNextSpot);
                currentCost += bestNextSpot.getPrice();
                currentDuration += bestNextSpot.getDuration();
                candidatePools.get(bestPoolIndex).remove(bestNextSpot);
                
                currentSpot = bestNextSpot;
                searchContinues = true;
            }
        }

        return finalRoute;
    }

    private boolean isValid(Spot s, double currentCost, double currentDuration, double maxBudget, double maxDuration) {
        if (currentCost + s.getPrice() > maxBudget) return false;
        if (currentDuration + s.getDuration() > maxDuration + 0.5) return false;
        return true;
    }


    private double calculateDistance(Spot s1, Spot s2) {
        double lat1 = s1.getLatitude();
        double lon1 = s1.getLongitude();
        double lat2 = s2.getLatitude();
        double lon2 = s2.getLongitude();

        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }
}
