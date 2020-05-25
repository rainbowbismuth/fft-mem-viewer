package rainbowbismuth.fft;

public class BattleUnit {
    public static final int NUM_UNITS = 20;
    public static final int SIZE = 448;
    public static final int TOTAL_SIZE = NUM_UNITS * SIZE;

    private static final int EXISTS = 0x0001;
    private static final int EXPERIENCE = 0x0021;
    private static final int LEVEL = 0x0022;

    private static final int BRAVE = 0x0024;
    private static final int FAITH = 0x0026;

    private static final int HP = 0x0028;
    private static final int MAX_HP = 0x002A;
    private static final int MP = 0x002C;
    private static final int MAX_MP = 0x002E;

    private static final int SP = 0x0038;
    private static final int CT = 0x0039;

    private static final int MOVE = 0x003A;
    private static final int JUMP = 0x003B;

    private static final int X_COORD = 0x0047;
    private static final int Y_COORD = 0x0048;
    private static final int ELEVATION_FACING = 0x0049;

    private static final int AUTO_STATUS_BYTES = 0x004E;
    private static final int STATUS_IMMUNITY_BYTES = 0x0053;
    private static final int STATUS_BYTES = 0x0058;

    private static final int UNIT_NAME = 0x012C;
    private static final int UNIT_NAME_END = 0x013B;
    private static final int JOB_NAME = 0x013C;
    private static final int JOB_NAME_END = 0x0149;
    private static final int LAST_USED_ABILITY_ID = 0x0170;
    private static final int CURRENTLY_UNITS_TURN = 0x0186;
    private final int index;
    private final byte[] memory;

    public BattleUnit(final int index, final byte[] memory) {
        this.index = index;
        this.memory = memory;
    }

    private int addr(final int offset) {
        return this.index * SIZE + offset;
    }

    private int readByte(final int offset) {
        return Byte.toUnsignedInt(this.memory[addr(offset)]);
    }

    private int readShort(final int offset) {
        final int low = Byte.toUnsignedInt(this.memory[addr(offset)]);
        final int high = Byte.toUnsignedInt(this.memory[addr(offset + 1)]);
        return low + (high << 8);
    }

    public int getIndex() {
        return index;
    }

    /**
     * @return If this unit exists in the current battle. The rest of the data is gibberish unless this returns false.
     */
    public boolean invalid() {
        return readByte(EXISTS) == 0xFF;
    }

    public int getExperience() {
        return readByte(EXPERIENCE);
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

    public int getBrave() {
        return readByte(BRAVE);
    }

    public int getFaith() {
        return readByte(FAITH);
    }

    public int getSpeed() {
        return readByte(SP);
    }

    public int getMove() {
        return readByte(MOVE);
    }

    public int getJump() {
        return readByte(JUMP);
    }

    public int getX() {
        return readByte(X_COORD);
    }

    public int getY() {
        return readByte(Y_COORD);
    }

    public boolean isOnHigherElevation() {
        return (readByte(ELEVATION_FACING) & 0x80) != 0;
    }

    public Facing getFacing() {
        final int index = readByte(ELEVATION_FACING) & 0b11;
        return Facing.VALUES[index];
    }

    /**
     * @return The unit's name, e.g. "Rad"
     */
    public String getName() {
        return CharacterSet.INSTANCE.read(this.memory, addr(UNIT_NAME), addr(UNIT_NAME_END));
    }

    /**
     * @return The unit's job, e.g. "Squire"
     */
    public String getJobName() {
        return CharacterSet.INSTANCE.read(this.memory, addr(JOB_NAME), addr(JOB_NAME_END));
    }

    /**
     * @return The ability ID of this unit's last used, or currently charging ability if they have one of the
     * charging/jumping/performing statuses.
     */
    public int getLastUsedAbilityId() {
        return readShort(LAST_USED_ABILITY_ID);
    }

    /**
     * @return True if the unit has the given status applied automatically.
     */
    public boolean hasAutoStatus(final Status status) {
        final int statusByte = readByte(AUTO_STATUS_BYTES + status.getOffset());
        return (statusByte & status.getFlag()) != 0;
    }

    /**
     * @return True if the unit is immune to the given status.
     */
    public boolean hasStatusImmunity(final Status status) {
        final int statusByte = readByte(STATUS_IMMUNITY_BYTES + status.getOffset());
        return (statusByte & status.getFlag()) != 0;
    }

    /**
     * @return True if the unit has the given status.
     */
    public boolean hasStatus(final Status status) {
        final int statusByte = readByte(STATUS_BYTES + status.getOffset());
        return (statusByte & status.getFlag()) != 0;
    }

    private void setStatusInArray(final TacticsInspector inspector, final int statusArrayBegin, final Status status, final boolean value) throws PSMemoryWriteException {
        final int offset = statusArrayBegin + status.getOffset();
        final int statusByte = readByte(offset);
        if (value) {
            inspector.writeBattleUnitByte(addr(offset), statusByte | status.getFlag());
        } else {
            inspector.writeBattleUnitByte(addr(offset), statusByte & (~status.getFlag()));
        }
    }

    public void setStatus(final TacticsInspector inspector, final Status status, final boolean value) throws PSMemoryWriteException {
        setStatusInArray(inspector, STATUS_BYTES, status, value);
    }

    public void setAutoStatus(final TacticsInspector inspector, final Status status, final boolean value) throws PSMemoryWriteException {
        setStatusInArray(inspector, AUTO_STATUS_BYTES, status, value);
    }


    public void setStatusImmunity(final TacticsInspector inspector, final Status status, final boolean value) throws PSMemoryWriteException {
        setStatusInArray(inspector, STATUS_IMMUNITY_BYTES, status, value);
    }


    /**
     * @return True if the unit is currently taking their turn.
     */
    public boolean isTakingTurn() {
        return readByte(CURRENTLY_UNITS_TURN) == 0x01;
    }
}
