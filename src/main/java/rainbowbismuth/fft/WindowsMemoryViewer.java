package rainbowbismuth.fft;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;


/**
 * Read game memory out of another Window's process, the Java process will need elevated privileges to do so.
 */
public class WindowsMemoryViewer implements PSMemory, AutoCloseable {
    private static final long MIN_ADDRESS = 0x80000000;
    private final Kernel32 kernel32 = Kernel32.INSTANCE;
    private final int pid;
    private final WinNT.HANDLE handle;
    private final long psRAMOffset;

    public WindowsMemoryViewer(final String lpClassName, final String lpWindowName, final int psRAMOffset) throws Exception {
        this.psRAMOffset = psRAMOffset;
        final User32 user32 = User32.INSTANCE;
        final WinDef.HWND window = user32.FindWindow(lpClassName, lpWindowName);
        if (window == null) {
            throw new Exception(String.format(
                    "Error code from FindWindow: %s", kernel32.GetLastError()));
        }
        try {
            final IntByReference pidPointer = new IntByReference();
            user32.GetWindowThreadProcessId(window, pidPointer);
            pid = pidPointer.getValue();
            if (pid == 0) {
                throw new Exception(String.format(
                        "Error code from GetWindowThreadProcessId %s", kernel32.GetLastError()));
            }
            handle = kernel32.OpenProcess(Kernel32.PROCESS_VM_OPERATION | Kernel32.PROCESS_VM_READ, false, pid);
            if (handle == null) {
                throw new Exception(String.format(
                        "Error code from OpenProcess %s", kernel32.GetLastError()));
            }
        } finally {
            kernel32.CloseHandle(window);
        }
    }

    /**
     * Read game memory out of an ePSXe 1.5 window
     */
    public static WindowsMemoryViewer createEPSXE15Viewer(final String lpClassName, final String lpWindowName) throws Exception {
        return new WindowsMemoryViewer(lpClassName, lpWindowName, 0x5b5c40);
    }

    private long translateAddress(final long gameAddress) throws PSMemoryReadException {
        if (gameAddress < MIN_ADDRESS) {
            throw new PSMemoryReadException(String.format("Address not mapped: %08X", gameAddress));
        }
        return psRAMOffset + (gameAddress - MIN_ADDRESS);
    }

    @Override
    public byte[] read(final long gameAddress, final int size) throws PSMemoryReadException {
        final long realAddress = translateAddress(gameAddress);
        final Pointer baseAddressPointer = Pointer.createConstant(realAddress);
        final Memory buffer = new Memory(size);
        if (kernel32.ReadProcessMemory(handle, baseAddressPointer, buffer, size, null)) {
            return buffer.getByteArray(0, size);
        } else {
            throw new PSMemoryReadException(String.format(
                    "Couldn't read memory from PID %s, error code: %d, game address: %08X, real address: %08X",
                    pid, kernel32.GetLastError(), gameAddress, realAddress));
        }
    }

    @Override
    public void close() {
        kernel32.CloseHandle(this.handle);
    }
}
