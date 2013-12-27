package wetodo.manager;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.muc.*;
import org.jivesoftware.openfire.muc.spi.LocalMUCRole;
import org.jivesoftware.openfire.muc.spi.LocalMUCRoom;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
import wetodo.conf.MucConf;
import wetodo.dao.RoomDAO;
import wetodo.model.Room;
import wetodo.model.User;

import java.util.List;
import java.util.UUID;

public class RoomManager {

    private static RoomManager instance;

    /**
     * 单例产生实例
     *
     * @return
     */
    public static RoomManager getInstance() {
        if (instance == null) {
            synchronized (RoomManager.class) {
                instance = new RoomManager();
            }
        }
        return instance;
    }

    /**
     * 创建房间
     *
     * @param roomJid
     * @param userJid
     * @param subject
     * @return
     */
    public Room create(JID roomJid, JID userJid, String subject) {
        try {
            MultiUserChatService chatService =
                    XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJid);
            MUCRoom room = chatService.getChatRoom(roomJid.getNode(), userJid);
            room.setPersistent(true);
            room.setSubject(subject);
            room.setMembersOnly(true);
            room.saveToDB();
            room.unlock(room.getRole());

            return RoomDAO.findByRoomJidDetail(roomJid.toBareJID());
        } catch (NotAllowedException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 房间列表
     *
     * @param jid
     * @return
     */
    public List<Room> list(String jid) {
        return RoomDAO.list(jid);
    }

    /**
     * 查询房间信息
     *
     * @param roomJid
     * @return
     */
    public Room query(String roomJid) {
        return RoomDAO.findByRoomJidDetail(roomJid);
    }

    /**
     * 邀请进入房间
     *
     * @param roomJid
     * @param inviterJid
     * @param inviteeJid
     */
    public void invite(JID roomJid, JID inviterJid, JID inviteeJid) throws NotAllowedException, UserNotFoundException, ConflictException, ForbiddenException, CannotBeInvitedException {
        MultiUserChatService chatService =
                XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJid);
        LocalMUCRoom room = (LocalMUCRoom) chatService.getChatRoom(roomJid.getNode(), inviterJid);

        LocalMUCRole role = (LocalMUCRole) room.getOccupant(inviterJid.getNode());
        if (room.isMembersOnly()) {
            room.addMember(inviteeJid, null, role);
        }
        // Send the invitation to the invitee
        room.sendInvitation(inviteeJid,
                "join in", role, null);
    }

    /**
     * 生成房间JID
     *
     * @param userJid
     * @return
     */
    public JID generateRoomJid(JID userJid) {
        try {
            String userId = userJid.getNode();
            String roomId = userId + "_" + UUID.randomUUID().toString() + "@" + MucConf.CONFERENCE;
            return new JID(roomId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> fetchMemberList(String roomId) {
        List<User> memberList = RoomDAO.fetchMemberList(roomId);
        return memberList;
    }

    public void exit(JID roomJid, JID userJid) throws NotAllowedException, UserNotFoundException, ConflictException, ForbiddenException {
        MultiUserChatService chatService =
                XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(roomJid);
        LocalMUCRoom room = (LocalMUCRoom) chatService.getChatRoom(roomJid.getNode(), userJid);

        LocalMUCRole role = (LocalMUCRole) room.getOccupant(userJid.getNode());

        room.leaveRoom(role);
        room.addNone(userJid, role);
    }
}
