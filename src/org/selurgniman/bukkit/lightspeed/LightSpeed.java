/**
 * 
 */
package org.selurgniman.bukkit.lightspeed;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;


/**
 * @author <a href="mailto:bandycj@gmail.com">Chris Bandy</a>
 * Created on: Mar 24, 2011
 */
public class LightSpeed extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	private Double speed=3d;
	private Material railMaterial=Material.SANDSTONE;
	private Material pathMaterial=Material.GLASS;
	private Material trackMaterial=Material.IRON_BLOCK;
	
    @Override
	public void onDisable() {
		
	}

	@Override
	public void onEnable() {
    	// Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, new LightSpeedPlayerMoveEvent(), Priority.Normal, this);

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	private class LightSpeedPlayerMoveEvent extends PlayerListener {
		private static final long serialVersionUID = 1L;
		
		public void onPlayerMove(PlayerMoveEvent event){
			Location toLocation=event.getTo();
			Player player=event.getPlayer();
			Block toBlock=toLocation.getBlock();
			if (toBlock.getType() == Material.AIR || toBlock.getType() == Material.IRON_BLOCK || toBlock.getType() == Material.TORCH){
				Block toPathBlock=toBlock.getFace(BlockFace.DOWN);
				if (player.isInsideVehicle()){
					toPathBlock=toBlock;
				}
				Boolean accelerate=false;
				if ((toPathBlock.getType() == pathMaterial || toPathBlock.getType() == trackMaterial) && player.getVelocity().length() <= 1d) {
					BlockFace startBlockFace=null;
					BlockFace oppositeBlockFace=null;
					if (toPathBlock.getFace(BlockFace.NORTH).getType() == railMaterial && toPathBlock.getFace(BlockFace.SOUTH).getType() == railMaterial){
						startBlockFace=BlockFace.NORTH;
						oppositeBlockFace=BlockFace.SOUTH;
					} else if (toPathBlock.getFace(BlockFace.EAST).getType() == railMaterial && toPathBlock.getFace(BlockFace.WEST).getType() == railMaterial){
						startBlockFace=BlockFace.EAST;
						oppositeBlockFace=BlockFace.WEST;
					}
					if (startBlockFace!=null && oppositeBlockFace!=null){
						Block[] path=new Block[7];
						path[0]=toPathBlock.getFace(startBlockFace);
						path[1]=path[0].getFace(BlockFace.DOWN);
						path[2]=path[1].getFace(BlockFace.DOWN);
						path[3]=path[2].getFace(oppositeBlockFace);
						path[4]=path[3].getFace(oppositeBlockFace);
						path[5]=path[4].getFace(BlockFace.UP);
						path[6]=path[5].getFace(BlockFace.UP);
						
						accelerate = (traceShape(path)==path.length);
					}
				}
				
				// Courtesy of Raphfrk from the bukkit forums.
				Location loc = event.getFrom();
				Vector target = new Vector(toLocation.getX(), toLocation.getY(), toLocation.getZ());
				Vector velocity = target.clone().subtract(new Vector(loc.getX(), loc.getY(), loc.getZ()));
				
				if (accelerate){
					if (toPathBlock.getFace(BlockFace.DOWN, 2).isBlockPowered()){
						velocity.multiply(speed);
						if (player.isInsideVehicle()){
							velocity.multiply(speed);
							player.getVehicle().setVelocity(velocity);
						} else {
							player.setVelocity(velocity);
						}
					}
				}
				if (player.getVelocity().length()>1d && (toPathBlock.getType() == pathMaterial || toPathBlock.getType() == trackMaterial)){
					velocity.multiply(speed/velocity.length());
					if (player.isInsideVehicle()){
						velocity.multiply(speed/velocity.length());
						player.getVehicle().setVelocity(velocity);
					} else {
						player.setVelocity(velocity);
					}
				}
			}
			
			toBlock=null;
			player=null;
			toLocation=null;
		}
		
		private int traceShape(Block[] path){
			for (int i=0;i<path.length;i++){
				if (path[i].getType() != railMaterial){
					return 0;
				}
			}
			return path.length;
		}
	}
}

























