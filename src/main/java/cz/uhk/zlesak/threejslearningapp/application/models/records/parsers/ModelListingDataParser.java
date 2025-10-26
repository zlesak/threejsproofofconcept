package cz.uhk.zlesak.threejslearningapp.application.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.ModelForSelectRecord;

import java.util.List;
import java.util.Map;
public abstract class ModelListingDataParser {

    public static List<ModelForSelectRecord> modelForSelectDataParser (Map<String, QuickModelEntity> models) {
        if(models == null || models.isEmpty()) {
            return List.of();
        }

        return models.entrySet().stream()
                .map(entry -> new ModelForSelectRecord(
                        entry.getValue().getModel().getId(),
                        entry.getKey(),
                        entry.getValue().getModel().getName()))
                .toList();
    }
}
