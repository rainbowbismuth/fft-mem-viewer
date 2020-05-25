package rainbowbismuth.fft;

import java.util.ArrayList;
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
     */
    public List<BattleUnit> readBattleUnits() throws PSMemoryReadException {
        final byte[] memory = this.PSMemory.read(UNIT_STATS_ADDRESS, BattleUnit.TOTAL_SIZE);
        final List<BattleUnit> units = new ArrayList<>(BattleUnit.NUM_UNITS);
        for (int i = 0; i < BattleUnit.NUM_UNITS; i++) {
            units.add(new BattleUnit(i, memory));
        }
        return units;
    }

    /**
     * @return A list of every ability name in the game
     */
    public List<String> readAbilityNameTable() throws PSMemoryReadException {
        final int maxSize = 0x1FF * 16;  // This just needs to be big enough.
        final byte[] memory = this.PSMemory.read(ABILITY_NAME_TABLE_ADDRESS, maxSize);
        return CharacterSet.INSTANCE.readTable(memory, 0, maxSize - 1);
    }

    void writeBattleUnitByte(final int offset, final int value) throws PSMemoryWriteException {
        final byte[] bytes = new byte[]{(byte) value};
        this.PSMemory.write(UNIT_STATS_ADDRESS + offset, bytes);
    }
}

