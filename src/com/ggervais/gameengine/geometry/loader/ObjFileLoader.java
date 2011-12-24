package com.ggervais.gameengine.geometry.loader;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.ggervais.gameengine.geometry.SkinnedMeshGeometry;
import com.ggervais.gameengine.geometry.Model;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.material.Material;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.resource.ResourceSubsystem;
import com.ggervais.gameengine.resource.ResourceType;
import com.ggervais.gameengine.scene.DisplayableEntity;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.material.texture.TextureLoader;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import org.apache.log4j.Logger;

public class ObjFileLoader extends GeometryLoader {

    private static final Logger log = Logger.getLogger(ObjFileLoader.class);

	private String filename;
	
	public ObjFileLoader(String filename) {
		this.filename = filename;
	}

    private static Material loadMaterialFile(String filename) {

        File file = new File(filename);
        Material material = new Material();

        log.info("Loading material file '" + filename + "'");

        try {

            FileInputStream in = new FileInputStream("assets/models/" + file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                StringTokenizer tokenizer = new StringTokenizer(line);
                int nbTokens = tokenizer.countTokens();
                if (nbTokens >= 1) {
                    String type = tokenizer.nextToken();
                    if (type.equals("newmtl") && nbTokens >= 2) {
                        String materialName = tokenizer.nextToken();
                        material.setName(materialName);
                    } else if (type.equals("map_Kd") && nbTokens >= 2) {
                        String textureFilename = tokenizer.nextToken();
                        Texture texture = TextureLoader.loadTexture("assets/textures/" + textureFilename);
                        material.addTexture(texture);
                    }
                }


            }
            reader.close();

            if (material.getName().length() == 0) {
                material.setName(filename);
                log.warn("Material has no name, using filename '" + filename + "'");
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (material != null) {
            ResourceSubsystem.getInstance().addResource(material);
        }

        return material;
    }

	private static Texture loadTextureFromMaterialFile(String filename) {
		File file = new File(filename);
		Texture texture = null;
		
		try {
			FileInputStream in = new FileInputStream("assets/models/" + file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
			while((line = reader.readLine()) != null) {
				if (line.equals("")) {
					continue;
				}
				StringTokenizer tokenizer = new StringTokenizer(line);
				int nbTokens = tokenizer.countTokens();
				if (nbTokens >= 1) {
					String type = tokenizer.nextToken();
					if (type.equals("map_Kd") && nbTokens >= 2) {
						String textureFilename = tokenizer.nextToken();
						texture = TextureLoader.loadTexture("assets/textures/" + textureFilename);
					}
				}
			}
            reader.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return texture;
	}
	
	public static Geometry loadFile(String filename) {
		File file = new File(filename);
		
		System.out.print("Loading " + filename + "... ");
		
		Model model = new Model();
        SkinnedMeshGeometry geometry = new SkinnedMeshGeometry();
        geometry.setGeometryDirty(true);
		Texture texture = null;

        Material material = null;

        List<Point3D> positions = new ArrayList<Point3D>();
		List<TextureCoords> coordsList = new ArrayList<TextureCoords>();
		Map<Integer, List<IndexTexture>> indexTextures = new HashMap<Integer, List<IndexTexture>>();
        Map<Integer, List<Integer>> textureCoordsIndices = new HashMap<Integer, List<Integer>>();
        Map<Integer, Integer> coordsMap = new HashMap<Integer, Integer>();
        Map<Integer, List<Integer>> coordsListMap = new HashMap<Integer, List<Integer>>();
        Map<Integer, List<Integer>> rawIndexMap = new HashMap<Integer, List<Integer>>();

		try {
			FileInputStream in = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = reader.readLine();
			while(line != null) {

				StringTokenizer tokenizer = new StringTokenizer(line);
				int nbTokens = tokenizer.countTokens();

				if (nbTokens <= 1) {
					line = reader.readLine();
					continue;
				}

				String elementType = tokenizer.nextToken();
				if (elementType.equals("v") && nbTokens >= 4) {

					String strX = tokenizer.nextToken();
					String strY = tokenizer.nextToken();
					String strZ = tokenizer.nextToken();

					float x = Float.parseFloat(strX);
					float y = Float.parseFloat(strY);
					float z = Float.parseFloat(strZ);

					Point3D position = new Point3D(x, y, z);
					Vertex vertex = new Vertex(position, Color.WHITE, 0, 0);
					positions.add(position);
					model.getVertexBuffer().addVertex(vertex);

                    //geometry.getVertexBuffer().addVertex(vertex);

				} else if (elementType.equals("f") && nbTokens >= 4) {

                    String[] indices = new String[nbTokens - 1];
                    int i = 0;
                    while(tokenizer.hasMoreTokens()) {
                        indices[i] = tokenizer.nextToken();
                        i++;
                    }

                    int[] indexArray = new int[nbTokens - 1];
                    int nbVerticesPerFace = indices.length;

                    if (!indexTextures.containsKey(nbVerticesPerFace)) {
                        indexTextures.put(nbVerticesPerFace, new ArrayList<IndexTexture>());
                    }
                    
					for (int index = 0; index < indices.length; index++) {
                        String strIndex = indices[index];
                        String[] parts = strIndex.split("/");
                        int v = Integer.parseInt(parts[0]) - 1;
                        if (parts.length >= 2) {
                            int t = Integer.parseInt(parts[1]) - 1;
                            if (!textureCoordsIndices.containsKey(nbVerticesPerFace)) {
                                textureCoordsIndices.put(nbVerticesPerFace, new ArrayList<Integer>());
                            }
                            textureCoordsIndices.get(nbVerticesPerFace).add(t);
                            coordsMap.put(v, t);
                            if (!coordsListMap.containsKey(v)) {
                                coordsListMap.put(v, new ArrayList<Integer>());
                            }
                            if (!coordsListMap.get(v).contains(t)) {
                                coordsListMap.get(v).add(t);
                            }
                            
                            
                            indexTextures.get(nbVerticesPerFace).add(new IndexTexture(v, t));
                        }

                        if (!rawIndexMap.containsKey(nbVerticesPerFace)) {
                            rawIndexMap.put(nbVerticesPerFace, new ArrayList<Integer>());
                        }
                        rawIndexMap.get(nbVerticesPerFace).add(v);

                        indexArray[index] = v;
                    }

                    /*
                    if (indexArray.length == 3) {
                        geometry.getIndexBuffer().addIndex(3, indexArray[0]);
                        geometry.getIndexBuffer().addIndex(3, indexArray[1]);
                        geometry.getIndexBuffer().addIndex(3, indexArray[2]);
                    } else if (indexArray.length == 4) {
                        geometry.getIndexBuffer().addIndex(4, indexArray[0]);
                        geometry.getIndexBuffer().addIndex(4, indexArray[1]);
                        geometry.getIndexBuffer().addIndex(4, indexArray[2]);
                        geometry.getIndexBuffer().addIndex(4, indexArray[3]);
                        //geometry.getIndexBuffer().addIndex(3, indexArray[3]);
                        //geometry.getIndexBuffer().addIndex(3, indexArray[0]);
                    }*/

				} else if (elementType.equals("vt") && (nbTokens == 3 || nbTokens == 4)) {
					String strU = tokenizer.nextToken();
					String strV = tokenizer.nextToken();
					//String strW = tokenizer.nextToken();

					float u = Float.parseFloat(strU);
					float v = Float.parseFloat(strV);
					//float w = Float.parseFloat(strW);

					TextureCoords coords = new TextureCoords(u, 1 - v);
					coordsList.add(coords);
				} else if (elementType.equals("mtllib") && nbTokens >= 2) {
					String materialFilename = tokenizer.nextToken();
                    loadMaterialFile(materialFilename);
                } else if (elementType.equals("usemtl") && nbTokens >= 2) {
                    String materialName = tokenizer.nextToken();
                    material = (Material) ResourceSubsystem.getInstance().findResourceByTypeAndName(ResourceType.MATERIAL, materialName);

                }

				line = reader.readLine();
			}

            List<VertexDuplicationData> duplicationDatas = new ArrayList<VertexDuplicationData>();
            List<Integer> keys = new ArrayList<Integer>(coordsListMap.keySet());
            Collections.sort(keys);
            int nbNewVertices = 0;
            int nbProblematicVertices = 0;
            int nbGoodVertices = 0;
            for (Integer vertexIndex : keys) {
                List<Integer> coordList = coordsListMap.get(vertexIndex);
                if (coordList.size() > 1) {
                    nbNewVertices += (coordList.size() - 1);
                    VertexDuplicationData vdd = new VertexDuplicationData();
                    
                    vdd.setOriginalVertexIndex(vertexIndex);
                    vdd.putReplacement(coordList.get(0), vertexIndex);
                    
                    for (int replacementIndex = 1; replacementIndex <= (coordList.size() - 1); replacementIndex++) {
                        Point3D originalPosition = positions.get(vertexIndex);
                        positions.add(originalPosition);
                        vdd.putReplacement(coordList.get(replacementIndex), positions.size() - 1);
                    }
                    duplicationDatas.add(vdd);
                    nbProblematicVertices++;
                } else {
                    nbGoodVertices++;
                }
            }

            for (VertexDuplicationData vdd : duplicationDatas) {
                for (int nbVerticesPerFace : indexTextures.keySet()) {
                    List<IndexTexture> listOfIndexTextures = indexTextures.get(nbVerticesPerFace);
                    for (IndexTexture indexTexture : listOfIndexTextures) {
                        if (vdd.getOriginalVertexIndex() == indexTexture.vertexIndex) {
                            indexTexture.vertexIndex = vdd.getReplacement(indexTexture.textureIndex);
                        }
                    }
                }
            }

            log.info(nbProblematicVertices + " prob. vert., " + nbGoodVertices + ", we need to create " + nbNewVertices + "new vertices.");

            for (Point3D position : positions) {
                geometry.getVertexBuffer().addVertex(new Vertex(position, Color.WHITE, 0, 0));
            }

            Effect effect = new Effect();
            int sum = 0;
            for (int nbVerticesPerFace : indexTextures.keySet()) {
                sum += indexTextures.get(nbVerticesPerFace).size();
            }
            if (sum > 0) {
                for (int nbVerticesPerFace : indexTextures.keySet()) {
                    List<IndexTexture> listOfIndexTextures = indexTextures.get(nbVerticesPerFace);
                    for (IndexTexture indexTexture : listOfIndexTextures) {
                        geometry.getIndexBuffer().addIndex(nbVerticesPerFace, indexTexture.vertexIndex);
                        effect.addTextureCoordinatesForVertex(0, indexTexture.vertexIndex, coordsList.get(indexTexture.textureIndex));
                    }
                }
            } else {
                for (int nbVerticesPerFace : rawIndexMap.keySet()) {
                    for (int index : rawIndexMap.get(nbVerticesPerFace)) {
                        geometry.getIndexBuffer().addIndex(nbVerticesPerFace, index);
                    }
                }
            }

            //Effect effect = new Effect();
			for (int nbVerticesPerFace : textureCoordsIndices.keySet()) {
                for (int i : textureCoordsIndices.get(nbVerticesPerFace)) {
                    TextureCoords coords = coordsList.get(i);
                    //effect.addTextureCoordinatesForVertex(0, nbVerticesPerFace, coords);
                    model.getTextureBuffer().addCoords(coords);
                }
			}

            for (int vertexIndex : coordsMap.keySet()) {
                TextureCoords coords = coordsList.get(coordsMap.get(vertexIndex));
                //effect.addTextureCoordinatesForVertex(vertexIndex, coords);
            }

            log.info("Effect uses tex coords per vertices: " + effect.hasTexturesCoordsPerVertex());
            log.info("Effect has nb. vertices: " + geometry.getVertexBuffer().getRealSize() + " and nb. tex. coords: " + effect.getNbTextureCoordinatesForVertex(0));
			geometry.setEffect(effect);
			reader.close();
			in.close();
		} catch (FileNotFoundException fnfe) {
			// Throw an exception.
		} catch (IOException ioe) {
			// Throw an exception.
		}
		
		log.info("done!");
		
		DisplayableEntity entity = new DisplayableEntity(model);
		if (texture != null) {
			entity.setTexture(texture);
		}
        if (material != null) {
            entity.setMaterial(material);
            geometry.getEffect().addTexture(material.getTexture(0));
        }

		return geometry;
	}
}