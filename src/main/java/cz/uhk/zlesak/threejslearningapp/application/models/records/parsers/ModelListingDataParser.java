package cz.uhk.zlesak.threejslearningapp.application.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.ModelForSelectRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ModelListingDataParser {

    /**
     * Parses model data for selection lists, prioritizing the 'main' model if present.
     *
     * @param models Map of model keys to QuickModelEntity objects
     * @return List of ModelForSelectRecord objects for selection
     */
    public static List<ModelForSelectRecord> modelForSelectDataParser(Map<String, QuickModelEntity> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        if (models.containsKey("main")) {
            ModelForSelectRecord mainModelRecord = new ModelForSelectRecord(
                    models.get("main").getModel().getId(),
                    "main",
                    models.get("main").getModel().getName()
            );
            List<ModelForSelectRecord> otherModels = models.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("main"))
                    .map(entry -> new ModelForSelectRecord(
                            entry.getValue().getModel().getId(),
                            entry.getKey(),
                            entry.getValue().getModel().getName()))
                    .collect(Collectors.toCollection(LinkedList::new));
            otherModels.addFirst(mainModelRecord);
            return otherModels;
        } else {
            return models.entrySet().stream()
                    .map(entry -> new ModelForSelectRecord(
                            entry.getValue().getModel().getId(),
                            entry.getKey(),
                            entry.getValue().getModel().getName()))
                    .toList();
        }
    }
}
