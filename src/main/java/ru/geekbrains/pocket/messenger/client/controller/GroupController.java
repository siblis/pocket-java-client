package ru.geekbrains.pocket.messenger.client.controller;

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

import static ru.geekbrains.pocket.messenger.client.controller.ClientController.token;
import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

//for api:
//Group
//          /groups/%id
//          /groups
//Group members
//          /groups/%id/members
//          /groups/%id/members/%id
//Group invites
//          /groups/%id/invites

public class GroupController {

    static final Logger controllerLogger = LogManager.getLogger(AuthController.class);
    
    ClientController clientCtrllr;

    GroupController(ClientController cc) {
        clientCtrllr = cc;
    }

    Group getGroupInfo(String groupName){
        Group group = new Group();
        try {
            ServerResponse response = HTTPSRequest.getGroupInfo(groupName, token);
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

    void joinGroup(String groupName){
        Group group = getGroupInfo(groupName);
        addUserGroup(group.getGid(), clientCtrllr.myUser.getId());
    }

    void addGroup(String group_name){
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
            controllerLogger.error("HTTPSRequest.addGroup_error", e);
        }

    }

    void addUserGroup(String group_id, String new_user_id) {
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
            controllerLogger.error("HTTPSRequest.addUserGroup_error", e);
        }
    }
}
