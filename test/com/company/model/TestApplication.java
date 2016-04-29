package com.company.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestApplication {

    @Test
    public void testGetSHA256Hash() throws Exception {
        byte[] expected = new byte[]{(byte) 0x9F, (byte) 0x86, (byte) 0xD0, (byte) 0x81, (byte) 0x88, (byte) 0x4C,
                (byte) 0x7D, (byte) 0x65, (byte) 0x9A, (byte) 0x2F, (byte) 0xEA, (byte) 0xA0, (byte) 0xC5,
                (byte) 0x5A, (byte) 0xD0, (byte) 0x15, (byte) 0xA3, (byte) 0xBF, (byte) 0x4F, (byte) 0x1B,
                (byte) 0x2B, (byte) 0xB, (byte) 0x82, (byte) 0x2C, (byte) 0xD1, (byte) 0x5D, (byte) 0x6C,
                (byte) 0x15, (byte) 0xB0, (byte) 0xF0, (byte) 0xA
        };
        byte[] hash = Manager.getSHA256Hash("test");
        for (int i = 0; i < hash.length - 1; i++) {
            assertEquals(expected[i], hash[i]);
        }
    }
}
