package app.nss.webchat.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class BlockedUserService implements BlockedUserServiceInterface {

    private final Map<Long, Set<Long>> blockedUsersByRoom = new HashMap<>();

    @Override
    public boolean isUserBlockedForRoom(Long userId, Long roomId) {
        Set<Long> blockedUsers = blockedUsersByRoom.get(roomId);
        return blockedUsers != null && blockedUsers.contains(userId);
    }

    @Override
    public void blockUserForRoom(Long userId, Long roomId) {
        blockedUsersByRoom.computeIfAbsent(roomId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void unblockUserForRoom(Long userId, Long roomId) {
        Set<Long> blockedUsers = blockedUsersByRoom.get(roomId);
        if (blockedUsers != null) {
            blockedUsers.remove(userId);
        }
    }
}

