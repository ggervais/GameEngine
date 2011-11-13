package com.ggervais.gameengine.geometry.loader;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import com.ggervais.gameengine.geometry.MeshGeometry;
import com.ggervais.gameengine.geometry.Model;
import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.material.Material;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.resource.ResourceSubsystem;
import com.ggervais.gameengine.resource.ResourceType;
import com.ggervais.gameengine.scene.DisplayableEntity;
import com.ggervais.gameengine.scene.Scene;
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
        MeshGeometry geometry = new MeshGeometry();
		Texture texture = null;

        Material material = null;

		List<Vertex> vertices = new ArrayList<Vertex>();
		List<TextureCoords> coordsList = new ArrayList<TextureCoords>();
		Map<Integer, List<Integer>> textureCoordsIndices = new HashMap<Integer, List<Integer>>();
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
					vertices.add(vertex);
					model.getVertexBuffer().addVertex(vertex);

                    geometry.getVertexBuffer().addVertex(vertex);

				} else if (elementType.equals("f") && nbTokens >= 4) {

                    String[] indices = new String[nbTokens - 1];
                    int i = 0;
                    while(tokenizer.hasMoreTokens()) {
                        indices[i] = tokenizer.nextToken();
                        i++;
                    }

                    int nbVerticesPerFace = indices.length;
					for (String strIndex : indices) {
                        String[] parts = strIndex.split("/");
                        int v = Integer.parseInt(parts[0]);
                        if (parts.length >= 2) {
                            int t = Integer.parseInt(parts[1]);
                            if (!textureCoordsIndices.containsKey(nbVerticesPerFace)) {
                                textureCoordsIndices.put(nbVerticesPerFace, new ArrayList<Integer>());
                            }
                            textureCoordsIndices.get(nbVerticesPerFace).add(t);
                        }
                        geometry.getIndexBuffer().addIndex(nbVerticesPerFace, v - 1);
                    }
					/*
					int v1 = Integer.parseInt(v1Parts[0]);
					int v2 = Integer.parseInt(v2Parts[0]);
					int v3 = Integer.parseInt(v3Parts[0]);
					

					Face face = new Face();
					face.addVertex(vertices.get(v1 - 1));
					face.addVertex(vertices.get(v2 - 1));
					face.addVertex(vertices.get(v3 - 1));
					
					
					if (v1Parts.length >= 2 && v2Parts.length >= 2 && v3Parts.length >= 2) {
						int t1 = Integer.parseInt(v1Parts[1]);
						int t2 = Integer.parseInt(v2Parts[1]);
						int t3 = Integer.parseInt(v3Parts[1]);
						textureCoordsIndices.add(t1);
						textureCoordsIndices.add(t2);
						textureCoordsIndices.add(t3);
						face.addTextureCoords(coordsList.get(t1 - 1));
						face.addTextureCoords(coordsList.get(t2 - 1));
						face.addTextureCoords(coordsList.get(t3 - 1));
					}
					
					model.getIndexBuffer().addIndex(v1 - 1);
					model.getIndexBuffer().addIndex(v2 - 1);
					model.getIndexBuffer().addIndex(v3 - 1);
					
					model.getFaces().add(face);

                    geometry.getIndexBuffer().addIndex(v1 - 1);
                    geometry.getIndexBuffer().addIndex(v2 - 1);
                    geometry.getIndexBuffer().addIndex(v3 - 1);*/

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
            Effect effect = new Effect();
			for (int nbVerticesPerFace : textureCoordsIndices.keySet()) {
                for (int i : textureCoordsIndices.get(nbVerticesPerFace)) {
                    TextureCoords coords = coordsList.get(i - 1);
                    effect.addTextureCoordinates(0, nbVerticesPerFace, coords);
                    model.getTextureBuffer().addCoords(coords);
                }
			}
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
