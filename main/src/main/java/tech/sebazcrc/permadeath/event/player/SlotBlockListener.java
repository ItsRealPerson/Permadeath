package tech.sebazcrc.permadeath.event.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.api.PermadeathAPI;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.util.item.PermadeathItems;
import java.util.Random;

public class SlotBlockListener implements Listener {
    private Main main;

    public SlotBlockListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (e.isCancelled()) return;
        if (e.getItemDrop().getItemStack().getType() == Material.STRUCTURE_VOID) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClickVoid(InventoryClickEvent e) {
        if (e.isCancelled()) return;
        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
                if (e.getClick() == ClickType.NUMBER_KEY) {
                    e.getInventory().remove(Material.STRUCTURE_VOID);
                }
            }
        }

        if (e.getCursor() != null) {
            if (e.getCursor().getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemCraft(PrepareItemCraftEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().getResult() != null) {
                if (e.getInventory().getResult().getType() == Material.TORCH || e.getInventory().getResult().getType() == Material.REDSTONE_TORCH) {
                    e.getInventory().setResult(null);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.STRUCTURE_VOID) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        if (e.isCancelled()) return;
        if (e.getOffHandItem() != null) {
            if (e.getOffHandItem().getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
            }
        }

        if (e.getMainHandItem() != null) {
            if (e.getMainHandItem().getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMoveItem(InventoryMoveItemEvent e) {
        if (e.isCancelled()) return;
        if (e.getItem() != null) {
            if (e.getItem().getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(InventoryPickupItemEvent e) {
        if (e.isCancelled()) return;
        if (e.getItem().getItemStack() != null) {
            if (e.getItem().getItemStack().getType() == Material.STRUCTURE_VOID) {
                e.setCancelled(true);
            }
        }
        // Disparar bloqueo reactivo si el inventario pertenece a un jugador
        if (e.getInventory().getHolder() instanceof Player p) {
            PermadeathItems.slotBlock(p);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickupPlayer(org.bukkit.event.entity.EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p) {
            // Un pequeño delay para que el item llegue al inventario antes de bloquear
            Bukkit.getScheduler().runTaskLater(main, () -> PermadeathItems.slotBlock(p), 1L);
        }
    }

    @EventHandler
    public void onWitchThrow(ProjectileLaunchEvent e) {
        if (PermadeathAPI.getDay() < 40) return;
        if (e.getEntity().getShooter() instanceof Witch) {
            // ... lógica original de brujas ...
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!e.getNewItems().isEmpty()) {
            for (int i : e.getNewItems().keySet()) {
                ItemStack s = e.getNewItems().get(i);
                if (s != null) {
                    if (s.getType() == Material.STRUCTURE_VOID) {
                        e.getInventory().removeItem(s);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onIntWithEndRelic(PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand() != null) {
            if (esReliquia(e.getPlayer(), e.getPlayer().getInventory().getItemInMainHand())) {
                e.setCancelled(true);
            }
        }

        if (e.getPlayer().getInventory().getItemInOffHand() != null) {
            if (esReliquia(e.getPlayer(), e.getPlayer().getInventory().getItemInOffHand())) {
                e.setCancelled(true);
            }
        }
    }

    public boolean esReliquia(Player p, ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        if (stack.getType() == Material.LIGHT_BLUE_DYE && stack.getItemMeta().getDisplayName().endsWith(TextUtils.format("&6Reliquia Del Fin"))) {
            return true;
        }
        return false;
    }
}
