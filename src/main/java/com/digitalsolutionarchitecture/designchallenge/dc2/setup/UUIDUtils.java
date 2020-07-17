package com.digitalsolutionarchitecture.designchallenge.dc2.setup;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtils {

    public static byte[] getBytesFromUUID(UUID uuid) {
    	
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }
	
}
