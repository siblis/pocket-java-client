package client.view.customFX;

import database.entity.User;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Objects;

public class CFXListElement extends AnchorPane {

    @FXML
    private Text topic;

    @FXML
    private Text body;

    @FXML
    private Circle avatar;

    @FXML
    private Text dateText;

    @FXML
    private Text unreadMessages;

    @FXML
    private Circle circleUnreadMessages;

    @FXML
    private MaterialDesignIconView unreadGlyph;

    @FXML
    private Circle circleOnline;

//    @FXML
//    private FontAwesomeIconView unreadGlyph;

    private String imageURL;

    private User user;


    public CFXListElement() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/CFXListElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }
        
        setUnreadMessages("0");
        setBody("");
        setOnlineStatus(false);
    }

    public CFXListElement(User user) {
        this();
        setUser(user);
    }

    public String getTopic(){
        return this.topic.getText();
    }

    public void setTopic(String topic) {
        this.topic.setText(topic);
    }

    public String getDateText() {
        return dateText.getText();
    }

    public void setDateText(String dateText) {
        this.dateText.setText(dateText);
    }

    public String getBody() {
        return body.getText();
    }

    public void setBody(String body) {
        int maxLenOfVisible = 40;
        if (body.length() > maxLenOfVisible) body = body.substring(0, maxLenOfVisible - 3) + "...";
        body = body.replace("\n", " ");
        this.body.setText(body);
    }

    public void setAvatar(Image avatar) {
        this.avatar.setFill(new ImagePattern(avatar));

    }

    public String getUnreadMessages() {
        return unreadMessages.getText();
    }

    public void setUnreadMessages(String unreadMessages) {
        if (unreadMessages.equals("0")){
            circleUnreadMessages.setVisible(false);
            unreadGlyph.setVisible(false);
            this.unreadMessages.setVisible(false);
        } else
        {
            circleUnreadMessages.setVisible(true);
            unreadGlyph.setVisible(true);
            this.unreadMessages.setText("+"+unreadMessages);
            this.unreadMessages.setVisible(true);
        }
    }

    public User getUser(){
        return this.user;
    }

    public void setUser(User user){
        this.user = user;
        this.topic.setText(user.getAccount_name());

    }

    public void setOnlineStatus(boolean isOnline){
        this.circleOnline.setVisible(isOnline);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.user);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CFXListElement other = (CFXListElement) obj;
        return Objects.equals(this.user, other.user);
    }
}