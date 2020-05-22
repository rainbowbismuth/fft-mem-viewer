package rainbowbismuth.fft;

public class BattleUnit {
    public static final int SIZE = 448;
    private static final int EXISTS = 0x0001;
    private static final int LEVEL = 0x0022;
    private static final int HP = 0x0028;
    private static final int MAX_HP = 0x002A;
    private static final int MP = 0x002C;
    private static final int MAX_MP = 0x002E;
    private static final int CT = 0x0039;
    private static final int STATUS_BYTES = 0x0058;
    private static final int UNIT_NAME = 0x012C;
    private static final int UNIT_NAME_END = 0x013B;
    private static final int JOB_NAME = 0x013C;
    private static final int JOB_NAME_END = 0x0149;
    private static final int LAST_USED_ABILITY_ID = 0x0170;
    private static final int CURRENTLY_UNITS_TURN = 0x0186;
    private final byte[] memory;

    public BattleUnit(final byte[] memory) {
        this.memory = memory;
    }

    private int readByte(final int offset) {
        return Byte.toUnsignedInt(this.memory[offset]);
    }

    private int readShort(final int offset) {
        final int low = Byte.toUnsignedInt(this.memory[offset]);
        final int high = Byte.toUnsignedInt(this.memory[offset + 1]);
        return low + (high << 8);
    }

    /**
     * @return If this unit exists in the current battle. The rest of the data is gibberish unless this returns true.
     */
    public boolean exists() {
        return readByte(EXISTS) != 0xFF;
    }

    public int getLevel() {
        return readByte(LEVEL);
    }

    /**
     * @return The unit's current CT, not the charge time on a charging ability.
     */
    public int getCT() {
        return readByte(CT);
    }

    public int getHP() {
        return readShort(HP);
    }

    public int getMaxHP() {
        return readShort(MAX_HP);
    }

    public int getMP() {
        return readShort(MP);
    }

    public int getMaxMP() {
        return readShort(MAX_MP);
    }

    /**
     * @return The unit's name, e.g. "Rad"
     */
    public String getName() {
        return CharacterSet.INSTANCE.read(this.memory, UNIT_NAME, UNIT_NAME_END);
    }

    /**
     * @return The unit's job, e.g. "Squire"
     */
    public String getJobName() {
        return CharacterSet.INSTANCE.read(this.memory, JOB_NAME, JOB_NAME_END);
    }

    /**
     * @return The ability ID of this unit's last used, or currently charging ability if they have one of the
     * charging/jumping/performing statuses.
     */
    public int getLastUsedAbilityId() {
        return readShort(LAST_USED_ABILITY_ID);
    }

    /**
     * @return True if the unit has the passed in status.
     */
    public boolean hasStatus(final Status status) {
        final int statusByte = readByte(STATUS_BYTES + status.getOffset());
        return (statusByte & status.getFlag()) != 0;
    }

    /**
     * @return True if the unit is currently taking their turn.
     */
    public boolean isTakingTurn() {
        return readByte(CURRENTLY_UNITS_TURN) == 0x01;
    }
}
