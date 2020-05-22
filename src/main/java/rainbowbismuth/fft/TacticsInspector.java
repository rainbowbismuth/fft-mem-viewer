package rainbowbismuth.fft;

import java.util.List;

/**
 * Read FFT game data out of memory.
 */
public class TacticsInspector {
    private static final long UNIT_STATS_ADDRESS = 0x801908CC;
    private static final long ABILITY_NAME_TABLE_ADDRESS = 0x80163b88;

    private PSMemory PSMemory;

    public TacticsInspector(final PSMemory PSMemory) {
        this.PSMemory = PSMemory;
    }

    public PSMemory getPSMemory() {
        return PSMemory;
    }

    public void setPSMemory(final PSMemory PSMemory) {
        this.PSMemory = PSMemory;
    }

    /**
     * Read a unit's battle statistics, there are up to 20 units in one battle, and the first 16 correspond to the
     * sixteen ENTD slots.
     *
     * @param index An index, 0 to 19 inclusive.
     * @return A unit that is in the current battle.
     */
    public BattleUnit readBattleUnit(final int index) throws PSMemoryReadException {
        final long address = UNIT_STATS_ADDRESS + BattleUnit.SIZE * index;
        final byte[] memory = this.PSMemory.read(address, BattleUnit.SIZE);
        return new BattleUnit(memory);
    }

    /**
     * @return A list of every ability name in the game
     */
    public List<String> readAbilityNameTable() throws PSMemoryReadException {
        final int maxSize = 0x1FF * 16;  // This just needs to be big enough.
        final byte[] memory = this.PSMemory.read(ABILITY_NAME_TABLE_ADDRESS, maxSize);
        return CharacterSet.INSTANCE.readTable(memory, 0, maxSize - 1);
    }
}

