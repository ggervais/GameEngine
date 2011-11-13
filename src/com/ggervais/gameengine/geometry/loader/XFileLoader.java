package com.ggervais.gameengine.geometry.loader;

import com.ggervais.gameengine.geometry.MeshGeometry;
import com.ggervais.gameengine.geometry.skinning.Bone;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.scene.Scene;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class XFileLoader extends GeometryLoader {

    private static final String MAGIC_STRING_TEXT_FORMAT = "xof 0303txt 0032";
    private static final Logger log = Logger.getLogger(XFileLoader.class);

    private static final Pattern HEADER_PATTERN = Pattern.compile(".*\\bheader\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile(".*\\btemplate\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern FRAME_PATTERN = Pattern.compile(".*\\bframe\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern FRAME_TRANSFORM_MATRIX_PATTERN = Pattern.compile(".*\\bframetransformmatrix\\b(.+)?\\b.*", Pattern.CASE_INSENSITIVE);

    // Right now, only text-based .x files are supported.
    public static Geometry loadFile(String filename) {

        Geometry loadedGeometry = new MeshGeometry();
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

                            Bone rootBone = handleFrame(reader, frameName, 0);
                            rootBone.logTree();

                            continue;
                        }

                        //System.out.println(line);
                    }
                }
            }


            reader.close();
        } catch (FileNotFoundException fnfe) {
            log.error(fnfe.getMessage());
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }

        File file = new File(filename);


        return loadedGeometry;
    }

    private static Bone handleFrame(BufferedReader reader, String name, int level) throws IOException {

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
                System.out.println(transform);

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
                Bone childBone = handleFrame(reader, frameName, level + 1);
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
        Matrix4x4 matrix = Matrix4x4.createFromFloatArray(matrixArray, true);
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
                if (level == 0) {
                    stopParsing = true;
                }
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
        XFileLoader.loadFile("assets/models/warrior.x");
        //XFileLoader.loadFile("assets/models/bouncy_thing.x");
    }
}
