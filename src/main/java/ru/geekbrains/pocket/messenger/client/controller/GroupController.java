package ru.geekbrains.pocket.messenger.client.controller;

//for api:
//Group
//          /groups/%id
//          /groups
//Group members
//          /groups/%id/members
//          /groups/%id/members/%id
//Group invites
//          /groups/%id/invites

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.Group;
import ru.geekbrains.pocket.messenger.client.model.ServerResponse;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AddGroup;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AddUserGroup;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.database.entity.User;

import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

public class GroupController {
    private static final Logger log = LogManager.getLogger(GroupController.class);
    private static GroupController instance;

    public static GroupController getInstance() {
        if (instance == null) {
            instance = new GroupController();
        }
        return instance;
    }

    public Group getGroupInfo(String groupName, String token){
        Group group = new Group();
        try {
            ServerResponse response = HTTPSRequest.getGroupInfo(groupName,token);
            switch (response.getResponseCode()){
                case 200:
                    System.out.println("получение информации о группе");
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    group = gson.fromJson(response.getResponseJson(), Group.class);
                    return group;
                case 400:
                    System.out.println("Проблемы с токеном");
                    break;
                case 404:
                    System.out.println("группа не найдена");
                    break;
                case 500:
                    System.out.println("ошибка сервера");
                    break;
                default:
                    System.out.println("другая ошибка");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }

    public void joinGroup(User myUser, String groupName, String token){
        Group group = getGroupInfo(groupName, token);
        addUserGroup(group.getGid(), myUser.getId().toString(), token);
    }

    public void addGroup(String group_name, String token){
        AddGroup addGroup = new AddGroup(group_name);
        String requestJSON = new Gson().toJson(addGroup);

        try {
            ServerResponse response = HTTPSRequest.addGroup(requestJSON, token);
            switch (response.getResponseCode()) {
                case 200:
                    showAlert("Группа" + group_name + "успешно создана", Alert.AlertType.INFORMATION);
                    //addContactToDB(convertJSONToUser(response.getResponseJson()));
                    //if (chatViewController != null) chatViewController.fillContactListView();
                    break;
                case 400:
                    showAlert("Ошибка запроса", Alert.AlertType.ERROR);
                    break;
                case 404:
                    showAlert("Невозможно создать группу с названием: " + group_name, Alert.AlertType.ERROR);
                    break;
                case 500:
                    showAlert("Ошибка сервера", Alert.AlertType.ERROR);
                    break;
                default:
                    showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            log.error("HTTPSRequest.addGroup_error", e);
        }

    }

    public void addUserGroup(String group_id, String new_user_id, String token) {
        AddUserGroup aug = new AddUserGroup(group_id, new_user_id);
        String requestJSON = new Gson().toJson(aug);

        try {
            ServerResponse response = HTTPSRequest.addUserGroup(requestJSON, token);
            switch (response.getResponseCode()) {
                case 200:
                    showAlert("Группа успешно добавлена", Alert.AlertType.INFORMATION);
                    //addContactToDB(convertJSONToUser(response.getResponseJson()));
                    //if (chatViewController != null) chatViewController.fillContactListView();
                    break;
                case 400:
                    showAlert("Ошибка запроса", Alert.AlertType.ERROR);
                    break;
                case 404:
                    showAlert("Группа не найдена или вы уже состоите в ней", Alert.AlertType.ERROR);
                    break;
                case 500:
                    showAlert("Ошибка сервера", Alert.AlertType.ERROR);
                    break;
                default:
                    showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            log.error("HTTPSRequest.addUserGroup_error", e);
        }
    }
}
