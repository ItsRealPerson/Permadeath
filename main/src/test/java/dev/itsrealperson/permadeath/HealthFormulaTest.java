package dev.itsrealperson.permadeath;

import dev.itsrealperson.permadeath.data.DateManager;
import dev.itsrealperson.permadeath.util.events.LifeOrbEvent;
import dev.itsrealperson.permadeath.util.inventory.AccessoryInventory;
import dev.itsrealperson.permadeath.util.item.NetheriteArmor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HealthFormulaTest {

    private MockedStatic<Main> mockedMain;
    private MockedStatic<DateManager> mockedDateManager;
    private MockedStatic<AccessoryInventory> mockedAccessoryInventory;
    private MockedStatic<org.bukkit.Bukkit> mockedBukkit;
    private MockedConstruction<NamespacedKey> mockedNamespacedKey;
    
    private Main main;
    private DateManager dateManager;
    private LifeOrbEvent lifeOrbEvent;
    private dev.itsrealperson.permadeath.api.storage.PlayerDataStorage playerStorage;

    @BeforeEach
    public void setup() {
        main = mock(Main.class);
        when(main.getName()).thenReturn("permadeath");
        
        dateManager = mock(DateManager.class);
        lifeOrbEvent = mock(LifeOrbEvent.class);
        playerStorage = mock(dev.itsrealperson.permadeath.api.storage.PlayerDataStorage.class);

        mockedMain = mockStatic(Main.class);
        mockedMain.when(Main::getInstance).thenReturn(main);
        
        mockedDateManager = mockStatic(DateManager.class);
        mockedDateManager.when(DateManager::getInstance).thenReturn(dateManager);

        mockedAccessoryInventory = mockStatic(AccessoryInventory.class);
        mockedBukkit = mockStatic(org.bukkit.Bukkit.class);
        
        // Mocking NamespacedKey construction to avoid internal Bukkit validation issues
        mockedNamespacedKey = mockConstruction(NamespacedKey.class, (mock, context) -> {
            if (context.arguments().size() >= 2) {
                String key = (String) context.arguments().get(1);
                when(mock.getKey()).thenReturn(key);
            }
        });

        when(main.getOrbEvent()).thenReturn(lifeOrbEvent);
        when(main.getPlayerStorage()).thenReturn(playerStorage);
        when(playerStorage.loadPlayer(anyString())).thenReturn(java.util.Optional.of(
                dev.itsrealperson.permadeath.api.storage.PlayerData.createDefault("test")
        ));
    }

    @AfterEach
    public void tearDown() {
        mockedMain.close();
        mockedDateManager.close();
        mockedAccessoryInventory.close();
        mockedBukkit.close();
        mockedNamespacedKey.close();
    }

    @Test
    public void testBaseHealthDay1() {
        Player player = createMockPlayer(false, false, new ItemStack[4]);
        when(main.getDay()).thenReturn(1L);
        when(lifeOrbEvent.isRunning()).thenReturn(false);

        Double health = NetheriteArmor.getAvailableMaxHealth(player);
        assertEquals(20.0, health, 0.001);
    }

    @Test
    public void testHealthDay40() {
        Player player = createMockPlayer(false, false, new ItemStack[4]);
        when(main.getDay()).thenReturn(40L);
        when(lifeOrbEvent.isRunning()).thenReturn(false);

        Double health = NetheriteArmor.getAvailableMaxHealth(player);
        assertEquals(12.0, health, 0.001); // 20 - 8
    }

    @Test
    public void testHealthDay60WithOrb() {
        Player player = createMockPlayer(false, false, new ItemStack[4]);
        when(main.getDay()).thenReturn(60L);
        when(lifeOrbEvent.isRunning()).thenReturn(true);

        Double health = NetheriteArmor.getAvailableMaxHealth(player);
        assertEquals(4.0, health, 0.001); // 20 - 8 (D40) - 8 (D60)
    }

    @Test
    public void testHealthDay60NoOrb() {
        Player player = createMockPlayer(false, false, new ItemStack[4]);
        when(main.getDay()).thenReturn(60L);
        when(lifeOrbEvent.isRunning()).thenReturn(false);

        Double health = NetheriteArmor.getAvailableMaxHealth(player);
        assertEquals(0.000001, health, 0.0000001); // 20 - 8 - 8 - 16 = -12 -> clamped to min
    }

    @Test
    public void testHealthWithHyperFoods() {
        Player player = createMockPlayer(true, true, new ItemStack[4]);
        when(main.getDay()).thenReturn(1L);
        when(lifeOrbEvent.isRunning()).thenReturn(false);

        Double health = NetheriteArmor.getAvailableMaxHealth(player);
        assertEquals(28.0, health, 0.001); // 20 + 4 + 4
    }

    @Test
    public void testHealthWithFullNetheriteDay40() {
        // Simulamos 4 piezas de netherite
        ItemStack[] armor = new ItemStack[4];
        for (int i = 0; i < 4; i++) armor[i] = mock(ItemStack.class);
        
        MockedStatic<NetheriteArmor> netheriteArmorMockedStatic = mockStatic(NetheriteArmor.class, invocation -> {
            if (invocation.getMethod().getName().equals("isNetheritePiece")) return true;
            if (invocation.getMethod().getName().equals("isInfernalPiece")) return false;
            return invocation.callRealMethod();
        });

        try {
            Player player = createMockPlayer(false, false, armor);
            when(main.getDay()).thenReturn(40L);
            when(lifeOrbEvent.isRunning()).thenReturn(false);

            Double health = NetheriteArmor.getAvailableMaxHealth(player);
            assertEquals(20.0, health, 0.001); // 20 (base) + 8 (netherite) - 8 (D40)
        } finally {
            netheriteArmorMockedStatic.close();
        }
    }

    private Player createMockPlayer(boolean hyperOne, boolean hyperTwo, ItemStack[] armor) {
        Player player = mock(Player.class);
        PlayerInventory inv = mock(PlayerInventory.class);
        PersistentDataContainer pdc = mock(PersistentDataContainer.class);

        when(player.getInventory()).thenReturn(inv);
        when(player.getPersistentDataContainer()).thenReturn(pdc);
        when(inv.getArmorContents()).thenReturn(armor);
        when(inv.getContents()).thenReturn(new ItemStack[0]);
        
        mockedAccessoryInventory.when(() -> AccessoryInventory.load(player)).thenReturn(new ItemStack[0]);

        java.util.Set<String> keys = new java.util.HashSet<>();
        if (hyperOne) keys.add("hyper_one");
        if (hyperTwo) keys.add("hyper_two");

        when(pdc.has(any(NamespacedKey.class), any())).thenAnswer(invocation -> {
            NamespacedKey k = invocation.getArgument(0);
            return k != null && keys.contains(k.getKey());
        });

        return player;
    }
}