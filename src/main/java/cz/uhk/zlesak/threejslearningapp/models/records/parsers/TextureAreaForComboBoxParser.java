package cz.uhk.zlesak.threejslearningapp.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForComboBoxRecord;

import java.util.List;

public abstract class TextureAreaForComboBoxParser {

    public static List<TextureAreaForComboBoxRecord> csvParse(String csv){
        return List.of(csv.split(";")).stream()
                .map(line -> {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        return new TextureAreaForComboBoxRecord(parts[0].trim(), parts[1].trim());
                    } else {
                        throw new IllegalArgumentException("Invalid CSV format for TextureAreaForComboBoxRecord: " + line);
                    }
                })
                .toList();
    }
}
