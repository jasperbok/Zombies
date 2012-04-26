package nl.jasperbok.zombies.level;

import java.util.HashMap;

import nl.jasperbok.zombies.entity.Player;
import nl.jasperbok.zombies.entity.Trigger;
import nl.jasperbok.zombies.entity.building.AutoTurret;
import nl.jasperbok.zombies.entity.building.Door;
import nl.jasperbok.zombies.entity.building.Switch;
import nl.jasperbok.zombies.entity.item.Item;
import nl.jasperbok.zombies.entity.mob.Zombie;
import nl.jasperbok.zombies.entity.object.WoodenCrate;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

public class MapLoader {

	public MapLoader() throws SlickException {
	}
	
	public static Tile[][] loadTiles(TiledMap map) throws SlickException {
		int mapWidth = map.getWidth();
		int mapHeight = map.getHeight();
		int tileWidth = map.getTileWidth();
		int tileHeight = map.getTileHeight();
		
		int collisionLayer = map.getLayerIndex("collision");
		
		Tile[][] tiles = new Tile[mapWidth][mapHeight];
		
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				int tileId = map.getTileId(x, y, collisionLayer);
				tiles[x][y] = new Tile(
					tileId,
					tileWidth,
					tileHeight,
					tileWidth * x,
					tileHeight * y,
					"true".equals(map.getTileProperty(tileId, "blocked", "false")),
					"true".equals(map.getTileProperty(tileId, "climable", "false")),
					"true".equals(map.getTileProperty(tileId, "hideable", "false"))
				);
			}
		}
		
		return tiles;
	}
	
	public static void loadEntities(TileEnvironment env, Level level, TiledMap map) throws SlickException {
		// The number of object layers.
		int numGroups = map.getObjectGroupCount();
		
		for (int i = 0; i < numGroups; i++) {
			// The number of objects in the current object layer.
			int numObjects = map.getObjectCount(i);
			
			for (int j = 0; j < numObjects; j++) {
				switch (map.getObjectType(i, j)) {
				case "AutoTurret" :
					MapLoader.spawnTurret(env, level, map, i, j);
					break;
				case "Door" :
					MapLoader.spawnDoor(env, level, map, i, j);
					break;
				case "Item" :
					MapLoader.spawnItem(env, level, map, i, j);
					break;
				case "Player" :
					MapLoader.spawnPlayer(env, level, map, i, j);
					break;
				case "Switch" :
					MapLoader.spawnSwitch(env, level, map, i, j);
					break;
				case "Trigger" :
					MapLoader.addTrigger(env, level, map, i, j);
					break;
				case "WoodenCrate" :
					MapLoader.spawnWoodenCrate(env, level, map, i, j);
					break;
				case "Zombie" :
					MapLoader.spawnZombie(env, level, map, i, j);
				}
			}
		}
	}
	
	private static void spawnDoor(TileEnvironment env, Level level, TiledMap map, int layerIndex, int objectIndex) throws SlickException {
		Door door = new Door(
				level,
				"left".equals(map.getObjectProperty(layerIndex, objectIndex, "direction", "right")));
		door.position.x = map.getObjectX(layerIndex, objectIndex);
		door.position.y = map.getObjectY(layerIndex, objectIndex);
		door.name = map.getObjectName(layerIndex, objectIndex);
		env.spawnEntity(door);
	}
	
	private static void spawnTurret(TileEnvironment env, Level level, TiledMap map, int layerIndex, int objectIndex) throws SlickException {
		AutoTurret turret = new AutoTurret(
				level,
				"left".equals(map.getObjectProperty(layerIndex, objectIndex, "direction", "left")),
				null,
				new Vector2f(map.getObjectX(layerIndex, objectIndex), map.getObjectY(layerIndex, objectIndex))
				);
		turret.name = map.getObjectName(layerIndex, objectIndex);
		env.spawnEntity(turret);
	}
	
	private static void spawnPlayer(TileEnvironment env, Level level, TiledMap map, int layerIndex, int objectIndex) throws SlickException {
		Player player = new Player(
				level,
				new Vector2f(map.getObjectX(layerIndex, objectIndex), map.getObjectY(layerIndex, objectIndex))
				);
		player.name = "player";
		env.spawnEntity(player);
	}
	
	private static void spawnSwitch(TileEnvironment env, Level level, TiledMap map, int layerIndex, int objectIndex) throws SlickException {
		HashMap<String, String> settings = new HashMap<String, String>();
		settings.put("target", map.getObjectProperty(layerIndex, objectIndex, "target", ""));
		settings.put("requires", map.getObjectProperty(layerIndex, objectIndex, "requires", ""));
		settings.put("successMessage", map.getObjectProperty(layerIndex, objectIndex, "successMessage", ""));
		settings.put("failureMessage", map.getObjectProperty(layerIndex, objectIndex, "failureMessage", ""));
		Switch newSwitch = new Switch(
				level,
				"true".equals(map.getObjectProperty(layerIndex, objectIndex, "initial_state", "false")),
				settings
				);
		newSwitch.position.x = map.getObjectX(layerIndex, objectIndex);
		newSwitch.position.y = map.getObjectY(layerIndex, objectIndex);
		newSwitch.name = map.getObjectName(layerIndex, objectIndex);
		env.spawnEntity(newSwitch);
	}
	
	private static void spawnItem(TileEnvironment env, Level level, TiledMap map, int layerIndex, int objectIndex) throws SlickException {
		Item item = new Item(level, map.getObjectProperty(layerIndex, objectIndex, "type", ""));
		item.position.x = map.getObjectX(layerIndex, objectIndex);
		item.position.y = map.getObjectY(layerIndex, objectIndex);
		item.name = map.getObjectName(layerIndex, objectIndex);
		env.spawnEntity(item);
	}
	
	private static void spawnZombie(TileEnvironment env, Level level, TiledMap map, int layerIndex, int objectIndex) throws SlickException {
		Zombie zombie = new Zombie(level);
		zombie.position.x = map.getObjectX(layerIndex, objectIndex);
		zombie.position.y = map.getObjectY(layerIndex, objectIndex);
		zombie.name = map.getObjectName(layerIndex, objectIndex);
		
		// Loading blocking points.
		if (Integer.parseInt(map.getObjectProperty(layerIndex, objectIndex, "max-left", "-1")) != -1) {
			zombie.addBlockingPointLeft(Integer.parseInt(map.getObjectProperty(layerIndex, objectIndex, "max-left", "-1")));
		}
		if (Integer.parseInt(map.getObjectProperty(layerIndex, objectIndex, "max-right", "-1")) != -1) {
			zombie.addBlockingPointRight(Integer.parseInt(map.getObjectProperty(layerIndex, objectIndex, "max-right", "-1")));
		}
		env.spawnEntity(zombie);
	}
	
	private static void spawnWoodenCrate(TileEnvironment env, Level level, TiledMap map, int layerIndex, int objectIndex) throws SlickException {
		WoodenCrate crate = new WoodenCrate(level);
		crate.position.x = map.getObjectX(layerIndex, objectIndex);
		crate.position.y = map.getObjectY(layerIndex, objectIndex);
		crate.name = map.getObjectName(layerIndex, objectIndex);
		env.spawnEntity(crate);
	}
	
	private static void addTrigger(TileEnvironment env, Level level, TiledMap map, int layerIndex, int objectIndex) throws SlickException {
		HashMap<String, String> settings = new HashMap<String, String>();
		settings.put("target", map.getObjectProperty(layerIndex, objectIndex, "target", ""));
		settings.put("sfx", map.getObjectProperty(layerIndex, objectIndex, "sound", ""));
		settings.put("repeatDelay", map.getObjectProperty(layerIndex, objectIndex, "repeatDelay", ""));
		settings.put("message", map.getObjectProperty(layerIndex, objectIndex, "message", ""));
		settings.put("messageDuration", map.getObjectProperty(layerIndex, objectIndex, "messageDuration", ""));
		settings.put("goToLevel", map.getObjectProperty(layerIndex, objectIndex, "goToLevel", ""));
		Trigger trigger = new Trigger(
					level,
					"true".equals(map.getObjectProperty(layerIndex, objectIndex, "repeat", "false")),
					new Vector2f(map.getObjectX(layerIndex, objectIndex), map.getObjectY(layerIndex, objectIndex)),
					new Vector2f(map.getObjectWidth(layerIndex, objectIndex), map.getObjectHeight(layerIndex, objectIndex)),
					settings
				);
		env.addTrigger(trigger);
		
		if (!"".equals(map.getObjectProperty(layerIndex, objectIndex, "sound", ""))) {
			env.sounds.loadSFX(map.getObjectProperty(layerIndex, objectIndex, "sound", ""));
		}
	}
}
