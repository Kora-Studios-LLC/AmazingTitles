package org.korastudios.amazingtitles.code.internal.smartbar;

import org.bukkit.entity.Player;
import org.korastudios.amazingtitles.code.internal.Booter;

import java.util.*;

public class SmartBar {

    private final Player owner;
    private final boolean notifications;
    private final boolean staticAnimation;
    private final boolean staticAnimationNotifications;
    private final SmartBarManager manager;

    private volatile boolean hide = false;
    private int staticAnimationContentCounter = 0;
    private final Map<String, SmartNotification> notificationsContent =
            Collections.synchronizedMap(new LinkedHashMap<>());

    public SmartBar(Player owner, boolean notifications, boolean staticAnimation,
                    boolean staticAnimationNotifications, SmartBarManager manager) {
        this.owner = owner;
        this.notifications = notifications;
        this.staticAnimation = staticAnimation;
        this.staticAnimationNotifications = staticAnimationNotifications;
        this.manager = manager;
    }

    public void setNotification(String id, SmartNotification notification) {
        double time = notification.getTime();
        synchronized (notificationsContent) {
            notificationsContent.values().forEach(n -> n.extend(time));
            notificationsContent.put(id, notification);
        }
    }

    public void tryToInstantRemoveNotification(String id) {
        synchronized (notificationsContent) {
            SmartNotification notification = notificationsContent.get(id);
            if (notification != null) notification.quickRemove();
        }
    }

    public void setHide(boolean hide) { this.hide = hide; }

    public void prepareAndTryToSend() {
        if (hide) return;
        String text;
        if (staticAnimation) {
            if (staticAnimationNotifications && !notificationsContent.isEmpty()) {
                text = prepareNotifications();
            } else {
                text = pickCurrentStaticFrame();
            }
        } else if (notifications) {
            text = prepareNotifications();
        } else {
            return;
        }
        if (text == null || text.isEmpty()) return;
        Object packet = Booter.getNmsProvider().createActionbarPacket(text);
        Booter.getNmsProvider().sendActionbar(owner, packet);
    }

    private String prepareNotifications() {
        if (notificationsContent.isEmpty()) return "";
        long now = System.currentTimeMillis();

        List<Map.Entry<String, SmartNotification>> entries;
        synchronized (notificationsContent) {
            entries = new ArrayList<>(notificationsContent.entrySet());
        }

        int size = entries.size();
        Set<String> toRemove = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            Map.Entry<String, SmartNotification> entry = entries.get(i);
            SmartNotification notification = entry.getValue();
            if (!notification.isStarted()) notification.start(now);
            if (notification.isOver(now)) {
                toRemove.add(entry.getKey());
                continue;
            }
            boolean latest = (i == size - 1) || entries.get(i + 1).getValue().isEnding(now);
            sb.append(' ').append(notification.getCurrentFrame(now, latest));
        }

        if (!toRemove.isEmpty()) {
            synchronized (notificationsContent) {
                toRemove.forEach(notificationsContent::remove);
            }
        }

        return sb.length() < 2 ? "" : sb.substring(1);
    }

    private String pickCurrentStaticFrame() {
        List<String> content = manager.getStaticAnimationContent();
        if (content.isEmpty()) return "";
        String frame = content.get(staticAnimationContentCounter);
        if (++staticAnimationContentCounter >= content.size()) staticAnimationContentCounter = 0;
        return frame != null ? frame : "";
    }
}
