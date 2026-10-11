package com.tambolaapp.tambola.service;

import com.tambolaapp.tambola.dto.*;
import com.tambolaapp.tambola.entity.PlayerGeneratedValues;

import java.util.List;

public interface SaveData {

    String updateLinkOfHost(String username, String hostid);

    String checkIfExists(HostIdCheck urlid);

    GetUserNameResponse savePlayerName(GetUserNameRequest getUserNameRequest);

    void createPlayerValue(List<DataItemRequest> dataItemRequest);

    void updatelayerValue(List<DataItemRequest> dataItemRequest);

    List<PlayerGeneratedValues> playerGeneratedValues(Long requestPlayerId);

    void updatevaluesafe(DataSingleItem dataSingleItem);

    GetUserNameResponse getPlayerDetails(Long playerid);
}
