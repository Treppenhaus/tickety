package eu.treppi.codingschule.ticekty.core.transcript;

public class TicketMessage {
    String username, messagecontent, messageid, userid, useravatar;

    public TicketMessage(String username, String messagecontent, String messageid, String userid, String useravatar) {
        this.userid = userid;
        this.messagecontent = messagecontent;
        this.messageid = messageid;
        this.username = username;
        this.useravatar = useravatar;
    }

    public String getUseravatar() {
        return useravatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessagecontent() {
        return messagecontent;
    }

    public void setMessagecontent(String messagecontent) {
        this.messagecontent = messagecontent;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
