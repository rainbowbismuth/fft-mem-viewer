package rainbowbismuth.fft;

public enum Status {
    CRYSTAL(0, 0x40),
    DEAD(0, 0x20),
    CHARGING(0, 0x08),
    JUMP(0, 0x04),
    DEFENDING(0, 0x02),
    PERFORMING(0, 0x01),
    PETRIFY(1, 0x80),
    INVITE(1, 0x40),
    DARKNESS(1, 0x20),
    CONFUSION(1, 0x10),
    SILENCE(1, 0x08),
    BLOOD_SUCK(1, 0x04),
    CURSED(1, 0x02),
    TREASURE(1, 0x01),
    OIL(2, 0x80),
    FLOAT(2, 0x40),
    RERAISE(2, 0x20),
    TRANSPARENT(2, 0x10),
    BERSERK(2, 0x08),
    CHICKEN(2, 0x04),
    FROG(2, 0x02),
    CRITICAL(2, 0x01),
    POISON(3, 0x80),
    REGEN(3, 0x40),
    PROTECT(3, 0x20),
    SHELL(3, 0x10),
    HASTE(3, 0x08),
    SLOW(3, 0x04),
    STOP(3, 0x02),
    WALL(3, 0x01),
    FAITH(4, 0x80),
    INNOCENT(4, 0x40),
    CHARM(4, 0x20),
    SLEEP(4, 0x10),
    DONT_MOVE(4, 0x08),
    DONT_ACT(4, 0x04),
    REFLECT(4, 0x02),
    DEATH_SENTENCE(4, 0x01);

    private final byte offset;
    private final byte flag;

    Status(final int offset, final int flag) {
        this.offset = (byte) offset;
        this.flag = (byte) flag;
    }

    public byte getOffset() {
        return offset;
    }

    public byte getFlag() {
        return flag;
    }
}
