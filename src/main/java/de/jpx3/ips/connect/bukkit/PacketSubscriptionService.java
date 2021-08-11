package de.jpx3.ips.connect.bukkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.jpx3.ips.IntaveProxySupportPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class PacketSubscriptionService {
  private IntaveProxySupportPlugin plugin;
  private Map<Class<? extends AbstractPacket>, List<PacketSubscriber>> packetSubscriptions = ImmutableMap.of();

  private PacketSubscriptionService(IntaveProxySupportPlugin plugin) {
    this.plugin = plugin;
  }

  public void setup() {
    packetSubscriptions = Maps.newHashMap();
    // Assign all packet-classes an empty list
    PacketRegister.packetTypes()
      .forEach(packetClass ->
        packetSubscriptions.put(packetClass, Lists.newCopyOnWriteArrayList()));
  }

  public void reset() {
    packetSubscriptions.clear();
  }

  public <P extends AbstractPacket> void subscribe(
    Class<P> type,
    PacketSubscriber<P> subscriber
  ) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(subscriber);
    subscriptionsOf(type).add(subscriber);
  }

  public <P extends AbstractPacket> void broadcastPacketToSubscribers(
    ProxiedPlayer sender,
    P packet
  ) {
    Preconditions.checkNotNull(sender);
    Preconditions.checkNotNull(packet);
    subscriptionsOf(packet)
      .forEach(packetSubscriber ->
        packetSubscriber.handle(sender, packet));
  }

  private List<PacketSubscriber> subscriptionsOf(AbstractPacket packet) {
    return subscriptionsOf(packet.getClass());
  }

  private List<PacketSubscriber> subscriptionsOf(Class<? extends AbstractPacket> packetClass) {
    return packetSubscriptions().get(packetClass);
  }

  public Map<Class<? extends AbstractPacket>, List<PacketSubscriber>> packetSubscriptions() {
    return packetSubscriptions;
  }

  public static PacketSubscriptionService createFrom(IntaveProxySupportPlugin plugin) {
    return new PacketSubscriptionService(plugin);
  }
}
