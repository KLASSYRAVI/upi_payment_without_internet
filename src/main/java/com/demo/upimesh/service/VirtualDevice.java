package com.demo.upimesh.service;

import com.demo.upimesh.model.MeshPacket;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simulated phone in the mesh. Holds packets it has seen.
 *
 * In the real system, this state would be on a physical Android device,
 * with packets exchanged via BLE GATT characteristics.
 *
 * hasInternet is volatile so it can be toggled at runtime from the dashboard
 * without requiring a restart.
 */
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

    /** Toggle bridge/offline status. Returns the new state. */
    public boolean toggleInternet() { return internetFlag.getAndUpdate(v -> !v) ? false : true; }

    public void hold(MeshPacket packet) {
        heldPackets.putIfAbsent(packet.getPacketId(), packet);
    }

    public Collection<MeshPacket> getHeldPackets() {
        return heldPackets.values();
    }

    public boolean holds(String packetId) {
        return heldPackets.containsKey(packetId);
    }

    public int packetCount() {
        return heldPackets.size();
    }

    public void clear() {
        heldPackets.clear();
    }
}
