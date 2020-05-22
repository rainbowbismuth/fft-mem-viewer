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

        for (int i = 0; i < 20; i++) {
            final BattleUnit battleUnit = fftInspector.readBattleUnit(i);
            if (!battleUnit.exists()) {
                continue;
            }

            System.out.println(String.format("Unit %d (%s, %s)", i, battleUnit.getName(), battleUnit.getJobName()));
            System.out.println(String.format("    Level: %d", battleUnit.getLevel()));
            System.out.println(String.format("       CT: %d", battleUnit.getCT()));
            System.out.println(String.format("   Cur HP: %d", battleUnit.getHP()));
            System.out.println(String.format("   Max HP: %d", battleUnit.getMaxHP()));
            System.out.println(String.format("   Cur MP: %d", battleUnit.getMP()));
            System.out.println(String.format("   Max MP: %d", battleUnit.getMaxMP()));
            System.out.println(String.format("  On turn: %s", battleUnit.isTakingTurn()));

            if (battleUnit.hasStatus(Status.CHARGING)) {
                final int abilityId = battleUnit.getLastUsedAbilityId();
                final String abilityName = abilityNameTable.get(abilityId);
                System.out.println(String.format(" Charging: %s (ID: %d)", abilityName, abilityId));

            }
            System.out.println();
        }
    }
}
