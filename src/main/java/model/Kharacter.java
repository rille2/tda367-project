package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Kharacter {

    private String name;
    private HashMap<Stat, Integer> stats;

    Kharacter(HashMap<Stat, Integer> stats, String name) {
        this.stats = stats;
        this.name = name;
    }

    HashMap<Stat, Integer> getStats() {
        return stats;
    }

    int getStat(Stat stat) {
        return stats.get(stat);
    }

    /**
     * @return strings to represent stats in view
     */
    List<String> getStatsAsStrings() {
        List<String> statStrings = new ArrayList<>();
        statStrings.add("Speed: " + stats.get(Stat.SPEED));
        statStrings.add("Strength: " + stats.get(Stat.STRENGTH));
        statStrings.add("Stamina: " + stats.get(Stat.STAMINA));
        statStrings.add("Sanity: " + stats.get(Stat.SANITY));
        return statStrings;
    }

    /**
     * adds to Stat hashmaps together
     * @param statsToAdd for example stats of an item
     */
    public void updateStat(HashMap<Stat, Integer> statsToAdd) { //Jätteful metod i know
        for (Stat playerStat : stats.keySet()) {
            for (Stat statToAdd : statsToAdd.keySet()) {
                if (playerStat == statToAdd)
                    stats.put(playerStat, stats.get(playerStat) + statsToAdd.get(statToAdd));
            }
        }
    }

    void updateStatFromCombat(Stat stat, int damage) {
        stats.put(stat, stats.get(stat) - damage);
    }


    String getName() {
        return name;
    }
}

