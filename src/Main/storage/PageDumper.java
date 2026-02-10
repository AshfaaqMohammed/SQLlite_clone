package storage;

import java.nio.ByteBuffer;

public final class PageDumper {

    private PageDumper() {}

    public static void dumpPage(ByteBuffer page) {
        page.position(0);
        int bytesPerRow = 16;
        int total = page.capacity();

        for (int offset = 0; offset < total; offset += bytesPerRow) {
            // Print offset
            System.out.printf("%08X  ", offset);

            // Print hex representation
            StringBuilder ascii = new StringBuilder();
            for (int i = 0; i < bytesPerRow; i++) {
                if (offset + i < total) {
                    byte b = page.get(offset + i);
                    System.out.printf("%02X ", b);

                    // build ASCII output (printable characters only)
                    if (b >= 32 && b <= 126) {
                        ascii.append((char) b);
                    } else {
                        ascii.append('.');
                    }
                } else {
                    System.out.print("   ");
                    ascii.append(' ');
                }
            }

            // Print ASCII representation
            System.out.printf(" |%s|%n", ascii.toString());
        }
    }
}
