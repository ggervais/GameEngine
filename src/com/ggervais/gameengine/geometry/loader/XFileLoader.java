package com.ggervais.gameengine.geometry.loader;

import com.ggervais.gameengine.geometry.MeshGeometry;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.geometry.skinning.Bone;
import com.ggervais.gameengine.geometry.skinning.SkinWeights;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class XFileLoader extends GeometryLoader {

    private static final String MAGIC_STRING_TEXT_FORMAT = "xof 0303txt 0032";
    private static final Logger log = Logger.getLogger(XFileLoader.class);

    private static final Pattern HEADER_PATTERN = Pattern.compile(".*\\bheader\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile(".*\\btemplate\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern FRAME_PATTERN = Pattern.compile(".*\\bframe\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern FRAME_TRANSFORM_MATRIX_PATTERN = Pattern.compile(".*\\bframetransformmatrix\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern MESH_PATTERN = Pattern.compile(".*\\bmesh\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern MESH_TEXTURE_COORDS_PATTERN = Pattern.compile(".*\\bmeshtexturecoords\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern MESH_MATERIAL_LIST_PATTERN = Pattern.compile(".*\\bmeshmateriallist\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern MESH_NORMALS_PATTERN = Pattern.compile(".*\\bmeshnormals\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern SKIN_WEIGHTS_PATTERN = Pattern.compile(".*\\bskinweights\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern X_SKIN_MESH_HEADER_PATTERN = Pattern.compile(".*\\bxskinmeshheader\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);

    private static final boolean COLUMN_MAJOR_MATRIX = true;

    // Right now, only text-based .x files are supported.
    public static Geometry loadFile(String filename) {

        Bone rootBone = null;
        List<MeshGeometry> encounteredGeometries =  new ArrayList<MeshGeometry>();
        try {
            File file = new File(filename);
            if (!file.exists()) {
                throw new FileNotFoundException("File \"" + filename + "\" not found!");
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            char[] header = new char[MAGIC_STRING_TEXT_FORMAT.length()];
            reader.read(header, 0, MAGIC_STRING_TEXT_FORMAT.length());

            String fileHeader = new String(header);

            boolean isValidFile = true;
            if (MAGIC_STRING_TEXT_FORMAT.equals(fileHeader)) {
                log.info("File \"" + filename + "\" can be processed!");
            } else {
                log.error("File \"" + filename + "\" can't be processed!");
                isValidFile = false;
            }

            if (isValidFile) {
                String line;
                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (lineShouldBeIgnored(line)) {
                        continue;
                    } else {

                        if (HEADER_PATTERN.matcher(line).matches()  || TEMPLATE_PATTERN.matcher(line).matches()) {
                            skipToNextEndOfBlock(reader);
                            log.info("Skipping the header or a template.");
                            continue;
                        }

                        Matcher frameMatcher = FRAME_PATTERN.matcher(line);
                        if (frameMatcher.matches()) {

                            String frameName = null;
                            try {
                                frameName = frameMatcher.group(1).trim();
                            } catch (Exception e) {}

                            if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                                skipToNextBeginningOfBlock(reader);
                            }

                            rootBone = handleFrame(reader, frameName, encounteredGeometries, 0);

                            continue;
                        }
                    }
                }
            }

            reader.close();
        } catch (FileNotFoundException fnfe) {
            log.error(fnfe.getMessage());
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }

        Map<Integer, Float> weightSums = new HashMap<Integer, Float>();
        for (Geometry geometry : encounteredGeometries) {

            geometry.setBoneHierarchyRoot(rootBone);

            for (SkinWeights skinWeights : geometry.getSkinWeightsList()) {
                String boneName = skinWeights.getBoneName();
                Bone foundBone = rootBone.findByName(boneName);
                if (foundBone != null) {
                    for (int index : skinWeights.getIndicesWeights().keySet()) {
                        float weight = skinWeights.getIndicesWeights().get(index);
                        foundBone.addVertexIndex(index);
                        foundBone.addWeight(weight);

                        if (!weightSums.containsKey(index)) {
                            weightSums.put(index, 0f);
                        }
                        float currentWeight = weightSums.get(index);
                        float sum = currentWeight + weight;
                        weightSums.put(index, sum);
                        log.info(String.format("For vertex %d, %f + %f = %f.", index, currentWeight, weight, sum));
                    }
                    foundBone.setSkinOffsetMatrix(skinWeights.getSkinOffsetMatrix());
                }
            }
        }
        log.info("======");
        for (int index : weightSums.keySet()) {
            log.info(String.format("For vertex %d, total weight is %f.", index, weightSums.get(index)));
        }

        rootBone.logTree();

        Geometry loadedGeometry = encounteredGeometries.get(0);
        if (loadedGeometry == null) {
            loadedGeometry = new MeshGeometry();
        }

        return loadedGeometry;
    }

    private static int readNextInteger(BufferedReader reader, String delimiter) throws IOException {
        String strInteger = readAllDataUntilNextOccurrenceOfString(reader, delimiter).trim();
        int integer = 0;
        try {
            integer = Integer.parseInt(strInteger);
        } catch (NumberFormatException nfe) {}

        return integer;
    }

    private static float readNextFloat(BufferedReader reader, String delimiter) throws IOException {
        String strFloat = readAllDataUntilNextOccurrenceOfString(reader, delimiter).trim();
        float floatValue = 0;
        try {
            floatValue = Float.parseFloat(strFloat);
        } catch (NumberFormatException nfe) {}

        return floatValue;
    }

    private static String readNextString(BufferedReader reader, String delimiter, boolean delimitedByQuotes) throws IOException {
        String string = readAllDataUntilNextOccurrenceOfString(reader, delimiter).trim();
        if (delimitedByQuotes) {
            string = string.replaceAll("^\"", "").replaceAll("\"$", "");
        }
        return string;
    }

    private static MeshGeometry handleMesh(BufferedReader reader, String name, Bone bone) throws IOException {
        MeshGeometry createdMesh = new MeshGeometry();

        log.info("handling mesh " + name);

        int nbVertices = readNextInteger(reader, ";");
        for (int i = 0; i < nbVertices; i++) {
            String endOfPosition = (i < nbVertices - 1 ? "," : ";;");
            String strVertex = readAllDataUntilNextOccurrenceOfString(reader, endOfPosition).trim();
            String[] vertexParts = strVertex.split(";");
            if (vertexParts.length == 3) {

                try {
                    float x = Float.parseFloat(vertexParts[0]);
                    float y = Float.parseFloat(vertexParts[1]);
                    float z = Float.parseFloat(vertexParts[2]);

                    Point3D position = new Point3D(x, y, z);
					Vertex vertex = new Vertex(position, Color.WHITE, 0, 0);

                    createdMesh.getVertexBuffer().addVertex(vertex);

                } catch (NumberFormatException nfe) {}
            }
        }

        int nbFaces = readNextInteger(reader, ";");
        log.info("Mesh has " + nbFaces + " faces.");
        for (int i = 0; i < nbFaces; i++) {
            int nbVerticesInFace = readNextInteger(reader, ";");

            String endOfPosition = (i < nbFaces - 1 ? ";," : ";;");
            String strVertex = readAllDataUntilNextOccurrenceOfString(reader, endOfPosition).trim();
            String[] vertexParts = strVertex.split(",");
            if (vertexParts.length == nbVerticesInFace) {

                for (int v = 0; v < nbVerticesInFace; v++) {
                    String vertexPart = vertexParts[v];
                    try {
                        int index = Integer.parseInt(vertexPart);
                        createdMesh.getIndexBuffer().addIndex(nbVerticesInFace, index);
                    } catch (NumberFormatException nfe) {}
                }
            }
        }

        log.info("Mesh has " + createdMesh.getVertexBuffer().getRealSize() + " vertices.");
        log.info("Mesh has " + createdMesh.getIndexBuffer().size() + " indices.");

        boolean stopParsing = false;
        String line;
        while(!stopParsing && (line = reader.readLine()) != null) {

            line = line.trim();

            if (lineShouldBeIgnored(line)) {
                continue;
            }

            if (MESH_TEXTURE_COORDS_PATTERN.matcher(line).matches()) {
                if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                    skipToNextBeginningOfBlock(reader);
                }
                handleTextureCoordinates(reader, createdMesh);
                log.info("Skipping MeshTextureCoords for now.");

                continue;
            }

             if (MESH_NORMALS_PATTERN.matcher(line).matches()) {
                if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                    skipToNextBeginningOfBlock(reader);
                }
                skipToCorrespondingEndOfBlock(reader);
                log.info("Skipping MeshNormals for now.");

                continue;
            }

            if (MESH_MATERIAL_LIST_PATTERN.matcher(line).matches()) {
                if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                    skipToNextBeginningOfBlock(reader);
                }
                skipToCorrespondingEndOfBlock(reader);
                log.info("Skipping MeshMaterialList for now.");

                continue;
            }

            if (X_SKIN_MESH_HEADER_PATTERN.matcher(line).matches()) {
                if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                    skipToNextBeginningOfBlock(reader);
                }
                skipToCorrespondingEndOfBlock(reader);
                log.info("Skipping XSkinMeshHeader for now.");

                continue;
            }

            if (SKIN_WEIGHTS_PATTERN.matcher(line).matches()) {
                if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                    skipToNextBeginningOfBlock(reader);
                }
                handleSkinWeights(reader, createdMesh);

                continue;
            }

            if (line.contains(Character.toString(XFileConstants.END_BLOCK))) {
                stopParsing = true;
            }

        }

        return createdMesh;
    }

    private static void handleTextureCoordinates(BufferedReader reader, MeshGeometry createdMesh) throws IOException {
        int nbVertices = readNextInteger(reader, ";");
        List<TextureCoords> coordsPerVertex = new ArrayList<TextureCoords>();

        for (int i = 0; i < nbVertices; i++) {
            String endOfPosition = (i < nbVertices - 1 ? ";," : ";;");
            String strVertex = readAllDataUntilNextOccurrenceOfString(reader, endOfPosition).trim();
            String[] textureParts = strVertex.split(";");
            if (textureParts.length == 2) {
                String strTU = textureParts[0];
                String strTV = textureParts[1];

                try {
                    float tu = Float.parseFloat(strTU);
                    float tv = Float.parseFloat(strTV);
                    coordsPerVertex.add(new TextureCoords(tu, tv));
                } catch (NumberFormatException nfe) {}
            }
        }

        Effect effect = new Effect();
        for (int nbVerticesPerFace : createdMesh.getIndexBuffer().getNbVerticesList()) {
            for (int vertexIndex : createdMesh.getIndexBuffer().getSubIndexBuffer(nbVerticesPerFace)) {
                TextureCoords coords = coordsPerVertex.get(vertexIndex);
                if (coords != null) {
                    effect.addTextureCoordinates(0, nbVerticesPerFace, coords);
                }
            }
        }
        createdMesh.setEffect(effect);
        skipToCorrespondingEndOfBlock(reader);
    }

    private static String readAllDataUntilNextOccurrenceOfString(BufferedReader reader, String string) throws IOException {

        StringBuilder finalStringBuilder = new StringBuilder();
        String finalString = "";
        boolean stopParsing = false;
        char[] buffer = new char[string.length()];
        int lastNumberOfCharactersRead = 0;
        while (!stopParsing && lastNumberOfCharactersRead != -1) {
            lastNumberOfCharactersRead = reader.read(buffer, 0, buffer.length);
            String resultingString = new String(buffer);
            finalStringBuilder.append(resultingString);
            if (finalStringBuilder.toString().contains(string)) {
                stopParsing = true;
                finalString = finalStringBuilder.toString().replace(string, "");
            }
        }
        return finalString;
    }

    private static void handleSkinWeights(BufferedReader reader, MeshGeometry createdMesh) throws IOException {

        String affectedBoneName = readNextString(reader, ";", true);

        SkinWeights skinWeights = new SkinWeights();
        skinWeights.setBoneName(affectedBoneName);

        List<Integer> indices = new ArrayList<Integer>();
        List<Float> weights = new ArrayList<Float>();
        int nbIndicesAndWeights = readNextInteger(reader, ";");
        for (int i = 0; i < nbIndicesAndWeights; i++) {
            String delimiter = (i < nbIndicesAndWeights - 1 ? "," : ";");
            int index = readNextInteger(reader, delimiter);
            indices.add(index);
        }
        for (int i = 0; i < nbIndicesAndWeights; i++) {
            String delimiter = (i < nbIndicesAndWeights - 1 ? "," : ";");
            float weight = readNextFloat(reader, delimiter);
            weights.add(weight);
        }
        for (int i = 0; i < nbIndicesAndWeights; i++) {
            int index = indices.get(i);
            float weight = weights.get(i);
            skinWeights.getIndicesWeights().put(index, weight);
        }

        float[] matrixArray = new float[16];
        int lastIndexProcessed = 0;
        boolean stopParsing = false;
        String line;
        while(!stopParsing && (line = reader.readLine()) != null) {
            line = line.trim();
            if (lineShouldBeIgnored(line)) {
                continue;
            }

            String[] parts = line.split(Character.toString(XFileConstants.LIST_DELIMITER));
            for (String part : parts) {
                try {
                    float element = Float.parseFloat(part.replace(Character.toString(XFileConstants.LIST_DELIMITER), "").replace(Character.toString(XFileConstants.LIST_END), ""));
                    matrixArray[lastIndexProcessed] = element;
                    lastIndexProcessed++;
                } catch (NumberFormatException nfe) {}
            }

            if (line.contains(Character.toString(XFileConstants.LIST_END))) {
                stopParsing = true;
            }
        }


        skinWeights.setSkinOffsetMatrix(Matrix4x4.createFromFloatArray(matrixArray, COLUMN_MAJOR_MATRIX));

        createdMesh.getSkinWeightsList().add(skinWeights);

        skipToCorrespondingEndOfBlock(reader);

    }

    private static Bone handleFrame(BufferedReader reader, String name, List<MeshGeometry> geometries, int level) throws IOException {

        Bone bone = new Bone();

        if (name != null) {
            bone.setName(name);
        }

        StringBuilder levelIndicator = new StringBuilder();
        for (int i = 0; i < level; i++) {
            levelIndicator.append("#");
        }
        if (levelIndicator.length() > 0) {
            levelIndicator.append(" ");
        }
        log.info(String.format("%sHandling frame %s", levelIndicator, name));

        boolean stopParsing = false;
        String line;
        while(!stopParsing && (line = reader.readLine()) != null) {

            line = line.trim();

            if (lineShouldBeIgnored(line)) {
                continue;
            }

            if (FRAME_TRANSFORM_MATRIX_PATTERN.matcher(line).matches()) {
                if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                    skipToNextBeginningOfBlock(reader);
                }
                Matrix4x4 transform = handleFrameTransformationMatrix(reader);
                bone.setTransformMatrix(transform);

                continue;
            }

            Matcher meshMatcher = MESH_PATTERN.matcher(line);
            if (meshMatcher.matches()) {

                String meshName = null;
                try {
                    meshName = meshMatcher.group(1).trim();
                } catch (Exception e) {}

                if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                    skipToNextBeginningOfBlock(reader);
                }
                MeshGeometry meshGeometry = handleMesh(reader, meshName, bone);
                geometries.add(meshGeometry);
                continue;
            }

            Matcher frameMatcher = FRAME_PATTERN.matcher(line);
            if (frameMatcher.matches()) {

                String frameName = null;
                try {
                    frameName = frameMatcher.group(1).trim();
                } catch (Exception e) {}

                if (!line.contains(Character.toString(XFileConstants.BEGIN_BLOCK))) {
                    skipToNextBeginningOfBlock(reader);
                }
                Bone childBone = handleFrame(reader, frameName, geometries, level + 1);
                bone.addChild(childBone);
            }
            if (line.contains(Character.toString(XFileConstants.END_BLOCK))) {
                stopParsing = true;
            }
        }

        return bone;
    }

    private static Matrix4x4 handleFrameTransformationMatrix(BufferedReader reader) throws IOException {

        float[] matrixArray = new float[16];
        int lastIndexProcessed = 0;
        boolean stopParsing = false;
        String line;
        while(!stopParsing && (line = reader.readLine()) != null) {
            line = line.trim();
            if (lineShouldBeIgnored(line)) {
                continue;
            }

            String[] parts = line.split(Character.toString(XFileConstants.LIST_DELIMITER));
            for (String part : parts) {
                try {
                    float element = Float.parseFloat(part.replace(Character.toString(XFileConstants.LIST_DELIMITER), "").replace(Character.toString(XFileConstants.LIST_END), ""));
                    matrixArray[lastIndexProcessed] = element;
                    lastIndexProcessed++;
                } catch (NumberFormatException nfe) {}
            }

            if (line.contains(Character.toString(XFileConstants.LIST_END))) {
                stopParsing = true;
                if (!line.contains(Character.toString(XFileConstants.END_BLOCK))) {
                    skipToNextEndOfBlock(reader);
                }
            }
        }


        // Here, we assume that the matrix is column major, but it is not documented, so this assumption is based
        // on what matrices look like in sample .x files.
        Matrix4x4 matrix = Matrix4x4.createFromFloatArray(matrixArray, COLUMN_MAJOR_MATRIX);
        return matrix;
    }


    private static void skipToCorrespondingEndOfBlock(BufferedReader reader) throws IOException {
        int character = 0;
        int level = 0;
        boolean stopParsing = false;
        while (character != -1 && !stopParsing) {
            if (((char) character) == XFileConstants.END_BLOCK && level == 0) {
                stopParsing = true;
            } else if (((char) character) == XFileConstants.BEGIN_BLOCK) {
                level++;
            } else if (((char) character) == XFileConstants.END_BLOCK) {
                level--;
            }
            character = reader.read();
        }
    }

    private static void skipToNextEndOfBlock(BufferedReader reader) throws IOException {
        int character = 0;
        while ( ((char) character) != XFileConstants.END_BLOCK && character != -1) {
            character = reader.read();
        }
    }

    private static void skipToNextBeginningOfBlock(BufferedReader reader) throws IOException {
        int character = 0;
        while ( ((char) character) != XFileConstants.BEGIN_BLOCK && character != -1) {
            character = reader.read();
        }
    }

    private static boolean lineShouldBeIgnored(String line) {
        boolean shouldBeIgnored = false;
        line = line.trim();
        if (line.startsWith(XFileConstants.SLASH_COMMENT) || line.startsWith(XFileConstants.POUND_COMMENT)) {
            shouldBeIgnored = true;
        } else if (line.isEmpty()) {
            shouldBeIgnored = true;
        } else if (line.matches("^\\s*$")) {
            shouldBeIgnored = true;
        }
        return shouldBeIgnored;  //To change body of created methods use File | Settings | File Templates.
    }

    public static void main(String[] args) {
        //XFileLoader.loadFile("assets/models/warrior.x");
        XFileLoader.loadFile("assets/models/bouncy_thing.x");
    }
}
