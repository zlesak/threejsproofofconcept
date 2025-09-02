package cz.uhk.zlesak.threejslearningapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ModelInputStreamController provides frontend endpoint for streaming 3D model and texture files.
 * It uses ModelController and TextureController to fetch the files and serves them with appropriate headers.
 */
@RestController
public class ModelInputStreamController {

    @Autowired
    private ModelController modelController;
    @Autowired
    private TextureController textureController;

    /**
     * Streams the 3D model file based on the provided ID.
     * If 'advanced' parameter is true, serves the model as .obj file; otherwise, serves as .glb file.
     * @param id the ID of the model to stream
     * @param advanced flag to determine the file format (.obj or .glb)
     * @return ResponseEntity containing the model file as a Resource
     */
    @GetMapping("/api/model/{id}/stream")
    public ResponseEntity<Resource> streamModel(@PathVariable String id, @RequestParam(required = false, defaultValue = "false") boolean advanced) {
        try {
            Resource resource = modelController.getInputStream(id);
            String modelName = modelController.getModelName(id);
            String headerValue = advanced ? "attachment; filename=\"" + modelName + ".obj\"" : "attachment; filename=\"" + modelName + ".glb\"";
            MediaType contentType = advanced ? MediaType.TEXT_PLAIN : MediaType.parseMediaType("model/gltf-binary");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Streams the texture file based on the provided ID.
     * @param id the ID of the texture to stream
     * @return ResponseEntity containing the texture file as a Resource
     */
    @GetMapping("/api/texture/{id}/stream")
    public ResponseEntity<Resource> streamTexture(@PathVariable String id) {
        try {
            Resource resource = textureController.getInputStream(id);
            String textureName = textureController.getTextureName(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + textureName + ".jpg\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
