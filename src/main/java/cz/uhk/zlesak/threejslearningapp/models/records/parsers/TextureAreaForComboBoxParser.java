package cz.uhk.zlesak.threejslearningapp.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForSelectRecord;

import java.util.List;
//TODO WIP - wait for the BE side to provide the CSV for textures
public abstract class TextureAreaForComboBoxParser {

    public static List<TextureAreaForSelectRecord> csvParse(String csv){
        return List.of(csv.split(";")).stream()
                .map(line -> {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        return new TextureAreaForSelectRecord(parts[0].trim(), parts[1].trim());
                    } else {
                        throw new IllegalArgumentException("Invalid CSV format for TextureAreaForComboBoxRecord: " + line);
                    }
                })
                .toList();
    }
}
