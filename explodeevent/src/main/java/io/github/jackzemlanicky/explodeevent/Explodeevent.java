package io.github.jackzemlanicky.explodeevent;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import java.util.List;

public final class Explodeevent extends JavaPlugin implements Listener{

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("explosion plugin starting");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent explodeEvent){
        // we don't want any blocks to drop on an explosion
        explodeEvent.setYield(0);
        // setting variables here to be used in loop
        Location locationDifference;
        System.out.println("blast happened");
        List<Block> blockList = explodeEvent.blockList();
        // for each block in explosion radius
        for(Block block : blockList){
            // works weird when other tnt are in the explosion, so ignore it. also only care about actual blocks not carpet n grass n stuff
            if(block.getType() != Material.TNT && block.isSolid()){
                // only do it for a percentage of all blocks
                if(Math.random() > 0.5){
                    // clone the blast and block locations and subtract using those so we don't edit the event and block themselves
                    Location blastLocationClone = explodeEvent.getLocation().clone();
                    Location blockLocationClone = block.getLocation().clone();
                    locationDifference = blockLocationClone.subtract(blastLocationClone);
                    System.out.println("location difference from origin of explosion: "+locationDifference.getBlockX()+", " + locationDifference.getBlockY()+", " + locationDifference.getBlockZ());

                    // this is a literal guess at how far away a block can be from tnt to be affected by it- cant find jdocs on it
                    int maxRadius = 7, magnitude = 7;
                    // the lower the mag the higher the mag yk
                    double xVel = ((maxRadius - Math.abs(locationDifference.x()))/magnitude)*Math.signum(locationDifference.x());
                    double yVel = ((maxRadius - Math.abs(locationDifference.y()))/magnitude); //make all y's positive so blocks go up for wowie effect
                    double zVel = ((maxRadius - Math.abs(locationDifference.z()))/magnitude)*Math.signum(locationDifference.z());

                    // create new block that will be thrown with the explosion, casted to falling block from entity
                    block.getWorld().spawn(block.getLocation(), FallingBlock.class, (explodedBlock -> {
                        // so it doesn't drop an item if it can't land
                        explodedBlock.setDropItem(false);
                        // set the block's velocity
                        explodedBlock.setVelocity(new Vector(xVel, yVel, zVel));
                    }));
                }

            }
        }
    }
}
