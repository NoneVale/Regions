package net.nighthawkempires.regions.listeners;

import net.nighthawkempires.regions.region.RegionFlag;
import net.nighthawkempires.regions.region.RegionModel;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static net.nighthawkempires.core.CorePlugin.*;
import static net.nighthawkempires.regions.RegionsPlugin.*;
import static net.nighthawkempires.regions.region.RegionFlag.*;
import static net.nighthawkempires.regions.region.RegionFlag.Result.*;
import static org.bukkit.ChatColor.*;

public class RegionListener implements Listener {

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        RegionModel region = getRegionRegistry().getObeyRegion(player.getLocation());
        if (region != null && !region.getBypassRegion().contains(player.getUniqueId())) {
            if (region.inRegion(event.getBlock().getLocation()) || region.inRegion(player.getLocation())) {
                if (region.getFlagResult(BUILD) == DENY) {
                    player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you're not allowed to place blocks in this region."));
                    event.setCancelled(true);
                    return;
                }
            }
        }

        region = getRegionRegistry().getObeyRegion(event.getBlockPlaced().getLocation());
        if (region != null && !region.getBypassRegion().contains(player.getUniqueId())) {
            if (region.inRegion(event.getBlock().getLocation()) || region.inRegion(player.getLocation())) {
                if (region.getFlagResult(BUILD) == DENY) {
                    player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you're not allowed to place blocks in this region."));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        RegionModel region = getRegionRegistry().getObeyRegion(player.getLocation());
        if (region != null && !region.getBypassRegion().contains(player.getUniqueId())) {
            if (region.inRegion(event.getBlock().getLocation()) || region.inRegion(player.getLocation())) {
                if (region.getFlagResult(BREAK) == DENY) {
                    player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you're not allowed to break blocks in this region."));
                    event.setCancelled(true);
                    return;
                }
            }
        }

        region = getRegionRegistry().getObeyRegion(event.getBlock().getLocation());
        if (region != null && !region.getBypassRegion().contains(player.getUniqueId())) {
            if (region.inRegion(event.getBlock().getLocation()) || region.inRegion(player.getLocation())) {
                if (region.getFlagResult(BREAK) == DENY) {
                    player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you're not allowed to place blocks in this region."));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            RegionModel region = getRegionRegistry().getObeyRegion(event.getBlock().getLocation());

            if (region != null) {
                if (event.getBlock().getType() == Material.FARMLAND) {
                    if (region.getFlagResult(CROP_TRAMPLE) == DENY) {
                        player.sendMessage("no trampling crops on my watch.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        switch (event.getAction()) {
            case PHYSICAL:
                if (event.getClickedBlock() != null) {
                    RegionModel region = getRegionRegistry().getObeyRegion(event.getClickedBlock().getLocation());
                    if (region != null) {
                        if (event.getClickedBlock().getType() == Material.FARMLAND){
                            if (region.getFlagResult(CROP_TRAMPLE) == DENY) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
                break;
            case RIGHT_CLICK_BLOCK:
                if (event.getClickedBlock() != null) {
                    RegionModel region = getRegionRegistry().getObeyRegion(event.getClickedBlock().getLocation());
                    if (region != null && !region.getBypassRegion().contains(player.getUniqueId())) {
                        if (getMaterials().isButton(event.getClickedBlock().getType())) {
                            if (region.getFlagResult(INTERACT_BUTTON) == DENY) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you can not interact with that here."));
                                event.setCancelled(true);
                            }
                        } else if (getMaterials().isChest(event.getClickedBlock().getType())) {
                            if (region.getFlagResult(INTERACT_CHEST) == DENY) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you can not interact with that here."));
                                event.setCancelled(true);
                            }
                        } else if (getMaterials().isDoor(event.getClickedBlock().getType())) {
                            if (region.getFlagResult(INTERACT_DOOR) == DENY) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you can not interact with that here."));
                                event.setCancelled(true);
                            }
                        } else if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                            if (region.getFlagResult(INTERACT_ENDER_CHEST) == DENY) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you can not interact with that here."));
                                event.setCancelled(true);
                            }
                        } else if (event.getClickedBlock().getType() == Material.LEVER) {
                            if (region.getFlagResult(INTERACT_LEVER) == DENY) {
                                player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but you can not interact with that here."));
                                event.setCancelled(true);
                            }
                        }
                    }
                }
                break;
        }
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        if (event.getBlock().getType() != null) {
            RegionModel region = getRegionRegistry().getObeyRegion(event.getBlock().getLocation());
            if (region != null) {
                if (event.getBlock().getType() == Material.FARMLAND) {
                    if (region.getFlagResult(CROP_TRAMPLE) == DENY) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            RegionModel region = getRegionRegistry().getObeyRegion(player.getLocation());
            if (region != null) {
                if (event.getFoodLevel() < player.getFoodLevel()) {
                    if (region.getFlagResult(HUNGER_DEGEN) == DENY) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());
        if (region != null) {
            if (event.getEntity().getShooter() instanceof Player) {
                if (region.getBypassRegion().contains(((Player) event.getEntity().getShooter()).getUniqueId())) return;
            }

            if (region.getFlagResult(PROJECTILES) == DENY) {
                event.getEntity().remove();

            }
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());
        if (region != null) {
            if (event.getEntity().getShooter() instanceof Player) {
                if (region.getBypassRegion().contains(((Player) event.getEntity().getShooter()).getUniqueId())) return;
            }

            if (region.getFlagResult(PROJECTILES) == DENY) {
                event.getEntity().remove();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPearl(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Player player = event.getPlayer();

            RegionModel regionTo = getRegionRegistry().getObeyRegion(event.getTo());
            RegionModel regionFrom = getRegionRegistry().getObeyRegion(event.getFrom());

            if (regionTo != null) {
                if (!regionTo.getBypassRegion().contains(player.getUniqueId())) {
                    if (regionTo.getFlagResult(ENDERPEARL) == DENY) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but ender pearls have been disabled in this region."));
                        event.setCancelled(true);
                    }
                }
            }

            if (regionFrom != null) {
                if (!regionFrom.getBypassRegion().contains(player.getUniqueId())) {
                    if (regionFrom.getFlagResult(ENDERPEARL) == DENY) {
                        player.sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but ender pearls have been disabled in this region."));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getBlock().getLocation());

        if (region != null) {
            if (region.getFlagResult(FIRE) == DENY) {
                event.setCancelled(true);
            }
        }

        region = getRegionRegistry().getObeyRegion(event.getIgnitingBlock().getLocation());
        if (region != null) {
            if (region.getFlagResult(FIRE) == DENY) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getSource().getLocation());

        if (region != null) {
            if (event.getSource().getType() == Material.FIRE) {
                if (region.getFlagResult(FIRE_SPREAD) == DENY) {
                    event.setCancelled(true);
                }
            }
        }

        region = getRegionRegistry().getObeyRegion(event.getBlock().getLocation());

        if (region != null) {
            if (event.getBlock().getType() == Material.FIRE) {
                if (region.getFlagResult(FIRE_SPREAD) == DENY) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());

        if (region != null) {
            if (region.getFlagResult(POTION_SPLASH) == DENY) {
                event.getEntity().remove();
                event.setCancelled(true);
            }
        }
    }

    /**@EventHandler
    public void onPotion(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player) {
            RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());

            if (region != null) {
                if (event.getCause() != Cause.BEACON || event.getCause()
                        != Cause.EXPIRATION || event.getCause() != Cause.POTION_DRINK) {
                    if (region.getFlagResult(RegionFlag.POTION_SPLASH) == DENY) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }*/

    @EventHandler
    public void onPotion(LingeringPotionSplashEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());

        if (region != null) {
            if (region.getFlagResult(POTION_SPLASH) == DENY) {

                event.getEntity().remove();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());
        if (event.getEntity() instanceof Player)  {
            Player player = (Player) event.getEntity();

            if (region != null) {
                if (region.getFlagResult(DAMAGE) == DENY) {
                    event.setCancelled(true);
                }
            }
        } else {
            if (region != null) {
                if (region.getFlagResult(MOB_DAMAGE) == DENY) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPrime(ExplosionPrimeEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());

        if (region != null) {
            if (region.getFlagResult(TNT) == DENY) {
                if (event.getEntity() instanceof LivingEntity) {
                    ((LivingEntity) event.getEntity()).setHealth(0);
                }

                event.setCancelled(true);
                event.setFire(false);
                event.setRadius(0);
            }
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getLocation());

        if (region != null) {
            if (!(event.getEntity() instanceof Player)) {
                if (!(event.getEntity() instanceof Item)) {
                    if (region.getFlagResult(MOB_SPAWN) == DENY) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());
        if (event.getEntity() instanceof Player)  {
            Player player = (Player) event.getEntity();

            if (region != null) {
                if (region.getFlagResult(DAMAGE) == DENY) {
                    event.setCancelled(true);
                }
            }

            if (event.getDamager() instanceof Player) {
                if (region != null) {
                    if (region.getFlagResult(PVP) == DENY) {
                        event.getDamager().sendMessage(getMessages().getChatMessage(GRAY + "I'm sorry, but PvP is disabled in this region."));
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            if (region != null) {
                if (region.getFlagResult(MOB_DAMAGE) == DENY) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByBlockEvent event) {
        RegionModel region = getRegionRegistry().getObeyRegion(event.getEntity().getLocation());
        if (event.getEntity() instanceof Player)  {
            Player player = (Player) event.getEntity();

            if (region != null) {
                if (region.getFlagResult(DAMAGE) == DENY) {
                    event.setCancelled(true);
                }
            }
        } else {
            if (region != null) {
                if (region.getFlagResult(MOB_DAMAGE) == DENY) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
