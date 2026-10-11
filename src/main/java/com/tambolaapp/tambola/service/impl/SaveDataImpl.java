package com.tambolaapp.tambola.service.impl;

import com.tambolaapp.tambola.dto.*;
import com.tambolaapp.tambola.entity.PlayerDetails;
import com.tambolaapp.tambola.entity.PlayerGeneratedValues;
import com.tambolaapp.tambola.entity.User;
import com.tambolaapp.tambola.repository.PlayerDetailsRepository;
import com.tambolaapp.tambola.repository.PlayerGeneratedValuesRepository;
import com.tambolaapp.tambola.repository.UsersRepository;
import com.tambolaapp.tambola.service.SaveData;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class SaveDataImpl implements SaveData {
    private static final Logger logger = LoggerFactory.getLogger(SaveDataImpl.class);
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PlayerDetailsRepository playerDetailsRepository;

    @Autowired
    PlayerGeneratedValuesRepository playerGeneratedValuesRepository;

    @Override
    public String updateLinkOfHost(String username, String hostid) {
        var user = usersRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("Invalid Username or Password!"));;

        if (user!=null) {
            User users = user;
            users.setHostid(hostid);
            usersRepository.save(users); // save() will update the existing record if the ID exists
        } else {
            throw new RuntimeException("User not found");
        }

        return "";
    }

    @Override
    public String checkIfExists(HostIdCheck urlid) {
        Optional<User> user = usersRepository.findByHostid(urlid.getHostid());
        if(user.isPresent()){
            return "success";
        }
        return "not found";
    }

    @Override
    public GetUserNameResponse savePlayerName(GetUserNameRequest getUserNameRequest) {
        PlayerDetails playerDetails = PlayerDetails.builder()
                .username(getUserNameRequest.getUsername())
                .hostid(getUserNameRequest.getHostid())
                .build();
        playerDetailsRepository.save(playerDetails);

        return GetUserNameResponse.builder()
                .id(playerDetails.getId())
                .username(playerDetails.getUsername())
                .hostid(playerDetails.getHostid())
                .build();
    }

    @Override
    public void createPlayerValue(List<DataItemRequest> dataItemRequest) {
        List<PlayerGeneratedValues> dataItemEntities = dataItemRequest.stream()
                .filter(request -> request.getUserid() != null)
                .map(request -> {
                    PlayerGeneratedValues entity = new PlayerGeneratedValues();
                    entity.setKey(request.getKey());
                    entity.setValue(request.getValue());
                    entity.setChecked(request.isChecked());
                    entity.setUserid(Long.parseLong(request.getUserid()));
                    return entity;
                })
                .collect(Collectors.toList());

        // Save the list of DataItemEntity to the database
        playerGeneratedValuesRepository.saveAll(dataItemEntities);

    }

    @Override
    public void updatelayerValue(List<DataItemRequest> dataItemRequest) {
        // Extract the keys from the DataItemRequest list to use them to fetch entities
        List<Integer> keys = dataItemRequest.stream()
                .map(DataItemRequest::getKey)  // Extract the 'key' from each request
                .collect(Collectors.toList());

        // Fetch all entities from the database that have keys matching those in the list
        List<PlayerGeneratedValues> entitiesToUpdate = playerGeneratedValuesRepository.findByKeyIn(keys);

        // Iterate over the entities and update their 'checked' status
        for (PlayerGeneratedValues entity : entitiesToUpdate) {
            // Find the matching DataItemRequest by key
            DataItemRequest matchingRequest = dataItemRequest.stream()
                    .filter(request -> request.getKey() == entity.getKey())
                    .findFirst()
                    .orElse(null);

            if (matchingRequest != null) {
                // Update the 'checked' field in the entity based on the request
                entity.setChecked(matchingRequest.isChecked());
            }
        }

        // Save the updated entities back to the database in a batch
        if (!entitiesToUpdate.isEmpty()) {
            playerGeneratedValuesRepository.saveAll(entitiesToUpdate);
        }

    }

    @Override
    public List<PlayerGeneratedValues> playerGeneratedValues(Long requestPlayerId) {
        List<PlayerGeneratedValues> playerGeneratedValues= playerGeneratedValuesRepository.findByUseridOrderByKeyAsc(requestPlayerId);
        if(!playerGeneratedValues.isEmpty())
            return playerGeneratedValues;
        return null;
    }

    @Override
    public void updatevaluesafe(DataSingleItem dataSingleItem) {
        getEntityByUseridAndKey(Long.parseLong(dataSingleItem.getF()), Integer.parseInt( String.valueOf(dataSingleItem.getE())));
    }

    public void getEntityByUseridAndKey(long userid, int key) {
        PlayerGeneratedValues playerGeneratedValues = playerGeneratedValuesRepository.findByUseridAndKey(userid, key)
                .orElseThrow(() -> new RuntimeException("Entity not found"));
        if(playerGeneratedValues.isChecked()){
            playerGeneratedValues.setChecked(false);
            //updateChecked(userid,key,false);
        }
        else{
            playerGeneratedValues.setChecked(true);
          //  updateChecked(userid,key,true);
        }
        playerGeneratedValuesRepository.save(playerGeneratedValues);

    }

    // Method to update the 'checked' field
//    public void updateChecked(long userid, int key, boolean checked) {
//        int updatedRows = playerGeneratedValuesRepository.updateCheckedByUseridAndKey(checked, userid, key);
//        if (updatedRows == 0) {
//            throw new RuntimeException("No rows were updated");
//        }
//    }

    @Override
    public GetUserNameResponse getPlayerDetails(Long playerid) {
        return playerDetailsRepository.findById(playerid)
                .map(player -> GetUserNameResponse.builder()
                        .id(player.getId())
                        .username(player.getUsername())
                        .hostid(player.getHostid())
                        .build())
                .orElse(null);
    }
}
