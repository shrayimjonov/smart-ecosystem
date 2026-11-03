package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Message implements Serializable {
    private Long message_id;
    private From from;
    private String date;
    private String text;
    private Chat chat;
    private Contact contact;
    private Location location;
    private List<Entities> entities;
    private Message reply_to_message;
    private List<Photo> photo;
    private File document;
    private String caption;
}
