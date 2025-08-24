package cz.uhk.zlesak.threejslearningapp.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForSelectRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class TextureAreaForComboBoxParser {

    public static List<TextureAreaForSelectRecord> csvParse(Map<String ,String> csvMap) {
        if(csvMap == null || csvMap.isEmpty()) {
            return List.of();
        }
        List<TextureAreaForSelectRecord> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : csvMap.entrySet()) {
            String[] rows = entry.getValue().split("\\r?\\n|\\r");
            for (String row : rows) {
                row = row.trim();
                if (row.isEmpty()) continue;
                String[] parts = row.split(";");
                if (parts.length == 2) {
                    result.add(new TextureAreaForSelectRecord(entry.getKey(), parts[0].trim(), parts[1].trim()));
                } else {
                    throw new IllegalArgumentException("Invalid CSV format for TextureAreaForComboBoxRecord: " + row);
                }
            }
        }
        return result;
    }
}
