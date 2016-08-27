/*
 * see license.txt 
 */
package franks.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Color;

import franks.gfx.ImageUtil;

/**
 * @author Tony
 *
 */
public class AnimationTool {

	/**
	 * 
	 */
	public AnimationTool() {
		// TODO Auto-generated constructor stub
	}

	private String[] states = {
		"tipping over",
		"been hit",
		"walking",
		//"stopped",
		"running",
		"paused",
		"greeting",
		"attack",
		"shooting"
	};
	
	// Knights Mask
	//public static final int Mask = 0x6f4f33;
	
	// Archer Mask
	//public static final int Mask = 0x694A2E;
	
	// DarkDwarf
	public static final int Mask = 0x61442B;
	
	
	private String getGroupName(File f) {
		for(String state : states) {
			if(f.getName().startsWith(state)) {
				return state;
			}
		}
		return null;
	}
	
	
	
	
	private Map<String, List<File>> groupStates(File[] files) {
		Map<String, List<File>> groups = new HashMap<String, List<File>>();
		for(String state : states) {
			groups.put(state, new ArrayList<>());
		}
		
		for(File f : files) {
			String state = getGroupName(f);
			if(state!=null) {
				groups.get(state).add(f);
			}
		}
		
		return groups;
	}
	
	static class Directions {
		static final String[] Dirs = {
			" e00",
			"n00",
			"ne00",
			"nw00",
			"s00",
			"se00",
			"sw00",
			" w00",
		};
		Map<String, List<File>> directions = new LinkedHashMap<>();
		
		List<File> north=new ArrayList<>();
		List<File> northEast=new ArrayList<>();
		List<File> southEast=new ArrayList<>();
		List<File> south=new ArrayList<>();
		List<File> southWest=new ArrayList<>();
		List<File> west=new ArrayList<>();
		List<File> northWest=new ArrayList<>();
	}
	
	private int getNumber(File f) {
		int index = f.getName().indexOf("00");
		int endIndex = f.getName().indexOf(".bmp");
		return Integer.parseInt(f.getName().substring(index, endIndex));
	}
	
	private Map<String, Directions> groupDirections(Map<String, List<File>> groups) {
		Map<String, Directions> sortedGroup = new LinkedHashMap<>();
		groups.entrySet().forEach(entry -> {
			Directions dirs = new Directions();
			
			for(String direction : Directions.Dirs) {
				dirs.directions.put(direction.trim(), entry.getValue().stream().filter(file -> file.getName().contains(direction))
													.sorted( (a,b) -> getNumber(a) - getNumber(b))
													.collect(Collectors.toList()));
			}
					
			sortedGroup.put(entry.getKey(), dirs);
		});
		
		return sortedGroup;
	}
	
	private BufferedImage readImage(File file) {
		try {
			return ImageIO.read(file);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void writeImage(BufferedImage image, File output) {
		try {
			ImageIO.write(image, "png", output);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int nextPowerOf2(int n) {
		int res = 2;
		while (res < n) {
			res = res * 2;
		}

		return res;
	}
	
	int width = 0;
	int height = 0;
	int deltaHeight=0;
	
	private void run() {
		File spritesDir = new File("C:\\Users\\Tony\\Desktop\\SpriteSheetPacker\\DarkDwarf\\All");
		
		final File outputDir = new File(spritesDir, "Output");
		outputDir.mkdir();
		
		File [] allImages = spritesDir.listFiles();
		Map<String, List<File>> groups = groupStates(allImages);
		Map<String, Directions> sorted = groupDirections(groups);
		sorted.forEach((action, dirs) -> {
			final File actionFile = new File(outputDir, action);
			actionFile.mkdir();
			
			width = 0;
			height = 0;

			deltaHeight = 0;
			
			List<BufferedImage> allDirections = new ArrayList<>();
			
			dirs.directions.forEach( (dir, files) -> {
//				final File dirFile = new File(actionFile, dir);
//				dirFile.mkdir();
				List<BufferedImage> images = new ArrayList<>();
				
				int dwidth = 0;
				int dheight = 0;
				
				for(File file : files) {
					BufferedImage image = readImage(file);
					//System.out.println(dirFile.getPath() + "/" + file.getName());
					dwidth += image.getWidth();
					if(dheight < image.getHeight()) {
						dheight = image.getHeight();
					}
					images.add(image);
				};
				height += dheight;
				
				if(deltaHeight < dheight) {
					deltaHeight = dheight;
				}
				
				dwidth = nextPowerOf2(dwidth);
				//dheight = nextPowerOf2(dheight);
				
				if(dheight <= 0) {
					return;
				}
				
				if(width < dwidth) {
					width = dwidth;
				}
				
				BufferedImage dirImage = ImageUtil.createImage(dwidth, dheight);
				Graphics2D g = (Graphics2D) dirImage.createGraphics();
				
				int x = 0;
				for(BufferedImage img : images) {
					
					g.drawImage(img, x, 0, null);
					x += img.getWidth();
				}
				g.dispose();
				
				dirImage = ImageUtil.applyMask(dirImage, new java.awt.Color(Mask));
				allDirections.add(dirImage);
			});
			
			System.out.println("DeltaHeight: " +deltaHeight);
			
			width = nextPowerOf2(width);
			height = nextPowerOf2(height);
			
			BufferedImage bigImage = ImageUtil.createImage(width, height);
			Graphics2D g = (Graphics2D) bigImage.createGraphics();
			int y = 0;
			
			for(BufferedImage img : allDirections) {
				g.drawImage(img, 0, y, null);
				y += deltaHeight;
			}
			g.dispose();
			writeImage(bigImage, new File(actionFile, "output.png"));
		});
		
		File idleDir = new File(outputDir, "idle");
		idleDir.mkdir();
		createIdle(idleDir, allImages);
	}
	
	
	private void createIdle(File output, File[] files) {
		List<File> idles = new ArrayList<>();
		for(File f : files) {
			if(f.getName().startsWith("stopped")) {
				idles.add(f);
			}
		}
		int[] map = {
				4,
				6,
				7,
				3,
				1,
				2,
				0,
				5
		};
		
		File[] orderFiles = new File[8];
		for(File f : idles) {
			int index = map[getNumber(f)];
			orderFiles[index] = f;
		}
		
		
		int width = 0;
		int height = 0;
		int deltaHeight = 0;
		
		List<BufferedImage> images = new ArrayList<>();
		for(File f : orderFiles) {
			BufferedImage image = readImage(f);
			image = ImageUtil.applyMask(image, new java.awt.Color(Mask));
			if(width < image.getWidth()) {
				width = image.getWidth();
			}
			height += image.getHeight();
			
			if(deltaHeight < image.getHeight()) {
				deltaHeight = image.getHeight();
			}
			
			images.add(image);
		}
		
		width = nextPowerOf2(width);
		height = nextPowerOf2(height);
		
		BufferedImage bigImage = ImageUtil.createImage(width, height);
		Graphics2D g = (Graphics2D) bigImage.createGraphics();
		int y = 0;
		
		for(BufferedImage img : images) {
			g.drawImage(img, 0, y, null);
			y += deltaHeight;
		}
		g.dispose();
		writeImage(bigImage, new File(output, "output.png"));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new AnimationTool().run();
	}

}
