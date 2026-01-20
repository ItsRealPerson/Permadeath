package tech.sebazcrc.permadeath.task;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import tech.sebazcrc.permadeath.Main;
import tech.sebazcrc.permadeath.util.TextUtils;
import tech.sebazcrc.permadeath.end.demon.DemonCurrentAttack;
import tech.sebazcrc.permadeath.end.demon.DemonPhase;

import java.util.*;

public class EndTask extends BukkitRunnable {

    private Map<Location, Integer> regenTime = new HashMap<>();
    private Location teleportLocation;
    private DemonCurrentAttack currentAttack = DemonCurrentAttack.NONE;
    private DemonPhase currentDemonPhase = DemonPhase.NORMAL;
    private MovesTask currentMovesTask = null;

    private EnderDragon enderDragon;
    private Main main;

    private int timeForTnT = 30;
    private int nextDragonAttack = 20;
    private int lightingDuration = 5;
    private int nightVisionDuration = 5;
    private int timeForEnd360 = 20;

    private boolean nightVision = false;
    private boolean isDied;
    private boolean attack360 = false;
    private boolean lightingRain = false;
    private boolean canMakeAnAttack = true;
    private boolean decided = false;

    private Location eggLocation;

    private SplittableRandom random = new SplittableRandom();
    
    // MEJORAS
    private int gravityWellDuration = 0;
    private int meteorShowerDuration = 0;
    private int platformDeleteTimer = 100;

    // Para Folia
    private Object foliaTask;

    public EndTask(Main plugin, EnderDragon enderDragon) {
        this.main = plugin;

        this.isDied = false;
        this.enderDragon = enderDragon;

        int y = main.endWorld.getMaxHeight() - 1;
        while (y > 0 && main.endWorld.getBlockAt(0, y, 0).getType() != Material.BEDROCK) {
            y--;
        }
        this.eggLocation = main.endWorld.getHighestBlockAt(new Location(main.endWorld, 0, y, 0)).getLocation();

        double maxHealth = Main.instance.getConfig().getDouble("Toggles.End.PermadeathDemon.Health", 2000.0);
        org.bukkit.attribute.AttributeInstance attr = enderDragon.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(maxHealth);
            enderDragon.setHealth(attr.getValue());
        }

        teleportLocation = eggLocation.clone().add(0, 2, 0);
        teleportLocation.setPitch(enderDragon.getLocation().getPitch());

        for (Entity all : enderDragon.getWorld().getEntitiesByClass(Ghast.class)) {
            all.remove();
        }
    }

    public void start() {
        if (Main.isRunningFolia()) {
            this.foliaTask = enderDragon.getScheduler().runAtFixedRate(main, t -> run(), null, 1L, 20L);
        } else {
            this.runTaskTimer(main, 0, 20L);
        }
    }

    @Override
    public void cancel() {
        if (Main.isRunningFolia() && foliaTask != null) {
            ((io.papermc.paper.threadedregions.scheduler.ScheduledTask) foliaTask).cancel();
        } else {
            try {
                super.cancel();
            } catch (IllegalStateException ignored) {}
        }
    }

    @Override
    public void run() {
        if (isDied || enderDragon.isDead() || !enderDragon.isValid()) {
            main.setTask(null);
            cancel();
            return;
        }
        tickTnTAttack();
        tickLightingRain();
        tickNightVision();
        tick360Attack();
        tickDemonPhase();
        tickRandomLighting();
        tickEnderCrystals();
        tickDragonAttacks();
        
        // Mejoras nuevas
        tickGravityWell();
        tickMeteorShower();
        tickApocalypticWeather();
        tickVanishingPlatforms();
    }

    private void tickEnderCrystals() {
        if (!regenTime.isEmpty()) {
            Iterator<Map.Entry<Location, Integer>> it = regenTime.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Location, Integer> entry = it.next();
                int time = entry.getValue();
                if (time >= 1) {
                    entry.setValue(time - 1);
                } else {
                    entry.getKey().getWorld().spawnEntity(entry.getKey(), EntityType.END_CRYSTAL);
                    it.remove();
                    if (entry.getKey().getWorld().getBlockAt(entry.getKey()) != null) {
                        Material type = entry.getKey().getWorld().getBlockAt(entry.getKey()).getType();
                        if (type != Material.BEDROCK && type != Material.AIR) {
                            entry.getKey().getWorld().getBlockAt(entry.getKey()).setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    private void tickRandomLighting() {
        int x = (random.nextBoolean() ? 1 : -1) * random.nextInt(21);
        int z = (random.nextBoolean() ? 1 : -1) * random.nextInt(21);
        int y = main.endWorld.getHighestBlockYAt(x, z);

        if (y < 0) return;

        main.endWorld.strikeLightning(new Location(main.endWorld, x, y, z));
    }

    private void tickDemonPhase() {
        if (currentDemonPhase == DemonPhase.ENRAGED) {
            enderDragon.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 7));
            enderDragon.setCustomName(TextUtils.format(main.getConfig().getString("Toggles.End.PermadeathDemon.DisplayNameEnraged")));
        } else {
            enderDragon.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 5));
        }
    }

    private void tick360Attack() {
        if (enderDragon.getLocation().distance(eggLocation) >= 10.0D && decided) {
            decided = false;
        }
        if (enderDragon.getLocation().distance(eggLocation) <= 3.0D && !decided) {

            decided = true;
            enderDragon.setRotation(enderDragon.getLocation().getPitch(), 0);

            if (random.nextInt(10) <= 7) {
                start360attack();
            }
        }

        if (attack360) {
            canMakeAnAttack = false;
            if (timeForEnd360 >= 1) {
                timeForEnd360 = timeForEnd360 - 1;
            }
            if (timeForEnd360 >= 16) {
                EnderDragon dragon = enderDragon;
                if (dragon.getPhase() != EnderDragon.Phase.LAND_ON_PORTAL) {
                    dragon.setPhase(EnderDragon.Phase.LAND_ON_PORTAL);
                }
                if (Main.isRunningFolia()) {
                    dragon.teleportAsync(teleportLocation);
                } else {
                    dragon.teleport(teleportLocation);
                }
            }

            if (timeForEnd360 == 15) {
                this.currentMovesTask = new MovesTask(main, (EnderDragon) enderDragon, teleportLocation);
                currentMovesTask.start();
            }

            if (timeForEnd360 == 0) {
                if (currentMovesTask != null) {
                    currentMovesTask.cancel();
                    currentMovesTask = null;
                }

                canMakeAnAttack = true;
                timeForEnd360 = 20;
                attack360 = false;
                enderDragon.setPhase(EnderDragon.Phase.LEAVE_PORTAL);
            }
        }
    }

    private void tickDragonAttacks() {
        if (nextDragonAttack >= 1) {
            nextDragonAttack = nextDragonAttack - 1;
        } else if (nextDragonAttack == 0) {
            nextDragonAttack = (getCurrentDemonPhase() == DemonPhase.NORMAL) ? 60 : 40;

            if (canMakeAnAttack) {
                chooseAnAttack();
            } else {
                currentAttack = DemonCurrentAttack.NONE;
            }
            if (currentAttack == DemonCurrentAttack.NONE) {
                return;
            }
            if (currentAttack == DemonCurrentAttack.ENDERMAN_BUFF) {

                int endermanschoosed = 0;
                ArrayList<Enderman> endermen = new ArrayList<>();

                for (Enderman man : main.endWorld.getEntitiesByClass(Enderman.class)) {

                    Location backUp = man.getLocation();
                    backUp.setY(0);

                    if (eggLocation.distance(backUp) <= 35) {
                        if (endermanschoosed < 4) {
                            endermanschoosed = endermanschoosed + 1;
                            endermen.add(man);
                        }
                    }
                }
                if (!endermen.isEmpty()) {
                    for (Enderman mans : endermen) {
                        AreaEffectCloud a = (AreaEffectCloud) main.endWorld.spawnEntity(main.endWorld.getHighestBlockAt(mans.getLocation()).getLocation().add(0, 1, 0), EntityType.AREA_EFFECT_CLOUD);  
                        a.setRadius(10.0F);
                        a.setParticle(Particle.HAPPY_VILLAGER);
                        a.setColor(Color.GREEN);

                        a.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 999999, 0), false);

                        mans.setInvulnerable(true);
                    }
                }
            } else if (currentAttack == DemonCurrentAttack.LIGHTING_RAIN) {
                lightingRain = true;
                lightingDuration = 5;
            } else if (currentAttack == DemonCurrentAttack.NIGHT_VISION) {
                nightVision = true;
                nightVisionDuration = 5;
                for (Player all : main.endWorld.getPlayers()) {
                    all.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 7, 0));
                }
            }
        }
    }

    private void tickTnTAttack() {
        timeForTnT = timeForTnT - 1;

        if (timeForTnT == 0) {

            if (enderDragon.getPhase() != EnderDragon.Phase.DYING && !attack360 && enderDragon.getLocation().distance(eggLocation) >= 15) {

                for (int i = 0; i < 6; i++) {
                    Location l = enderDragon.getLocation().add(random.nextInt(7)-3, 0, random.nextInt(7)-3);
                    TNTPrimed tnt = (TNTPrimed) enderDragon.getWorld().spawnEntity(l, EntityType.TNT);
                    tnt.setFuseTicks(60);
                    tnt.setYield(tnt.getYield() * 2);
                    tnt.setCustomName("dragontnt");
                    tnt.setCustomNameVisible(false);
                }
            }
            timeForTnT = 30 + (random.nextInt(61));
        }
    }

    private void tickLightingRain() {
        if (lightingRain) {
            if (lightingDuration >= 1) {
                canMakeAnAttack = false;
                lightingDuration = lightingDuration - 1;

                for (Player all : main.endWorld.getPlayers()) {

                    main.endWorld.strikeLightning(all.getLocation());

                    if (currentDemonPhase == DemonPhase.ENRAGED) {

                        all.damage(1.0D);
                    }
                }
            } else {
                lightingRain = false;
                lightingDuration = 5;
                canMakeAnAttack = true;
            }
        }
    }

    private void tickNightVision() {
        if (nightVision) {
            if (nightVisionDuration >= 1) {
                nightVisionDuration--;
            } else {
                for (Player all : main.endWorld.getPlayers()) {
                    Location highest = main.endWorld.getHighestBlockAt(all.getLocation()).getLocation().add(0, 1, 0);
                    AreaEffectCloud eff = (AreaEffectCloud) main.endWorld.spawnEntity(highest, EntityType.AREA_EFFECT_CLOUD);
                    eff.setParticle(Particle.DAMAGE_INDICATOR);
                    eff.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 20 * 5, 1), false);
                    eff.setRadius(3.0F);
                }

                nightVision = false;
                canMakeAnAttack = true;
            }
        }
    }

    private void tickGravityWell() {
        if (currentAttack == DemonCurrentAttack.GRAVITY_WELL && gravityWellDuration > 0) {
            gravityWellDuration--;
            eggLocation.getWorld().spawnParticle(Particle.PORTAL, eggLocation.clone().add(0, 1, 0), 100, 5, 2, 5, 0.1);
            for (Player p : eggLocation.getWorld().getPlayers()) {
                if (p.getLocation().distanceSquared(eggLocation) < 2500) {
                    Vector direction = eggLocation.toVector().subtract(p.getLocation().toVector()).normalize();
                    p.setVelocity(p.getVelocity().add(direction.multiply(0.15)));
                    if (p.getLocation().distanceSquared(eggLocation) < 4) {
                        p.damage(4.0);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 2));
                    }
                }
            }
        } else if (gravityWellDuration <= 0 && currentAttack == DemonCurrentAttack.GRAVITY_WELL) {
            currentAttack = DemonCurrentAttack.NONE;
            canMakeAnAttack = true;
        }
    }

    private void tickMeteorShower() {
        if (currentAttack == DemonCurrentAttack.METEOR_SHOWER && meteorShowerDuration > 0) {
            meteorShowerDuration--;
            if (random.nextInt(10) == 0) {
                for (Player p : main.endWorld.getPlayers()) {
                    Location spawnLoc = p.getLocation().clone().add(random.nextInt(20)-10, 40, random.nextInt(20)-10);
                    FallingBlock meteor = main.endWorld.spawnFallingBlock(spawnLoc, Material.END_STONE.createBlockData());
                    meteor.setDropItem(false);
                    meteor.setHurtEntities(true);
                    meteor.setVelocity(new Vector(0, -1.5, 0));
                    meteor.setMetadata("Meteor", new org.bukkit.metadata.FixedMetadataValue(main, true));
                }
            }
        } else if (meteorShowerDuration <= 0 && currentAttack == DemonCurrentAttack.METEOR_SHOWER) {
            currentAttack = DemonCurrentAttack.NONE;
            canMakeAnAttack = true;
        }
    }

    private void tickApocalypticWeather() {
        double healthPercent = enderDragon.getHealth() / enderDragon.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        if (healthPercent < 0.25) {
            main.endWorld.getWorldBorder().setWarningDistance(Integer.MAX_VALUE);
            if (random.nextInt(20) == 0) {
                for (Enderman e : main.endWorld.getEntitiesByClass(Enderman.class)) {
                    if (e.getTarget() == null) {
                        Player target = main.endWorld.getPlayers().stream().findAny().orElse(null);
                        if (target != null) e.setTarget(target);
                    }
                }
            }
        } else {
            main.endWorld.getWorldBorder().setWarningDistance(0);
        }
    }

    private void tickVanishingPlatforms() {
        if (currentDemonPhase == DemonPhase.ENRAGED) {
            platformDeleteTimer--;
            if (platformDeleteTimer <= 0) {
                platformDeleteTimer = 40;
                int rx = random.nextInt(40) - 20;
                int rz = random.nextInt(40) - 20;
                Location center = eggLocation.clone().add(rx, 0, rz);
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        for (int y = -5; y <= 5; y++) {
                            Block b = center.clone().add(x, y, z).getBlock();
                            if (b.getType() == Material.END_STONE || b.getType() == Material.END_STONE_BRICKS) {
                                if (Main.isRunningFolia()) Bukkit.getRegionScheduler().run(main, b.getLocation(), t -> b.setType(Material.AIR));
                                else b.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
    }

    public void chooseAnAttack() {
        int ran = random.nextInt(100);
        if (ran < 15) {
            currentAttack = DemonCurrentAttack.GRAVITY_WELL;
            gravityWellDuration = 100;
            Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&d&l¡EL VACÍO TE RECLAMA!"));
        } else if (ran < 30 && currentDemonPhase == DemonPhase.ENRAGED) {
            currentAttack = DemonCurrentAttack.METEOR_SHOWER;
            meteorShowerDuration = 160;
            Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&6&l¡LLUVIA ARDIENTE DEL FIN!"));
        } else if (ran < 45) {
            currentAttack = DemonCurrentAttack.PHANTOM_SWARM;
            spawnPhantomSwarm();
        } else if (ran < 60) {
            currentAttack = DemonCurrentAttack.ENDERMAN_BUFF;
        } else if (ran < 80) {
            currentAttack = DemonCurrentAttack.LIGHTING_RAIN;
        } else {
            currentAttack = DemonCurrentAttack.NIGHT_VISION;
        }
    }

    private void spawnPhantomSwarm() {
        for (Player p : main.endWorld.getPlayers()) {
            for (int i = 0; i < 3; i++) {
                Phantom ph = (Phantom) main.endWorld.spawnEntity(p.getLocation().add(0, 15, 0), EntityType.PHANTOM);
                ph.setCustomName(ChatColor.DARK_PURPLE + "Abyssal Phantom");
                ph.getAttribute(Attribute.MAX_HEALTH).setBaseValue(40.0);
                ph.setHealth(40.0);
                ph.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
            }
        }
        currentAttack = DemonCurrentAttack.NONE;
        canMakeAnAttack = true;
    }

    public Map<Location, Integer> getRegenTime() { return regenTime; }
    public void setDied(boolean died) { isDied = died; }
    public Entity getEnderDragon() { return enderDragon; }
    public boolean isDied() { return isDied; }
    public Main getMain() { return main; }
    public void start360attack() { this.attack360 = true; }
    public DemonPhase getCurrentDemonPhase() { return currentDemonPhase; }
    public void setCurrentDemonPhase(DemonPhase currentDemonPhase) { this.currentDemonPhase = currentDemonPhase; }
}
