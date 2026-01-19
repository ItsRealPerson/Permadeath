package tech.sebazcrc.permadeath.util;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;

public class MobFactory {
    private Main instance;

    public MobFactory(Main instance) {
        this.instance = instance;
    }

    public Creeper spawnEnderCreeper(Location l, Creeper c) {
        if (c == null) {
            c = l.getWorld().spawn(l, Creeper.class);
        }

        c.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));

        c.setCustomName(format("&6Ender Creeper"));
        addPersistentData(c, "ender_creeper");

        return c;
    }

    public Creeper spawnQuantumCreeper(Location l, @Nullable Creeper c) {
        if (c == null) {
            c = l.getWorld().spawn(l, Creeper.class);
        }
        c.setCustomName(format("&6Quantum Creeper"));
        addPersistentData(c, "quantum_creeper");
        c.setExplosionRadius(instance.getConfig().getInt("Toggles.Quantum-Explosion-Power"));
        
        // Soporte 1.21
        if (VersionManager.getRev().equals("1_21_R3")) {
            instance.getNmsHandler().spawnNMSCustomEntity("QuantumCreeper", null, l, null);
            if (c.isValid()) c.remove();
        }
        return c;
    }

    public Creeper spawnEnderQuantumCreeper(Location l, @Nullable Creeper c) {
        if (c == null) {
            c = l.getWorld().spawn(l, Creeper.class);
        }
        c.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        c.setCustomName(format("&6Ender Quantum Creeper"));
        addPersistentData(c, "ender_quantum_creeper");
        c.setExplosionRadius(instance.getConfig().getInt("Toggles.Quantum-Explosion-Power"));

        // Soporte 1.21
        if (VersionManager.getRev().equals("1_21_R3")) {
            instance.getNmsHandler().spawnNMSCustomEntity("EnderQuantumCreeper", null, l, null);
            if (c.isValid()) c.remove();
        }
        return c;
    }

    public void spawnAbyssalCreeper(Location l) {
        instance.getNmsHandler().spawnNMSCustomEntity("AbyssalCreeper", null, l, null);
    }

    public void spawnVoidSkeleton(Location l) {
        instance.getNmsHandler().spawnNMSCustomEntity("VoidSkeleton", null, l, null);
    }

    public void spawnCustomGiant(Location l) {
        instance.getNmsHandler().spawnNMSCustomEntity("CustomGiant", null, l, null);
    }

    public void spawnMinion(String type, Location l) {
        instance.getNmsHandler().spawnNMSCustomEntity(type, null, l, null);
    }

    public boolean hasData(Entity entity, String id) {
        return entity.getPersistentDataContainer().has(new NamespacedKey(instance, id), PersistentDataType.BYTE);
    }

    public void addPersistentData(Entity entity, String id) {
        entity.getPersistentDataContainer().set(new NamespacedKey(instance, id), PersistentDataType.BYTE, (byte) 1);
    }

    public String format(String s) {
        return TextUtils.format(s);
    }
}
















