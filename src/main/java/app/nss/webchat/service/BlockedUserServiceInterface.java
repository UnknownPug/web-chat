package app.nss.webchat.service;

public interface BlockedUserServiceInterface {
    boolean isUserBlockedForRoom(Long userId, Long roomId);
    void blockUserForRoom(Long userId, Long roomId);
    void unblockUserForRoom(Long userId, Long roomId);
}
