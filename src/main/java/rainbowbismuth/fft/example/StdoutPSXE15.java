package rainbowbismuth.fft.example;

import rainbowbismuth.fft.BattleUnit;
import rainbowbismuth.fft.Status;
import rainbowbismuth.fft.TacticsInspector;
import rainbowbismuth.fft.WindowsMemoryViewer;

import java.util.List;

public class StdoutPSXE15 {
    public static void main(final String[] args) throws Exception {
        final WindowsMemoryViewer memory = WindowsMemoryViewer
                .createEPSXE15Viewer(null, "ePSXe - Enhanced PSX emulator");

        final TacticsInspector fftInspector = new TacticsInspector(memory);
        final List<String> abilityNameTable = fftInspector.readAbilityNameTable();
        final List<BattleUnit> battleUnits = fftInspector.readBattleUnits();

        for (final BattleUnit unit : battleUnits) {
            if (unit.invalid()) {
                continue;
            }

            System.out.println(String.format("Unit %d (%s, %s)", unit.getIndex(), unit.getName(), unit.getJobName()));
            System.out.println(String.format("    Level: %d", unit.getLevel()));
            System.out.println(String.format("       CT: %d", unit.getCT()));
            System.out.println(String.format("   Cur HP: %d", unit.getHP()));
            System.out.println(String.format("   Max HP: %d", unit.getMaxHP()));
            System.out.println(String.format("   Cur MP: %d", unit.getMP()));
            System.out.println(String.format("   Max MP: %d", unit.getMaxMP()));
            System.out.println(String.format("  On turn: %s", unit.isTakingTurn()));

            if (unit.hasStatus(Status.CHARGING)) {
                final int abilityId = unit.getLastUsedAbilityId();
                final String abilityName = abilityNameTable.get(abilityId);
                System.out.println(String.format(" Charging: %s (ID: %d)", abilityName, abilityId));

            }
            System.out.println();
        }
    }
}
