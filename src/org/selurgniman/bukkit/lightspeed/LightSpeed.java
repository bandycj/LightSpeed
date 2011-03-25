/**
 * 
 */
package org.selurgniman.bukkit.lightspeed;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;


/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Mar 24, 2011
 */
public class LightSpeed extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	private Configuration config=null;
    public PermissionHandler Permissions=null;
	private Double speed=4d;
	private Material railMaterial=Material.COBBLESTONE;
	private Material pathMaterial=Material.GLASS;
	
    @Override
	public void onDisable() {
		
	}

	@Override
	public void onEnable() {
		setupPermissions();
    	
//    	config = this.getConfiguration();
//    	config.load();
//    	speed=config.getDouble("LightSpeed.multiplier", 0d);
//    	railMaterial=Material.matchMaterial(config.getString("LightSpeed.multiplier", "COBBLESTONE"));
//    	pathMaterial=Material.matchMaterial(config.getString("LightSpeed.multiplier", "GLASS"));
    	
    	// Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, new LightSpeedPlayerMoveEvent(), Priority.Normal, this);

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	
	private void setupPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

        if (this.Permissions == null) {
            if (test != null) {
                this.Permissions = ((Permissions)test).getHandler();
            } else {
                log.info("Permission system not detected, no permissions will be used!");
            }
        }
    }
	
	private class LightSpeedPlayerMoveEvent extends PlayerListener {
		private static final long serialVersionUID = 1L;
		
		public void onPlayerMove(PlayerMoveEvent event){
			Location toLocation=event.getTo();
			World world=toLocation.getWorld();
			Player player=event.getPlayer();
			Block toBlock=world.getBlockAt(toLocation);
			if (toBlock.getType() == pathMaterial){
				log.info("player on glass");
				if ((toBlock.getFace(BlockFace.NORTH).getType() == railMaterial 
						&& toBlock.getFace(BlockFace.SOUTH).getType() == railMaterial) || 
						(toBlock.getFace(BlockFace.EAST).getType() == railMaterial 
						&& toBlock.getFace(BlockFace.WEST).getType() == railMaterial)){

					log.info("player on path");
					// Courtesy of Raphfrk from the bukkit forums.
					Location loc = player.getLocation();
					Vector target = new Vector(toLocation.getX(), toLocation.getY(), toLocation.getZ());
					Vector velocity = target.clone().subtract(new Vector(loc.getX(), loc.getY(), loc.getZ()));
					velocity.multiply(speed/velocity.length());
					player.setVelocity(velocity);
				}
			} else if (toBlock.getType() != pathMaterial && world.getBlockAt(event.getFrom()).getType() == pathMaterial){
				// Courtesy of Raphfrk from the bukkit forums.
				Location loc = player.getLocation();
				Vector target = new Vector(toLocation.getX(), toLocation.getY(), toLocation.getZ());
				Vector velocity = target.clone().subtract(new Vector(loc.getX(), loc.getY(), loc.getZ()));
				velocity.multiply(speed*velocity.length());
				player.setVelocity(velocity);				 
			}
		}
	}
}

























