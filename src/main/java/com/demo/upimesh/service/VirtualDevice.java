package com.demo.upimesh.service;

import com.demo.upimesh.model.MeshPacket;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/** A simulated phone holding packets and atomic bridge connectivity state. */
public class VirtualDevice {
    private final String deviceId;
    private final AtomicBoolean internetFlag;
    private final Map<String, MeshPacket> heldPackets = new ConcurrentHashMap<>();

    public VirtualDevice(String deviceId, boolean hasInternet) {
        this.deviceId = deviceId;
        this.internetFlag = new AtomicBoolean(hasInternet);
    }

    public String getDeviceId() { return deviceId; }
    public boolean hasInternet() { return internetFlag.get(); }

    /** Toggle bridge/offline status and return the new state atomically. */
    public boolean toggleInternet() {
        boolean current;
        do {
            current = internetFlag.get();
        } while (!internetFlag.compareAndSet(current, !current));
        return !current;
    }

    public void hold(MeshPacket packet) {
        heldPackets.putIfAbsent(packet.getPacketId(), packet);
    }

    public Collection<MeshPacket> getHeldPackets() { return heldPackets.values(); }
    public boolean holds(String packetId) { return heldPackets.containsKey(packetId); }
    public int packetCount() { return heldPackets.size(); }
    public void clear() { heldPackets.clear(); }
}
